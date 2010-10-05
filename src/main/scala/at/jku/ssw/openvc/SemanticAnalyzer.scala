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

package at.jku.ssw.openvc

import scala.collection.mutable
import scala.collection.BitSet
import math.Numeric.{LongIsIntegral, DoubleAsIfIntegral, IntIsIntegral}
import annotation.tailrec

import at.jku.ssw.openvc.ast._
import at.jku.ssw.openvc.ast.concurrentStatements._
import at.jku.ssw.openvc.ast.sequentialStatements._
import at.jku.ssw.openvc.ast.declarations._
import at.jku.ssw.openvc.ast.expressions._
import at.jku.ssw.openvc.symbolTable._
import java.io.{File, IOException, FileInputStream}
import at.jku.ssw.openvc.VHDLCompiler.Configuration
import at.jku.ssw.openvc.codeGenerator.ByteCodeGenerator.getNextIndex

object SemanticAnalyzer {
  type SemanticCheckResult = (DesignFile, Seq[CompilerMessage], Seq[CompilerMessage])
  type Buffer[A] = mutable.ListBuffer[A]

  def isCompatible(dataType: DataType, expectedDataType: DataType): Boolean =
    if (dataType == expectedDataType || (dataType eq NoType) || (expectedDataType eq NoType)) true
    else {
      dataType match {
        case i: IntegerType => expectedDataType match {
          case ei: IntegerType => expectedDataType == SymbolTable.universalIntegerType || dataType == SymbolTable.universalIntegerType || i.baseType.getOrElse(i) == ei.baseType.getOrElse(ei)
          case _ => false
        }
        case r: RealType => expectedDataType match {
          case er: RealType => expectedDataType == SymbolTable.universalRealType || dataType == SymbolTable.universalRealType || r.baseType.getOrElse(r) == er.baseType.getOrElse(er)
          case _ => false
        }
        case e: EnumerationType => expectedDataType match {
          case ee: EnumerationType => e.baseType.getOrElse(e) == ee.baseType.getOrElse(ee)
          case _ => false
        }
        case NullType => expectedDataType.isInstanceOf[AccessType]
        case accessType: AccessType => expectedDataType == NullType
        case a: ArrayType => expectedDataType match {
          case ea: ArrayType => isCompatible(a.elementType, ea.elementType) //TODO check element type and range
          case _ => false
        }
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
    val (newDesignFile, context) = acceptNode(designFile, null, Context(Map(), 0, new SymbolTable(List[SymbolTable.Scope]())))
    (newDesignFile.asInstanceOf[DesignFile], semanticErrors.toList, semanticWarnings.toList)
  }

  def addError(stmt: Locatable, msg: String, messageParameters: AnyRef*) = addErrorPosition(stmt.position, msg, messageParameters: _*)

  def addErrorPosition(position: Position, msg: String, messageParameters: AnyRef*) =
    semanticErrors += new CompilerMessage(position, String.format(msg, messageParameters.toArray: _*))

  def addWarning(stmt: Locatable, msg: String, messageParameters: AnyRef*) = semanticWarnings += new CompilerMessage(stmt.position, String.format(msg, messageParameters.toArray: _*))

  def checkIdentifiersOption(startOption: Option[Identifier], endOption: Option[Identifier]) = startOption match {
    case Some(start) => checkIdentifiers(start, endOption)
    case None => endOption foreach (end => addError(end, SemanticMessage.START_LABEL_MISSING, end))
  }

  def checkIdentifiers(start: Identifier, endOption: Option[Identifier]) =
    for (end <- endOption) if (end.text != start.text) addError(end, SemanticMessage.START_END_LABEL_DIFFERENT, start, end)

  case class Context(libraries: Map[String, AbstractLibraryArchive], varIndex: Int, symbolTable: SymbolTable) {
    def find(name: SelectedName): Option[Symbol] = {
      require(name != null)
      def matchParts(parts: Seq[Identifier], symbol: Symbol): Symbol = parts match {
        case Seq() => symbol
        case Seq(identifier, xs@_*) =>
          /*
        case Name.AttributePart(signature, identifier, expression) =>
          require(signature.isEmpty)
          require(expression.isEmpty)
          symbol.attributes.get(identifier.text) match {
            case Some(attribute) => attribute.copy(owner = symbol)
            case None =>
              addError(identifier, SemanticMessage.NOT_FOUND, "attribute", identifier.text)
              null
          }
          */
          val newSymbol = symbol match {
            case packageSymbol: PackageHeaderSymbol =>
              packageSymbol.localSymbols.get(identifier.text).getOrElse {
                addError(identifier, SemanticMessage.NOT_FOUND, "item", identifier.text)
                null
              }
            case r: RuntimeSymbol =>
              if (r.dataType.isInstanceOf[RecordType]) {
                r.dataType.asInstanceOf[RecordType].elementsMap.get(identifier.text) match {
                  case None =>
                    addError(identifier, SemanticMessage.NO_FIELD, identifier.text)
                    null
                  case Some(t) =>
                    symbol match {
                      case v: VariableSymbol => new VariableSymbol(identifier, t, v.modifier, varIndex, symbol)
                      case s: SignalSymbol => new SignalSymbol(identifier, t, s.modifier, null, varIndex, symbol)
                    }
                }
              } else {
                addError(identifier, SemanticMessage.NO_FIELD, identifier.text)
                null
              }
          }
          matchParts(xs, newSymbol)
      }
      val symbol = symbolTable.find(name.identifiers.head.text) match {
        case None => null
        case Some(symbol) => matchParts(name.identifiers.tail, symbol)
      }
      Option(symbol)
    }

    def findSymbol[A >: Null <: Symbol](identifier: Identifier, symbolClass: Class[A]): Option[A] =
      findSymbol(new SelectedName(Seq(identifier)), symbolClass)

    def findSymbol[A >: Null <: Symbol](typeName: SelectedName, symbolClass: Class[A]): Option[A] =
      find(typeName) match {
        case None =>
          addError(typeName, SemanticMessage.NOT_FOUND, symbolClass.toString, typeName.toString)
          None
        case Some(symbol) =>
          if (symbol.getClass ne symbolClass) {
            addError(typeName, SemanticMessage.INVALID_SYMBOL_TYPE, symbol.name, symbolClass.toString, symbol.getClass.toString)
            None
          } else Some(symbol.asInstanceOf[A])
      }

    def findType(dataType: SelectedName, isAccessTypeDefinition: Boolean = false): DataType = find(dataType) match {
      case None =>
        addError(dataType, SemanticMessage.NOT_FOUND, "type", dataType.toString)
        NoType
      case Some(symbol) => symbol match {
        case typeSymbol: TypeSymbol =>
          if (!isAccessTypeDefinition && typeSymbol.dataType == IncompleteType) addError(dataType, "can not use incomplete type %s", typeSymbol.name)
          typeSymbol.dataType
        case _ =>
          addError(dataType, SemanticMessage.NOT_A, dataType.toString, "type");
          NoType
      }
    }

    def findType[A](dataType: String): A = symbolTable.find(dataType).get match {
      case typeSymbol: TypeSymbol => typeSymbol.dataType.asInstanceOf[A]
    }

    def findFunctionInList(list: Symbol, dataTypes: Seq[DataType], returnType: DataType): Option[FunctionSymbol] = list match {
      case listOfFunctions: ListOfFunctions =>
        listOfFunctions.functions.find {
          x =>
            val functionSymbol = x.asInstanceOf[FunctionSymbol]
            functionSymbol.parameters.size == dataTypes.size && (functionSymbol.returnType eq returnType) && (functionSymbol.parameters.zip(dataTypes).forall(t => t._1.dataType eq t._2))
        }.map(_.asInstanceOf[FunctionSymbol])
      case _ => None
    }

    def findProcedureInList(list: Symbol, dataTypes: Seq[DataType]): Option[ProcedureSymbol] = list match {
      case listOfProcedure: ListOfProcedures =>
        listOfProcedure.procedures.find {
          x =>
            val procedureSymbol = x.asInstanceOf[ProcedureSymbol]
            procedureSymbol.parameters.size == dataTypes.size && (procedureSymbol.parameters.zip(dataTypes).forall(t => t._1.dataType eq t._2))
        }.map(_.asInstanceOf[ProcedureSymbol])
      case _ => None
    }

    def findFunction(name: String, dataTypes: Seq[DataType], returnType: DataType): Option[FunctionSymbol] =
      symbolTable.find(name) flatMap (findFunctionInList(_, dataTypes, returnType))

    def findProcedure(name: String, dataTypes: Seq[DataType]): Option[ProcedureSymbol] =
      symbolTable.find(name) flatMap (findProcedureInList(_, dataTypes))

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

    def insertSymbols(list: Seq[Symbol]): Context = {
      @tailrec
      def insertSymbolsInner(context: Context, l: Seq[Symbol]): Context = l match {
        case Seq() => context
        case Seq(symbol, xs@_*) => insertSymbolsInner(context.insertSymbol(symbol), xs)
      }
      insertSymbolsInner(this, list)
    }

    def insertSymbol(symbol: Symbol): Context =
      symbolTable.currentScope.get(symbol.name) match {
        case Some(existingSymbol) =>
          existingSymbol match {
            case listOfFunctions: ListOfFunctions if (symbol.isInstanceOf[FunctionSymbol]) =>
              val function = symbol.asInstanceOf[FunctionSymbol]
              findFunctionInList(listOfFunctions, function.parameters.map(_.dataType), function.returnType) match {
                case None => listOfFunctions.functions.append(function)
                case Some(s) => addError(symbol, SemanticMessage.FUNCTION_ALREADY_DECLARED, function.name, function.parameters.map(_.dataType.name).mkString(","), function.returnType.name)
              }
              this
            case listOfProcedure: ListOfProcedures if (symbol.isInstanceOf[ProcedureSymbol]) =>
              val procedure = symbol.asInstanceOf[ProcedureSymbol]
              findProcedureInList(listOfProcedure, procedure.parameters.map(_.dataType)) match {
                case None => listOfProcedure.procedures.append(procedure)
                case Some(s) => addError(symbol, SemanticMessage.PROCEDURE_ALREADY_DECLARED, procedure.name, procedure.parameters.map(_.dataType.name).mkString(","))
              }
              this
            case constSymbol: ConstantSymbol if (!constSymbol.isDefined) => copy(symbolTable = symbolTable.insert(symbol)) //used for deferred constants
            case typeSymbol: TypeSymbol if (typeSymbol.dataType.isInstanceOf[ProtectedType] && !typeSymbol.dataType.asInstanceOf[ProtectedType].implemented) =>
              copy(symbolTable = symbolTable.insert(symbol)) //used for protected type body
            case _ =>
              addError(symbol, SemanticMessage.ALREADY_DECLARED, symbol.name)
              this
          }
        case None =>
          val s = symbol match {
            case x: FunctionSymbol => new ListOfFunctions(x.identifier, new Buffer() += x)
            case x: ProcedureSymbol => new ListOfProcedures(x.identifier, new Buffer() += x)
            case _ => symbol
          }
          copy(symbolTable = symbolTable.insert(s))
      }
  }

  def getFlags(symbol: Symbol): BitSet = symbol match {
    case _: PackageHeaderSymbol | _: PackageBodySymbol => BitSet(SubProgramFlags.Static)
    case typeSymbol: TypeSymbol => BitSet(SubProgramFlags.Synchronized)
    case _ => BitSet(0)
  }

  type ReturnType = (ASTNode, Context)

  def acceptDeclarativeItems(n: Seq[DeclarativeItem], owner: Symbol, context: Context): (Seq[DeclarativeItem], Context) = {
    def checkIfImplemented(symbol: SubprogramSymbol) =
      if (!symbol.implemented && !owner.isInstanceOf[PackageHeaderSymbol])
        addError(owner, SemanticMessage.NOT_FOUND, "body for function", symbol.name)

    val (list, newContext) = acceptList(n, owner, context)
    newContext.symbolTable.currentScope.values.foreach {
      _ match {
        case typeSymbol: TypeSymbol => typeSymbol.dataType match {
          case protectedType: ProtectedType =>
            //Each protected type declaration appearing immediately within a given declarative region (see 10.1) must
            //have exactly one corresponding protected type body appearing immediately within the same declarative
            //region and textually subsequent to the protected type declaration. Similarly, each protected type body
            //appearing immediately within a given declarative region must have exactly one corresponding protected
            //type declaration appearing immediately within the same declarative region and textually prior to the
            //protected type body.
            if (!protectedType.implemented && !owner.isInstanceOf[PackageHeaderSymbol])
              addError(owner, SemanticMessage.PROTECTED_TYPE_BODY_NOT_DEFINED, typeSymbol.name)
          //For each incomplete type declaration there must be a corresponding full type declaration with the same identifier.
          // This full type declaration must occur later and immediately within the same declarative part as the incomplete type declaration to which it corresponds.
          case IncompleteType => addError(typeSymbol, SemanticMessage.INCOMPLETE_TYPE_NOT_DEFINED, typeSymbol.name)
          case _ =>
        }
        //For each subprogram declaration, there shall be a corresponding body. If both a declaration and a body are given, the subprogram specification of the body
        //shall conform (see 2.7) to the subprogram specification of the declaration. Furthermore, both the declaration and the body must occur immediately
        //within the same declarative region (see 10.1).
        case ListOfFunctions(_, functions) => functions.foreach(checkIfImplemented)
        case ListOfProcedures(_, procedures) => procedures.foreach(checkIfImplemented)
        case _ =>
      }
    }
    (list, newContext)
  }

  def acceptList[A <: ASTNode](n: Seq[A], owner: Symbol, context: Context): (Seq[A], Context) = {
    @tailrec
    def acceptListInner(nodes: Seq[A], listBuffer: Buffer[A], contextInner: Context): (Seq[A], Context) = nodes match {
      case Seq() => (listBuffer.toList, contextInner)
      case Seq(head, tail@_*) =>
        val (newNode, newContext) = acceptNode(head, owner, contextInner)
        newNode match {
          case LeafNode | null =>
          case node => listBuffer += node.asInstanceOf[A]
        }
        acceptListInner(tail, listBuffer, newContext)
    }
    acceptListInner(n, new Buffer[A](), context)
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
      case aggregateExpression: AggregateExpression => visitAggregateExpression(aggregateExpression)
      case typeCastExpr: TypeCastExpression => visitTypeCastExpression(typeCastExpr)
      case relation: Relation =>
        val l = acceptExpressionInner(relation.left)
        val r = acceptExpressionInner(relation.right)
        visitRelation(relation.copy(left = l, right = r))
      case qualifiedExpr: QualifiedExpression => visitQualifiedExpression(qualifiedExpr)
      case nameExpr: NameExpression => visitNameExpression(nameExpr)
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

    def visitAggregateExpression(aggregateExpression: AggregateExpression): Expression = {
      // TODO Auto-generated method stub
      //require(aggregateExpression.aggregate.elements.size == 1,aggregateExpression.position)
      //println(aggregateExpression.aggregate.elements)
      val elements = expectedType match {
        case arrayType: ConstrainedArrayType =>
          val dataType = arrayType.dimensions.size match {
            case 1 => arrayType.elementType
            case size => ConstrainedArrayType(arrayType.name, arrayType.elementType, arrayType.dimensions.tail) //TODO check if for each dimension is a row
          }
          aggregateExpression.aggregate.elements.map {
            element =>
              val expression = checkExpression(context, element.expression, dataType) //TODO
              new Aggregate.ElementAssociation(choices = element.choices, expression = expression)
          }
        case recordType: RecordType => aggregateExpression.aggregate.elements.zip(recordType.elementList.unzip._2).map {
          case (element, dataType) =>
            val expression = checkExpression(context, element.expression, dataType) //TODO
            new Aggregate.ElementAssociation(choices = element.choices, expression = expression)
        }
        case dataType =>
          addError(aggregateExpression, "expected a expression of an array or record type found %s", dataType.name)
          aggregateExpression.aggregate.elements
      }
      new AggregateExpression(aggregate = new Aggregate(aggregateExpression.aggregate.position, elements = elements), dataType = expectedType)
    }

    def visitFactor(factor: Factor): Expression = {
      val newDataType = factor.operator match {
        case Factor.Operator.ABS =>
          factor.left.dataType match {
            case numericType: NumericType => numericType
            case dataType =>
              addError(factor.left, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, "integer,real or physical", dataType.name)
              NoType
          }
        case Factor.Operator.POW =>
          for (rightExpression <- factor.rightOption) {
            if (!(rightExpression.dataType == SymbolTable.integerType || rightExpression.dataType == SymbolTable.universalIntegerType))
              addError(rightExpression, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, "integer", rightExpression.dataType.name)
          }
          factor.left.dataType match {
            case _: IntegerType | _: RealType => factor.left.dataType
            case dataType =>
              addError(factor.left, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, "integer or real", dataType.name)
              NoType
          }
        case Factor.Operator.NOT =>
          factor.left.dataType match {
            case arrayType: ArrayType if (arrayType.dimensions.size == 1 && (arrayType.elementType == SymbolTable.bitType || arrayType.elementType == SymbolTable.booleanType)) => factor.left.dataType
            case otherType =>
              if (otherType == SymbolTable.bitType || otherType == SymbolTable.booleanType) factor.left.dataType
              else {
                addError(factor, SemanticMessage.UNARY_OPERATOR_NOT_DEFINED, factor.operator.toString, factor.left.dataType.name)
                NoType
              }
          }
      }
      factor.copy(dataType = newDataType)
    }

    def visitFunctionCallExpression(functionCallExpr: FunctionCallExpression): Expression = {
      //TODO
      val functionName = functionCallExpr.functionName
      context.symbolTable.find(functionName.toString) match {
        case Some(symbol) => symbol match {
          case l: ListOfFunctions =>
            val (functionSymbolOption, parameters) = functionCallExpr.parameterAssociationList match {
              case Some(assocList) =>
                val parameters = assocList.elements.map(element => acceptExpression(element.actualPart.get, NoType, context))
                (l.functions.find(function => function.parameters.zip(parameters).forall(x => isCompatible(x._2.dataType, x._1.dataType))), parameters)
              case None =>
                (l.functions.find(function => function.parameters.isEmpty && isCompatible(expectedType, function.returnType)), List())
            }
            //val parameters = checkAssociationList(context, functionCallExpr.parameterAssociationList, functionSymbol.parameters, functionCallExpr)
            val dataType = functionSymbolOption match {
              case Some(functionSymbol) => functionSymbol.returnType
              case None => NoType
            }
            functionCallExpr.copy(parameters = parameters, dataType = dataType, symbol = functionSymbolOption.getOrElse(null))
          case _ => EmptyExpression
        }
        case _ => EmptyExpression
      }

    }

    def visitAttributeExpression(attributeExpr: AttributeExpression): Expression = {
      val newExpr = attributeExpr.attribute.parameter match {
        case Some(requiredDataType) => attributeExpr.attribute.isParameterOptional match {
          case false => attributeExpr.expression match {
            case None =>
              addError(attributeExpr, SemanticMessage.ATTRIBUTE_PARAMETER_REQUIRED, attributeExpr.attribute.name, requiredDataType.name)
              None
            case Some(expr) => Option(checkExpression(context, expr, requiredDataType))
          }
          case true => attributeExpr.expression match {
            case None => Some(Literal(attributeExpr.position, "1", Literal.Type.INTEGER_LITERAL, SymbolTable.universalIntegerType, 1)) //this must be a array attribute where the dimension is optional
            case Some(expr) => Option(checkExpression(context, expr, requiredDataType))
          }
        }
        case None =>
          attributeExpr.expression.foreach(addError(_, SemanticMessage.ATTRIBUTE_NO_PARAMETER, attributeExpr.attribute.name))
          None
      }
      attributeExpr.copy(expression = newExpr)
    }

    def visitPhysicalLiteral(literal: PhysicalLiteral): Expression = {
      val dataType = expectedType match {
        case physicalType: PhysicalType =>
          if (!physicalType.containsUnit(literal.unitName.text)) {
            addError(literal, SemanticMessage.UNIT_NOT_FOUND, expectedType.name, literal.unitName.text)
            NoType
          } else expectedType
        case dataType =>
          addError(literal, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, expectedType.name, "physical")
          NoType
      }
      literal.copy(dataType = dataType)
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
          case dataType =>
            addError(literal, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, expectedType.name, dataType.name)
            literal.copy(dataType = NoType)
        }
        case CHARACTER_LITERAL =>
          val text = literal.text.replace("'", "")
          val (dataType, value) = expectedType match {
            case e: EnumerationType if (e.contains(text)) => (e, e.intValue(text))
            case a: ArrayType => a.elementType match {
              case e: EnumerationType if (e.contains(text)) => (e, e.intValue(text))
              case dataType =>
                addError(literal, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, expectedType.name, dataType.name)
                (NoType, -1)
            }
            case dataType =>
              addError(literal, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, expectedType.name, dataType.name)
              (NoType, -1)
          }
          literal.copy(dataType = dataType, value = value)
        case BIT_STRING_LITERAL =>
          val Regex = "(b|o|x)\"([a-f0-9]+)\"".r
          literal.text.replace("_", "").toLowerCase match {
            case Regex(baseSpecifier, values) =>
              val valueString = baseSpecifier match {
                case "b" =>
                  if (values.zipWithIndex.forall {
                    case (character, i) =>
                      if (character != '0' && character != '1') {
                        addErrorPosition(literal.position.addCharacterOffset(i + 1), SemanticMessage.INVALID_BASED_LITERAL_CHARACTER, character.toString, "binary")
                        false
                      } else true
                  })
                    Integer.parseInt(values, 2).toBinaryString
                  else values
                case "o" => values.zipWithIndex.map {
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
                case "x" => values.zipWithIndex.map {
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
                val fraction = fractionString.zipWithIndex.map {
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
      val dataType = if (isBooleanOrBit(logicalExpr.left.dataType) && logicalExpr.left.dataType == logicalExpr.right.dataType) logicalExpr.left.dataType
      else (logicalExpr.left.dataType, logicalExpr.right.dataType) match {
        case (left: ArrayType, right: ArrayType) if (left.dimensions.size == 1 && right.dimensions.size == 1 && isCompatible(left, right) && isBooleanOrBit(left.elementType)) => logicalExpr.left.dataType
        case _ =>
          addError(logicalExpr, SemanticMessage.OPERATOR_NOT_DEFINED, logicalExpr.operator.toString, logicalExpr.left.dataType.name, logicalExpr.right.dataType.name)
          NoType
      }
      logicalExpr.copy(dataType = dataType)
    }

    def visitNameExpression(nameExpression: NameExpression): Expression = {
      val name = nameExpression.name

      def matchParts(parts: Seq[Name.Part], symbol: Symbol): Expression = {
        parts match {
          case Seq() => EmptyExpression
          case Seq(part, xs@_*) =>
            part match {
              case Name.IndexPart(indexes) => symbol match {
                case typeSymbol: TypeSymbol =>
                  require(xs.isEmpty)
                  if (indexes.size == 1) {
                    visitTypeCastExpression(TypeCastExpression(indexes.head, typeSymbol.dataType))
                  } else {
                    addError(nameExpression.name, SemanticMessage.INVALID_TYPE_CAST)
                    EmptyExpression
                  }
                case r: RuntimeSymbol => r.dataType match {
                  case array: ArrayType =>
                    if (indexes.size != array.dimensions.size)
                      addError(part, SemanticMessage.INVALID_INDEXES_COUNT, indexes.size.toString, array.dimensions.size.toString)
                    val expressions = indexes.zip(array.dimensions).map(x => checkExpression(context, x._1, x._2.elementType))
                    val newSymbol = r.makeCopy(Identifier(part.position, "array"), array.elementType, symbol)
                    val expr = matchParts(xs, newSymbol)
                    new ArrayAccessExpression(r, expressions, if (expr eq EmptyExpression) array.elementType else expr.dataType, expr)
                  case _ =>
                    addError(part, SemanticMessage.NOT_A, r.name, "array")
                    EmptyExpression
                }
                case list: ListOfFunctions =>
                  require(xs.isEmpty)
                  val parameterList = indexes.map(expr => new AssociationList.Element(formalPart = None, actualPart = Some(expr)))
                  visitFunctionCallExpression(FunctionCallExpression(new SelectedName(Seq(name.identifier)), Some(new AssociationList(parameterList))))
                case symbol =>
                  addError(nameExpression.name, SemanticMessage.NOT_A, symbol.name, "function")
                  EmptyExpression
              }
              case Name.AttributePart(signature, identifier, expression) =>
                require(signature.isEmpty)
                require(xs.isEmpty)
                symbol.attributes.get(identifier.text) match {
                  case None =>
                    expression.foreach(acceptExpression(_, NoType, context))
                    addError(part, SemanticMessage.NOT_FOUND, "attribute", identifier.text)
                    EmptyExpression
                  case Some(attribute) =>
                    visitAttributeExpression(AttributeExpression(identifier.position, symbol, attribute, expression, attribute.dataType))
                }
              case Name.SlicePart(range) =>
                require(xs.isEmpty)
                symbol match {
                  case r: RuntimeSymbol if (r.dataType.isInstanceOf[ArrayType]) => new RangeAccessExpression(r, range, r.dataType)
                  case s =>
                    addError(part, SemanticMessage.NOT_A, "array", s.name)
                    EmptyExpression
                }
              case Name.SelectedPart(identifier) => symbol match {
                case r: RuntimeSymbol if (r.dataType.isInstanceOf[RecordType] || (r.dataType.isInstanceOf[AccessType] && r.dataType.asInstanceOf[AccessType].pointerType.isInstanceOf[RecordType])) =>
                  val record = r.dataType match {
                    case recordType: RecordType => recordType
                    case accessType: AccessType => accessType.pointerType.asInstanceOf[RecordType]
                  }
                  record.elementsMap.get(identifier.text) match {
                    case None =>
                      addError(part, SemanticMessage.NOT_FOUND, "field", identifier.text)
                      EmptyExpression
                    case Some(dataType) =>
                      val newSymbol = r.makeCopy(identifier, dataType, symbol)
                      val expr = matchParts(xs, newSymbol)
                      new FieldAccessExpression(r, identifier, dataType, if (expr eq EmptyExpression) dataType else expr.dataType, expr)
                  }
                case s =>
                  addError(part, SemanticMessage.NOT_A, s.name, "record")
                  EmptyExpression
              }
            }
        }
      }
      context.symbolTable.find(name.identifier.text) match {
        case None => name.parts match {
          case Seq() => expectedType match {
            case _: PhysicalType => visitPhysicalLiteral(PhysicalLiteral(nameExpression.position, "1", name.identifier, Literal.Type.INTEGER_LITERAL))
            case enumType: EnumerationType =>
              if (enumType.contains(name.identifier.text)) Literal(nameExpression.position, name.identifier.text, Literal.Type.INTEGER_LITERAL, enumType, enumType.intValue(name.identifier.text))
              else {
                addError(nameExpression.name, SemanticMessage.NO_ENUMERATION_VALUE, name.identifier.text, enumType.name)
                EmptyExpression
              }
            case arrayType: ArrayType => visitLiteral(Literal(nameExpression.position, name.identifier.text, Literal.Type.STRING_LITERAL)) //TODO
            case _ =>
              addError(nameExpression.name, SemanticMessage.NOT_FOUND, "type,variable,signal,constant or function", name.identifier)
              EmptyExpression
          }
          case xs =>
            addError(nameExpression.name, SemanticMessage.NOT_FOUND, "type,variable,signal,constant or function", name.identifier)
            EmptyExpression
        }
        case Some(symbol) => name.parts match {
          case Seq() => symbol match {
            case list: ListOfFunctions => visitFunctionCallExpression(FunctionCallExpression(new SelectedName(Seq(name.identifier)), None))
            case r: RuntimeSymbol => ItemExpression(name.position, r)
          }
          case xs => matchParts(xs, symbol)
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
        case AggregateExpression(Aggregate(_, Seq(Aggregate.ElementAssociation(None, expression))), _) => expression //expression is of type: type_mark ' ( expression )
        case _ => qualifiedExpression.expression //expression is of type: type_mark ' aggregate
      }
      checkExpression(context, expressionToCheck, dataType)
    }

    def visitRelation(relation: Relation): Expression = {
      import Relation.Operator._
      def checkIfFileOrProtected(dataType: DataType): Boolean = dataType.isInstanceOf[FileType] || dataType.isInstanceOf[ProtectedType]
      relation.operator match {
        case EQ | NEQ =>
          if (checkIfFileOrProtected(relation.left.dataType) || checkIfFileOrProtected(relation.right.dataType))
            addError(relation, SemanticMessage.OPERATOR_NOT_DEFINED, relation.operator.toString, relation.left.dataType.name, relation.right.dataType.name)
          else {
            if (!isCompatible(relation.left.dataType, relation.right.dataType))
              addError(relation, SemanticMessage.OPERATOR_NOT_DEFINED, relation.operator.toString, relation.left.dataType.name, relation.right.dataType.name)
          }
        case _ => (relation.left.dataType, relation.right.dataType) match {
          case (left: ScalarType, right: ScalarType) if (isCompatible(left, right)) =>
          case (left: ArrayType, right: ArrayType) if (isCompatible(left, right) && left.elementType.isInstanceOf[DiscreteType] && right.elementType.isInstanceOf[DiscreteType]) =>
          case _ => addError(relation, SemanticMessage.OPERATOR_NOT_DEFINED, relation.operator.toString, relation.left.dataType.name, relation.right.dataType.name)
        }
      }
      relation.copy(dataType = SymbolTable.booleanType)
    }

    def visitShiftExpression(shiftExpr: ShiftExpression): Expression = {
      val dataType = shiftExpr.left.dataType match {
        case arrayType: ArrayType if (arrayType.dimensions.size == 1 && (arrayType.elementType == SymbolTable.bitType) || arrayType.elementType == SymbolTable.booleanType) => shiftExpr.left.dataType
        case otherType =>
          addError(shiftExpr.left, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, "one-dimensional array type whose element type is BIT or BOOLEAN", otherType.name)
          NoType
      }
      if (shiftExpr.right.dataType != SymbolTable.integerType && shiftExpr.right.dataType != SymbolTable.universalIntegerType)
        addError(shiftExpr.right, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, "integer", shiftExpr.right.dataType.name)
      shiftExpr.copy(dataType = dataType)
    }

    def visitSimpleExpression(simpleExpr: SimpleExpression): Expression = {
      val dataType = simpleExpr.signOperator.map {
        sign => simpleExpr.left.dataType match {
          case numericType: NumericType => numericType
          case dataType =>
            addError(simpleExpr, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, "integer,real or physical", dataType.name)
            NoType
        }
      }
      simpleExpr.copy(dataType = dataType.getOrElse(simpleExpr.left.dataType))
    }

    def visitTerm(term: Term): Expression = {
      def errorMessage(): DataType = {
        addError(term, SemanticMessage.OPERATOR_NOT_DEFINED, term.operator.toString, term.left.dataType.name, term.right.dataType.name)
        NoType
      }
      def isValid(dataType: DataType): Boolean = dataType == SymbolTable.integerType || dataType == SymbolTable.universalIntegerType || dataType == SymbolTable.realType || dataType == SymbolTable.universalRealType
      import Term.Operator._
      val dataType = term.operator match {
        case MUL => (term.left.dataType, term.right.dataType) match {
          case (integerOrRealType@(_: IntegerType | _: RealType), right) if (isCompatible(integerOrRealType, right)) => integerOrRealType
          case (physicalType: PhysicalType, integerOrRealType@(_: IntegerType | _: RealType)) if (isValid(integerOrRealType)) => physicalType
          case (integerOrRealType@(_: IntegerType | _: RealType), physicalType: PhysicalType) if (isValid(integerOrRealType)) => physicalType
          case (SymbolTable.universalRealType, SymbolTable.universalIntegerType) => SymbolTable.universalRealType
          case (SymbolTable.universalIntegerType, SymbolTable.universalRealType) => SymbolTable.universalRealType
          case _ => errorMessage()
        }
        case DIV => (term.left.dataType, term.right.dataType) match {
          case (integerOrRealType@(_: IntegerType | _: RealType), right) if (isCompatible(integerOrRealType, right)) => integerOrRealType
          case (physicalType: PhysicalType, integerOrRealType@(_: IntegerType | _: RealType)) if (isValid(integerOrRealType)) => physicalType
          case (physicalType: PhysicalType, right) if (physicalType == right) => SymbolTable.universalIntegerType
          case (SymbolTable.universalRealType, SymbolTable.universalIntegerType) => SymbolTable.universalRealType
          case _ => errorMessage()
        }
        case MOD | REM => (term.left.dataType, term.right.dataType) match {
          case (integerType: IntegerType, right) if (integerType == right || right == SymbolTable.universalIntegerType) => integerType
          case _ => errorMessage()
        }
      }
      term.copy(dataType = dataType)
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


  def checkExpressionOption(context: Context, exprOption: Option[Expression], dataType: DataType): Option[Expression] =
    exprOption map (checkExpression(context, _, dataType))

  def checkExpression(context: Context, expr: Expression, dataType: DataType): Expression = {
    val newExpr = acceptExpression(expr, dataType, context)
    if (!isCompatible(newExpr.dataType, dataType)) {
      addError(expr, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, dataType.name, newExpr.dataType.name)
    }
    newExpr
  }

  def checkAssociationList(context: Context, associationList: Option[AssociationList], symbols: Seq[Symbol], owner: Locatable): Seq[Expression] = {
    //TODO check formal Signal => actual Signal, formal Constant => actual Constant, formal Variable => actual Variable, formal File => actual File
    associationList match {
      case None =>
        if (symbols.nonEmpty) addError(owner, SemanticMessage.INVALID_ARG_COUNT, "0", symbols.size.toString)
        List()
      case Some(list) =>
        if (list.elements.size != symbols.size) addError(owner, SemanticMessage.INVALID_ARG_COUNT, associationList.iterator.size.toString, symbols.size.toString)
        list.elements.map(element => checkExpression(context, element.actualPart.get, NoType)) // TODO check parameters
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
        subTypeIndication.constraint.map {
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

  def checkLoopLabel(context: Context, loopLabelOption: Option[Identifier], node: ASTNode, stmtName: String): (Int, Int) =
    return (0, 0) //TODO
  /*return loopLabelOption match {
    case None =>
      findAncestor(ancestorList, classOf[AbstractLoopStatement]) match {
        case Some(loopStatement) => (loopStatement.position.line, loopStatement.position.charPosition)
        case None =>
          addError(node, SemanticMessage.NOT_INSIDE_A_LOOP, stmtName);
          null
      }
    case Some(loopLabel) =>
      val label = loopLabel.text
      @tailrec
      def findLoop(ancestorList: Seq[ASTNode]): (Int, Int) = {
        val (loopStmtOption, ancestorListRest) = findAncestorAndList(ancestorList, classOf[AbstractLoopStatement])
        loopStmtOption match {
          case None =>
            addError(loopLabel, SemanticMessage.NOT_FOUND, "loop label", label)
            null
          case Some(loopStmt) =>
            if (loopStmt.label.isDefined && loopStmt.label.get.text == label) (loopStmt.position.line, loopStmt.position.charPosition)
            else findLoop(ancestorListRest)
        }
      }
      findLoop(ancestorList)
  }*/

  def checkPure(context: Context, node: ASTNode, owner: Symbol, symbol: Symbol) = owner match {
    case functionSymbol: FunctionSymbol =>
      if (functionSymbol.isPure && (symbol.owner ne owner)) {
        addError(node, SemanticMessage.ASSIGN_IN_PURE_FUNCTION, symbol.getClass.toString, functionSymbol.name, symbol.name)
      }
    case _ =>
  }

  def checkRange(context: Context, range: Range, calcValues: Boolean = false): Range = range.attributeNameOption match { //TODO check if range expression are locally static expressions
    case None =>
      val fromExpression = checkExpression(context, range.fromExpression, NoType)
      val toExpression = checkExpression(context, range.toExpression, fromExpression.dataType)
      if (calcValues) {
        fromExpression.dataType match {
          case _: IntegerType =>
            lowLong = StaticExpressionCalculator.calcValue(fromExpression)(LongIsIntegral)
            highLong = StaticExpressionCalculator.calcValue(toExpression)(LongIsIntegral)
            range.direction match {
              case Range.Direction.To => if (lowLong > highLong) addError(fromExpression, SemanticMessage.INVALID_TO_DIRECTION)
              case Range.Direction.Downto => if (lowLong < highLong) addError(fromExpression, SemanticMessage.INVALID_DOWNTO_DIRECTION)
            }
          case _: RealType =>
            lowDouble = StaticExpressionCalculator.calcValue(fromExpression)(DoubleAsIfIntegral)
            highDouble = StaticExpressionCalculator.calcValue(toExpression)(DoubleAsIfIntegral)
            range.direction match {
              case Range.Direction.To => if (lowDouble > highDouble) addError(fromExpression, SemanticMessage.INVALID_TO_DIRECTION)
              case Range.Direction.Downto => if (lowDouble < highDouble) addError(fromExpression, SemanticMessage.INVALID_DOWNTO_DIRECTION)
            }
          case _: EnumerationType =>
            lowLong = StaticExpressionCalculator.calcValue(fromExpression)(IntIsIntegral)
            highLong = StaticExpressionCalculator.calcValue(toExpression)(IntIsIntegral)
            range.direction match {
              case Range.Direction.To => if (lowLong > highLong) addError(fromExpression, SemanticMessage.INVALID_TO_DIRECTION)
              case Range.Direction.Downto => if (lowLong < highLong) addError(fromExpression, SemanticMessage.INVALID_DOWNTO_DIRECTION)
            }
          case _ => addError(fromExpression, SemanticMessage.INVALID_SIMPLE_EXPRESSION)
        }
      }
      new Range(fromExpression, range.direction, toExpression, attributeNameOption = None, dataType = fromExpression.dataType)
    case Some(attributeName) =>
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


  def checkSignalList(context: Context, signalList: Seq[SelectedName]): Seq[SignalSymbol] = signalList.flatMap(name => context.findSymbol(name, classOf[SignalSymbol]))

  val operators = Set(
    "and", "or", "nand", "nor", "xor", "xnor", //logical_operator
    "=", "/=", "<", "<=", ">", ">=", //relational_operator
    "sll", "srl", "sla", "sra", "rol", "ror", //shift_operator
    "+", "-", "&", //adding_operator + sign
    "*", "/", "mod", "rem", //multiplying_operator
    "**", "abs", "not" //miscellaneous_operator
    )

  val operatorMangleMap = Map(
    "+" -> "$plus",
    "-" -> "$minus",
    "=" -> "$eq",
    "/=" -> "$eq$div",
    "<" -> "$less",
    "<=" -> "$less$eq",
    ">" -> "$greater",
    ">=" -> "$greater$eq",
    "*" -> "$times",
    "/" -> "$div",
    "**" -> "$times$times",
    "&" -> "$amp"
    )

  def getMangledName(name: Identifier): Identifier = {
    if (name.text(0) != '"') return name
    val operator = name.text.replace("\"", "").toLowerCase
    val text = operators(operator) match {
      case true => operatorMangleMap.get(operator).getOrElse(operator)
      case false =>
        addError(name, SemanticMessage.NOT_A, name.text, "valid overloaded operator")
        name.text
    }
    Identifier(name.position, text)
  }

  def checkIsStaticExpression(expr: Option[Expression]) = {

  }

  def getSymbolListFromInterfaceList(context: Context, interfaceListOption: Option[InterfaceList], owner: Symbol): (Seq[RuntimeSymbol], Int) = {
    val startIndex = owner match {
      case _: PackageHeaderSymbol | _: PackageBodySymbol | null => 0
      case _: ArchitectureSymbol | _: ProcessSymbol | _: TypeSymbol => 1
    }
    interfaceListOption match {
      case None => (List(), startIndex)
      case Some(interfaceList) =>
        import InterfaceList._
        var varIndex = startIndex
        val list = interfaceList.elements.flatMap {
          element =>
            val dataType = createType(context, element.subType)
            checkIsStaticExpression(element.expression)
            element.expression = checkExpressionOption(context, element.expression, dataType)

            import InterfaceList.InterfaceMode._
            val mod = element.interfaceMode.getOrElse(InterfaceList.InterfaceMode.IN) match {
            // TODO BUFFER,LINKAGE
              case IN => RuntimeSymbol.Modifier.IN
              case OUT => RuntimeSymbol.Modifier.OUT
              case INOUT => RuntimeSymbol.Modifier.IN_OUT
            }
            val nextIndex = getNextIndex(dataType)
            val isOptional = element.expression.isDefined
            element.identifierList.map {
              identifier =>
                val (symbol, indexChange) = element match {
                  case variableDeclaration: InterfaceVariableDeclaration => (new VariableSymbol(identifier, dataType, mod, varIndex, owner, isOptional), nextIndex)
                  case signalDeclaration: InterfaceSignalDeclaration => (new SignalSymbol(identifier, dataType, mod, None, varIndex, owner, isOptional), 1)
                  case fileDeclaration: InterfaceFileDeclaration => (new FileSymbol(identifier, dataType, varIndex, owner, isOptional), 1)
                  case constantDeclaration: InterfaceConstantDeclaration => (new ConstantSymbol(identifier, dataType, varIndex, owner, isOptional), nextIndex)
                }
                varIndex += indexChange
                symbol
            }
        }
        (list, varIndex)
    }
  }

  def createType(context: Context, subtypeIndication: SubTypeIndication, subTypeName: String = "subtype", isAccessTypeDefinition: Boolean = false): DataType = {
    def createEnumerationSubType(range: Range, baseType: EnumerationType): DataType = {
      def getEnumEntry(expr: Expression): Option[Int] = acceptExpression(expr, baseType, context) match {
        case literal: Literal if (literal.dataType eq baseType) => Some(literal.value.asInstanceOf[Int])
        case _ => None
      }

      val low = getEnumEntry(range.fromExpression).getOrElse(baseType.left)
      val high = getEnumEntry(range.toExpression).getOrElse(baseType.right)
      range.direction match {
        case Range.Direction.To => if (low > high) addError(range.fromExpression, SemanticMessage.INVALID_TO_DIRECTION)
        case Range.Direction.Downto => if (low < high) addError(range.fromExpression, SemanticMessage.INVALID_DOWNTO_DIRECTION)
      }
      val (newList, _) = baseType.elements.zipWithIndex.filter {case (_, i) => i >= low && i <= high}.unzip
      new EnumerationType(subTypeName, newList, Option(baseType.baseType.getOrElse(baseType)), baseType.owner)
    }

    def createIntegerOrRealSubType[T <: DataType](sourceRange: Range, baseType: DataType): DataType = {
      val range = checkRange(context, sourceRange, true)
      val dataType = range.dataType
      if (dataType.getClass eq baseType.getClass) {
        (baseType: @unchecked) match {
          case intBaseType: IntegerType =>
            val intType = new IntegerType(subTypeName, lowLong.toInt, highLong.toInt, Option(intBaseType.baseType.getOrElse(intBaseType))) //if this is a subtype of a subtype we want the real base type
            if (intType.lowerBound < intBaseType.lowerBound) addError(range.fromExpression, "lower bound %s is smaller than the lower bound of the base type %s", intType.lowerBound.toString, intBaseType.lowerBound.toString)
            if (intType.upperBound > intBaseType.upperBound) addError(range.toExpression, "upper bound %s is greater than the upper bound of the base type %s", intType.upperBound.toString, intBaseType.upperBound.toString)
            intType
          case realBaseType: RealType =>
            val realType = new RealType(subTypeName, lowDouble, highDouble, Option(realBaseType.baseType.getOrElse(realBaseType)))
            if (realType.lowerBound < realType.lowerBound) addError(range.fromExpression, "lower bound %s is smaller than the lower bound of the base type %s", realType.lowerBound.toString, realType.lowerBound.toString)
            if (realType.upperBound > realType.upperBound) addError(range.toExpression, "upper bound %s is greater than the upper bound of the base type %s", realType.upperBound.toString, realType.upperBound.toString)
            realType
        }
      } else {
        addError(range.fromExpression, SemanticMessage.EXPECTED_EXPRESSION_OF_TYPE, baseType.name, dataType.name)
        NoType
      }
    }
    // TODO subtypeIndication.resolutionFunction
    val baseType = context.findType(subtypeIndication.typeName, isAccessTypeDefinition)
    baseType match {
      case IncompleteType =>
        if (isAccessTypeDefinition) subtypeIndication.constraint.foreach(constraint => addError(subtypeIndication.typeName, "can not use an incomplete type with constraints"))
        baseType
      case _ => subtypeIndication.constraint match {
        case None => baseType
        case Some(constraint) => constraint match {
          case Left(range) => baseType match {
            case _: IntegerType | _: RealType => createIntegerOrRealSubType(range, baseType)
            case e: EnumerationType => createEnumerationSubType(range, e)
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
              val x = arrayConstraint.map(checkDiscreteRange(context, _, true).dataType)
              new ConstrainedArrayType(unconstrainedArrayType.name, unconstrainedArrayType.elementType, x)
            case _ =>
              addError(subtypeIndication.typeName, SemanticMessage.NOT_ALLOWED, "access subtype with a range constraint")
              NoType
          } //error("not implemented")
        }
      }
    }
  }

  def setUpTypes(context: Context) {
    SymbolTable.booleanType = context.findType("boolean")
    SymbolTable.bitType = context.findType("bit")
    SymbolTable.characterType = context.findType("character")
    SymbolTable.severityLevel = context.findType("severity_level")
    SymbolTable.integerType = context.findType("integer")
    SymbolTable.realType = context.findType("real")
    SymbolTable.timeType = context.findType("time")
    SymbolTable.naturalType = context.findType("natural")
    SymbolTable.positiveType = context.findType("positive")
    SymbolTable.stringType = context.findType("string")
    SymbolTable.bitVector = context.findType("bit_vector")
    SymbolTable.fileOpenKind = context.findType("file_open_kind")
    SymbolTable.fileOpenStatus = context.findType("file_open_status")
  }

  /* def findSubProgramSymbolFromSignature(siganture:Signature):SubProgramSymbol={

  }*/
  def visitAliasDeclaration(aliasDeclaration: AliasDeclaration, context: Context): ReturnType = {
    /*TODO def signatureToTypes(sig: Signature): (Seq[DataType], Option[DataType]) = {
      val p = sig.parameterList.map(parameter => context.findType(parameter))
      (p, sig.returnType.map(context.findType))
    }
    val name = aliasDeclaration.identifier
    if (aliasDeclaration.signature.isDefined) {
      val (parameters, returnType) = signatureToTypes(aliasDeclaration.signature.get)
      throw new UnsupportedOperationException()
    }
    val dataType = aliasDeclaration.subType.map(createType(context, _))
    val newContext = context.find(aliasDeclaration.name) map {
      originalSymbol =>
        val newSymbol = originalSymbol match {
          case typeSymbol: TypeSymbol => new TypeSymbol(name, dataType.getOrElse(typeSymbol.dataType))
          case constSymbol: ConstantSymbol => new ConstantSymbol(name, dataType.getOrElse(constSymbol.dataType), constSymbol.index, constSymbol.owner)
          case varSymbol: VariableSymbol => new VariableSymbol(name, dataType.getOrElse(varSymbol.dataType), varSymbol.modifier, varSymbol.index, varSymbol.owner)
          case signalSymbol: SignalSymbol => new SignalSymbol(name, dataType.getOrElse(signalSymbol.dataType), signalSymbol.modifier, signalSymbol.signalType, signalSymbol.index, signalSymbol.owner)
          case fileSymbol: FileSymbol => new FileSymbol(name, dataType.getOrElse(fileSymbol.dataType), fileSymbol.index, fileSymbol.owner)
        }
        context.insertSymbol(newSymbol)
    }
    (aliasDeclaration, newContext.getOrElse(context))
    */
    (aliasDeclaration, context)
  }

  def visitArchitectureDeclaration(architectureDeclaration: ArchitectureDeclaration, context: Context): ReturnType = {
    val symbol = new ArchitectureSymbol(architectureDeclaration.identifier)
    val entity = architectureDeclaration.entityName.toString
    val (entitySymbol, newSymbolTable) = try {
      val scopes = SymbolTable.getScopesFromInputStream(new FileInputStream(configuration.designLibrary + File.separator + entity + ".sym"))
      require(scopes.size != 3, "puh need to think of this code")
      val sb = context.symbolTable.insertScopes(scopes).openScope
      (context.copy(symbolTable = sb).findSymbol(architectureDeclaration.entityName, classOf[EntitySymbol]), sb)
    } catch {
      case e: IOException => addError(architectureDeclaration, SemanticMessage.NOT_FOUND, "entitySymbol", entity)
      (None, context.symbolTable.openScope)
    }
    val newContext = context.copy(symbolTable = newSymbolTable)
    setUpTypes(newContext)

    val (declarativeItems, c1) = acceptDeclarativeItems(architectureDeclaration.declarativeItems, symbol, newContext)
    val (concurrentStatements, c2) = acceptList(architectureDeclaration.concurrentStatements, symbol, c1)
    c2.closeScope()
    (architectureDeclaration.copy(declarativeItems = declarativeItems, concurrentStatements = concurrentStatements,
      symbol = symbol, entitySymbol = entitySymbol.getOrElse(null)), context)
  }

  def visitConfigurationDeclaration(configurationDeclaration: ConfigurationDeclaration, context: Context): ReturnType = {
    (configurationDeclaration, context)
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
    val symbol = new AttributeSymbol(attributeDeclaration.identifier, dataType, parameter = None, isParameterOptional = false, owner = owner, isPredefined = false)
    (attributeDeclaration, context.insertSymbol(symbol))
  }

  def visitAttributeSpecification(attributeSpec: AttributeSpecification, context: Context): ReturnType = {
    attributeSpec.identifier
    attributeSpec.entityClass
    attributeSpec.expression
    // TODO
    (attributeSpec, context)
  }

  def visitBlockStatement(blockStmt: BlockStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(blockStmt.label, blockStmt.endLabel)

    val (generics, lastGenericIndex) = getSymbolListFromInterfaceList(context, blockStmt.genericInterfaceList, owner)
    val (ports, lastPortIndex) = getSymbolListFromInterfaceList(context, blockStmt.portInterfaceList, owner)
    val newContext = context.insertSymbols(generics).insertSymbols(ports)

    // TODO
    val genericsList = checkAssociationList(newContext, blockStmt.genericAssociationList, generics, blockStmt)
    val portsList = checkAssociationList(newContext, blockStmt.portAssociationList, ports, blockStmt)
    val guardExpression = checkExpressionOption(newContext, blockStmt.guardExpression, SymbolTable.booleanType)

    val cx = blockStmt.guardExpression.map(expr => newContext.insertSymbol(new SignalSymbol(Identifier(expr.position, "guard"), SymbolTable.booleanType, RuntimeSymbol.Modifier.IN, None, -1, owner))).getOrElse(newContext)
    val (declarativeItems, c) = acceptDeclarativeItems(blockStmt.declarativeItems, owner, cx.copy(varIndex = lastPortIndex))
    val (statementList, _) = acceptList(blockStmt.statementList, owner, c)

    (blockStmt.copy(generics = genericsList, ports = portsList, guardExpression = guardExpression,
      declarativeItems = declarativeItems, statementList = statementList), context)
  }

  def visitCaseStatement(caseStmt: CaseStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(caseStmt.label, caseStmt.endLabel)
    val caseStmtExpression = checkExpression(context, caseStmt.expression, NoType)
    val containsOthers = caseStmt.alternatives.exists(when => when.choices.elements.exists(choice => choice.isOthers))
    val lastAlternative = caseStmt.alternatives.last

    val alternativesMapped = caseStmt.alternatives.map {
      when =>
        val choices = when.choices.elements.map {
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
      val caseError = new CaseStatement.When(new Choices(List(new Choices.Choice(null, None))), List(ThrowStatement(lastAlternative.choices.elements.head.position.addLineOffset(1), "case fall through")))
      alternativesMapped :+ caseError
    } else {
      alternativesMapped
    }

    caseStmtExpression.dataType match {
      case _: EnumerationType | _: IntegerType =>
        val keys = alternatives.flatMap(when =>
          when.choices.elements.collect(_ match {
            case choice: Choices.Choice if (!choice.isOthers) => choice.rangeOrExpression.get match {case Right(expression) => StaticExpressionCalculator.calcValue(expression)(IntIsIntegral)}
          }))

        (caseStmt.copy(expression = caseStmtExpression, keys = keys, alternatives = alternatives), context)
      case _ =>
        def toExpr(choices: Choices): Expression = {
          val tmpList: Seq[Expression] = choices.elements.map {
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

    val (genericSymbolList, _) = getSymbolListFromInterfaceList(context, componentDeclaration.genericInterfaceList, null)
    val (portSymbolList, _) = getSymbolListFromInterfaceList(context, componentDeclaration.portInterfaceList, null)

    val symbol = new ComponentSymbol(componentDeclaration.identifier, genericSymbolList, portSymbolList, owner)
    portSymbolList.foreach(port => port.owner = symbol)
    genericSymbolList.foreach(generic => generic.owner = symbol)

    (componentDeclaration.copy(symbol = symbol), context.insertSymbol(symbol))
  }

  def visitComponentInstantiationStatement(componentInstantiationStmt: ComponentInstantiationStatement, context: Context): ReturnType = {
    import ComponentInstantiationStatement.ComponentType._

    val (symbol: Symbol, generics, ports) = componentInstantiationStmt.componentType match {
      case COMPONENT =>
        context.findSymbol(componentInstantiationStmt.name, classOf[ComponentSymbol]) match {
          case Some(component) => (component, component.generics, component.ports)
          case None => (null, null, null)
        }
      case ENTITY =>
        context.findSymbol(componentInstantiationStmt.name, classOf[EntitySymbol]) match {
          case Some(entity) => (entity, entity.generics, entity.ports)
          case None => (null, null, null)
        }
      case CONFIGURATION => throw new UnsupportedOperationException()
    }
    val genericsList = checkAssociationList(context, componentInstantiationStmt.genericAssociationList, generics, componentInstantiationStmt)
    val portsList = checkAssociationList(context, componentInstantiationStmt.portAssociationList, ports, componentInstantiationStmt)
    (componentInstantiationStmt.copy(generics = genericsList, ports = portsList, symbol = symbol), context)
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
    val sensitivityList = concurrentProcedureCallStmt.parameterAssociationList.map {_.elements.flatMap {_.actualPart.map(expressionToSensitivityList(_)).toList.flatten}}.toList.flatten
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

    if (constantDeclaration.defaultExpression.isEmpty && !owner.isInstanceOf[PackageHeaderSymbol])
      addError(constantDeclaration, SemanticMessage.DEFERRED_CONSTANT_NOT_ALLOWED)

    val defaultExpression = checkExpressionOption(context, constantDeclaration.defaultExpression, dataType)
    val multiplier = getNextIndex(dataType) //+2 for real and physical, +1 for all other constants
    val symbols = constantDeclaration.identifierList.zipWithIndex.map {
      case (identifier, i) => context.symbolTable.currentScope.get(identifier.text) match {
        case Some(x) if (x.isInstanceOf[ConstantSymbol]) =>
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

  def visitDesignUnit(designUnit: DesignUnit, owner: Symbol, context: Context): ReturnType =
    designUnit.libraryUnit.map {
      unit =>
        checkIdentifiers(unit.identifier, unit.endIdentifier)
        val libraries = ((Identifier(designUnit.position, "std") +: designUnit.libraries).distinct.flatMap {
          id =>
            try {
              Some(id.text -> new JarFileLibraryArchive(id.text + ".jar"))
            } catch {
              case e: IOException =>
                addError(id, SemanticMessage.NOT_FOUND, "library", id)
                None
            }
        }).toMap + ("work" -> new DirectoryLibraryArchive("work"))

        val newSymbolTable = if ("standard" == unit.identifier.text) {
          context.symbolTable
        } else if (unit.isInstanceOf[EntityDeclaration] || unit.isInstanceOf[PackageDeclaration]) {
          // set up universe
          try {
            val scopes = SymbolTable.getScopesFromInputStream(libraries("std").getInputStream("package_standard.sym").get)
            require(scopes.size == 1, "puh need to think of this code")
            val symbolTable = context.symbolTable.insertScopes(scopes)
            setUpTypes(context.copy(symbolTable = symbolTable))
            symbolTable
          } catch {
            case e: IOException => e.printStackTrace();
            throw new RuntimeException(e.getMessage())
          }
        } else {
          context.symbolTable.openScope
        }
        val (useClauses, c1) = acceptList(designUnit.useClauses, owner, context.copy(libraries = libraries, symbolTable = newSymbolTable))
        val (libraryUnit, newContext) = acceptNode(unit, owner, c1.copy(libraries = libraries))
        libraries.valuesIterator.foreach(_.close)
        (designUnit.copy(useClauses = useClauses, libraryUnit = Some(libraryUnit.asInstanceOf[LibraryUnit])), newContext)
    }.getOrElse((LeafNode, context))

  def visitDisconnectionSpecification(disconnectionSpec: DisconnectionSpecification, context: Context): ReturnType = {
    // TODO
    val timeExpression = checkExpression(context, disconnectionSpec.timeExpression, SymbolTable.timeType)
    disconnectionSpec.signalListOrIdentifier match {
      case Left(signalList) => checkSignalList(context, signalList)
      case Right(identifier) => error("not implemented") //identifier is ALL or OTHERS
    }
    (disconnectionSpec.copy(timeExpression = timeExpression), context)
  }

  def visitEntityDeclaration(entityDeclaration: EntityDeclaration, context: Context): ReturnType = {
    val (generics, _) = getSymbolListFromInterfaceList(context, entityDeclaration.genericInterfaceList, null)
    val (ports, _) = getSymbolListFromInterfaceList(context, entityDeclaration.portInterfaceList, null)

    val symbol = new EntitySymbol(entityDeclaration.identifier, generics, ports)

    ports.foreach(port => port.owner = symbol)
    generics.foreach(generic => generic.owner = symbol)

    // TODO check if concurrentStatements are passive TDGTV page 182
    val (declarativeItems, c) = acceptDeclarativeItems(entityDeclaration.declarativeItems, symbol, context.openScope.insertSymbols(generics).insertSymbols(ports).insertSymbol(symbol))
    val (concurrentStatements, c2) = acceptList(entityDeclaration.concurrentStatements, symbol, c)
    for (node <- concurrentStatements) {
      val process = node.asInstanceOf[ProcessStatement]
      if (!process.symbol.isPassive) addError(process, SemanticMessage.STATEMENT_NOT_PASSIVE)
    }
    try {
      c2.symbolTable.writeToFile(configuration.designLibrary + File.separator + entityDeclaration.identifier.text + ".sym")
    } catch {
      case e: IOException => e.printStackTrace
    }
    (entityDeclaration.copy(declarativeItems = declarativeItems, concurrentStatements = concurrentStatements, symbol = symbol), context)
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
    val symbols = fileDeclaration.identifierList.zipWithIndex.map {
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
    val (declarativeItems, c1) = acceptDeclarativeItems(forGenerateStmt.declarativeItems, owner, context.openScope.insertSymbol(symbol))
    val (statementList, _) = acceptList(forGenerateStmt.statementList, owner, c1)
    (forGenerateStmt.copy(declarativeItems = declarativeItems, statementList = statementList), context)
  }

  def visitForStatement(forStmt: ForStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(forStmt.label, forStmt.endLabel)
    val discreteRange = checkDiscreteRange(context, forStmt.discreteRange, false)
    val symbol = new ConstantSymbol(forStmt.identifier, discreteRange.dataType.elementType, context.varIndex + 1, owner)
    val (sequentialStatementList, _) = acceptList(forStmt.sequentialStatementList, owner, context.openScope.insertSymbol(symbol).copy(varIndex = context.varIndex + 1))
    (forStmt.copy(sequentialStatementList = sequentialStatementList, symbol = symbol, discreteRange = discreteRange), context)
  }

  def visitFunctionDeclaration(functionDeclaration: FunctionDeclaration, owner: Symbol, context: Context): ReturnType = {
    val returnType = context.findType(functionDeclaration.returnType)
    val (parameters, _) = getSymbolListFromInterfaceList(context, functionDeclaration.parameterInterfaceList, owner)
    context.insertSymbols(parameters)
    val name = getMangledName(functionDeclaration.identifier)
    val symbol = new FunctionSymbol(name, parameters, returnType, owner, getFlags(owner), functionDeclaration.pure)
    parameters.foreach(s => s.owner = symbol)
    (functionDeclaration.copy(symbol = symbol), context.insertSymbol(symbol))
  }

  def visitFunctionDefinition(functionDefinition: FunctionDefinition, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiers(functionDefinition.identifier, functionDefinition.endIdentifier)
    val returnType = context.findType(functionDefinition.returnType)
    val (parameters, lastIndex) = getSymbolListFromInterfaceList(context, functionDefinition.parameterInterfaceList, owner)
    val name = getMangledName(functionDefinition.identifier)
    if (owner.isInstanceOf[TypeSymbol]) {
      //this is a method in a protected Type
      parameters.foreach(p => checkIfNotFileAccessType(p.identifier, p.dataType))
      checkIfNotFileAccessType(functionDefinition.returnType, returnType)
    }
    val (symbol, tmpContext) = context.findFunction(name.text, parameters.map(_.dataType), returnType) match {
      case Some(existingSymbol) if (!existingSymbol.implemented) => (existingSymbol, context)
      case _ =>
        val symbol = new FunctionSymbol(name, parameters, returnType, owner, getFlags(owner), functionDefinition.pure)
        (symbol, context.insertSymbol(symbol))
    }
    symbol.implemented = true
    parameters.foreach(s => s.owner = symbol)
    val newContext = tmpContext.openScope.insertSymbols(parameters)
    val (declarativeItems, c1) = acceptDeclarativeItems(functionDefinition.declarativeItems, symbol, newContext.copy(varIndex = lastIndex))
    val (sequentialStatementList, c2) = acceptList(functionDefinition.sequentialStatementList, symbol, c1)
    val newSequentialStatementList = sequentialStatementList match {
      case Seq(AssertStatement(pos, _, _, _, _)) =>
        //corner case for functions with only a assert statement, throw statement will make the JVM verifier happy, that this function does not return a value
        sequentialStatementList :+ ThrowStatement(pos, "function fall through")
      case _ => sequentialStatementList
    }
    val localSymbols = c2.closeScope()
    (functionDefinition.copy(declarativeItems = declarativeItems, sequentialStatementList = newSequentialStatementList,
      symbol = symbol, localSymbols = localSymbols), tmpContext)
  }

  def visitGroupDeclaration(groupDeclaration: GroupDeclaration, owner: Symbol, context: Context): ReturnType = {
    val symbol = new GroupSymbol(groupDeclaration.identifier, owner)
    /*
    * final Symbol groupTemplateSymbol = find(groupDeclaration.getGroupName(), SymbolObjectType.GROUP_TEMPLATE); if
    * (groupTemplateSymbol != null) { // TODO }
    */
    (groupDeclaration, context.insertSymbol(symbol))
  }

  def visitGroupTemplateDeclaration(groupTemplateDeclaration: GroupTemplateDeclaration, owner: Symbol, context: Context): ReturnType = {
    val lastElement = groupTemplateDeclaration.elements.last
    val items = groupTemplateDeclaration.elements.init.map {
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
    val (declarativeItems, c1) = acceptDeclarativeItems(ifGenerateStmt.declarativeItems, owner, context)
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
    val (sequentialStatementList, _) = acceptList(loopStmt.sequentialStatementList, owner, context)
    (loopStmt.copy(sequentialStatementList = sequentialStatementList), context)
  }

  def visitNextStatement(nextStmt: NextStatement, context: Context): ReturnType = {
    val condition = checkExpressionOption(context, nextStmt.condition, SymbolTable.booleanType)
    val loopStatement = checkLoopLabel(context, nextStmt.loopLabel, nextStmt, "next")
    (nextStmt.copy(condition = condition, loopStatement = loopStatement), context)
  }

  def visitPackageBodyDeclaration(packageBodyDeclaration: PackageBodyDeclaration, context: Context): ReturnType = {
    val packageName = packageBodyDeclaration.identifier
    val symbol = new PackageBodySymbol(packageName, Map())
    val newSymbolTable = try {
      val scopes = SymbolTable.getScopesFromInputStream(new FileInputStream(configuration.designLibrary + File.separator + "package_" + packageName + ".sym"))
      //require(scopes.size == 2)
      val table = context.symbolTable.insertScopes(scopes)
      setUpTypes(context.copy(symbolTable = table))
      table
    } catch {
      case e: IOException =>
        addError(packageBodyDeclaration, SemanticMessage.NOT_FOUND, "package", packageName)
        context.symbolTable
    }
    val (declarativeItems, c1) = acceptDeclarativeItems(packageBodyDeclaration.declarativeItems, symbol, context.copy(symbolTable = newSymbolTable))
    c1.closeScope().collect {_ match {case c: ConstantSymbol if (!c.isDefined) => c}}.foreach(symbol => addError(packageBodyDeclaration, SemanticMessage.DEFERRED_CONSTANT_NOT_DECLARED, symbol.name))
    (packageBodyDeclaration.copy(declarativeItems = declarativeItems, symbol = symbol), context)
  }

  def visitPackageDeclaration(packageDeclaration: PackageDeclaration, context: Context): ReturnType = {
    val symbol = new PackageHeaderSymbol(packageDeclaration.identifier, Map())
    val (declarativeItems, newContext) = acceptDeclarativeItems(packageDeclaration.declarativeItems, symbol, context.openScope.insertSymbol(symbol))
    try {
      val fileName = configuration.designLibrary + File.separator + "package_" + packageDeclaration.identifier.text + ".sym"
      newContext.symbolTable.writeToFile(fileName)
    } catch {
      case e: IOException => e.printStackTrace
    }
    (packageDeclaration.copy(declarativeItems = declarativeItems, symbol = symbol), context)
  }

  def visitProcedureCallStatement(procedureCallStmt: ProcedureCallStatement, context: Context): ReturnType = {
    val name = procedureCallStmt.procedureName
    context.findSymbol(name, classOf[ListOfProcedures]) match {
      case Some(procedures) =>
        val parameters = checkAssociationList(context, procedureCallStmt.parameterAssociationList, procedures.procedures.head.parameters, procedureCallStmt) //TODO
        (procedureCallStmt.copy(parameters = parameters, symbol = procedures.procedures.head), context)
      case None => (procedureCallStmt, context)
    }
  }

  def visitProcedureDeclaration(procedureDeclaration: ProcedureDeclaration, owner: Symbol, context: Context): ReturnType = {
    val (parameters, _) = getSymbolListFromInterfaceList(context, procedureDeclaration.parameterInterfaceList, owner)
    context.insertSymbols(parameters)
    val name = getMangledName(procedureDeclaration.identifier)
    val symbol = new ProcedureSymbol(name, parameters, owner, getFlags(owner), false)
    parameters.foreach(s => s.owner = symbol)
    parameters.foreach(s => s.owner = symbol)
    (procedureDeclaration.copy(symbol = symbol), context.insertSymbol(symbol))
  }

  def visitProcedureDefinition(procedureDefinition: ProcedureDefinition, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiers(procedureDefinition.identifier, procedureDefinition.endIdentifier)
    val (parameters, lastIndex) = getSymbolListFromInterfaceList(context, procedureDefinition.parameterInterfaceList, owner)
    if (owner.isInstanceOf[TypeSymbol]) {
      //this is a method in a protected Type
      parameters.foreach(p => checkIfNotFileAccessType(p.identifier, p.dataType))
    }
    val name = getMangledName(procedureDefinition.identifier)
    val (symbol, tmpContext) = context.findProcedure(name.text, parameters.map(_.dataType)) match {
      case Some(existingSymbol) if (!existingSymbol.implemented) => (existingSymbol, context)
      case _ =>
        val symbol = new ProcedureSymbol(procedureDefinition.identifier, parameters, owner, getFlags(owner), false)
        (symbol, context.insertSymbol(symbol))
    }
    symbol.implemented = true;
    parameters.foreach(s => s.owner = symbol)
    val newContext = context.openScope.insertSymbols(parameters)
    val (declarativeItems, c1) = acceptDeclarativeItems(procedureDefinition.declarativeItems, symbol, newContext.copy(varIndex = lastIndex))
    val (sequentialStatementList, c2) = acceptList(procedureDefinition.sequentialStatementList, symbol, c1)
    symbol.isPassive = isPassive(sequentialStatementList)
    val localSymbols = c2.closeScope()
    (procedureDefinition.copy(declarativeItems = declarativeItems, sequentialStatementList = sequentialStatementList,
      symbol = symbol, localSymbols = localSymbols), tmpContext)
  }

  def isPassive(list: Seq[SequentialStatement]): Boolean = list.forall {
    _ match {
      case loopStmt: AbstractLoopStatement => isPassive(loopStmt.sequentialStatementList)
      case caseStmt: CaseStatement => caseStmt.alternatives.forall(alternative => isPassive(alternative.statements))
      case ifStmt: IfStatement => ifStmt.ifThenList.forall(ifThen => isPassive(ifThen.statements)) && ifStmt.elseSequentialStatementList.map(isPassive).getOrElse(true)
      case _: SignalAssignmentStatement => false
      case procedureCallStmt: ProcedureCallStatement => procedureCallStmt.symbol.isPassive
      case _ => true
    }
  }

  def toLinearList(sequentialStatementList: Seq[SequentialStatement]): Seq[SequentialStatement] = {
    @tailrec
    def toLinearListInner(list: Seq[SequentialStatement], listBuffer: Buffer[SequentialStatement]): Seq[SequentialStatement] = list match {
      case Seq() => listBuffer.toList
      case node => list.head match {
        case loopStmt: AbstractLoopStatement =>
          listBuffer += loopStmt
          listBuffer.appendAll(toLinearList(loopStmt.sequentialStatementList))
        case caseStmt: CaseStatement =>
          listBuffer += caseStmt
          listBuffer.appendAll(caseStmt.alternatives.flatMap(alternative => toLinearList(alternative.statements)))
        case ifStmt: IfStatement =>
          listBuffer += ifStmt
          listBuffer.appendAll(ifStmt.ifThenList.flatMap(ifThen => toLinearList(ifThen.statements)))
          listBuffer.appendAll(ifStmt.elseSequentialStatementList.map(toLinearList).flatten)
        case statement => listBuffer += statement
      }
      toLinearListInner(list.tail, listBuffer)
    }
    toLinearListInner(sequentialStatementList, new Buffer[SequentialStatement]())
  }

  def visitProcessStatement(processStatement: ProcessStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(processStatement.label, processStatement.endLabel)

    val newSymbolTable = context.symbolTable.openScope
    val name = processStatement.label.getOrElse(Identifier(processStatement.position, "process_" + processStatement.position.line))
    val symbol = new ProcessSymbol(name, owner.asInstanceOf[ArchitectureSymbol], false)

    val (declarativeItems, newContext) = acceptDeclarativeItems(processStatement.declarativeItems, symbol, context.copy(symbolTable = newSymbolTable))
    val (newSequentialStatementList, c2) = acceptList(processStatement.sequentialStatementList, symbol, newContext)

    val sequentialStatementList = newSequentialStatementList ++ processStatement.sensitivityList.map {
      sensitivityList =>
        toLinearList(newSequentialStatementList).foreach {
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
    owner match {
      case _: ProcessSymbol => addError(returnStmt, SemanticMessage.RETURN_STMT_IN_PROCESS)
      case functionSymbol: FunctionSymbol => returnStmt.expression match {
        case None => addError(returnStmt, SemanticMessage.FUNCTION_RETURN_WITHOUT_EXPRESSION)
        case Some(_) =>
          val expression = checkExpressionOption(context, returnStmt.expression, functionSymbol.returnType)
          return (returnStmt.copy(expression = expression), context)
      }
      case procedureSymbol: ProcedureSymbol =>
        returnStmt.expression.foreach(expression => addError(expression, SemanticMessage.PROCEDURE_RETURN_VALUE))
        return (returnStmt.copy(procedureSymbol = procedureSymbol), context)
      case _ => addError(returnStmt, "return statement is only in functions and procedures allowed")
    }
    (returnStmt, context)
  }

  def visitSignalAssignmentStatement(signalAssignStmt: SignalAssignmentStatement, owner: Symbol, context: Context): ReturnType = {
    def checkWaveform(waveForm: Waveform, dataType: DataType): Waveform = {
      val elements = waveForm.elements.map {
        element =>
          val valueExpression = checkExpression(context, element.valueExpression, dataType)
          val timeExpression = checkExpressionOption(context, element.timeExpression, SymbolTable.timeType)
          new Waveform.Element(valueExpression = valueExpression, timeExpression = timeExpression)
      }
      new Waveform(waveForm.position, elements)
    }

    def checkDelayMechanism(delayMechanismOption: Option[DelayMechanism]): Option[DelayMechanism] =
      delayMechanismOption.map {
        delayMechanism =>
          val rejectExpression = checkExpressionOption(context, delayMechanism.rejectExpression, SymbolTable.timeType)
          new DelayMechanism(delayMechanism.delayType, rejectExpression = rejectExpression)
      }

    signalAssignStmt match {
      case stmt: SimpleSignalAssignmentStatement =>
        stmt.target.nameOrAggregate match {
          case Left(name) =>
            val nameExpression = acceptExpression(NameExpression(name), NoType, context)
            nameExpression match {
              case w: WithSymbol[_] if (w.symbol.isInstanceOf[SignalSymbol]) =>
                val signalSymbol = w.symbol.asInstanceOf[SignalSymbol]
                if (signalSymbol.modifier == RuntimeSymbol.Modifier.IN) addError(signalAssignStmt, SemanticMessage.ASSIGN_READ_ONLY, "signal", signalSymbol.name)
                owner match {
                  case processSymbol: ProcessSymbol =>
                    if (signalSymbol.isUnresolved && signalSymbol.driver != null
                            && (signalSymbol.driver ne processSymbol)) {
                      addError(signalAssignStmt, SemanticMessage.RESOLVED_DUPLICATE_SIGNAL_ASSIGNMENT, signalSymbol.name, signalSymbol.driver.position.line.toString)
                    } else {
                      signalSymbol.driver = null //TODO processSymbol
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
    val symbols = signalDeclaration.identifierList.zipWithIndex.map {
      case (identifier, i) => new SignalSymbol(identifier, dataType, RuntimeSymbol.Modifier.IN_OUT, signalDeclaration.signalType, context.varIndex + i, owner)
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
        val elements = enumerationType.elements.map(id => id.text.replace("'", ""))
        require(enumerationType.elements.size <= Char.MaxValue)
        checkDuplicateIdentifiers(enumerationType.elements, SemanticMessage.DUPLICATE_ENUMERATION_VALUE)
        (enumerationType.copy(dataType = new EnumerationType(name, elements, None, owner)), Seq())
      case physicalType: PhysicalTypeDefinition =>
        checkIdentifiers(physicalType.identifier, physicalType.endIdentifier)
        checkDuplicateIdentifiers(physicalType.elements.map(_.identifier), SemanticMessage.DUPLICATE_PHYSICAL_UNIT)
        val range = checkRange(context, physicalType.range, true)
        if (!range.fromExpression.dataType.isInstanceOf[IntegerType] && range.fromExpression.dataType != null) addError(range.fromExpression, SemanticMessage.EXPECTED_INTEGER_EXPRESSION)
        if (!range.toExpression.dataType.isInstanceOf[IntegerType] && range.toExpression.dataType != null) addError(range.toExpression, SemanticMessage.EXPECTED_INTEGER_EXPRESSION)
        @tailrec
        def buildUnitsMap(units: Seq[PhysicalTypeDefinition.Element], unitsMap: Map[String, Long]): Map[String, Long] = units match {
          case Seq() => unitsMap
          case list =>
            val unitDef = list.head
            val unit = unitDef.literal.unitName.text
            val value = if (unitDef.literal.literalType != Literal.Type.INTEGER_LITERAL) {
              addError(unitDef.literal, SemanticMessage.EXPECTED_LITERAL_OF_TYPE, "integer")
              0
            } else if (!unitsMap.contains(unit)) {
              addError(unitDef.literal.unitName, SemanticMessage.NOT_FOUND, "unit", unit)
              0
            } else unitsMap(unit) * unitDef.literal.toLong
            buildUnitsMap(list.tail, unitsMap + (unitDef.identifier.text -> value))
        }

        val phyType = new PhysicalType(name, lowLong, highLong, buildUnitsMap(physicalType.elements, Map(physicalType.baseIdentifier.text -> 1)))
        (physicalType.copy(dataType = phyType), Seq())
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
            val dataType = new UnconstrainedArrayType(name, elementDataType, unconstrainedArrayTypeDefinition.dimensions.map {
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
        val elements = recordType.elements.flatMap {
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
          new Some(ProcedureSymbol(Identifier("deallocate"), Seq(VariableSymbol(Identifier("p"), new AccessType(name, dataType), RuntimeSymbol.Modifier.IN_OUT, 0, null)), Runtime, BitSet(SubProgramFlags.Static), true))
        else None
        (accessType.copy(dataType = new AccessType(name, dataType)), deallocateSymbol.toList)
      case fileTypeDefinition: FileTypeDefinition =>
        val dataType = context.findType(fileTypeDefinition.typeName)
        checkIfNotFileProtectedAccessType(fileTypeDefinition.typeName, dataType)
        if (dataType.isInstanceOf[ArrayType] && dataType.asInstanceOf[ArrayType].dimensions.size != 1)
          addError(fileTypeDefinition.typeName, SemanticMessage.INVALID_TYPE, "multidimension array")
        val fileType = new FileType(name, dataType)
        val staticBitSet = BitSet(SubProgramFlags.Static)

        import RuntimeSymbol.Modifier._
        val pos = fileTypeDefinition.identifier.position
        val fileSymbol = FileSymbol(Identifier("f"), fileType, 0, null)
        val file_open1 = new ProcedureSymbol(Identifier(pos, "file_open"),
          Seq(fileSymbol, ConstantSymbol(Identifier("external_name"), SymbolTable.stringType, 0, null), ConstantSymbol(Identifier("open_kind"), SymbolTable.fileOpenKind, 0, null, isOptional = true)),
          Runtime, staticBitSet, true)

        val file_open2 = new ProcedureSymbol(Identifier(pos, "file_open"),
          Seq(VariableSymbol(Identifier("status"), SymbolTable.fileOpenStatus, OUT, 0, null, isOptional = true), fileSymbol, ConstantSymbol(Identifier("external_name"), SymbolTable.stringType, 0, null),
            ConstantSymbol(Identifier("open_kind"), SymbolTable.fileOpenKind, 0, null, isOptional = true)), Runtime, staticBitSet, true)

        val file_close = new ProcedureSymbol(Identifier(pos, "file_close"), Seq(fileSymbol), Runtime, staticBitSet, true)
        val read = if (dataType.isInstanceOf[ArrayType])
          new ProcedureSymbol(Identifier(pos, "read"), Seq(fileSymbol, VariableSymbol(Identifier("value"), dataType, OUT, 0, null), VariableSymbol(Identifier("length"), SymbolTable.naturalType, OUT, 0, null)), Runtime, staticBitSet, true)
        else new ProcedureSymbol(Identifier(pos, "read"), Seq(fileSymbol, VariableSymbol(Identifier("value"), dataType, OUT, 0, null)), Runtime, staticBitSet, true)
        val write = new ProcedureSymbol(Identifier(pos, "write"), Seq(fileSymbol, ConstantSymbol(Identifier("value"), dataType, 0, null)), Runtime, staticBitSet, true)
        val endfile = new FunctionSymbol(Identifier(pos, "endfile"), Seq(fileSymbol), SymbolTable.booleanType, Runtime, staticBitSet, true)

        (fileTypeDefinition.copy(dataType = fileType), Seq(file_open1, file_open2, file_close, read, write, endfile))
      case protectedType: ProtectedTypeDeclaration =>
        checkIdentifiers(protectedType.identifier, protectedType.endIdentifier)
        val (declarativeItems, c) = acceptDeclarativeItems(protectedType.declarativeItems, owner, context.openScope)
        val dataType = new ProtectedType(name, c.symbolTable.currentScope.values.flatMap {
          _ match {
            case ListOfFunctions(_, functions) => functions.toList
            case ListOfProcedures(_, procedures) => procedures.toList
            case _ => Seq()
          }
        }.toSeq, owner, false)
        (protectedType.copy(declarativeItems = declarativeItems, dataType = dataType), Seq())
      case protectedTypeBody: ProtectedTypeBodyDeclaration =>
        if (owner.isInstanceOf[PackageHeaderSymbol]) addError(protectedTypeBody, SemanticMessage.PROTECTED_TYPE_BODY_IN_PACKAGE)
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
        val accessTypes = context.symbolTable.currentScope.values.collect(_ match {case typeSymbol: TypeSymbol if (typeSymbol.dataType.isInstanceOf[AccessType] && typeSymbol.dataType.asInstanceOf[AccessType].pointerType == IncompleteType) => typeSymbol.dataType.asInstanceOf[AccessType]}).toSeq
        accessTypes.foreach(accessType => accessType.pointerType = newTypeDeclaration.dataType)
        //we can now add the deallocate procedure, as the type is now defined
        val deallocateProcedures = accessTypes.map(accessType => new ProcedureSymbol(Identifier("deallocate"), Seq(VariableSymbol(Identifier("p"), accessType, RuntimeSymbol.Modifier.IN_OUT, 0, null)), Runtime, BitSet(SubProgramFlags.Static), true))
        (newTypeDeclaration, context.copy(symbolTable = context.insertSymbols(deallocateProcedures).symbolTable.insert(new TypeSymbol(typeDeclaration.identifier, newTypeDeclaration.dataType, owner))))
      case _ =>
        (newTypeDeclaration, if (newTypeDeclaration.dataType == NoType) context else context.insertSymbols(new TypeSymbol(typeDeclaration.identifier, newTypeDeclaration.dataType, owner) +: newSymbols))
    }
  }

  def visitSubTypeDeclaration(subTypeDeclaration: SubTypeDeclaration, owner: Symbol, context: Context): ReturnType = {
    val dataType = createType(context, subTypeDeclaration.subTypeIndication, subTypeDeclaration.identifier.text)
    val symbol = new TypeSymbol(subTypeDeclaration.identifier, dataType, owner)
    (subTypeDeclaration, context.insertSymbol(symbol))
  }

  def visitUseClause(useClause: UseClause, context: Context): ReturnType = {
    val symbolList = useClause.useList.flatMap {
      name =>
        val library = name.identifiers.head.text
        val archive = if (configuration.designLibrary == library) {
          None
        } else {
          if (name.identifiers.size != 3 && name.identifiers.size != 2) {
            addError(name, SemanticMessage.INVALID_NAME)
          }
          context.libraries.get(library) match {
            case some@Some(arch) => some
            case None =>
              addError(name.identifiers.head, SemanticMessage.NOT_FOUND, "library", library)
              None
          }
        }
        val packagePart = name.identifiers(1)
        val fileName = "package_" + packagePart.text + ".sym"
        val scopes = archive match {
          case None => SymbolTable.getScopesFromInputStream(new FileInputStream(configuration.designLibrary + File.separator + fileName))
          case Some(arch) =>
            arch.getInputStream(fileName) match {
              case Some(stream) => SymbolTable.getScopesFromInputStream(stream)
              case None =>
                addError(packagePart, SemanticMessage.NOT_FOUND, "package", packagePart.text);
                List()
            }
        }
        val scopeSize = scopes.size
        require(scopeSize == 2 || scopeSize == 1, "puh need to think of this code")
        if (name.identifiers.size == 1) {
          val symbol = new PackageHeaderSymbol(packagePart, Map() ++ scopes(scopeSize - 1))
          List(symbol)
        } else {
          val id = name.identifiers(2)
          if ("all" == id.text) {
            scopes(0).valuesIterator.toList
          } else {
            scopes(scopeSize - 1).get(id.text) match {
              case Some(symbol) => List(symbol)
              case None => addError(id, SemanticMessage.NOT_FOUND, "symbol", id.text);
              List()
            }
          }
        }
    }
    (useClause, context.copy(symbolTable = context.symbolTable.insertWithoutCheck(symbolList)))
  }

  def visitVariableAssignmentStatement(varAssignStmt: VariableAssignmentStatement, owner: Symbol, context: Context): ReturnType = varAssignStmt match {
    case stmt: SimpleVariableAssignmentStatement =>
      stmt.target.nameOrAggregate match {
        case Left(name) =>
          val nameExpression = acceptExpression(NameExpression(name), NoType, context)
          nameExpression match {
            case w: WithSymbol[_] if (w.symbol.isInstanceOf[VariableSymbol]) =>
              val varSymbol = w.symbol.asInstanceOf[VariableSymbol]
              if (varSymbol.modifier == RuntimeSymbol.Modifier.IN) addError(varAssignStmt, SemanticMessage.ASSIGN_READ_ONLY, "variable", varSymbol.name)
              varSymbol.used = true
              checkPure(context, varAssignStmt, owner, varSymbol)
            case _ => addError(name.identifier, SemanticMessage.NOT_A, name.identifier.text, "variable")
          }
          val expression = checkExpression(context, stmt.expression, nameExpression.dataType)
          (stmt.copy(expression = expression, nameExpression = nameExpression), context)
        case Right(aggregate) => error("not implemented") // TODO stmt.target.aggregate
      }
  }


  def visitVariableDeclaration(variableDeclaration: VariableDeclaration, owner: Symbol, context: Context): ReturnType = {
    val dataType = createType(context, variableDeclaration.subType)
    owner match {
      case _: ArchitectureSymbol | _: PackageHeaderSymbol | _: PackageBodySymbol | _: EntitySymbol =>
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
      case _: ProtectedType =>
        variableDeclaration.initialValueExpression.foreach(expression => addError(expression, SemanticMessage.PROTECTED_TYPE_INITIAL_VALUE_EXPRESSION))
        None
      case _ => checkExpressionOption(context, variableDeclaration.initialValueExpression, dataType)
    }
    val multiplier = getNextIndex(dataType) //+2 for real and physical, +1 for all other variables
    val symbols = variableDeclaration.identifierList.zipWithIndex.map {
      case (identifier, i) => new VariableSymbol(identifier, dataType, RuntimeSymbol.Modifier.IN_OUT, context.varIndex + (i * multiplier), owner)
    }
    val newNode = variableDeclaration.copy(initialValueExpression = initialValueExpression, symbols = symbols)
    val newContext = context.insertSymbols(symbols).copy(varIndex = context.varIndex + (variableDeclaration.identifierList.size * multiplier) + 1)
    (newNode, newContext)
  }

  def visitWaitStatement(waitStmt: WaitStatement, context: Context): ReturnType = {
    //TODO
    //if (waitStmt.sensitivityList.isDefined) {
    //  waitStmt.sensitivityList.get.foreach(name => context.findSymbol(name, classOf[SignalSymbol]))
    //}
    //TODO checkSignalList(context, sensitivityList)
    val untilCondition = checkExpressionOption(context, waitStmt.untilCondition, SymbolTable.booleanType)
    val forExpression = checkExpressionOption(context, waitStmt.forExpression, SymbolTable.timeType)
    (waitStmt.copy(untilCondition = untilCondition, forExpression = forExpression), context)
  }

  def visitWhileStatement(whileStmt: WhileStatement, owner: Symbol, context: Context): ReturnType = {
    checkIdentifiersOption(whileStmt.label, whileStmt.endLabel)
    val condition = checkExpression(context, whileStmt.condition, SymbolTable.booleanType)
    val (sequentialStatementList, _) = acceptList(whileStmt.sequentialStatementList, owner, context)
    (whileStmt.copy(condition = condition, sequentialStatementList = sequentialStatementList), context)
  }

  def acceptNode(node: ASTNode, owner: Symbol, context: Context): ReturnType = node match {
    case null => (LeafNode, context) //nothing
    case DesignFile(designUnits) =>
      val (units, newContext) = acceptList(designUnits, owner, context)
      (DesignFile(units), newContext)
    case designUnit: DesignUnit => visitDesignUnit(designUnit, owner, context)
    case packageBodyDeclaration: PackageBodyDeclaration => visitPackageBodyDeclaration(packageBodyDeclaration, context)
    case packageDeclaration: PackageDeclaration => visitPackageDeclaration(packageDeclaration, context)
    case entityDeclaration: EntityDeclaration => visitEntityDeclaration(entityDeclaration, context)
    case architectureDeclaration: ArchitectureDeclaration => visitArchitectureDeclaration(architectureDeclaration, context)
    case configurationDeclaration: ConfigurationDeclaration => visitConfigurationDeclaration(configurationDeclaration, context)
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
    case attributeSpec: AttributeSpecification => visitAttributeSpecification(attributeSpec, context)
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
