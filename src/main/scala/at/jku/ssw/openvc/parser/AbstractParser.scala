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

import org.antlr.runtime._
import at.jku.ssw.openvc.CompilerMessage
import at.jku.ssw.openvc.ast.Identifier
import at.jku.ssw.openvc.util.{OffsetPosition, RangePosition}
import VHDLParser._

abstract class AbstractParser(input: TokenStream, state: RecognizerSharedState) extends Parser(input, state) {
  var ams = false
  var vhdl2008 = false

  type Buffer[A] = scala.collection.immutable.VectorBuilder[A]//scala.collection.mutable.ListBuffer[A]

  protected val syntaxErrorList = new Buffer[CompilerMessage]()

  def syntaxErrors: Seq[CompilerMessage] = this.syntaxErrorList.result

  protected implicit def toPosition(token: Token): OffsetPosition =
    new OffsetPosition(token.getLine, token.getCharPositionInLine, token.asInstanceOf[CommonToken].getStartIndex, token.asInstanceOf[CommonToken].getStopIndex)

  protected implicit def anyToOption[A](value: A): Option[A] = Option(value)

  protected def toIdentifier(token: Token, toLowerCase: Boolean = true): Identifier =
    if (token.getType() != STRING_LITERAL && token.getType() != CHARACTER_LITERAL)
      new Identifier(toPosition(token), if (toLowerCase) token.getText().toLowerCase() else token.getText().replace("""\\""", "\\"))
    else new Identifier(toPosition(token), token.getText())

  private val tokenMap = Map(
    "DOUBLESTAR" -> "**",
    "AMS_ASSIGN" -> "==",
    "LEQ" -> "<=",
    "GEQ" -> ">=",
    "ARROW" -> "=>",
    "NEQ" -> "/=",
    "VAR_ASSIGN" -> ":=",
    "BOX" -> "<>",
    "DBLQUOTE" -> "\"",
    "SEMICOLON" -> ";",
    "COMMA" -> ",",
    "AMPERSAND" -> "&",
    "LPAREN" -> "(",
    "RPAREN" -> ")",
    "LBRACKET" -> "[",
    "RBRACKET" -> "]",
    "COLON" -> ":",
    "MUL" -> "*",
    "DIV" -> "/",
    "PLUS" -> "+",
    "MINUS" -> "-",
    "LT" -> "<",
    "GT" -> ">",
    "EQ" -> "=",
    "BAR" -> "|",
    "DOT" -> ".",
    "MEQ" -> "?=",
    "MNEQ" -> "?/=",
    "MLT" -> "?<",
    "MLEQ" -> "?<=",
    "MGT" -> "?>",
    "MGEQ" -> "?>=",
    "AT" -> "@",
    "QMARK" -> "?",
    "CONDITION_OPERATOR" -> "??",
    "WS" -> "whitespace",
    "NESTED_ML_COMMENT" -> "multiline comment",
    "LINECOMMENT" -> "line comment",
    "BASIC_IDENTIFIER" -> "basic identifier",
    "EXTENDED_IDENTIFIER" -> "extended identifier",
    "BASED_LITERAL" -> "based literal",
    "INTEGER_LITERAL" -> "integer literal",
    "REAL_LITERAL" -> "real literal",
    "APOSTROPHE" -> "apostrophe",
    "STRING_LITERAL" -> "string literal",
    "BIT_STRING_LITERAL" -> "bit string literal",
    "CHARACTER_LITERAL" -> "character literal",
    "GRAPHIC_CHARACTER" -> "graphic character",
    "LETTER_OR_DIGIT" -> "letter or digit",
    "BASED_INTEGER" -> "based integer",
    "BASE_SPECIFIER" -> "base specifier",
    "EXTENDED_DIGIT" -> "extended digit",
    "UPPER_CASE_LETTER" -> "upper case letter",
    "LOWER_CASE_LETTER" -> "lower case letter",
    "SPECIAL_CHARACTER" -> "special character",
    "SPACE_CHARACTER" -> "space character",
    "OTHER_SPECIAL_CHARACTER" -> "other special character",
    "'<<'" -> "<<",
    "'>>'" -> ">>",
    "'^'" -> "^",
    "'\\''" -> "'"
  )

