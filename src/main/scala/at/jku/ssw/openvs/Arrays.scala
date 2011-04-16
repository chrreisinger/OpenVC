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

package at.jku.ssw.openvs

import at.jku.ssw.openvs.VHDLRuntime.VHDLRuntimeException

abstract class ArrayType {
  val dimensions: Dimension

  def copy: ArrayType

  def left(dim: Dimension): Int

  def right(dim: Dimension): Int

  def low(dim: Dimension): Int

  def high(dim: Dimension): Int

  def range(dim: Dimension): VHDLRange

  def reverse_range(dim: Dimension): VHDLRange

  def length(dim: Dimension): Int

  def ascending(dim: Dimension): Boolean

  protected def reverse(range: VHDLRange) = new VHDLRange(range.end, range.start, -range.step)

  protected def indexPosition(range: VHDLRange, index: Index) = if (range.start < range.end) (index - range.start) else (range.start - index)

  protected def isAscending(range: VHDLRange) = range.start < range.end

  protected def low(range: VHDLRange) = math.min(range.start, range.end)

  protected def high(range: VHDLRange) = math.max(range.start, range.end)
}

final class RuntimeArray1D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A](val data: ArrayImpl[A], val range: VHDLRange) extends ArrayType {

  def outOfRange(dim: Dimension) = throw new VHDLRuntimeException("dimension:" + dim + " is invalid in a one dimensional array")

  val dimensions = 1

  def copy = new RuntimeArray1D(copyImpl(data), range)

