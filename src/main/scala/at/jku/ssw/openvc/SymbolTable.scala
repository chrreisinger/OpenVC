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

package at.jku.ssw.openvc.symbolTable

import scala.collection.mutable
import scala.collection.BitSet
import scala.annotation.tailrec

import at.jku.ssw.openvc.ast._
import at.jku.ssw.openvc.ast.declarations.EntityClass.{Value => EntityClass}
import at.jku.ssw.openvc.ast.declarations.SignalDeclaration

import java.io._

object SymbolTable {
  type Scope = Map[String, Symbol]

  @throws(classOf[IOException])
  def getScopesFromInputStream(input: InputStream): Seq[Scope] = {
    @tailrec
    def getScopesFromInputStreamInner(objectInput: ObjectInputStream, list: Seq[Scope]): Seq[Scope] = {
      val obj = try {
        objectInput.readObject.asInstanceOf[Scope]
      } catch {
        case _: EOFException => null
      }
      if (obj == null) return list
      getScopesFromInputStreamInner(objectInput, obj +: list)
    }
    val reader = new ObjectInputStream(input)
    val listOfScopes = getScopesFromInputStreamInner(reader, List())
    reader.close()
    listOfScopes
  }
  //TODO change to vals and move to Context
  var booleanType: EnumerationType = null
  var bitType: EnumerationType = null
  var characterType: EnumerationType = null
  var severityLevel: EnumerationType = null
  var integerType = IntegerType("integer", Integer.MIN_VALUE, Integer.MAX_VALUE, None) //universal_integer
  var realType = RealType("real", java.lang.Double.MIN_VALUE, java.lang.Double.MAX_VALUE, None) //universal_real
  var timeType: PhysicalType = null
  var naturalType: IntegerType = null
  var positiveType: IntegerType = null
  var stringType: ArrayType = null
  var bitVector: ArrayType = null
  var fileOpenKind: EnumerationType = null
  var fileOpenStatus: EnumerationType = null
}
final class SymbolTable(private val scopes: Seq[SymbolTable.Scope]) {
  import SymbolTable.Scope

  override def toString = scopes.mkString

  def currentScope = scopes.head

  def find(name: String): Option[Symbol] = {
    for (scope <- this.scopes) {
      val obj = scope.get(name)
      if (obj.isDefined) return obj
    }
    None
  }

  def findInCurrentScope[T <: Symbol](name: String, clazz: Class[T]): Option[T] =
    currentScope.get(name).flatMap {
      symbol =>
        if (symbol.getClass ne clazz)
          None
        else Some(symbol.asInstanceOf[T])
    }

  def insertWithoutCheck(list: Seq[Symbol]) = {
    val newHead = list.map(x => x.name -> x).toMap ++ this.scopes.head
    new SymbolTable(newHead +: this.scopes.tail)
  }

  def insert(obj: Symbol): SymbolTable = new SymbolTable((this.scopes.head + (obj.name -> obj)) +: this.scopes.tail)

  def insertScopes(scopeList: Seq[Scope]): SymbolTable = new SymbolTable(scopeList.filter(!_.isEmpty) ++ this.scopes)

  def openScope: SymbolTable = new SymbolTable(Map[String, Symbol]() +: this.scopes)

  def dumpTo(out: PrintStream): Unit =
    for ((s, i) <- this.scopes.zipWithIndex) {
      out.println("scope:" + i)
      for (symbol <- s) out.println(symbol._2.name)
    }

  @throws(classOf[IOException])
  def writeToFile(file: String): Unit = {
    // this.dumpTo(System.out)
    val writer = new ObjectOutputStream(new FileOutputStream(file, false))
    try {
      this.scopes.reverse.foreach(scope => if (!scopes.isEmpty) writer.writeObject(scope))
    }
    finally {
      writer.close()
    }
  }
}

abstract sealed class AbstractLibraryArchive {
  def close: Unit

  def getInputStream(file: String): Option[InputStream]
}
final class JarFileLibraryArchive(file: String) extends AbstractLibraryArchive {
  import java.util.jar.{JarFile, JarEntry}