  lazy val vhdlTokenNames = getTokenNames.map(tokenName => tokenMap.getOrElse(tokenName, tokenName.toLowerCase))

  private val ruleMap = Map(
    "sequential_statement" -> "a sequential statement",
    "wait_statement" -> "a wait statement",
    "assertion_statement" -> "an assertion statement",
    "report_statement" -> "a report statement",
    "procedure_call_statement" -> "a procedure call statement",
    "assignment_statement" -> "a signal or variable assignment statement",
    "if_statement" -> "an if statement",
    "case_statement" -> "a case statement",
    "loop_statement" -> "a loop statement",
    "next_statement" -> "a next statement",
    "exit_statement" -> "an exit statement",
    "return_statement" -> "a return statement",
    "null_statement" -> "a null statement",
    "ams_break_statement" -> "a break statement"
  )

  protected def stackPositionDescription(ruleName: String): String =
    ruleMap.get(ruleName) match {
      case Some(description) => description
      case _ =>
        (ruleName.charAt(0) match {
          case 'a' | 'e' | 'i' | 'o' | 'u' => "an "
          case _ => "a "
        }) + ruleName.split('_').mkString(" ")
    }

  private object TokenType extends Enumeration {
    val KeyWord = Value("a keyword")
    val Operator = Value("an operator")
    val Identifier = Value("an identifier")
    val Other = Value("a token")
  }

  private def classifyToken(token: Token): TokenType.Value = classifyToken(token.getType)

  private val operatorKeywords = Set(
    AND, OR, NAND, NOR, XOR, XNOR, SLL, SRL, SLA, SRA, ROL, ROR, MOD, REM, ABS, NOT
  )

  private val extensionsKeywords = Set(
    CONTEXT, FORCE, PARAMETER, RELEASE, DEFAULT, NATURE, TERMINAL, QUANTITY, TOLERANCE, ACROSS, THROUGH,
    SPECTRUM, NOISE, SUBNATURE, LIMIT, REFERENCE, BREAK, PROCEDURAL
  )

  private def classifyToken(tokenType: Int): TokenType.Value =
    if (operatorKeywords.contains(tokenType)) TokenType.Operator
    else if (tokenType >= DOUBLESTAR && tokenType <= CONDITION_OPERATOR) TokenType.Operator
    else if ((tokenType >= ABS && tokenType <= XOR) || extensionsKeywords.contains(tokenType)) TokenType.KeyWord
    else if (tokenType == BASIC_IDENTIFIER || tokenType == EXTENDED_IDENTIFIER) TokenType.Identifier
    else TokenType.Other

  private val parserName = this.getClass.getName

  private def pos: Int = pos(input.LT(1))

  private def pos(tok: Token): Int = (tok.asInstanceOf[CommonToken]).getStartIndex

  /**
   * Calculates the position in the character stream where a missing
   * semi-colon looks like it ought to have been.
   *
   * The method is called from the rule that detects that there should have been
   * a semi colon to terminate a statement or expression, hence the input
   * stream will be positioned too far ahead of the position we are looking to report.
   * To find where we should report we need to search backwards in the input stream for
   * the first non-hidden token before the current one, then position after the end of
   * the text that that token represents.
   */
  private def semiPos: Int = {
    val token =
      if (input.LT(-1) != null) input.LT(-1).asInstanceOf[CommonToken]
      else input.LT(1).asInstanceOf[CommonToken]
    if (token == null) 0
    else token.getStopIndex + 1
  }

