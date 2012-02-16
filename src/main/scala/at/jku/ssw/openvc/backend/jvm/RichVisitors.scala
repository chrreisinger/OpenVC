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

package at.jku.ssw.openvc.backend.jvm

import at.jku.ssw.openvc.symbolTable.dataTypes._
import at.jku.ssw.openvc.symbolTable.symbols._
import ByteCodeGenerator.{getJVMDataType, getJVMName, getBoxedType, getJVMParameterList}

import org.objectweb.asm._

final class RichLabel(mv: MethodVisitor) extends Label {
  def apply() {mv.visitLabel(this)}
}

final class RichClassWriter(private val outputDirectory: String, val className: String, private val cw: ClassWriter, cv: Option[ClassVisitor] = None) extends ClassVisitor(Opcodes.ASM4, cv.getOrElse(cw)) {
  def writeToFile() {
    //create sub-directories if the do not exist (e.g. for foo/bar/classname.class create folders foo/bar/)
    val directory = new java.io.File(outputDirectory + className.substring(0, className.lastIndexOf('/')))
    if (!directory.canWrite) directory.mkdirs

    val outputFile = new java.io.FileOutputStream(outputDirectory + className + ".class")
    try {
      this.visitEnd()
      outputFile.write(cw.toByteArray)
    }
    finally {
      outputFile.close()
    }
  }

  def createEmptyConstructor() {
    val mv = this.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V")
    import mv._
    visitCode()
    ALOAD(0)
    INVOKESPECIAL("java/lang/Object", "<init>", "()V")
    RETURN
    endMethod()
  }

  def createMethod(flags: Int = 0, name: String, parameters: String = "()", returnType: String = "V", signature: String = null): RichMethodVisitor = {
    val parameterString = if (parameters == "()") "()" else "(" + parameters + ")"
    val mv = this.visitMethod(Opcodes.ACC_PUBLIC + flags, name, parameterString + returnType, signature)
    mv.visitCode()
    mv
  }

  def createMethod(flags: Int, subprogramSymbol: SubprogramSymbol): RichMethodVisitor = subprogramSymbol match {
    case procedureSymbol: ProcedureSymbol =>
      val (returnType, signature) = procedureSymbol.copyBackSymbols match {
        case Seq() => ("V", null)
        case Seq(symbol) => (getJVMDataType(symbol), null)
        case _ =>
          ("Lscala/Tuple" + procedureSymbol.copyBackSymbols.size + ";",
            "()Lscala/Tuple" + procedureSymbol.copyBackSymbols.size + "<" + procedureSymbol.copyBackSymbols.map(symbol => getBoxedType(symbol.dataType)).mkString + ">;")
      }
      createMethod(flags = flags, name = procedureSymbol.mangledName, parameters = getJVMParameterList(procedureSymbol.parameters), returnType = returnType, signature = signature)
    case functionSymbol: FunctionSymbol =>
      createMethod(flags = flags, name = functionSymbol.mangledName, parameters = getJVMParameterList(functionSymbol.parameters), returnType = getJVMDataType(functionSymbol.returnType))
  }

  def visitField(access: Int, name: String, desc: String): FieldVisitor =
    super.visitField(access, name, desc, null, null)

  def visitMethod(access: Int, name: String, desc: String, signature: String = null): RichMethodVisitor =
    new RichMethodVisitor(super.visitMethod(access, name, desc, signature, null))
}

final class RichMethodVisitor(mv: MethodVisitor) extends MethodVisitor(Opcodes.ASM4, mv) {

  import at.jku.ssw.openvc.ast.Locatable
  import at.jku.ssw.openvc.util.{Position, NoPosition}

  private[this] var lastLine = -1

  def NOP() {this.visitInsn(Opcodes.NOP)}

  def ACONST_NULL {this.visitInsn(Opcodes.ACONST_NULL)}

  def ICONST_M1 {this.visitInsn(Opcodes.ICONST_M1)}

  def ICONST_0 {this.visitInsn(Opcodes.ICONST_0)}

  def ICONST_1 {this.visitInsn(Opcodes.ICONST_1)}

  def ICONST_2 {this.visitInsn(Opcodes.ICONST_2)}