  val jarFile = new JarFile(file)
  type mapType = Map[String, JarEntry]
  val files = {
    @tailrec
    def initInner(map: mapType, entries: java.util.Enumeration[JarEntry]): mapType = {
      if (!entries.hasMoreElements()) return map
      val entry = entries.nextElement()
      initInner(map + (entry.getName() -> entry), entries)
    }
    initInner(Map(), jarFile.entries())
  }

  @throws(classOf[IOException])
  override def close: Unit = this.jarFile.close()

  override def getInputStream(file: String): Option[InputStream] =
    this.files.get(file).map(entry => new BufferedInputStream(this.jarFile.getInputStream(entry)))
}

final class DirectoryLibraryArchive(directory: String) extends AbstractLibraryArchive {
  import java.io.File

  @throws(classOf[IOException])
  override def close: Unit = {}

  override def getInputStream(file: String): Option[InputStream] = //TODO close FileInputStream
    try {
      Some(new FileInputStream(directory + File.separator + file))
    } catch {
      case _ => None
    }
}

@SerialVersionUID(234776547415907469L)
abstract sealed class DataType extends Serializable {
  val name: String

  val resolutionFunction: Option[FunctionSymbol] = None

  val isResolved: Boolean = !this.resolutionFunction.isEmpty

  val isUnresolved: Boolean = this.resolutionFunction.isEmpty

  lazy val attributes = Map[String, AttributeSymbol]()

  def fullName(separator: String = "$"): String = name
}

@SerialVersionUID(-8688092999207243317L)
final case class ProtectedType(name: String, var subprograms: Seq[SubprogramSymbol]) extends DataType

@SerialVersionUID(3917539156743915612L)
case object NoType extends DataType {
  val name = "NoType"
}

@SerialVersionUID(6716628440174747960L)
case object IncompleteType extends DataType {
  val name = "IncompleteType"
}

@SerialVersionUID(3501831035118009139L)
case object NullType extends DataType {
  val name = "NullType"
}

@SerialVersionUID(7611073448233675364L)
final case class AccessType(name: String, var pointerType: DataType) extends DataType { //var is needed for incomplete type declarations
  override def fullName(separator: String = "$"): String = pointerType.fullName(separator)
  //override toString, so that we don't get a StackOverflowError Exception, because when an access type points to record type which contains a field of the same access type, we've got a cycle
  override def toString = "AccessType(" + name + "," + pointerType.name + ")"
}

abstract sealed class RangeType extends DataType {
  val elementType: DataType
  val name = "range type"
}

@SerialVersionUID(-5994541849515628478L)
final case class UnconstrainedRangeType(elementType: DataType) extends RangeType

@SerialVersionUID(6084663176503609733L)
final case class ConstrainedRangeType(elementType: DataType, from: Int, to: Int) extends RangeType {
  val size = to - from + 1
}

abstract sealed class CompositeType extends DataType

@SerialVersionUID(997963117532466580L)
abstract sealed class ArrayType extends CompositeType {
  val elementType: DataType
  val dimensions: Seq[RangeType]
  override lazy val attributes = Map(
    ("left" -> new AttributeSymbol("left", SymbolTable.integerType, Option(SymbolTable.integerType), true)),
    ("right" -> new AttributeSymbol("right", SymbolTable.integerType, Option(SymbolTable.integerType), true)),
    ("low" -> new AttributeSymbol("low", SymbolTable.integerType, Option(SymbolTable.integerType), true)),
    ("high" -> new AttributeSymbol("high", SymbolTable.integerType, Option(SymbolTable.integerType), true)),
    //TODO add dimension parameter, must be calculated at compile time
    //("range" -> new AttributeSymbol("range", UnconstrainedRangeType("range", elementType, Range.Direction.TO), Option(SymbolTable.integerType), true)),
    //("reverse_range" -> new AttributeSymbol("reverse_range", UnconstrainedRangeType("reverse_range", elementType, Range.Direction.DOWNTO), Option(SymbolTable.integerType), true)),
    ("range" -> new AttributeSymbol("range", UnconstrainedRangeType(elementType), None, true)),
    ("reverse_range" -> new AttributeSymbol("reverse_range", UnconstrainedRangeType(elementType), None, true)),
    ("length" -> new AttributeSymbol("length", SymbolTable.integerType, Option(SymbolTable.integerType), true)),
    ("ascending" -> new AttributeSymbol("ascending", SymbolTable.booleanType, Option(SymbolTable.integerType), true))
    )
}

