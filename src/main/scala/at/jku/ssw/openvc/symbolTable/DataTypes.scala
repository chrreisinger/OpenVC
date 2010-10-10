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

package at.jku.ssw.openvc.symbolTable.dataTypes

import java.io.Serializable
import at.jku.ssw.openvc.symbolTable.SymbolTable
import at.jku.ssw.openvc.symbolTable.symbols._

@SerialVersionUID(234776547415907469L)
abstract sealed class DataType extends Serializable {
  val name: String

  val resolutionFunction: Option[FunctionSymbol] = None

  lazy val attributes = Map[String, AttributeSymbol]()

  lazy val fullName: String = this match {
    case accessType: AccessType => accessType.pointerType.fullName
    case enumerationType: EnumerationType if (enumerationType.baseType.isDefined) => enumerationType.baseType.get.fullName
    case hasOwner: HasOwner =>
      val str = hasOwner.owner match {
        case header: PackageHeaderSymbol => header.name + "_header"
        case process: ProcessSymbol => process.owner.name + "$" + process.name
        case subprogramSymbol: SubprogramSymbol => subprogramSymbol.owner.name + "$" + subprogramSymbol.name
        case typeSymbol: TypeSymbol => typeSymbol.dataType.fullName
        case owner => owner.name
      }
      str + "$" + name
    case _ => name
  }
}

@SerialVersionUID(-8688092999207243317L)
final case class ProtectedType(name: String, subprograms: Seq[SubprogramSymbol], owner: Symbol, implemented: Boolean) extends DataType with HasOwner

trait HasOwner {
  val owner: Symbol
}

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
  val size = (to - from + 1).abs
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
final case class RecordType(name: String, elementList: Seq[(String, DataType)], owner: Symbol) extends CompositeType with HasOwner {
  val elementsMap = elementList.toMap
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
  override lazy val attributes = Map(
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
final case class EnumerationType(name: String, elements: Seq[String], baseType: Option[EnumerationType], owner: Symbol) extends ScalarType with DiscreteType with HasOwner {
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