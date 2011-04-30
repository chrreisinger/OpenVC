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
 *  Represents a value of one of three possible types (a disjoint union).
 *  The data constructors [[at.jku.ssw.openvc.util.First]], [[at.jku.ssw.openvc.util.Second]]
 *  and [[at.jku.ssw.openvc.util.Third]] represent the three possible values.
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[http://www.scala-lang.org/archives/downloads/distrib/files/nightly/docs/library/index.html#scala.Either scala.Either]]
 */
sealed abstract class TripleEither[+A, +B, +C] {
 /**
  * Returns `true` if this is a `First`, `false` otherwise.
  */
  def isFirst: Boolean

 /**
  * Returns `true` if this is a `Second`, `false` otherwise.
  */
  def isSecond: Boolean

 /**
  * Returns `true` if this is a `Third`, `false` otherwise.
  */
  def isThird: Boolean
}

/**
 * The first value of the disjoint union, as opposed to `Second` and `Third`.
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @param a the value
 */
final case class First[+A, +B, +C](a: A) extends TripleEither[A, B, C] {
  def isFirst = true

  def isSecond = false

  def isThird = false
}

/**
 * The second value of the disjoint union, as opposed to `First` and `Third`.
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @param b the value
 */
final case class Second[+A, +B, +C](b: B) extends TripleEither[A, B, C] {
  def isFirst = false

  def isSecond = true

  def isThird = false
}

/**
 * The third value of the disjoint union, as opposed to `First` and `Second`.
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @param c the value
 */
final case class Third[+A, +B, +C](c: C) extends TripleEither[A, B, C] {
  def isFirst = false

  def isSecond = false

  def isThird = true
}