@SerialVersionUID(696290634066156396L)
final case class UnconstrainedArrayType(name: String, elementType: DataType, dimensions: Seq[RangeType]) extends ArrayType

@SerialVersionUID(3434904105948977149L)
final case class ConstrainedArrayType(name: String, elementType: DataType, dimensions: Seq[ConstrainedRangeType]) extends ArrayType

@SerialVersionUID(1153487243267887269L)
final case class RecordType(name: String, elementList: Seq[(String, DataType)], parent: Symbol) extends CompositeType {
  val elementsMap=elementList.toMap
  override def fullName(separator: String = "$"): String = {
    val str = parent match {
      case _: PackageHeaderSymbol => parent.name + "_header"
      case process: ProcessSymbol => process.parent.name + "$" + process.name
      case _ => parent.name
    }
    str + separator + name
  }
}

@SerialVersionUID(4190805209591793888L)
final case class FileType(name: String, elementType: DataType) extends DataType

abstract sealed class ScalarType extends DataType {
  val left: AnyVal
  val right: AnyVal
  val lowerBound: AnyVal
  val upperBound: AnyVal
  val ascending: Boolean
  val isSubType: Boolean
  lazy override val attributes = Map(
    ("left" -> new AttributeSymbol("left", this, None)),
    ("right" -> new AttributeSymbol("right", this, None)),
    ("low" -> new AttributeSymbol("low", this, None)),
    ("high" -> new AttributeSymbol("high", this, None)),
    ("ascending" -> new AttributeSymbol("ascending", SymbolTable.booleanType, None)),
    ("image" -> new AttributeSymbol("image", SymbolTable.stringType, Option(this))),
    ("value" -> new AttributeSymbol("value", this, Option(SymbolTable.stringType))),
    ("pos" -> new AttributeSymbol("pos", this, Option(this))),
    ("val" -> new AttributeSymbol("val", this, Option(this))),
    ("succ" -> new AttributeSymbol("succ", this, Option(this))),
    ("leftof" -> new AttributeSymbol("leftof", this, Option(this))),
    ("rightof" -> new AttributeSymbol("rightof", this, Option(this))) //,
    //("base" -> new AttributeSymbol("base", this, None)) TODO
    )
}

trait DiscreteType //marker trait

@SerialVersionUID(-2424799647169553170L)
final case class EnumerationType(name: String, elements: Seq[String], baseType: Option[EnumerationType], parent: Symbol) extends ScalarType with DiscreteType {
  //TODO recursive classen namen aufbauen
  override def fullName(separator: String = "$"): String = {
    val str = parent match {
      case _: PackageHeaderSymbol => parent.name + "_header"
      case process: ProcessSymbol => process.parent.name + "$" + process.name
      case _ => parent.name
    }
    str + separator + (if (baseType.isDefined) baseType.get.name else this.name)
  }

  private[this] val firstElement = elements.head
  private[this] val lastElement = elements.last

  private[this] val internalMap = baseType match {
    case Some(base) => elements.zip(base.intValue(firstElement) to base.intValue(lastElement)).toMap
    case None => elements.zipWithIndex.toMap
  }

  val left = internalMap(firstElement)
  val right = internalMap(lastElement)
  val lowerBound = math.min(left, right)
  val upperBound = math.max(left, right)
  val ascending = true

  val isSubType: Boolean = baseType.isDefined

  def contains(element: String): Boolean = internalMap.contains(element)

  def intValue(element: String): Int = internalMap(element)
}

abstract sealed class NumericType extends ScalarType //marker trait

