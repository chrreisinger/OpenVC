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

object VHDLCompiler {
  def compile(unit: CompilationUnit): CompilationUnit = {
    import java.io.File
    import annotation.tailrec
    import unit.configuration
    import parser.SyntaxAnalyzer
    import semanticAnalyzer.{PreAnalyzerTransformer, SemanticAnalyzer}
    import backend.BackendPhase

    @tailrec
    def run(phases: Seq[Phase], unit: CompilationUnit): CompilationUnit = phases match {
      case Seq() => unit
      case Seq(phase, xs@_*) =>
        val phaseStart = System.currentTimeMillis
        val newUnit = phase(unit)
        val phaseEnd = System.currentTimeMillis - phaseStart
        if (configuration.debugCompiler) println(phase.name + " time:" + phaseEnd)
        run(xs, newUnit)
    }

    val directory = new File(configuration.libraryOutputDirectory)
    if (!directory.exists) directory.mkdirs

    val phases =
      if (configuration.parseOnly) Seq(SyntaxAnalyzer)
      else Seq(SyntaxAnalyzer, PreAnalyzerTransformer, SemanticAnalyzer, BackendPhase)

    run(phases, unit)
  }
}
