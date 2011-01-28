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

  lazy val implementationName: String = this match {
    case accessType: AccessType => accessType.pointerType.implementationName
    case enumerationType: EnumerationType if (enumerationType.baseType.isDefined) => enumerationType.baseType.get.implementationName
    case protectedType: ProtectedType => protectedType.owner.implementationName + "$" + name + (if (protectedType.isImplemented) "_body" else "_header")
    case hasOwner: HasOwner => hasOwner.owner.implementationName + "$" + name
    case _ => name
  }
}

@SerialVersionUID(-8688092999207243317L)
final case class ProtectedType(name: String, subprograms: Seq[SubprogramSymbol], owner: Symbol, isImplemented: Boolean) extends DataType with HasOwner

@SerialVersionUID(2345838707170856424L)
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
//var is needed for incomplete type declarations
final case class AccessType(name: String, var pointerType: DataType) extends DataType {
  //override toString, so that we don't get a StackOverflowError Exception, because when an access type points to record type which contains a field of the same access type, we've got a cycle
  override def toString = "AccessType(" + name + "," + pointerType.name + ")"
}

@SerialVersionUID(-5488583920827260162L)
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

@SerialVersionUID(-2605006893808289144L)
abstract sealed class CompositeType extends DataType

@SerialVersionUID(997963117532466580L)
abstract sealed class ArrayType extends CompositeType {
  val elementType: DataType
  val dimensions: Seq[RangeType]
  override lazy val attributes = Map(
    ("left" -> new PreDefinedAttributeSymbol("left", SymbolTable.integerType, Option(SymbolTable.integerType), true)),
    ("right" -> new PreDefinedAttributeSymbol("right", SymbolTable.integerType, Option(SymbolTable.integerType), true)),
    ("low" -> new PreDefinedAttributeSymbol("low", SymbolTable.integerType, Option(SymbolTable.integerType), true)),
    ("high" -> new PreDefinedAttributeSymbol("high", SymbolTable.integerType, Option(SymbolTable.integerType), true)),
    ("range" -> new PreDefinedAttributeSymbol("range", dimensions.head, Option(SymbolTable.integerType), true)),
    ("reverse_range" -> new PreDefinedAttributeSymbol("reverse_range", dimensions.head, Option(SymbolTable.integerType), true)),
    ("length" -> new PreDefinedAttributeSymbol("length", SymbolTable.integerType, Option(SymbolTable.integerType), true)),
    ("ascending" -> new PreDefinedAttributeSymbol("ascending", SymbolTable.booleanType, Option(SymbolTable.integerType), true))
  )
}

@SerialVersionUID(696290634066156396L)
final case class UnconstrainedArrayType(name: String, elementType: DataType, dimensions: Seq[RangeType]) extends ArrayType

@SerialVersionUID(3434904105948977149L)
final case class ConstrainedArrayType(name: String, elementType: DataType, dimensions: Seq[ConstrainedRangeType]) extends ArrayType

@SerialVersionUID(1153487243267887269L)
final case class RecordType(name: String, fields: Seq[(String, DataType)], owner: Symbol) extends CompositeType with HasOwner {
  val fieldsMap = fields.toMap
  val fieldsIndexMap = fields.unzip._1.zipWithIndex.toMap
}

@SerialVersionUID(4190805209591793888L)
final case class FileType(name: String, elementType: DataType) extends DataType

@SerialVersionUID(-1576358965988610486L)
abstract sealed class ScalarType extends DataType {
  val left: AnyVal
  val right: AnyVal
  val lowerBound: AnyVal
  val upperBound: AnyVal
  val isAscending: Boolean
  val baseType: Option[ScalarType]
  lazy val isSubType = baseType.isDefined
  override val resolutionFunction: Option[FunctionSymbol]
  override lazy val attributes = Map(
    ("left" -> new PreDefinedAttributeSymbol("left", this, None)),
    ("right" -> new PreDefinedAttributeSymbol("right", this, None)),
    ("low" -> new PreDefinedAttributeSymbol("low", this, None)),
    ("high" -> new PreDefinedAttributeSymbol("high", this, None)),
    ("ascending" -> new PreDefinedAttributeSymbol("ascending", SymbolTable.booleanType, None)),
    ("image" -> new PreDefinedAttributeSymbol("image", SymbolTable.stringType, Option(this))),
    ("value" -> new PreDefinedAttributeSymbol("value", this, Option(SymbolTable.stringType))),
    ("pos" -> new PreDefinedAttributeSymbol("pos", this, Option(this))),
    ("val" -> new PreDefinedAttributeSymbol("val", this, Option(this))),
    ("succ" -> new PreDefinedAttributeSymbol("succ", this, Option(this))),
    ("leftof" -> new PreDefinedAttributeSymbol("leftof", this, Option(this))),
    ("rightof" -> new PreDefinedAttributeSymbol("rightof", this, Option(this))),
    ("base" -> new PreDefinedAttributeSymbol("base", baseType.getOrElse(this), None))
  )
}

@SerialVersionUID(-5746211454960179435L)
//marker trait
trait DiscreteType {
  val left: Int
  val right: Int
}

@SerialVersionUID(-2424799647169553170L)
final case class EnumerationType(name: String, elements: Seq[String], baseType: Option[EnumerationType], owner: Symbol, override val resolutionFunction: Option[FunctionSymbol] = None) extends ScalarType with DiscreteType with HasOwner {
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
  val isAscending = true

  def contains(element: String): Boolean = internalMap.contains(element)

  def intValue(element: String): Int = internalMap(element)
}

@SerialVersionUID(8486753470499674454L)
abstract sealed class NumericType extends ScalarType

@SerialVersionUID(5078439353614332831L)
final case class IntegerType(name: String, left: Int, right: Int, baseType: Option[IntegerType], override val resolutionFunction: Option[FunctionSymbol] = None) extends NumericType with DiscreteType {
  val lowerBound = math.min(left, right)
  val upperBound = math.max(left, right)
  val isAscending = left < right
}

@SerialVersionUID(5078432353614332831L)
final case class RealType(name: String, left: Double, right: Double, baseType: Option[RealType], override val resolutionFunction: Option[FunctionSymbol] = None) extends NumericType {
  val lowerBound = math.min(left, right)
  val upperBound = math.max(left, right)
  val isAscending = left < right
}

@SerialVersionUID(955663168353310816L)
final case class PhysicalType(name: String, left: Long, right: Long, units: Map[String, Long]) extends NumericType {
  val lowerBound = math.min(left, right)
  val upperBound = math.max(left, right)
  val isAscending = left < right
  val baseType = None
  override val resolutionFunction = None

  def containsUnit(unit: String) = units.contains(unit)
}