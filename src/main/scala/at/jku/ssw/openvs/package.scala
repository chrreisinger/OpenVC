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

package at.jku.ssw

package object openvs {

  import collection.mutable.ArrayBuffer

  type Dimension = Int
  type Index = Int
  type VHDLRange = Range.Inclusive

  type ArrayImpl[A] = ArrayBuffer[A]
  val ArrayImpl = ArrayBuffer

  def copyImpl[A](array: ArrayImpl[A]) = array.clone

  def indexPosition(range: VHDLRange, index: Index) = if (range.start < range.end) (index - range.start) else (range.start - index)
}