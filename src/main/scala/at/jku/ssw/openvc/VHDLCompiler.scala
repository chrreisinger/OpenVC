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

import ast.declarations.DesignFile
import ast.Position

final class CompilerMessage(val position: Position, val message: String) extends Ordered[CompilerMessage] {
  override def toString = position + " " + message

  override def compare(that: CompilerMessage): Int = this.position.compare(that.position)
}

object ASTBuilder {

  import org.antlr.runtime.{ANTLRStringStream, ANTLRFileStream, CharStream, CommonTokenStream}
  import at.jku.ssw.openvc.parser.{VHDLParser, VHDLLexer}

  private final class CaseInsensitiveStringStream(input: String) extends ANTLRStringStream(input) {
    override def LA(i: Int): Int = {
      val laToken = super.LA(i)
      if (laToken != 0 && laToken != CharStream.EOF) Character.toLowerCase(laToken)
      else laToken
    }
  }

  private final class CaseInsensitiveFileStream(fileName: String) extends ANTLRFileStream(fileName) {
    override def LA(i: Int): Int = {
      val laToken = super.LA(i)
      if (laToken != 0 && laToken != CharStream.EOF) Character.toLowerCase(laToken)
      else laToken
    }
  }

  type ASTResult = (DesignFile, Seq[CompilerMessage])

  private def fromCharStream(caseInsensitiveStringStream: ANTLRStringStream, configuration: VHDLCompiler.Configuration): ASTResult = {
    val lexer = new VHDLLexer(caseInsensitiveStringStream)
    lexer.ams = configuration.amsEnabled
    lexer.vhdl2008 = configuration.vhdl2008
    val tokens = new CommonTokenStream(lexer)
    val parser = new VHDLParser(tokens)
    parser.ams = configuration.amsEnabled
    parser.vhdl2008 = configuration.vhdl2008
    val designFile = parser.design_file()
    (designFile, parser.syntaxErrors ++ lexer.lexerErrors)
  }

  def fromFile(fileName: String, configuration: VHDLCompiler.Configuration): ASTResult =
    fromCharStream(new CaseInsensitiveFileStream(fileName), configuration)

  def fromText(code: String, configuration: VHDLCompiler.Configuration): ASTResult =
    fromCharStream(new CaseInsensitiveStringStream(code), configuration)
}

object VHDLCompiler {

  import at.jku.ssw.openvc.backend.jvm.ByteCodeGenerator
  import java.io.PrintWriter

  final class Configuration(val amsEnabled: Boolean, val vhdl2008: Boolean, val parseOnly: Boolean, val outputDirectory: String, val designLibrary: String, val libraryDirectory: String, val debugCompiler: Boolean, val debugCodeGenerator: Boolean) {
    val libraryOutputDirectory = outputDirectory + designLibrary + java.io.File.separator
  }

  final class CompileResult(val syntaxErrors: Seq[CompilerMessage], val semanticErrors: Seq[CompilerMessage], val semanticWarnings: Seq[CompilerMessage], val designFile: DesignFile, val sourceFile: String) {
    def printErrors(writer: PrintWriter, sourceLinesOption: Option[IndexedSeq[String]]) {
      if (syntaxErrors.nonEmpty || semanticErrors.nonEmpty || semanticWarnings.nonEmpty) {
        writer.println("syntax errors:" + syntaxErrors.size + " semantic errors:" + semanticErrors.size + " semantic warnings:" + semanticWarnings.size)
      }
      def printMessages(prefix: String, messages: Seq[CompilerMessage]) {
        for (msg <- messages) {
          writer.println(prefix + sourceFile + ": line:" + msg.position.line + " col:" + msg.position.column + " " + msg.message)
          sourceLinesOption.foreach {
            sourceLines =>
              if (msg.position != Position.NoPosition) {
                writer.println(sourceLines(math.min(msg.position.line - 1, sourceLines.size - 1)).toLowerCase)
                writer.println((" " * msg.position.column) + "^")
              }
          }
        }
      }
      printMessages("[err]", (semanticErrors ++ syntaxErrors).sorted)
      printMessages("[warn]", semanticWarnings)
      writer.flush
    }
  }

  private def compile(configuration: Configuration, astBuilder: (String, Configuration) => (DesignFile, Seq[CompilerMessage]), source: String, fileName: String): CompileResult = {
    import java.io.File
    import semanticAnalyzer.SemanticAnalyzer
    val directory = new File(configuration.libraryOutputDirectory)
    if (!directory.exists) directory.mkdirs

    val parseStart = System.currentTimeMillis
    val (designFile, syntaxErrors) = astBuilder(source, configuration)
    val parseTime = System.currentTimeMillis - parseStart

    if (configuration.parseOnly) new CompileResult(syntaxErrors, Seq(), Seq(), designFile, fileName)
    else {
      val semanticCheckStart = System.currentTimeMillis
      val (checkedDesignFile, semanticErrors, semanticWarnings) = SemanticAnalyzer(designFile, configuration)
      val semanticCheckTime = System.currentTimeMillis - semanticCheckStart

      val codeGenStart = System.currentTimeMillis
      if (semanticErrors.isEmpty && syntaxErrors.isEmpty) {
        ByteCodeGenerator(configuration, fileName, checkedDesignFile)
      }
      val codeGenTime = System.currentTimeMillis - codeGenStart
      if (configuration.debugCompiler) {
        println("parse time:" + parseTime)
        println("sema check time:" + semanticCheckTime)
        println("code gen time:" + codeGenTime)
        println("complete time:" + (System.currentTimeMillis - parseStart))
      }
      new CompileResult(syntaxErrors, semanticErrors, semanticWarnings, designFile, fileName)
    }
  }

  def compileFile(file: String, configuration: Configuration): CompileResult = this.compile(configuration, ASTBuilder.fromFile, file, file)

  def compileFileFromText(code: String, file: String, configuration: Configuration): CompileResult = this.compile(configuration, ASTBuilder.fromText, code, file)
}
