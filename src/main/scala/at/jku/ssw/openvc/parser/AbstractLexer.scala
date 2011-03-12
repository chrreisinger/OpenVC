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

package at.jku.ssw.openvc.parser

import org.antlr.runtime.{Lexer => ANTLRLexer, _}
import at.jku.ssw.openvc.CompilerMessage
import at.jku.ssw.openvc.util.OffsetPosition

abstract class AbstractLexer(input: CharStream, state: RecognizerSharedState) extends ANTLRLexer(input, state) {
  var ams = false
  var vhdl2008 = false

  private val lexerErrorList = new scala.collection.immutable.VectorBuilder[CompilerMessage]() //scala.collection.mutable.ListBuffer[A]

  def lexerErrors: Seq[CompilerMessage] = this.lexerErrorList.result()

  override def getErrorMessage(e: RecognitionException, tokenNames: Array[String]): String =
    if (e.isInstanceOf[NoViableAltException]) {
      if (e.c == Token.EOF)
        "Sorry, I scanned to the end of your file from around line " + e.line + " but could not see how to process it. " +
          "This can happen if you forget a closing delimiter such as ''' or '\"'"
      else "The character " + getCharErrorDisplay(e.c) + " is not allowed here."
    }
    else super.getErrorMessage(e, tokenNames)

  override def displayRecognitionError(tokenNames: Array[String], e: RecognitionException) {
    lexerErrorList += new CompilerMessage(position = new OffsetPosition(e.line, e.charPositionInLine, e.index, e.index + 1), message = getErrorMessage(e, tokenNames))
  }
}