  def ICONST_3 {this.visitInsn(Opcodes.ICONST_3)}

  def ICONST_4 {this.visitInsn(Opcodes.ICONST_4)}

  def ICONST_5 {this.visitInsn(Opcodes.ICONST_5)}

  def LCONST_0() {this.visitInsn(Opcodes.LCONST_0)}

  def LCONST_1() {this.visitInsn(Opcodes.LCONST_1)}

  def FCONST_0() {this.visitInsn(Opcodes.FCONST_0)}

  def FCONST_1() {this.visitInsn(Opcodes.FCONST_1)}

  def FCONST_2() {this.visitInsn(Opcodes.FCONST_2)}

  def DCONST_0() {this.visitInsn(Opcodes.DCONST_0)}

  def DCONST_1() {this.visitInsn(Opcodes.DCONST_1)}

  def BIPUSH(value: Int) {this.visitIntInsn(Opcodes.BIPUSH, value)}

  def SIPUSH(value: Int) {this.visitIntInsn(Opcodes.SIPUSH, value)}

  def LDC(value: AnyRef) {this.visitLdcInsn(value)}

  def ILOAD(variable: Int) {this.visitVarInsn(Opcodes.ILOAD, variable)}

  def LLOAD(variable: Int) {this.visitVarInsn(Opcodes.LLOAD, variable)}

  def FLOAD(variable: Int) {this.visitVarInsn(Opcodes.FLOAD, variable)}

  def DLOAD(variable: Int) {this.visitVarInsn(Opcodes.DLOAD, variable)}

  def ALOAD(variable: Int) {this.visitVarInsn(Opcodes.ALOAD, variable)}

  def IALOAD() {this.visitInsn(Opcodes.IALOAD)}

  def LALOAD() {this.visitInsn(Opcodes.LALOAD)}

  def FALOAD() {this.visitInsn(Opcodes.FALOAD)}

  def DALOAD() {this.visitInsn(Opcodes.DALOAD)}

  def AALOAD() {this.visitInsn(Opcodes.AALOAD)}

  def BALOAD() {this.visitInsn(Opcodes.BALOAD)}

  def CALOAD() {this.visitInsn(Opcodes.CALOAD)}

  def SALOAD() {this.visitInsn(Opcodes.SALOAD)}

  def ISTORE(variable: Int) {this.visitVarInsn(Opcodes.ISTORE, variable)}

  def LSTORE(variable: Int) {this.visitVarInsn(Opcodes.LSTORE, variable)}

  def FSTORE(variable: Int) {this.visitVarInsn(Opcodes.FSTORE, variable)}

  def DSTORE(variable: Int) {this.visitVarInsn(Opcodes.DSTORE, variable)}

  def ASTORE(variable: Int) {this.visitVarInsn(Opcodes.ASTORE, variable)}

  def IASTORE() {this.visitInsn(Opcodes.IASTORE)}

  def LASTORE() {this.visitInsn(Opcodes.LASTORE)}

  def FASTORE() {this.visitInsn(Opcodes.FASTORE)}

  def DASTORE() {this.visitInsn(Opcodes.DASTORE)}

  def AASTORE {this.visitInsn(Opcodes.AASTORE)}

  def BASTORE() {this.visitInsn(Opcodes.BASTORE)}

  def CASTORE() {this.visitInsn(Opcodes.CASTORE)}

  def SASTORE() {this.visitInsn(Opcodes.SASTORE)}

  def POP() {this.visitInsn(Opcodes.POP)}

  def POP2() {this.visitInsn(Opcodes.POP2)}

  def DUP {this.visitInsn(Opcodes.DUP)}

  def DUP_X1() {this.visitInsn(Opcodes.DUP_X1)}

  def DUP_X2() {this.visitInsn(Opcodes.DUP_X2)}

  def DUP2() {this.visitInsn(Opcodes.DUP2)}

  def DUP2_X1() {this.visitInsn(Opcodes.DUP2_X1)}

  def DUP2_X2() {this.visitInsn(Opcodes.DUP2_X2)}

  def SWAP() {this.visitInsn(Opcodes.SWAP)}

  def IADD {this.visitInsn(Opcodes.IADD)}

  def LADD {this.visitInsn(Opcodes.LADD)}

