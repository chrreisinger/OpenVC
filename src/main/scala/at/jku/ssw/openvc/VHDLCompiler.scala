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
 * This object is the compiler that provides the `compile` method to compile a compilation unit.
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 */
object VHDLCompiler {

  import parser.SyntaxAnalyzer
  import semanticAnalyzer.{PreAnalyzerTransformer, SemanticAnalyzer}
  import backend.BackendPhase

 /**
  * compiles a compilation unit and returns the result
  *
  * @example {{{
  *   val unit = VHDLCompiler.compile(new CompilationUnit(SourceFile.fromFile("test.vhd"), configuration)))
  *   unit.printMessages(new java.io.PrintWriter(System.out))
  * }}}
  * @param unit the compilation unit to compile
  * @return a compilation unit with the [[at.jku.ssw.openvc.ast.ASTNode]] and the compiler messages
  */
  def compile(unit: CompilationUnit): CompilationUnit = {
    import annotation.tailrec
    import unit.configuration

    @tailrec
    def run(phases: Seq[Phase], unit: CompilationUnit): CompilationUnit = phases match {
      case Seq() => unit
      case Seq(phase, xs@_*) =>
        val phaseStart = System.currentTimeMillis
        val newUnit = phase(unit)
        val phaseEnd = System.currentTimeMillis - phaseStart
        if (configuration.XdebugCompiler) println(phase.name + " time:" + phaseEnd)
        run(xs, newUnit)
    }

    val phases = configuration.XrunOnlyToPhase match {
      case None => AllPhases
      case Some(phase) => AllPhases.take(AllPhases.indexWhere(_.name == phase))
    }
    run(phases, unit)
  }

  /** all phases needed by the compiler to completely compile one compilation unit*/
  val AllPhases = Seq(SyntaxAnalyzer, PreAnalyzerTransformer, SemanticAnalyzer, BackendPhase)
}
