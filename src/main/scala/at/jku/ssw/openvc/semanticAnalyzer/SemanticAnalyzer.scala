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

import scala.collection.{SortedMap, immutable}
import math.Numeric.{LongIsIntegral, DoubleAsIfIntegral}
import annotation.tailrec

import at.jku.ssw.openvc.util._
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
  type Buffer[A] = immutable.VectorBuilder[A]

  private[this] val operatorMangleMap = Map(
    "and" -> "and", "or" -> "or", "nand" -> "nand", "nor" -> "nor", "xor" -> "xor", "xnor" -> "xnor", //logical_operator
    "=" -> "$eq", "/=" -> "$eq$div", "<" -> "$less", "<=" -> "$less$eq", ">" -> "$greater", ">=" -> "$greater$eq", //relational_operator
    "sll" -> "sll", "srl" -> "srl", "sla" -> "sla", "sra" -> "sra", "rol" -> "rol", "ror" -> "ror", //shift_operator
    "+" -> "$plus", "-" -> "$minus", "&" -> "$amp", //adding_operator + sign
    "*" -> "$times", "/" -> "$div", "mod" -> "mod", "rem" -> "rem", //multiplying_operator
    "**" -> "$times$times", "abs" -> "abs", "not" -> "not" //miscellaneous_operator
  )

  private[this] val entityClassToSymbolMap = {
    import EntityClass._
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
        case (at1: ArrayType, at2: ArrayType) => at1.elementType == at2.elementType //isCompatible(at1.elementType, at2.elementType) //TODO check element type and range
        case _ => false
      }
    }

  //TODO remove me
  var configuration: Configuration = null

  val semanticErrors = new Buffer[CompilerMessage]
  val semanticWarnings = new Buffer[CompilerMessage]

  def apply(designFile: DesignFile, configuration: Configuration): SemanticCheckResult = {
    this.configuration = configuration
    semanticErrors.clear
    semanticWarnings.clear
    val (newDesignFile, context) = acceptNode(designFile, NoSymbol, Context(0, new SymbolTable(List()), collection.immutable.Stack()))
    (newDesignFile.asInstanceOf[DesignFile], semanticErrors.result, semanticWarnings.result)
  }

  def addError(stmt: Locatable, msg: String, messageParameters: AnyRef*): Option[Nothing] = addErrorPosition(stmt.position, msg, messageParameters: _*)

  def addErrorPosition(position: Position, msg: String, messageParameters: AnyRef*): Option[Nothing] = {
    semanticErrors += new CompilerMessage(position, String.format(msg, messageParameters.toArray: _*))
    None
  }

  def addWarning(stmt: Locatable, msg: String, messageParameters: AnyRef*) = semanticWarnings += new CompilerMessage(stmt.position, String.format(msg, messageParameters.toArray: _*))

  def checkIdentifiersOption(startOption: Option[Identifier], endOption: Option[Identifier]) = startOption match {
    case Some(start) => checkIdentifiers(start, endOption)
    case None => endOption foreach (end => addError(end, "The statement is not labeld, %s is illegal here", end))
  }

  def checkIdentifiers(start: Identifier, endOption: Option[Identifier]) =
    for (end <- endOption) if (end.text != start.text) addError(end, "The start label:%s ist differend form this end label:%s", start, end)

  final case class Context(varIndex: Int, symbolTable: SymbolTable, loopLabels: collection.immutable.Stack[(Option[Identifier], Position)]) {
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
              librarySymbol.libraryArchive.loadSymbol(symbol.name + "/" + identifier.text + "_header", classOf[PackageSymbol]).orElse(addError(identifier, "package %s not found", identifier.text))
            case packageSymbol: PackageSymbol =>
              packageSymbol.localSymbols.get(operatorMangleMap.get(identifier.text).getOrElse(identifier.text)).orElse(addError(identifier, "symbol %s not found", identifier.text))
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
        case None => addError(symbolName, "symbol %s not found", symbolName)
        case Some(symbol) =>
          if (symbolClass.isInstance(symbol)) Some(symbol.asInstanceOf[A])
          else addError(symbolName, "%s is not a %s, it is a %s", symbol.name, symbolClass.toString, symbol.getClass.toString)
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

    def closeScope(): Seq[Symbol] = symbolTable.currentScope.values.toSeq

    def insertSymbols(symbols: Seq[Symbol], generateErrorMessage: Boolean = true): Context =
      copy(symbolTable = insertSymbolsIntoSymbolTable(this.symbolTable, symbols, generateErrorMessage))

    @tailrec
    def insertSymbolsIntoSymbolTable(symbolTable: SymbolTable, symbols: Seq[Symbol], generateErrorMessage: Boolean = true): SymbolTable = symbols match {
      case Seq() => symbolTable
      case Seq(symbol, xs@_*) => insertSymbolsIntoSymbolTable(insertSymbolIntoSymbolTable(symbolTable, symbol, generateErrorMessage), xs, generateErrorMessage)
    }

    def insertSymbol(optionSymbol: Option[Symbol]): Context =
      optionSymbol.map(insertSymbol(_, true)).getOrElse(this)

    def insertSymbol(symbol: Symbol, generateErrorMessage: Boolean = true): Context =
      copy(symbolTable = insertSymbolIntoSymbolTable(this.symbolTable, symbol, generateErrorMessage))

    def insertSymbolIntoSymbolTable(symbolTable: SymbolTable, symbol: Symbol, generateErrorMessage: Boolean = true): SymbolTable =
      symbolTable.currentScope.get(symbol.name) match {
        case Some(existingSymbol) =>
          existingSymbol match {
            case listOfSubprograms: ListOfSubprograms if (symbol.isInstanceOf[SubprogramSymbol] || symbol.isInstanceOf[ListOfSubprograms]) =>
              ((symbol: @unchecked) match {
                case subprogramSymbol: SubprogramSymbol =>
                  findSubprogramInList(listOfSubprograms, subprogramSymbol.parameters.map(_.dataType), subprogramSymbol.returnTypeOption) match {
                    case None => subprogramSymbol +: listOfSubprograms.subprograms
                    case Some(s) =>
                      if (!s.isImplemented && subprogramSymbol.isImplemented) {
                        subprogramSymbol.attributes ++= s.attributes
                        subprogramSymbol +: listOfSubprograms.subprograms.filter(_ ne s)
                      }
                      else {
                        addError(symbol, "subprogram %s already declared", symbol.name)
                        listOfSubprograms.subprograms
                      }
                  }
                case ListOfSubprograms(_, subprograms) => listOfSubprograms.subprograms ++ subprograms.filter(subprogramSymbol => findSubprogramInList(listOfSubprograms, subprogramSymbol.parameters.map(_.dataType), subprogramSymbol.returnTypeOption).isEmpty)
              }) match {
                case Seq() => symbolTable
                case subprograms => symbolTable.insert(ListOfSubprograms(symbol.identifier, subprograms))
              }
            case listOfEnumerations: ListOfEnumerations if (symbol.isInstanceOf[EnumerationSymbol] || symbol.isInstanceOf[ListOfEnumerations]) =>
              ((symbol: @unchecked) match {
                case enum: EnumerationSymbol => Seq(enum)
                case ListOfEnumerations(_, enumerations) => enumerations
              }) match {
                case Seq() => symbolTable
                case enumerations => symbolTable.insert(listOfEnumerations.copy(enumerations = enumerations ++ listOfEnumerations.enumerations))
              }
            case constSymbol: ConstantSymbol if (!constSymbol.isDefined) => symbolTable.insert(symbol) //used for deferred constants
            case typeSymbol: TypeSymbol if (typeSymbol.dataType.isInstanceOf[ProtectedType] && !typeSymbol.dataType.asInstanceOf[ProtectedType].isImplemented) =>
              symbolTable.insert(symbol) //used for protected type body
            case _ =>
              if (generateErrorMessage) addError(symbol, "symbol %s already declared in current scope", symbol.name)
              symbolTable
          }
        case None =>
          symbolTable.insert(symbol match {
            case subprogramSymbol: SubprogramSymbol => new ListOfSubprograms(subprogramSymbol.identifier, Seq(subprogramSymbol))
            case enum: EnumerationSymbol => new ListOfEnumerations(enum.identifier, Seq(enum))
            case _ => symbol
          })
      }
  }

  type ReturnType = (ASTNode, Context)

  def acceptDeclarativeItems(n: Seq[DeclarativeItem], owner: Symbol, context: Context): (Seq[DeclarativeItem], Context) = {
    val (list, newContext) = acceptNodes(n, owner, context)
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
            if (!protectedType.isImplemented && !owner.isInstanceOf[PackageSymbol])
              addError(owner, "no protected type body found for type %s", typeSymbol.name)
          //For each incomplete type declaration there must be a corresponding full type declaration with the same identifier.
          // This full type declaration must occur later and immediately within the same declarative part as the incomplete type declaration to which it corresponds.
          case IncompleteType => addError(typeSymbol, "the incomplete type %s is not defined", typeSymbol.name)
          case _ =>
        }
        //For each subprogram declaration, there shall be a corresponding body. If both a declaration and a body are given, the subprogram specification of the body
        //shall conform (see 2.7) to the subprogram specification of the declaration. Furthermore, both the declaration and the body must occur immediately
        //within the same declarative region (see 10.1).
        case ListOfSubprograms(_, subprograms) => subprograms.foreach{
          symbol =>
            if (!symbol.isImplemented && !(owner.isInstanceOf[PackageSymbol] && !owner.asInstanceOf[PackageSymbol].isBody) && (symbol.owner == owner) &&
              !(symbol.attributes.contains("foreign") && symbol.attributes("foreign").isInstanceOf[ForeignAttributeSymbol]))
              addError(owner, "body for subprogram %s not found", symbol.name)
        }
        case _ =>
      }
    }
    (list, newContext)
  }

  @tailrec
  def acceptNodes[A <: ASTNode](nodes: Seq[A], owner: Symbol, context: Context, buffer: Buffer[A] = new Buffer[A]()): (Seq[A], Context) = nodes match {
    case Seq() => (buffer.result, context)
    case Seq(head, xs@_*) =>
      val (newNode, newContext) = acceptNode(head, owner, context)
      newNode match {
        case null =>
        case node => buffer += node.asInstanceOf[A]
      }
      acceptNodes(xs, owner, newContext, buffer)
  }

  def acceptNodesOption[A <: ASTNode](listOption: Option[Seq[A]], owner: Symbol, context: Context): (Option[Seq[A]], Context) =
    listOption match {
      case None => (None, context)
      case Some(list) =>
        val (resultList, resultContext) = acceptNodes(list, owner, context)
        (Option(resultList), resultContext)
    }

  def acceptExpression(expr: Expression, expectedType: DataType, context: Context): Expression = {

    return acceptExpressionInner(expr)

    def acceptExpressionInnerOption(expression: Option[Expression]): Option[Expression] = expression.map(acceptExpressionInner(_))

    def acceptExpressionInner(expression: Expression): Expression = (expression: @unchecked) match {
      case NoExpression | null => NoExpression
      case item: ItemExpression => item
      case term: Term =>
        visitTerm(term.copy(left = acceptExpressionInner(term.left), right = acceptExpressionInner(term.right)))
      case aggregate: Aggregate => visitAggregate(aggregate)
      case relation: Relation =>
        visitRelation(relation.copy(left = acceptExpressionInner(relation.left), right = acceptExpressionInner(relation.right)))
      case qualifiedExpr: QualifiedExpression => visitQualifiedExpression(qualifiedExpr)
      case name: Name => visitName(name)
      case shiftExpr: ShiftExpression =>
        visitShiftExpression(shiftExpr.copy(left = acceptExpressionInner(shiftExpr.left), right = acceptExpressionInner(shiftExpr.right)))
      case factor: Factor =>
        visitFactor(factor.copy(left = acceptExpressionInner(factor.left), rightOption = acceptExpressionInnerOption(factor.rightOption)))
      case logicalExpr: LogicalExpression =>
        visitLogicalExpression(logicalExpr.copy(left = acceptExpressionInner(logicalExpr.left), right = acceptExpressionInner(logicalExpr.right)))
      case simpleExpr: SimpleExpression =>
        visitSimpleExpression(simpleExpr.copy(left = acceptExpressionInner(simpleExpr.left), rightOption = acceptExpressionInnerOption(simpleExpr.rightOption)))
      case newExpr: NewExpression => visitNewExpression(newExpr)
      case literal: Literal => visitLiteral(literal)
      case physicalLiteral: PhysicalLiteral => visitPhysicalLiteral(physicalLiteral)
    }

    def findOverloadedOperator(operator: String, loc: Locatable, expressions: Expression*): Option[FunctionCallExpression] = {
      val identifier = Identifier(loc.position, operatorMangleMap(operator))
      context.findSymbol(identifier, classOf[ListOfSubprograms]).flatMap{
        listOfSubprograms =>
          context.findFunctionInList(listOfSubprograms, expressions.map(_.dataType), NoType).map{
            functionSymbol => FunctionCallExpression(identifier, Option(AssociationList(Seq(), expressions)), None, functionSymbol)
          }
      }.orElse{
        expressions.toSeq match {
          case Seq(left, right) => addError(loc, """the operator "%s" is not defined for types %s and %s""", operator, left.dataType.name, right.dataType.name)
          case Seq(expression) => addError(loc, """the unary operator "%s" is not defined for type %s""", operator, expression.dataType.name)
        }
      }
    }

    def visitAggregate(aggregate: Aggregate): Expression =
      aggregate.elements match {
        case Seq(Aggregate.ElementAssociation(None, expression)) => checkExpression(context, expression, expectedType) //this is a aggregate of the form: (expression)
        case _ =>
          val (positionalElements, namedElements) = aggregate.elements.span(_.choices.isEmpty)
          (namedElements.find(element => element.choices.get.exists(choice => choice.isOthers)) match {
            case Some(othersElement) if ((othersElement ne namedElements.last) || !othersElement.choices.get.last.isOthers || othersElement.choices.get.size != 1) => addError(othersElement.choices.get.head, "the others choice must be the last single choice")
            case _ => namedElements.find(_.choices.isEmpty) match {
              case Some(element) => addErrorPosition(element.expression.firstPosition, "can not use positional element after named element")
              case _ =>
                val expressions = expectedType match {
                  case arrayType: ConstrainedArrayType =>
                    val dataType = arrayType.dimensions.size match {
                      case 1 => arrayType.elementType
                      case size => ConstrainedArrayType(arrayType.name, arrayType.elementType, arrayType.dimensions.tail) //TODO check if for each dimension is a row
                    }
                    @tailrec
                    def mapPositionalElements(positionalElements: Seq[Aggregate.ElementAssociation], map: immutable.SortedMap[Int, Expression], i: Int): immutable.SortedMap[Int, Expression] = positionalElements match {
                      case Seq() => map
                      case Seq(element, xs@_*) =>
                        require(element.choices.isEmpty)
                        mapPositionalElements(xs, map + (i -> checkExpression(context, element.expression, dataType)), i + 1)
                    }
                    require(namedElements.isEmpty) //TODO
                    mapPositionalElements(positionalElements, immutable.SortedMap(), 0).values.toSeq
                  case recordType: RecordType =>
                    @tailrec
                    def mapPositionalElements(positionalElements: Seq[(Aggregate.ElementAssociation, DataType)], map: immutable.SortedMap[Int, Expression], i: Int): immutable.SortedMap[Int, Expression] = positionalElements match {
                      case Seq() => map
                      case Seq((element, dataType), xs@_*) =>
                        require(element.choices.isEmpty)
                        mapPositionalElements(xs, map + (i -> checkExpression(context, element.expression, dataType)), i + 1)
                    }

                    @tailrec
                    def mapNamedElements(namedElements: Seq[Aggregate.ElementAssociation], map: immutable.SortedMap[Int, Expression]): immutable.SortedMap[Int, Expression] = namedElements match {
                      case Seq() => map
                      case Seq(element, xs@_*) =>
                        @tailrec
                        def mapChoices(choices: Seq[Choices.Choice], fields: Seq[Int] = Seq()): Seq[Int] = choices match {
                          case Seq() => fields
                          case Seq(choice, xs@_*) =>
                            val newFields = choice.rangeOrExpressionOrIdentifier match {
                              case None =>
                                val othersFields = recordType.fields.indices.diff(fields).diff(map.keys.toSeq)
                                if (othersFields.isEmpty) addError(choice, "no others fields found, all fields alreay have a value")
                                othersFields
                              case Some(rangeOrExpressionOrIdentifier) => (rangeOrExpressionOrIdentifier match {
                                case First(_) => addError(choice, "can not use a range in a record aggregate")
                                case Second(_) => addError(choice, "can not use a expressiong in a record aggregate")
                                case Third(identifier) => recordType.fieldsMap.get(identifier.text).orElse(addError(identifier, "field %s does not exist", identifier.text)).flatMap{
                                  dataType =>
                                    if (map.contains(recordType.fieldsIndexMap(identifier.text)) || fields.contains(recordType.fieldsIndexMap(identifier.text))) addError(identifier, "field %s has already a value", identifier.text)
                                    else Option(Seq(recordType.fieldsIndexMap(identifier.text)))
                                }
                              }) match {
                                case Some(x) => x ++ fields
                                case None => fields
                              }
                            }
                            mapChoices(xs, newFields)
                        }

                        val choices = element.choices.get
                        val fields = mapChoices(choices)
                        if (fields.nonEmpty) {
                          val dataType = recordType.fields(fields.head)._2
                          fields.zipAll(choices, fields.head, choices.head).foreach{
                            case (field, choice) => if (!isCompatible(recordType.fields(field)._2, dataType)) {
                              addError(choice, "field %s is not of type %s", recordType.fields(field)._1, dataType.name)
                            }
                          }
                          val expr = checkExpression(context, element.expression, dataType)
                          mapNamedElements(xs, map ++ immutable.SortedMap(fields.map(i => i -> expr): _*))
                        } else mapNamedElements(xs, map)
                    }

                    val parametersMap = mapNamedElements(namedElements, mapPositionalElements(positionalElements.zip(recordType.fields.unzip._2), immutable.SortedMap(), 0))
                    recordType.fields.zipWithIndex.flatMap{
                      case ((field, _), i) => parametersMap.get(i).orElse(addError(aggregate, "no value found for field %s", field))
                    }
                  case dataType => addError(aggregate, "expected a expression of an constrained array or record type found %s", dataType.name).toList
                }
                Option(new Aggregate(aggregate.position, Seq(), expressions, expectedType))
            }
          }).getOrElse(aggregate)
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
                addErrorPosition(literal.position.addCharacterOffset(i + 1), "'%s' is not a element of enumeration type %s", c.toString, enumType.name)
            }
            //literal.copy(dataType = new ConstrainedRangeType(arrayType.elementType, 0, literal.text.length))//TODO
            literal.copy(dataType = arrayType)
          case NoType => literal.copy(dataType = SymbolTable.stringType) //TODO
          case dataType =>
            addError(literal, "expected a expression of type %s, found %s", expectedType.name, dataType.name)
            literal.copy(dataType = NoType)
        }
        case CHARACTER_LITERAL =>
          context.findSymbol(Identifier(literal.position, literal.text), classOf[ListOfEnumerations]).flatMap{
            list => list.enumerations.filter(_.dataType == expectedType) match {
              case Seq(enumerationSymbol) => Option(enumerationSymbol.dataType)
              case Seq() => addError(literal, "enumeration value %s not found in enumeration type %s", literal.text, expectedType.name)
              case symbols => addError(literal, "ambiguous character literal, found %s matching symbols", symbols.size.toString)
            }
          } match {
            case Some(dataType) => literal.copy(dataType = dataType, value = dataType.intValue(literal.text.replace("'", "")))
            case None => literal
          }
        case BIT_STRING_LITERAL =>
          val Regex = "(b|o|x)\"([a-f0-9]+)\"".r
          literal.text.replace("_", "").toLowerCase match {
            case Regex(baseSpecifier, values) =>
              val INVALID_BASED_LITERAL_CHARACTER = " invalid character %s in based literal with base %s"
              val valueString = baseSpecifier match {
                case "b" =>
                  if (values.zipWithIndex.forall{
                    case (character, i) =>
                      if (character != '0' && character != '1') {
                        addErrorPosition(literal.position.addCharacterOffset(i + 1), INVALID_BASED_LITERAL_CHARACTER, character.toString, "binary")
                        false
                      } else true
                  }) Integer.parseInt(values, 2).toBinaryString
                  else values
                case "o" => values.zipWithIndex.map{
                  case (character, i) =>
                    if (character >= '0' && character <= '7') {
                      val value = Integer.toBinaryString(character - '0')
                      value.length match {
                        case 1 => "00" + value
                        case 2 => "0" + value
                        case _ => value
                      }
                    }
                    else {
                      addErrorPosition(literal.position.addCharacterOffset(i + 1), INVALID_BASED_LITERAL_CHARACTER, character.toString, "octal")
                      "000"
                    }
                }.mkString
                case "x" => values.zipWithIndex.map{
                  case (character, i) =>
                    if ((character >= '0' && character <= '9') || (character >= 'a' && character <= 'f')) {
                      val value = Integer.toBinaryString(character - (if (Character.isDigit(character)) '0' else 'W'))
                      value.length match {
                        case 1 => "000" + value
                        case 2 => "00" + value
                        case 3 => "0" + value
                        case _ => value
                      }
                    }
                    else {
                      addErrorPosition(literal.position.addCharacterOffset(i + 1), INVALID_BASED_LITERAL_CHARACTER, character.toString, "octal")
                      "0000"
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
      def visitFunctionCallExpression(functionCallExpr: FunctionCallExpression, listOfSubprograms: ListOfSubprograms): Expression = {
        val (functionSymbol, parameterAssociation) = checkSubprogramAssociationList(context, functionCallExpr.parameterAssociation, listOfSubprograms, Option(expectedType), functionCallExpr)
        (functionCallExpr.copy(parameterAssociation = parameterAssociation, symbol = functionSymbol.orNull.asInstanceOf[FunctionSymbol]))
      }
      def matchParts(parts: Seq[Name.Part], symbol: Symbol): Option[Expression] = {
        if (symbol.isInstanceOf[AliasSymbol]) matchParts(parts, symbol.asInstanceOf[AliasSymbol].destination)
        else parts match {
          case Seq() => None
          case Seq(part, xs@_*) =>
            part match {
              case Name.AssociationListPart(_, associationList) =>
                symbol match {
                  case list: ListOfSubprograms =>
                    require(xs.isEmpty)
                    val expression = visitFunctionCallExpression(FunctionCallExpression(name.identifier, Some(associationList)), list) //TODO name.identifier is wrong
                    /*
                    TODO match the result value of a expression, e.g. x:=fooFunction(1,2,3,4).x;
                    val newSymbol = ConstantSymbol(Identifier("blub"), expression.dataType, -1, null)
                    val expr = matchParts(xs, newSymbol).getOrElse(NoExpression)
                    */
                    Option(expression)
                  case _ =>
                    if (associationList.elements.forall(element => element.formalPart.isEmpty && element.actualPart.isLeft)) {
                      val indexes = associationList.elements.map(_.actualPart.asInstanceOf[Left[Expression, Identifier]].a)
                      symbol match {
                        case _: SubTypeSymbol | _: TypeSymbol =>
                          val dataType = (symbol: @unchecked) match {
                            case SubTypeSymbol(_, dataType, _) => dataType
                            case TypeSymbol(_, dataType, _) => dataType
                          }
                          if (indexes.size == 1) {
                            require(xs.isEmpty)
                            val expression = acceptExpression(indexes.head, NoType, context)
                            (dataType, expression.dataType) match {
                              case (_: NumericType, _: NumericType) =>
                              case (arr: ArrayType, arr2: ArrayType) if (isCompatible(arr.elementType, arr2.elementType) && arr.dimensions.size == arr2.dimensions.size) => // TODO check if cast is allowed
                              case (firstType, secondType) => addError(expression, "invalid type cast from type %s to type %s", firstType.name, secondType.name)
                            }
                            Option(TypeCastExpression(expression, dataType))
                          }
                          else addError(name, "invalid type cast expression")
                        case r: RuntimeSymbol => r.dataType match {
                          case array: ArrayType =>
                            if (indexes.size != array.dimensions.size)
                              addError(part, "invalid indices count, found %s expected %s", indexes.size.toString, array.dimensions.size.toString)
                            val expressions = indexes.zip(array.dimensions).map(x => checkExpression(context, x._1, x._2.elementType))
                            Option(new ArrayAccessExpression(r, expressions, array.elementType, matchParts(xs, r.makeCopy(Identifier(part.position, "array"), array.elementType, symbol))))
                          case _ => addError(part, "%s is not a array", r.name)
                        }
                        case _ => addError(name, "%s is not a function", symbol.name)
                      }
                    } else addError(name, "%s is not a function", symbol.name)
                }
              case Name.AttributePart(signatureOption, identifier, expressionOption) =>
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
                  _.attributes.get(identifier.text) match {
                    case None => addError(identifier, "attribute %s not found", identifier.text)
                    case Some(attribute) =>
                      val parameterExpression = attribute match {
                        case preDefinedAttributeSymbol: PreDefinedAttributeSymbol => preDefinedAttributeSymbol.parameter match {
                          case Some(requiredDataType) => preDefinedAttributeSymbol.isParameterOptional match {
                            case false => expressionOption match {
                              case None => addError(identifier, "the attribute %s requires a parameter of type %s", attribute.name, requiredDataType.name)
                              case Some(expression) => Option(checkExpression(context, expression, requiredDataType))
                            }
                            case true => expressionOption match {
                              case None => Some(Literal(identifier.position, "1", Literal.Type.INTEGER_LITERAL, SymbolTable.universalIntegerType, 1)) //this must be a array attribute where the dimension is optional
                              case Some(expression) => Option(checkExpression(context, expression, requiredDataType))
                            }
                          }
                          case None => expressionOption.flatMap(addError(_, "the attribute %s does not take a parameter", attribute.name))
                        }
                        case userDefinedAttributeSymbol: UserDefinedAttributeSymbol => expressionOption.flatMap(addError(_, "a user defined attribute does not take parameters"))
                      }
                      val expression = attribute match {
                        case preDefinedAttributeSymbol: PreDefinedAttributeSymbol =>
                          if (identifier.text == "base") matchParts(xs, TypeSymbol(Identifier("base"), preDefinedAttributeSymbol.dataType, null))
                          else {
                            require(xs.isEmpty)
                            None
                          }
                        case userDefinedAttributeSymbol: UserDefinedAttributeSymbol =>
                          require(xs.isEmpty)
                          None
                      }
                      Option(AttributeExpression(identifier.position, symbol, attribute, parameterExpression, expression))
                  }
                }
              case Name.SlicePart(range) =>
                symbol match {
                  case r: RuntimeSymbol if (r.dataType.isInstanceOf[ArrayType]) => Option(new SliceAccessExpression(r, range, r.dataType, matchParts(xs, r.makeCopy(Identifier(part.position, "range"), r.dataType, symbol))))
                  case s => addError(part, "%s is not a array", s.name)
                }
              case Name.SelectedPart(identifier) => symbol match {
                case r: RuntimeSymbol if (r.dataType.isInstanceOf[RecordType] || (r.dataType.isInstanceOf[AccessType] && r.dataType.asInstanceOf[AccessType].pointerType.isInstanceOf[RecordType])) =>
                  ((r.dataType: @unchecked) match {
                    case recordType: RecordType => recordType
                    case accessType: AccessType => accessType.pointerType.asInstanceOf[RecordType]
                  }).fieldsMap.get(identifier.text) match {
                    case None => addError(part, "field %s not found", identifier.text)
                    case Some(dataType) => Option(new FieldAccessExpression(r, identifier, dataType, matchParts(xs, r.makeCopy(identifier, dataType, symbol))))
                  }
                case s => addError(part, "%s is not a record", s.name)
              }
            }
        }
      }

      context.symbolTable.find(operatorMangleMap.get(name.identifier.text).getOrElse(name.identifier.originalText)) match {
        case None => (name.parts match {
          case Seq() => expectedType match {
            case _: ArrayType | NoType => Option(visitLiteral(Literal(name.position, name.identifier.text, Literal.Type.STRING_LITERAL))) //TODO
            case _ => addError(name, "symbol %s not found", name.identifier)
          }
          case xs => addError(name, "symbol %s not found", name.identifier)
        }).getOrElse(NoExpression)
        case Some(symbol) => name.parts match {
          case Seq() =>
            def matchSymbol(symbol: Symbol): Expression = symbol match {
              case AliasSymbol(_, destination) => matchSymbol(destination)
              case list: ListOfSubprograms => visitFunctionCallExpression(FunctionCallExpression(name.identifier, None), list)
              case unitSymbol: UnitSymbol => PhysicalLiteral(name.position, "1", new SelectedName(Seq(name.identifier)), Literal.Type.INTEGER_LITERAL, unitSymbol)
              case ListOfEnumerations(_, enumerations) => enumerations.find(_.dataType == expectedType) match {
                case Some(enumSymbol) => Literal(name.position, name.identifier.text, Literal.Type.INTEGER_LITERAL, enumSymbol.dataType, enumSymbol.dataType.intValue(enumSymbol.name))
                case _ =>
                  addError(name, "enumerations value " + name.identifier.text)
                  NoExpression
              }
              case r: RuntimeSymbol => ItemExpression(name.position, r)
              case _ =>
                addError(name, "symbol %s unexpected here" + symbol.name)
                NoExpression
            }
            matchSymbol(symbol)
          case xs => matchParts(xs, symbol).getOrElse(NoExpression)
        }
      }
    }

    def visitNewExpression(newExpression: NewExpression): Expression =
    // TODO Auto-generated method stub
      newExpression.qualifiedExpressionOrSubTypeIndication match {
        case Left(qualifiedExpression) => expectedType match {
          case accessType: AccessType => NewExpression(newExpression.position, Left(checkExpression(context, qualifiedExpression, accessType.pointerType)))
          case otherType =>
            addError(qualifiedExpression, "%s is not a access type", otherType.name)
            NoExpression
        }
        case Right(subType) => NewExpression(newExpression.position, Right(createType(context, subType)))
      }

    def visitQualifiedExpression(qualifiedExpression: QualifiedExpression): Expression = {
      //because a qualified expression is parsed as type_mark ' aggregate we must distinguish between the two forms
      //the two forms are parsed as aggregate because a aggregate is of the from ( (choices ARROW)? expression (, (choices ARROW)? expression)* ), so a input of
      //type_mark ' ( expression ) can also be an aggregate with one element without a choices part
      val dataType = context.findType(qualifiedExpression.typeName)
      val expressionToCheck = qualifiedExpression.expression match {
        case Aggregate(_, Seq(Aggregate.ElementAssociation(None, expression)), _, _) => expression //expression is of type: type_mark ' ( expression )
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
    throw new RuntimeException()
  }

  def findSubprogramFromSignature(context: Context, listOfFunctions: ListOfSubprograms, signature: Signature): Option[SubprogramSymbol] = {
    def signatureToTypes(context: Context, signature: Signature): (Seq[DataType], Option[DataType]) =
      (signature.parameterTypes.map(list => list.map(parameter => context.findType(parameter))).flatten.toSeq, signature.returnType.map(returnType => context.findType(returnType)))

    val (dataTypes, returnTypeOption) = signatureToTypes(context, signature)
    context.findSubprogramInList(listOfFunctions, dataTypes, returnTypeOption).orElse(addError(signature, "no subprogram found with this signature"))
  }

  def findLiteralFromSignature(context: Context, enumerations: Seq[EnumerationSymbol], signature: Signature): Option[EnumerationSymbol] = {
    signature.parameterTypes.foreach(_.foreach(typeName => addError(typeName, "a signature for an enumeration literal can not have a parameter type")))
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
      addErrorPosition(expr.firstPosition, "expected a expression of type %s, found %s", dataType.name, newExpr.dataType.name)
    }
    newExpr
  }

  @tailrec
  def insertSequentialStatementsLabels(context: Context, statements: Seq[SequentialStatement], owner: Symbol): Context = statements match {
    case Seq() => context
    case Seq(statement, xs@_*) =>
      def insertLabelSymbol(statement: SequentialStatement): Context = statement.label.map(identifier => context.insertSymbol(new LabelSymbol(identifier, owner))).getOrElse(context)
      val list = statement match {
        case abstractLoopStatement: AbstractLoopStatement => abstractLoopStatement.sequentialStatements
        case ifStatement: IfStatement => ifStatement.ifThenList.map(_.sequentialStatements).flatten ++ ifStatement.elseSequentialStatements.getOrElse(Seq())
        case caseStatement: CaseStatement => caseStatement.alternatives.map(_.sequentialStatements).flatten
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

  def checkSubprogramAssociationList(context: Context, associationList: Option[AssociationList], listOfSubprograms: ListOfSubprograms, returnType: Option[DataType], owner: Locatable): (Option[SubprogramSymbol], Option[AssociationList]) =
    listOfSubprograms.subprograms.filter(_.returnTypeOption == returnType) match {
      case Seq(subprogram) => (Option(subprogram), checkGenericAssociationList(context, associationList, Right(subprogram.parameters), owner)._2)
      case Seq() => returnType match {
        case None => (addError(owner, "no procedure %s found", listOfSubprograms.name), None)
        case Some(dataType) => (addError(owner, "no founction %s found with return type %s", listOfSubprograms.name, dataType.name), None)
      }
      case subs => checkGenericAssociationList(context, associationList, Left(subs), owner)
    }

  def checkAssociationList(context: Context, associationList: Option[AssociationList], symbols: Seq[RuntimeSymbol], owner: Locatable): Option[AssociationList] =
    checkGenericAssociationList(context, associationList, Right(symbols), owner)._2

  def checkGenericAssociationList(context: Context, associationList: Option[AssociationList], parameters: Either[Seq[SubprogramSymbol], Seq[RuntimeSymbol]], owner: Locatable): (Option[SubprogramSymbol], Option[AssociationList]) =
  //TODO check formal Signal => actual Signal, formal Constant => actual Constant, formal Variable => actual Variable, formal File => actual File
    associationList match {
      case None =>
        parameters match {
          case Left(subprograms) => subprograms match {
            case Seq(subprogram) => (Some(subprogram), None)
            case subs => (addError(owner, "ambiguous subprogramm call, found %s matching subprogramms", subs.size.toString), None)
          }
          case Right(symbols) =>
            if (symbols.exists(symbol => !symbol.isOptional)) addError(owner, "invalid argument count, found 0 expected %s", symbols.size.toString)
            (None, Option(AssociationList(Seq(), symbols.map(symbol => DefaultExpression(symbol)), symbols)))
        }
      case Some(list) =>
        val (positionalElements, namedElements) = list.elements.span(_.formalPart.isEmpty)
        namedElements.find(element => element.formalPart.isEmpty) match {
          case Some(element) => (None, addErrorPosition(element.actualPosition, "can not use positional element after named element"))
          case _ =>
            parameters match {
              case Left(subprograms) =>
              /*
              A call to an overloaded subprogram is ambiguous (and therefore is an error) if the name of the subprogram, the number of parameter associations,
              the types and order of the actual parameters, the names of the formal parameters (if named associations are used), and the result type (for functions)
              are not sufficient to identify exactly one (overloaded) subprogram.
              */

                @tailrec
                def mapPositionalElements(positionalElements: Seq[AssociationList.Element], buffer: Buffer[AssociationList.Element], subprograms: Seq[SubprogramSymbol], i: Int): Seq[SubprogramSymbol] = positionalElements match {
                  case Seq() => subprograms
                  case Seq(element, xs@_*) =>
                    require(element.formalPart.isEmpty)
                    (element.actualPart match {
                      case Right(openIdentifier) => addError(openIdentifier, "a parameter for a subprogramm can not be open")
                      case Left(expression) =>
                        val expr = checkExpression(context, expression, NoType)
                        subprograms.filter(subprogram => isCompatible(subprogram.parameters(i).dataType, expr.dataType)) match {
                          case Seq() => addErrorPosition(element.actualPosition, "no subprogram found, which takes a %s at parameter position %s", expr.dataType.name, i.toString)
                          case sub => Option((sub, expr))
                        }
                    }) match {
                      case None => subprograms
                      case Some((sub, expr)) =>
                        mapPositionalElements(xs, buffer += element.copy(actualPart = Left(expr)), sub, i + 1)
                    }
                }

                @tailrec
                def mapNamedElements(namedElements: Seq[AssociationList.Element], buffer: Buffer[AssociationList.Element], subprograms: Seq[SubprogramSymbol]): Seq[SubprogramSymbol] = namedElements match {
                  case Seq() => subprograms
                  case Seq(element, xs@_*) => element.formalPart.get match {
                    case Left(identifier) =>
                      (element.actualPart match {
                        case Right(openIdentifier) => addError(openIdentifier, "a parameter for a subprogramm can not be open")
                        case Left(expression) =>
                          val expr = checkExpression(context, expression, NoType)
                          subprograms.filter(subprogram => subprogram.parameters.find(_.name == identifier.text).map(symbol => isCompatible(symbol.dataType, expr.dataType)).isDefined) match {
                            case Seq() => addError(identifier, "no subprogram found, which has a parameter with formal name %s of type %s", identifier.text, expr.dataType.name)
                            case sub => Option((sub, expr))
                          }
                      }) match {
                        case None => subprograms
                        case Some((sub, expr)) => mapNamedElements(xs, buffer += element.copy(actualPart = Left(expr)), sub)
                      }
                    case Right((name, part)) => error("not implemented")
                  }
                }
                val buffer = new Buffer[AssociationList.Element]()
                mapNamedElements(namedElements, buffer, mapPositionalElements(positionalElements, buffer, subprograms, 0)).filter(_.parameters.size == list.elements.size) match {
                  case Seq(subprogram) => (Some(subprogram), checkGenericAssociationList(context, Option(AssociationList(buffer.result)), Right(subprogram.parameters), owner)._2)
                  case Seq() => (addError(owner, "no matching subprogramm found"), None)
                  case subs => (addError(owner, "ambiguous subprogramm call, found %s matching subprogramms", subs.size.toString), None)
                }
              case Right(symbols) =>
                val symbolMap = symbols.map(symbol => (symbol.name -> symbol)).toMap
                val symbolIndexMap = symbols.zipWithIndex.toMap

                def checkParameterExpression(element: AssociationList.Element, symbol: RuntimeSymbol): Expression = element.actualPart match {
                  case Right(openIdentifier) =>
                    symbol match {
                      case signalSymbol: SignalSymbol if (signalSymbol.isPort) =>
                        if (signalSymbol.mode == InterfaceList.Mode.IN) addError(openIdentifier, "a ports with mode in can not be open")
                        else if (signalSymbol.dataType.isInstanceOf[UnconstrainedArrayType]) addError(openIdentifier, "a port with mode in can not be open")
                      case _ => addError(openIdentifier, "only ports can be open")
                    }
                    OpenExpression(symbol)
                  case Left(expression) =>
                    val expr = if (expression.dataType == NoType) checkExpression(context, expression, symbol.dataType) else expression
                    expr match {
                      case w: WithSymbol => (symbol, w.symbol) match {
                        case (formalSignalSymbol: SignalSymbol, actualSignalSymbol: SignalSymbol) =>
                          import InterfaceList.Mode._
                          if (formalSignalSymbol.mode != IN) addError(expression, "to provide a port with constant driving values the port must be of mode in")
                          //TODO checkIsStaticName
                          formalSignalSymbol.mode match {
                            case IN => if (actualSignalSymbol.mode != IN && actualSignalSymbol.mode != INOUT && actualSignalSymbol.mode != BUFFER) addErrorPosition(expression.firstPosition, "For a formal port of mode in, the associated actual must be a port of mode in, inout, or buffer")
                            case OUT => if (actualSignalSymbol.mode != OUT && actualSignalSymbol.mode != INOUT && actualSignalSymbol.mode != BUFFER) addErrorPosition(expression.firstPosition, "For a formal port of mode out, the associated actual must be a port of mode out, inout, or buffer")
                            case INOUT => if (actualSignalSymbol.mode != INOUT && actualSignalSymbol.mode != BUFFER) addErrorPosition(expression.firstPosition, "For a formal port of mode inout, the associated actual must be a port of mode inout or buffer")
                            case BUFFER => if (actualSignalSymbol.mode != OUT && actualSignalSymbol.mode != INOUT && actualSignalSymbol.mode != BUFFER) addErrorPosition(expression.firstPosition, "For a formal port of mode buffer, the associated actual must be a port of mode out, inout, or buffer")
                          }
                        case (formalSignalSymbol: SignalSymbol, actualSymbol) => addErrorPosition(expression.firstPosition, "expected a signal parameter")
                        case (formalFileSymbol: FileSymbol, actualSymbol) => if (!actualSymbol.isInstanceOf[FileSymbol]) addErrorPosition(expression.firstPosition, "expected a file parameter")
                        case (formalConstantSymbol: ConstantSymbol, actualSymbol) => if (!actualSymbol.isInstanceOf[ConstantSymbol] && !actualSymbol.isInstanceOf[VariableSymbol]) addErrorPosition(expression.firstPosition, "expected a constant or variable parameter")
                        case (formalVariableSymbol: VariableSymbol, actualVariableSymbol: VariableSymbol) => //TODO
                        case (formalVariableSymbol: VariableSymbol, actualSymbol) => addErrorPosition(expression.firstPosition, "expected a variable parameter")
                      }
                      case _ => if (symbol.isInstanceOf[SignalSymbol] && symbol.asInstanceOf[SignalSymbol].isPort) checkIsGloballyStaticExpression(expr)
                    }
                    expr
                }

                @tailrec
                def mapPositionalElements(positionalElements: Seq[(AssociationList.Element, RuntimeSymbol)], map: SortedMap[Int, Expression], i: Int): SortedMap[Int, Expression] = positionalElements match {
                  case Seq() => map
                  case Seq((element, symbol), xs@_*) =>
                    require(element.formalPart.isEmpty)
                    mapPositionalElements(xs, map + (i -> checkParameterExpression(element, symbol)), i + 1)
                }

                @tailrec
                def mapNamedElements(namedElements: Seq[AssociationList.Element], map: SortedMap[Int, Expression]): SortedMap[Int, Expression] = namedElements match {
                  case Seq() => map
                  case Seq(element, xs@_*) => element.formalPart.get match {
                    case Left(identifier) => symbolMap.get(identifier.text).orElse(addError(identifier, "parameter %s does not exist", identifier.text)).flatMap(symbol => symbolIndexMap.get(symbol).map(index => (symbol, index))) match {
                      case None => mapNamedElements(xs, map)
                      case Some((symbol, index)) =>
                        if (map.contains(index)) addError(identifier, "parameter %s already has a value", symbol.name)
                        mapNamedElements(xs, map + (index -> checkParameterExpression(element, symbol)))
                    }
                    case Right((name, part)) => error("not implemented")
                  }
                }
                val parametersMap = mapNamedElements(namedElements, mapPositionalElements(positionalElements.zip(symbols), SortedMap(), 0))
                val parameters = symbols.zipWithIndex.flatMap{
                  case (symbol, i) => parametersMap.get(i).orElse{
                    if (symbol.isOptional) Option(DefaultExpression(symbol))
                    else addError(owner, "no value found for parameter %s", symbol.name)
                  }
                }
                (None, Some(list.copy(parameters = parameters, symbols = symbols)))
            }

        }
    }

  def checkDiscreteRange(context: Context, discreteRange: DiscreteRange): DiscreteRange =
    discreteRange.rangeOrSubTypeIndication match {
      case Left(range) =>
        val newRange = checkRange(context, range)
        if (!newRange.dataType.isInstanceOf[DiscreteType]) addError(newRange, "expected a discrete type")
        new DiscreteRange(Left(newRange), dataType = new ConstrainedRangeType(newRange.dataType, 0, 10)) //TODO lowLong.toInt, highLong.toInt))
      case Right(subTypeIndication) =>
      // TODO
        val dataType = context.findType(subTypeIndication.typeName)
        if (!dataType.isInstanceOf[DiscreteType]) addError(subTypeIndication.typeName, "expected a discrete type")
        for (resolutionFunction <- subTypeIndication.resolutionFunction) addError(resolutionFunction, "a subtype indication in a discrete range can not have a resolution function")
        subTypeIndication.constraint.flatMap{
          _ match {
            case Left(sourceRange) =>
              val range = checkRange(context, sourceRange)
              val (low: Long, high: Long) = calcRangeValues(range)(LongIsIntegral).getOrElse((Int.MinValue, Int.MinValue))
              Option(new DiscreteRange(Left(range), dataType = new ConstrainedRangeType(dataType, low.toInt, high.toInt)))
            case _ => addError(subTypeIndication.typeName, "expected a subtype indication with a range constraint, found a index constraint")
          }
        }.getOrElse(discreteRange)
    }

  def checkLoopLabel(context: Context, loopLabelOption: Option[Identifier], node: ASTNode, stmtName: String): Position =
    (loopLabelOption match {
      case None => context.loopLabels match {
        case Seq() => addError(node, "the %s statement must be inside a loop", stmtName)
        case Seq((_, position), _) => Some(position)
      }
      case Some(loopLabel) => context.loopLabels.find(_._1 == loopLabel) match {
        case None => addError(loopLabel, "loop label %s not found", loopLabel)
        case Some((_, position)) => Some(position)
      }
    }).orNull


  def checkBlockConfiguration(context: Context, blockConfiguration: BlockConfiguration) {

  }

  def checkPure(context: Context, node: ASTNode, owner: Symbol, symbol: Symbol) = owner match {
    case functionSymbol: FunctionSymbol =>
      if (functionSymbol.isPure && (symbol.owner ne owner)) {
        addError(node, "%s assignment in pure function %s not allowed for %s", symbol.getClass.toString, functionSymbol.name, symbol.name)
      }
    case _ =>
  }

  def calcRangeValues[A <: AnyVal](range: Range)(implicit numeric: Integral[A]): Option[(A, A)] =
    range.expressionsOrName match {
      case Left((fromExpression, direction, toExpression)) =>
        import numeric.mkOrderingOps
        (StaticExpressionCalculator.calcValue(fromExpression)(numeric), StaticExpressionCalculator.calcValue(toExpression)(numeric)) match {
          case (Some(x), Some(y)) => direction match {
            case Range.Direction.To =>
              if (x > y) addError(fromExpression, "the left value is bigger then the right value")
              else Option((x, y))
            case Range.Direction.Downto =>
              if (x < y) addError(fromExpression, "the left value is smaller then the right value")
              else Option((y, x))
          }
          case _ => None
        }
      case Right(_) => None
    }

  def checkRange(context: Context, range: Range): Range = range.expressionsOrName match {
  //TODO check if range expression are locally static expressions
    case Left((sourceFromExpression, direction, sourceToExpression)) =>
      val fromExpression = checkExpression(context, sourceFromExpression, NoType)
      val toExpression = checkExpression(context, sourceToExpression, fromExpression.dataType)
      if (!isCompatible(fromExpression.dataType, toExpression.dataType)) addError(toExpression, "data type %s is not comaptible with %s", toExpression.dataType.name, fromExpression.dataType)
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
        addError(name, "%s is not a valid overloaded operator", name.text)
        name.text
    }
    Identifier(name.position, text)
  }

  def checkIsStaticExpression(expr: Expression) {
    //TODO
  }

  def checkIsGloballyStaticExpression(expr: Expression) {
    //TODO
  }

  def checkIsStaticName(name: Name) {
    //TODO
  }

  def getStartIndex(owner: Symbol): Int = (owner: @unchecked) match {
    case _: PackageSymbol | null => 0
    case _: ArchitectureSymbol | _: ProcessSymbol | _: TypeSymbol | _: EntitySymbol | _: ComponentSymbol => 1
    case subProgramSymbol: SubprogramSymbol => getStartIndex(subProgramSymbol.owner)
  }

  def createSymbolsFromInterfaceList(context: Context, interfaceListOption: Option[Seq[InterfaceList.AbstractInterfaceElement]], owner: Symbol): (Seq[RuntimeSymbol], Option[Seq[InterfaceList.AbstractInterfaceElement]]) =
    interfaceListOption match {
      case None => (Seq(), None)
      case Some(interfaceList) =>
        var varIndex = getStartIndex(owner)
        val (symbols, elements) = interfaceList.map{
          element =>
            import InterfaceList._
            import InterfaceList.Mode.{IN, LINKAGE}
            val subType = createType(context, element.subType)
            val dataType = subType.dataType
            val nextIndex = getNextIndex(dataType)
            val isOptional = element.expression.isDefined
            val expression = checkExpressionOption(context, element.expression, dataType)
            val mode = element.mode.getOrElse(IN)

            if (dataType.isInstanceOf[ProtectedType] && element.expression.isDefined) addError(element.expression.get, "a protected type can not have a default value")
            expression.foreach(checkIsStaticExpression)
            if (mode == LINKAGE) addError(element.subType, "linkage mode is currently not supported") //LINKAGE is used for local variables and signals

            val symbols = element.identifiers.map{
              identifier =>
                val (symbol, indexChange) = element match {
                  case variableDeclaration: InterfaceVariableDeclaration =>
                    if (mode != IN && element.expression.isDefined) addError(element.expression.get, "a variable parameter with a mode other than in can not have a default value")
                    (new VariableSymbol(identifier, dataType, mode, varIndex, owner, isOptional, isParameter = true), nextIndex)
                  case signalDeclaration: InterfaceSignalDeclaration =>
                    checkIfNotFileProtectedAccessType(element.subType, dataType)
                    if (element.expression.isDefined) {
                      if (owner.isInstanceOf[SubprogramSymbol]) addError(element.expression.get, "a signal parameter can not have a default value")
                      else if (mode != IN) addError(element.expression.get, "a port with a mode other than in can not have a default value")
                    }
                    (new SignalSymbol(identifier, dataType, mode, if (signalDeclaration.isBus) Option(SignalDeclaration.Type.BUS) else None, varIndex, owner, isOptional, isParameter = true), 1)
                  case fileDeclaration: InterfaceFileDeclaration =>
                    if (!dataType.isInstanceOf[FileType]) addError(element.subType, "file type expected")
                    (new FileSymbol(identifier, dataType, varIndex, owner, isOptional, isParameter = true), 1)
                  case constantDeclaration: InterfaceConstantDeclaration =>
                    checkIfNotFileProtectedAccessType(element.subType, dataType)
                    (new ConstantSymbol(identifier, dataType, varIndex, owner, isOptional, isParameter = true), nextIndex)
                  case _ => error("not implemented")
                }
                varIndex += indexChange
                symbol
            }
            val newElement = element match {
              case variableDeclaration: InterfaceVariableDeclaration => variableDeclaration.copy(subType = subType, expression = expression)
              case signalDeclaration: InterfaceSignalDeclaration => signalDeclaration.copy(subType = subType, expression = expression)
              case fileDeclaration: InterfaceFileDeclaration => fileDeclaration.copy(subType = subType)
              case constantDeclaration: InterfaceConstantDeclaration => constantDeclaration.copy(subType = subType, expression = expression)
              case _ => error("not implemented")
            }
            (symbols, newElement)
        }.unzip
        (symbols.flatten, Option(elements))
    }

  def createType(context: Context, subTypeIndication: SubTypeIndication, subTypeName: String = "subtype", isAccessTypeDefinition: Boolean = false): SubTypeIndication = {
    def createEnumerationSubType(range: Range, baseType: EnumerationType, resolutionFunction: Option[FunctionSymbol]): Option[EnumerationType] = range.expressionsOrName match {
      case Left((fromExpression, direction, toExpression)) =>
        def getEnumEntry(expr: Expression): Option[Int] = acceptExpression(expr, baseType, context) match {
          case literal: Literal if (literal.dataType eq baseType) => Some(literal.value.asInstanceOf[Int])
          case _ => addError(expr, "expected a enumeration literal of type %s", baseType.name)
        }

        val low = getEnumEntry(fromExpression).getOrElse(baseType.left)
        val high = getEnumEntry(toExpression).getOrElse(baseType.right)
        direction match {
          case Range.Direction.To => if (low > high) addError(fromExpression, "the left value is bigger then the right value")
          case Range.Direction.Downto => if (low < high) addError(fromExpression, "the left value is smaller then the right value")
        }
        val newList = baseType.elements.zipWithIndex.filter{
          case (_, i) => i >= low && i <= high
        }.unzip._1
        Option(new EnumerationType(subTypeName, newList, baseType.baseType.orElse(Some(baseType)), baseType.owner, resolutionFunction))
      case Right(attributeName) => addError(attributeName, "expected a range in the form from TO/DOWNTO to for a enumeration subtype")
    }

    def createIntegerOrRealSubType[T <: NumericType](sourceRange: Range, baseType: DataType, resolutionFunction: Option[FunctionSymbol]): Option[DataType] = {
      def check[A](lowerBound: A, baseTypeLowerBound: A, upperBound: A, baseTypeUpperBound: A)(implicit numeric: Integral[A]) {
        import numeric.mkOrderingOps
        if (lowerBound < baseTypeLowerBound) addError(sourceRange, "lower bound %s is smaller than the lower bound of the base type %s", lowerBound.toString, baseTypeLowerBound.toString)
        if (upperBound > baseTypeUpperBound) addError(sourceRange, "upper bound %s is greater than the upper bound of the base type %s", upperBound.toString, baseTypeUpperBound.toString)
      }
      val range = checkRange(context, sourceRange)
      val dataType = range.dataType
      if (dataType.getClass eq baseType.getClass) {
        (baseType: @unchecked) match {
          case intBaseType: IntegerType =>
            val (low: Long, high: Long) = calcRangeValues(range)(LongIsIntegral).getOrElse((intBaseType.lowerBound, intBaseType.upperBound))
            val intType = new IntegerType(subTypeName, low.toInt, high.toInt, intBaseType.baseType.orElse(Some(intBaseType)), resolutionFunction) //if this is a subtype of a subtype we want the real base type
            check(intType.lowerBound, intBaseType.lowerBound, intType.upperBound, intBaseType.upperBound)
            Option(intType)
          case realBaseType: RealType =>
            val (low: Double, high: Double) = calcRangeValues(range)(DoubleAsIfIntegral).getOrElse((realBaseType.lowerBound, realBaseType.upperBound))
            val realType = new RealType(subTypeName, low, high, realBaseType.baseType.orElse(Some(realBaseType)), resolutionFunction)
            check(realType.lowerBound, realBaseType.lowerBound, realType.upperBound, realBaseType.upperBound)(DoubleAsIfIntegral)
            Option(realType)
        }
      } else addError(range, "expected a expression of type %s, found %s", baseType.name, dataType.name)
    }
    val baseType = context.findType(subTypeIndication.typeName, isAccessTypeDefinition)
    val resolutionFunction = subTypeIndication.resolutionFunction.flatMap{
      resolutionFunction => context.findSymbol(resolutionFunction, classOf[ListOfSubprograms]).flatMap{
        listOfSubprograms => context.findFunctionInList(listOfSubprograms, Seq(UnconstrainedArrayType("", baseType, Seq(UnconstrainedRangeType(SymbolTable.naturalType)))), baseType).orElse{
          addError(resolutionFunction, "no resolution function found with the right signature\n a resolution function must have a single input parameter of" +
            " class constant that is a one-dimensional, unconstrained array whose element type is that of the resolved signal. \nThe type of the return value of the function must also be that of the signal.")
        }
      }
    }
    baseType match {
      case IncompleteType =>
        if (isAccessTypeDefinition) subTypeIndication.constraint.foreach(constraint => addError(subTypeIndication.typeName, "can not use an incomplete type with constraints"))
        subTypeIndication.copy(constraint = None, dataType = baseType)
      case _ => subTypeIndication.constraint match {
        case None =>
          val dataType = if (subTypeName == "subtype" && resolutionFunction.isEmpty) baseType
          else baseType match {
            case i: IntegerType => IntegerType(subTypeName, i.left, i.right, i.baseType.orElse(Some(i)), resolutionFunction)
            case r: RealType => RealType(subTypeName, r.left, r.right, r.baseType.orElse(Some(r)), resolutionFunction)
            case e: EnumerationType => EnumerationType(subTypeName, e.elements, e.baseType.orElse(Some(e)), e.owner, resolutionFunction)
            case _ =>
              addError(subTypeIndication.typeName, "expected a integer,real or enumeration type")
              baseType
          }
          subTypeIndication.copy(dataType = dataType)
        case Some(constraint) => constraint match {
          case Left(range) =>
            val dataType = baseType match {
              case _: IntegerType | _: RealType => createIntegerOrRealSubType(range, baseType, resolutionFunction)
              case e: EnumerationType => createEnumerationSubType(range, e, resolutionFunction)
              case _: AccessType =>
              //The only form of constraint that is allowed after the name of an access type in a subtype indication is an index constraint
                addError(range, "you can not create a access subtype with a range constraint")
              case _ => addError(subTypeIndication.typeName, "expected a integer,real or enumeration type")
            }
            subTypeIndication.copy(constraint = Option(Left(range)), dataType = dataType.getOrElse(NoType))
          case Right(arrayConstraint) => baseType match {
            case unconstrainedArrayType: UnconstrainedArrayType =>
              val discreteRanges = arrayConstraint.map(checkDiscreteRange(context, _))
              subTypeIndication.copy(constraint = Option(Right(discreteRanges)), dataType = new ConstrainedArrayType(unconstrainedArrayType.name, unconstrainedArrayType.elementType, discreteRanges.map(_.dataType)))
            case _ =>
              addError(subTypeIndication.typeName, "you can not use a array constraint for non array subtypes")
              subTypeIndication.copy(dataType = NoType)
          }
        }
      }
    }
  }

  def visitAliasDeclaration(aliasDeclaration: AliasDeclaration, context: Context): ReturnType = {
    val (identifiers, rest) = aliasDeclaration.name.parts.span(_.isInstanceOf[Name.SelectedPart])
    val identifier = new SelectedName(aliasDeclaration.name.identifier +: identifiers.map(_.asInstanceOf[Name.SelectedPart].identifier))

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
      case _: LabelSymbol => addError(aliasDeclaration.name, "aliases for labels are not allowed")
      case _ =>
      //non-object alias
        for (subType <- aliasDeclaration.subType) addError(subType, "a subtype indication is not allowed for a non object alias")
        rest.headOption.foreach(addError(_, "illegal name part in non object alias"))
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
    (aliasDeclaration, context.insertSymbol(context.find(identifier).flatMap(createAliasSymbol)))
  }

  def visitArchitectureDeclaration(architectureDeclaration: ArchitectureDeclaration, owner: Symbol, context: Context): ReturnType =
    SymbolTable.getScopesFromFile(configuration.libraryOutputDirectory + architectureDeclaration.entityName) match {
      case Some(scopes) =>
        val newContext = context.copy(symbolTable = new SymbolTable(scopes ++ context.symbolTable.scopes))
        val entitySymbol = newContext.findSymbol(architectureDeclaration.entityName, classOf[EntitySymbol]).get
        if (entitySymbol.owner != owner) addError(architectureDeclaration, "a architecture declaration must be placed into the same design library as the entity declaration")
        val symbol = new ArchitectureSymbol(architectureDeclaration.identifier, entitySymbol, owner)
        val (declarativeItems, c1) = acceptDeclarativeItems(architectureDeclaration.declarativeItems, symbol, insertConcurrentStatementsLabels(newContext, architectureDeclaration.concurrentStatements, symbol))
        val (concurrentStatements, _) = acceptNodes(architectureDeclaration.concurrentStatements, symbol, c1)
        (architectureDeclaration.copy(declarativeItems = declarativeItems, concurrentStatements = concurrentStatements, symbol = symbol), context)
      case None =>
        addError(architectureDeclaration.entityName, "entity %s not found", architectureDeclaration.entityName)
        (architectureDeclaration, context)
    }

  def visitConfigurationDeclaration(configurationDeclaration: ConfigurationDeclaration, owner: Symbol, context: Context): ReturnType = {
    val configuration = new ConfigurationSymbol(configurationDeclaration.identifier, owner)
    context.findSymbol(configurationDeclaration.entityName, classOf[DesignEntity]) match {
      case Some(designEntity) =>
        if (designEntity.owner != owner) addError(configurationDeclaration.entityName, "a configuration declaration must be placed into the same design library as the entity declaration")
        val (declarativeItems, newContext) = acceptDeclarativeItems(configurationDeclaration.declarativeItems, configuration, context.insertSymbol(configuration))
        checkBlockConfiguration(newContext, configurationDeclaration.blockConfiguration)
        require(newContext.symbolTable.depth == 2)
        (configurationDeclaration.copy(declarativeItems = declarativeItems, symbol = configuration), context)
      case _ => (configurationDeclaration, context)
    }
  }

  def visitAssertionStatement(assertStmt: AssertionStatement, context: Context): ReturnType = {
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
        val constantSymbol = new ConstantSymbol(Identifier("$attribute" + context.varIndex), attributeSymbol.dataType, context.varIndex, owner)
        if (attributeSpec.entityClass == EntityClass.ARCHITECTURE || attributeSpec.entityClass == EntityClass.ENTITY || attributeSpec.entityClass == EntityClass.CONFIGURATION) checkIsStaticExpression(expression)

        (attributeSpec.entityList match {
          case Left(entityDesignatorList) => entityDesignatorList.flatMap{
            case (identifier, signatureOption) =>
              context.findSymbol(identifier, entityClass) match {
                case Some(symbol) => symbol match {
                  case listOfSubprograms: ListOfSubprograms => signatureOption match {
                    case Some(signature) =>
                      (signature.returnType, attributeSpec.entityClass) match {
                        case (None, EntityClass.FUNCTION) => addError(signature, "a signature for a function needs a return type")
                        case (Some(returnType), EntityClass.PROCEDURE) => addError(returnType, "a signature for a procedure can not have a return type")
                        case _ => findSubprogramFromSignature(context, listOfSubprograms, signature)
                      }
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
                    case _ => Option(symbol)
                  }
                  case _ => None
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
                  case _ => None
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
                      symbol.attributes += (attributeSpec.identifier.text -> new ForeignAttributeSymbol(attributeSpec.identifier, symbol, Left((className.replace('.', '/'), methodName))))
                    case Array(className, methodName, parameterTypes) =>
                      symbol.attributes += (attributeSpec.identifier.text -> new ForeignAttributeSymbol(attributeSpec.identifier, symbol, Right((className.replace('.', '/'), methodName, parameterTypes))))
                    case _ => addError(expression, """expected a string of type "className methodName [parameterTypes]" """)
                  }
                  case _ => addError(expression, "string literal expected")
                }
                None
              } else {
                symbol.attributes += (attributeSpec.identifier.text -> new UserDefinedAttributeSymbol(attributeSpec.identifier, expression.dataType, attributeSymbol, symbol, constantSymbol))
                Some(true)
              }
            }
        } match {
          case Seq() => None
          case _ => Option((constantSymbol, expression))
        }
    } match {
      case Some((constantSymbol, expression)) => (ConstantDeclaration(Position.NoPosition, Seq(), null, Option(expression), Seq(constantSymbol)), context.copy(varIndex = context.varIndex + getNextIndex(constantSymbol.dataType)))
      case None => (attributeSpec, context)
    }

  def visitBlockStatement(blockStmt: BlockStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(blockStmt.label, blockStmt.endLabel)

    val (generics, genericInterfaceList) = createSymbolsFromInterfaceList(context, blockStmt.genericInterfaceList, owner)
    val (ports, portInterfaceList) = createSymbolsFromInterfaceList(context, blockStmt.portInterfaceList, owner)
    val newContext = context.insertSymbols(generics).insertSymbols(ports)

    // TODO
    val genericAssociationList = checkAssociationList(newContext, blockStmt.genericAssociationList, generics, blockStmt)
    val portAssociationList = checkAssociationList(newContext, blockStmt.portAssociationList, ports, blockStmt)
    val guardExpression = checkExpressionOption(newContext, blockStmt.guardExpression, SymbolTable.booleanType)

    val cx = blockStmt.guardExpression.map(expr => newContext.insertSymbol(new SignalSymbol(Identifier(expr.position, "guard"), SymbolTable.booleanType, InterfaceList.Mode.LINKAGE, None, -1, owner))).getOrElse(newContext)
    val (declarativeItems, c) = acceptDeclarativeItems(blockStmt.declarativeItems, owner, insertConcurrentStatementsLabels(cx.copy(varIndex = ports.last.index + 1), blockStmt.concurrentStatements, owner))
    val (concurrentStatements, _) = acceptNodes(blockStmt.concurrentStatements, owner, c)

    (blockStmt.copy(genericInterfaceList = genericInterfaceList, genericAssociationList = genericAssociationList, portInterfaceList = portInterfaceList, portAssociationList = portAssociationList,
      guardExpression = guardExpression, declarativeItems = declarativeItems, concurrentStatements = concurrentStatements), context)
  }

  def visitCaseStatement(caseStmt: CaseStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(caseStmt.label, caseStmt.endLabel)
    val caseStmtExpression = checkExpression(context, caseStmt.expression, NoType)
    val containsOthers = caseStmt.alternatives.exists(when => when.choices.exists(choice => choice.isOthers))
    val lastAlternative = caseStmt.alternatives.last

    val alternativesMapped = caseStmt.alternatives.map{
      when =>
        val choices = when.choices.map{
          choice => choice.rangeOrExpressionOrIdentifier match {
            case Some(rangeOrExpressionOrIdentifier) => rangeOrExpressionOrIdentifier match {
              case First(range) => error("not implemented")
              case Second(expression) => new Choices.Choice(position = choice.position, Some(Second(checkExpression(context, expression, caseStmtExpression.dataType))))
              case Third(identifier) => error("not implemented")
            }
            case None =>
              if (when ne lastAlternative) {
                addError(choice, "the others case must be the last case")
              }
              if (when.choices.size > 1) {
                addError(choice, "the others case must be alone")
              }
              choice
          }
        }
        val (statements, _) = acceptNodes(when.sequentialStatements, owner, context)
        new CaseStatement.When(choices = choices, statements)
    }

    val alternatives = if (!containsOthers) {
      //add default error handling
      val caseError = new CaseStatement.When(Seq(new Choices.Choice(Position.NoPosition, None)), List(ThrowStatement(lastAlternative.choices.head.position.addLineOffset(1), "case fall through")))
      alternativesMapped :+ caseError
    } else {
      alternativesMapped
    }

    caseStmtExpression.dataType match {
      case _: EnumerationType | _: IntegerType =>
        val keys = alternatives.flatMap(when =>
          when.choices.collect(_ match {
            case choice: Choices.Choice if (!choice.isOthers) => choice.rangeOrExpressionOrIdentifier.get match {
              case Second(expression) => StaticExpressionCalculator.calcValue(expression)(math.Numeric.IntIsIntegral)
              case _ => error("not implemented")
            }
          })).flatten

        (caseStmt.copy(expression = caseStmtExpression, keys = keys, alternatives = alternatives), context)
      case _ =>
        def toExpr(choices: Seq[Choices.Choice]): Expression = {
          val tmpList: Seq[Expression] = choices.map{
            choice =>
              choice.rangeOrExpressionOrIdentifier match {
                case Some(rangeOrExpressionOrIdentifier) => rangeOrExpressionOrIdentifier match {
                  case First(discreteRange) => error("not implemented")
                  case Second(expression) => Relation(choice.position, expression, Relation.Operator.EQ, caseStmtExpression)
                  case Third(identifier) => error("not implemented")
                }
                case None => NoExpression
              }
          }
          return checkExpression(context, tmpList.reduceLeft((r1, r2) => LogicalExpression(r1.position, r1, LogicalExpression.Operator.OR, r2)), SymbolTable.booleanType)
        }
        val ifThenList = alternatives.init.map(alternative => new IfStatement.IfThenPart(toExpr(alternative.choices), alternative.sequentialStatements))
        val ifStmt = IfStatement(caseStmt.position, None, ifThenList, Option(alternatives.last.sequentialStatements), None)
        (ifStmt, context)
    }
  }

  def visitComponentDeclaration(componentDeclaration: ComponentDeclaration, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiers(componentDeclaration.identifier, componentDeclaration.endIdentifier)
    val component = new ComponentSymbol(componentDeclaration.identifier, Seq(), Seq(), owner)
    val (generics, genericInterfaceList) = createSymbolsFromInterfaceList(context, componentDeclaration.genericInterfaceList, component)
    val (ports, portInterfaceList) = createSymbolsFromInterfaceList(context, componentDeclaration.portInterfaceList, component)
    component.generics = generics
    component.ports = ports
    (componentDeclaration.copy(genericInterfaceList = genericInterfaceList, portInterfaceList = portInterfaceList, symbol = component), context.insertSymbol(component))
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

  def expressionToSensitivityList(expr: Expression): Seq[SignalSymbol] = Seq()

  //TODO

  def toProcessStatement(statement: SequentialStatement, sensitivityList: Seq[SignalSymbol], label: Option[Identifier], isPostponed: Boolean, owner: Symbol, context: Context): ReturnType = {
    val waitStatement = WaitStatement(statement.position, label = None, signals = None, untilCondition = None, forExpression = None, sensitivitySignalList = sensitivityList)
    visitProcessStatement(ProcessStatement(statement.position, label = label, isPostponed = isPostponed, sensitivityList = None, declarativeItems = Seq(),
      sequentialStatements = Seq(statement, waitStatement), endLabel = None), owner, context)
  }

  def visitConcurrentAssertionStatement(concurrentAssertStmt: ConcurrentAssertionStatement, owner: Symbol, context: Context): ReturnType = {
    val assertStmt = AssertionStatement(concurrentAssertStmt.position, None, concurrentAssertStmt.condition, concurrentAssertStmt.reportExpression, concurrentAssertStmt.severityExpression)
    toProcessStatement(assertStmt, expressionToSensitivityList(concurrentAssertStmt.condition), concurrentAssertStmt.label, concurrentAssertStmt.isPostponed, owner, context)
  }

  def visitConcurrentProcedureCallStatement(concurrentProcedureCallStmt: ConcurrentProcedureCallStatement, owner: Symbol, context: Context): ReturnType = {
    val procedureCallStmt = ProcedureCallStatement(None, concurrentProcedureCallStmt.name, concurrentProcedureCallStmt.parameterAssociation)
    val sensitivityList = concurrentProcedureCallStmt.parameterAssociation.map{
      _.elements.flatMap{
        _.actualPart.fold(expressionToSensitivityList(_), id => Seq[SignalSymbol]())
      }
    }.toList.flatten
    toProcessStatement(procedureCallStmt, sensitivityList, concurrentProcedureCallStmt.label, concurrentProcedureCallStmt.isPostponed, owner, context)
  }

  def visitConcurrentSignalAssignmentStatement(signalAssignStmt: ConcurrentSignalAssignmentStatement, owner: Symbol, context: Context): ReturnType = {
    require(!signalAssignStmt.isGuarded)

    def waveTransform(waveform: Waveform): SequentialStatement =
      if (waveform.isUnaffected) {
        NullStatement(waveform.position, label = None)
      } else {
        SimpleSignalAssignmentStatement(waveform.position, None, signalAssignStmt.target, signalAssignStmt.delay, waveform)
      }

    val newStatement = signalAssignStmt match {
      case conditionalSignalAssignment: ConcurrentConditionalSignalAssignment => conditionalSignalAssignment.alternatives match {
        case Seq(x) if (x.condition == None) => waveTransform(x.waveform)
        case _ =>
          val last = conditionalSignalAssignment.alternatives.last
          val mapper = (alternative: ConcurrentConditionalSignalAssignment.When) => new IfStatement.IfThenPart(alternative.condition.get, Seq(waveTransform(alternative.waveform)))
          val (ifThenList, elseSequentialStatements) = if (last.condition == None) {
            (conditionalSignalAssignment.alternatives.init.map(mapper), Some(Seq(waveTransform(last.waveform))))
          }
          else {
            (conditionalSignalAssignment.alternatives.map(mapper), None)
          }
          IfStatement(conditionalSignalAssignment.position, label = None, ifThenList = ifThenList, elseSequentialStatements = elseSequentialStatements, endLabel = None)
      }
      case selectedSignalAssignment: ConcurrentSelectedSignalAssignment =>
        val caseStmtAlternatives = selectedSignalAssignment.alternatives.map(alternative => new CaseStatement.When(alternative.choices, Seq(waveTransform(alternative.waveform))))
        CaseStatement(selectedSignalAssignment.position, label = None, expression = selectedSignalAssignment.expression, alternatives = caseStmtAlternatives, endLabel = None)
    }
    toProcessStatement(newStatement, Seq(), signalAssignStmt.label, signalAssignStmt.isPostponed, owner, context)
  }

  def visitConstantDeclaration(constantDeclaration: ConstantDeclaration, owner: Symbol, context: Context): ReturnType = {
    val subType = createType(context, constantDeclaration.subType)
    checkIfNotFileProtectedAccessType(constantDeclaration.subType, subType.dataType)

    if (constantDeclaration.value.isEmpty && !owner.isInstanceOf[PackageSymbol])
      addError(constantDeclaration, "deferred constant are only in package header allowed")

    val value = checkExpressionOption(context, constantDeclaration.value, subType.dataType)
    val multiplier = getNextIndex(subType.dataType) //+2 for real and physical, +1 for all other constants
    val symbols = constantDeclaration.identifiers.zipWithIndex.map{
      case (identifier, i) => context.symbolTable.findInCurrentScope(identifier.text, classOf[ConstantSymbol]) match {
        case Some(constantSymbol) if (constantSymbol.isDefined) =>
          if (constantSymbol.dataType != subType.dataType) addError(constantDeclaration.subType, "expected a expression of type %s, found %s", constantSymbol.dataType.name, subType.dataType.name)
          constantSymbol.copy(isDefined = constantDeclaration.value.isDefined)
        case _ =>
          new ConstantSymbol(identifier, subType.dataType, context.varIndex + (i * multiplier), owner, false, constantDeclaration.value.isDefined, constantDeclaration.value.isEmpty)
      }
    }
    val newNode = constantDeclaration.copy(value = value, subType = subType, symbols = symbols)
    val newContext = context.insertSymbols(symbols).copy(varIndex = context.varIndex + (constantDeclaration.identifiers.size * multiplier) + 1)
    (newNode, newContext)
  }

  def visitDesignUnit(designUnit: DesignUnit, owner: Symbol, context: Context): ReturnType = {
    val libraryUnitOption = designUnit.libraryUnit.map{
      unit =>
        checkIdentifiers(unit.identifier, unit.endIdentifier)
        val workSymbol = new LibrarySymbol(Identifier("work"), new DirectoryLibraryArchive(configuration.libraryOutputDirectory))
        val librarySymbols = ((if ("std" == configuration.designLibrary) designUnit.libraries else Identifier(designUnit.position, "std") +: designUnit.libraries).distinct.flatMap{
          id =>
            try {
              val archive = new JarFileLibraryArchive(configuration.libraryDirectory + id.text + ".jar")
              require(archive.jarFile != null) //do not remove, this creates the lazy variable jarFile, and throws a FileNotFoundException if the jar file does not exist
              Some(new LibrarySymbol(id, archive))
            } catch {
              case _: java.io.FileNotFoundException => addError(id, "library %s not found", id)
            }
        })

        val newContext = if ("standard" == unit.identifier.text) {
          context.openScope
        } else {
          // set up universe
          val c = visitUseClause(UseClause(Position.NoPosition, Seq(new SelectedName(Seq(Identifier("std"), Identifier("standard"), Identifier("all"))))), context.openScope.insertSymbol(librarySymbols(0)))._2.openScope.insertSymbols(workSymbol +: librarySymbols)
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

        val (useClauses, c1) = acceptNodes(designUnit.useClauses, owner, newContext)
        val (node, c2) = acceptNode(unit, if (configuration.designLibrary == "work") workSymbol else new LibrarySymbol(Identifier(configuration.designLibrary), new DirectoryLibraryArchive(configuration.libraryOutputDirectory)), c1)
        val libraryUnit = node.asInstanceOf[LibraryUnit]
        import java.io.{File, ObjectOutputStream, FileOutputStream}

        if (libraryUnit.symbol != null) {
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
    val entity = new EntitySymbol(entityDeclaration.identifier, Seq(), Seq(), owner)
    val (generics, genericInterfaceList) = createSymbolsFromInterfaceList(context, entityDeclaration.genericInterfaceList, entity)
    val (ports, portInterfaceList) = createSymbolsFromInterfaceList(context, entityDeclaration.portInterfaceList, entity)
    entity.generics = generics
    entity.ports = ports
    val (declarativeItems, c) = acceptDeclarativeItems(entityDeclaration.declarativeItems, entity,
      insertConcurrentStatementsLabels(context.insertSymbols(entity +: (entity.generics ++ entity.ports)), entityDeclaration.concurrentStatements, entity))
    require(c.symbolTable.depth == 2)
    val (concurrentStatements, c2) = acceptNodes(entityDeclaration.concurrentStatements, entity, c)
    require(c2.symbolTable.depth == 2)
    for (node <- concurrentStatements) {
      val process = node.asInstanceOf[ProcessStatement]
      if (!process.symbol.isPassive) addError(process, "this concurrent statement is not passive, and only passive statement are allowed in a entitySymbol declaration")
    }
    c2.symbolTable.writeToFile(configuration.libraryOutputDirectory + entityDeclaration.identifier.text)
    (entityDeclaration.copy(genericInterfaceList = genericInterfaceList, portInterfaceList = portInterfaceList, declarativeItems = declarativeItems, concurrentStatements = concurrentStatements, symbol = entity), context)
  }

  def visitExitStatement(exitStmt: ExitStatement, context: Context): ReturnType = {
    val condition = checkExpressionOption(context, exitStmt.condition, SymbolTable.booleanType)
    val loopStatement = checkLoopLabel(context, exitStmt.loopLabel, exitStmt, "exit")
    (exitStmt.copy(condition = condition, loopStatement = loopStatement), context)
  }

  def visitFileDeclaration(fileDeclaration: FileDeclaration, owner: Symbol, context: Context): ReturnType = {
    val openKind = checkExpressionOption(context, fileDeclaration.openKind, SymbolTable.fileOpenKind)
    val logicalName = checkExpressionOption(context, fileDeclaration.logicalName, SymbolTable.stringType)
    val subType = createType(context, fileDeclaration.subType)
    if (!subType.dataType.isInstanceOf[FileType]) addError(fileDeclaration.subType, "expected a file type")
    val symbols = fileDeclaration.identifiers.zipWithIndex.map{
      case (identifier, i) => new FileSymbol(identifier, subType.dataType, context.varIndex + i, owner)
    }
    val newNode = fileDeclaration.copy(openKind = openKind, logicalName = logicalName, subType = subType, symbols = symbols)
    val newContext = context.insertSymbols(symbols).copy(varIndex = context.varIndex + fileDeclaration.identifiers.size + 1)
    (newNode, newContext)
  }

  def visitForGenerateStatement(forGenerateStmt: ForGenerateStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(forGenerateStmt.label, forGenerateStmt.endLabel)
    val discreteRange = checkDiscreteRange(context, forGenerateStmt.discreteRange)
    val symbol = new ConstantSymbol(forGenerateStmt.loopIdentifier, discreteRange.dataType.elementType, -1, owner)
    val (declarativeItems, c1) = acceptDeclarativeItems(forGenerateStmt.declarativeItems, owner, insertConcurrentStatementsLabels(context.openScope, forGenerateStmt.concurrentStatements, owner).insertSymbol(symbol))
    val (concurrentStatements, _) = acceptNodes(forGenerateStmt.concurrentStatements, owner, c1)
    (forGenerateStmt.copy(declarativeItems = declarativeItems, concurrentStatements = concurrentStatements), context)
  }

  def visitForStatement(forStmt: ForStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(forStmt.label, forStmt.endLabel)
    val discreteRange = checkDiscreteRange(context, forStmt.discreteRange)
    val symbol = new ConstantSymbol(forStmt.identifier, discreteRange.dataType.elementType, context.varIndex + 1, owner)
    val (sequentialStatements, _) = acceptNodes(forStmt.sequentialStatements, owner, context.openScope.insertSymbol(symbol).insertLoopLabel(forStmt.label, forStmt.position).copy(varIndex = context.varIndex + 1))
    (forStmt.copy(sequentialStatements = sequentialStatements, symbol = symbol, discreteRange = discreteRange), context)
  }

  def visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, owner: Symbol, context: Context): ReturnType = {
    val returnType = context.findType(functionDeclaration.returnType)
    val function = new FunctionSymbol(getMangledName(functionDeclaration.identifier), Seq(), returnType, owner, functionDeclaration.isPure, false)
    val (parameters, parameterInterfaceList) = createSymbolsFromInterfaceList(context, functionDeclaration.parameterInterfaceList, owner)
    function.parameters = parameters
    (functionDeclaration.copy(parameterInterfaceList = parameterInterfaceList, symbol = function), context.insertSymbol(function))
  }

  def visitFunctionDefinition(functionDefinition: FunctionDefinition, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiers(functionDefinition.identifier, functionDefinition.endIdentifier)
    val returnType = context.findType(functionDefinition.returnType)
    val function = new FunctionSymbol(getMangledName(functionDefinition.identifier), Seq(), returnType, owner, functionDefinition.isPure, true)
    val (parameters, parameterInterfaceList) = createSymbolsFromInterfaceList(context, functionDefinition.parameterInterfaceList, function)
    function.parameters = parameters

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
      insertSequentialStatementsLabels(tmp.openScope.insertSymbols(function.parameters), functionDefinition.sequentialStatements, function).copy(varIndex = function.parameters.lastOption.map(_.index + 1).getOrElse(getStartIndex(owner))))
    val (sequentialStatements, c2) = acceptNodes(functionDefinition.sequentialStatements, function, c1)
    val newSequentialStatements = sequentialStatements match {
      case Seq(AssertionStatement(pos, _, _, _, _)) =>
      //corner case for functions with only a assert statement, throw statement will make the JVM verifier happy, that this function does not return a value
        sequentialStatements :+ ThrowStatement(pos, "function fall through")
      case _ => sequentialStatements
    }
    (functionDefinition.copy(parameterInterfaceList = parameterInterfaceList, declarativeItems = declarativeItems, sequentialStatements = newSequentialStatements,
      symbol = function, localSymbols = c2.closeScope()), tmp)
  }

  def visitGroupDeclaration(groupDeclaration: GroupDeclaration, owner: Symbol, context: Context): ReturnType = {
    val symbol = new GroupSymbol(groupDeclaration.identifier, owner)
    context.findSymbol(groupDeclaration.groupTemplateName, classOf[GroupTemplateSymbol]) match {
      case _ => error("not implemented") //TODO
    }
    (groupDeclaration, context.insertSymbol(symbol))
  }

  def visitGroupTemplateDeclaration(groupTemplateDeclaration: GroupTemplateDeclaration, owner: Symbol, context: Context): ReturnType = {
    val lastElement = groupTemplateDeclaration.entries.last
    val items = groupTemplateDeclaration.entries.init.map{
      element =>
        if (element.isInfinite) addError(groupTemplateDeclaration, "you can not use infinite elements, it its only allowed at the last element")
        element.entityClass
    }
    val groupTemplateSymbol = new GroupTemplateSymbol(groupTemplateDeclaration.identifier, items :+ lastElement.entityClass, lastElement.isInfinite, owner)
    (groupTemplateDeclaration, context.insertSymbol(groupTemplateSymbol))
  }

  def visitIfGenerateStatement(ifGenerateStmt: IfGenerateStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(ifGenerateStmt.label, ifGenerateStmt.endLabel)
    val condition = checkExpression(context, ifGenerateStmt.condition, SymbolTable.booleanType)
    //TODO ifGenerateStmt.value=StaticExpressionCalculator.calcBooleanValue(condition)
    val (declarativeItems, c1) = acceptDeclarativeItems(ifGenerateStmt.declarativeItems, owner, insertConcurrentStatementsLabels(context, ifGenerateStmt.concurrentStatements, owner))
    val (concurrentStatements, _) = acceptNodes(ifGenerateStmt.concurrentStatements, owner, c1)
    (ifGenerateStmt.copy(declarativeItems = declarativeItems, concurrentStatements = concurrentStatements), context)
  }

  def visitIfStatement(ifStmt: IfStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(ifStmt.label, ifStmt.endLabel)
    val ifThenList = ifStmt.ifThenList.map(ifThen => new IfStatement.IfThenPart(checkExpression(context, ifThen.condition, SymbolTable.booleanType), acceptNodes(ifThen.sequentialStatements, owner, context)._1))
    val (elseSequentialStatements, _) = acceptNodesOption(ifStmt.elseSequentialStatements, owner, context)
    (ifStmt.copy(ifThenList = ifThenList, elseSequentialStatements = elseSequentialStatements), context)
  }

  def visitLoopStatement(loopStmt: LoopStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(loopStmt.label, loopStmt.endLabel)
    val (sequentialStatements, _) = acceptNodes(loopStmt.sequentialStatements, owner, context.insertLoopLabel(loopStmt.label, loopStmt.position))
    (loopStmt.copy(sequentialStatements = sequentialStatements), context)
  }

  def visitNextStatement(nextStmt: NextStatement, context: Context): ReturnType = {
    val condition = checkExpressionOption(context, nextStmt.condition, SymbolTable.booleanType)
    val loopStatement = checkLoopLabel(context, nextStmt.loopLabel, nextStmt, "next")
    (nextStmt.copy(condition = condition, loopStatement = loopStatement), context)
  }

  def visitPackageBodyDeclaration(packageBodyDeclaration: PackageBodyDeclaration, owner: Symbol, context: Context): ReturnType =
    SymbolTable.getScopesFromFile(configuration.libraryOutputDirectory + packageBodyDeclaration.identifier) match {
      case Some(scopes) =>
        val symbol = new PackageSymbol(packageBodyDeclaration.identifier, Map(), true, owner)
        val (declarativeItems, newContext) = acceptDeclarativeItems(packageBodyDeclaration.declarativeItems, symbol, context.copy(symbolTable = new SymbolTable(scopes ++ context.symbolTable.scopes)))
        require(newContext.symbolTable.depth == 3)
        newContext.closeScope().foreach{
          _ match {
            case constantSymbol: ConstantSymbol => if (!constantSymbol.isDefined) addError(packageBodyDeclaration, "the deferred constant %s is not fully declared", constantSymbol.name)
            case _ =>
          }
        }
        (packageBodyDeclaration.copy(declarativeItems = declarativeItems, symbol = symbol), context)
      case None =>
        addError(packageBodyDeclaration.identifier, "package declaration %s not found", packageBodyDeclaration.identifier)
        (packageBodyDeclaration, context)
    }

  def visitPackageDeclaration(packageDeclaration: PackageDeclaration, owner: Symbol, context: Context): ReturnType = {
    val packageSymbol = new PackageSymbol(packageDeclaration.identifier, Map(), false, owner)
    val (declarativeItems, newContext) = acceptDeclarativeItems(packageDeclaration.declarativeItems, packageSymbol, context.insertSymbol(packageSymbol))
    require(newContext.symbolTable.depth <= 2)
    packageSymbol.localSymbols = newContext.closeScope.map(symbol => (symbol.name, symbol)).toMap
    newContext.symbolTable.writeToFile(configuration.libraryOutputDirectory + packageDeclaration.identifier.text)
    (packageDeclaration.copy(declarativeItems = declarativeItems, symbol = packageSymbol), context)
  }

  def visitProcedureCallStatement(procedureCallStmt: ProcedureCallStatement, context: Context): ReturnType =
    context.findSymbol(procedureCallStmt.name, classOf[ListOfSubprograms]) match {
      case Some(listOfSubprograms) =>
        val (procedureSymbol, parameterAssociation) = checkSubprogramAssociationList(context, procedureCallStmt.parameterAssociation, listOfSubprograms, None, procedureCallStmt)
        (procedureCallStmt.copy(parameterAssociation = parameterAssociation, symbol = procedureSymbol.orNull.asInstanceOf[ProcedureSymbol]), context)
      case None => (procedureCallStmt, context)
    }

  def visitProcedureDeclaration(procedureDeclaration: ProcedureDeclaration, owner: Symbol, context: Context): ReturnType = {
    val procedure = new ProcedureSymbol(procedureDeclaration.identifier, Seq(), owner, false, false)
    val (parameters, parameterInterfaceList) = createSymbolsFromInterfaceList(context, procedureDeclaration.parameterInterfaceList, procedure)
    procedure.parameters = parameters
    (procedureDeclaration.copy(parameterInterfaceList = parameterInterfaceList, symbol = procedure), context.insertSymbol(procedure))
  }

  def visitProcedureDefinition(procedureDefinition: ProcedureDefinition, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiers(procedureDefinition.identifier, procedureDefinition.endIdentifier)
    val procedure = new ProcedureSymbol(procedureDefinition.identifier, Seq(), owner, true, true)
    val (parameters, parameterInterfaceList) = createSymbolsFromInterfaceList(context, procedureDefinition.parameterInterfaceList, procedure)
    procedure.parameters = parameters
    if (owner.isInstanceOf[TypeSymbol]) {
      //this is a method in a protected Type
      procedure.parameters.foreach(p => checkIfNotFileAccessType(p.identifier, p.dataType))
    }
    val tmp = context.insertSymbol(procedure)
    val (declarativeItems, c1) = acceptDeclarativeItems(procedureDefinition.declarativeItems, procedure,
      insertSequentialStatementsLabels(tmp.openScope.insertSymbols(procedure.parameters), procedureDefinition.sequentialStatements, procedure).copy(varIndex = procedure.parameters.lastOption.map(_.index + 1).getOrElse(getStartIndex(owner))))
    val (sequentialStatements, c2) = acceptNodes(procedureDefinition.sequentialStatements, procedure, c1)
    procedure.isPassive = isPassive(sequentialStatements)
    (procedureDefinition.copy(parameterInterfaceList = parameterInterfaceList, declarativeItems = declarativeItems, sequentialStatements = sequentialStatements,
      symbol = procedure, localSymbols = c2.closeScope()), tmp)
  }

  def isPassive(list: Seq[SequentialStatement]): Boolean = list.forall{
    _ match {
      case loopStmt: AbstractLoopStatement => isPassive(loopStmt.sequentialStatements)
      case caseStmt: CaseStatement => caseStmt.alternatives.forall(alternative => isPassive(alternative.sequentialStatements))
      case ifStmt: IfStatement => ifStmt.ifThenList.forall(ifThen => isPassive(ifThen.sequentialStatements)) && ifStmt.elseSequentialStatements.map(isPassive).getOrElse(true)
      case _: SignalAssignmentStatement => false
      case procedureCallStmt: ProcedureCallStatement => if (procedureCallStmt.symbol != null) procedureCallStmt.symbol.isPassive else true
      case _ => true
    }
  }

  @tailrec
  def toLinearList(sequentialStatements: Seq[SequentialStatement], buffer: Buffer[SequentialStatement] = new Buffer[SequentialStatement]()): Seq[SequentialStatement] =
    sequentialStatements match {
      case Seq() => buffer.result
      case Seq(statement, xs@_*) => statement match {
        case loopStmt: AbstractLoopStatement =>
          buffer += loopStmt
          toLinearList(loopStmt.sequentialStatements, buffer)
        case caseStmt: CaseStatement =>
          buffer += caseStmt
          buffer ++= (caseStmt.alternatives.flatMap(alternative => toLinearList(alternative.sequentialStatements)))
        case ifStmt: IfStatement =>
          buffer += ifStmt
          buffer ++= (ifStmt.ifThenList.flatMap(ifThen => toLinearList(ifThen.sequentialStatements)))
          buffer ++= (ifStmt.elseSequentialStatements.map(toLinearList(_)).flatten)
        case statement => buffer += statement
      }
      toLinearList(xs, buffer)
    }

  def visitProcessStatement(processStatement: ProcessStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(processStatement.label, processStatement.endLabel)

    val name = processStatement.label.getOrElse(Identifier(processStatement.position, "process_" + processStatement.position.line))
    val symbol = new ProcessSymbol(name, owner, false, processStatement.isPostponed)

    val (declarativeItems, newContext) = acceptDeclarativeItems(processStatement.declarativeItems, symbol, insertSequentialStatementsLabels(context.openScope, processStatement.sequentialStatements, symbol))
    val (newSequentialStatementList, c2) = acceptNodes(processStatement.sequentialStatements, symbol, newContext)

    val sequentialStatements = newSequentialStatementList ++ processStatement.sensitivityList.map{
      sensitivityList =>
        toLinearList(newSequentialStatementList).foreach{
          _ match {
            case waitStatement: WaitStatement => addError(waitStatement, "a process with a sensitivity list can not contain an exlicit wait statement")
            case procedureCallStatement: ProcedureCallStatement =>
              if (procedureCallStatement.symbol != null && !procedureCallStatement.symbol.isPassive)
                addError(procedureCallStatement, "a process with a sensitivity list can not call a procedure with a wait statement")
            case _ =>
          }
        }
        visitWaitStatement(WaitStatement(position = newSequentialStatementList.last.position.addLineOffset(1), label = None, signals = Some(sensitivityList), untilCondition = None, forExpression = None), context)._1.asInstanceOf[WaitStatement]
    }.toList

    symbol.isPassive = isPassive(newSequentialStatementList)
    (processStatement.copy(declarativeItems = declarativeItems, sequentialStatements = sequentialStatements, localSymbols = c2.closeScope(), symbol = symbol), context)
  }

  def visitReportStatement(reportStmt: ReportStatement, context: Context): ReturnType = {
    val reportExpression = checkExpression(context, reportStmt.reportExpression, SymbolTable.stringType)
    val severityExpression = checkExpressionOption(context, reportStmt.severityExpression, SymbolTable.severityLevel)
    (reportStmt.copy(reportExpression = reportExpression, severityExpression = severityExpression), context)
  }

  def visitReturnStatement(returnStmt: ReturnStatement, owner: Symbol, context: Context): ReturnType = {
    val expression = owner match {
      case _: ProcessSymbol => addError(returnStmt, "a process can not contain a return statement!")
      case functionSymbol: FunctionSymbol => returnStmt.expression match {
        case None => addError(returnStmt, "a function must return a value")
        case Some(_) => checkExpressionOption(context, returnStmt.expression, functionSymbol.returnType)
      }
      case procedureSymbol: ProcedureSymbol => returnStmt.expression.flatMap(addError(_, "a procedure can not return a value"))
      case _ => addError(returnStmt, "return statement is only in functions and procedures allowed")
    }
    (returnStmt.copy(expression = expression), context)
  }

  def visitSignalAssignmentStatement(signalAssignStmt: SignalAssignmentStatement, owner: Symbol, context: Context): ReturnType = {
    def checkWaveform(waveform: Waveform, dataType: DataType): Waveform = {
      val elements = waveform.elements.map{
        element =>
          val valueExpression = checkExpression(context, element.valueExpression, dataType)
          val timeExpression = checkExpressionOption(context, element.timeExpression, SymbolTable.timeType)
          new Waveform.Element(valueExpression = valueExpression, timeExpression = timeExpression)
      }
      new Waveform(waveform.position, elements)
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
                if (signalSymbol.mode == InterfaceList.Mode.IN) addError(signalAssignStmt, "can not write signal %s with modifier IN", signalSymbol.name)
                owner match {
                  case processSymbol: ProcessSymbol =>
                    if (signalSymbol.isUnresolved && signalSymbol.driver != null
                      && (signalSymbol.driver ne processSymbol)) {
                      addError(signalAssignStmt, "signal %s already has a driver at line: %s", signalSymbol.name, signalSymbol.driver.position.line.toString)
                    } else {
                      signalSymbol.driver = processSymbol
                    }
                  case _ =>
                }
                checkPure(context, signalAssignStmt, owner, signalSymbol)
                val delayMechanism = checkDelayMechanism(stmt.delayMechanism)
                val waveform = checkWaveform(stmt.waveform, signalSymbol.dataType)
                (stmt.copy(waveform = waveform, delayMechanism = delayMechanism), context)
              case _ =>
                addError(name.identifier, "%s is not a signal", name.identifier.text)
                (stmt, context)
            }
          case Right(aggregate) => error("not implemented") // TODO stmt.target.aggregate
        }
    }
  }

  def visitSignalDeclaration(signalDeclaration: SignalDeclaration, owner: Symbol, context: Context): ReturnType = {
    val subType = createType(context, signalDeclaration.subType)
    checkIfNotFileProtectedAccessType(signalDeclaration.subType, subType.dataType)
    val defaultExpression = checkExpressionOption(context, signalDeclaration.defaultExpression, subType.dataType)
    val symbols = signalDeclaration.identifiers.zipWithIndex.map{
      case (identifier, i) => new SignalSymbol(identifier, subType.dataType, InterfaceList.Mode.LINKAGE, signalDeclaration.signalType, context.varIndex + i, owner)
    }
    val newNode = signalDeclaration.copy(defaultExpression = defaultExpression, subType = subType, symbols = symbols)
    val newContext = context.insertSymbols(symbols).copy(varIndex = context.varIndex + signalDeclaration.identifiers.size + 1)
    (newNode, newContext)
  }

  def checkIfNotType(location: Locatable, dataType: DataType, invalidTypes: Set[Class[_]], message: String): Unit = {
    def isInvalidType(dataType: DataType): Boolean =
      invalidTypes.contains(dataType.getClass) || (dataType match {
        case recordType: RecordType => recordType.fields.exists(element => isInvalidType(element._2))
        case arrayType: ArrayType => isInvalidType(arrayType.elementType)
        case _ => false
      })
    if (isInvalidType(dataType)) addError(location, "the type can not be a %s type, or a composite type having a subelement that is a %s", message, message)
  }

  def checkIfNotFileProtectedAccessType(location: Locatable, dataType: DataType) =
    checkIfNotType(location, dataType, Set(classOf[FileType], classOf[ProtectedType], classOf[AccessType]), "file, protected, access")

  def checkIfNotFileProtectedType(subType: SubTypeIndication) =
    checkIfNotType(subType, subType.dataType, Set(classOf[FileType], classOf[ProtectedType]), "file, protected")

  def checkIfNotFileAccessType(location: Locatable, dataType: DataType) =
    checkIfNotType(location, dataType, Set(classOf[FileType], classOf[AccessType]), "file, access")

  def visitTypeDeclaration(typeDeclaration: AbstractTypeDeclaration, owner: Symbol, context: Context): ReturnType = {
    def checkDuplicateIdentifiers(identifiers: Seq[Identifier], message: String) = identifiers.diff(identifiers.distinct).foreach(identifier => addError(identifier, message, identifier))

    val name = typeDeclaration.identifier.text

    val (newTypeDeclaration, newSymbols) = typeDeclaration match {
      case enumerationType: EnumerationTypeDefinition =>
        val elements = enumerationType.elements.map(id => (id.text.replace("'", "")))
        require(enumerationType.elements.size <= Char.MaxValue)
        checkDuplicateIdentifiers(enumerationType.elements, "duplicate enumeration value %s")
        val dataType = new EnumerationType(name, elements, None, owner)
        val symbols = enumerationType.elements.map(id => new EnumerationSymbol(id, dataType, owner)) //TODO is owner the new created TypeSymbol?
        (enumerationType.copy(dataType = dataType), symbols)
      case physicalType: PhysicalTypeDefinition =>
        checkIdentifiers(physicalType.identifier, physicalType.endIdentifier)
        checkDuplicateIdentifiers(physicalType.elements.map(_.identifier), "duplicate physical unit %s")
        val range = checkRange(context, physicalType.range)
        range.expressionsOrName match {
          case Left((fromExpression, _, toExpression)) => if (!fromExpression.dataType.isInstanceOf[IntegerType] && fromExpression.dataType != null) addError(fromExpression, "expected a integer expression")
          case Right(attributeName) => addError(attributeName, "expected a range in the form from TO/DOWNTO to for a physical type")
        }
        @tailrec
        def buildUnitsMap(units: Seq[PhysicalTypeDefinition.Element], unitsMap: Map[String, Long]): Map[String, Long] = units match {
          case Seq() => unitsMap
          case Seq(unitDef, xs@_*) =>
            require(unitDef.literal.unitName.identifiers.size == 1)
            val unit = unitDef.literal.unitName.identifiers.head.text
            val value = if (unitDef.literal.literalType != Literal.Type.INTEGER_LITERAL) addError(unitDef.literal, "expected a integer literal")
            else if (!unitsMap.contains(unit)) addError(unitDef.literal.unitName, "physical unit %s not found", unit)
            else Some(unitsMap(unit) * unitDef.literal.toLong)
            buildUnitsMap(xs, unitsMap + (unitDef.identifier.text -> value.getOrElse(0)))
        }
        val (low, high) = calcRangeValues(range).getOrElse((Long.MinValue, Long.MinValue))
        val phyType = new PhysicalType(name, low, high, buildUnitsMap(physicalType.elements, Map(physicalType.baseIdentifier.text -> 1)))
        val symbols = new UnitSymbol(physicalType.baseIdentifier, phyType, owner) +: physicalType.elements.map(element => new UnitSymbol(element.identifier, phyType, owner))
        (physicalType.copy(range = range, dataType = phyType), symbols)
      case integerOrRealType: IntegerOrFloatingPointTypeDefinition =>
        val range = checkRange(context, integerOrRealType.range)
        val dataType = range.dataType match {
          case _: IntegerType =>
            val (low: Long, high: Long) = calcRangeValues(range)(LongIsIntegral).getOrElse((Int.MinValue, Int.MinValue))
            new IntegerType(name, low.toInt, low.toInt, None)
          case _: RealType =>
            val (low: Double, high: Double) = calcRangeValues(range)(DoubleAsIfIntegral).getOrElse((Double.MinValue, Double.MinValue))
            new RealType(name, low, high, None)
          case otherType =>
            addError(range, "expected a expression of type integer or real, found %s", otherType.name)
            NoType
        }
        (integerOrRealType.copy(dataType = dataType), Seq())
      case arrayType: ArrayTypeDefinition =>
        val subType = createType(context, arrayType.subType)
        checkIfNotFileProtectedType(subType)
        val dataType = arrayType.dimensions match {
          case Left(typeNames) =>
            new UnconstrainedArrayType(name, subType.dataType, typeNames.map{
              typeName =>
                val dataType = context.findType(typeName)
                if (!dataType.isInstanceOf[DiscreteType]) addError(typeName, "expected a discrete type")
                UnconstrainedRangeType(dataType)
            })
          case Right(ranges) => new ConstrainedArrayType(name, subType.dataType, ranges.map(checkDiscreteRange(context, _).dataType))
        }
        (arrayType.copy(subType = subType, dataType = dataType), Seq())
      case recordType: RecordTypeDefinition =>
        checkIdentifiers(recordType.identifier, recordType.endIdentifier)
        checkDuplicateIdentifiers(recordType.elements.flatMap(_.identifiers), "duplicate record field %s")
        val elements = recordType.elements.flatMap{
          element =>
            val subType = createType(context, element.subType)
            checkIfNotFileProtectedType(subType)
            element.identifiers.map(id => id.text -> subType.dataType)
        }
        (recordType.copy(dataType = new RecordType(name, elements, owner)), Seq())
      case accessType: AccessTypeDefinition =>
        val subType = createType(context, accessType.subType, isAccessTypeDefinition = true)
        checkIfNotFileProtectedType(subType)
        val deallocateSymbol = if (subType.dataType != IncompleteType)
          new Some(ProcedureSymbol(Identifier("deallocate"), Seq(VariableSymbol(Identifier("p"), new AccessType(name, subType.dataType), InterfaceList.Mode.INOUT, 0, null)), Runtime, true))
        else None
        (accessType.copy(subType = subType, dataType = new AccessType(name, subType.dataType)), deallocateSymbol.toList)
      case fileTypeDefinition: FileTypeDefinition =>
        val dataType = context.findType(fileTypeDefinition.typeName)
        checkIfNotFileProtectedAccessType(fileTypeDefinition.typeName, dataType)
        if (dataType.isInstanceOf[ArrayType] && dataType.asInstanceOf[ArrayType].dimensions.size != 1)
          addError(fileTypeDefinition.typeName, "the type can not be a multidimension array type")
        val fileType = new FileType(name, dataType)

        import InterfaceList.Mode._
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
        if (owner.isInstanceOf[PackageSymbol] && !owner.asInstanceOf[PackageSymbol].isBody) addError(protectedTypeBody, "protected type body declaration in package declaration is not allowed")
        checkIdentifiers(protectedTypeBody.identifier, protectedTypeBody.endIdentifier)

        val (declarativeItems, protectedType) = context.findSymbol(protectedTypeBody.identifier, classOf[TypeSymbol]) match {
          case None => (protectedTypeBody.declarativeItems, null)
          case Some(typeSymbol) => typeSymbol.dataType match {
            case protectedType: ProtectedType => (acceptDeclarativeItems(protectedTypeBody.declarativeItems, typeSymbol, context.openScope.insertSymbols(protectedType.subprograms))._1, protectedType.copy(isImplemented = true))
            case _ =>
              addError(protectedTypeBody.identifier, "%s is not a protected type", protectedTypeBody.identifier)
              (protectedTypeBody.declarativeItems, null)
          }
        }
        (protectedTypeBody.copy(declarativeItems = declarativeItems, dataType = protectedType.copy(owner = owner, isImplemented = true), header = protectedType), Seq())
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
        val deallocateProcedures = accessTypes.map(accessType => new ProcedureSymbol(Identifier("deallocate"), Seq(VariableSymbol(Identifier("p"), accessType, InterfaceList.Mode.INOUT, 0, null)), Runtime, true))
        (newTypeDeclaration, context.copy(symbolTable = context.insertSymbols(deallocateProcedures).symbolTable.insert(new TypeSymbol(typeDeclaration.identifier, newTypeDeclaration.dataType, owner))))
      case _ =>
        (newTypeDeclaration, if (newTypeDeclaration.dataType == NoType) context else context.insertSymbols(new TypeSymbol(typeDeclaration.identifier, newTypeDeclaration.dataType, owner) +: newSymbols))
    }
  }

  def visitSubTypeDeclaration(subTypeDeclaration: SubTypeDeclaration, owner: Symbol, context: Context): ReturnType = {
    val subType = createType(context, subTypeDeclaration.subType, subTypeDeclaration.identifier.text)
    val symbol = new SubTypeSymbol(subTypeDeclaration.identifier, subType.dataType, owner)
    (subTypeDeclaration.copy(subType = subType), context.insertSymbol(symbol))
  }

  def visitUseClause(useClause: UseClause, context: Context): ReturnType = useClause.names.flatMap{
    name =>
      name.identifiers match {
        case Seq(library, packageIdentifier, itemList@_*) if (itemList.size == 1 || itemList.size == 0) =>
          context.findSymbol(library, classOf[LibrarySymbol]).flatMap{
            symbol =>
              symbol.libraryArchive.loadSymbol(library.text + "/" + packageIdentifier.text + "_header", classOf[PackageSymbol]).orElse(addError(packageIdentifier, "package %s not found", packageIdentifier.text))
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
                      case None => addError(item, "symbol %s not found", item.text).toList
                    }
                  }
              }
          }
        case _ => addError(name, "not a valid name").toList
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
              if (varSymbol.mode == InterfaceList.Mode.IN) addError(varAssignStmt, "can not write variable %s with modifier IN", varSymbol.name)
              checkPure(context, varAssignStmt, owner, varSymbol)
            case _ => addError(name.identifier, "%s is not a variable", name.identifier.text)
          }
          val expression = checkExpression(context, stmt.expression, nameExpression.dataType)
          (stmt.copy(expression = expression, target = stmt.target.copy(expression = nameExpression)), context)
        case Right(aggregate) => error("not implemented") // TODO stmt.target.aggregate
      }
  }


  def visitVariableDeclaration(variableDeclaration: VariableDeclaration, owner: Symbol, context: Context): ReturnType = {
    val subType = createType(context, variableDeclaration.subType)
    owner match {
      case _: ArchitectureSymbol | _: PackageSymbol | _: EntitySymbol =>
        if (!variableDeclaration.isShared) addError(variableDeclaration, "non shared variables are not allowed in architecture, package and entity declarations")
        else {
          if (!subType.dataType.isInstanceOf[ProtectedType]) addError(variableDeclaration.subType, "the base type of the subtype indication of a shared variable declaration must be a protected type.")
        }
      case typeSymbol: TypeSymbol =>
        if (variableDeclaration.isShared) addError(variableDeclaration, "you can not use a shared variable in a protected type")
        if (subType.dataType == typeSymbol.dataType) addError(variableDeclaration.subType, "you can not use the defined protected type in the body where it is defined")
      case _ => if (variableDeclaration.isShared) addError(variableDeclaration, "you can only use shared variables in architecture, package and entity declarations")
    }
    val initialValue = subType.dataType match {
      case _: ProtectedType => variableDeclaration.initialValue.flatMap(expression => addError(expression, "a variable of a protected type can not have a initial value expression"))
      case _ => checkExpressionOption(context, variableDeclaration.initialValue, subType.dataType)
    }
    val multiplier = getNextIndex(subType.dataType) //+2 for real and physical, +1 for all other variables
    val symbols = variableDeclaration.identifiers.zipWithIndex.map{
      case (identifier, i) => new VariableSymbol(identifier, subType.dataType, InterfaceList.Mode.LINKAGE, context.varIndex + (i * multiplier), owner)
    }
    val newNode = variableDeclaration.copy(initialValue = initialValue, subType = subType, symbols = symbols)
    val newContext = context.insertSymbols(symbols).copy(varIndex = context.varIndex + (variableDeclaration.identifiers.size * multiplier) + 1)
    (newNode, newContext)
  }

  def visitWaitStatement(waitStmt: WaitStatement, context: Context): ReturnType = {
    val nameList = waitStmt.signals.map(nameList => nameList.map(name => checkExpression(context, name, NoType))).getOrElse(Seq())
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
    val (sequentialStatements, _) = acceptNodes(whileStmt.sequentialStatements, owner, context.insertLoopLabel(whileStmt.label, whileStmt.position))
    (whileStmt.copy(condition = condition, sequentialStatements = sequentialStatements), context)
  }

  def acceptNode(node: ASTNode, owner: Symbol, context: Context): ReturnType = node match {
    case null => (null, context) //nothing
    case DesignFile(designUnits) =>
      val (units, newContext) = acceptNodes(designUnits, owner, context)
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
    case assertStmt: AssertionStatement => visitAssertionStatement(assertStmt, context)
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