  @throws(classOf[VHDLRuntimeException])
  def ascending(dim: Dimension) = dim match {
    case 1 => isAscending(range)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def range(dim: Dimension) = dim match {
    case 1 => range
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def reverse_range(dim: Dimension) = dim match {
    case 1 => reverse(range)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def left(dim: Dimension) = dim match {
    case 1 => range.start
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def right(dim: Dimension) = dim match {
    case 1 => range.end
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def low(dim: Dimension) = dim match {
    case 1 => low(range)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def high(dim: Dimension) = dim match {
    case 1 => high(range)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def length(dim: Dimension) = dim match {
    case 1 => range.size
    case _ => outOfRange(dim)
  }

  def apply(index: Dimension): A =
    data(indexPosition(range, index))

  def update(index: Dimension, value: A) {
    data(indexPosition(range, index)) = value
  }

  def getValue(index: Dimension): A = apply(index)

  def setValue(index: Dimension, value: A) {
    update(index, value)
  }

  override def toString = data.mkString("Array(", ",", ")")

}

final class RuntimeArray2D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A]
(val data: ArrayImpl[ArrayImpl[A]], range1: VHDLRange, range2: VHDLRange) extends ArrayType {

  def outOfRange(dim: Dimension) = throw new VHDLRuntimeException("dimension:" + dim + " is out of range 1 to 2")

  val dimensions = 2

  def copy = new RuntimeArray2D(copyImpl(data), range1, range2)

  @throws(classOf[VHDLRuntimeException])
  def ascending(dim: Dimension) = dim match {
    case 1 => isAscending(range1)
    case 2 => isAscending(range2)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def range(dim: Dimension) = dim match {
    case 1 => range1
    case 2 => range2
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def reverse_range(dim: Dimension) = dim match {
    case 1 => reverse(range1)
    case 2 => reverse(range2)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def left(dim: Dimension) = dim match {
    case 1 => range1.start
    case 2 => range2.start
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def right(dim: Dimension) = dim match {
    case 1 => range1.end
    case 2 => range2.end
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def low(dim: Dimension) = dim match {
    case 1 => low(range1)
    case 2 => low(range2)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def high(dim: Dimension) = dim match {
    case 1 => high(range1)
    case 2 => high(range2)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def length(dim: Dimension) = dim match {
    case 1 => range1.size
    case 2 => range2.size
    case _ => outOfRange(dim)
  }

  def apply(index1: Dimension, index2: Dimension): A =
    data(indexPosition(range1, index1))(indexPosition(range2, index2))

  def update(index1: Dimension, index2: Dimension, value: A) {
    data(indexPosition(range1, index1))(indexPosition(range2, index2)) = value
  }

  def getValue(index1: Dimension, index2: Dimension): A = apply(index1, index2)

  def setValue(index1: Dimension, index2: Dimension, value: A) {
    update(index1, index2, value)
  }

  override def toString = data.map(_.mkString(" ")).mkString("\n")

}

final class RuntimeArray3D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A]
(val data: ArrayImpl[ArrayImpl[ArrayImpl[A]]], range1: VHDLRange, range2: VHDLRange, range3: VHDLRange) extends ArrayType {

  def outOfRange(dim: Dimension) = throw new VHDLRuntimeException("dimension:" + dim + " is out of range 1 to 3")

  val dimensions = 3

  def copy = new RuntimeArray3D(copyImpl(data), range1, range2, range3)

  @throws(classOf[VHDLRuntimeException])
  def ascending(dim: Dimension) = dim match {
    case 1 => isAscending(range1)
    case 2 => isAscending(range2)
    case 3 => isAscending(range3)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def range(dim: Dimension) = dim match {
    case 1 => range1
    case 2 => range2
    case 3 => range3
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def reverse_range(dim: Dimension) = dim match {
    case 1 => reverse(range1)
    case 2 => reverse(range2)
    case 3 => reverse(range3)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def left(dim: Dimension) = dim match {
    case 1 => range1.start
    case 2 => range2.start
    case 3 => range3.start
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def right(dim: Dimension) = dim match {
    case 1 => range1.end
    case 2 => range2.end
    case 3 => range3.end
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def low(dim: Dimension) = dim match {
    case 1 => low(range1)
    case 2 => low(range2)
    case 3 => low(range3)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def high(dim: Dimension) = dim match {
    case 1 => high(range1)
    case 2 => high(range2)
    case 3 => high(range3)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def length(dim: Dimension) = dim match {
    case 1 => range1.size
    case 2 => range2.size
    case 3 => range3.size
    case _ => outOfRange(dim)
  }

  def apply(index1: Dimension, index2: Dimension, index3: Dimension): A =
    data(indexPosition(range1, index1))(indexPosition(range2, index2))(indexPosition(range3, index3))

  def update(index1: Dimension, index2: Dimension, index3: Dimension, value: A) {
    data(indexPosition(range1, index1))(indexPosition(range2, index2))(indexPosition(range3, index3)) = value
  }

  def getValue(index1: Dimension, index2: Dimension, index3: Dimension): A = apply(index1, index2, index3)

  def setValue(index1: Dimension, index2: Dimension, index3: Dimension, value: A) {
    update(index1, index2, index3, value)
  }

  override def toString = data.map(_.mkString(" ")).mkString("\n")

}

final class RuntimeArray4D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A]
(val data: ArrayImpl[ArrayImpl[ArrayImpl[ArrayImpl[A]]]], range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, range4: VHDLRange) extends ArrayType {

  def outOfRange(dim: Dimension) = throw new VHDLRuntimeException("dimension:" + dim + " is out of range 1 to 4")

  val dimensions = 4

  def copy = new RuntimeArray4D(copyImpl(data), range1, range2, range3, range4)

  @throws(classOf[VHDLRuntimeException])
  def ascending(dim: Dimension) = dim match {
    case 1 => isAscending(range1)
    case 2 => isAscending(range2)
    case 3 => isAscending(range3)
    case 4 => isAscending(range4)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def range(dim: Dimension) = dim match {
    case 1 => range1
    case 2 => range2
    case 3 => range3
    case 4 => range4
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def reverse_range(dim: Dimension) = dim match {
    case 1 => reverse(range1)
    case 2 => reverse(range2)
    case 3 => reverse(range3)
    case 4 => reverse(range4)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def left(dim: Dimension) = dim match {
    case 1 => range1.start
    case 2 => range2.start
    case 3 => range3.start
    case 4 => range4.start
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def right(dim: Dimension) = dim match {
    case 1 => range1.end
    case 2 => range2.end
    case 3 => range3.end
    case 4 => range4.end
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def low(dim: Dimension) = dim match {
    case 1 => low(range1)
    case 2 => low(range2)
    case 3 => low(range3)
    case 4 => low(range4)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def high(dim: Dimension) = dim match {
    case 1 => high(range1)
    case 2 => high(range2)
    case 3 => high(range3)
    case 4 => high(range4)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def length(dim: Dimension) = dim match {
    case 1 => range1.size
    case 2 => range2.size
    case 3 => range3.size
    case 4 => range4.size
    case _ => outOfRange(dim)
  }

  def apply(index1: Index, index2: Index, index3: Index, index4: Index): A =
    data(indexPosition(range1, index1))(indexPosition(range2, index2))(indexPosition(range3, index3))(indexPosition(range4, index4))

  def update(index1: Index, index2: Index, index3: Index, index4: Index, value: A) {
    data(indexPosition(range1, index1))(indexPosition(range2, index2))(indexPosition(range3, index3))(indexPosition(range4, index4)) = value
  }

  def getValue(index1: Index, index2: Index, index3: Index, index4: Index): A = apply(index1, index2, index3, index4)

  def setValue(index1: Index, index2: Index, index3: Index, index4: Index, value: A) {
    update(index1, index2, index3, index4, value)
  }

  override def toString = data.map(_.mkString(" ")).mkString("\n")

}

final class RuntimeArray5D[@specialized(scala.Boolean, scala.Byte, scala.Char, scala.Int, scala.Double, scala.Long) A]
(val data: ArrayImpl[ArrayImpl[ArrayImpl[ArrayImpl[ArrayImpl[A]]]]], range1: VHDLRange, range2: VHDLRange, range3: VHDLRange, range4: VHDLRange, range5: VHDLRange) extends ArrayType {

  def outOfRange(dim: Dimension) = throw new VHDLRuntimeException("dimension:" + dim + " is out of range 1 to 5")

  val dimensions = 5

  def copy = new RuntimeArray5D(copyImpl(data), range1, range2, range3, range4, range5)

  @throws(classOf[VHDLRuntimeException])
  def ascending(dim: Dimension) = dim match {
    case 1 => isAscending(range1)
    case 2 => isAscending(range2)
    case 3 => isAscending(range3)
    case 4 => isAscending(range4)
    case 5 => isAscending(range5)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def range(dim: Dimension) = dim match {
    case 1 => range1
    case 2 => range2
    case 3 => range3
    case 4 => range4
    case 5 => range5
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def reverse_range(dim: Dimension) = dim match {
    case 1 => reverse(range1)
    case 2 => reverse(range2)
    case 3 => reverse(range3)
    case 4 => reverse(range4)
    case 5 => reverse(range5)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def left(dim: Dimension) = dim match {
    case 1 => range1.start
    case 2 => range2.start
    case 3 => range3.start
    case 4 => range4.start
    case 5 => range5.start
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def right(dim: Dimension) = dim match {
    case 1 => range1.end
    case 2 => range2.end
    case 3 => range3.end
    case 4 => range4.end
    case 5 => range5.end
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def low(dim: Dimension) = dim match {
    case 1 => low(range1)
    case 2 => low(range2)
    case 3 => low(range3)
    case 4 => low(range4)
    case 5 => low(range5)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def high(dim: Dimension) = dim match {
    case 1 => high(range1)
    case 2 => high(range2)
    case 3 => high(range3)
    case 4 => high(range4)
    case 5 => high(range5)
    case _ => outOfRange(dim)
  }

  @throws(classOf[VHDLRuntimeException])
  def length(dim: Dimension) = dim match {
    case 1 => range1.size
    case 2 => range2.size
    case 3 => range3.size
    case 4 => range4.size
    case 5 => range5.size
    case _ => outOfRange(dim)
  }

  def apply(index1: Index, index2: Index, index3: Index, index4: Index, index5: Index): A =
    data(indexPosition(range1, index1))(indexPosition(range2, index2))(indexPosition(range3, index3))(indexPosition(range4, index4))(indexPosition(range5, index5))

  def update(index1: Index, index2: Index, index3: Index, index4: Index, index5: Index, value: A) {
    data(indexPosition(range1, index1))(indexPosition(range2, index2))(indexPosition(range3, index3))(indexPosition(range4, index4))(indexPosition(range5, index5)) = value
  }

  def getValue(index1: Index, index2: Index, index3: Index, index4: Index, index5: Index): A = apply(index1, index2, index3, index4, index5)

  def setValue(index1: Index, index2: Index, index3: Index, index4: Index, index5: Index, value: A) {
    update(index1, index2, index3, index4, index5, value)
  }

  override def toString = data.map(_.mkString(" ")).mkString("\n")

}