@SerialVersionUID(5078439353614332831L)
final case class IntegerType(name: String, left: Int, right: Int, baseType: Option[IntegerType]) extends NumericType with DiscreteType {
  val lowerBound = math.min(left, right)
  val upperBound = math.max(left, right)
  val ascending = left < right

  val isSubType: Boolean = baseType.isDefined
}

@SerialVersionUID(5078432353614332831L)
final case class RealType(name: String, left: Double, right: Double, baseType: Option[RealType]) extends NumericType {
  val lowerBound = math.min(left, right)
  val upperBound = math.max(left, right)
  val ascending = left < right

  val isSubType: Boolean = baseType.isDefined
}

@SerialVersionUID(955663168353310816L)
final case class PhysicalType(name: String, left: Long, right: Long, units: Map[String, Long]) extends NumericType {
  val lowerBound = math.min(left, right)
  val upperBound = math.max(left, right)
  val ascending = left < right
  val isSubType = true

  def containsUnit(unit: String) = units.contains(unit)
}

@SerialVersionUID(6463078064067295176L)
abstract sealed class Symbol extends Serializable with Locatable {
  val identifier: Identifier
  lazy val position = identifier.position
  lazy val name = identifier.text
  lazy val attributes = Map[String, AttributeSymbol]()
}

@SerialVersionUID(7013029205564976456L)
final case class ListOfFunctions(identifier: Identifier, functions: mutable.ListBuffer[FunctionSymbol]) extends Symbol

@SerialVersionUID(2010307013874058143L)
final case class ListOfProcedures(identifier: Identifier, procedures: mutable.ListBuffer[ProcedureSymbol]) extends Symbol

@SerialVersionUID(1238244612233452818L)
final case class AttributeSymbol(identifier: Identifier, dataType: DataType, parameter: Option[DataType], isParameterOptional: Boolean, parent: Symbol, isPredefined: Boolean) extends Symbol {
  def this(name: String, dataType: DataType, parameter: Option[DataType], isParameterOptional: Boolean = false) =
    this (Identifier(name), dataType, parameter, isParameterOptional = isParameterOptional, parent = null, isPredefined = true)
}

object SubProgramFlags {
  val Synchronized = 32
  val Static = 8
}

abstract sealed class SubprogramSymbol extends Symbol {
  val parameters: Seq[RuntimeSymbol]
  val flags: BitSet
}
@SerialVersionUID(-7427096092567821868L)
final case class FunctionSymbol(identifier: Identifier, parameters: Seq[RuntimeSymbol], returnType: DataType, parent: Symbol, flags: BitSet, isPure: Boolean) extends SubprogramSymbol {
  var implemented = false
}

@SerialVersionUID(1492091303227052136L)
final case class ProcedureSymbol(identifier: Identifier, parameters: Seq[RuntimeSymbol], parent: Symbol, flags: BitSet, var isPassive: Boolean) extends SubprogramSymbol {
  var implemented = false
  val copyBackSymbols = parameters.collect {
    _ match {
      case x: VariableSymbol if (x.modifier != RuntimeSymbol.Modifier.IN && x.dataType.isInstanceOf[ScalarType]) => x
    }
  }
  val needsCopyBack = !copyBackSymbols.isEmpty
}

@SerialVersionUID(-1724650145084937620L)
case object Runtime extends Symbol {
  import at.jku.ssw.openvc.codeGenerator.CodeGenerator
  val identifier = Identifier(CodeGenerator.RUNTIME)
}

@SerialVersionUID(4341399901500604597L)
final case class GroupTemplateSymbol(identifier: Identifier, items: Seq[EntityClass], infinite: Boolean) extends Symbol

@SerialVersionUID(-3051930707092602812L)
final case class GroupSymbol(identifier: Identifier) extends Symbol

@SerialVersionUID(-6214450543318369430L)
final case class ProcessSymbol(identifier: Identifier, parent: ArchitectureSymbol, var isPassive: Boolean) extends Symbol

@SerialVersionUID(7523805927766617817L)
final case class TypeSymbol(identifier: Identifier, dataType: DataType) extends Symbol {
  override lazy val attributes = dataType.attributes
}