  def FADD() {this.visitInsn(Opcodes.FADD)}

  def DADD {this.visitInsn(Opcodes.DADD)}

  def ISUB {this.visitInsn(Opcodes.ISUB)}

  def LSUB {this.visitInsn(Opcodes.LSUB)}

  def FSUB() {this.visitInsn(Opcodes.FSUB)}

  def DSUB {this.visitInsn(Opcodes.DSUB)}

  def IMUL {this.visitInsn(Opcodes.IMUL)}

  def LMUL {this.visitInsn(Opcodes.LMUL)}

  def FMUL() {this.visitInsn(Opcodes.FMUL)}

  def DMUL {this.visitInsn(Opcodes.DMUL)}

  def IDIV {this.visitInsn(Opcodes.IDIV)}

  def LDIV {this.visitInsn(Opcodes.LDIV)}

  def FDIV() {this.visitInsn(Opcodes.FDIV)}

  def DDIV {this.visitInsn(Opcodes.DDIV)}

  def IREM {this.visitInsn(Opcodes.IREM)}

  def LREM() {this.visitInsn(Opcodes.LREM)}

  def FREM() {this.visitInsn(Opcodes.FREM)}

  def DREM() {this.visitInsn(Opcodes.DREM)}

  def INEG {this.visitInsn(Opcodes.INEG)}

  def LNEG {this.visitInsn(Opcodes.LNEG)}

  def FNEG() {this.visitInsn(Opcodes.FNEG)}

  def DNEG {this.visitInsn(Opcodes.DNEG)}

  def ISHL() {this.visitInsn(Opcodes.ISHL)}

  def LSHL() {this.visitInsn(Opcodes.LSHL)}

  def ISHR() {this.visitInsn(Opcodes.ISHR)}

  def LSHR() {this.visitInsn(Opcodes.LSHR)}

  def IUSHR() {this.visitInsn(Opcodes.IUSHR)}

  def LUSHR() {this.visitInsn(Opcodes.LUSHR)}

  def IAND() {this.visitInsn(Opcodes.IAND)}

  def LAND() {this.visitInsn(Opcodes.LAND)}

  def IOR() {this.visitInsn(Opcodes.IOR)}

  def LOR() {this.visitInsn(Opcodes.LOR)}

  def IXOR() {this.visitInsn(Opcodes.IXOR)}

  def LXOR() {this.visitInsn(Opcodes.LXOR)}

  def IINC(variable: Int, increment: Int) {this.visitIincInsn(variable, increment)}

  def I2L {this.visitInsn(Opcodes.I2L)}

  def I2F() {this.visitInsn(Opcodes.I2F)}

  def I2D {this.visitInsn(Opcodes.I2D)}

  def L2I {this.visitInsn(Opcodes.L2I)}

  def L2F() {this.visitInsn(Opcodes.L2F)}

  def L2D {this.visitInsn(Opcodes.L2D)}

  def F2I() {this.visitInsn(Opcodes.F2I)}

  def F2L() {this.visitInsn(Opcodes.F2L)}

  def F2D() {this.visitInsn(Opcodes.F2D)}

  def D2I {this.visitInsn(Opcodes.D2I)}

  def D2L {this.visitInsn(Opcodes.D2L)}

  def D2F() {this.visitInsn(Opcodes.D2F)}

  def I2B() {this.visitInsn(Opcodes.I2B)}

  def I2C() {this.visitInsn(Opcodes.I2C)}

  def I2S() {this.visitInsn(Opcodes.I2S)}

  def LCMP() {this.visitInsn(Opcodes.LCMP)}

  def FCMPL() {this.visitInsn(Opcodes.FCMPL)}

  def FCMPG() {this.visitInsn(Opcodes.FCMPG)}

  def DCMPL {this.visitInsn(Opcodes.DCMPL)}

  def DCMPG {this.visitInsn(Opcodes.DCMPG)}

  def IFEQ(label: Label) {this.visitJumpInsn(Opcodes.IFEQ, label)}

  def IFNE(label: Label) {this.visitJumpInsn(Opcodes.IFNE, label)}

  def IFLT(label: Label) {this.visitJumpInsn(Opcodes.IFLT, label)}

