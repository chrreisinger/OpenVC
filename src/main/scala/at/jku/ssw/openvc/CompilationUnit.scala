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

import ast.{NoNode, ASTNode, Locatable}
import util.{SourceFile, Position, NoPosition}
import collection.immutable.SortedSet

object CompilationUnit {

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


  final class CompilerMessage(val position: Position, val severity: Severity.Value, val message: String) extends Ordered[CompilerMessage] {
    override def toString = position + " " + message

    override def compare(that: CompilerMessage): Int = this.position.compare(that.position)
  }

  object Severity extends Enumeration {
    val INFO, ERROR, WARNING = Value
  }

}

final case class CompilationUnit(sourceFile: SourceFile,
                                 configuration: CompilationUnit.Configuration,
                                 astNode: ASTNode = NoNode,
                                 private var messagesSet: SortedSet[CompilationUnit.CompilerMessage] = SortedSet()) {

  import java.io.PrintWriter
  import CompilationUnit._

  def messages = messagesSet

  def hasErrors: Boolean = errors.nonEmpty

  def hasWarnings: Boolean = warnings.nonEmpty

  lazy val errors = messagesSet.filter(_.severity == Severity.ERROR)

  lazy val warnings = messagesSet.filter(_.severity == Severity.WARNING)

  def addMessage(position: Position, severity: Severity.Value, message: String, messageParameters: AnyRef*): Option[Nothing] = {
    messagesSet = messagesSet + new CompilerMessage(position, severity, String.format(message, messageParameters.toArray: _*))
    None
  }

  def addError(stmt: Locatable, msg: String, messageParameters: AnyRef*): Option[Nothing] = addError(stmt.position, msg, messageParameters: _*)

  def addError(position: Position, message: String, messageParameters: AnyRef*) = addMessage(position, Severity.ERROR, message, messageParameters: _*)

  def addWarning(stmt: Locatable, message: String, messageParameters: AnyRef*) = addMessage(stmt.position, Severity.WARNING, message, messageParameters: _*)

  def printMessages(writer: PrintWriter) {
    lazy val sourceLines = sourceFile.getLines().toIndexedSeq
    if (hasErrors || hasWarnings) {
      writer.println("errors:" + errors.size + " warnings:" + warnings.size)
    }
    for (msg <- messages) {
      val prefix = msg.severity match {
        case Severity.WARNING => "[warn] "
        case Severity.ERROR => "[err] "
      }
      writer.println(prefix + sourceFile.fileName + ": line:" + msg.position.line + " col:" + msg.position.column + " " + msg.message)
      if (msg.position != NoPosition) {
        writer.println(sourceLines(math.min(msg.position.line - 1, sourceLines.size - 1)))
        writer.println((" " * msg.position.column) + "^")
      }
    }
    writer.flush()
  }
}