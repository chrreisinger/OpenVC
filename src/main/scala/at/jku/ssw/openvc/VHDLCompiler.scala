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

import ast.{NoNode, ASTNode}
import util.{SourceFile, Position, NoPosition}
import at.jku.ssw.openvc.VHDLCompiler.Configuration
import collection.mutable.ListBuffer

final class CompilerMessage(val position: Position, val message: String) extends Ordered[CompilerMessage] {
  override def toString = position + " " + message

  override def compare(that: CompilerMessage): Int = this.position.compare(that.position)
}

final case class CompilationUnit(source: SourceFile,
                                 configuration: Configuration,
                                 astNode: ASTNode = NoNode,
                                 private val errorBuffer: ListBuffer[CompilerMessage] = new ListBuffer[CompilerMessage],
                                 private val warningBuffer: ListBuffer[CompilerMessage] = new ListBuffer[CompilerMessage]) {

  import java.io.PrintWriter

  def errors = errorBuffer.result()

  def warnings = warningBuffer.result()

  def addErrors(errors: Seq[CompilerMessage]) {errorBuffer ++= errors}

  def addWarnings(warnings: Seq[CompilerMessage]) {warningBuffer ++= warnings}

  def printErrors(writer: PrintWriter) {
    lazy val sourceLines = source.getLines().toIndexedSeq
    if (errors.nonEmpty || warnings.nonEmpty) {
      writer.println("errors:" + errors.size + " warnings:" + warnings.size)
    }
    def printMessages(prefix: String, messages: Seq[CompilerMessage]) {
      for (msg <- messages) {
        writer.println(prefix + source.fileName + ": line:" + msg.position.line + " col:" + msg.position.column + " " + msg.message)
        if (msg.position != NoPosition) {
          writer.println(sourceLines(math.min(msg.position.line - 1, sourceLines.size - 1)))
          writer.println((" " * msg.position.column) + "^")
        }
      }
    }
    printMessages("[err] ", errors.sorted)
    printMessages("[warn] ", warnings)
    writer.flush()
  }
}

object VHDLCompiler {

  final class Configuration(val amsEnabled: Boolean,
                            val vhdl2008: Boolean,
                            val parseOnly: Boolean,
                            val outputDirectory: String,
                            val designLibrary: String,
                            val libraryDirectory: String,
                            val debugCompiler: Boolean,
                            val debugCodeGenerator: Boolean) {
    val libraryOutputDirectory = outputDirectory + designLibrary + java.io.File.separator
  }

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