@SerialVersionUID(1111707794631502222L)
final case class ComponentSymbol(identifier: Identifier, generics: Seq[RuntimeSymbol], ports: Seq[RuntimeSymbol]) extends Symbol

@SerialVersionUID(2222707794631501111L)
final case class EntitySymbol(identifier: Identifier, generics: Seq[RuntimeSymbol], ports: Seq[RuntimeSymbol]) extends Symbol

@SerialVersionUID(5924008907619747454L)
final case class ArchitectureSymbol(identifier: Identifier) extends Symbol

@SerialVersionUID(-848787780393119417L)
final case class PackageHeaderSymbol(identifier: Identifier, localSymbols: Map[String, Symbol]) extends Symbol

@SerialVersionUID(-555587780393116666L)
final case class PackageBodySymbol(identifier: Identifier, localSymbols: Map[String, Symbol]) extends Symbol

object RuntimeSymbol {
  type Modifier = Modifier.Value
  object Modifier extends Enumeration {
    val IN, OUT, IN_OUT = Value
  }
}

abstract sealed class RuntimeSymbol extends Symbol {
  @transient var used = false
  val dataType: DataType
  val index: Int
  val isOptional: Boolean //only used for parameters of functions and procedures
  var owner: Symbol
  override lazy val attributes = dataType.attributes

  def makeCopy(identifier: Identifier, dataType: DataType): RuntimeSymbol
}

@SerialVersionUID(6644707794631509820L)
//isDefined is used for deferred constants
final case class ConstantSymbol(identifier: Identifier, dataType: DataType, index: Int, var owner: Symbol, isOptional: Boolean = false, isDefined: Boolean = true, isDeferred: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType) = this.copy(identifier = identifier, dataType = dataType, owner = this)
}

@SerialVersionUID(2790429723019134248L)
final case class FileSymbol(identifier: Identifier, dataType: DataType, index: Int, var owner: Symbol, isOptional: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType) = this.copy(identifier = identifier, dataType = dataType, owner = this)
}

@SerialVersionUID(3016929919136295810L)
final case class SignalSymbol(identifier: Identifier, dataType: DataType, modifier: RuntimeSymbol.Modifier, signalType: Option[SignalDeclaration.Type], index: Int, var owner: Symbol, isOptional: Boolean = false) extends RuntimeSymbol {
  @transient var driver: ASTNode = null

  def makeCopy(identifier: Identifier, dataType: DataType) = this.copy(identifier = identifier, dataType = dataType, owner = this)

  val signalAttributes = Map(
    ("delayed" -> new AttributeSymbol("delayed", dataType, Option(SymbolTable.timeType))),
    ("stable" -> new AttributeSymbol("stable", SymbolTable.booleanType, Option(SymbolTable.timeType))),
    ("quiet" -> new AttributeSymbol("quiet", SymbolTable.booleanType, Option(SymbolTable.timeType))),
    ("transaction" -> new AttributeSymbol("transaction", SymbolTable.bitType, None)),
    ("event" -> new AttributeSymbol("event", SymbolTable.booleanType, None)),
    ("active" -> new AttributeSymbol("active", SymbolTable.booleanType, None)),
    ("last_event" -> new AttributeSymbol("last_event", SymbolTable.timeType, None)),
    ("last_active" -> new AttributeSymbol("last_active", SymbolTable.timeType, None)),
    ("last_value" -> new AttributeSymbol("last_value", dataType, None)),
    ("driving" -> new AttributeSymbol("driving", SymbolTable.booleanType, None)),
    ("driving_value" -> new AttributeSymbol("driving_value", dataType, None))
    )

  override lazy val attributes = dataType.attributes ++ signalAttributes
}

@SerialVersionUID(1933275516688445881L)
final case class VariableSymbol(identifier: Identifier, dataType: DataType, modifier: RuntimeSymbol.Modifier, index: Int, var owner: Symbol, isOptional: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType) = this.copy(identifier = identifier, dataType = dataType, owner = this)
}