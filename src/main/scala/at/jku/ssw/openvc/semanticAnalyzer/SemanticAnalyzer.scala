/*
 *     OpenVC, an open source VHDL compiler/simulator
 *     Copyright (C) 2010  Christian Reisinger
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.jku.ssw.openvc.semanticAnalyzer

import scala.collection.mutable
import math.Numeric.{LongIsIntegral, DoubleAsIfIntegral, IntIsIntegral}
import annotation.tailrec

import java.io.{IOException, FileInputStream}

import at.jku.ssw.openvc.ast._
import at.jku.ssw.openvc.ast.concurrentStatements._
import at.jku.ssw.openvc.ast.sequentialStatements._
import at.jku.ssw.openvc.ast.declarations._
import at.jku.ssw.openvc.ast.expressions._
import at.jku.ssw.openvc.symbolTable._
import at.jku.ssw.openvc.symbolTable.dataTypes._
import at.jku.ssw.openvc.symbolTable.symbols._

import at.jku.ssw.openvc.VHDLCompiler.Configuration
import at.jku.ssw.openvc.codeGenerator.ByteCodeGenerator.getNextIndex
import at.jku.ssw.openvc.CompilerMessage

object SemanticAnalyzer {
  type SemanticCheckResult = (DesignFile, Seq[CompilerMessage], Seq[CompilerMessage])
  type Buffer[A] = mutable.ListBuffer[A]


  private[this] val operatorMangleMap = Map(
    "and" -> "and", "or" -> "or", "nand" -> "nand", "nor" -> "nor", "xor" -> "xor", "xnor" -> "xnor", //logical_operator
    "=" -> "$eq", "/=" -> "$eq$div", "<" -> "$less", "<=" -> "$less$eq", ">" -> "$greater", ">=" -> "$greater$eq", //relational_operator
    "sll" -> "sll", "srl" -> "srl", "sla" -> "sla", "sra" -> "sra", "rol" -> "rol", "ror" -> "ror", //shift_operator
    "+" -> "$plus", "-" -> "$minus", "&" -> "$amp", //adding_operator + sign
    "*" -> "$times", "/" -> "$div", "mod" -> "mod", "rem" -> "rem", //multiplying_operator
    "**" -> "$times$times", "abs" -> "abs", "not" -> "not" //miscellaneous_operator
  )

  private[this] val entityClassToSymbolMap = {
    import at.jku.ssw.openvc.ast.declarations.EntityClass._
    Map(
      ENTITY -> classOf[EntitySymbol],
      ARCHITECTURE -> classOf[ArchitectureSymbol],
      CONFIGURATION -> classOf[ConfigurationSymbol],
      PACKAGE -> classOf[PackageSymbol],
      PROCEDURE -> classOf[ListOfSubprograms],
      FUNCTION -> classOf[ListOfSubprograms],
      TYPE -> classOf[TypeSymbol],
      SUBTYPE -> classOf[SubTypeSymbol],
      CONSTANT -> classOf[ConstantSymbol],
      SIGNAL -> classOf[SignalSymbol],
      VARIABLE -> classOf[VariableSymbol],
      FILE -> classOf[FileSymbol],
      COMPONENT -> classOf[ComponentSymbol],
      LABEL -> classOf[LabelSymbol],
      LITERAL -> classOf[ListOfEnumerations],
      UNITS -> classOf[UnitSymbol],
      GROUP -> classOf[GroupSymbol]
      //, NATURE, SUBNATURE, QUANTITY, TERMINAL
    )
  }

  def isCompatible(dataType: DataType, expectedDataType: DataType): Boolean =
    if (dataType == expectedDataType || (dataType eq NoType) || (expectedDataType eq NoType)) true
    else {
      (dataType, expectedDataType) match {
        case (_: IntegerType, SymbolTable.universalIntegerType) => true
        case (SymbolTable.universalIntegerType, _: IntegerType) => true
        case (it1: IntegerType, it2: IntegerType) => it1.baseType.getOrElse(it1) == it2.baseType.getOrElse(it2)
        case (_: RealType, SymbolTable.universalRealType) => true
        case (SymbolTable.universalRealType, _: RealType) => true
        case (rt1: RealType, rt2: RealType) => rt1.baseType.getOrElse(rt1) == rt2.baseType.getOrElse(rt2)
        case (et1: EnumerationType, et2: EnumerationType) => et1.baseType.getOrElse(et1) == et1.baseType.getOrElse(et2)
        case (NullType, _: AccessType) => true
        case (_: AccessType, NullType) => true
        case (at1: ArrayType, at2: ArrayType) => isCompatible(at1.elementType, at2.elementType) //TODO check element type and range
        case _ => false
      }
    }

  //TODO remove me
  var highDouble, lowDouble = 0.0D
  var lowLong, highLong = 0L
  var configuration: Configuration = null

  val semanticErrors = new Buffer[CompilerMessage]
  val semanticWarnings = new Buffer[CompilerMessage]

  def apply(designFile: DesignFile, configuration: Configuration): SemanticCheckResult = {
    this.configuration = configuration
    semanticErrors.clear
    semanticWarnings.clear
    val (newDesignFile, context) = acceptNode(designFile, null, Context(0, new SymbolTable(List[SymbolTable.Scope]()), collection.immutable.Stack()))
    (newDesignFile.asInstanceOf[DesignFile], semanticErrors.toList, semanticWarnings.toList)
  }

  def addError(stmt: Locatable, msg: String, messageParameters: AnyRef*): Option[Nothing] = addErrorPosition(stmt.position, msg, messageParameters: _*)

  def addErrorPosition(position: Position, msg: String, messageParameters: AnyRef*): Option[Nothing] = {
    semanticErrors += new CompilerMessage(position, String.format(msg, messageParameters.toArray: _*))
    None
  }

  def addWarning(stmt: Locatable, msg: String, messageParameters: AnyRef*) = semanticWarnings += new CompilerMessage(stmt.position, String.format(msg, messageParameters.toArray: _*))

  def checkIdentifiersOption(startOption: Option[Identifier], endOption: Option[Identifier]) = startOption match {
    case Some(start) => checkIdentifiers(start, endOption)
    case None => endOption foreach (end => addError(end, SemanticMessage.START_LABEL_MISSING, end))
  }

  def checkIdentifiers(start: Identifier, endOption: Option[Identifier]) =
    for (end <- endOption) if (end.text != start.text) addError(end, SemanticMessage.START_END_LABEL_DIFFERENT, start, end)

  case class Context(varIndex: Int, symbolTable: SymbolTable, loopLabels: collection.immutable.Stack[(Option[Identifier], Position)]) {
    def insertLoopLabel(label: Option[Identifier], position: Position): Context =
      copy(loopLabels = loopLabels.push((label, position)))

    def find(name: SelectedName): Option[Symbol] = {
      @tailrec
      def matchParts(identifiers: Seq[Identifier], symbol: Symbol): Option[Symbol] = identifiers match {
        case Seq() => symbol match {
          case AliasSymbol(_, destination) => matchParts(identifiers, destination)
          case _ => Option(symbol)
        }
        case Seq(identifier, xs@_*) =>
          (symbol match {
            case AliasSymbol(_, destination) => Option(destination)
            case librarySymbol: LibrarySymbol =>
              librarySymbol.libraryArchive.loadSymbol(symbol.name + "/" + identifier.text + "_header.sym", classOf[PackageSymbol]).orElse(addError(identifier, SemanticMessage.NOT_FOUND, "package", identifier.text))
            case packageSymbol: PackageSymbol =>
              packageSymbol.localSymbols.get(operatorMangleMap.get(identifier.text).getOrElse(identifier.text)).orElse(addError(identifier, SemanticMessage.NOT_FOUND, "item", identifier.text))
            case _ => addError(identifier, "%s is not a library or package, so you can not use qualified names", identifier.text)
          }) match {
            case None => None
            case Some(newSymbol) => matchParts(if (symbol.isInstanceOf[AliasSymbol]) identifiers else xs, newSymbol)
          }
      }
      symbolTable.find(operatorMangleMap.getOrElse(name.identifiers.head.text, name.identifiers.head.text)).flatMap(symbol => matchParts(name.identifiers.tail, symbol))
    }

    def findSymbol[A <: Symbol](identifier: Identifier, symbolClass: Class[A]): Option[A] =
      findSymbol(new SelectedName(Seq(identifier)), symbolClass)

    def findSymbol[A <: Symbol](symbolName: SelectedName, symbolClass: Class[A]): Option[A] =
      find(symbolName) match {
        case None => addError(symbolName, SemanticMessage.NOT_FOUND, "symbol", symbolName)
        case Some(symbol) =>
          if (symbol.getClass eq symbolClass) Some(symbol.asInstanceOf[A])
          else addError(symbolName, SemanticMessage.INVALID_SYMBOL_TYPE, symbol.name, symbolClass.toString, symbol.getClass.toString)
      }

    def findType(typeName: SelectedName, isAccessTypeDefinition: Boolean = false): DataType = find(typeName) match {
      case None =>
        addError(typeName, "type %s not found", typeName)
        NoType
      case Some(symbol) =>
        val dataType = symbol match {
          case typeSymbol: TypeSymbol => typeSymbol.dataType
          case subTypeSymbol: SubTypeSymbol => subTypeSymbol.dataType
          case _ =>
            addError(typeName, "%s is not a type or subtype", typeName)
            NoType
        }
        if (!isAccessTypeDefinition && dataType == IncompleteType) addError(typeName, "can not use incomplete type %s", typeName)
        dataType
    }

    def findFunctionInList(listOfSubprograms: ListOfSubprograms, dataTypes: Seq[DataType], returnType: DataType): Option[FunctionSymbol] =
      findSubprogramInList(listOfSubprograms, dataTypes, Option(returnType)).map(_.asInstanceOf[FunctionSymbol])

    def findSubprogramInList(listOfSubprograms: ListOfSubprograms, dataTypes: Seq[DataType], returnTypeOption: Option[DataType]): Option[SubprogramSymbol] =
      listOfSubprograms.subprograms.find{
        subprogramSymbol =>
          subprogramSymbol.parameters.size == dataTypes.size && subprogramSymbol.parameters.zip(dataTypes).forall(t => isCompatible(t._1.dataType, t._2)) && ((subprogramSymbol, returnTypeOption) match {
            case (functionSymbol: FunctionSymbol, Some(returnType)) => isCompatible(returnType, functionSymbol.returnType)
            case (procedureSymbol: ProcedureSymbol, None) => true
            case _ => false
          })
      }

    def openScope = copy(symbolTable = symbolTable.openScope)

    def closeScope(): Seq[Symbol] = {
      val scope = symbolTable.currentScope
      for (symbol <- scope.valuesIterator) {
        symbol match {
          case r: RuntimeSymbol => if (!r.used) addWarning(r, SemanticMessage.UNUSED_SYMBOL, symbol.getClass.toString.split('.').last.split('S').head, symbol.name)
          case _ =>
        }
      }
      scope.valuesIterator.toList
    }

    def insertSymbols(list: Seq[Symbol], generateErrorMessage: Boolean = true): Context = {
      @tailrec
      def insertSymbolsInner(context: Context, l: Seq[Symbol]): Context = l match {
        case Seq() => context
        case Seq(symbol, xs@_*) => insertSymbolsInner(context.insertSymbol(symbol, generateErrorMessage), xs)
      }
      insertSymbolsInner(this, list)
    }

    def insertSymbol(optionSymbol: Option[Symbol]): Context =
      optionSymbol.map(insertSymbol(_, true)).getOrElse(this)

    def insertSymbol(symbol: Symbol, generateErrorMessage: Boolean = true): Context =
      symbolTable.currentScope.get(symbol.name) match {
        case Some(existingSymbol) =>
          existingSymbol match {
            case listOfSubprograms: ListOfSubprograms if (symbol.isInstanceOf[SubprogramSymbol] || symbol.isInstanceOf[ListOfSubprograms]) =>
              ((symbol: @unchecked) match {
                case subprogramSymbol: SubprogramSymbol =>
                  findSubprogramInList(listOfSubprograms, subprogramSymbol.parameters.map(_.dataType), subprogramSymbol.returnTypeOption) match {
                    case None => Seq(subprogramSymbol)
                    case Some(s) =>
                      if (!s.implemented && subprogramSymbol.implemented) {
                        subprogramSymbol.attributes ++= s.attributes
                        Seq(subprogramSymbol)
                      }
                      else addError(symbol, "subprogram %s already declared", symbol.name).toList
                  }
                case ListOfSubprograms(_, subprograms) => subprograms.filter(subprogramSymbol => findSubprogramInList(listOfSubprograms, subprogramSymbol.parameters.map(_.dataType), subprogramSymbol.returnTypeOption).isEmpty)
              }) match {
                case Seq() => this
                case subprogram => copy(symbolTable = symbolTable.insert(listOfSubprograms.copy(subprograms = subprogram ++ listOfSubprograms.subprograms)))
              }
            case listOfEnumerations: ListOfEnumerations if (symbol.isInstanceOf[EnumerationSymbol] || symbol.isInstanceOf[ListOfEnumerations]) =>
              ((symbol: @unchecked) match {
                case enum: EnumerationSymbol => Seq(enum)
                case ListOfEnumerations(_, enumerations) => enumerations
              }) match {
                case Seq() => this
                case enumerations => copy(symbolTable = symbolTable.insert(listOfEnumerations.copy(enumerations = enumerations ++ listOfEnumerations.enumerations)))
              }
            case constSymbol: ConstantSymbol if (!constSymbol.isDefined) => copy(symbolTable = symbolTable.insert(symbol)) //used for deferred constants
            case typeSymbol: TypeSymbol if (typeSymbol.dataType.isInstanceOf[ProtectedType] && !typeSymbol.dataType.asInstanceOf[ProtectedType].implemented) =>
              copy(symbolTable = symbolTable.insert(symbol)) //used for protected type body
            case _ =>
              if (generateErrorMessage) addError(symbol, SemanticMessage.ALREADY_DECLARED, symbol.name)
              this
          }
        case None =>
          val s = symbol match {
            case subprogramSymbol: SubprogramSymbol => new ListOfSubprograms(subprogramSymbol.identifier, Seq(subprogramSymbol))
            case enum: EnumerationSymbol => new ListOfEnumerations(enum.identifier, Seq(enum))
            case _ => symbol
          }
          copy(symbolTable = symbolTable.insert(s))
      }
  }

  type ReturnType = (ASTNode, Context)

  def acceptDeclarativeItems(n: Seq[DeclarativeItem], owner: Symbol, context: Context): (Seq[DeclarativeItem], Context) = {
    val (list, newContext) = acceptList(n, owner, context)
    newContext.symbolTable.currentScope.values.foreach{
      _ match {
        case typeSymbol: TypeSymbol => typeSymbol.dataType match {
          case protectedType: ProtectedType =>
          //Each protected type declaration appearing immediately within a given declarative region (see 10.1) must
          //have exactly one corresponding protected type body appearing immediately within the same declarative
          //region and textually subsequent to the protected type declaration. Similarly, each protected type body
          //appearing immediately within a given declarative region must have exactly one corresponding protected
          //type declaration appearing immediately within the same declarative region and textually prior to the
          //protected type body.
            if (!protectedType.implemented && !owner.isInstanceOf[PackageSymbol])
              addError(owner, SemanticMessage.PROTECTED_TYPE_BODY_NOT_DEFINED, typeSymbol.name)
          //For each incomplete type declaration there must be a corresponding full type declaration with the same identifier.
          // This full type declaration must occur later and immediately within the same declarative part as the incomplete type declaration to which it corresponds.
          case IncompleteType => addError(typeSymbol, SemanticMessage.INCOMPLETE_TYPE_NOT_DEFINED, typeSymbol.name)
          case _ =>
        }
        //For each subprogram declaration, there shall be a corresponding body. If both a declaration and a body are given, the subprogram specification of the body
        //shall conform (see 2.7) to the subprogram specification of the declaration. Furthermore, both the declaration and the body must occur immediately
        //within the same declarative region (see 10.1).
        case ListOfSubprograms(_, subprograms) => subprograms.foreach{
          symbol =>
            if (!symbol.implemented && !(owner.isInstanceOf[PackageSymbol] && !owner.asInstanceOf[PackageSymbol].isBody) && (symbol.owner eq owner) &&
              !(symbol.attributes.contains("foreign") && symbol.attributes("foreign").isInstanceOf[UserDefinedAttributeSymbol] && symbol.attributes("foreign").asInstanceOf[UserDefinedAttributeSymbol].attributeDeclaration == SymbolTable.foreignAttribute))
              addError(owner, SemanticMessage.NOT_FOUND, "body for subprogram", symbol.name)
        }
        case _ =>
      }
    }
    (list, newContext)
  }

  @tailrec
  def acceptList[A <: ASTNode](nodes: Seq[A], owner: Symbol, context: Context, buffer: Buffer[A] = new Buffer[A]()): (Seq[A], Context) = nodes match {
    case Seq() => (buffer.toList, context)
    case Seq(head, xs@_*) =>
      val (newNode, newContext) = acceptNode(head, owner, context)
      newNode match {
        case null =>
        case node => buffer += node.asInstanceOf[A]
      }
      acceptList(xs, owner, newContext, buffer)
  }

  def acceptListOption[A <: ASTNode](listOption: Option[Seq[A]], owner: Symbol, context: Context): (Option[Seq[A]], Context) =
    listOption match {
      case None => (None, context)
      case Some(list) =>
        val (resultList, resultContext) = acceptList(list, owner, context)
        (Option(resultList), resultContext)
    }

  def acceptExpression(expr: Expression, expectedType: DataType, context: Context): Expression = {

    return acceptExpressionInner(expr)

    def acceptExpressionInnerOption(expression: Option[Expression]): Option[Expression] = expression.map(acceptExpressionInner(_))

    def acceptExpressionInner(expression: Expression): Expression = (expression: @unchecked) match {
      case EmptyExpression | null => EmptyExpression
      case item: ItemExpression => item
      case term: Term =>
        val l = acceptExpressionInner(term.left)
        val r = acceptExpressionInner(term.right)
        visitTerm(term.copy(left = l, right = r))
      case aggregate: Aggregate => visitAggregate(aggregate)
      case typeCastExpr: TypeCastExpression => visitTypeCastExpression(typeCastExpr)
      case relation: Relation =>
        val l = acceptExpressionInner(relation.left)
        val r = acceptExpressionInner(relation.right)
        visitRelation(relation.copy(left = l, right = r))
      case qualifiedExpr: QualifiedExpression => visitQualifiedExpression(qualifiedExpr)
      case name: Name => visitName(name)
      case shiftExpr: ShiftExpression =>
        val l = acceptExpressionInner(shiftExpr.left)
        val r = acceptExpressionInner(shiftExpr.right)
        visitShiftExpression(shiftExpr.copy(left = l, right = r))
      case factor: Factor =>
        val l = acceptExpressionInner(factor.left)
        val r = acceptExpressionInnerOption(factor.rightOption)
        visitFactor(factor.copy(left = l, rightOption = r))
      case functionCallExpr: FunctionCallExpression => visitFunctionCallExpression(functionCallExpr)
      case logicalExpr: LogicalExpression =>
        val l = acceptExpressionInner(logicalExpr.left)
        val r = acceptExpressionInner(logicalExpr.right)
        visitLogicalExpression(logicalExpr.copy(left = l, right = r))
      case simpleExpr: SimpleExpression =>
        val l = acceptExpressionInner(simpleExpr.left)
        val r = acceptExpressionInnerOption(simpleExpr.rightOption)
        visitSimpleExpression(simpleExpr.copy(left = l, rightOption = r))
      case newExpr: NewExpression => visitNewExpression(newExpr)
      case literal: Literal => visitLiteral(literal)
      case physicalLiteral: PhysicalLiteral => visitPhysicalLiteral(physicalLiteral)
    }

    def findOverloadedOperator(operator: String, loc: Locatable, expressions: Expression*): Option[FunctionCallExpression] = {
      val identifier = new SelectedName(Seq(Identifier(loc.position, operatorMangleMap(operator))))
      context.findSymbol(identifier, classOf[ListOfSubprograms]).flatMap{
        listOfSubprograms =>
          context.findFunctionInList(listOfSubprograms, expressions.map(_.dataType), NoType).map{
            functionSymbol => FunctionCallExpression(identifier, Option(AssociationList(Seq(), expressions)), EmptyExpression, functionSymbol.returnType, functionSymbol)
          }
      }.orElse{
        expressions.toSeq match {
          case Seq(left, right) => addError(loc, SemanticMessage.OPERATOR_NOT_DEFINED, operator, left.dataType.name, right.dataType.name)
          case Seq(expression) => addError(loc, SemanticMessage.UNARY_OPERATOR_NOT_DEFINED, operator, expression.dataType.name)
        }
      }
    }

    def visitAggregate(aggregate: Aggregate): Expression =
      aggregate match {
        case Aggregate(_, Seq(Aggregate.ElementAssociation(None, expression)), _) => checkExpression(context, expression, expectedType) //this is a aggregate of the form: (expression)
        case _ =>
        // TODO Auto-generated method stub
        //require(aggregate.elements.size == 1,aggregate.position)
        //println(aggregate.elements)
          val elements = expectedType match {
            case arrayType: ConstrainedArrayType =>
              val dataType = arrayType.dimensions.size match {
                case 1 => arrayType.elementType
                case size => ConstrainedArrayType(arrayType.name, arrayType.elementType, arrayType.dimensions.tail) //TODO check if for each dimension is a row
              }
              aggregate.elements.map{
                element =>
                  val expression = checkExpression(context, element.expression, dataType) //TODO
                  new Aggregate.ElementAssociation(choices = element.choices, expression = expression)
              }
            case recordType: RecordType => aggregate.elements.zip(recordType.elementList.unzip._2).map{
              case (element, dataType) =>
                val expression = checkExpression(context, element.expression, dataType) //TODO
                new Aggregate.ElementAssociation(choices = element.choices, expression = expression)
            }
            case dataType =>
              addError(aggregate, "expected a expression of an array or record type found %s", dataType.name)
              aggregate.elements
          }
          new Aggregate(aggregate.position, elements, expectedType)
      }

    def visitFactor(factor: Factor): Expression = {
      val dataType = factor.operator match {
        case Factor.Operator.ABS =>
          factor.left.dataType match {
            case numericType: NumericType => numericType
            case dataType => NoType
          }
        case Factor.Operator.POW =>
          factor.rightOption match {
            case Some(rightExpression) =>
              if (!(rightExpression.dataType == SymbolTable.integerType || rightExpression.dataType == SymbolTable.universalIntegerType)) NoType
              else {
                factor.left.dataType match {
                  case _: IntegerType | _: RealType => factor.left.dataType
                  case dataType => NoType
                }
              }
            case None => NoType
          }
        case Factor.Operator.NOT =>
          factor.left.dataType match {
            case arrayType: ArrayType if (arrayType.dimensions.size == 1 && (arrayType.elementType == SymbolTable.bitType || arrayType.elementType == SymbolTable.booleanType)) => factor.left.dataType
            case otherType =>
              if (otherType == SymbolTable.bitType || otherType == SymbolTable.booleanType) factor.left.dataType
              else NoType
          }
      }
      if (dataType == NoType) (factor.rightOption match {
        case Some(right) => findOverloadedOperator(factor.operator.toString, factor, factor.left, right)
        case None => findOverloadedOperator(factor.operator.toString, factor, factor.left)
      }).getOrElse(factor.copy(dataType = NoType))
      else factor.copy(dataType = dataType)
    }

    def visitFunctionCallExpression(functionCallExpr: FunctionCallExpression): Expression = {
      //TODO ...., alias symbols handling
      val functionName = functionCallExpr.functionName
      context.symbolTable.find(functionName.toString) match {
        case Some(symbol) => symbol match {
          case l: ListOfSubprograms =>
            val (functionSymbolOption, parameterAssociationList) = functionCallExpr.parameterAssociationList match {
              case Some(assocList) =>
                val parameters = assocList.elements.map(element => acceptExpression(element.actualPart.get, NoType, context))
                (l.subprograms.find(function => function.isInstanceOf[FunctionSymbol] && function.parameters.zip(parameters).forall(x => isCompatible(x._2.dataType, x._1.dataType))).map(_.asInstanceOf[FunctionSymbol]), Some(assocList.copy(parameters = parameters)))
              case None =>
                (l.subprograms.find(function => function.isInstanceOf[FunctionSymbol] && function.parameters.isEmpty && isCompatible(expectedType, function.asInstanceOf[FunctionSymbol].returnType)).map(_.asInstanceOf[FunctionSymbol]), None)
            }
            //val parameters = checkAssociationList(context, functionCallExpr.parameterAssociationList, functionSymbol.parameters, functionCallExpr)
            val dataType = functionSymbolOption match {
              case Some(functionSymbol) => functionSymbol.returnType
              case None => NoType
            }
            functionCallExpr.copy(parameterAssociationList = parameterAssociationList, dataType = dataType, symbol = functionSymbolOption.orNull)
          case _ => EmptyExpression
        }
        case _ => EmptyExpression
      }
    }

    def visitAttributeExpression(attributeExpr: AttributeExpression): Expression = {
      val newExpr = attributeExpr.attribute match {
        case preDefinedAttributeSymbol: PreDefinedAttributeSymbol => preDefinedAttributeSymbol.parameter match {
          case Some(requiredDataType) => preDefinedAttributeSymbol.isParameterOptional match {
            case false => attributeExpr.expression match {
              case None => addError(attributeExpr, SemanticMessage.ATTRIBUTE_PARAMETER_REQUIRED, attributeExpr.attribute.name, requiredDataType.name)
              case Some(expr) => Option(checkExpression(context, expr, requiredDataType))
            }
            case true => attributeExpr.expression match {
              case None => Some(Literal(attributeExpr.position, "1", Literal.Type.INTEGER_LITERAL, SymbolTable.universalIntegerType, 1)) //this must be a array attribute where the dimension is optional
              case Some(expr) => Option(checkExpression(context, expr, requiredDataType))
            }
          }
          case None => attributeExpr.expression.flatMap(addError(_, SemanticMessage.ATTRIBUTE_NO_PARAMETER, attributeExpr.attribute.name))
        }
        case userDefinedAttributeSymbol: UserDefinedAttributeSymbol =>
          for (expression <- attributeExpr.expression) addError(expression, "a user defined attribute does not take parameters")
          None
      }
      attributeExpr.copy(expression = newExpr)
    }

    def visitPhysicalLiteral(literal: PhysicalLiteral): Expression =
      context.findSymbol(literal.unitName, classOf[UnitSymbol]) match {
        case Some(unitSymbol) => literal.copy(unitSymbol = unitSymbol)
        case _ => literal
      }

    def visitLiteral(literal: Literal): Expression = {
      import Literal.Type._

      literal.literalType match {
        case INTEGER_LITERAL => literal.copy(dataType = SymbolTable.universalIntegerType, value = literal.toInt)
        case REAL_LITERAL => literal.copy(dataType = SymbolTable.universalRealType, value = literal.toDouble)
        case STRING_LITERAL => expectedType match {
          case arrayType: ArrayType if (arrayType.elementType.isInstanceOf[EnumerationType] && arrayType.dimensions.size == 1) =>
            val enumType = arrayType.elementType.asInstanceOf[EnumerationType]
            for ((c, i) <- literal.text.replace("\"", "").zipWithIndex) {
              if (!enumType.contains(c.toString))
                addErrorPosition(literal.position.addCharacterOffset(i + 1), SemanticMessage.NOT_A, c.toString, "element of enumeration type " + enumType.name)
            }
            //literal.copy(dataType = new ConstrainedRangeType(arrayType.elementType, 0, literal.text.length))//TODO
            literal.copy(dataType = arrayType)
          case NoType => literal.copy(dataType = SymbolTable.stringType) //TODO
          case dataType =>
            addError(literal, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, expectedType.name, dataType.name)
            literal.copy(dataType = NoType)
        }
        case CHARACTER_LITERAL =>
          val text = literal.text.replace("'", "")
          val (dataType, value) = expectedType match {
            case e: EnumerationType if (e.contains(text)) => (Option(e), e.intValue(text))
            case a: ArrayType => a.elementType match {
              case e: EnumerationType if (e.contains(text)) => (Option(e), e.intValue(text))
              case dataType => (addError(literal, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, expectedType.name, dataType.name), -1)
            }
            case dataType => (addError(literal, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, expectedType.name, dataType.name), -1)
          }
          literal.copy(dataType = dataType.getOrElse(NoType), value = value)
        case BIT_STRING_LITERAL =>
          val Regex = "(b|o|x)\"([a-f0-9]+)\"".r
          literal.text.replace("_", "").toLowerCase match {
            case Regex(baseSpecifier, values) =>
              val valueString = baseSpecifier match {
                case "b" =>
                  if (values.zipWithIndex.forall{
                    case (character, i) =>
                      if (character != '0' && character != '1') {
                        addErrorPosition(literal.position.addCharacterOffset(i + 1), SemanticMessage.INVALID_BASED_LITERAL_CHARACTER, character.toString, "binary")
                        false
                      } else true
                  })
                    Integer.parseInt(values, 2).toBinaryString
                  else values
                case "o" => values.zipWithIndex.map{
                  case (character, i) =>
                    character match {
                      case '0' => "000"
                      case '1' => "001"
                      case '2' => "010"
                      case '3' => "011"
                      case '4' => "100"
                      case '5' => "101"
                      case '6' => "110"
                      case '7' => "111"
                      case _ => addErrorPosition(literal.position.addCharacterOffset(i + 1), SemanticMessage.INVALID_BASED_LITERAL_CHARACTER, character.toString, "octal")
                    }
                }.mkString
                case "x" => values.zipWithIndex.map{
                  case (character, i) =>
                    character match {
                      case '0' => "0000"
                      case '1' => "0001"
                      case '2' => "0010"
                      case '3' => "0011"
                      case '4' => "0100"
                      case '5' => "0101"
                      case '6' => "0110"
                      case '7' => "0111"
                      case '8' => "1000"
                      case '9' => "1001"
                      case 'a' => "1010"
                      case 'b' => "1011"
                      case 'c' => "1100"
                      case 'd' => "1101"
                      case 'e' => "1110"
                      case 'f' => "1111"
                      case _ => addErrorPosition(literal.position.addCharacterOffset(i + 1), SemanticMessage.INVALID_BASED_LITERAL_CHARACTER, character.toString, "hex")
                    }
                }.mkString
              }
              visitLiteral(Literal(literal.position, valueString, STRING_LITERAL))
          }
        case BASED_LITERAL =>
        //INTEGER '#' BASED_INTEGER ( DOT BASED_INTEGER )? '#' EXPONENT? ;
          val Regex = """(\d+)#([a-f0-9]+)(.([a-f0-9]+))?#(e(['+'|'-']?)(\d+))?""".r
          literal.text.replace("_", "").toLowerCase match {
            case Regex(baseString, values, _, fractionString, _, sign, exponentString) =>
              val base = baseString.toInt
              val exponent = (if (exponentString != null) math.pow(base, Integer.parseInt(exponentString)).toInt else 1)
              if (fractionString == null) {
                val value = (Integer.parseInt(values, base) * exponent)
                Literal(literal.position, value.toString, INTEGER_LITERAL, SymbolTable.universalIntegerType, value)
              }
              else {
                val fraction = fractionString.zipWithIndex.map{
                  case (digit, i) => Integer.parseInt(digit.toString, base) / math.pow(base, i + 1)
                }.sum
                val value = ((Integer.parseInt(values, base) + fraction) * exponent)
                Literal(literal.position, value.toString, REAL_LITERAL, SymbolTable.universalRealType, value)
              }
          }
        case NULL_LITERAL => literal.copy(dataType = NullType)
      }
    }

    def visitLogicalExpression(logicalExpr: LogicalExpression): Expression = {
      def isBooleanOrBit(dataType: DataType): Boolean = dataType == SymbolTable.bitType || dataType == SymbolTable.booleanType
      if (isBooleanOrBit(logicalExpr.left.dataType) && logicalExpr.left.dataType == logicalExpr.right.dataType) logicalExpr.copy(dataType = logicalExpr.left.dataType)
      else (logicalExpr.left.dataType, logicalExpr.right.dataType) match {
        case (left: ArrayType, right: ArrayType) if (left.dimensions.size == 1 && right.dimensions.size == 1 && isCompatible(left, right) && isBooleanOrBit(left.elementType)) => logicalExpr.copy(dataType = logicalExpr.left.dataType)
        case _ => findOverloadedOperator(logicalExpr.operator.toString, logicalExpr, logicalExpr.left, logicalExpr.right).getOrElse(logicalExpr.copy(dataType = NoType))
      }
    }

    def visitName(name: Name): Expression = {
      def matchParts(parts: Seq[Name.Part], symbol: Symbol): Option[Expression] = {
        if (symbol.isInstanceOf[AliasSymbol]) matchParts(parts, symbol.asInstanceOf[AliasSymbol].destination)
        else parts match {
          case Seq() => None
          case Seq(part, xs@_*) =>
            part match {
              case Name.AssociationListPart(_, associationList) =>
                if (associationList.elements.forall(element => element.formalPart.isEmpty && element.actualPart.nonEmpty)) {
                  val indexes = associationList.elements.map(_.actualPart.get)
                  symbol match {
                    case subTypeSymbol: SubTypeSymbol =>
                      require(xs.isEmpty)
                      if (indexes.size == 1) Option(visitTypeCastExpression(TypeCastExpression(indexes.head, subTypeSymbol.dataType)))
                      else addError(name, SemanticMessage.INVALID_TYPE_CAST)
                    case typeSymbol: TypeSymbol =>
                      require(xs.isEmpty)
                      if (indexes.size == 1) Option(visitTypeCastExpression(TypeCastExpression(indexes.head, typeSymbol.dataType)))
                      else addError(name, SemanticMessage.INVALID_TYPE_CAST)
                    case r: RuntimeSymbol => r.dataType match {
                      case array: ArrayType =>
                        if (indexes.size != array.dimensions.size)
                          addError(part, SemanticMessage.INVALID_INDEXES_COUNT, indexes.size.toString, array.dimensions.size.toString)
                        val expressions = indexes.zip(array.dimensions).map(x => checkExpression(context, x._1, x._2.elementType))
                        val newSymbol = r.makeCopy(Identifier(part.position, "array"), array.elementType, symbol)
                        val expr = matchParts(xs, newSymbol).getOrElse(EmptyExpression)
                        Option(new ArrayAccessExpression(r, expressions, if (expr eq EmptyExpression) array.elementType else expr.dataType, expr))
                      case _ => addError(part, SemanticMessage.NOT_A, r.name, "array")
                    }
                    case list: ListOfSubprograms =>
                      require(xs.isEmpty)
                      val expression = visitFunctionCallExpression(FunctionCallExpression(new SelectedName(Seq(name.identifier)), Some(associationList)))
                      /*
                      TODO match the result value of a expression, e.g. x:=fooFunction(1,2,3,4).x;
                      val newSymbol = ConstantSymbol(Identifier("blub"), expression.dataType, -1, null)
                      val expr = matchParts(xs, newSymbol).getOrElse(EmptyExpression)
                      */
                      Option(expression)
                    case symbol => addError(name, SemanticMessage.NOT_A, symbol.name, "function")
                  }
                } else Option(visitFunctionCallExpression(FunctionCallExpression(new SelectedName(Seq(name.identifier)), Some(associationList))))
              case Name.AttributePart(signatureOption, identifier, expression) =>
                require(xs.isEmpty)
                (symbol match {
                  case listOfSubprograms: ListOfSubprograms => signatureOption match {
                    case Some(signature) => findSubprogramFromSignature(context, listOfSubprograms, signature)
                    case None => addError(identifier, "an attribute for a subprogram needs signature")
                  }
                  case ListOfEnumerations(_, enumerations) => signatureOption match {
                    case Some(signature) => findLiteralFromSignature(context, enumerations, signature)
                    case None => addError(identifier, "an attribute for an enumeration literal needs a signature")
                  }
                  case _ =>
                    for (signature <- signatureOption) addError(signature, "signature not allowed for non subprogram or enumeration attributes")
                    Option(symbol)
                }).flatMap{
                  symbol =>
                    symbol.attributes.get(identifier.text) match {
                      case None =>
                        expression.foreach(acceptExpression(_, NoType, context))
                        addError(part, SemanticMessage.NOT_FOUND, "attribute", identifier.text)
                      case Some(attribute) =>
                        Option(visitAttributeExpression(AttributeExpression(identifier.position, symbol, attribute, expression, attribute.dataType)))
                    }
                }
              case Name.SlicePart(range) =>
                require(xs.isEmpty)
                symbol match {
                  case r: RuntimeSymbol if (r.dataType.isInstanceOf[ArrayType]) => Option(new RangeAccessExpression(r, range, r.dataType))
                  case s => addError(part, SemanticMessage.NOT_A, "array", s.name)
                }
              case Name.SelectedPart(identifier) => symbol match {
                case r: RuntimeSymbol if (r.dataType.isInstanceOf[RecordType] || (r.dataType.isInstanceOf[AccessType] && r.dataType.asInstanceOf[AccessType].pointerType.isInstanceOf[RecordType])) =>
                  ((r.dataType: @unchecked) match {
                    case recordType: RecordType => recordType
                    case accessType: AccessType => accessType.pointerType.asInstanceOf[RecordType]
                  }).elementsMap.get(identifier.text) match {
                    case None => addError(part, SemanticMessage.NOT_FOUND, "field", identifier.text)
                    case Some(dataType) =>
                      val newSymbol = r.makeCopy(identifier, dataType, symbol)
                      val expr = matchParts(xs, newSymbol).getOrElse(EmptyExpression)
                      Option(new FieldAccessExpression(r, identifier, dataType, if (expr eq EmptyExpression) dataType else expr.dataType, expr))
                  }
                case s => addError(part, SemanticMessage.NOT_A, s.name, "record")
              }
            }
        }
      }

      context.symbolTable.find(operatorMangleMap.get(name.identifier.text).getOrElse(name.identifier.originalText)) match {
        case None => name.parts match {
          case Seq() => expectedType match {
            case _: ArrayType | NoType => visitLiteral(Literal(name.position, name.identifier.text, Literal.Type.STRING_LITERAL)) //TODO
            case _ => {
              addError(name, SemanticMessage.NOT_FOUND, "symbol", name.identifier)
              EmptyExpression
            }
          }
          case xs => {
            addError(name, SemanticMessage.NOT_FOUND, "symbol", name.identifier)
            EmptyExpression
          }
        }
        case Some(symbol) => name.parts match {
          case Seq() =>
            def matchSymbol(symbol: Symbol): Expression = symbol match {
              case AliasSymbol(_, destination) => matchSymbol(destination)
              case list: ListOfSubprograms => visitFunctionCallExpression(FunctionCallExpression(new SelectedName(Seq(name.identifier)), None))
              case unitSymbol: UnitSymbol => PhysicalLiteral(name.position, "1", new SelectedName(Seq(name.identifier)), Literal.Type.INTEGER_LITERAL, unitSymbol)
              case ListOfEnumerations(_, enumerations) => enumerations.find(_.dataType == expectedType) match {
                case Some(enumSymbol) => Literal(name.position, name.identifier.text, Literal.Type.INTEGER_LITERAL, enumSymbol.dataType, enumSymbol.dataType.intValue(enumSymbol.identifier.text))
                case _ =>
                  addError(name, "enumerations value " + name.identifier.text)
                  EmptyExpression
              }
              case r: RuntimeSymbol => ItemExpression(name.position, r)
            }
            matchSymbol(symbol)
          case xs => matchParts(xs, symbol).getOrElse(EmptyExpression)
        }
      }
    }

    def visitNewExpression(newExpression: NewExpression): Expression =
    // TODO Auto-generated method stub
      newExpression.qualifiedExpressionOrSubTypeIndication match {
        case Left(qualifiedExpression) => expectedType match {
          case accessType: AccessType =>
            val expression = checkExpression(context, qualifiedExpression, accessType.pointerType)
            NewExpression(newExpression.position, Left(expression), accessType)
          case otherType =>
            addError(qualifiedExpression, SemanticMessage.NOT_A, "access type", otherType.name)
            EmptyExpression
        }
        case Right(subType) =>
          val dataType = createType(context, subType)
          NewExpression(newExpression.position, Right(subType), dataType)
      }

    def visitQualifiedExpression(qualifiedExpression: QualifiedExpression): Expression = {
      //because a qualified expression is parsed as type_mark ' aggregate we must distinguish between the two forms
      //the two forms are parsed as aggregate because a aggregate is of the from ( (choices ARROW)? expression (, (choices ARROW)? expression)* ), so a input of
      //type_mark ' ( expression ) can also be an aggregate with one element without a choices part
      val dataType = context.findType(qualifiedExpression.typeName)
      val expressionToCheck = qualifiedExpression.expression match {
        case Aggregate(_, Seq(Aggregate.ElementAssociation(None, expression)), _) => expression //expression is of type: type_mark ' ( expression )
        case _ => qualifiedExpression.expression //expression is of type: type_mark ' aggregate
      }
      checkExpression(context, expressionToCheck, dataType)
    }

    def visitRelation(relation: Relation): Expression = {
      import Relation.Operator._
      def checkIfFileOrProtected(dataType: DataType): Boolean = dataType.isInstanceOf[FileType] || dataType.isInstanceOf[ProtectedType]
      val valid = relation.operator match {
        case EQ | NEQ =>
          if (checkIfFileOrProtected(relation.left.dataType) || checkIfFileOrProtected(relation.right.dataType)) false
          else if (!isCompatible(relation.left.dataType, relation.right.dataType)) false
          else true
        case _ => (relation.left.dataType, relation.right.dataType) match {
          case (left: ScalarType, right: ScalarType) if (isCompatible(left, right)) => true
          case (left: ArrayType, right: ArrayType) if (isCompatible(left, right) && left.elementType.isInstanceOf[DiscreteType] && right.elementType.isInstanceOf[DiscreteType]) => true
          case _ => false
        }
      }
      if (!valid) findOverloadedOperator(relation.operator.toString, relation, relation.left, relation.right).getOrElse(relation.copy(dataType = NoType))
      else relation.copy(dataType = SymbolTable.booleanType)
    }

    def visitShiftExpression(shiftExpr: ShiftExpression): Expression = {
      val dataType = shiftExpr.left.dataType match {
        case arrayType: ArrayType if (arrayType.dimensions.size == 1 && (arrayType.elementType == SymbolTable.bitType) || arrayType.elementType == SymbolTable.booleanType) =>
          if (shiftExpr.right.dataType != SymbolTable.integerType && shiftExpr.right.dataType != SymbolTable.universalIntegerType) NoType
          else shiftExpr.left.dataType
        case otherType => NoType
      }
      if (dataType == NoType) findOverloadedOperator(shiftExpr.operator.toString, shiftExpr, shiftExpr.left, shiftExpr.right).getOrElse(shiftExpr.copy(dataType = NoType))
      else shiftExpr.copy(dataType = dataType)
    }

    def visitSimpleExpression(simpleExpr: SimpleExpression): Expression = simpleExpr.signOperator match {
      case Some(sign) => simpleExpr.left.dataType match {
        case numericType: NumericType => simpleExpr.copy(dataType = numericType)
        case dataType => findOverloadedOperator(sign.toString, simpleExpr, simpleExpr.left).getOrElse(simpleExpr.copy(dataType = NoType))
      }
      case None =>
        val right = simpleExpr.rightOption.get
        (simpleExpr.left.dataType, right.dataType) match {
          case (left: NumericType, right) if (isCompatible(left, right)) => simpleExpr.copy(dataType = left)
          case _ => findOverloadedOperator(simpleExpr.addOperator.get.toString, simpleExpr, simpleExpr.left, right).getOrElse(simpleExpr.copy(dataType = NoType))
        }
    }

    def visitTerm(term: Term): Expression = {
      def isValid(dataType: DataType): Boolean = dataType == SymbolTable.integerType || dataType == SymbolTable.universalIntegerType || dataType == SymbolTable.realType || dataType == SymbolTable.universalRealType
      import Term.Operator._
      val dataType = term.operator match {
        case MUL => (term.left.dataType, term.right.dataType) match {
          case (integerOrRealType@(_: IntegerType | _: RealType), right) if (isCompatible(integerOrRealType, right)) => integerOrRealType
          case (physicalType: PhysicalType, integerOrRealType@(_: IntegerType | _: RealType)) if (isValid(integerOrRealType)) => physicalType
          case (integerOrRealType@(_: IntegerType | _: RealType), physicalType: PhysicalType) if (isValid(integerOrRealType)) => physicalType
          case (SymbolTable.universalRealType, SymbolTable.universalIntegerType) => SymbolTable.universalRealType
          case (SymbolTable.universalIntegerType, SymbolTable.universalRealType) => SymbolTable.universalRealType
          case _ => NoType
        }
        case DIV => (term.left.dataType, term.right.dataType) match {
          case (integerOrRealType@(_: IntegerType | _: RealType), right) if (isCompatible(integerOrRealType, right)) => integerOrRealType
          case (physicalType: PhysicalType, integerOrRealType@(_: IntegerType | _: RealType)) if (isValid(integerOrRealType)) => physicalType
          case (physicalType: PhysicalType, right) if (physicalType == right) => SymbolTable.universalIntegerType
          case (SymbolTable.universalRealType, SymbolTable.universalIntegerType) => SymbolTable.universalRealType
          case _ => NoType
        }
        case MOD | REM => (term.left.dataType, term.right.dataType) match {
          case (integerType: IntegerType, right) if (integerType == right || right == SymbolTable.universalIntegerType) => integerType
          case _ => NoType
        }
      }
      if (dataType == NoType) findOverloadedOperator(term.operator.toString, term, term.left, term.right).getOrElse(term.copy(dataType = NoType))
      else term.copy(dataType = dataType)
    }

    def visitTypeCastExpression(typeCastExpression: TypeCastExpression): Expression = {
      // TODO check if cast is allowed
      val expression = acceptExpression(typeCastExpression.expression, NoType, context)
      typeCastExpression.dataType match {
        case _: IntegerType if expression.dataType.isInstanceOf[RealType] =>
        case _: RealType if expression.dataType.isInstanceOf[IntegerType] =>
        case arr: ArrayType if (expression.dataType.isInstanceOf[ArrayType]) =>
          expression.dataType match {
            case arr2: ArrayType => (arr.elementType eq arr2.elementType) && arr.dimensions.size == arr2.dimensions.size
            case _ => addError(typeCastExpression, SemanticMessage.NOT_ALLOWED, "cast")
          }
        case _ => addError(typeCastExpression, SemanticMessage.NOT_ALLOWED, "cast")
      }
      typeCastExpression.copy(expression = expression)
    }
    throw new RuntimeException()
  }

  def findSubprogramFromSignature(context: Context, listOfFunctions: ListOfSubprograms, signature: Signature): Option[SubprogramSymbol] = {
    def signatureToTypes(context: Context, signature: Signature): (Seq[DataType], Option[DataType]) =
      (signature.parameterList.map(list => list.map(parameter => context.findType(parameter))).flatten.toSeq, signature.returnType.map(returnType => context.findType(returnType)))

    val (dataTypes, returnTypeOption) = signatureToTypes(context, signature)
    context.findSubprogramInList(listOfFunctions, dataTypes, returnTypeOption).orElse(addError(signature, "no subprogram found with this signature"))
  }

  def findLiteralFromSignature(context: Context, enumerations: Seq[EnumerationSymbol], signature: Signature): Option[EnumerationSymbol] = {
    signature.parameterList.foreach(_.foreach(typeName => addError(typeName, "a signature for an enumeration literal can not have a parameter type")))
    signature.returnType match {
      case Some(returnType) => enumerations.find(_.dataType == context.findType(returnType)).orElse(addError(signature, "no enumeration literal found with this signature"))
      case _ => addError(signature, "a signature for an enumeration literal alias needs a return type")
    }
  }

  def checkExpressionOption(context: Context, exprOption: Option[Expression], dataType: DataType): Option[Expression] =
    exprOption map (checkExpression(context, _, dataType))

  def checkExpression(context: Context, expr: Expression, dataType: DataType): Expression = {
    val newExpr = acceptExpression(expr, dataType, context)
    if (!isCompatible(newExpr.dataType, dataType)) {
      addError(expr, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, dataType.name, newExpr.dataType.name)
    }
    newExpr
  }

  @tailrec
  def insertSequentialStatementsLabels(context: Context, statements: Seq[SequentialStatement], owner: Symbol): Context = statements match {
    case Seq() => context
    case Seq(statement, xs@_*) =>
      def insertLabelSymbol(statement: SequentialStatement): Context = statement.label.map(identifier => context.insertSymbol(new LabelSymbol(identifier, owner))).getOrElse(context)
      val list = statement match {
        case abstractLoopStatement: AbstractLoopStatement => abstractLoopStatement.sequentialStatementList
        case ifStatement: IfStatement => ifStatement.ifThenList.map(_.statements).flatten ++ ifStatement.elseSequentialStatementList.getOrElse(Seq())
        case caseStatement: CaseStatement => caseStatement.alternatives.map(_.statements).flatten
        case _ => Seq()
      }
      insertSequentialStatementsLabels(insertSequentialStatementsLabels(insertLabelSymbol(statement), list, owner), xs, owner)
  }

  @tailrec
  def insertConcurrentStatementsLabels(context: Context, statements: Seq[ConcurrentStatement], owner: Symbol): Context = statements match {
    case Seq() => context
    case Seq(statement, xs@_*) =>
      insertConcurrentStatementsLabels(statement.label.map(identifier => context.insertSymbol(new LabelSymbol(identifier, owner))).getOrElse(context), xs, owner)
  }

  def checkAssociationList(context: Context, associationList: Option[AssociationList], symbols: Seq[RuntimeSymbol], owner: Locatable): Option[AssociationList] =
  //TODO check formal Signal => actual Signal, formal Constant => actual Constant, formal Variable => actual Variable, formal File => actual File
    associationList match {
      case None =>
        if (symbols.filter(symbol => !symbol.isOptional).nonEmpty) addError(owner, SemanticMessage.INVALID_ARG_COUNT, "0", symbols.size.toString)
        None
      case Some(list) =>
        val index = list.elements.findIndexOf(element => element.formalPart.isDefined)
        (if (index != -1) list.elements.drop(index).find(element => element.formalPart.isEmpty) else None) match {
          case Some(element) => addError(element.actualPart.get, "can not use positional element after named element")
          case _ =>
            if (list.elements.size != symbols.size) addError(owner, SemanticMessage.INVALID_ARG_COUNT, associationList.iterator.size.toString, symbols.size.toString)
            Some(list.copy(parameters = list.elements.map(element => checkExpression(context, element.actualPart.get, NoType)))) // TODO check parameters
        }
    }

  def checkDiscreteRange(context: Context, discreteRange: DiscreteRange, calcValues: Boolean): DiscreteRange =
    discreteRange.rangeOrSubTypeIndication match {
      case Left(range) =>
        val newRange = checkRange(context, range, calcValues)
        if (!newRange.dataType.isInstanceOf[DiscreteType]) addError(newRange, SemanticMessage.EXPECTED_TYPE, "discrete")
        new DiscreteRange(Left(newRange), dataType = new ConstrainedRangeType(newRange.dataType, lowLong.toInt, highLong.toInt))
      case Right(subTypeIndication) =>
      // TODO
        val dataType = context.findType(subTypeIndication.typeName)
        for (resolutionFunction <- subTypeIndication.resolutionFunction) addError(resolutionFunction, "resolution function")
        subTypeIndication.constraint.map{
          _ match {
            case Left(range) =>
              val newRange = checkRange(context, range, calcValues)
              new DiscreteRange(Left(newRange), dataType = new ConstrainedRangeType(dataType, lowLong.toInt, highLong.toInt))
            case _ =>
              addError(subTypeIndication.typeName, "index constraint")
              discreteRange
          }
        }.getOrElse(discreteRange)
    }

  def checkLoopLabel(context: Context, loopLabelOption: Option[Identifier], node: ASTNode, stmtName: String): Position =
    (loopLabelOption match {
      case None => context.loopLabels match {
        case Seq() => addError(node, SemanticMessage.NOT_INSIDE_A_LOOP, stmtName)
        case Seq((_, position), _) => Some(position)
      }
      case Some(loopLabel) => context.loopLabels.find(x => x._1.isDefined && x._1.get == loopLabel) match {
        case None => addError(loopLabel, SemanticMessage.NOT_FOUND, "loop label", loopLabel)
        case Some((_, position)) => Some(position)
      }
    }).orNull

  def checkPure(context: Context, node: ASTNode, owner: Symbol, symbol: Symbol) = owner match {
    case functionSymbol: FunctionSymbol =>
      if (functionSymbol.isPure && (symbol.owner ne owner)) {
        addError(node, SemanticMessage.ASSIGN_IN_PURE_FUNCTION, symbol.getClass.toString, functionSymbol.name, symbol.name)
      }
    case _ =>
  }

  def checkRange(context: Context, range: Range, calcValues: Boolean = false): Range = range.expressionsOrName match {
  //TODO check if range expression are locally static expressions
    case Left((sourceFromExpression, direction, sourceToExpression)) =>
      val fromExpression = checkExpression(context, sourceFromExpression, NoType)
      val toExpression = checkExpression(context, sourceToExpression, fromExpression.dataType)
      if (calcValues) {
        def calcAndCheck[A](implicit numeric: Integral[A]): (A, A) = {
          import numeric.mkOrderingOps
          val x = StaticExpressionCalculator.calcValue(fromExpression)(numeric)
          val y = StaticExpressionCalculator.calcValue(toExpression)(numeric)
          direction match {
            case Range.Direction.To => if (x > y) addError(fromExpression, SemanticMessage.INVALID_TO_DIRECTION)
            case Range.Direction.Downto => if (x < y) addError(fromExpression, SemanticMessage.INVALID_DOWNTO_DIRECTION)
          }
          direction match {
            case Range.Direction.To => (x, y)
            case Range.Direction.Downto => (y, x)
          }
        }
        fromExpression.dataType match {
          case _: IntegerType | _: EnumerationType =>
            val x = calcAndCheck(LongIsIntegral)
            lowLong = x._1
            highLong = x._2
          case _: RealType =>
            val x = calcAndCheck(DoubleAsIfIntegral)
            lowDouble = x._1
            highDouble = x._2
          case _ => addError(fromExpression, SemanticMessage.INVALID_SIMPLE_EXPRESSION)
        }
      }
      new Range(new Left((fromExpression, direction, toExpression)), dataType = fromExpression.dataType)
    case Right(attributeName) =>
      range
  /* TODO context.find(attributeName) match {
    case None =>
      addError(attributeName, SemanticMessage.NOT_FOUND, "type or attribute", attributeName.toString)
      range
    case Some(symbol) => symbol match {
      case typeSymbol: TypeSymbol =>
        typeSymbol.dataType match {
          case scalar: ScalarType =>
            val fromExpression = Literal(attributeName.position, scalar.leftAttribute.toString, Literal.Type.DECIMAL_LITERAL, scalar)
            val toExpression = Literal(attributeName.position, scalar.rightAttribute.toString, Literal.Type.DECIMAL_LITERAL, scalar)
            lowLong = scalar.leftAttribute.asInstanceOf[Int]
            highLong = scalar.rightAttribute.asInstanceOf[Int]
            new Range(fromExpression = fromExpression, toExpression = toExpression, direction = Range.Direction.To, attributeNameOption = None)
          case dataType =>
            addError(attributeName, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, "scalar", dataType.name)
            range
        }
      case attribute: AttributeSymbol =>
        attribute.dataType match {
          case rangeType: UnconstrainedRangeType =>
            //TODO  symbol and Expression
            val dimension = Literal(attributeName.position, "1", Literal.Type.DECIMAL_LITERAL, SymbolTable.integerType)
            val fromExpression = AttributeExpression(attributeName.position, attribute.owner, new AttributeSymbol("left", rangeType.elementType, None), Some(dimension), rangeType.elementType)
            val toExpression = AttributeExpression(attributeName.position, attribute.owner, new AttributeSymbol("right", rangeType.elementType, None), Some(dimension), rangeType.elementType)
            new Range(fromExpression = fromExpression, toExpression = toExpression, direction = if (attribute.name == "range") Range.Direction.To else Range.Direction.Downto, attributeNameOption = None)
          case dataType =>
            addError(attributeName, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, "range", dataType.name)
            range
        }
    }
  }*/
  }

  def getMangledName(name: Identifier): Identifier = {
    if (name.originalText(0) != '"') return name
    val text = operatorMangleMap.get(name.text) match {
      case Some(mangledName) => mangledName
      case _ =>
        addError(name, SemanticMessage.NOT_A, name.text, "valid overloaded operator")
        name.text
    }
    Identifier(name.position, text)
  }

  def checkIsStaticExpression(expr: Option[Expression]) {
    //TODO
  }

  def getStartIndex(owner: Symbol): Int = owner match {
    case _: PackageSymbol | null => 0
    case _: ArchitectureSymbol | _: ProcessSymbol | _: TypeSymbol => 1
    case subProgramSymbol: SubprogramSymbol => getStartIndex(subProgramSymbol.owner)
  }

  def createSymbolsFromInterfaceList(context: Context, interfaceListOption: Option[InterfaceList], owner: Symbol): Seq[RuntimeSymbol] =
    interfaceListOption match {
      case None => List()
      case Some(interfaceList) =>
        import InterfaceList._
        var varIndex = getStartIndex(owner)
        interfaceList.elements.flatMap{
          element =>
            val dataType = createType(context, element.subType)
            if (dataType.isInstanceOf[ProtectedType] && element.expression.isDefined) addError(element.expression.get, "a protected type can not have a default value")
            element.expression = checkExpressionOption(context, element.expression, dataType)
            checkIsStaticExpression(element.expression)

            import InterfaceList.InterfaceMode.{IN, LINKAGE}
            val mode = element.interfaceMode.getOrElse(IN)
            if (mode == LINKAGE) addError(element.subType, "linkage mode is currently not supported") //LINKAGE is used for local variables and signals
            val nextIndex = getNextIndex(dataType)
            val isOptional = element.expression.isDefined
            element.identifierList.map{
              identifier =>
                val (symbol, indexChange) = element match {
                  case variableDeclaration: InterfaceVariableDeclaration =>
                    if (mode != IN && element.expression.isDefined) addError(element.expression.get, "a variable parameter with a mode other than in can not have a default value")
                    (new VariableSymbol(identifier, dataType, mode, varIndex, owner, isOptional, isParameter = true), nextIndex)
                  case signalDeclaration: InterfaceSignalDeclaration =>
                    checkIfNotFileProtectedAccessType(element.subType, dataType)
                    if (owner.isInstanceOf[SubprogramSymbol] && element.expression.isDefined) addError(element.expression.get, "a signal parameter can not have a default value")
                    (new SignalSymbol(identifier, dataType, mode, if (signalDeclaration.bus) Option(SignalDeclaration.Type.BUS) else None, varIndex, owner, isOptional, isParameter = true), 1)
                  case fileDeclaration: InterfaceFileDeclaration =>
                    if (!dataType.isInstanceOf[FileType]) addError(element.subType, "file type expected")
                    (new FileSymbol(identifier, dataType, varIndex, owner, isOptional, isParameter = true), 1)
                  case constantDeclaration: InterfaceConstantDeclaration =>
                    checkIfNotFileProtectedAccessType(element.subType, dataType)
                    (new ConstantSymbol(identifier, dataType, varIndex, owner, isOptional, isParameter = true), nextIndex)
                }
                varIndex += indexChange
                symbol
            }
        }
    }

  def createType(context: Context, subtypeIndication: SubTypeIndication, subTypeName: String = "subtype", isAccessTypeDefinition: Boolean = false): DataType = {
    def createEnumerationSubType(range: Range, baseType: EnumerationType, resolutionFunction: Option[FunctionSymbol]): EnumerationType = range.expressionsOrName match {
      case Left((fromExpression, direction, toExpression)) =>
        def getEnumEntry(expr: Expression): Option[Int] = acceptExpression(expr, baseType, context) match {
          case literal: Literal if (literal.dataType eq baseType) => Some(literal.value.asInstanceOf[Int])
          case _ => None
        }

        val low = getEnumEntry(fromExpression).getOrElse(baseType.left)
        val high = getEnumEntry(toExpression).getOrElse(baseType.right)
        direction match {
          case Range.Direction.To => if (low > high) addError(fromExpression, SemanticMessage.INVALID_TO_DIRECTION)
          case Range.Direction.Downto => if (low < high) addError(fromExpression, SemanticMessage.INVALID_DOWNTO_DIRECTION)
        }
        val (newList, _) = baseType.elements.zipWithIndex.filter{
          case (_, i) => i >= low && i <= high
        }.unzip
        new EnumerationType(subTypeName, newList, Option(baseType.baseType.getOrElse(baseType)), baseType.owner, resolutionFunction)
      case Right(attributeName) => error("not implemented")
    }

    def createIntegerOrRealSubType[T <: DataType](sourceRange: Range, baseType: DataType, resolutionFunction: Option[FunctionSymbol]): DataType = {
      def check[A](lowerBound: A, baseTypeLowerBound: A, upperBound: A, baseTypeUpperBound: A)(implicit numeric: Integral[A]) {
        import numeric.mkOrderingOps
        if (lowerBound < baseTypeLowerBound) addError(sourceRange, "lower bound %s is smaller than the lower bound of the base type %s", lowerBound.toString, baseTypeLowerBound.toString)
        if (upperBound > baseTypeUpperBound) addError(sourceRange, "upper bound %s is greater than the upper bound of the base type %s", upperBound.toString, baseTypeUpperBound.toString)
      }
      val range = checkRange(context, sourceRange, true)
      val dataType = range.dataType
      if (dataType.getClass eq baseType.getClass) {
        (baseType: @unchecked) match {
          case intBaseType: IntegerType =>
            val intType = new IntegerType(subTypeName, lowLong.toInt, highLong.toInt, Option(intBaseType.baseType.getOrElse(intBaseType)), resolutionFunction) //if this is a subtype of a subtype we want the real base type
            check(intType.lowerBound, intBaseType.lowerBound, intType.upperBound, intBaseType.upperBound)
            intType
          case realBaseType: RealType =>
            val realType = new RealType(subTypeName, lowDouble, highDouble, Option(realBaseType.baseType.getOrElse(realBaseType)), resolutionFunction)
            check(realType.lowerBound, realBaseType.lowerBound, realType.upperBound, realBaseType.upperBound)(DoubleAsIfIntegral)
            realType
        }
      } else {
        addError(range, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, baseType.name, dataType.name)
        NoType
      }
    }
    val baseType = context.findType(subtypeIndication.typeName, isAccessTypeDefinition)
    val resolutionFunction = subtypeIndication.resolutionFunction.flatMap{
      resolutionFunction => context.findSymbol(resolutionFunction, classOf[ListOfSubprograms]).flatMap{
        listOfSubprograms => context.findFunctionInList(listOfSubprograms, Seq(UnconstrainedArrayType("", baseType, Seq(UnconstrainedRangeType(SymbolTable.naturalType)))), baseType).orElse{
          addError(resolutionFunction, "no resolution function found with the right signature\n a resolution function must have a single input parameter of" +
            "class constant that is a one-dimensional, unconstrained array whose element type is that of the resolved signal. \nThe type of the return value of the function must also be that of the signal.")
        }
      }
    }
    baseType match {
      case IncompleteType =>
        if (isAccessTypeDefinition) subtypeIndication.constraint.foreach(constraint => addError(subtypeIndication.typeName, "can not use an incomplete type with constraints"))
        baseType
      case _ => subtypeIndication.constraint match {
        case None => baseType
        case Some(constraint) => constraint match {
          case Left(range) => baseType match {
            case _: IntegerType | _: RealType => createIntegerOrRealSubType(range, baseType, resolutionFunction)
            case e: EnumerationType => createEnumerationSubType(range, e, resolutionFunction)
            case _: AccessType =>
            //The only form of constraint that is allowed after the name of an access type in a subtype indication is an index constraint
              addError(range, SemanticMessage.NOT_ALLOWED, "access subtype with a range constraint")
              NoType
            case _ =>
              addError(subtypeIndication.typeName, SemanticMessage.EXPECTED_TYPE, "integer, real or enumeration")
              NoType
          }
          case Right(arrayConstraint) => baseType match {
            case unconstrainedArrayType: UnconstrainedArrayType =>
              new ConstrainedArrayType(unconstrainedArrayType.name, unconstrainedArrayType.elementType, arrayConstraint.map(checkDiscreteRange(context, _, true).dataType))
            case _ =>
              addError(subtypeIndication.typeName, SemanticMessage.NOT_ALLOWED, "array constraint for non array subtypes")
              NoType
          } //error("not implemented")
        }
      }
    }
  }

  def visitAliasDeclaration(aliasDeclaration: AliasDeclaration, context: Context): ReturnType = {
    val (identifierList, rest) = aliasDeclaration.name.parts.span(_.isInstanceOf[Name.SelectedPart])
    val identifier = new SelectedName(aliasDeclaration.name.identifier +: identifierList.map(_.asInstanceOf[Name.SelectedPart].identifier))

    def createAliasSymbol(symbol: Symbol): Option[AliasSymbol] = symbol match {
      case r: RuntimeSymbol =>
      //object alias
        for (signature <- aliasDeclaration.signature) addError(signature, "signature not allowed for object alias")
        r match {
        /*
        TODO
        case constSymbol: ConstantSymbol => new ConstantSymbol(name, dataType.getOrElse(constSymbol.dataType), constSymbol.index, constSymbol.owner)
        case varSymbol: VariableSymbol => new VariableSymbol(name, dataType.getOrElse(varSymbol.dataType), varSymbol.modifier, varSymbol.index, varSymbol.owner)
        case signalSymbol: SignalSymbol => new SignalSymbol(name, dataType.getOrElse(signalSymbol.dataType), signalSymbol.modifier, signalSymbol.signalType, signalSymbol.index, signalSymbol.owner)
        case fileSymbol: FileSymbol => new FileSymbol(name, dataType.getOrElse(fileSymbol.dataType), fileSymbol.index, fileSymbol.owner)
        */
          case _ => None
        }
      case _: LabelSymbol => addError(aliasDeclaration.identifier, "aliases for labels are not allowed")
      case _ =>
      //non-object alias
        for (subType <- aliasDeclaration.subType) addError(subType, "a subtype indication is not allowed for a non object alias")
        rest.headOption.foreach(part => addError(part, "illegal name part in non object alias"))
        symbol match {
          case listOfSubprograms: ListOfSubprograms => aliasDeclaration.signature match {
            case Some(signature) => findSubprogramFromSignature(context, listOfSubprograms, signature).map(subprogramSymbol => new AliasSymbol(aliasDeclaration.identifier, ListOfSubprograms(subprogramSymbol.identifier, Seq(subprogramSymbol))))
            case None => addError(aliasDeclaration.name, "an alias for a subprogram needs signature")
          }
          case ListOfEnumerations(_, enumerations) => aliasDeclaration.signature match {
            case Some(signature) => findLiteralFromSignature(context, enumerations, signature).map(enumerationSymbol => new AliasSymbol(aliasDeclaration.identifier, ListOfEnumerations(enumerationSymbol.identifier, Seq(enumerationSymbol))))
            case None => addError(aliasDeclaration.name, "an alias for an enumeration literal needs a signature")
          }
          case _ =>
            for (signature <- aliasDeclaration.signature) addError(signature, "signature not allowed for non subprogram or enumeration literal alias")
            Some(new AliasSymbol(aliasDeclaration.identifier, symbol))
        }
    }
    (aliasDeclaration, context.insertSymbol(context.find(identifier).orElse(addError(identifier, "no object found")).flatMap(createAliasSymbol)))
  }

  def visitArchitectureDeclaration(architectureDeclaration: ArchitectureDeclaration, owner: Symbol, context: Context): ReturnType = {
    val entity = architectureDeclaration.entityName.toString
    val (entitySymbol, newSymbolTable) = try
    {
      val scopes = SymbolTable.getScopesFromInputStream(new FileInputStream(configuration.libraryOutputDirectory + entity + ".sym"))
      require(scopes.size != 3, "puh need to think of this code")
      val table = new SymbolTable(scopes ++ context.symbolTable.scopes)
      (context.copy(symbolTable = table).findSymbol(architectureDeclaration.entityName, classOf[EntitySymbol]), table)
    } catch {
      case e: IOException => (addError(architectureDeclaration, SemanticMessage.NOT_FOUND, "entity", entity), context.symbolTable.openScope)
    }
    val symbol = new ArchitectureSymbol(architectureDeclaration.identifier, entitySymbol.orNull, owner)
    val (declarativeItems, c1) = acceptDeclarativeItems(architectureDeclaration.declarativeItems, symbol, insertConcurrentStatementsLabels(context.copy(symbolTable = newSymbolTable), architectureDeclaration.concurrentStatements, symbol))
    val (concurrentStatements, c2) = acceptList(architectureDeclaration.concurrentStatements, symbol, c1)
    c2.closeScope()
    (architectureDeclaration.copy(declarativeItems = declarativeItems, concurrentStatements = concurrentStatements, symbol = symbol), context)
  }

  def visitConfigurationDeclaration(configurationDeclaration: ConfigurationDeclaration, owner: Symbol, context: Context): ReturnType = {
    val configuration = new ConfigurationSymbol(configurationDeclaration.identifier, owner)
    context.findSymbol(configurationDeclaration.entityName, classOf[EntitySymbol]) match {
      case Some(entitySymbol) =>
        val (declarativeItems, newContext) = acceptDeclarativeItems(configurationDeclaration.declarativeItems, configuration, context.openScope.insertSymbol(configuration))
        require(newContext.symbolTable.depth <= 2)
        error("not implemented")
        (configurationDeclaration.copy(declarativeItems = declarativeItems, symbol = configuration), context)
      case _ => (configurationDeclaration, context)
    }
  }

  def visitAssertStatement(assertStmt: AssertStatement, context: Context): ReturnType = {
    val condition = checkExpression(context, assertStmt.condition, SymbolTable.booleanType)
    val reportExpression = checkExpressionOption(context, assertStmt.reportExpression, SymbolTable.stringType)
    val severityExpression = checkExpressionOption(context, assertStmt.severityExpression, SymbolTable.severityLevel)
    (assertStmt.copy(condition = condition, reportExpression = reportExpression, severityExpression = severityExpression), context)
  }

  def visitAttributeDeclaration(attributeDeclaration: AttributeDeclaration, owner: Symbol, context: Context): ReturnType = {
    val dataType = context.findType(attributeDeclaration.typeName)
    checkIfNotFileProtectedAccessType(attributeDeclaration.typeName, dataType)
    val symbol = new AttributeDeclarationSymbol(attributeDeclaration.identifier, dataType, owner)
    (attributeDeclaration, context.insertSymbol(symbol))
  }

  def visitAttributeSpecification(attributeSpec: AttributeSpecification, owner: Symbol, context: Context): ReturnType =
    context.findSymbol(attributeSpec.identifier, classOf[AttributeDeclarationSymbol]).flatMap{
      attributeSymbol =>
        def isRightSubprogram(symbol: SubprogramSymbol): Boolean =
          if (attributeSpec.entityClass == EntityClass.PROCEDURE && symbol.isInstanceOf[ProcedureSymbol]) true
          else if (attributeSpec.entityClass == EntityClass.FUNCTION && symbol.isInstanceOf[FunctionSymbol]) true
          else false

        val entityClass = entityClassToSymbolMap(attributeSpec.entityClass)
        val expression = checkExpression(context, attributeSpec.expression, attributeSymbol.dataType)
        if (attributeSpec.entityClass == EntityClass.ARCHITECTURE || attributeSpec.entityClass == EntityClass.ENTITY || attributeSpec.entityClass == EntityClass.CONFIGURATION) checkIsStaticExpression(Option(expression))

        (attributeSpec.entityList match {
          case Left(entityDesignatorList) => entityDesignatorList.flatMap{
            case (identifier, signatureOption) =>
              context.findSymbol(identifier, entityClass) match {
                case Some(symbol) => symbol match {
                  case listOfSubprograms: ListOfSubprograms => signatureOption match {
                    case Some(signature) => findSubprogramFromSignature(context, listOfSubprograms, signature)
                    case _ => listOfSubprograms.subprograms.filter(isRightSubprogram)
                  }
                  case ListOfEnumerations(_, enumerations) => signatureOption match {
                    case Some(signature) => findLiteralFromSignature(context, enumerations, signature)
                    case _ => enumerations
                  }
                  case _ =>
                    for (signature <- signatureOption) addError(signature, "signature not allowed for non subprogram")
                    Option(symbol)
                }
                case _ => None
              }
          }
          case Right(identifier) =>
            identifier.text match {
              case "all" => context.symbolTable.currentScope.values.flatMap{
                _ match {
                  case symbol if ((symbol.getClass eq entityClass) && symbol.owner == owner) => symbol match {
                    case ListOfSubprograms(_, subprograms) => subprograms.filter(isRightSubprogram)
                    case ListOfEnumerations(_, enumerations) => enumerations
                    case runtimeSymbol: RuntimeSymbol => if (runtimeSymbol.isParameter) None else Option(symbol)
                  }
                  case _ => Seq()
                }
              }
              case "others" => context.symbolTable.currentScope.values.flatMap{
                _ match {
                  case symbol if ((symbol.getClass eq entityClass) && symbol.owner == owner) => symbol match {
                    case ListOfSubprograms(_, subprograms) => subprograms.filter(subprogramSymbol => subprogramSymbol.attributes.contains(attributeSpec.identifier.text) && isRightSubprogram(subprogramSymbol))
                    case ListOfEnumerations(_, enumerations) => enumerations.filter(enumerationSymbol => enumerationSymbol.attributes.contains(attributeSpec.identifier.text))
                    case runtimeSymbol: RuntimeSymbol => if (runtimeSymbol.isParameter) None else (if (symbol.attributes.contains(attributeSpec.identifier.text)) None else Option(symbol))
                    case _ => if (symbol.attributes.contains(attributeSpec.identifier.text)) None else Option(symbol)
                  }
                  case _ => Seq()
                }
              }
            }
        }).flatMap{
          symbol =>
            if (attributeSpec.entityClass == EntityClass.ARCHITECTURE || attributeSpec.entityClass == EntityClass.ENTITY || attributeSpec.entityClass == EntityClass.CONFIGURATION || attributeSpec.entityClass == EntityClass.PACKAGE) {
              if (owner ne symbol) addError(attributeSpec, "An attribute specification for an attribute of a design unit must appear immediately within the declarative part of that design unit.")
            } else if (symbol.owner != owner) addError(attributeSpec, "an attribute specification for a symbol must appear within the declarative part in which that symol is explicitly or implicitly declared.")

            if (symbol.attributes.contains(attributeSpec.identifier.text)) addError(attributeSpec, symbol.name + " already has a attributes named " + attributeSpec.identifier.text)
            else {
              if (attributeSymbol == SymbolTable.foreignAttribute) {
                expression match {
                  case Literal(_, text, Literal.Type.STRING_LITERAL, _, _) => text.split("\\s+") match {
                    case Array(className, methodName) =>
                      symbol.attributes += (attributeSpec.identifier.text -> new UserDefinedAttributeSymbol(attributeSpec.identifier, expression.dataType, attributeSymbol, symbol, Left((className.replace('.', '/'), methodName))))
                    case Array(className, methodName, parameterTypes) =>
                      symbol.attributes += (attributeSpec.identifier.text -> new UserDefinedAttributeSymbol(attributeSpec.identifier, expression.dataType, attributeSymbol, symbol, Right((className.replace('.', '/'), methodName, parameterTypes))))
                    case _ => addError(expression, """expected a string of type "className methodName [parameterTypes]" """)
                  }
                  case _ => addError(expression, "simple string expected")
                }
                None
              } else {
                symbol.attributes += (attributeSpec.identifier.text -> new UserDefinedAttributeSymbol(attributeSpec.identifier, expression.dataType, attributeSymbol, symbol, null))
                Some(true)
              }
            }
        } match {
          case Seq() => None
          case _ => Option((new ConstantSymbol(Identifier("$attribute" + context.varIndex), attributeSymbol.dataType, context.varIndex, owner)), expression)
        }
    } match {
      case Some((constantSymbol, expression)) => (ConstantDeclaration(Position.Empty, Seq(), null, Option(expression), Seq(constantSymbol)), context.copy(varIndex = context.varIndex + getNextIndex(constantSymbol.dataType)))
      case None => (attributeSpec, context)
    }

  def visitBlockStatement(blockStmt: BlockStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(blockStmt.label, blockStmt.endLabel)

    val generics = createSymbolsFromInterfaceList(context, blockStmt.genericInterfaceList, owner)
    val ports = createSymbolsFromInterfaceList(context, blockStmt.portInterfaceList, owner)
    val newContext = context.insertSymbols(generics).insertSymbols(ports)

    // TODO
    val genericAssociationList = checkAssociationList(newContext, blockStmt.genericAssociationList, generics, blockStmt)
    val portAssociationList = checkAssociationList(newContext, blockStmt.portAssociationList, ports, blockStmt)
    val guardExpression = checkExpressionOption(newContext, blockStmt.guardExpression, SymbolTable.booleanType)

    val cx = blockStmt.guardExpression.map(expr => newContext.insertSymbol(new SignalSymbol(Identifier(expr.position, "guard"), SymbolTable.booleanType, InterfaceList.InterfaceMode.LINKAGE, None, -1, owner))).getOrElse(newContext)
    val (declarativeItems, c) = acceptDeclarativeItems(blockStmt.declarativeItems, owner, insertConcurrentStatementsLabels(cx.copy(varIndex = ports.last.index + 1), blockStmt.statementList, owner))
    val (statementList, _) = acceptList(blockStmt.statementList, owner, c)

    (blockStmt.copy(genericAssociationList = genericAssociationList, portAssociationList = portAssociationList, guardExpression = guardExpression,
      declarativeItems = declarativeItems, statementList = statementList), context)
  }

  def visitCaseStatement(caseStmt: CaseStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(caseStmt.label, caseStmt.endLabel)
    val caseStmtExpression = checkExpression(context, caseStmt.expression, NoType)
    val containsOthers = caseStmt.alternatives.exists(when => when.choices.elements.exists(choice => choice.isOthers))
    val lastAlternative = caseStmt.alternatives.last

    val alternativesMapped = caseStmt.alternatives.map{
      when =>
        val choices = when.choices.elements.map{
          choice => choice.rangeOrExpression match {
            case Some(rangeOrExpression) => rangeOrExpression match {
              case Left(range) => error("not implemented")
              case Right(expression) => new Choices.Choice(position = choice.position, Some(Right(checkExpression(context, expression, caseStmtExpression.dataType))))
            }
            case None =>
              if (when ne lastAlternative) {
                addError(choice, SemanticMessage.OTHERS_CHOICE_NOT_LAST_ALTERNATIVE)
              }
              if (when.choices.elements.size > 1) {
                addError(choice, SemanticMessage.OTHERS_CHOICE_NOT_ALONE)
              }
              choice
          }
        }
        val (statements, _) = acceptList(when.statements, owner, context)
        new CaseStatement.When(choices = new Choices(choices), statements)
    }

    val alternatives = if (!containsOthers) {
      //add default error handling
      val caseError = new CaseStatement.When(new Choices(List(new Choices.Choice(Position.Empty, None))), List(ThrowStatement(lastAlternative.choices.elements.head.position.addLineOffset(1), "case fall through")))
      alternativesMapped :+ caseError
    } else {
      alternativesMapped
    }

    caseStmtExpression.dataType match {
      case _: EnumerationType | _: IntegerType =>
        val keys = alternatives.flatMap(when =>
          when.choices.elements.collect(_ match {
            case choice: Choices.Choice if (!choice.isOthers) => choice.rangeOrExpression.get match {
              case Right(expression) => StaticExpressionCalculator.calcValue(expression)(IntIsIntegral)
            }
          }))

        (caseStmt.copy(expression = caseStmtExpression, keys = keys, alternatives = alternatives), context)
      case _ =>
        def toExpr(choices: Choices): Expression = {
          val tmpList: Seq[Expression] = choices.elements.map{
            choice =>
              choice.rangeOrExpression match {
                case Some(rangeOrExpression) => rangeOrExpression match {
                  case Left(discreteRange) => error("not implemented")
                  case Right(expression) => Relation(choice.position, expression, Relation.Operator.EQ, caseStmtExpression)
                }
                case None => EmptyExpression
              }
          }
          return checkExpression(context, tmpList.reduceLeft((r1, r2) => LogicalExpression(r1.position, r1, LogicalExpression.Operator.OR, r2)), SymbolTable.booleanType)
        }
        val ifThenList = alternatives.init.map(alternative => new IfStatement.IfThenPart(toExpr(alternative.choices), alternative.statements))
        val ifStmt = IfStatement(caseStmt.position, None, ifThenList, Option(alternatives.last.statements), None)
        (ifStmt, context)
    }
  }

  def visitComponentDeclaration(componentDeclaration: ComponentDeclaration, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiers(componentDeclaration.identifier, componentDeclaration.endIdentifier)
    val component = new ComponentSymbol(componentDeclaration.identifier, null, null, owner)
    component.generics = createSymbolsFromInterfaceList(context, componentDeclaration.genericInterfaceList, component)
    component.ports = createSymbolsFromInterfaceList(context, componentDeclaration.portInterfaceList, component)
    (componentDeclaration.copy(symbol = component), context.insertSymbol(component))
  }

  def visitComponentInstantiationStatement(componentInstantiationStmt: ComponentInstantiationStatement, context: Context): ReturnType = {
    import ComponentInstantiationStatement.ComponentType._

    (componentInstantiationStmt.componentType match {
      case COMPONENT => context.findSymbol(componentInstantiationStmt.name, classOf[ComponentSymbol]).map(component => (component, component.generics, component.ports))
      case ENTITY => context.findSymbol(componentInstantiationStmt.name, classOf[EntitySymbol]).map(entity => (entity, entity.generics, entity.ports))
      case CONFIGURATION =>
        context.findSymbol(componentInstantiationStmt.name, classOf[ConfigurationSymbol])
        error("not implemented")
    }) match {
      case Some((symbol, generics, ports)) =>
        val genericAssociationList = checkAssociationList(context, componentInstantiationStmt.genericAssociationList, generics, componentInstantiationStmt)
        val portAssociationList = checkAssociationList(context, componentInstantiationStmt.portAssociationList, ports, componentInstantiationStmt)
        (componentInstantiationStmt.copy(genericAssociationList = genericAssociationList, portAssociationList = portAssociationList, symbol = symbol), context)
      case _ => (componentInstantiationStmt, context)
    }
  }

  def expressionToSensitivityList(expr: Expression): Seq[SignalSymbol] = Seq() //TODO

  def toProcessStatement(statement: SequentialStatement, sensitivityList: Seq[SignalSymbol], label: Option[Identifier], postponed: Boolean, owner: Symbol, context: Context): ReturnType = {
    val waitStatement = WaitStatement(statement.position, label = None, sensitivityList = None, untilCondition = None, forExpression = None, sensitivitySignalList = sensitivityList)
    visitProcessStatement(ProcessStatement(statement.position, label = label, postponed = postponed, sensitivityList = None, declarativeItems = Seq(),
      sequentialStatementList = Seq(statement, waitStatement), endLabel = None), owner, context)
  }

  def visitConcurrentAssertionStatement(concurrentAssertStmt: ConcurrentAssertionStatement, owner: Symbol, context: Context): ReturnType = {
    val assertStmt = AssertStatement(concurrentAssertStmt.position, None, concurrentAssertStmt.condition, concurrentAssertStmt.reportExpression, concurrentAssertStmt.severityExpression)
    toProcessStatement(assertStmt, expressionToSensitivityList(concurrentAssertStmt.condition), concurrentAssertStmt.label, concurrentAssertStmt.postponed, owner, context)
  }

  def visitConcurrentProcedureCallStatement(concurrentProcedureCallStmt: ConcurrentProcedureCallStatement, owner: Symbol, context: Context): ReturnType = {
    val procedureCallStmt = ProcedureCallStatement(None, concurrentProcedureCallStmt.procedureName, concurrentProcedureCallStmt.parameterAssociationList)
    val sensitivityList = concurrentProcedureCallStmt.parameterAssociationList.map{
      _.elements.flatMap{
        _.actualPart.map(expressionToSensitivityList(_)).toList.flatten
      }
    }.toList.flatten
    toProcessStatement(procedureCallStmt, sensitivityList, concurrentProcedureCallStmt.label, concurrentProcedureCallStmt.postponed, owner, context)
  }

  def visitConcurrentSignalAssignmentStatement(signalAssignStmt: ConcurrentSignalAssignmentStatement, owner: Symbol, context: Context): ReturnType = {
    require(!signalAssignStmt.guarded)

    def waveTransform(waveForm: Waveform): SequentialStatement =
      if (waveForm.isUnaffected) {
        NullStatement(waveForm.position, label = None)
      } else {
        SimpleSignalAssignmentStatement(waveForm.position, None, signalAssignStmt.target, signalAssignStmt.delayMechanism, waveForm)
      }

    val newStatement = signalAssignStmt match {
      case conditionalSignalAssignment: ConcurrentConditionalSignalAssignment => conditionalSignalAssignment.alternatives match {
        case Seq(x) if (x.condition == null) => waveTransform(x.waveForm)
        case _ =>
          val last = conditionalSignalAssignment.alternatives.last
          val mapper = (alternative: ConcurrentConditionalSignalAssignment.When) => new IfStatement.IfThenPart(alternative.condition, Seq(waveTransform(alternative.waveForm)))
          val (ifThenList, elseSequentialStatementList) = if (last.condition == null) {
            (conditionalSignalAssignment.alternatives.init.map(mapper), Some(Seq(waveTransform(last.waveForm))))
          }
          else {
            (conditionalSignalAssignment.alternatives.map(mapper), None)
          }
          IfStatement(conditionalSignalAssignment.position, label = None, ifThenList = ifThenList, elseSequentialStatementList = elseSequentialStatementList, endLabel = None)
      }
      case selectedSignalAssignment: ConcurrentSelectedSignalAssignment =>
        val caseStmtAlternatives = selectedSignalAssignment.alternatives.map(alternative => new CaseStatement.When(alternative.choices, Seq(waveTransform(alternative.waveForm))))
        CaseStatement(selectedSignalAssignment.position, label = None, expression = selectedSignalAssignment.expression, alternatives = caseStmtAlternatives, endLabel = None)
    }
    toProcessStatement(newStatement, Seq(), signalAssignStmt.label, signalAssignStmt.postponed, owner, context)
  }

  def visitConstantDeclaration(constantDeclaration: ConstantDeclaration, owner: Symbol, context: Context): ReturnType = {
    val dataType = createType(context, constantDeclaration.subType)
    checkIfNotFileProtectedAccessType(constantDeclaration.subType, dataType)

    if (constantDeclaration.defaultExpression.isEmpty && !owner.isInstanceOf[PackageSymbol])
      addError(constantDeclaration, SemanticMessage.DEFERRED_CONSTANT_NOT_ALLOWED)

    val defaultExpression = checkExpressionOption(context, constantDeclaration.defaultExpression, dataType)
    val multiplier = getNextIndex(dataType) //+2 for real and physical, +1 for all other constants
    val symbols = constantDeclaration.identifierList.zipWithIndex.map{
      case (identifier, i) => context.symbolTable.currentScope.get(identifier.text) match {
        case Some(x) if (x.isInstanceOf[ConstantSymbol] && !x.asInstanceOf[ConstantSymbol].isDefined) =>
          val symbol = x.asInstanceOf[ConstantSymbol]
          if (symbol.dataType != dataType) addError(constantDeclaration.subType, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, symbol.dataType.name, dataType.name)
          symbol.copy(isDefined = constantDeclaration.defaultExpression.isDefined)
        case _ =>
          new ConstantSymbol(identifier, dataType, context.varIndex + (i * multiplier), owner, false, constantDeclaration.defaultExpression.isDefined, constantDeclaration.defaultExpression.isEmpty)
      }
    }
    val newNode = constantDeclaration.copy(defaultExpression = defaultExpression, symbols = symbols)
    val newContext = context.insertSymbols(symbols).copy(varIndex = context.varIndex + (constantDeclaration.identifierList.size * multiplier) + 1)
    (newNode, newContext)
  }

  def visitDesignUnit(designUnit: DesignUnit, owner: Symbol, context: Context): ReturnType = {
    val libraryUnitOption = designUnit.libraryUnit.map{
      unit =>
        checkIdentifiers(unit.identifier, unit.endIdentifier)
        val workSymbol = new LibrarySymbol(Identifier("work"), new DirectoryLibraryArchive(configuration.libraryOutputDirectory))
        val librarySymbols = workSymbol +: ((if ("std" == configuration.designLibrary) designUnit.libraries else Identifier(designUnit.position, "std") +: designUnit.libraries).distinct.flatMap{
          id =>
            try
            {
              Some(new LibrarySymbol(id, new JarFileLibraryArchive(configuration.libraryDirectory + id.text + ".jar")))
            } catch {
              case e: IOException => addError(id, SemanticMessage.NOT_FOUND, "library", id)
            }
        })

        val newContext = if ("standard" == unit.identifier.text) {
          context
        } else {
          // set up universe
          val c = visitUseClause(UseClause(Position.Empty, Seq(new SelectedName(Seq(Identifier("std"), Identifier("standard"), Identifier("all"))))), context.openScope.insertSymbols(librarySymbols))._2
          def findType[A](dataType: String): A = c.symbolTable.scopes.last(dataType) match {
            case typeSymbol: TypeSymbol => typeSymbol.dataType.asInstanceOf[A]
            case subTypeSymbol: SubTypeSymbol => subTypeSymbol.dataType.asInstanceOf[A]
            case _ => error("evil internal compiler error")
          }
          SymbolTable.booleanType = findType("boolean")
          SymbolTable.bitType = findType("bit")
          SymbolTable.characterType = findType("character")
          SymbolTable.severityLevel = findType("severity_level")
          SymbolTable.integerType = findType("integer")
          SymbolTable.realType = findType("real")
          SymbolTable.timeType = findType("time")
          SymbolTable.naturalType = findType("natural")
          SymbolTable.positiveType = findType("positive")
          SymbolTable.stringType = findType("string")
          SymbolTable.bitVector = findType("bit_vector")
          SymbolTable.fileOpenKind = findType("file_open_kind")
          SymbolTable.fileOpenStatus = findType("file_open_status")
          SymbolTable.foreignAttribute = c.symbolTable.scopes.last("foreign").asInstanceOf[AttributeDeclarationSymbol]
          c
        }

        val (useClauses, c1) = acceptList(designUnit.useClauses, owner, newContext)
        val (node, c2) = acceptNode(unit, if (configuration.designLibrary == "work") workSymbol else new LibrarySymbol(Identifier(configuration.designLibrary), new DirectoryLibraryArchive(configuration.libraryOutputDirectory)), c1)
        val libraryUnit = node.asInstanceOf[LibraryUnit]
        import java.io.{File, ObjectOutputStream, FileOutputStream}
        /* TODO remove comment to add check if a design unit exists
          if (new File(configuration.outputDirectory + libraryUnit.symbol.implementationName + ".sym").exists) {
          addError(unit.identifier, "existing design unit with name: " + unit.identifier.text + " found")
        }else*/
        {
          val file = new java.io.File(configuration.outputDirectory + (libraryUnit.symbol.implementationName.lastIndexOf('/') match {
            case -1 => ""
            case i => libraryUnit.symbol.implementationName.substring(0, i)
          }))
          if (!file.canWrite) file.mkdirs
          val writer = new ObjectOutputStream(new FileOutputStream(configuration.outputDirectory + libraryUnit.symbol.implementationName + ".sym", false))
          writer.writeObject(libraryUnit.symbol)
        }
        librarySymbols.foreach(_.libraryArchive.close)
        libraryUnit
    }
    (designUnit.copy(libraryUnit = libraryUnitOption), context)
  }

  def visitDisconnectionSpecification(disconnectionSpec: DisconnectionSpecification, context: Context): ReturnType = {
    // TODO
    def checkSignalList(context: Context, signalList: Seq[SelectedName]): Seq[SignalSymbol] = signalList.flatMap(name => context.findSymbol(name, classOf[SignalSymbol]))

    val timeExpression = checkExpression(context, disconnectionSpec.timeExpression, SymbolTable.timeType)
    disconnectionSpec.signalListOrIdentifier match {
      case Left(signalList) => checkSignalList(context, signalList)
      case Right(identifier) => error("not implemented") //identifier is ALL or OTHERS
    }
    (disconnectionSpec.copy(timeExpression = timeExpression), context)
  }

  def visitEntityDeclaration(entityDeclaration: EntityDeclaration, owner: Symbol, context: Context): ReturnType = {
    val entity = new EntitySymbol(entityDeclaration.identifier, null, null, owner)
    entity.generics = createSymbolsFromInterfaceList(context, entityDeclaration.genericInterfaceList, entity)
    entity.ports = createSymbolsFromInterfaceList(context, entityDeclaration.portInterfaceList, entity)
    val (declarativeItems, c) = acceptDeclarativeItems(entityDeclaration.declarativeItems, entity,
      insertConcurrentStatementsLabels(context.openScope.insertSymbols(entity +: (entity.generics ++ entity.ports)), entityDeclaration.concurrentStatements, entity))
    require(c.symbolTable.depth == 2)
    val (concurrentStatements, c2) = acceptList(entityDeclaration.concurrentStatements, entity, c)
    require(c2.symbolTable.depth == 2)
    for (node <- concurrentStatements) {
      val process = node.asInstanceOf[ProcessStatement]
      if (!process.symbol.isPassive) addError(process, SemanticMessage.STATEMENT_NOT_PASSIVE)
    }
    c2.symbolTable.writeToFile(configuration.libraryOutputDirectory + entityDeclaration.identifier.text)
    (entityDeclaration.copy(declarativeItems = declarativeItems, concurrentStatements = concurrentStatements, symbol = entity), context)
  }

  def visitExitStatement(exitStmt: ExitStatement, context: Context): ReturnType = {
    val condition = checkExpressionOption(context, exitStmt.condition, SymbolTable.booleanType)
    val loopStatement = checkLoopLabel(context, exitStmt.loopLabel, exitStmt, "exit")
    (exitStmt.copy(condition = condition, loopStatement = loopStatement), context)
  }

  def visitFileDeclaration(fileDeclaration: FileDeclaration, owner: Symbol, context: Context): ReturnType = {
    val fileOpenKindExpression = checkExpressionOption(context, fileDeclaration.fileOpenKindExpression, SymbolTable.fileOpenKind)
    val fileLogicalName = checkExpressionOption(context, fileDeclaration.fileLogicalName, SymbolTable.stringType)
    val dataType = createType(context, fileDeclaration.subType)
    if (!dataType.isInstanceOf[FileType]) addError(fileDeclaration.subType, SemanticMessage.EXPECTED_TYPE, "file")
    val symbols = fileDeclaration.identifierList.zipWithIndex.map{
      case (identifier, i) => new FileSymbol(identifier, dataType, context.varIndex + i, owner)
    }
    val newNode = fileDeclaration.copy(fileOpenKindExpression = fileOpenKindExpression, fileLogicalName = fileLogicalName, symbols = symbols)
    val newContext = context.insertSymbols(symbols).copy(varIndex = context.varIndex + fileDeclaration.identifierList.size + 1)
    (newNode, newContext)
  }

  def visitForGenerateStatement(forGenerateStmt: ForGenerateStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(forGenerateStmt.label, forGenerateStmt.endLabel)
    val discreteRange = checkDiscreteRange(context, forGenerateStmt.discreteRange, false)
    val symbol = new ConstantSymbol(forGenerateStmt.loopIdentifier, discreteRange.dataType.elementType, -1, owner)
    val (declarativeItems, c1) = acceptDeclarativeItems(forGenerateStmt.declarativeItems, owner, insertConcurrentStatementsLabels(context.openScope, forGenerateStmt.statementList, owner).insertSymbol(symbol))
    val (statementList, _) = acceptList(forGenerateStmt.statementList, owner, c1)
    (forGenerateStmt.copy(declarativeItems = declarativeItems, statementList = statementList), context)
  }

  def visitForStatement(forStmt: ForStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(forStmt.label, forStmt.endLabel)
    val discreteRange = checkDiscreteRange(context, forStmt.discreteRange, false)
    val symbol = new ConstantSymbol(forStmt.identifier, discreteRange.dataType.elementType, context.varIndex + 1, owner)
    val (sequentialStatementList, _) = acceptList(forStmt.sequentialStatementList, owner, context.openScope.insertSymbol(symbol).insertLoopLabel(forStmt.label, forStmt.position).copy(varIndex = context.varIndex + 1))
    (forStmt.copy(sequentialStatementList = sequentialStatementList, symbol = symbol, discreteRange = discreteRange), context)
  }

  def visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, owner: Symbol, context: Context): ReturnType = {
    val returnType = context.findType(functionDeclaration.returnType)
    val function = new FunctionSymbol(getMangledName(functionDeclaration.identifier), null, returnType, owner, functionDeclaration.pure, false)
    function.parameters = createSymbolsFromInterfaceList(context, functionDeclaration.parameterInterfaceList, owner)
    (functionDeclaration.copy(symbol = function), context.insertSymbol(function))
  }

  def visitFunctionDefinition(functionDefinition: FunctionDefinition, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiers(functionDefinition.identifier, functionDefinition.endIdentifier)
    val returnType = context.findType(functionDefinition.returnType)
    val function = new FunctionSymbol(getMangledName(functionDefinition.identifier), null, returnType, owner, functionDefinition.pure, true)
    function.parameters = createSymbolsFromInterfaceList(context, functionDefinition.parameterInterfaceList, function)

    val name = functionDefinition.identifier.text
    if (name == "+" || name == "-") {
      if (function.parameters.size != 2 && function.parameters.size != 1) addError(functionDefinition.identifier, "overloaded operator " + name + " must be a unary or binary operator")
    }
    else if (name == "abs" || name == "not") {
      if (function.parameters.size != 1) addError(functionDefinition.identifier, "overloaded unary operator " + name + " must have a single parameter")
    }
    else if (functionDefinition.identifier.originalText(0) == '"' && function.parameters.size != 2) addError(functionDefinition.identifier, "overloaded binary operator " + name + " must have two parameters")

    if (owner.isInstanceOf[TypeSymbol]) {
      //this is a method in a protected Type
      function.parameters.foreach(p => checkIfNotFileAccessType(p.identifier, p.dataType))
      checkIfNotFileAccessType(functionDefinition.returnType, returnType)
    }
    val tmp = context.insertSymbol(function)
    val (declarativeItems, c1) = acceptDeclarativeItems(functionDefinition.declarativeItems, function,
      insertSequentialStatementsLabels(tmp.openScope.insertSymbols(function.parameters), functionDefinition.sequentialStatementList, function).copy(varIndex = function.parameters.lastOption.map(_.index + 1).getOrElse(getStartIndex(owner))))
    val (sequentialStatementList, c2) = acceptList(functionDefinition.sequentialStatementList, function, c1)
    val newSequentialStatementList = sequentialStatementList match {
      case Seq(AssertStatement(pos, _, _, _, _)) =>
      //corner case for functions with only a assert statement, throw statement will make the JVM verifier happy, that this function does not return a value
        sequentialStatementList :+ ThrowStatement(pos, "function fall through")
      case _ => sequentialStatementList
    }
    val localSymbols = c2.closeScope()
    (functionDefinition.copy(declarativeItems = declarativeItems, sequentialStatementList = newSequentialStatementList,
      symbol = function, localSymbols = localSymbols), tmp)
  }

  def visitGroupDeclaration(groupDeclaration: GroupDeclaration, owner: Symbol, context: Context): ReturnType = {
    val symbol = new GroupSymbol(groupDeclaration.identifier, owner)
    context.findSymbol(groupDeclaration.groupTemplateName, classOf[GroupTemplateSymbol]) match {
      case _ => error("not implemented") //TODO
    }
    (groupDeclaration, context.insertSymbol(symbol))
  }

  def visitGroupTemplateDeclaration(groupTemplateDeclaration: GroupTemplateDeclaration, owner: Symbol, context: Context): ReturnType = {
    val lastElement = groupTemplateDeclaration.elements.last
    val items = groupTemplateDeclaration.elements.init.map{
      element =>
        if (element.box) addError(groupTemplateDeclaration, SemanticMessage.NOT_ALLOWED, "infinite elements, it its only allowed at the last element")
        element.entityClass
    }
    val groupTemplateSymbol = new GroupTemplateSymbol(groupTemplateDeclaration.identifier, items :+ lastElement.entityClass, lastElement.box, owner)
    (groupTemplateDeclaration, context.insertSymbol(groupTemplateSymbol))
  }

  def visitIfGenerateStatement(ifGenerateStmt: IfGenerateStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(ifGenerateStmt.label, ifGenerateStmt.endLabel)
    val condition = checkExpression(context, ifGenerateStmt.condition, SymbolTable.booleanType)
    //TODO ifGenerateStmt.value=StaticExpressionCalculator.calcBooleanValue(condition)
    val (declarativeItems, c1) = acceptDeclarativeItems(ifGenerateStmt.declarativeItems, owner, insertConcurrentStatementsLabels(context, ifGenerateStmt.statementList, owner))
    val (statementList, _) = acceptList(ifGenerateStmt.statementList, owner, c1)
    (ifGenerateStmt.copy(declarativeItems = declarativeItems, statementList = statementList), context)
  }

  def visitIfStatement(ifStmt: IfStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(ifStmt.label, ifStmt.endLabel)
    val ifThenList = ifStmt.ifThenList.map(ifThen => new IfStatement.IfThenPart(checkExpression(context, ifThen.condition, SymbolTable.booleanType), acceptList(ifThen.statements, owner, context)._1))
    val (elseSequentialStatementList, _) = acceptListOption(ifStmt.elseSequentialStatementList, owner, context)
    (ifStmt.copy(ifThenList = ifThenList, elseSequentialStatementList = elseSequentialStatementList), context)
  }

  def visitLoopStatement(loopStmt: LoopStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(loopStmt.label, loopStmt.endLabel)
    val (sequentialStatementList, _) = acceptList(loopStmt.sequentialStatementList, owner, context.insertLoopLabel(loopStmt.label, loopStmt.position))
    (loopStmt.copy(sequentialStatementList = sequentialStatementList), context)
  }

  def visitNextStatement(nextStmt: NextStatement, context: Context): ReturnType = {
    val condition = checkExpressionOption(context, nextStmt.condition, SymbolTable.booleanType)
    val loopStatement = checkLoopLabel(context, nextStmt.loopLabel, nextStmt, "next")
    (nextStmt.copy(condition = condition, loopStatement = loopStatement), context)
  }

  def visitPackageBodyDeclaration(packageBodyDeclaration: PackageBodyDeclaration, owner: Symbol, context: Context): ReturnType = {
    val packageName = packageBodyDeclaration.identifier
    val symbol = new PackageSymbol(packageName, Map(), true, owner)
    val newSymbolTable = try
    {
      val scopes = SymbolTable.getScopesFromInputStream(new FileInputStream(configuration.libraryOutputDirectory + packageName + ".sym"))
      //require(scopes.size == 2)
      new SymbolTable(scopes ++ context.symbolTable.scopes)
    } catch {
      case e: IOException =>
        addError(packageBodyDeclaration, SemanticMessage.NOT_FOUND, "package", packageName)
        context.symbolTable
    }
    val (declarativeItems, c1) = acceptDeclarativeItems(packageBodyDeclaration.declarativeItems, symbol, context.copy(symbolTable = newSymbolTable))
    require(c1.symbolTable.depth <= 3)
    c1.closeScope().collect{
      _ match {
        case c: ConstantSymbol if (!c.isDefined) => c
      }
    }.foreach(symbol => addError(packageBodyDeclaration, SemanticMessage.DEFERRED_CONSTANT_NOT_DECLARED, symbol.name))
    (packageBodyDeclaration.copy(declarativeItems = declarativeItems, symbol = symbol), context)
  }

  def visitPackageDeclaration(packageDeclaration: PackageDeclaration, owner: Symbol, context: Context): ReturnType = {
    val packageSymbol = new PackageSymbol(packageDeclaration.identifier, Map(), false, owner)
    val (declarativeItems, newContext) = acceptDeclarativeItems(packageDeclaration.declarativeItems, packageSymbol, context.openScope.insertSymbol(packageSymbol))
    require(newContext.symbolTable.depth <= 2)
    packageSymbol.localSymbols = newContext.closeScope.map(symbol => (symbol.identifier.text, symbol)).toMap
    newContext.symbolTable.writeToFile(configuration.libraryOutputDirectory + packageDeclaration.identifier.text)
    (packageDeclaration.copy(declarativeItems = declarativeItems, symbol = packageSymbol), context)
  }

  def visitProcedureCallStatement(procedureCallStmt: ProcedureCallStatement, context: Context): ReturnType = {
    val name = procedureCallStmt.procedureName
    context.findSymbol(name, classOf[ListOfSubprograms]) match {
      case Some(subprograms) =>
        val parameterAssociationList = checkAssociationList(context, procedureCallStmt.parameterAssociationList, subprograms.subprograms.head.parameters, procedureCallStmt) //TODO
        (procedureCallStmt.copy(parameterAssociationList = parameterAssociationList, symbol = subprograms.subprograms.head.asInstanceOf[ProcedureSymbol]), context)
      case None => (procedureCallStmt, context)
    }
  }

  def visitProcedureDeclaration(procedureDeclaration: ProcedureDeclaration, owner: Symbol, context: Context): ReturnType = {
    val procedure = new ProcedureSymbol(procedureDeclaration.identifier, null, owner, false, false)
    procedure.parameters = createSymbolsFromInterfaceList(context, procedureDeclaration.parameterInterfaceList, procedure)
    (procedureDeclaration.copy(symbol = procedure), context.insertSymbol(procedure))
  }

  def visitProcedureDefinition(procedureDefinition: ProcedureDefinition, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiers(procedureDefinition.identifier, procedureDefinition.endIdentifier)
    val procedure = new ProcedureSymbol(procedureDefinition.identifier, null, owner, true, true)
    procedure.parameters = createSymbolsFromInterfaceList(context, procedureDefinition.parameterInterfaceList, procedure)
    if (owner.isInstanceOf[TypeSymbol]) {
      //this is a method in a protected Type
      procedure.parameters.foreach(p => checkIfNotFileAccessType(p.identifier, p.dataType))
    }
    val tmp = context.insertSymbol(procedure)
    val (declarativeItems, c1) = acceptDeclarativeItems(procedureDefinition.declarativeItems, procedure,
      insertSequentialStatementsLabels(tmp.openScope.insertSymbols(procedure.parameters), procedureDefinition.sequentialStatementList, procedure).copy(varIndex = procedure.parameters.lastOption.map(_.index + 1).getOrElse(getStartIndex(owner))))
    val (sequentialStatementList, c2) = acceptList(procedureDefinition.sequentialStatementList, procedure, c1)
    procedure.isPassive = isPassive(sequentialStatementList)
    val localSymbols = c2.closeScope()
    (procedureDefinition.copy(declarativeItems = declarativeItems, sequentialStatementList = sequentialStatementList,
      symbol = procedure, localSymbols = localSymbols), tmp)
  }

  def isPassive(list: Seq[SequentialStatement]): Boolean = list.forall{
    _ match {
      case loopStmt: AbstractLoopStatement => isPassive(loopStmt.sequentialStatementList)
      case caseStmt: CaseStatement => caseStmt.alternatives.forall(alternative => isPassive(alternative.statements))
      case ifStmt: IfStatement => ifStmt.ifThenList.forall(ifThen => isPassive(ifThen.statements)) && ifStmt.elseSequentialStatementList.map(isPassive).getOrElse(true)
      case _: SignalAssignmentStatement => false
      case procedureCallStmt: ProcedureCallStatement => if (procedureCallStmt.symbol != null) procedureCallStmt.symbol.isPassive else true
      case _ => true
    }
  }

  @tailrec
  def toLinearList(sequentialStatementList: Seq[SequentialStatement], buffer: Buffer[SequentialStatement] = new Buffer[SequentialStatement]()): Seq[SequentialStatement] =
    sequentialStatementList match {
      case Seq() => buffer.toList
      case Seq(statement, xs@_*) => statement match {
        case loopStmt: AbstractLoopStatement =>
          buffer += loopStmt
          toLinearList(loopStmt.sequentialStatementList, buffer)
        case caseStmt: CaseStatement =>
          buffer += caseStmt
          buffer.appendAll(caseStmt.alternatives.flatMap(alternative => toLinearList(alternative.statements)))
        case ifStmt: IfStatement =>
          buffer += ifStmt
          buffer.appendAll(ifStmt.ifThenList.flatMap(ifThen => toLinearList(ifThen.statements)))
          buffer.appendAll(ifStmt.elseSequentialStatementList.map(toLinearList(_)).flatten)
        case statement => buffer += statement
      }
      toLinearList(xs, buffer)
    }

  def visitProcessStatement(processStatement: ProcessStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(processStatement.label, processStatement.endLabel)

    val name = processStatement.label.getOrElse(Identifier(processStatement.position, "process_" + processStatement.position.line))
    val symbol = new ProcessSymbol(name, owner, false)

    val (declarativeItems, newContext) = acceptDeclarativeItems(processStatement.declarativeItems, symbol, insertSequentialStatementsLabels(context.openScope, processStatement.sequentialStatementList, symbol))
    val (newSequentialStatementList, c2) = acceptList(processStatement.sequentialStatementList, symbol, newContext)

    val sequentialStatementList = newSequentialStatementList ++ processStatement.sensitivityList.map{
      sensitivityList =>
        toLinearList(newSequentialStatementList).foreach{
          _ match {
            case waitStatement: WaitStatement => addError(waitStatement, SemanticMessage.PROCESS_WITH_SENSITIVITY_LIST_AND_WAIT)
            case procedureCallStatement: ProcedureCallStatement =>
              if (procedureCallStatement.symbol != null && !procedureCallStatement.symbol.isPassive)
                addError(procedureCallStatement, SemanticMessage.PROCESS_WITH_SENSITIVITY_LIST_AND_PROCEDURE_CALL)
            case _ =>
          }
        }
        visitWaitStatement(WaitStatement(position = newSequentialStatementList.last.position.addLineOffset(1), label = None, sensitivityList = Some(sensitivityList), untilCondition = None, forExpression = None), context)._1.asInstanceOf[WaitStatement]
    }.toList

    symbol.isPassive = isPassive(newSequentialStatementList)
    val localSymbols = c2.closeScope()
    (processStatement.copy(declarativeItems = declarativeItems, sequentialStatementList = sequentialStatementList, localSymbols = localSymbols, symbol = symbol), context)
  }

  def visitReportStatement(reportStmt: ReportStatement, context: Context): ReturnType = {
    val reportExpression = checkExpression(context, reportStmt.reportExpression, SymbolTable.stringType)
    val severityExpression = checkExpressionOption(context, reportStmt.severityExpression, SymbolTable.severityLevel)
    (reportStmt.copy(reportExpression = reportExpression, severityExpression = severityExpression), context)
  }

  def visitReturnStatement(returnStmt: ReturnStatement, owner: Symbol, context: Context): ReturnType = {
    val expression = owner match {
      case _: ProcessSymbol => addError(returnStmt, SemanticMessage.RETURN_STMT_IN_PROCESS)
      case functionSymbol: FunctionSymbol => returnStmt.expression match {
        case None => addError(returnStmt, SemanticMessage.FUNCTION_RETURN_WITHOUT_EXPRESSION)
        case Some(_) => checkExpressionOption(context, returnStmt.expression, functionSymbol.returnType)
      }
      case procedureSymbol: ProcedureSymbol => returnStmt.expression.flatMap(expression => addError(expression, SemanticMessage.PROCEDURE_RETURN_VALUE))
      case _ => addError(returnStmt, "return statement is only in functions and procedures allowed")
    }
    (returnStmt.copy(expression = expression), context)
  }

  def visitSignalAssignmentStatement(signalAssignStmt: SignalAssignmentStatement, owner: Symbol, context: Context): ReturnType = {
    def checkWaveform(waveForm: Waveform, dataType: DataType): Waveform = {
      val elements = waveForm.elements.map{
        element =>
          val valueExpression = checkExpression(context, element.valueExpression, dataType)
          val timeExpression = checkExpressionOption(context, element.timeExpression, SymbolTable.timeType)
          new Waveform.Element(valueExpression = valueExpression, timeExpression = timeExpression)
      }
      new Waveform(waveForm.position, elements)
    }

    def checkDelayMechanism(delayMechanismOption: Option[DelayMechanism]): Option[DelayMechanism] =
      delayMechanismOption.map{
        delayMechanism =>
          val rejectExpression = checkExpressionOption(context, delayMechanism.rejectExpression, SymbolTable.timeType)
          new DelayMechanism(delayMechanism.delayType, rejectExpression = rejectExpression)
      }

    signalAssignStmt match {
      case stmt: SimpleSignalAssignmentStatement =>
        stmt.target.nameOrAggregate match {
          case Left(name) =>
            val nameExpression = acceptExpression(name, NoType, context)
            nameExpression match {
              case w: WithSymbol if (w.symbol.isInstanceOf[SignalSymbol]) =>
                val signalSymbol = w.symbol.asInstanceOf[SignalSymbol]
                if (signalSymbol.modifier == InterfaceList.InterfaceMode.IN) addError(signalAssignStmt, SemanticMessage.ASSIGN_READ_ONLY, "signal", signalSymbol.name)
                owner match {
                  case processSymbol: ProcessSymbol =>
                    if (signalSymbol.isUnresolved && signalSymbol.driver != null
                      && (signalSymbol.driver ne processSymbol)) {
                      addError(signalAssignStmt, SemanticMessage.RESOLVED_DUPLICATE_SIGNAL_ASSIGNMENT, signalSymbol.name, signalSymbol.driver.position.line.toString)
                    } else {
                      signalSymbol.driver = processSymbol
                    }
                  case _ =>
                }
                signalSymbol.used = true
                checkPure(context, signalAssignStmt, owner, signalSymbol)
                val delayMechanism = checkDelayMechanism(stmt.delayMechanism)
                val waveForm = checkWaveform(stmt.waveForm, signalSymbol.dataType)
                (stmt.copy(waveForm = waveForm, delayMechanism = delayMechanism), context)
              case _ =>
                addError(name.identifier, SemanticMessage.NOT_A, name.identifier.text, "signal")
                (stmt, context)
            }
          case Right(aggregate) => error("not implemented") // TODO stmt.target.aggregate
        }
    }
  }

  def visitSignalDeclaration(signalDeclaration: SignalDeclaration, owner: Symbol, context: Context): ReturnType = {
    val dataType = createType(context, signalDeclaration.subType)
    checkIfNotFileProtectedAccessType(signalDeclaration.subType, dataType)
    val defaultExpression = checkExpressionOption(context, signalDeclaration.defaultExpression, dataType)
    val symbols = signalDeclaration.identifierList.zipWithIndex.map{
      case (identifier, i) => new SignalSymbol(identifier, dataType, InterfaceList.InterfaceMode.LINKAGE, signalDeclaration.signalType, context.varIndex + i, owner)
    }
    val newNode = signalDeclaration.copy(defaultExpression = defaultExpression, symbols = symbols)
    val newContext = context.insertSymbols(symbols).copy(varIndex = context.varIndex + signalDeclaration.identifierList.size + 1)
    (newNode, newContext)
  }

  def checkIfNotFileProtectedAccessType(location: Locatable, dataType: DataType): Unit = dataType match {
    case _: FileType => addError(location, SemanticMessage.INVALID_TYPE, "file")
    case _: ProtectedType => addError(location, SemanticMessage.INVALID_TYPE, "protected")
    case _: AccessType => addError(location, SemanticMessage.INVALID_TYPE, "access")
    case recordType: RecordType => recordType.elementList.foreach(element => checkIfNotFileProtectedAccessType(location, element._2))
    case arrayType: ArrayType => checkIfNotFileProtectedAccessType(location, arrayType.elementType)
    case _ =>
  }

  def checkIfNotFileProtectedType(location: Locatable, dataType: DataType): Unit = dataType match {
    case _: FileType => addError(location, SemanticMessage.INVALID_TYPE, "file")
    case _: ProtectedType => addError(location, SemanticMessage.INVALID_TYPE, "protected")
    case recordType: RecordType => recordType.elementList.foreach(element => checkIfNotFileProtectedType(location, element._2))
    case arrayType: ArrayType => checkIfNotFileProtectedType(location, arrayType.elementType)
    case _ =>
  }

  def checkIfNotFileAccessType(location: Locatable, dataType: DataType): Unit = dataType match {
    case _: FileType => addError(location, SemanticMessage.INVALID_TYPE, "file")
    case _: AccessType => addError(location, SemanticMessage.INVALID_TYPE, "access")
    case recordType: RecordType => recordType.elementList.foreach(element => checkIfNotFileAccessType(location, element._2))
    case arrayType: ArrayType => checkIfNotFileAccessType(location, arrayType.elementType)
    case _ =>
  }

  def visitTypeDeclaration(typeDeclaration: AbstractTypeDeclaration, owner: Symbol, context: Context): ReturnType = {
    def checkDuplicateIdentifiers(identifiers: Seq[Identifier], message: String) = identifiers.diff(identifiers.distinct).foreach(identifier => addError(identifier, message, identifier))

    val name = typeDeclaration.identifier.text

    val (newTypeDeclaration, newSymbols) = typeDeclaration match {
      case enumerationType: EnumerationTypeDefinition =>
        val elements = enumerationType.elements.map(id => (id.text.replace("'", "")))
        require(enumerationType.elements.size <= Char.MaxValue)
        checkDuplicateIdentifiers(enumerationType.elements, SemanticMessage.DUPLICATE_ENUMERATION_VALUE)
        val dataType = new EnumerationType(name, elements, None, owner)
        val symbols = enumerationType.elements.map(id => new EnumerationSymbol(id, dataType, owner)) //TODO is owner the new created TypeSymbol?
        (enumerationType.copy(dataType = dataType), symbols)
      case physicalType: PhysicalTypeDefinition =>
        checkIdentifiers(physicalType.identifier, physicalType.endIdentifier)
        checkDuplicateIdentifiers(physicalType.elements.map(_.identifier), SemanticMessage.DUPLICATE_PHYSICAL_UNIT)
        val range = checkRange(context, physicalType.range, true)
        range.expressionsOrName match {
          case Left((fromExpression, _, toExpression)) =>
            if (!fromExpression.dataType.isInstanceOf[IntegerType] && fromExpression.dataType != null) addError(fromExpression, SemanticMessage.EXPECTED_INTEGER_EXPRESSION)
            if (!toExpression.dataType.isInstanceOf[IntegerType] && toExpression.dataType != null) addError(toExpression, SemanticMessage.EXPECTED_INTEGER_EXPRESSION)
          case Right(attributeName) => error("not implemented")
        }
        @tailrec
        def buildUnitsMap(units: Seq[PhysicalTypeDefinition.Element], unitsMap: Map[String, Long]): Map[String, Long] = units match {
          case Seq() => unitsMap
          case Seq(unitDef, xs@_*) =>
            require(unitDef.literal.unitName.identifiers.size == 1)
            val unit = unitDef.literal.unitName.identifiers.head.text
            val value = if (unitDef.literal.literalType != Literal.Type.INTEGER_LITERAL) addError(unitDef.literal, SemanticMessage.EXPECTED_LITERAL_OF_TYPE, "integer")
            else if (!unitsMap.contains(unit)) addError(unitDef.literal.unitName, SemanticMessage.NOT_FOUND, "unit", unit)
            else Some(unitsMap(unit) * unitDef.literal.toLong)
            buildUnitsMap(xs, unitsMap + (unitDef.identifier.text -> value.getOrElse(0)))
        }

        val phyType = new PhysicalType(name, lowLong, highLong, buildUnitsMap(physicalType.elements, Map(physicalType.baseIdentifier.text -> 1)))
        val symbols = new UnitSymbol(physicalType.baseIdentifier, phyType, owner) +: physicalType.elements.map(element => new UnitSymbol(element.identifier, phyType, owner))
        (physicalType.copy(dataType = phyType), symbols)
      case integerOrRealType: IntegerOrFloatingPointTypeDefinition =>
        val range = checkRange(context, integerOrRealType.range, true)
        val dataType = range.dataType match {
          case _: IntegerType => new IntegerType(name, lowLong.toInt, highLong.toInt, None)
          case _: RealType => new RealType(name, lowDouble, highDouble, None)
          case otherType =>
            addError(range, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, "integer or real", otherType.name)
            NoType
        }
        (integerOrRealType.copy(dataType = dataType), Seq())
      case arrayType: AbstractArrayTypeDefinition =>
        val elementDataType = createType(context, arrayType.subType)
        checkIfNotFileProtectedType(arrayType.subType, elementDataType)
        arrayType match {
          case unconstrainedArrayTypeDefinition: UnconstrainedArrayTypeDefinition =>
            val dataType = new UnconstrainedArrayType(name, elementDataType, unconstrainedArrayTypeDefinition.dimensions.map{
              typeName =>
                val dataType = context.findType(typeName)
                if (!dataType.isInstanceOf[DiscreteType]) addError(typeName, SemanticMessage.EXPECTED_TYPE, "discrete")
                UnconstrainedRangeType(dataType)
            })
            (unconstrainedArrayTypeDefinition.copy(dataType = dataType), Seq())
          case constraintArray: ConstrainedArrayTypeDefinition =>
            val dataType = new ConstrainedArrayType(name, elementDataType, constraintArray.dimensions.map(checkDiscreteRange(context, _, true).dataType))
            (constraintArray.copy(dataType = dataType), Seq())
        }
      case recordType: RecordTypeDefinition =>
        checkIdentifiers(recordType.identifier, recordType.endIdentifier)
        checkDuplicateIdentifiers(recordType.elements.flatMap(_.identifierList), SemanticMessage.DUPLICATE_RECORD_FIELD)
        val elements = recordType.elements.flatMap{
          element =>
            val dataType = createType(context, element.subType)
            checkIfNotFileProtectedType(element.subType, dataType)
            element.identifierList.map(id => id.text -> dataType)
        }
        (recordType.copy(dataType = new RecordType(name, elements, owner)), Seq())
      case accessType: AccessTypeDefinition =>
        val dataType = createType(context, accessType.subType, isAccessTypeDefinition = true)
        checkIfNotFileProtectedType(accessType.subType, dataType)
        val deallocateSymbol = if (dataType != IncompleteType)
          new Some(ProcedureSymbol(Identifier("deallocate"), Seq(VariableSymbol(Identifier("p"), new AccessType(name, dataType), InterfaceList.InterfaceMode.INOUT, 0, null)), Runtime, true))
        else None
        (accessType.copy(dataType = new AccessType(name, dataType)), deallocateSymbol.toList)
      case fileTypeDefinition: FileTypeDefinition =>
        val dataType = context.findType(fileTypeDefinition.typeName)
        checkIfNotFileProtectedAccessType(fileTypeDefinition.typeName, dataType)
        if (dataType.isInstanceOf[ArrayType] && dataType.asInstanceOf[ArrayType].dimensions.size != 1)
          addError(fileTypeDefinition.typeName, SemanticMessage.INVALID_TYPE, "multidimension array")
        val fileType = new FileType(name, dataType)

        import InterfaceList.InterfaceMode._
        val pos = fileTypeDefinition.identifier.position
        val fileSymbol = FileSymbol(Identifier("f"), fileType, 0, null)
        val file_open1 = new ProcedureSymbol(Identifier(pos, "file_open"),
          Seq(fileSymbol, ConstantSymbol(Identifier("external_name"), SymbolTable.stringType, 0, null), ConstantSymbol(Identifier("open_kind"), SymbolTable.fileOpenKind, 0, null, isOptional = true)),
          Runtime, true)

        val file_open2 = new ProcedureSymbol(Identifier(pos, "file_open"),
          Seq(VariableSymbol(Identifier("status"), SymbolTable.fileOpenStatus, OUT, 0, null, isOptional = true), fileSymbol, ConstantSymbol(Identifier("external_name"), SymbolTable.stringType, 0, null),
            ConstantSymbol(Identifier("open_kind"), SymbolTable.fileOpenKind, 0, null, isOptional = true)), Runtime, true)

        val file_close = new ProcedureSymbol(Identifier(pos, "file_close"), Seq(fileSymbol), Runtime, true)
        val read = if (dataType.isInstanceOf[ArrayType])
          new ProcedureSymbol(Identifier(pos, "read"), Seq(fileSymbol, VariableSymbol(Identifier("value"), dataType, OUT, 0, null), VariableSymbol(Identifier("length"), SymbolTable.naturalType, OUT, 0, null)), Runtime, true)
        else new ProcedureSymbol(Identifier(pos, "read"), Seq(fileSymbol, VariableSymbol(Identifier("value"), dataType, OUT, 0, null)), Runtime, true)
        val write = new ProcedureSymbol(Identifier(pos, "write"), Seq(fileSymbol, ConstantSymbol(Identifier("value"), dataType, 0, null)), Runtime, true)
        val endfile = new FunctionSymbol(Identifier(pos, "endfile"), Seq(fileSymbol), SymbolTable.booleanType, Runtime, true)

        (fileTypeDefinition.copy(dataType = fileType), Seq(file_open1, file_open2, file_close, read, write, endfile))
      case protectedType: ProtectedTypeDeclaration =>
        checkIdentifiers(protectedType.identifier, protectedType.endIdentifier)
        val (declarativeItems, c) = acceptDeclarativeItems(protectedType.declarativeItems, owner, context.openScope)
        val dataType = new ProtectedType(name, c.symbolTable.currentScope.values.flatMap{
          _ match {
            case ListOfSubprograms(_, subprograms) => subprograms.filter(_.owner.isInstanceOf[TypeSymbol])
            case _ => Seq()
          }
        }.toSeq, owner, false)
        (protectedType.copy(declarativeItems = declarativeItems, dataType = dataType), Seq())
      case protectedTypeBody: ProtectedTypeBodyDeclaration =>
        if (owner.isInstanceOf[PackageSymbol] && !owner.asInstanceOf[PackageSymbol].isBody) addError(protectedTypeBody, SemanticMessage.PROTECTED_TYPE_BODY_IN_PACKAGE)
        checkIdentifiers(protectedTypeBody.identifier, protectedTypeBody.endIdentifier)

        val (declarativeItems, protectedType) = context.findSymbol(protectedTypeBody.identifier, classOf[TypeSymbol]) match {
          case None => (protectedTypeBody.declarativeItems, null)
          case Some(typeSymbol) => typeSymbol.dataType match {
            case protectedType: ProtectedType => (acceptDeclarativeItems(protectedTypeBody.declarativeItems, typeSymbol, context.openScope.insertSymbols(protectedType.subprograms))._1, protectedType.copy(implemented = true))
            case _ =>
              addError(protectedTypeBody.identifier, SemanticMessage.NOT_A, protectedTypeBody.identifier, "protected type")
              (protectedTypeBody.declarativeItems, null)
          }
        }
        (protectedTypeBody.copy(declarativeItems = declarativeItems, dataType = protectedType), Seq())
      case typeDef: IncompleteTypeDeclaration => (typeDef.copy(dataType = IncompleteType), Seq())
    }
    context.symbolTable.findInCurrentScope(typeDeclaration.identifier.text, classOf[TypeSymbol]) match {
      case Some(symbol) if (symbol.dataType == IncompleteType) =>
      //we found an incomplete type declaration (TypeSymbol where dataType == IncompleteType), now we must update all access types where this incomplete type is used, so that the point to the new defined type
        val accessTypes = context.symbolTable.currentScope.values.collect(_ match {
          case typeSymbol: TypeSymbol if (typeSymbol.dataType.isInstanceOf[AccessType] && typeSymbol.dataType.asInstanceOf[AccessType].pointerType == IncompleteType) => typeSymbol.dataType.asInstanceOf[AccessType]
        }).toSeq
        accessTypes.foreach(accessType => accessType.pointerType = newTypeDeclaration.dataType)
        //we can now add the deallocate procedure, as the type is now defined
        val deallocateProcedures = accessTypes.map(accessType => new ProcedureSymbol(Identifier("deallocate"), Seq(VariableSymbol(Identifier("p"), accessType, InterfaceList.InterfaceMode.INOUT, 0, null)), Runtime, true))
        (newTypeDeclaration, context.copy(symbolTable = context.insertSymbols(deallocateProcedures).symbolTable.insert(new TypeSymbol(typeDeclaration.identifier, newTypeDeclaration.dataType, owner))))
      case _ =>
        (newTypeDeclaration, if (newTypeDeclaration.dataType == NoType) context else context.insertSymbols(new TypeSymbol(typeDeclaration.identifier, newTypeDeclaration.dataType, owner) +: newSymbols))
    }
  }

  def visitSubTypeDeclaration(subTypeDeclaration: SubTypeDeclaration, owner: Symbol, context: Context): ReturnType = {
    val dataType = createType(context, subTypeDeclaration.subTypeIndication, subTypeDeclaration.identifier.text)
    val symbol = new SubTypeSymbol(subTypeDeclaration.identifier, dataType, owner)
    (subTypeDeclaration, context.insertSymbol(symbol))
  }

  def visitUseClause(useClause: UseClause, context: Context): ReturnType = useClause.useList.flatMap{
    name =>
      name.identifiers match {
        case Seq(library, packageIdentifier, itemList@_*) if (itemList.size == 1 || itemList.size == 0) =>
          context.findSymbol(library, classOf[LibrarySymbol]).flatMap{
            symbol =>
              symbol.libraryArchive.loadSymbol(library.text + "/" + packageIdentifier.text + "_header.sym", classOf[PackageSymbol]).orElse(addError(packageIdentifier, SemanticMessage.NOT_FOUND, "package", packageIdentifier.text))
          }.map{
            packageSymbol =>
              itemList match {
                case Seq() => Seq(packageSymbol)
                case Seq(item) =>
                  if ("all" == item.text) {
                    packageSymbol.localSymbols.values
                  } else {
                    packageSymbol.localSymbols.get(operatorMangleMap.getOrElse(item.text, item.text)) match {
                      case Some(symbol) => Seq(symbol)
                      case None => addError(item, SemanticMessage.NOT_FOUND, "symbol", item.text).toList
                    }
                  }
              }
          }
        case _ => addError(name, SemanticMessage.INVALID_NAME).toList
      }
  }.flatten match {
    case Seq() => (useClause, context)
    case symbolList => (useClause, context.insertSymbols(symbolList, false))
  }

  def visitVariableAssignmentStatement(varAssignStmt: VariableAssignmentStatement, owner: Symbol, context: Context): ReturnType = varAssignStmt match {
    case stmt: SimpleVariableAssignmentStatement =>
      stmt.target.nameOrAggregate match {
        case Left(name) =>
          val nameExpression = acceptExpression(name, NoType, context)
          nameExpression match {
            case w: WithSymbol if (w.symbol.isInstanceOf[VariableSymbol]) =>
              val varSymbol = w.symbol.asInstanceOf[VariableSymbol]
              if (varSymbol.modifier == InterfaceList.InterfaceMode.IN) addError(varAssignStmt, SemanticMessage.ASSIGN_READ_ONLY, "variable", varSymbol.name)
              varSymbol.used = true
              checkPure(context, varAssignStmt, owner, varSymbol)
            case _ => addError(name.identifier, SemanticMessage.NOT_A, name.identifier.text, "variable")
          }
          val expression = checkExpression(context, stmt.expression, nameExpression.dataType)
          (stmt.copy(expression = expression, target = stmt.target.copy(expression = nameExpression)), context)
        case Right(aggregate) => error("not implemented") // TODO stmt.target.aggregate
      }
  }


  def visitVariableDeclaration(variableDeclaration: VariableDeclaration, owner: Symbol, context: Context): ReturnType = {
    val dataType = createType(context, variableDeclaration.subType)
    owner match {
      case _: ArchitectureSymbol | _: PackageSymbol | _: EntitySymbol =>
        if (!variableDeclaration.shared) addError(variableDeclaration, SemanticMessage.NOT_ALLOWED, "non shared variable")
        else {
          if (!dataType.isInstanceOf[ProtectedType]) addError(variableDeclaration.subType, SemanticMessage.SHARED_VARIABLE_NOT_PROTECTED_TYPE)
        }
      case typeSymbol: TypeSymbol =>
        if (variableDeclaration.shared) addError(variableDeclaration, SemanticMessage.NOT_ALLOWED, "shared variable")
        if (dataType == typeSymbol.dataType) addError(variableDeclaration.subType, SemanticMessage.PROTECTED_TYPE_IN_BODY)
      case _ => if (variableDeclaration.shared) addError(variableDeclaration, SemanticMessage.NOT_ALLOWED, "shared variable")
    }
    val initialValueExpression = dataType match {
      case _: ProtectedType => variableDeclaration.initialValueExpression.flatMap(expression => addError(expression, SemanticMessage.PROTECTED_TYPE_INITIAL_VALUE_EXPRESSION))
      case _ => checkExpressionOption(context, variableDeclaration.initialValueExpression, dataType)
    }
    val multiplier = getNextIndex(dataType) //+2 for real and physical, +1 for all other variables
    val symbols = variableDeclaration.identifierList.zipWithIndex.map{
      case (identifier, i) => new VariableSymbol(identifier, dataType, InterfaceList.InterfaceMode.LINKAGE, context.varIndex + (i * multiplier), owner)
    }
    val newNode = variableDeclaration.copy(initialValueExpression = initialValueExpression, symbols = symbols)
    val newContext = context.insertSymbols(symbols).copy(varIndex = context.varIndex + (variableDeclaration.identifierList.size * multiplier) + 1)
    (newNode, newContext)
  }

  def visitWaitStatement(waitStmt: WaitStatement, context: Context): ReturnType = {
    val nameList = waitStmt.sensitivityList.map(nameList => nameList.map(name => checkExpression(context, name, NoType))).getOrElse(Seq())
    nameList.foreach{
      expr =>
        expr match {
          case w: WithSymbol if (w.symbol.isInstanceOf[SignalSymbol]) =>
          case _ => addError(expr, "signal expected")
        }
    }
    val untilCondition = checkExpressionOption(context, waitStmt.untilCondition, SymbolTable.booleanType)
    val forExpression = checkExpressionOption(context, waitStmt.forExpression, SymbolTable.timeType)
    (waitStmt.copy(untilCondition = untilCondition, forExpression = forExpression), context)
  }

  def visitWhileStatement(whileStmt: WhileStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(whileStmt.label, whileStmt.endLabel)
    val condition = checkExpression(context, whileStmt.condition, SymbolTable.booleanType)
    val (sequentialStatementList, _) = acceptList(whileStmt.sequentialStatementList, owner, context.insertLoopLabel(whileStmt.label, whileStmt.position))
    (whileStmt.copy(condition = condition, sequentialStatementList = sequentialStatementList), context)
  }

  def acceptNode(node: ASTNode, owner: Symbol, context: Context): ReturnType = node match {
    case null => (null, context) //nothing
    case DesignFile(designUnits) =>
      val (units, newContext) = acceptList(designUnits, owner, context)
      (DesignFile(units), newContext)
    case designUnit: DesignUnit => visitDesignUnit(designUnit, owner, context)
    case packageBodyDeclaration: PackageBodyDeclaration => visitPackageBodyDeclaration(packageBodyDeclaration, owner, context)
    case packageDeclaration: PackageDeclaration => visitPackageDeclaration(packageDeclaration, owner, context)
    case entityDeclaration: EntityDeclaration => visitEntityDeclaration(entityDeclaration, owner, context)
    case architectureDeclaration: ArchitectureDeclaration => visitArchitectureDeclaration(architectureDeclaration, owner, context)
    case configurationDeclaration: ConfigurationDeclaration => visitConfigurationDeclaration(configurationDeclaration, owner, context)
    //declarative Items
    case variableDeclaration: VariableDeclaration => visitVariableDeclaration(variableDeclaration, owner, context)
    case constantDeclaration: ConstantDeclaration => visitConstantDeclaration(constantDeclaration, owner, context)
    case signalDeclaration: SignalDeclaration => visitSignalDeclaration(signalDeclaration, owner, context)
    case fileDeclaration: FileDeclaration => visitFileDeclaration(fileDeclaration, owner, context)
    case typeDeclaration: AbstractTypeDeclaration => visitTypeDeclaration(typeDeclaration, owner, context)
    case functionDefinition: FunctionDefinition => visitFunctionDefinition(functionDefinition, owner, context)
    case procedureDefinition: ProcedureDefinition => visitProcedureDefinition(procedureDefinition, owner, context)
    case componentDeclaration: ComponentDeclaration => visitComponentDeclaration(componentDeclaration, owner, context)
    case functionDeclaration: FunctionDeclaration => visitFunctionDeclaration(functionDeclaration, owner, context)
    case procedureDeclaration: ProcedureDeclaration => visitProcedureDeclaration(procedureDeclaration, owner, context)
    case subTypeDeclaration: SubTypeDeclaration => visitSubTypeDeclaration(subTypeDeclaration, owner, context)
    case attributeDeclaration: AttributeDeclaration => visitAttributeDeclaration(attributeDeclaration, owner, context)
    case attributeSpecification: AttributeSpecification => visitAttributeSpecification(attributeSpecification, owner, context)
    case useClause: UseClause => visitUseClause(useClause, context)
    case aliasDeclaration: AliasDeclaration => visitAliasDeclaration(aliasDeclaration, context)
    case groupDeclaration: GroupDeclaration => visitGroupDeclaration(groupDeclaration, owner, context)
    case groupTemplateDeclaration: GroupTemplateDeclaration => visitGroupTemplateDeclaration(groupTemplateDeclaration, owner, context)
    //sequential Statements
    case assertStmt: AssertStatement => visitAssertStatement(assertStmt, context)
    case waitStmt: WaitStatement => visitWaitStatement(waitStmt, context)
    case nextStmt: NextStatement => visitNextStatement(nextStmt, context)
    case exitStmt: ExitStatement => visitExitStatement(exitStmt, context)
    case nullStmt: NullStatement => (nullStmt, context)
    case reportStmt: ReportStatement => visitReportStatement(reportStmt, context)
    case returnStmt: ReturnStatement => visitReturnStatement(returnStmt, owner, context)
    case loopStmt: LoopStatement => visitLoopStatement(loopStmt, owner, context)
    case whileStmt: WhileStatement => visitWhileStatement(whileStmt, owner, context)
    case forStmt: ForStatement => visitForStatement(forStmt, owner, context)
    case signalAssignmentStmt: SignalAssignmentStatement => visitSignalAssignmentStatement(signalAssignmentStmt, owner, context)
    case variableAssignmentStmt: VariableAssignmentStatement => visitVariableAssignmentStatement(variableAssignmentStmt, owner, context)
    case procedureCallStmt: ProcedureCallStatement => visitProcedureCallStatement(procedureCallStmt, context)
    case caseStmt: CaseStatement => visitCaseStatement(caseStmt, owner, context)
    case ifStmt: IfStatement => visitIfStatement(ifStmt, owner, context)
    //concurrent Statements
    case concurrentSignalAssignmentStmt: ConcurrentSignalAssignmentStatement => visitConcurrentSignalAssignmentStatement(concurrentSignalAssignmentStmt, owner, context)
    case concurrentProcedureCallStmt: ConcurrentProcedureCallStatement => visitConcurrentProcedureCallStatement(concurrentProcedureCallStmt, owner, context)
    case concurrentAssertStmt: ConcurrentAssertionStatement => visitConcurrentAssertionStatement(concurrentAssertStmt, owner, context)
    case ifGenerateStmt: IfGenerateStatement => visitIfGenerateStatement(ifGenerateStmt, owner, context)
    case forGenerateStmt: ForGenerateStatement => visitForGenerateStatement(forGenerateStmt, owner, context)
    case componentInstantiationStmt: ComponentInstantiationStatement => visitComponentInstantiationStatement(componentInstantiationStmt, context)
    case processStmt: ProcessStatement => visitProcessStatement(processStmt, owner, context)
    case blockStmt: BlockStatement => visitBlockStatement(blockStmt, owner, context)
  }
}
