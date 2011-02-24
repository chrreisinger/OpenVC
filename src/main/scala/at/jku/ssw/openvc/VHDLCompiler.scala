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
import util.{Position, NoPosition}

final class CompilerMessage(val position: Position, val message: String) extends Ordered[CompilerMessage] {
  override def toString = position + " " + message

  override def compare(that: CompilerMessage): Int = this.position.compare(that.position)
}

object ASTBuilder {

  import org.antlr.runtime.{CommonTokenStream, CharStream, ANTLRStringStream, ANTLRFileStream}
  import at.jku.ssw.openvc.parser.{VHDLParser, VHDLLexer}

  type ASTResult = (SourceFile, DesignFile, Seq[CompilerMessage])

  sealed abstract class SourceFile extends scala.io.Source {
    val content: Array[Char]
    lazy val iter = content.iterator
  }

  final class ANTLRStream(val stringStream: ANTLRStringStream) extends SourceFile with CharStream {

    //TODO change me to stringStream.getData when possible, to remove 2 Arrays.copyOf calls
    lazy val content = stringStream.toString.toCharArray

    //just forward all calls to the ANTLRStringStream
    def substring(start: Int, stop: Int): String = stringStream.substring(start, stop)

    def LT(i: Int): Int = stringStream.LT(i)

    def getLine: Int = stringStream.getLine

    def setLine(line: Int): Unit = stringStream.setLine(line)

    def setCharPositionInLine(pos: Int): Unit = stringStream.setCharPositionInLine(pos)

    def getCharPositionInLine: Int = stringStream.getCharPositionInLine

    def consume: Unit = stringStream.consume

    def LA(i: Int): Int = {
      val laToken = stringStream.LA(i)
      if (laToken == 0 || laToken == CharStream.EOF) laToken
      else Character.toLowerCase(laToken)
    }

    def mark: Int = stringStream.mark

    def index: Int = stringStream.index

    def rewind(marker: Int): Unit = stringStream.rewind(marker)

    def rewind: Unit = stringStream.rewind

    def release(marker: Int): Unit = stringStream.release(marker)

    def seek(index: Int): Unit = stringStream.seek(index)

    override def size: Int = stringStream.size

    def getSourceName: String = stringStream.getSourceName
  }

  private def fromCharStream(stream: ANTLRStream, configuration: VHDLCompiler.Configuration): ASTResult = {
    val lexer = new VHDLLexer(stream)
    lexer.ams = configuration.amsEnabled
    lexer.vhdl2008 = configuration.vhdl2008
    val tokens = new CommonTokenStream(lexer)
    val parser = new VHDLParser(tokens)
    parser.ams = configuration.amsEnabled
    parser.vhdl2008 = configuration.vhdl2008
    val designFile = parser.design_file()
    (stream, designFile, parser.syntaxErrors ++ lexer.lexerErrors)
  }

  def fromFile(fileName: String, configuration: VHDLCompiler.Configuration): ASTResult =
    fromCharStream(new ANTLRStream(new ANTLRFileStream(fileName)), configuration)

  def fromText(code: String, configuration: VHDLCompiler.Configuration): ASTResult =
    fromCharStream(new ANTLRStream(new ANTLRStringStream(code)), configuration)
}

object VHDLCompiler {

  import backend.jvm.ByteCodeGenerator
  import ASTBuilder.SourceFile
  import java.io.PrintWriter

  final class Configuration(val amsEnabled: Boolean, val vhdl2008: Boolean, val parseOnly: Boolean, val outputDirectory: String, val designLibrary: String, val libraryDirectory: String, val debugCompiler: Boolean, val debugCodeGenerator: Boolean) {
    val libraryOutputDirectory = outputDirectory + designLibrary + java.io.File.separator
  }

  final class CompileResult(val source: SourceFile, val syntaxErrors: Seq[CompilerMessage], val semanticErrors: Seq[CompilerMessage], val semanticWarnings: Seq[CompilerMessage], val designFile: DesignFile, val sourceFile: String) {
    def printErrors(writer: PrintWriter) {
      if (syntaxErrors.nonEmpty || semanticErrors.nonEmpty || semanticWarnings.nonEmpty) {
        writer.println("syntax errors:" + syntaxErrors.size + " semantic errors:" + semanticErrors.size + " semantic warnings:" + semanticWarnings.size)
      }
      def printMessages(prefix: String, messages: Seq[CompilerMessage]) {
        val sourceLines = source.getLines.toIndexedSeq
        for (msg <- messages) {
          writer.println(prefix + sourceFile + ": line:" + msg.position.line + " col:" + msg.position.column + " " + msg.message)
          if (msg.position != NoPosition) {
            writer.println(sourceLines(math.min(msg.position.line - 1, sourceLines.size - 1)).toLowerCase)
            writer.println((" " * msg.position.column) + "^")
          }
        }
      }
      printMessages("[err] ", (semanticErrors ++ syntaxErrors).sorted)
      printMessages("[warn] ", semanticWarnings)
      writer.flush
    }
  }

  private def compile(configuration: Configuration, astBuilder: (String, Configuration) => (SourceFile, DesignFile, Seq[CompilerMessage]), source: String, fileName: String): CompileResult = {
    import java.io.File
    import semanticAnalyzer.SemanticAnalyzer
    val directory = new File(configuration.libraryOutputDirectory)
    if (!directory.exists) directory.mkdirs

    val parseStart = System.currentTimeMillis
    val (sourceFile, designFile, syntaxErrors) = astBuilder(source, configuration)
    val parseTime = System.currentTimeMillis - parseStart

    if (configuration.parseOnly) new CompileResult(sourceFile, syntaxErrors, Seq(), Seq(), designFile, fileName)
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
      new CompileResult(sourceFile, syntaxErrors, semanticErrors, semanticWarnings, designFile, fileName)
    }
  }

  def compileFile(file: String, configuration: Configuration): CompileResult = this.compile(configuration, ASTBuilder.fromFile, file, file)

  def compileFileFromText(code: String, file: String, configuration: Configuration): CompileResult = this.compile(configuration, ASTBuilder.fromText, code, file)
}
