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

package at.jku.ssw.openvc.util

/**
 * Represents a position in a source file
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.toPosition]]
 */
sealed abstract class Position extends Ordered[Position] {
  /**Is this position not NoPosition?
   * If isDefined is true, line, column, start and end are defined.
   */
  val isDefined = true
  /**Is this position a range position? */
  val isRange = false

  /**
   * returns a new position with a character offset
   * @param characterOffset the offset
   * @return the new position
   */
  def addCharacterOffset(characterOffset: Int): Position = error("Position.addCharacterOffset")

  /**
   * returns a new position with a line offset
   * @param lineOffset the offset
   * @return the new position
   */
  def addLineOffset(lineOffset: Int): Position = error("Position.addLineOffset")

  /**the line in the source file */
  def line: Int

  /**column the character position in the line */
  def column: Int

  /**The start of the position, either of the token or range */
  def start: Int

  /**The end of the position, either of the token or range */
  def end: Int

  /**Result of comparing <code>this</code> with operand <code>that</code>.
   *  returns <code>x</code> where
   *  <code>x &lt; 0</code>    iff    <code>this &lt; that</code>
   *  <code>x == 0</code>   iff    <code>this == that</code>
   *  <code>x &gt; 0</code>    iff    <code>this &gt; that</code>
   */
  override def compare(that: Position): Int =
    if (this == NoPosition) -1
    else if (that == NoPosition) 1
    else if (this.start < that.start) -1
    else if (this.start == that.start) this.end - that.end
    else 1
}

final case class OffsetPosition(line: Int, column: Int, start: Int, end: Int) extends Position {

  override def addLineOffset(lineOffset: Int) = new OffsetPosition(this.line + lineOffset, this.column, -1, -1)

  override def addCharacterOffset(characterOffset: Int) = new OffsetPosition(this.line, this.column + characterOffset, -1, -1)
}

final case class RangePosition(point: OffsetPosition, start: Int, end: Int) extends Position {
  override val isRange = true
  val line = point.line
  val column = point.column
}


object NoPosition extends Position {
  override val isDefined = false

  def line = error("NoPosition.line")

  def column = error("NoPosition.column")

  def start = error("NoPosition.startIndex")

  def end = error("NoPosition.stopIndex")
}