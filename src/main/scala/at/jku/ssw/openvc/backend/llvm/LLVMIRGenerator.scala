package at.jku.ssw.openvc.backend.llvm

import at.jku.ssw.openvc._
import ast._
import ast.concurrentStatements._
import ast.sequentialStatements._
import ast.declarations._
import ast.expressions._
import symbolTable._
import symbols._
import dataTypes._
import VHDLCompiler.Configuration

object LLVMIRGenerator {

  implicit def toLLVMValue(expression: Expression): LLVMValue = new LLVMValue("%e" + expression.position.line.toString + expression.position.charPosition, getLLVMDataType(expression.dataType))

  private final class LoopLabels(val continueLabel: LLVMBlock, val breakLabel: LLVMBlock)

  private final case class Context(module: LLVMModule, function: LLVMFunction = null, loopLabels: Map[Position, LoopLabels] = Map()) {
    def insertLoopLabels(position: Position, loopLabels: LoopLabels): Context = this.copy(loopLabels = this.loopLabels + (position -> loopLabels))

    implicit val implicitFunction = function
  }

  private object ExpressionContext {

    object JumpKind extends Enumeration {
      val TrueJump, FalseJump = Value
    }

  }

  private final case class ExpressionContext(trueJumpLabel: LLVMBlock, falseJumpLabel: LLVMBlock, kind: ExpressionContext.JumpKind.Value) {
    def invert: ExpressionContext = {
      val newKind = kind match {
        case ExpressionContext.JumpKind.TrueJump => ExpressionContext.JumpKind.FalseJump
        case ExpressionContext.JumpKind.FalseJump => ExpressionContext.JumpKind.TrueJump
      }
      ExpressionContext(trueJumpLabel = falseJumpLabel, falseJumpLabel = trueJumpLabel, kind = newKind)
    }
  }


  def getLLVMDataType(dataType: DataType): String = dataType match {
    case _: IntegerType => "i32"
    case _: RealType => "double"
    case _: PhysicalType => "i64"
    case enumeration: EnumerationType =>
      if (enumeration == SymbolTable.booleanType || enumeration == SymbolTable.bitType) "i1"
      else if (enumeration.elements.size <= Byte.MaxValue) "i8"
      else "i16"
  }

