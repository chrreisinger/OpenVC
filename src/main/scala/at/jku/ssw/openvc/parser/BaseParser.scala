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

import collection.immutable.SortedMap
import org.antlr.runtime.{Parser => ANTLRParser, Token => ANTLRToken, _}
import at.jku.ssw.openvc.CompilationUnit
import at.jku.ssw.openvc.ast.Identifier
import at.jku.ssw.openvc.util.{OffsetPosition, RangePosition}
import Parser._

/**
 * Base class for the automatic generated ANTLR parser
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.Parser]]
 * @param input where is the parser drawing characters from?
 * @param state the state of the parser is collected into a state object so the state can be shared.
 */
private[parser] abstract class BaseParser(input: TokenStream, state: RecognizerSharedState) extends ANTLRParser(input, state) {
  /** the compilation unit that is used to report errors and that has the configuration options */
  var compilationUnit: CompilationUnit = _

  /** variable that stores the follow set of a rule */
  protected var followSet: BitSet = _

  /** enables VHDL-AMS in the parser */
  protected lazy val ams = compilationUnit.configuration.enableAMS

  /** enables VHDL 2008 in the parser */
  protected lazy val vhdl2008 = compilationUnit.configuration.enableVhdl2008

  /** the type that is used to store sequences of ASTNodes */
  protected type Buffer[A] = scala.collection.immutable.VectorBuilder[A]

  /** the type of generated Tokens */
  protected type Token = CommonToken

  /** converts the position of a token to a OffsetPosition object */
  protected implicit def toPosition(token: ANTLRToken): OffsetPosition =
    new OffsetPosition(token.getLine, token.getCharPositionInLine, token.asInstanceOf[Token].getStartIndex, token.asInstanceOf[Token].getStopIndex)

  /** converts Any to Option[A] */
  protected implicit def anyToOption[A](value: A): Option[A] = Option(value)

  /** converts a token to Option[Identifier] */
  protected implicit def toIdentifierOption(token: ANTLRToken): Option[Identifier] = if (token != null) Some(toIdentifier(token)) else None

  /** converts a token to a Identifier */
  protected implicit def toIdentifier(token: ANTLRToken): Identifier =
    if (token.getType == IDENTIFIER && token.getText.charAt(0) == '\\') new Identifier(toPosition(token), token.getText.replace("""\\""", "\\"))
    else if (token.getType == IDENTIFIER) new Identifier(toPosition(token), token.getText.toLowerCase)
    else new Identifier(toPosition(token), token.getText)

  /** a Map that contains more meaningful descriptions of tokens */
  private val tokenMap = SortedMap(
    AMS_ASSIGN -> "==",
    VAR_ASSIGN -> ":=",
    BOX -> "<>",
    DBLQUOTE -> "\"",
    COMMA -> ",",
    SEMICOLON -> ";",
    LPAREN -> "(",
    RPAREN -> ")",
    LBRACKET -> "[",
    RBRACKET -> "]",
    COLON -> ":",
    DOT -> ".",
    AMPERSAND -> "&",
    BAR -> "|",
    ARROW -> "=>",
    AT -> "@",
    QMARK -> "?",
    DLT -> "<<",
    DGT -> ">>",
    CIRCUMFLEX -> "^",
    DOUBLESTAR -> "**",
    MUL -> "*",
    DIV -> "/",
    PLUS -> "+",
    MINUS -> "-",
    EQ -> "=",
    NEQ -> "/=",
    LT -> "<",
    GT -> ">",
    LEQ -> "<=",
    GEQ -> ">=",
    MEQ -> "?=",
    MNEQ -> "?/=",
    MLT -> "?<",
    MLEQ -> "?<=",
    MGT -> "?>",
    MGEQ -> "?>=",
    CONDITION_OPERATOR -> "??",
    WHITESPACE -> "whitespace",
    NESTED_ML_COMMENT -> "multiline comment",
    LINE_COMMENT -> "line comment",
    BASIC_IDENTIFIER -> "basic identifier",
    EXTENDED_IDENTIFIER -> "extended identifier",
    BASED_LITERAL -> "based literal",
    INTEGER_LITERAL -> "integer literal",
    REAL_LITERAL -> "real literal",
    APOSTROPHE -> "apostrophe",
    STRING_LITERAL -> "string literal",
    BIT_STRING_LITERAL -> "bit string literal",
    CHARACTER_LITERAL -> "character literal",
    GRAPHIC_CHARACTER -> "graphic character",
    LETTER_OR_DIGIT -> "letter or digit",
    BASED_INTEGER -> "based integer",
    BASE_SPECIFIER -> "base specifier",
    EXTENDED_DIGIT -> "extended digit",
    UPPER_CASE_LETTER -> "upper case letter",
    LOWER_CASE_LETTER -> "lower case letter",
    SPECIAL_CHARACTER -> "special character",
    SPACE_CHARACTER -> "space character",
    OTHER_SPECIAL_CHARACTER -> "other special character"
  )

  /** an array that contains for each token type a user friendly string */
  private lazy val vhdlTokenNames = (SortedMap(getTokenNames.zipWithIndex.map(_.swap): _*) ++ tokenMap).values.toArray

  /** a Map that contains more meaningful descriptions of parser rules */
  private val ruleMap = Map(
    "assignment_statement" -> "a signal or variable assignment statement",
    "label_colon" -> "identifier :"
  )

  /**
   * This enumeration classifies the different tokens in keywords, operators, identifiers and others
   * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
   */
  private object TokenType extends Enumeration {
    val KeyWord = Value("a keyword")
    val Operator = Value("an operator")
    val Identifier = Value("an identifier")
    val Other = Value("a token")
  }

  /**
   * classifies a token
   *
   * @param token the token
   * @return the class to which this token belongs
   */
  private def classifyToken(token: ANTLRToken): TokenType.Value = classifyToken(token.getType)

  /** a Set of token types that are keywords and operators */
  private val operatorKeywords = Set(AND, OR, NAND, NOR, XOR, XNOR, SLL, SRL, SLA, SRA, ROL, ROR, MOD, REM, ABS, NOT)

  /**
   * classifies a token type
   *
   * @param tokenType the token type
   * @return the class to which this token belongs
   */
  private def classifyToken(tokenType: Int): TokenType.Value =
    if (operatorKeywords.contains(tokenType) || (tokenType >= DOUBLESTAR && tokenType <= CONDITION_OPERATOR)) TokenType.Operator
    else if (tokenType >= ABS && tokenType <= PROCEDURAL) TokenType.KeyWord
    else if (tokenType == IDENTIFIER) TokenType.Identifier
    else TokenType.Other

  /** the name of the parser class */
  private val parserName = this.getClass.getName

  /**
   * reports an error found by the parser
   *
   * @param recognitionException the exception thrown by ANTLR that contains the error information
   */
  protected override def reportError(recognitionException: RecognitionException) {
    /** @return returns the start index of the next token */
    def pos: Int = input.LT(1).asInstanceOf[Token].getStartIndex

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
    def semiPos: Int = {
      val token =
        if (input.LT(-1) != null) input.LT(-1).asInstanceOf[Token]
        else input.LT(1).asInstanceOf[Token]
      if (token == null) 0
      else token.getStopIndex + 1
    }

    if (state.errorRecovery) return
    state.syntaxErrors += 1 // don't count spurious
    state.errorRecovery = true

    val stack = BaseRecognizer.getRuleInvocationStack(recognitionException, parserName)
    val stackTop = stack.get(stack.size() - 1).toString
    val posDescription = ruleMap.get(stackTop).getOrElse {
      val name = stackTop.split('_').filter(string => string != "v2008" && string != "ams").mkString(" ")
      name.charAt(0) match {
        case 'a' | 'e' | 'i' | 'o' | 'u' => "an " + name
        case _ => "a " + name
      }
    }
    val sb = new scala.collection.mutable.StringBuilder
    val token = recognitionException.token.asInstanceOf[Token]

    // Don't ramble by repeating things like "...extra identifier, which is an identifier that should not be there"
    def getTokenType(tokenType: TokenType.Value): String =
      if (tokenType != TokenType.Other && tokenType != TokenType.Operator && !posDescription.equalsIgnoreCase(tokenType.toString)) " which is " + tokenType.toString
      else ""

    sb append "Sorry, I was trying to understand " append posDescription

    // Rather than just send a diagnostic message containing just a start position, we really want to create an error spanning Erroneous node.
    // The recipient, such as the IDE, can then nicely underline the token(s) that are in error. So we calculate an endPos and start pos to create
    // an erroneous node at the end of this method, defaulting it to the current position.
    val (startPoint, endPoint) = recognitionException match {
      case unwantedTokenException: UnwantedTokenException =>
        //There was an extra token in the stream that we can see was extra because the next token after it is the one that would have matched correctly.
        //We have discarded it for error recovery but still need to report it.
        val uwt = unwantedTokenException.getUnexpectedToken.asInstanceOf[Token]
        sb append " but I got confused when I found an extra " append getTokenErrorDisplay(uwt)
        sb append getTokenType(classifyToken(token)) append " that should not be there"
        (uwt.getStartIndex, uwt.getStopIndex + 1)
      case missingTokenException: MissingTokenException =>
        // There was a missing token in the stream that we see was missing because the token we actually saw was one
        // that is a member of the follow-set had the token been present.
        sb append " but I got confused because "
        val tokenClass = classifyToken(missingTokenException.expecting)
        if (posDescription.equalsIgnoreCase(tokenClass.toString)) sb append "you seem to have omitted this"
        else if (missingTokenException.expecting == ANTLRToken.EOF) sb append "I was looking for the end of the file here"
        else sb append "you seem to have missed out '" + vhdlTokenNames(missingTokenException.expecting) + "'" append (getTokenType(tokenClass)) append " that should be there"
        // The token is missing, so we want to use the char position directly
        // after the previous token and just make it a single character long.
        // This will be the insert point for the missing token, whatever is
        // actually at that position
        (semiPos, pos + 1)
      case mismatchedTokenException: MismatchedTokenException =>
        // The token we received was not one we were expecting, but we could neither identify a missing token that would have made it
        // something we can deal with, nor that it was just an accidental extra token that we can throw away. Something like
        // A B C D and we got to B but the token we got was neither C, D nor anything following.
        val tokenClass = classifyToken(token)
        sb append " but I got confused when I "
        sb append
          (if (mismatchedTokenException.token.getType == ANTLRToken.EOF) "hit the end of the file."
          else "saw " + getTokenErrorDisplay(token) + (getTokenType(tokenClass)))
        sb append
          (if (tokenClass == TokenType.KeyWord && mismatchedTokenException.expecting == Parser.BASIC_IDENTIFIER) ".\n Perhaps you tried to use a keyword as the name of an object (use \\\\keyword if you need to do this)"
          else if (mismatchedTokenException.expecting != ANTLRToken.EOF) ".\n Perhaps you are missing a '" + vhdlTokenNames(mismatchedTokenException.expecting) + "'"
          else ".\n I was looking for the end of the file here")

        if (mismatchedTokenException.token.getType == ANTLRToken.EOF) (semiPos, pos + 1) // The start and end points come directly from the end of the prior token
        else (mismatchedTokenException.token.asInstanceOf[Token].getStartIndex, mismatchedTokenException.token.asInstanceOf[Token].getStopIndex + 1) // The start and end points come directly from the mismatched token.
      case noViableAltException: NoViableAltException =>
        // The token we saw isn't predicted by any alternative path available at this point in the current rule.
        // something like:  ... (B|C|D|E) but we got Z which does not follow from anywhere.
        val tokenClass = classifyToken(token)
        sb append " but I got confused when I "
        if (noViableAltException.getUnexpectedType == ANTLRToken.EOF) {
          sb append "hit the end of the file."
          // The start and end points come directly from the end of the prior token
          (semiPos, pos + 1)
        } else {
          sb append "saw " + getTokenErrorDisplay(token) append (getTokenType(tokenClass))
          if (tokenClass == TokenType.KeyWord && (stackTop.equals("name") || stackTop.equals("identifier"))) {
            sb append ".\n Perhaps you tried to use a keyword as the name of an object (use \\\\keyword if you need to do this)"
          }
          // The start and end points come directly from the mismatched token.
          (noViableAltException.token.asInstanceOf[Token].getStartIndex, noViableAltException.token.asInstanceOf[Token].getStopIndex + 1)
        }
      case mismatchedSetException: MismatchedSetException =>
        // The parser would have accepted any one of two or more tokens, but the actual token was not in that set and
        // was not a token that we could determine was spurious or from which we could determine that we just had a token missing.
        sb append " but I got confused when I saw " append getTokenErrorDisplay(recognitionException.token) append (getTokenType(classifyToken(recognitionException.token)))
        if (mismatchedSetException.expecting != null) sb append ".\n I was looking for one of: " append mismatchedSetException.expecting
        // The start and end points come directly from the mismatched token.
        (token.getStartIndex, token.getStopIndex + 1)
      case _ =>
        sb append " " append super.getErrorMessage(recognitionException, vhdlTokenNames)
        // The start and end points come directly from the mismatched token.
        (token.getStartIndex, token.getStopIndex + 1)
    }
    compilationUnit.addError(position = new RangePosition(token, startPoint, endPoint), message = sb.toString())
  }

  /**
   * Syncs the parser to a token found in `followSet` and generates an error if any token was consumed.
   *
   * @param rule the name of the parser rule that caused the sync
   * @param followSet the follow set that is used to search for the next good token
   */
  protected def syncAndAddError(rule: String, followSet: Option[BitSet] = None) {
    val startToken = input.LT(1)
    syncToSet(followSet.getOrElse(state.following(state._fsp))) // Compute the follow set that is in context wherever we are in the rule chain/stack
    // If we consume any tokens at this point then we create an error.
    if (startToken ne input.LT(1)) {
      compilationUnit.addError(position = toPosition(startToken), message = "garbled " + rule)
    }
  }

  /**
   * The following code was taken from http://www.antlr.org/wiki/display/ANTLR3/Custom+Syntax+Error+Recovery
   *
   * Use the `followSet` follow set to work out the valid tokens that
   * can follow on from the current point in the parse, then recover by
   * eating tokens that are not a member of the follow set we computed.
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
   *
   * @param followSet the follow set that is used to sync
   */
  private def syncToSet(followSet: BitSet) {
    var mark = -1
    try {
      mark = input.mark()
      // Consume all tokens in the stream until we find a member of the follow
      // set, which means the next production should be guaranteed to be happy.
      while (!followSet.member(input.LA(1))) {
        if (input.LA(1) == ANTLRToken.EOF) {
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

  /**
   * Represents a missing token that was created from the parser
   *
   * @param tokenType the type of the token
   * @param text the text of the token
   */
  private final class MissingCommonToken(tokenType: Int, text: String) extends Token(tokenType, text)

  /**
   * If the parser is able to recover from the fact that a single token
   * is missing from the input stream, then it will call this method
   * to manufacture a token for use by actions in the grammar.
   *
   * In general the tokens we will need to manufacture here will be things
   * like identifiers, missing parentheses and braces and other fairly simple constructs
   * as these can be recognized from the union of follow sets that can be
   * constructed at any one point.
   *
   * @param input The token stream where we are normally drawing tokens from
   * @param recognitionException The exception that was raised by the parser
   * @param expectedTokenType The type of the token that the parser was expecting to see next
   * @param followSet The follow set of tokens that can follow on from here
   * @return A newly manufactured token of the required type
   */
  override protected def getMissingSymbol(input: IntStream, recognitionException: RecognitionException, expectedTokenType: Int, followSet: BitSet): AnyRef = {
    val current =
      if (input.asInstanceOf[TokenStream].LT(1).getType != ANTLRToken.EOF) input.asInstanceOf[TokenStream].LT(1).asInstanceOf[Token]
      else input.asInstanceOf[TokenStream].LT(-1).asInstanceOf[Token]

    val tokenText = expectedTokenType match {
      case ANTLRToken.EOF => "<missing EOF>"
      case IDENTIFIER => "<missing IDENTIFIER>"
      case _ => "<missing " + classifyToken(expectedTokenType).toString + ">"
    }
    val token = new MissingCommonToken(expectedTokenType, tokenText)
    token.setLine(current.getLine)
    token.setCharPositionInLine(current.getStopIndex + 1)
    token.setChannel(ANTLRToken.DEFAULT_CHANNEL)
    token
  }

}