  override def displayRecognitionError(tokenNames: Array[String], e: RecognitionException) = {
    val stack = BaseRecognizer.getRuleInvocationStack(e, parserName)
    val stackTop = stack.get(stack.size() - 1).toString()
    val posDescription = stackPositionDescription(stackTop)
    val mb = new scala.collection.mutable.StringBuilder

    // Don't ramble by repeating things like "...extra identifier, which is an identifier that should not be there"
    def getTokenType(tokenType: TokenType.Value): String =
      if (tokenType != TokenType.Other && tokenType != TokenType.Operator && !posDescription.equalsIgnoreCase(tokenType.toString)) " which is " + tokenType.toString
      else ""

    mb append "Sorry, I was trying to understand " append posDescription

    // Rather than just send a diagnostic message containing just a start position, we really want to create an error spanning Erroneous node.
    // The recipient, such as the IDE, can then nicely underline the token(s) that are in error. So we calculate an endPos and start pos to create
    // an erroneous node at the end of this method, defaulting it to the current position.
    val (startPoint, endPoint) = e match {
      case ute: UnwantedTokenException =>
      //There was an extra token in the stream that we can see was extra because the next token after it is the one that would have matched correctly.
      //We have discarded it for error recovery but still need to report it.
        val uwt = ute.getUnexpectedToken().asInstanceOf[CommonToken]
        mb append " but I got confused when I found an extra " append getTokenErrorDisplay(uwt)
        mb append getTokenType(classifyToken(e.token)) append " that should not be there"
        (uwt.getStartIndex(), uwt.getStopIndex() + 1)
      case mte: MissingTokenException =>
      // There was a missing token in the stream that we see was missing because the token we actually saw was one
      // that is a member of the follow-set had the token been present.
        mb append " but I got confused because "
        val tokenClass = classifyToken(mte.expecting)
        if (posDescription.equalsIgnoreCase(tokenClass.toString)) mb append "you seem to have omitted this"
        else if (mte.expecting == Token.EOF) mb append "I was looking for the end of the file here"
        else mb append "you seem to have missed out '" + vhdlTokenNames(mte.expecting) + "'" append (getTokenType(tokenClass)) append " that should be there"
        // The token is missing, so we want to use the char position directly
        // after the previous token and just make it a single character long.
        // This will be the insert point for the missing token, whatever is
        // actually at that position
        (semiPos, pos + 1)
      case mte: MismatchedTokenException =>
      // The token we received was not one we were expecting, but we could neither identify a missing token that would have made it
      // something we can deal with, nor that it was just an accidental extra token that we can throw away. Something like
      // A B C D and we got to B but the token we got was neither C, D nor anything following.
        val tokenClass = classifyToken(e.token)
        mb append " but I got confused when I "
        mb append
          (if (mte.token.getType() == Token.EOF) "hit the end of the file."
          else "saw " + getTokenErrorDisplay(e.token) + (getTokenType(tokenClass)))
        mb append
          (if (tokenClass == TokenType.KeyWord && mte.expecting == VHDLParser.BASIC_IDENTIFIER) ".\n Perhaps you tried to use a keyword as the name of an object (use \\\\keyword if you need to do this)"
          else if (mte.expecting != Token.EOF) ".\n Perhaps you are missing a '" + vhdlTokenNames(mte.expecting) + "'"
          else ".\n I was looking for the end of the file here")

        if (mte.token.getType() == Token.EOF) (semiPos, pos + 1) // The start and end points come directly from the end of the prior token
        else (mte.token.asInstanceOf[CommonToken].getStartIndex(), mte.token.asInstanceOf[CommonToken].getStopIndex() + 1) // The start and end points come directly from the mismatched token.
      case nvae: NoViableAltException =>
      // The token we saw isn't predicted by any alternative path available at this point in the current rule.
      // something like:  ... (B|C|D|E) but we got Z which does not follow from anywhere.
        val tokenClass = classifyToken(e.token)
        mb append " but I got confused when I "
        if (nvae.token.getType() == Token.EOF) {
          mb append "hit the end of the file."
          // The start and end points come directly from the end of the prior token
          (semiPos, pos + 1)
        } else {
          mb append "saw " + getTokenErrorDisplay(e.token) append (getTokenType(tokenClass))
          if (tokenClass == TokenType.KeyWord && (stackTop.equals("name") || stackTop.equals("identifier"))) {
            mb append ".\n Perhaps you tried to use a keyword as the name of an object (use \\\\keyword if you need to do this)"
          }
          // The start and end points come directly from the mismatched token.
          (nvae.token.asInstanceOf[CommonToken].getStartIndex(), nvae.token.asInstanceOf[CommonToken].getStopIndex() + 1)
        }
      case mse: MismatchedSetException =>
      // The parser would have accepted any one of two or more tokens, but the actual token was not in that set and
      // was not a token that we could determine was spurious or from which we could determine that we just had a token missing.
        mb append " but I got confused when I saw " append getTokenErrorDisplay(e.token) append (getTokenType(classifyToken(e.token)))
        mb append ".\n I was looking for one of: " append mse.expecting
        // The start and end points come directly from the mismatched token.
        (e.token.asInstanceOf[CommonToken].getStartIndex(), e.token.asInstanceOf[CommonToken].getStopIndex() + 1)
      case _ =>
        mb append super.getErrorMessage(e, vhdlTokenNames)
        // The start and end points come directly from the mismatched token.
        (e.token.asInstanceOf[CommonToken].getStartIndex(), e.token.asInstanceOf[CommonToken].getStopIndex() + 1)
    }
    syntaxErrorList += new CompilerMessage(position = new RangePosition(e.token, startPoint, endPoint), message = mb.toString)
  }