  def apply(configuration: Configuration, sourceFileName: String, designFile: DesignFile) {
    acceptNode(designFile, null)

    def acceptExpressionOption(expr: Option[Expression], contextOption: Option[ExpressionContext] = None, createDebugLineNumberInformation: Boolean = true)(implicit mv: LLVMFunction) =
      expr.foreach(acceptExpression(_, contextOption, createDebugLineNumberInformation))

    def acceptExpression(expression: Expression, context: Option[ExpressionContext] = None, createDebugLineNumberInformation: Boolean = true)(implicit mv: LLVMFunction): LLVMValue = {
      //if (createDebugLineNumberInformation) mv.createDebugLineNumberInformation(expr)
      import mv._
      return expression match {
      //case NoExpression => //nothing
        case term: Term => visitTerm(term)
        /*case aggregate: Aggregate => visitAggregate(aggregate)
    case typeCastExpr: TypeCastExpression => visitTypeCastExpression(typeCastExpr)*/
        case relation: Relation => visitRelation(relation)
        /*case factor: Factor => visitFactor(factor, innerContext)
     case shiftExpression: ShiftExpression =>
     case DefaultExpression(symbol) =>
     case functionCallExpr: FunctionCallExpression => visitFunctionCallExpression(functionCallExpr)
     case logicalExpr: LogicalExpression =>*/
        case simpleExpr: SimpleExpression => visitSimpleExpression(simpleExpr)
        //case newExpr: NewExpression => visitNewExpression(newExpr)
        case literal: Literal => visitLiteral(literal)
        case physicalLiteral: PhysicalLiteral => visitPhysicalLiteral(physicalLiteral)
        /*case ItemExpression(_, symbol) => mv.loadSymbol(symbol)
   case ArrayAccessExpression(symbol, indexes, elementType, expressionOption) =>
   case attributeAccess: AttributeExpression => visitAttributeAccessExpression(attributeAccess)
   case FieldAccessExpression(symbol, field, fieldDataType, expressionOption) =>*/
        case e =>
          println(e.getClass.getName + " " + e.position)
          throw new IllegalArgumentException()
      }

      def visitPhysicalLiteral(physicalLiteral: PhysicalLiteral): LLVMValue = {
        import Literal.Type._
        val dataType = physicalLiteral.dataType.asInstanceOf[PhysicalType]
        physicalLiteral.literalType match {
          case INTEGER_LITERAL => LLVMValue((physicalLiteral.toLong * dataType.units(physicalLiteral.unitSymbol.name)).toString, "i32")
          case REAL_LITERAL => LLVMValue((physicalLiteral.toDouble * dataType.units(physicalLiteral.unitSymbol.name)).toString, "double")
        }
      }

      def visitLiteral(literal: Literal): LLVMValue = {
        import Literal.Type._

        val text = literal.literalType match {
        /*case STRING_LITERAL =>
        if (literal.dataType == SymbolTable.stringType) LDC(literal.text)
        else {
          val dataType = literal.dataType.asInstanceOf[ArrayType].elementType.asInstanceOf[EnumerationType]
          pushInt(literal.text.length)
          NEWARRAY(getJVMArrayType(dataType))
          for ((c, i) <- literal.text.zipWithIndex) {
            DUP
            pushInt(i)
            pushInt(dataType.intValue(c.toString))
            arrayStoreInstruction(dataType)
          }
        }*/
          case INTEGER_LITERAL | REAL_LITERAL => literal.text
          case CHARACTER_LITERAL => literal.value.toString
          case NULL_LITERAL => "null"
        }
        new LLVMValue(text, getLLVMDataType(literal.dataType))
      }

      def visitRelation(relation: Relation): LLVMValue = {
        import Relation.Operator._

        val jumpInverted = context match {
          case Some(context) => context.kind match {
            case ExpressionContext.JumpKind.TrueJump => false
            case ExpressionContext.JumpKind.FalseJump => true
          }
          case None => false
        }
        val cond = relation.right match {
          case NoExpression => error("not implemented")
          /*acceptExpressionInner(relation.left, context)
          relation.operator match {
            case EQ => if (jumpInverted) IFNE(jumpLabel) else IFEQ(jumpLabel)
            case NEQ => if (jumpInverted) IFEQ(jumpLabel) else IFNE(jumpLabel)
          }
          */
          case _ =>
            if (relation.left.dataType == NullType || relation.right.dataType == NullType) {
              /*if (relation.left.dataType == NullType) acceptExpressionInner(relation.right, context)
              else acceptExpressionInner(relation.left, context) //also handles the strange corner case where both expression are null literals, e.g. if (null=null) then ... or if (null/=null) then
              relation.operator match {
                case EQ => if (jumpInverted) IFNONNULL(jumpLabel) else IFNULL(jumpLabel)
                case NEQ => if (jumpInverted) IFNULL(jumpLabel) else IFNONNULL(jumpLabel)
              }
              */
              error("not implemented")
            } else {
              val left = acceptExpression(relation.left)
              val right = acceptExpression(relation.right)
              (relation.left.dataType: @unchecked) match {
              /*  case arrayType: ArrayType =>
              relation.operator match {
                case EQ | NEQ =>
                  if (arrayType.dimensions.size == 1) INVOKESTATIC("java/util/Arrays", "equals", "(" + (getJVMDataType(relation.left.dataType) * 2) + ")Z")
                  else INVOKESTATIC("java/util/Arrays", "deepEquals", "([Ljava/lang/Object;[Ljava/lang/Object;)Z")

                  if (relation.operator == EQ) if (jumpInverted) IFEQ(jumpLabel) else IFNE(jumpLabel)
                  else if (jumpInverted) IFNE(jumpLabel) else IFEQ(jumpLabel)
                case LT | LEQ =>
                  INVOKESTATIC(RUNTIME, relation.operator.toString, "(" + (getJVMDataType(relation.left.dataType) * 2) + ")Z")
                  if (jumpInverted) IFEQ(jumpLabel) else IFNE(jumpLabel)
                case GT | GEQ =>
                //The relations > (greater than) and >= (greater than or equal) are defined to be the complements of the <= and < operators,
                //respectively, for the same two operands.
                  val operatorName = if (relation.operator == GT) "LEQ" else "LT"
                  INVOKESTATIC(RUNTIME, operatorName, "(" + (getJVMDataType(relation.left.dataType) * 2) + ")Z")
                  if (jumpInverted) IFNE(jumpLabel) else IFEQ(jumpLabel)
              }
            case recordType: RecordType =>
              INVOKEVIRTUAL(recordType.implementationName, "equals", "(Ljava/lang/Object;)Z")
              relation.operator match {
                case EQ => if (jumpInverted) IFEQ(jumpLabel) else IFNE(jumpLabel)
                case NEQ => if (jumpInverted) IFNE(jumpLabel) else IFEQ(jumpLabel)
              }
            case _: AccessType =>
              relation.operator match {
                case EQ => if (jumpInverted) IF_ACMPNE(jumpLabel) else IF_ACMPEQ(jumpLabel)
                case NEQ => if (jumpInverted) IF_ACMPEQ(jumpLabel) else IF_ACMPNE(jumpLabel)
              }
              */
                case _: IntegerType | _: EnumerationType | _: PhysicalType =>
                  val op = relation.operator match {
                    case EQ => if (jumpInverted) "neq" else "eq"
                    case NEQ => if (jumpInverted) "eq" else "neq"
                    case LT => if (jumpInverted) "sge" else "slt"
                    case LEQ => if (jumpInverted) "sgt" else "sle"
                    case GT => if (jumpInverted) "sle" else "sgt"
                    case GEQ => if (jumpInverted) "slt" else "sge"
                  }
                  icmp(relation, op, left, right)
                case _: RealType =>
                  val op = relation.operator match {
                    case EQ => if (jumpInverted) "oeq" else "oeq"
                    case NEQ => if (jumpInverted) "oeq" else "oeq"
                    case LT => if (jumpInverted) "oge" else "olt"
                    case LEQ => if (jumpInverted) "ogt" else "ole"
                    case GT => if (jumpInverted) "ole" else "ogt"
                    case GEQ => if (jumpInverted) "olt" else "oge"
                  }
                  fcmp(relation, op, left, right)
              }
            }
        }
        context.foreach(context => br(cond, context.trueJumpLabel, context.falseJumpLabel))
        cond
      }
      def visitTerm(term: Term): LLVMValue = {
        import Term.Operator._

        def loadOperands(): (LLVMValue, LLVMValue) = (acceptExpression(term.left), acceptExpression(term.right))

        ((term.left.dataType, term.right.dataType): @unchecked) match {
          case (_: IntegerType, _: IntegerType) =>
            val (left, right) = loadOperands()
            term.operator match {
              case MUL => mul(term, left, right)
              case DIV => sdiv(term, left, right)
              //case MOD => INVOKESTATIC(RUNTIME, "mod", "(II)I")
              case REM => srem(term, left, right)
            }
          case (_: RealType, _: RealType) =>
            val (left, right) = loadOperands()
            (term.operator: @unchecked) match {
              case MUL => fmul(term, left, right)
              case DIV => fdiv(term, left, right)
            }
        /*case (_: PhysicalType, _: IntegerType) =>
        loadOperands()
        I2L
        (term.operator: @unchecked) match {
          case MUL => LMUL
          case DIV => LDIV
        }
      case (_: PhysicalType, _: RealType) =>
        acceptExpressionInner(term.left)
        L2D
        acceptExpressionInner(term.right)
        (term.operator: @unchecked) match {
          case MUL => DMUL
          case DIV => DDIV
        }
        D2L
      case (_: IntegerType, _: PhysicalType) =>
        acceptExpressionInner(term.left)
        I2L
        acceptExpressionInner(term.right)
        LMUL
      case (_: RealType, _: PhysicalType) =>
        loadOperands()
        L2D
        DMUL
        D2L
      case (_: PhysicalType, _: PhysicalType) =>
        loadOperands()
        LDIV
        L2I
      case (_: RealType, _: IntegerType) =>
        loadOperands()
        I2D
        (term.operator: @unchecked) match {
          case MUL => DMUL
          case DIV => DDIV
        }
      case (_: IntegerType, _: RealType) =>
        acceptExpressionInner(term.left)
        I2D
        acceptExpressionInner(term.right)
        DMUL
        */
        }
      }

      def visitSimpleExpression(simpleExpr: SimpleExpression): LLVMValue = {
        import SimpleExpression._

        val left = acceptExpression(simpleExpr.left)
        (simpleExpr.addOperator, simpleExpr.rightOption) match {
          case (Some(addOperator), Some(rightExpression)) =>
            val right = acceptExpression(rightExpression)
            addOperator match {
              case AddOperator.PLUS =>
                simpleExpr.dataType match {
                  case _: RealType => fadd(simpleExpr, left, right)
                  case _ => add(simpleExpr, left, right)
                }
              case AddOperator.MINUS =>
                simpleExpr.dataType match {
                  case _: RealType => fsub(simpleExpr, left, right)
                  case _ => sub(simpleExpr, left, right)
                }
              case AddOperator.CONCATENATION =>
              /*if (simpleExpr.dataType == SymbolTable.stringType) {
              //INVOKESTATIC(RUNTIME, "stringAppend", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;")
            } else */
                error("not implemented")
            }
          case _ => simpleExpr.signOperator.get match {
            case SignOperator.PLUS => left
            case SignOperator.MINUS =>
              simpleExpr.dataType match {
                case _: RealType => fsub(simpleExpr, LLVMValue("0.0", "double"), left)
                case _ => sub(simpleExpr, LLVMValue("0", getLLVMDataType(simpleExpr.dataType)), left)
              }
          }
        }

      }
      error("na")
    }
    def acceptNodes(nodes: Seq[ASTNode], context: Context): Unit = for (node <- nodes) acceptNode(node, context)

    def acceptNode(node: ASTNode, context: Context): Unit = node match {
      case DesignFile(designUnits) => acceptNodes(designUnits, context)
      case designUnit: DesignUnit => designUnit.libraryUnit.foreach(acceptNode(_, context))
      case packageBodyDeclaration: PackageBodyDeclaration => visitPackageBodyDeclaration(packageBodyDeclaration)
      case packageDeclaration: PackageDeclaration => visitPackageDeclaration(packageDeclaration)
      /*case entityDeclaration: EntityDeclaration => visitEntityDeclaration(entityDeclaration)
      case architectureDeclaration: ArchitectureDeclaration => visitArchitectureDeclaration(architectureDeclaration)
      case configurationDeclaration: ConfigurationDeclaration => visitConfigurationDeclaration(configurationDeclaration)
      //concurrent Statements
      case ifGenerateStmt: IfGenerateStatement => visitIfGenerateStatement(ifGenerateStmt, context)
      case forGenerateStmt: ForGenerateStatement => visitForGenerateStatement(forGenerateStmt)
      case componentInstantiationStmt: ComponentInstantiationStatement => visitComponentInstantiationStatement(componentInstantiationStmt)
      case processStmt: ProcessStatement => visitProcessStatement(processStmt, context)
      case blockStmt: BlockStatement => visitBlockStatement(blockStmt, context)
      //sequential Statements
      case throwStatement: ThrowStatement =>
        context.mv.createDebugLineNumberInformation(throwStatement)
        context.mv.throwNewException(p(classOf[VHDLRuntimeException]), throwStatement.message)
      case assertStmt: AssertionStatement => visitAssertionStatement(assertStmt, context)
      case waitStmt: WaitStatement => visitWaitStatement(waitStmt, context)*/
      case nextStmt: NextStatement => visitNextStatement(nextStmt, context)
      case exitStmt: ExitStatement => visitExitStatement(exitStmt, context)
      /*case nullStmt: NullStatement =>
        context.mv.createDebugLineNumberInformation(nullStmt)
        context.mv.NOP
      case reportStmt: ReportStatement => visitReportStatement(reportStmt, context)
      */
      case returnStmt: ReturnStatement => visitReturnStatement(returnStmt, context)
      case loopStmt: LoopStatement => visitLoopStatement(loopStmt, context)
      case whileStmt: WhileStatement => visitWhileStatement(whileStmt, context)
      /*case forStmt: ForStatement => visitForStatement(forStmt, context)
      case signalAssignmentStmt: SignalAssignmentStatement => visitSignalAssignmentStatement(signalAssignmentStmt, context)
      case variableAssignmentStmt: VariableAssignmentStatement => visitVariableAssignmentStatement(variableAssignmentStmt, context)
      case procedureCallStmt: ProcedureCallStatement => visitProcedureCallStatement(procedureCallStmt, context)
      case caseStmt: CaseStatement => visitCaseStatement(caseStmt, context)*/
      case ifStmt: IfStatement => visitIfStatement(ifStmt, context)
      //declarative Items
      /*case variableDeclaration: VariableDeclaration => visitVariableDeclaration(variableDeclaration, context)
      case constantDeclaration: ConstantDeclaration => visitConstantDeclaration(constantDeclaration, context)
      case signalDeclaration: SignalDeclaration => visitSignalDeclaration(signalDeclaration, context)
      case fileDeclaration: FileDeclaration => visitFileDeclaration(fileDeclaration, context)
      case typeDeclaration: AbstractTypeDeclaration => visitTypeDeclaration(typeDeclaration, context)*/
      case functionDefinition: FunctionDefinition => visitFunctionDefinition(functionDefinition, context)
      //case procedureDefinition: ProcedureDefinition => visitProcedureDefinition(procedureDefinition, context)
      /*case componentDeclaration: ComponentDeclaration => visitComponentDeclaration(componentDeclaration, context)
      case aliasDeclaration: AliasDeclaration => visitAliasDeclaration(aliasDeclaration, context)*/
      case _: FunctionDeclaration | _: ProcedureDeclaration | _: SubTypeDeclaration | _: AttributeDeclaration | _: AttributeSpecification | _: GroupTemplateDeclaration | _: GroupDeclaration | _: UseClause => //nothing
    }

    def createModule(name: String): LLVMModule = new LLVMModule(name)

    def visitPackageDeclaration(packageDeclaration: PackageDeclaration) {
      val cw = createModule(packageDeclaration.symbol.implementationName)
      //initItems(packageDeclaration.identifier.text, Opcodes.ACC_STATIC + Opcodes.ACC_FINAL, "<clinit>", packageDeclaration.declarativeItems, cw)
      acceptNodes(packageDeclaration.declarativeItems, Context(cw))
      cw.writeToFile()
    }

    def visitPackageBodyDeclaration(packageBodyDeclaration: PackageBodyDeclaration) {
      val cw = createModule(packageBodyDeclaration.symbol.implementationName)
      //initItems(packageBodyDeclaration.identifier.text, Opcodes.ACC_STATIC + Opcodes.ACC_FINAL, "<clinit>", packageBodyDeclaration.declarativeItems, cw)
      acceptNodes(packageBodyDeclaration.declarativeItems, Context(cw))
      cw.writeToFile()
    }

    def visitFunctionDefinition(functionDefinition: FunctionDefinition, context: Context) {
      val newContext = context.copy(function = context.module.createFunction(functionDefinition.symbol))
      acceptNodes(functionDefinition.declarativeItems, newContext)
      acceptNodes(functionDefinition.sequentialStatements, newContext)
      /*createDefaultValuesMethods(functionDefinition.parameterInterfaceList, functionSymbol.mangledName, context.cw)

      val flags = (if (functionSymbol.isStatic) Opcodes.ACC_STATIC else 0) + (if (functionSymbol.isSynchronized) Opcodes.ACC_SYNCHRONIZED else 0)
      val mv = context.cw.createMethod(flags = flags, name = functionSymbol.mangledName,
        parameters = getJVMParameterList(functionSymbol.parameters), returnType = getJVMDataType(functionSymbol.returnType))
      val startLabel = RichLabel(mv)
      val stopLabel = RichLabel(mv)

      val newContext = Context(context.cw, mv, Map(), context.designUnit, functionDefinition.localSymbols.collect(_ match {
        case f: FileSymbol => f
      }))
      startLabel()
      acceptNodes(functionDefinition.declarativeItems, newContext)
      acceptNodes(functionDefinition.sequentialStatements, newContext)
      stopLabel()
      mv.createDebugLocalVariableInformation(functionDefinition.localSymbols, startLabel, stopLabel)
      mv.endMethod*/
    }

    def visitIfStatement(ifStmt: IfStatement, context: Context) {
      import context.function._

      //mv.createDebugLineNumberInformation(ifStmt)
      val endLabel = createBasicBlock("endLabel" + ifStmt.position.line)

      def generateCodeForIfThenPart(part: IfStatement.IfThenPart, generateBr: Boolean) {
        val falseJumpLabel = createBasicBlock("falseJump" + part.condition.position.line)
        val trueJumpLabel = createBasicBlock("trueJump" + part.condition.position.line)
        acceptExpression(part.condition, Some(new ExpressionContext(trueJumpLabel = trueJumpLabel, falseJumpLabel = falseJumpLabel, kind = ExpressionContext.JumpKind.FalseJump)))(context.function)
        trueJumpLabel()
        acceptNodes(part.sequentialStatements, context)
        if (generateBr && !part.sequentialStatements.exists(_.isInstanceOf[ReturnStatement])) br(endLabel)
        falseJumpLabel()
      }

      val elseIfList = ifStmt.ifThenList.tail

      generateCodeForIfThenPart(ifStmt.ifThenList.head, elseIfList.nonEmpty || ifStmt.elseSequentialStatements.isDefined)
      if (elseIfList.nonEmpty) {
        elseIfList.foreach(generateCodeForIfThenPart(_, true))
        generateCodeForIfThenPart(elseIfList.last, ifStmt.elseSequentialStatements.isDefined)
      }
      ifStmt.elseSequentialStatements.foreach(acceptNodes(_, context))

      endLabel()

    }
    def visitReturnStatement(returnStmt: ReturnStatement, context: Context) {
      import context.implicitFunction
      import context.function._

      //createDebugLineNumberInformation(returnStmt)
      //An implicit call to FILE_CLOSE exists in a subprogram body for every file object declared in the corresponding subprogram declarative part.
      //Each such call associates a unique file object with the formal parameter F and is called whenever the corresponding subprogram completes its execution.
      //close all open files in a subprogram before we return from it
      /*for (symbol <- context.fileSymbols) {
        ALOAD(symbol.index)
        INVOKESTATIC(RUNTIME, "file_close", "(" + ci(classOf[RuntimeFile]) + ")V")
      }*/
      returnStmt.expression match {
        case None =>
        /*val procedureSymbol = returnStmt.procedureSymbol
        //returns the copyBack Symbols
        procedureSymbol.copyBackSymbols match {
          case Seq() => RETURN
          case Seq(symbol) =>
          //we have only one symbol
            loadSymbol(symbol)
            symbol.dataType.asInstanceOf[ScalarType] match {
              case _: IntegerType | _: EnumerationType => IRETURN
              case _: RealType => DRETURN
              case _: PhysicalType => LRETURN
            }
          case _ =>
          //we need to return more than one symbol, so we create a scala TupleX
            NEW("scala/Tuple" + procedureSymbol.copyBackSymbols.size)
            DUP
            for (symbol <- procedureSymbol.copyBackSymbols) {
              loadSymbol(symbol)
              doBox(symbol.dataType.asInstanceOf[ScalarType])
            }
            INVOKESPECIAL("scala/Tuple" + procedureSymbol.copyBackSymbols.size, "<init>", "(" + (("Ljava/lang/Object;") * procedureSymbol.copyBackSymbols.size) + ")V")
            ARETURN
        }
        */
          ret
        case Some(expr) => ret(acceptExpression(expr))
      }
    }
    def visitLoopStatement(loopStmt: LoopStatement, context: Context) {
      import context.function._
      val continueLabel = createBasicBlock("continue" + loopStmt.position.line)
      val breakLabel = createBasicBlock("break" + loopStmt.position.line)

      //createDebugLineNumberInformation(loopStmt)
      continueLabel()
      acceptNodes(loopStmt.sequentialStatements, context.insertLoopLabels(loopStmt.position, new LoopLabels(continueLabel, breakLabel)))
      br(continueLabel)
      breakLabel()
    }

    def createConditionalJump(stmt: SequentialStatement, condition: Option[Expression], targetLabel: LLVMBlock, context: Context) {
      import context._
      import function._

      val trueJumpLabel = createBasicBlock("true")
      val falseJumpLabel = createBasicBlock("false")
      //createDebugLineNumberInformation(stmt)
      acceptExpressionOption(condition, Some(new ExpressionContext(trueJumpLabel = trueJumpLabel, falseJumpLabel = falseJumpLabel, kind = ExpressionContext.JumpKind.FalseJump)))
      trueJumpLabel()
      br(targetLabel)
      falseJumpLabel()
    }

    def visitNextStatement(nextStmt: NextStatement, context: Context) =
      createConditionalJump(nextStmt, nextStmt.condition, context.loopLabels(nextStmt.loopStatement).continueLabel, context)

    def visitExitStatement(exitStmt: ExitStatement, context: Context) =
      createConditionalJump(exitStmt, exitStmt.condition, context.loopLabels(exitStmt.loopStatement).breakLabel, context)

    def visitWhileStatement(whileStmt: WhileStatement, context: Context) {
      import context._
      import function._

      val conditionTestLabel = createBasicBlock("sdf")
      val breakLabel = createBasicBlock("df")
      val continueLabel = createBasicBlock("dfd")

      //createDebugLineNumberInformation(whileStmt)
      br(conditionTestLabel)
      continueLabel()
      acceptNodes(whileStmt.sequentialStatements, context.insertLoopLabels(whileStmt.position, new LoopLabels(conditionTestLabel, breakLabel)))
      conditionTestLabel()
      acceptExpression(whileStmt.condition, Some(new ExpressionContext(trueJumpLabel = continueLabel, falseJumpLabel = breakLabel, kind = ExpressionContext.JumpKind.TrueJump)))
      breakLabel()
    }
  }
}