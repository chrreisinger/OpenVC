package at.jku.ssw.openvc.backend.llvm

import scala.collection.mutable.ListBuffer
import at.jku.ssw.openvc.symbolTable.symbols.SubprogramSymbol
import at.jku.ssw.openvc.symbolTable.dataTypes._
import at.jku.ssw.openvc.symbolTable.SymbolTable

final class LLVMTextBinding

final class LLVMModule(val name: String) {
  val functions = ListBuffer[LLVMFunction]()

  def writeToFile() = println(functions.mkString("\n\n"))

  def createFunction(subprogram: SubprogramSymbol): LLVMFunction = {
    val function = new LLVMFunction(subprogram)
    functions += function
    function
  }
}

final case class LLVMValue(value: String, dataType: String) {
  override val toString = value
}

final case class LLVMBlock(label: String, function: LLVMFunction) {
  def apply() = {
    val string = label + ":"
    /*if (function.code.last.contains(':')) function.code(function.code.size - 1) = string
    else function.code += string
    */
    function.code += string
  }

  override val toString = label
}

final class LLVMFunction(val subprogram: SubprogramSymbol) {

  import LLVMIRGenerator.getLLVMDataType

  val code = ListBuffer[String]()

  def binOp(result: LLVMValue, op: String, op1: LLVMValue, op2: LLVMValue): LLVMValue = {
    // <result> = op <ty> <op1>, <op2>
    code += "   %s = %s %s %s, %s".format(result, op, op1.dataType, op1, op2)
    result
  }

  def fadd(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = binOp(result, "fadd", op1, op2)

  def add(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = binOp(result, "add", op1, op2)

  def fsub(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = binOp(result, "fsub", op1, op2)

  def sub(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = binOp(result, "sub", op1, op2)

  def fmul(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = binOp(result, "fmul", op1, op2)

  def mul(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = binOp(result, "mul", op1, op2)

  def fdiv(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = binOp(result, "fdiv", op1, op2)

  def sdiv(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = binOp(result, "sdiv", op1, op2)

  def srem(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = binOp(result, "srem", op1, op2)

  //  ret <type> <value>       ; Return a value from a non-void function
  def ret(result: LLVMValue) = {
    //ret void                 ; Return from void function
    code += "   ret %s %s".format(result.dataType, result)
  }

  def ret = {
    //ret void                 ; Return from void function
    code += "   ret void"
  }

  def icmp(result: LLVMValue, op: String, op1: LLVMValue, op2: LLVMValue): LLVMValue = {
    //<result> = icmp <cond> <ty> <op1>, <op2>
    code += "   %s = icmp %s %s %s, %s".format(result, op, op1.dataType, op1, op2)
    result
  }

  def fcmp(result: LLVMValue, op: String, op1: LLVMValue, op2: LLVMValue): LLVMValue = {
    //<result> = icmp <cond> <ty> <op1>, <op2>
    code += "   %s = fcmp double %s %s, %s".format(result, op, op1, op2)
    result
  }

  def icmp_eq(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = icmp(result, "eq", op1, op2)

  def icmp_ne(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = icmp(result, "ne", op1, op2)

  def icmp_sgt(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = icmp(result, "sgt", op1, op2)

  def icmp_sge(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = icmp(result, "sge", op1, op2)

  def icmp_slt(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = icmp(result, "slt", op1, op2)

  def icmp_sle(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = icmp(result, "sle", op1, op2)

  def fcmp_oeq(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = fcmp(result, "oeq", op1, op2)

  def fcmp_one(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = fcmp(result, "one", op1, op2)

  def fcmp_ogt(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = fcmp(result, "ogt", op1, op2)

  def fcmp_oge(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = fcmp(result, "oge", op1, op2)

  def fcmp_olt(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = fcmp(result, "olt", op1, op2)

  def fcmp_ole(result: LLVMValue, op1: LLVMValue, op2: LLVMValue) = fcmp(result, "ole", op1, op2)

  def br(dest: LLVMBlock) {
    //br label <dest>          ; Unconditional branch
    code += "   br label %" + dest.label
  }

  def br(cond: LLVMValue, iftrue: LLVMBlock, iffalse: LLVMBlock) {
    //br i1 <cond>, label <iftrue>, label <iffalse>
    code += "   br i1 %s, label %%%s, label %%%s".format(cond, iftrue, iffalse)
  }

  def call(result: LLVMValue, subprogram: SubprogramSymbol, parameters: Seq[LLVMValue]): LLVMValue = {
    //<result> = [tail] call [cconv] [ret attrs] <ty> [<fnty>*] <fnptrval>(<function args>) [fn attrs]
    val mapping = (for ((dataType, value) <- subprogram.parameters.map(s => getLLVMDataType(s.dataType)).zip(parameters)) yield value + " " + dataType).mkString(",")
    code += "   %s = call @%s ( %s )".format(result, subprogram.mangledName, mapping)
    result
  }

  def createBasicBlock(name: String): LLVMBlock = LLVMBlock(name, this)

  override def toString: String = {
    val mapping = (for (symbol <- subprogram.parameters) yield getLLVMDataType(symbol.dataType) + " " + "%" + symbol.name).mkString(",")
    "define %s @%s(%s)".format(subprogram.returnTypeOption.map(getLLVMDataType).getOrElse("void"), subprogram.name, mapping) + "{\n" + "entry:\n" + code.mkString("\n") + "\n}\n"
  }
}