  //The following code was taken from http://www.antlr.org/wiki/display/ANTLR3/Custom+Syntax+Error+Recovery
  /**
   * Use the current stacked followset to work out the valid tokens that
   * can follow on from the current point in the parse, then recover by
   * eating tokens that are not a member of the follow set we compute.
   *
   * This method is used whenever we wish to force a sync, even though
   * the parser has not yet checked LA(1) for alt selection. This is useful
   * in situations where only a subset of tokens can begin a new construct
   * (such as the start of a new statement in a block) and we want to
   * proactively detect garbage so that the current rule does not exit on
   * on an exception.
   *
   * We could override recover() to make this the default behavior but that
   * is too much like using a sledge hammer to crack a nut. We want finer
   * grained control of the recovery and error mechanisms.
   */
  protected def syncToSet(): Unit = {
    syncToSet(state.following(state._fsp)) // Compute the followset that is in context wherever we are in the rule chain/stack
  }

  protected def syncToSet(follow: BitSet): Unit = {
    var mark = -1
    try {
      mark = input.mark()
      // Consume all tokens in the stream until we find a member of the follow
      // set, which means the next production should be guaranteed to be happy.
      while (!follow.member(input.LA(1))) {
        if (input.LA(1) == Token.EOF) {
          // Looks like we didn't find anything at all that can help us here
          // so we need to rewind to where we were and let normal error handling bail out.
          input.rewind()
          mark = -1
          return
        }
        input.consume()
      }
    } catch {
      case _: Exception => // Just ignore any errors here, we will just let the recognizer try to resync as normal - something must be very screwed.
    }
    finally {
      // Always release the mark we took
      if (mark != -1) input.release(mark)
    }
  }

  class MissingCommonToken(tokenType: Int, text: String) extends CommonToken(tokenType, text)

  /**
   * If the parser is able to recover from the fact that a single token
   * is missing from the input stream, then it will call this method
   * to manufacture a token for use by actions in the grammar.
   *
   * In general the tokens we will need to manufacture here will be things
   * like identifiers, missing parens and braces and other fairly simple constructs
   * as these can be recognized from the union of follow sets that can be
   * constructed at any one point.
   *
   * @param input The token stream where we are normally drawing tokens from
   * @param e The exception that was raised by the parser
   * @param expectedTokenType The type of the token that the parser was expecting to see next
   * @param follow The followset of tokens that can follow on from here
   * @return A newly manufactured token of the required type
   */
  override protected def getMissingSymbol(input: IntStream, e: RecognitionException, expectedTokenType: Int, follow: BitSet): AnyRef = {
    val current =
      if (input.asInstanceOf[TokenStream].LT(1).getType != Token.EOF) input.asInstanceOf[TokenStream].LT(1).asInstanceOf[CommonToken]
      else input.asInstanceOf[TokenStream].LT(-1).asInstanceOf[CommonToken]

    val tokenText = expectedTokenType match {
      case Token.EOF => "<missing EOF>"
      case BASIC_IDENTIFIER | EXTENDED_IDENTIFIER => "<missing IDENTIFIER>"
      case _ => "<missing " + classifyToken(expectedTokenType).toString + ">"
    }
    val t = new MissingCommonToken(expectedTokenType, tokenText)
    t.setLine(current.getLine)
    t.setCharPositionInLine(current.getStopIndex + 1)
    t.setChannel(Token.DEFAULT_CHANNEL)
    t
  }

}