  def IFGE(label: Label) {this.visitJumpInsn(Opcodes.IFGE, label)}

  def IFGT(label: Label) {this.visitJumpInsn(Opcodes.IFGT, label)}

  def IFLE(label: Label) {this.visitJumpInsn(Opcodes.IFLE, label)}

  def IF_ICMPEQ(label: Label) {this.visitJumpInsn(Opcodes.IF_ICMPEQ, label)}

  def IF_ICMPNE(label: Label) {this.visitJumpInsn(Opcodes.IF_ICMPNE, label)}

  def IF_ICMPLT(label: Label) {this.visitJumpInsn(Opcodes.IF_ICMPLT, label)}

  def IF_ICMPGE(label: Label) {this.visitJumpInsn(Opcodes.IF_ICMPGE, label)}

  def IF_ICMPGT(label: Label) {this.visitJumpInsn(Opcodes.IF_ICMPGT, label)}

  def IF_ICMPLE(label: Label) {this.visitJumpInsn(Opcodes.IF_ICMPLE, label)}

  def IF_ACMPEQ(label: Label) {this.visitJumpInsn(Opcodes.IF_ACMPEQ, label)}

  def IF_ACMPNE(label: Label) {this.visitJumpInsn(Opcodes.IF_ACMPNE, label)}

  def GOTO(label: Label) {this.visitJumpInsn(Opcodes.GOTO, label)}

  def JSR(label: Label) {this.visitJumpInsn(Opcodes.JSR, label)}

  def RET(variable: Int) {this.visitVarInsn(Opcodes.RET, variable)}

  def TABLESWITCH(min: Int, max: Int, defaultLabel: Label, labels: Array[Label]) {this.visitTableSwitchInsn(min, max, defaultLabel, labels: _*)}

  def LOOKUPSWITCH(defaultLabel: Label, keys: Array[Int], labels: Array[Label]) {this.visitLookupSwitchInsn(defaultLabel, keys, labels)}

  def IRETURN {this.visitInsn(Opcodes.IRETURN)}

  def LRETURN {this.visitInsn(Opcodes.LRETURN)}

  def FRETURN() {this.visitInsn(Opcodes.FRETURN)}

  def DRETURN {this.visitInsn(Opcodes.DRETURN)}

  def ARETURN {this.visitInsn(Opcodes.ARETURN)}

  def RETURN {this.visitInsn(Opcodes.RETURN)}

  def GETSTATIC(owner: String, name: String, desc: String) {this.visitFieldInsn(Opcodes.GETSTATIC, owner, name, desc)}

  def PUTSTATIC(owner: String, name: String, desc: String) {this.visitFieldInsn(Opcodes.PUTSTATIC, owner, name, desc)}

  def GETFIELD(owner: String, name: String, desc: String) {this.visitFieldInsn(Opcodes.GETFIELD, owner, name, desc)}

  def PUTFIELD(owner: String, name: String, desc: String) {this.visitFieldInsn(Opcodes.PUTFIELD, owner, name, desc)}

