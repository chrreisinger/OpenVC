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

package at.jku.ssw.openvc

/**
 * Base class for all compiler phases
 * @see [[at.jku.ssw.openvc.VHDLCompiler]]
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 */
abstract class Phase {
 /** the name of the compiler phase */
  val name: String

 /** a description of the phase */
  def description = name

  override def toString = name

 /**
  * apply the phase to a compilation unit
  *
  * @param unit the compilation unit to which this phase should be applied
  * @return a `CompilationUnit` that represents the result of the phase
  */
  def apply(unit: CompilationUnit): CompilationUnit
}

/**
 * NoPhase is a dummy phase that throws an exception when applied
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 */
object NoPhase extends Phase {
  val name = "<no phase>"

  def apply(unit: CompilationUnit) = sys.error("NoPhase.run")
}
