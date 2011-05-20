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
import at.jku.ssw.openvc.CompilationUnit
import at.jku.ssw.openvc.util.OffsetPosition

/**
 * Base class for the automatic generated ANTLR lexer
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.Lexer]]
 * @param input where is the lexer drawing characters from?
 * @param state the state of the lexer is collected into a state object so the state can be shared.
 */
private[parser] abstract class BaseLexer(input: CharStream, state: RecognizerSharedState) extends ANTLRLexer(input, state) {
  /** the compilation unit that is used to report errors and that has the configuration options */
  var compilationUnit: CompilationUnit = _

  /** enables VHDL-AMS in the lexer */
  protected lazy val ams = compilationUnit.configuration.enableAMS

  /** enables VHDL 2008 in the lexer */
  protected lazy val vhdl2008 = compilationUnit.configuration.enableVhdl2008

  /** controls that the exponent of a integer literal does not contain a minus e.g. 100E-2 is illegal */
  protected def checkIntegerLiteral() {
    if (getText.contains("-")) {
      val index = getText.indexOf("-")
      val position = new OffsetPosition(getLine, getCharPositionInLine - index + 2, getCharIndex - index + 2, getCharIndex + 3 - index)
      compilationUnit.addError(position, "An exponent for an integer literal must not have a minus sign.")
    }
  }

  /**
   * reports an error found by the lexer
   *
   * @param recognitionException the exception thrown by ANTLR that contains the error information
   */
  protected override def reportError(recognitionException: RecognitionException) {
    val message =
      if (recognitionException.isInstanceOf[NoViableAltException]) {
        if (recognitionException.getUnexpectedType == Token.EOF)
          "Sorry, I scanned to the end of your file from around line " + recognitionException.line + " but could not see how to process it. " +
            "This can happen if you forget a closing delimiter such as ''' or '\"'"
        else "The character " + getCharErrorDisplay(recognitionException.c) + " is not allowed here."
      }
      else super.getErrorMessage(recognitionException, this.getTokenNames)
    compilationUnit.addError(new OffsetPosition(recognitionException.line, recognitionException.charPositionInLine, recognitionException.index, recognitionException.index + 1), message)
  }
}