  def INVOKEVIRTUAL(owner: String, name: String, desc: String) {this.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, name, desc)}

  def INVOKESPECIAL(owner: String, name: String, desc: String) {this.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, name, desc)}

  def INVOKESTATIC(owner: String, name: String, desc: String) {this.visitMethodInsn(Opcodes.INVOKESTATIC, owner, name, desc)}

  def INVOKEINTERFACE(owner: String, name: String, desc: String) {this.visitMethodInsn(Opcodes.INVOKEINTERFACE, owner, name, desc)}

  def INVOKEDYNAMIC(owner: String, name: String, desc: String) {this.visitMethodInsn(Opcodes.INVOKEDYNAMIC, owner, name, desc)}

  def NEW(className: String) {this.visitTypeInsn(Opcodes.NEW, className)}

  def NEWARRAY(value: Int) {this.visitIntInsn(Opcodes.NEWARRAY, value)}

  def ANEWARRAY(className: String) {this.visitTypeInsn(Opcodes.ANEWARRAY, className)}

  def ARRAYLENGTH() {this.visitInsn(Opcodes.ARRAYLENGTH)}

  def ATHROW() {this.visitInsn(Opcodes.ATHROW)}

  def CHECKCAST(className: String) {this.visitTypeInsn(Opcodes.CHECKCAST, className)}

  def CHECKCAST(dataType: DataType) {this.visitTypeInsn(Opcodes.CHECKCAST, getJVMName(dataType))}

  def INSTANCEOF(className: String) {this.visitTypeInsn(Opcodes.INSTANCEOF, className)}

  def MONITORENTER() {this.visitInsn(Opcodes.MONITORENTER)}

  def MONITOREXIT() {this.visitInsn(Opcodes.MONITOREXIT)}

  def MULTIANEWARRAY(desc: String, dims: Int) {this.visitMultiANewArrayInsn(desc, dims)}

  def IFNULL(label: Label) {this.visitJumpInsn(Opcodes.IFNULL, label)}

  def IFNONNULL(label: Label) {this.visitJumpInsn(Opcodes.IFNONNULL, label)}

  def endMethod() {
    try {
      //values will be ignored because COMPUTE_FRAMES and COMPUTE_MAXS are used, but the CheckClassAdapter needs values != 0
      this.visitMaxs(1000, 1000)
      this.visitEnd()
    } catch {
      case ex: Throwable => throw ex //only used, so that i can set a breakpoint here, when i generate bad code and this bad code causes an exception
    }
  }

  def loadInstruction(dataType: DataType, index: Int) {
    this.visitVarInsn((dataType: @unchecked) match {
      case _: IntegerType | _: EnumerationType => Opcodes.ILOAD
      case _: RealType => Opcodes.DLOAD
      case _: PhysicalType => Opcodes.LLOAD
      case _: ArrayType | _: RecordType | _: FileType | _: AccessType | _: ProtectedType => Opcodes.ALOAD
    }, index)
  }

  def storeInstruction(symbol: RuntimeSymbol) {
    this.visitVarInsn((symbol.dataType: @unchecked) match {
      case _: IntegerType | _: EnumerationType => Opcodes.ISTORE
      case _: RealType => Opcodes.DSTORE
      case _: PhysicalType => Opcodes.LSTORE
      case _: ArrayType | _: RecordType | _: FileType | _: AccessType | _: ProtectedType => Opcodes.ASTORE
    }, symbol.index)
  }

  def arrayStoreInstruction(dataType: DataType) {
    this.visitInsn((dataType: @unchecked) match {
      case _: IntegerType => Opcodes.IASTORE
      case _: RealType => Opcodes.DASTORE
      case _: PhysicalType => Opcodes.LASTORE
      case enumeration: EnumerationType =>
        if (enumeration.elements.size <= Byte.MaxValue) Opcodes.BASTORE
        else Opcodes.CASTORE
      case _: ArrayType | _: RecordType | _: FileType | _: AccessType | _: ProtectedType => Opcodes.AASTORE
    })
  }

  def loadSymbol(symbol: RuntimeSymbol) {
    (symbol.owner: @unchecked) match {
      case _: ArchitectureSymbol | _: EntitySymbol | _: ProcessSymbol | _: TypeSymbol => GETFIELD(symbol.owner.implementationName, symbol.name, getJVMDataType(symbol))
      case packageSymbol: PackageSymbol => GETSTATIC(packageSymbol.implementationName, symbol.name, getJVMDataType(symbol))
      case _: SubprogramSymbol => loadInstruction(symbol.dataType, symbol.index)
    }
  }

  def storeSymbol(symbol: RuntimeSymbol, targetType: DataType) {
    symbol.owner match {
      case _: ArchitectureSymbol | _: TypeSymbol | _: ProcessSymbol | _: EntitySymbol => PUTFIELD(symbol.owner.implementationName, symbol.name, getJVMDataType(symbol))
      case _: PackageSymbol => PUTSTATIC(symbol.owner.implementationName, symbol.name, getJVMDataType(symbol))
      case _: SubprogramSymbol => storeInstruction(symbol)
      case r: RuntimeSymbol =>
        symbol.dataType match {
          case recordType: RecordType =>
            PUTFIELD(recordType.implementationName, symbol.name, getJVMDataType(targetType))
          case accessType: AccessType =>
            PUTFIELD(getJVMName(accessType.pointerType), symbol.name, getJVMDataType(targetType))
          case constraintArray: ConstrainedArrayType =>
            arrayStoreInstruction(constraintArray.elementType)
          case unconstrainedArrayType: UnconstrainedArrayType =>
            INVOKEVIRTUAL(getJVMName(unconstrainedArrayType), "setValue", "(" + ("I" * unconstrainedArrayType.dimensions.size) + getJVMDataType(unconstrainedArrayType.elementType) + ")V")
        }
    }
  }

  def pushAnyVal(value: AnyVal) {
    value match {
      case b: Boolean => pushBoolean(b)
      case b: Byte => pushInt(b)
      case s: Short => pushInt(s)
      case c: Char => pushInt(c)
      case i: Int => pushInt(i)
      case f: Float => pushFloat(f)
      case d: Double => pushDouble(d)
      case l: Long => pushLong(l)
      case u: Unit => sys.error("impossible")
    }
  }

  def pushBoolean(value: Boolean) {
    if (value) ICONST_1
    else ICONST_0
  }

  def pushInt(value: Int) {
    value match {
      case -1 => ICONST_M1
      case 0 => ICONST_0
      case 1 => ICONST_1
      case 2 => ICONST_2
      case 3 => ICONST_3
      case 4 => ICONST_4
      case 5 => ICONST_5
      case byteValue if (value >= Byte.MinValue && value <= Byte.MaxValue) => BIPUSH(byteValue)
      case shortValue if (value >= Short.MinValue && value <= Short.MaxValue) => SIPUSH(shortValue)
      case _ =>
        if (value == Int.MinValue) GETSTATIC("java/lang/Integer", "MIN_VALUE", "I")
        else if (value == Int.MaxValue) GETSTATIC("java/lang/Integer", "MAX_VALUE", "I")
        else LDC(Integer.valueOf(value))
    }
  }

  def pushDouble(value: Double) {
    value match {
      case 0.0 => DCONST_0()
      case 1.0 => DCONST_1()
      case java.lang.Double.MIN_VALUE => GETSTATIC("java/lang/Double", "MIN_VALUE", "D")
      case java.lang.Double.MAX_VALUE => GETSTATIC("java/lang/Double", "MAX_VALUE", "D")
      case _ => LDC(java.lang.Double.valueOf(value))
    }
  }

  def pushFloat(value: Float) {
    value match {
      case 0.0 => FCONST_0()
      case 1.0 => FCONST_1()
      case 2.0 => FCONST_2()
      case java.lang.Float.MIN_VALUE => GETSTATIC("java/lang/Float", "MIN_VALUE", "F")
      case java.lang.Float.MAX_VALUE => GETSTATIC("java/lang/Float", "MAX_VALUE", "F")
      case _ => LDC(java.lang.Float.valueOf(value))
    }
  }

  def pushLong(value: Long) {
    value match {
      case 0 => LCONST_0()
      case 1 => LCONST_1()
      case java.lang.Long.MIN_VALUE => GETSTATIC("java/lang/Long", "MIN_VALUE", "J")
      case java.lang.Long.MAX_VALUE => GETSTATIC("java/lang/Long", "MAX_VALUE", "J")
      case _ => LDC(java.lang.Long.valueOf(value))
    }
  }

  def createDebugLineNumberInformation(node: Locatable) {createDebugLineNumberInformation(node.position)}

  def createDebugLineNumberInformation(position: Position) {
    if (position != NoPosition) {
      val line = position.line
      if (lastLine != line) {
        lastLine = line
        val label = createLabel
        label()
        visitLineNumber(line, label)
      }
    }
  }

  def throwNewException(className: String, message: String) {
    NEW(className)
    DUP
    LDC(message)
    INVOKESPECIAL(className, "<init>", "(Ljava/lang/String;)V")
    ATHROW()
  }

  def createLabel = new RichLabel(this)

  def createDebugLocalVariableInformation(symbols: Seq[Symbol], startLabel: RichLabel, stopLabel: RichLabel) {
    symbols.collect(_ match {
      case r: RuntimeSymbol => r
    }).foreach(r => this.visitLocalVariable(r.name, getJVMDataType(r), null, startLabel, stopLabel, r.index))
  }
}