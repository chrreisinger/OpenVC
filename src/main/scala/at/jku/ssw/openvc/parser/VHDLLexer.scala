// $ANTLR 3.2 Sep 23, 2009 12:02:23 C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g 2010-12-25 18:48:57

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

import at.jku.ssw.openvc._
import at.jku.ssw.openvc.ast.Position


import org.antlr.runtime._

import scala.collection.mutable.Stack


object VHDLLexer {
  val PACKAGE = 62
  val FUNCTION = 32
  val BASIC_IDENTIFIER = 148
  val EXPONENT = 158
  val LT = 134
  val NAND = 50
  val INERTIAL = 40
  val SPACE_CHARACTER = 165
  val SEVERITY = 79
  val WHILE = 98
  val ROR = 77
  val GENERIC = 34
  val LETTER = 153
  val MOD = 49
  val CASE = 19
  val NEW = 51
  val NOR = 53
  val POSTPONED = 64
  val NOT = 54
  val LINECOMMENT = 152
  val SUBTYPE = 86
  val EOF = -1
  val BREAK = 142
  val ROL = 76
  val TYPE = 90
  val QUANTITY = 104
  val BASED_LITERAL = 146
  val LBRACKET = 127
  val UNITS = 92
  val RPAREN = 126
  val DOWNTO = 24
  val STRING_LITERAL = 139
  val OTHER_SPECIAL_CHARACTER = 166
  val BASED_INTEGER = 157
  val REAL_LITERAL = 144
  val LOOP = 47
  val BEGIN = 14
  val RETURN = 75
  val TRANSPORT = 89
  val IMPURE = 38
  val BODY = 16
  val APOSTROPHE = 143
  val GEQ = 116
  val GENERATE = 33
  val EQ = 136
  val LINKAGE = 45
  val SPECTRUM = 107
  val REGISTER = 71
  val SELECT = 78
  val ARRAY = 11
  val LOWER_CASE_LETTER = 163
  val TOLERANCE = 140
  val EXIT = 29
  val SHARED = 80
  val RECORD = 70
  val GUARDED = 36
  val SRL = 85
  val SRA = 84
  val LETTER_OR_DIGIT = 154
  val NESTED_ML_COMMENT = 151
  val AMS_ASSIGN = 114
  val NULL = 55
  val XNOR = 100
  val ELSE = 25
  val CHARACTER_LITERAL = 141
  val ON = 57
  val BUS = 18
  val SEMICOLON = 122
  val WAIT = 96
  val VAR_ASSIGN = 119
  val FILE = 30
  val OF = 56
  val MUL = 130
  val ASSERT = 12
  val PROCEDURAL = 112
  val ABS = 4
  val GROUP = 35
  val WS = 150
  val UPPER_CASE_LETTER = 162
  val VARIABLE = 95
  val OUT = 61
  val UNTIL = 93
  val GRAPHIC_CHARACTER = 155
  val EXTENDED_IDENTIFIER = 149
  val INTEGER_LITERAL = 145
  val EXTENDED_DIGIT = 160
  val DBLQUOTE = 121
  val OR = 59
  val ALIAS = 7
  val CONSTANT = 22
  val GT = 135
  val ELSIF = 26
  val USE = 94
  val END = 27
  val OTHERS = 60
  val PROTECTED = 67
  val REPORT = 74
  val DOUBLESTAR = 113
  val LIMIT = 110
  val SLA = 82
  val ATTRIBUTE = 13
  val FOR = 31
  val CONFIGURATION = 21
  val LIBRARY = 44
  val SLL = 83
  val ARCHITECTURE = 10
  val AND = 9
  val BIT_STRING_LITERAL = 147
  val LPAREN = 125
  val IF = 37
  val ENTITY = 28
  val INOUT = 41
  val PURE = 68
  val BOX = 120
  val THEN = 87
  val IN = 39
  val COMMA = 123
  val IS = 42
  val REJECT = 72
  val ALL = 8
  val SIGNAL = 81
  val ACCESS = 5
  val NEXT = 52
  val PLUS = 132
  val DIGIT = 161
  val RBRACKET = 128
  val DOT = 138
  val COMPONENT = 20
  val WITH = 99
  val NATURE = 102
  val INTEGER = 156
  val XOR = 101
  val TO = 88
  val TERMINAL = 103
  val DISCONNECT = 23
  val SPECIAL_CHARACTER = 164
  val AMPERSAND = 124
  val RANGE = 69
  val PORT = 63
  val BUFFER = 17
  val REFERENCE = 111
  val LITERAL = 46
  val REM = 73
  val AFTER = 6
  val MINUS = 133
  val PROCEDURE = 65
  val SUBNATURE = 109
  val OPEN = 58
  val COLON = 129
  val NEQ = 118
  val BASE_SPECIFIER = 159
  val LABEL = 43
  val WHEN = 97
  val ACROSS = 105
  val BLOCK = 15
  val MAP = 48
  val THROUGH = 106
  val ARROW = 117
  val NOISE = 108
  val DIV = 131
  val PROCESS = 66
  val UNAFFECTED = 91
  val BAR = 137
  val LEQ = 115
}

final class VHDLLexer(input: CharStream, state: RecognizerSharedState) extends Lexer(input, state) {

  import VHDLLexer._
  import org.antlr.runtime.BaseRecognizer._

  var ams = false

  type Buffer[A] = scala.collection.immutable.VectorBuilder[A]
  //scala.collection.mutable.ListBuffer[A]

  private val lexerErrorList = new Buffer[CompilerMessage]()

  def lexerErrors: Seq[CompilerMessage] = this.lexerErrorList.result

  override def displayRecognitionError(tokenNames: Array[String], e: RecognitionException) =
    lexerErrorList += new CompilerMessage(position = Position(e.line,e.charPositionInLine), message = super.getErrorMessage(e, tokenNames))


  // delegates
  // delegators

  def this() = this (null, null)

  def this(input: CharStream) = {
    this (input, new RecognizerSharedState())

  }

  val grammarFileName = "C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g"

  // $ANTLR start "ABS"
  @throws(classOf[RecognitionException])
  def mABS(): Unit = {
    try {
      var _type = ABS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:45:5: ( 'abs' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:45:7: 'abs'
      `match`("abs");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ABS"

  // $ANTLR start "ACCESS"
  @throws(classOf[RecognitionException])
  def mACCESS(): Unit = {
    try {
      var _type = ACCESS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:46:8: ( 'access' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:46:10: 'access'
      `match`("access");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ACCESS"

  // $ANTLR start "AFTER"
  @throws(classOf[RecognitionException])
  def mAFTER(): Unit = {
    try {
      var _type = AFTER
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:47:7: ( 'after' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:47:9: 'after'
      `match`("after");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "AFTER"

  // $ANTLR start "ALIAS"
  @throws(classOf[RecognitionException])
  def mALIAS(): Unit = {
    try {
      var _type = ALIAS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:48:7: ( 'alias' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:48:9: 'alias'
      `match`("alias");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ALIAS"

  // $ANTLR start "ALL"
  @throws(classOf[RecognitionException])
  def mALL(): Unit = {
    try {
      var _type = ALL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:49:5: ( 'all' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:49:7: 'all'
      `match`("all");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ALL"

  // $ANTLR start "AND"
  @throws(classOf[RecognitionException])
  def mAND(): Unit = {
    try {
      var _type = AND
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:50:5: ( 'and' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:50:7: 'and'
      `match`("and");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "AND"

  // $ANTLR start "ARCHITECTURE"
  @throws(classOf[RecognitionException])
  def mARCHITECTURE(): Unit = {
    try {
      var _type = ARCHITECTURE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:51:14: ( 'architecture' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:51:16: 'architecture'
      `match`("architecture");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ARCHITECTURE"

  // $ANTLR start "ARRAY"
  @throws(classOf[RecognitionException])
  def mARRAY(): Unit = {
    try {
      var _type = ARRAY
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:52:7: ( 'array' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:52:9: 'array'
      `match`("array");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ARRAY"

  // $ANTLR start "ASSERT"
  @throws(classOf[RecognitionException])
  def mASSERT(): Unit = {
    try {
      var _type = ASSERT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:53:8: ( 'assert' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:53:10: 'assert'
      `match`("assert");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ASSERT"

  // $ANTLR start "ATTRIBUTE"
  @throws(classOf[RecognitionException])
  def mATTRIBUTE(): Unit = {
    try {
      var _type = ATTRIBUTE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:54:11: ( 'attribute' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:54:13: 'attribute'
      `match`("attribute");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ATTRIBUTE"

  // $ANTLR start "BEGIN"
  @throws(classOf[RecognitionException])
  def mBEGIN(): Unit = {
    try {
      var _type = BEGIN
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:55:7: ( 'begin' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:55:9: 'begin'
      `match`("begin");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BEGIN"

  // $ANTLR start "BLOCK"
  @throws(classOf[RecognitionException])
  def mBLOCK(): Unit = {
    try {
      var _type = BLOCK
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:56:7: ( 'block' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:56:9: 'block'
      `match`("block");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BLOCK"

  // $ANTLR start "BODY"
  @throws(classOf[RecognitionException])
  def mBODY(): Unit = {
    try {
      var _type = BODY
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:57:6: ( 'body' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:57:8: 'body'
      `match`("body");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BODY"

  // $ANTLR start "BUFFER"
  @throws(classOf[RecognitionException])
  def mBUFFER(): Unit = {
    try {
      var _type = BUFFER
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:58:8: ( 'buffer' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:58:10: 'buffer'
      `match`("buffer");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BUFFER"

  // $ANTLR start "BUS"
  @throws(classOf[RecognitionException])
  def mBUS(): Unit = {
    try {
      var _type = BUS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:59:5: ( 'bus' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:59:7: 'bus'
      `match`("bus");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BUS"

  // $ANTLR start "CASE"
  @throws(classOf[RecognitionException])
  def mCASE(): Unit = {
    try {
      var _type = CASE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:60:6: ( 'case' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:60:8: 'case'
      `match`("case");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "CASE"

  // $ANTLR start "COMPONENT"
  @throws(classOf[RecognitionException])
  def mCOMPONENT(): Unit = {
    try {
      var _type = COMPONENT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:61:11: ( 'component' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:61:13: 'component'
      `match`("component");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "COMPONENT"

  // $ANTLR start "CONFIGURATION"
  @throws(classOf[RecognitionException])
  def mCONFIGURATION(): Unit = {
    try {
      var _type = CONFIGURATION
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:62:15: ( 'configuration' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:62:17: 'configuration'
      `match`("configuration");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "CONFIGURATION"

  // $ANTLR start "CONSTANT"
  @throws(classOf[RecognitionException])
  def mCONSTANT(): Unit = {
    try {
      var _type = CONSTANT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:63:10: ( 'constant' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:63:12: 'constant'
      `match`("constant");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "CONSTANT"

  // $ANTLR start "DISCONNECT"
  @throws(classOf[RecognitionException])
  def mDISCONNECT(): Unit = {
    try {
      var _type = DISCONNECT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:64:12: ( 'disconnect' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:64:14: 'disconnect'
      `match`("disconnect");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "DISCONNECT"

  // $ANTLR start "DOWNTO"
  @throws(classOf[RecognitionException])
  def mDOWNTO(): Unit = {
    try {
      var _type = DOWNTO
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:65:8: ( 'downto' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:65:10: 'downto'
      `match`("downto");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "DOWNTO"

  // $ANTLR start "ELSE"
  @throws(classOf[RecognitionException])
  def mELSE(): Unit = {
    try {
      var _type = ELSE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:66:6: ( 'else' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:66:8: 'else'
      `match`("else");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ELSE"

  // $ANTLR start "ELSIF"
  @throws(classOf[RecognitionException])
  def mELSIF(): Unit = {
    try {
      var _type = ELSIF
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:67:7: ( 'elsif' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:67:9: 'elsif'
      `match`("elsif");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ELSIF"

  // $ANTLR start "END"
  @throws(classOf[RecognitionException])
  def mEND(): Unit = {
    try {
      var _type = END
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:68:5: ( 'end' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:68:7: 'end'
      `match`("end");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "END"

  // $ANTLR start "ENTITY"
  @throws(classOf[RecognitionException])
  def mENTITY(): Unit = {
    try {
      var _type = ENTITY
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:69:8: ( 'entity' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:69:10: 'entity'
      `match`("entity");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ENTITY"

  // $ANTLR start "EXIT"
  @throws(classOf[RecognitionException])
  def mEXIT(): Unit = {
    try {
      var _type = EXIT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:70:6: ( 'exit' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:70:8: 'exit'
      `match`("exit");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "EXIT"

  // $ANTLR start "FILE"
  @throws(classOf[RecognitionException])
  def mFILE(): Unit = {
    try {
      var _type = FILE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:71:6: ( 'file' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:71:8: 'file'
      `match`("file");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "FILE"

  // $ANTLR start "FOR"
  @throws(classOf[RecognitionException])
  def mFOR(): Unit = {
    try {
      var _type = FOR
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:72:5: ( 'for' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:72:7: 'for'
      `match`("for");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "FOR"

  // $ANTLR start "FUNCTION"
  @throws(classOf[RecognitionException])
  def mFUNCTION(): Unit = {
    try {
      var _type = FUNCTION
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:73:10: ( 'function' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:73:12: 'function'
      `match`("function");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "FUNCTION"

  // $ANTLR start "GENERATE"
  @throws(classOf[RecognitionException])
  def mGENERATE(): Unit = {
    try {
      var _type = GENERATE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:74:10: ( 'generate' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:74:12: 'generate'
      `match`("generate");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "GENERATE"

  // $ANTLR start "GENERIC"
  @throws(classOf[RecognitionException])
  def mGENERIC(): Unit = {
    try {
      var _type = GENERIC
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:75:9: ( 'generic' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:75:11: 'generic'
      `match`("generic");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "GENERIC"

  // $ANTLR start "GROUP"
  @throws(classOf[RecognitionException])
  def mGROUP(): Unit = {
    try {
      var _type = GROUP
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:76:7: ( 'group' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:76:9: 'group'
      `match`("group");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "GROUP"

  // $ANTLR start "GUARDED"
  @throws(classOf[RecognitionException])
  def mGUARDED(): Unit = {
    try {
      var _type = GUARDED
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:77:9: ( 'guarded' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:77:11: 'guarded'
      `match`("guarded");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "GUARDED"

  // $ANTLR start "IF"
  @throws(classOf[RecognitionException])
  def mIF(): Unit = {
    try {
      var _type = IF
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:78:4: ( 'if' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:78:6: 'if'
      `match`("if");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "IF"

  // $ANTLR start "IMPURE"
  @throws(classOf[RecognitionException])
  def mIMPURE(): Unit = {
    try {
      var _type = IMPURE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:79:8: ( 'impure' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:79:10: 'impure'
      `match`("impure");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "IMPURE"

  // $ANTLR start "IN"
  @throws(classOf[RecognitionException])
  def mIN(): Unit = {
    try {
      var _type = IN
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:80:4: ( 'in' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:80:6: 'in'
      `match`("in");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "IN"

  // $ANTLR start "INERTIAL"
  @throws(classOf[RecognitionException])
  def mINERTIAL(): Unit = {
    try {
      var _type = INERTIAL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:81:10: ( 'inertial' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:81:12: 'inertial'
      `match`("inertial");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "INERTIAL"

  // $ANTLR start "INOUT"
  @throws(classOf[RecognitionException])
  def mINOUT(): Unit = {
    try {
      var _type = INOUT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:82:7: ( 'inout' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:82:9: 'inout'
      `match`("inout");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "INOUT"

  // $ANTLR start "IS"
  @throws(classOf[RecognitionException])
  def mIS(): Unit = {
    try {
      var _type = IS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:83:4: ( 'is' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:83:6: 'is'
      `match`("is");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "IS"

  // $ANTLR start "LABEL"
  @throws(classOf[RecognitionException])
  def mLABEL(): Unit = {
    try {
      var _type = LABEL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:84:7: ( 'label' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:84:9: 'label'
      `match`("label");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "LABEL"

  // $ANTLR start "LIBRARY"
  @throws(classOf[RecognitionException])
  def mLIBRARY(): Unit = {
    try {
      var _type = LIBRARY
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:85:9: ( 'library' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:85:11: 'library'
      `match`("library");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "LIBRARY"

  // $ANTLR start "LINKAGE"
  @throws(classOf[RecognitionException])
  def mLINKAGE(): Unit = {
    try {
      var _type = LINKAGE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:86:9: ( 'linkage' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:86:11: 'linkage'
      `match`("linkage");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "LINKAGE"

  // $ANTLR start "LITERAL"
  @throws(classOf[RecognitionException])
  def mLITERAL(): Unit = {
    try {
      var _type = LITERAL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:87:9: ( 'literal' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:87:11: 'literal'
      `match`("literal");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "LITERAL"

  // $ANTLR start "LOOP"
  @throws(classOf[RecognitionException])
  def mLOOP(): Unit = {
    try {
      var _type = LOOP
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:88:6: ( 'loop' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:88:8: 'loop'
      `match`("loop");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "LOOP"

  // $ANTLR start "MAP"
  @throws(classOf[RecognitionException])
  def mMAP(): Unit = {
    try {
      var _type = MAP
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:89:5: ( 'map' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:89:7: 'map'
      `match`("map");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "MAP"

  // $ANTLR start "MOD"
  @throws(classOf[RecognitionException])
  def mMOD(): Unit = {
    try {
      var _type = MOD
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:90:5: ( 'mod' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:90:7: 'mod'
      `match`("mod");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "MOD"

  // $ANTLR start "NAND"
  @throws(classOf[RecognitionException])
  def mNAND(): Unit = {
    try {
      var _type = NAND
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:91:6: ( 'nand' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:91:8: 'nand'
      `match`("nand");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "NAND"

  // $ANTLR start "NEW"
  @throws(classOf[RecognitionException])
  def mNEW(): Unit = {
    try {
      var _type = NEW
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:92:5: ( 'new' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:92:7: 'new'
      `match`("new");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "NEW"

  // $ANTLR start "NEXT"
  @throws(classOf[RecognitionException])
  def mNEXT(): Unit = {
    try {
      var _type = NEXT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:93:6: ( 'next' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:93:8: 'next'
      `match`("next");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "NEXT"

  // $ANTLR start "NOR"
  @throws(classOf[RecognitionException])
  def mNOR(): Unit = {
    try {
      var _type = NOR
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:94:5: ( 'nor' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:94:7: 'nor'
      `match`("nor");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "NOR"

  // $ANTLR start "NOT"
  @throws(classOf[RecognitionException])
  def mNOT(): Unit = {
    try {
      var _type = NOT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:95:5: ( 'not' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:95:7: 'not'
      `match`("not");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "NOT"

  // $ANTLR start "NULL"
  @throws(classOf[RecognitionException])
  def mNULL(): Unit = {
    try {
      var _type = NULL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:96:6: ( 'null' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:96:8: 'null'
      `match`("null");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "NULL"

  // $ANTLR start "OF"
  @throws(classOf[RecognitionException])
  def mOF(): Unit = {
    try {
      var _type = OF
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:97:4: ( 'of' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:97:6: 'of'
      `match`("of");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "OF"

  // $ANTLR start "ON"
  @throws(classOf[RecognitionException])
  def mON(): Unit = {
    try {
      var _type = ON
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:98:4: ( 'on' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:98:6: 'on'
      `match`("on");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ON"

  // $ANTLR start "OPEN"
  @throws(classOf[RecognitionException])
  def mOPEN(): Unit = {
    try {
      var _type = OPEN
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:99:6: ( 'open' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:99:8: 'open'
      `match`("open");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "OPEN"

  // $ANTLR start "OR"
  @throws(classOf[RecognitionException])
  def mOR(): Unit = {
    try {
      var _type = OR
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:100:4: ( 'or' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:100:6: 'or'
      `match`("or");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "OR"

  // $ANTLR start "OTHERS"
  @throws(classOf[RecognitionException])
  def mOTHERS(): Unit = {
    try {
      var _type = OTHERS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:101:8: ( 'others' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:101:10: 'others'
      `match`("others");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "OTHERS"

  // $ANTLR start "OUT"
  @throws(classOf[RecognitionException])
  def mOUT(): Unit = {
    try {
      var _type = OUT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:102:5: ( 'out' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:102:7: 'out'
      `match`("out");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "OUT"

  // $ANTLR start "PACKAGE"
  @throws(classOf[RecognitionException])
  def mPACKAGE(): Unit = {
    try {
      var _type = PACKAGE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:103:9: ( 'package' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:103:11: 'package'
      `match`("package");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "PACKAGE"

  // $ANTLR start "PORT"
  @throws(classOf[RecognitionException])
  def mPORT(): Unit = {
    try {
      var _type = PORT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:104:6: ( 'port' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:104:8: 'port'
      `match`("port");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "PORT"

  // $ANTLR start "POSTPONED"
  @throws(classOf[RecognitionException])
  def mPOSTPONED(): Unit = {
    try {
      var _type = POSTPONED
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:105:11: ( 'postponed' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:105:13: 'postponed'
      `match`("postponed");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "POSTPONED"

  // $ANTLR start "PROCEDURE"
  @throws(classOf[RecognitionException])
  def mPROCEDURE(): Unit = {
    try {
      var _type = PROCEDURE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:106:11: ( 'procedure' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:106:13: 'procedure'
      `match`("procedure");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "PROCEDURE"

  // $ANTLR start "PROCESS"
  @throws(classOf[RecognitionException])
  def mPROCESS(): Unit = {
    try {
      var _type = PROCESS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:107:9: ( 'process' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:107:11: 'process'
      `match`("process");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "PROCESS"

  // $ANTLR start "PROTECTED"
  @throws(classOf[RecognitionException])
  def mPROTECTED(): Unit = {
    try {
      var _type = PROTECTED
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:108:11: ( 'protected' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:108:13: 'protected'
      `match`("protected");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "PROTECTED"

  // $ANTLR start "PURE"
  @throws(classOf[RecognitionException])
  def mPURE(): Unit = {
    try {
      var _type = PURE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:109:6: ( 'pure' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:109:8: 'pure'
      `match`("pure");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "PURE"

  // $ANTLR start "RANGE"
  @throws(classOf[RecognitionException])
  def mRANGE(): Unit = {
    try {
      var _type = RANGE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:110:7: ( 'range' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:110:9: 'range'
      `match`("range");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "RANGE"

  // $ANTLR start "RECORD"
  @throws(classOf[RecognitionException])
  def mRECORD(): Unit = {
    try {
      var _type = RECORD
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:111:8: ( 'record' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:111:10: 'record'
      `match`("record");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "RECORD"

  // $ANTLR start "REGISTER"
  @throws(classOf[RecognitionException])
  def mREGISTER(): Unit = {
    try {
      var _type = REGISTER
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:112:10: ( 'register' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:112:12: 'register'
      `match`("register");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "REGISTER"

  // $ANTLR start "REJECT"
  @throws(classOf[RecognitionException])
  def mREJECT(): Unit = {
    try {
      var _type = REJECT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:113:8: ( 'reject' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:113:10: 'reject'
      `match`("reject");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "REJECT"

  // $ANTLR start "REM"
  @throws(classOf[RecognitionException])
  def mREM(): Unit = {
    try {
      var _type = REM
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:114:5: ( 'rem' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:114:7: 'rem'
      `match`("rem");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "REM"

  // $ANTLR start "REPORT"
  @throws(classOf[RecognitionException])
  def mREPORT(): Unit = {
    try {
      var _type = REPORT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:115:8: ( 'report' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:115:10: 'report'
      `match`("report");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "REPORT"

  // $ANTLR start "RETURN"
  @throws(classOf[RecognitionException])
  def mRETURN(): Unit = {
    try {
      var _type = RETURN
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:116:8: ( 'return' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:116:10: 'return'
      `match`("return");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "RETURN"

  // $ANTLR start "ROL"
  @throws(classOf[RecognitionException])
  def mROL(): Unit = {
    try {
      var _type = ROL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:117:5: ( 'rol' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:117:7: 'rol'
      `match`("rol");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ROL"

  // $ANTLR start "ROR"
  @throws(classOf[RecognitionException])
  def mROR(): Unit = {
    try {
      var _type = ROR
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:118:5: ( 'ror' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:118:7: 'ror'
      `match`("ror");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ROR"

  // $ANTLR start "SELECT"
  @throws(classOf[RecognitionException])
  def mSELECT(): Unit = {
    try {
      var _type = SELECT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:119:8: ( 'select' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:119:10: 'select'
      `match`("select");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SELECT"

  // $ANTLR start "SEVERITY"
  @throws(classOf[RecognitionException])
  def mSEVERITY(): Unit = {
    try {
      var _type = SEVERITY
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:120:10: ( 'severity' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:120:12: 'severity'
      `match`("severity");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SEVERITY"

  // $ANTLR start "SHARED"
  @throws(classOf[RecognitionException])
  def mSHARED(): Unit = {
    try {
      var _type = SHARED
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:121:8: ( 'shared' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:121:10: 'shared'
      `match`("shared");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SHARED"

  // $ANTLR start "SIGNAL"
  @throws(classOf[RecognitionException])
  def mSIGNAL(): Unit = {
    try {
      var _type = SIGNAL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:122:8: ( 'signal' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:122:10: 'signal'
      `match`("signal");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SIGNAL"

  // $ANTLR start "SLA"
  @throws(classOf[RecognitionException])
  def mSLA(): Unit = {
    try {
      var _type = SLA
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:123:5: ( 'sla' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:123:7: 'sla'
      `match`("sla");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SLA"

  // $ANTLR start "SLL"
  @throws(classOf[RecognitionException])
  def mSLL(): Unit = {
    try {
      var _type = SLL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:124:5: ( 'sll' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:124:7: 'sll'
      `match`("sll");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SLL"

  // $ANTLR start "SRA"
  @throws(classOf[RecognitionException])
  def mSRA(): Unit = {
    try {
      var _type = SRA
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:125:5: ( 'sra' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:125:7: 'sra'
      `match`("sra");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SRA"

  // $ANTLR start "SRL"
  @throws(classOf[RecognitionException])
  def mSRL(): Unit = {
    try {
      var _type = SRL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:126:5: ( 'srl' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:126:7: 'srl'
      `match`("srl");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SRL"

  // $ANTLR start "SUBTYPE"
  @throws(classOf[RecognitionException])
  def mSUBTYPE(): Unit = {
    try {
      var _type = SUBTYPE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:127:9: ( 'subtype' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:127:11: 'subtype'
      `match`("subtype");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SUBTYPE"

  // $ANTLR start "THEN"
  @throws(classOf[RecognitionException])
  def mTHEN(): Unit = {
    try {
      var _type = THEN
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:128:6: ( 'then' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:128:8: 'then'
      `match`("then");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "THEN"

  // $ANTLR start "TO"
  @throws(classOf[RecognitionException])
  def mTO(): Unit = {
    try {
      var _type = TO
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:129:4: ( 'to' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:129:6: 'to'
      `match`("to");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "TO"

  // $ANTLR start "TRANSPORT"
  @throws(classOf[RecognitionException])
  def mTRANSPORT(): Unit = {
    try {
      var _type = TRANSPORT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:130:11: ( 'transport' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:130:13: 'transport'
      `match`("transport");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "TRANSPORT"

  // $ANTLR start "TYPE"
  @throws(classOf[RecognitionException])
  def mTYPE(): Unit = {
    try {
      var _type = TYPE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:131:6: ( 'type' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:131:8: 'type'
      `match`("type");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "TYPE"

  // $ANTLR start "UNAFFECTED"
  @throws(classOf[RecognitionException])
  def mUNAFFECTED(): Unit = {
    try {
      var _type = UNAFFECTED
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:132:12: ( 'unaffected' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:132:14: 'unaffected'
      `match`("unaffected");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "UNAFFECTED"

  // $ANTLR start "UNITS"
  @throws(classOf[RecognitionException])
  def mUNITS(): Unit = {
    try {
      var _type = UNITS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:133:7: ( 'units' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:133:9: 'units'
      `match`("units");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "UNITS"

  // $ANTLR start "UNTIL"
  @throws(classOf[RecognitionException])
  def mUNTIL(): Unit = {
    try {
      var _type = UNTIL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:134:7: ( 'until' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:134:9: 'until'
      `match`("until");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "UNTIL"

  // $ANTLR start "USE"
  @throws(classOf[RecognitionException])
  def mUSE(): Unit = {
    try {
      var _type = USE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:135:5: ( 'use' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:135:7: 'use'
      `match`("use");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "USE"

  // $ANTLR start "VARIABLE"
  @throws(classOf[RecognitionException])
  def mVARIABLE(): Unit = {
    try {
      var _type = VARIABLE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:136:10: ( 'variable' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:136:12: 'variable'
      `match`("variable");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "VARIABLE"

  // $ANTLR start "WAIT"
  @throws(classOf[RecognitionException])
  def mWAIT(): Unit = {
    try {
      var _type = WAIT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:137:6: ( 'wait' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:137:8: 'wait'
      `match`("wait");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "WAIT"

  // $ANTLR start "WHEN"
  @throws(classOf[RecognitionException])
  def mWHEN(): Unit = {
    try {
      var _type = WHEN
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:138:6: ( 'when' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:138:8: 'when'
      `match`("when");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "WHEN"

  // $ANTLR start "WHILE"
  @throws(classOf[RecognitionException])
  def mWHILE(): Unit = {
    try {
      var _type = WHILE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:139:7: ( 'while' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:139:9: 'while'
      `match`("while");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "WHILE"

  // $ANTLR start "WITH"
  @throws(classOf[RecognitionException])
  def mWITH(): Unit = {
    try {
      var _type = WITH
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:140:6: ( 'with' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:140:8: 'with'
      `match`("with");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "WITH"

  // $ANTLR start "XNOR"
  @throws(classOf[RecognitionException])
  def mXNOR(): Unit = {
    try {
      var _type = XNOR
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:141:6: ( 'xnor' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:141:8: 'xnor'
      `match`("xnor");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "XNOR"

  // $ANTLR start "XOR"
  @throws(classOf[RecognitionException])
  def mXOR(): Unit = {
    try {
      var _type = XOR
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:142:5: ( 'xor' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:142:7: 'xor'
      `match`("xor");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "XOR"

  // $ANTLR start "NATURE"
  @throws(classOf[RecognitionException])
  def mNATURE(): Unit = {
    try {
      var _type = NATURE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:143:8: ( 'nature' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:143:10: 'nature'
      `match`("nature");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "NATURE"

  // $ANTLR start "TERMINAL"
  @throws(classOf[RecognitionException])
  def mTERMINAL(): Unit = {
    try {
      var _type = TERMINAL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:144:10: ( 'terminal' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:144:12: 'terminal'
      `match`("terminal");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "TERMINAL"

  // $ANTLR start "QUANTITY"
  @throws(classOf[RecognitionException])
  def mQUANTITY(): Unit = {
    try {
      var _type = QUANTITY
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:145:10: ( 'quantity' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:145:12: 'quantity'
      `match`("quantity");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "QUANTITY"

  // $ANTLR start "ACROSS"
  @throws(classOf[RecognitionException])
  def mACROSS(): Unit = {
    try {
      var _type = ACROSS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:146:8: ( 'across' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:146:10: 'across'
      `match`("across");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ACROSS"

  // $ANTLR start "THROUGH"
  @throws(classOf[RecognitionException])
  def mTHROUGH(): Unit = {
    try {
      var _type = THROUGH
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:147:9: ( 'through' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:147:11: 'through'
      `match`("through");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "THROUGH"

  // $ANTLR start "SPECTRUM"
  @throws(classOf[RecognitionException])
  def mSPECTRUM(): Unit = {
    try {
      var _type = SPECTRUM
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:148:10: ( 'spectrum' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:148:12: 'spectrum'
      `match`("spectrum");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SPECTRUM"

  // $ANTLR start "NOISE"
  @throws(classOf[RecognitionException])
  def mNOISE(): Unit = {
    try {
      var _type = NOISE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:149:7: ( 'noise' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:149:9: 'noise'
      `match`("noise");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "NOISE"

  // $ANTLR start "SUBNATURE"
  @throws(classOf[RecognitionException])
  def mSUBNATURE(): Unit = {
    try {
      var _type = SUBNATURE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:150:11: ( 'subnature' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:150:13: 'subnature'
      `match`("subnature");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SUBNATURE"

  // $ANTLR start "LIMIT"
  @throws(classOf[RecognitionException])
  def mLIMIT(): Unit = {
    try {
      var _type = LIMIT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:151:7: ( 'limit' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:151:9: 'limit'
      `match`("limit");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "LIMIT"

  // $ANTLR start "REFERENCE"
  @throws(classOf[RecognitionException])
  def mREFERENCE(): Unit = {
    try {
      var _type = REFERENCE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:152:11: ( 'reference' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:152:13: 'reference'
      `match`("reference");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "REFERENCE"

  // $ANTLR start "PROCEDURAL"
  @throws(classOf[RecognitionException])
  def mPROCEDURAL(): Unit = {
    try {
      var _type = PROCEDURAL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:153:12: ( 'procedural' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:153:14: 'procedural'
      `match`("procedural");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "PROCEDURAL"

  // $ANTLR start "DOUBLESTAR"
  @throws(classOf[RecognitionException])
  def mDOUBLESTAR(): Unit = {
    try {
      var _type = DOUBLESTAR
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:154:12: ( '**' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:154:14: '**'
      `match`("**");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "DOUBLESTAR"

  // $ANTLR start "AMS_ASSIGN"
  @throws(classOf[RecognitionException])
  def mAMS_ASSIGN(): Unit = {
    try {
      var _type = AMS_ASSIGN
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:155:12: ( '==' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:155:14: '=='
      `match`("==");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "AMS_ASSIGN"

  // $ANTLR start "LEQ"
  @throws(classOf[RecognitionException])
  def mLEQ(): Unit = {
    try {
      var _type = LEQ
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:156:5: ( '<=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:156:7: '<='
      `match`("<=");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "LEQ"

  // $ANTLR start "GEQ"
  @throws(classOf[RecognitionException])
  def mGEQ(): Unit = {
    try {
      var _type = GEQ
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:157:5: ( '>=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:157:7: '>='
      `match`(">=");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "GEQ"

  // $ANTLR start "ARROW"
  @throws(classOf[RecognitionException])
  def mARROW(): Unit = {
    try {
      var _type = ARROW
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:158:7: ( '=>' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:158:9: '=>'
      `match`("=>");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "ARROW"

  // $ANTLR start "NEQ"
  @throws(classOf[RecognitionException])
  def mNEQ(): Unit = {
    try {
      var _type = NEQ
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:159:5: ( '/=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:159:7: '/='
      `match`("/=");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "NEQ"

  // $ANTLR start "VAR_ASSIGN"
  @throws(classOf[RecognitionException])
  def mVAR_ASSIGN(): Unit = {
    try {
      var _type = VAR_ASSIGN
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:160:12: ( ':=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:160:14: ':='
      `match`(":=");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "VAR_ASSIGN"

  // $ANTLR start "BOX"
  @throws(classOf[RecognitionException])
  def mBOX(): Unit = {
    try {
      var _type = BOX
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:161:5: ( '<>' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:161:7: '<>'
      `match`("<>");
      if (state.failed) return




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BOX"

  // $ANTLR start "DBLQUOTE"
  @throws(classOf[RecognitionException])
  def mDBLQUOTE(): Unit = {
    try {
      var _type = DBLQUOTE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:162:10: ( '\\\"' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:162:12: '\\\"'
      `match`('\"');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "DBLQUOTE"

  // $ANTLR start "SEMICOLON"
  @throws(classOf[RecognitionException])
  def mSEMICOLON(): Unit = {
    try {
      var _type = SEMICOLON
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:163:11: ( ';' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:163:13: ';'
      `match`(';');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "SEMICOLON"

  // $ANTLR start "COMMA"
  @throws(classOf[RecognitionException])
  def mCOMMA(): Unit = {
    try {
      var _type = COMMA
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:164:7: ( ',' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:164:9: ','
      `match`(',');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "COMMA"

  // $ANTLR start "AMPERSAND"
  @throws(classOf[RecognitionException])
  def mAMPERSAND(): Unit = {
    try {
      var _type = AMPERSAND
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:165:11: ( '&' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:165:13: '&'
      `match`('&');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "AMPERSAND"

  // $ANTLR start "LPAREN"
  @throws(classOf[RecognitionException])
  def mLPAREN(): Unit = {
    try {
      var _type = LPAREN
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:166:8: ( '(' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:166:10: '('
      `match`('(');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "LPAREN"

  // $ANTLR start "RPAREN"
  @throws(classOf[RecognitionException])
  def mRPAREN(): Unit = {
    try {
      var _type = RPAREN
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:167:8: ( ')' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:167:10: ')'
      `match`(')');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "RPAREN"

  // $ANTLR start "LBRACKET"
  @throws(classOf[RecognitionException])
  def mLBRACKET(): Unit = {
    try {
      var _type = LBRACKET
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:168:10: ( '[' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:168:12: '['
      `match`('[');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "LBRACKET"

  // $ANTLR start "RBRACKET"
  @throws(classOf[RecognitionException])
  def mRBRACKET(): Unit = {
    try {
      var _type = RBRACKET
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:169:10: ( ']' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:169:12: ']'
      `match`(']');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "RBRACKET"

  // $ANTLR start "COLON"
  @throws(classOf[RecognitionException])
  def mCOLON(): Unit = {
    try {
      var _type = COLON
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:170:7: ( ':' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:170:9: ':'
      `match`(':');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "COLON"

  // $ANTLR start "MUL"
  @throws(classOf[RecognitionException])
  def mMUL(): Unit = {
    try {
      var _type = MUL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:171:5: ( '*' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:171:7: '*'
      `match`('*');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "MUL"

  // $ANTLR start "DIV"
  @throws(classOf[RecognitionException])
  def mDIV(): Unit = {
    try {
      var _type = DIV
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:172:5: ( '/' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:172:7: '/'
      `match`('/');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "DIV"

  // $ANTLR start "PLUS"
  @throws(classOf[RecognitionException])
  def mPLUS(): Unit = {
    try {
      var _type = PLUS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:173:6: ( '+' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:173:8: '+'
      `match`('+');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "PLUS"

  // $ANTLR start "MINUS"
  @throws(classOf[RecognitionException])
  def mMINUS(): Unit = {
    try {
      var _type = MINUS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:174:7: ( '-' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:174:9: '-'
      `match`('-');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "MINUS"

  // $ANTLR start "LT"
  @throws(classOf[RecognitionException])
  def mLT(): Unit = {
    try {
      var _type = LT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:175:4: ( '<' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:175:6: '<'
      `match`('<');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "LT"

  // $ANTLR start "GT"
  @throws(classOf[RecognitionException])
  def mGT(): Unit = {
    try {
      var _type = GT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:176:4: ( '>' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:176:6: '>'
      `match`('>');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "GT"

  // $ANTLR start "EQ"
  @throws(classOf[RecognitionException])
  def mEQ(): Unit = {
    try {
      var _type = EQ
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:177:4: ( '=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:177:6: '='
      `match`('=');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "EQ"

  // $ANTLR start "BAR"
  @throws(classOf[RecognitionException])
  def mBAR(): Unit = {
    try {
      var _type = BAR
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:178:5: ( '|' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:178:7: '|'
      `match`('|');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BAR"

  // $ANTLR start "DOT"
  @throws(classOf[RecognitionException])
  def mDOT(): Unit = {
    try {
      var _type = DOT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:179:5: ( '.' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:179:7: '.'
      `match`('.');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "DOT"

  // $ANTLR start "BREAK"
  @throws(classOf[RecognitionException])
  def mBREAK(): Unit = {
    try {
      var _type = BREAK
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1939:7: ( 'break' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1939:9: 'break'
      `match`("break");
      if (state.failed) return

      if (state.backtracking == 0) {
        if (!ams) _type = BASIC_IDENTIFIER
      }



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BREAK"

  // $ANTLR start "TOLERANCE"
  @throws(classOf[RecognitionException])
  def mTOLERANCE(): Unit = {
    try {
      var _type = TOLERANCE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1941:11: ( 'tolerance' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1941:13: 'tolerance'
      `match`("tolerance");
      if (state.failed) return

      if (state.backtracking == 0) {
        if (!ams) _type = BASIC_IDENTIFIER
      }



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "TOLERANCE"

  // $ANTLR start "WS"
  @throws(classOf[RecognitionException])
  def mWS(): Unit = {
    try {
      var _type = WS
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1944:4: ( ( '\\t' | ' ' | '\\r' | '\\n' )+ )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1944:6: ( '\\t' | ' ' | '\\r' | '\\n' )+
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1944:6: ( '\\t' | ' ' | '\\r' | '\\n' )+
      var cnt1 = 0
      var loop1 = true
      do {
        var alt1 = 2
        val LA1_0 = input.LA(1)

        if (((LA1_0 >= '\t' && LA1_0 <= '\n') || LA1_0 == '\r' || LA1_0 == ' ')) {
          alt1 = 1
        }


        alt1 match {
          case 1 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
            if ((input.LA(1) >= '\t' && input.LA(1) <= '\n') || input.LA(1) == '\r' || input.LA(1) == ' ') {
              input.consume()
              state.failed = false
            }
            else {
              if (state.backtracking > 0) {
                state.failed = true;
                return
              }
              val mse = new MismatchedSetException(null, input)
              recover(mse)
              throw mse
            }


          case _ =>
            if (cnt1 >= 1) loop1 = false
            else {
              if (state.backtracking > 0) {
                state.failed = true;
                return
              }
              val eee = new EarlyExitException(1, input)
              throw eee
            }
        }
        cnt1 += 1
      } while (loop1);

      if (state.backtracking == 0) {
        skip()
      }



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "WS"

  // $ANTLR start "NESTED_ML_COMMENT"
  @throws(classOf[RecognitionException])
  def mNESTED_ML_COMMENT(): Unit = {
    try {
      var _type = NESTED_ML_COMMENT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1947:19: ( '/*' ( options {greedy=false; } : NESTED_ML_COMMENT | . )* '*/' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1948:2: '/*' ( options {greedy=false; } : NESTED_ML_COMMENT | . )* '*/'
      `match`("/*");
      if (state.failed) return

      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1948:7: ( options {greedy=false; } : NESTED_ML_COMMENT | . )*
      var loop2 = true
      do {
        var alt2 = 3
        val LA2_0 = input.LA(1)

        if ((LA2_0 == '*')) {
          val LA2_1 = input.LA(2)

          if ((LA2_1 == '/')) {
            alt2 = 3
          }
          else if (((LA2_1 >= '\u0000' && LA2_1 <= '.') || (LA2_1 >= '0' && LA2_1 <= '\uFFFF'))) {
            alt2 = 2
          }


        }
        else if ((LA2_0 == '/')) {
          val LA2_2 = input.LA(2)

          if ((LA2_2 == '*')) {
            alt2 = 1
          }
          else if (((LA2_2 >= '\u0000' && LA2_2 <= ')') || (LA2_2 >= '+' && LA2_2 <= '\uFFFF'))) {
            alt2 = 2
          }


        }
        else if (((LA2_0 >= '\u0000' && LA2_0 <= ')') || (LA2_0 >= '+' && LA2_0 <= '.') || (LA2_0 >= '0' && LA2_0 <= '\uFFFF'))) {
          alt2 = 2
        }


        alt2 match {
          case 1 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1948:34: NESTED_ML_COMMENT
            mNESTED_ML_COMMENT(); if (state.failed) return

          case 2 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1948:54: .
            matchAny(); if (state.failed) return


          case _ => loop2 = false
        }
      } while (loop2);

      `match`("*/");
      if (state.failed) return

      if (state.backtracking == 0) {
        skip()
      }



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "NESTED_ML_COMMENT"

  // $ANTLR start "LINECOMMENT"
  @throws(classOf[RecognitionException])
  def mLINECOMMENT(): Unit = {
    try {
      var _type = LINECOMMENT
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1951:13: ( '--' (~ ( '\\r' | '\\n' | '\\u000C' ) )* )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1951:15: '--' (~ ( '\\r' | '\\n' | '\\u000C' ) )*
      `match`("--");
      if (state.failed) return

      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1951:20: (~ ( '\\r' | '\\n' | '\\u000C' ) )*
      var loop3 = true
      do {
        var alt3 = 2
        val LA3_0 = input.LA(1)

        if (((LA3_0 >= '\u0000' && LA3_0 <= '\t') || LA3_0 == '\u000B' || (LA3_0 >= '\u000E' && LA3_0 <= '\uFFFF'))) {
          alt3 = 1
        }


        alt3 match {
          case 1 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1951:20: ~ ( '\\r' | '\\n' | '\\u000C' )
            if ((input.LA(1) >= '\u0000' && input.LA(1) <= '\t') || input.LA(1) == '\u000B' || (input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF')) {
              input.consume()
              state.failed = false
            }
            else {
              if (state.backtracking > 0) {
                state.failed = true;
                return
              }
              val mse = new MismatchedSetException(null, input)
              recover(mse)
              throw mse
            }


          case _ => loop3 = false
        }
      } while (loop3);

      if (state.backtracking == 0) {
        skip()
      }



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "LINECOMMENT"

  // $ANTLR start "BASIC_IDENTIFIER"
  @throws(classOf[RecognitionException])
  def mBASIC_IDENTIFIER(): Unit = {
    try {
      var _type = BASIC_IDENTIFIER
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1953:18: ( LETTER ( LETTER_OR_DIGIT | '_' )* )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1953:20: LETTER ( LETTER_OR_DIGIT | '_' )*
      mLETTER();
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1953:27: ( LETTER_OR_DIGIT | '_' )*
      var loop4 = true
      do {
        var alt4 = 2
        val LA4_0 = input.LA(1)

        if (((LA4_0 >= '0' && LA4_0 <= '9') || (LA4_0 >= 'A' && LA4_0 <= 'Z') || LA4_0 == '_' || (LA4_0 >= 'a' && LA4_0 <= 'z') || (LA4_0 >= '\u00C0' && LA4_0 <= '\u00D6') || (LA4_0 >= '\u00D8' && LA4_0 <= '\u00F6') || (LA4_0 >= '\u00F8' && LA4_0 <= '\u00FF'))) {
          alt4 = 1
        }


        alt4 match {
          case 1 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
            if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || input.LA(1) == '_' || (input.LA(1) >= 'a' && input.LA(1) <= 'z') || (input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6') || (input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6') || (input.LA(1) >= '\u00F8' && input.LA(1) <= '\u00FF')) {
              input.consume()
              state.failed = false
            }
            else {
              if (state.backtracking > 0) {
                state.failed = true;
                return
              }
              val mse = new MismatchedSetException(null, input)
              recover(mse)
              throw mse
            }


          case _ => loop4 = false
        }
      } while (loop4);




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BASIC_IDENTIFIER"

  // $ANTLR start "EXTENDED_IDENTIFIER"
  @throws(classOf[RecognitionException])
  def mEXTENDED_IDENTIFIER(): Unit = {
    try {
      var _type = EXTENDED_IDENTIFIER
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1956:21: ( '\\\\' ( '\\\"' | '\\\\\\\\' | GRAPHIC_CHARACTER )+ '\\\\' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1956:23: '\\\\' ( '\\\"' | '\\\\\\\\' | GRAPHIC_CHARACTER )+ '\\\\'
      `match`('\\');
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1956:28: ( '\\\"' | '\\\\\\\\' | GRAPHIC_CHARACTER )+
      var cnt5 = 0
      var loop5 = true
      do {
        var alt5 = 4
        input.LA(1) match {
          case '\\' =>
            val LA5_1 = input.LA(2)

            if ((LA5_1 == '\\')) {
              alt5 = 2
            }


          case '\"' =>
            alt5 = 1
          case ' ' | '!' | '#' | '$' | '%' | '&' | '\'' | '(' | ')' | '*' | '+' | ',' | '-' | '.' | '/' | '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | ':' | ';' | '<' | '=' | '>' | '?' | '@' | 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' | 'N' | 'O' | 'P' | 'Q' | 'R' | 'S' | 'T' | 'U' | 'V' | 'W' | 'X' | 'Y' | 'Z' | '[' | ']' | '^' | '_' | '`' | 'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p' | 'q' | 'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z' | '{' | '|' | '}' | '~' | '\u00A0' | '\u00A1' | '\u00A2' | '\u00A3' | '\u00A4' | '\u00A5' | '\u00A6' | '\u00A7' | '\u00A8' | '\u00A9' | '\u00AA' | '\u00AB' | '\u00AC' | '\u00AD' | '\u00AE' | '\u00AF' | '\u00B0' | '\u00B1' | '\u00B2' | '\u00B3' | '\u00B4' | '\u00B5' | '\u00B6' | '\u00B7' | '\u00B8' | '\u00B9' | '\u00BA' | '\u00BB' | '\u00BC' | '\u00BD' | '\u00BE' | '\u00BF' | '\u00C0' | '\u00C1' | '\u00C2' | '\u00C3' | '\u00C4' | '\u00C5' | '\u00C6' | '\u00C7' | '\u00C8' | '\u00C9' | '\u00CA' | '\u00CB' | '\u00CC' | '\u00CD' | '\u00CE' | '\u00CF' | '\u00D0' | '\u00D1' | '\u00D2' | '\u00D3' | '\u00D4' | '\u00D5' | '\u00D6' | '\u00D7' | '\u00D8' | '\u00D9' | '\u00DA' | '\u00DB' | '\u00DC' | '\u00DD' | '\u00DE' | '\u00DF' | '\u00E0' | '\u00E1' | '\u00E2' | '\u00E3' | '\u00E4' | '\u00E5' | '\u00E6' | '\u00E7' | '\u00E8' | '\u00E9' | '\u00EA' | '\u00EB' | '\u00EC' | '\u00ED' | '\u00EE' | '\u00EF' | '\u00F0' | '\u00F1' | '\u00F2' | '\u00F3' | '\u00F4' | '\u00F5' | '\u00F6' | '\u00F7' | '\u00F8' | '\u00F9' | '\u00FA' | '\u00FB' | '\u00FC' | '\u00FD' | '\u00FE' | '\u00FF' =>
            alt5 = 3

          case _ =>
        }

        alt5 match {
          case 1 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1956:30: '\\\"'
            `match`('\"'); if (state.failed) return

          case 2 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1956:37: '\\\\\\\\'
            `match`("\\\\"); if (state.failed) return


          case 3 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1956:46: GRAPHIC_CHARACTER
            mGRAPHIC_CHARACTER(); if (state.failed) return


          case _ =>
            if (cnt5 >= 1) loop5 = false
            else {
              if (state.backtracking > 0) {
                state.failed = true;
                return
              }
              val eee = new EarlyExitException(5, input)
              throw eee
            }
        }
        cnt5 += 1
      } while (loop5);

      `match`('\\');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "EXTENDED_IDENTIFIER"

  // $ANTLR start "BASED_LITERAL"
  @throws(classOf[RecognitionException])
  def mBASED_LITERAL(): Unit = {
    try {
      var _type = BASED_LITERAL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1958:15: ( INTEGER '#' BASED_INTEGER ( DOT BASED_INTEGER )? '#' ( EXPONENT )? )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1958:17: INTEGER '#' BASED_INTEGER ( DOT BASED_INTEGER )? '#' ( EXPONENT )?
      mINTEGER();
      if (state.failed) return
      `match`('#');
      if (state.failed) return
      mBASED_INTEGER();
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1958:43: ( DOT BASED_INTEGER )?
      var alt6 = 2
      val LA6_0 = input.LA(1)

      if ((LA6_0 == '.')) {
        alt6 = 1
      }
      alt6 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1958:45: DOT BASED_INTEGER
          mDOT();
          if (state.failed) return
          mBASED_INTEGER();
          if (state.failed) return


        case _ =>
      }

      `match`('#');
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1958:70: ( EXPONENT )?
      var alt7 = 2
      val LA7_0 = input.LA(1)

      if ((LA7_0 == 'e')) {
        alt7 = 1
      }
      alt7 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1958:70: EXPONENT
          mEXPONENT(); if (state.failed) return


        case _ =>
      }




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BASED_LITERAL"

  // $ANTLR start "INTEGER_LITERAL"
  @throws(classOf[RecognitionException])
  def mINTEGER_LITERAL(): Unit = {
    try {
      var _type = INTEGER_LITERAL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1960:17: ( INTEGER ( EXPONENT )? )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1960:19: INTEGER ( EXPONENT )?
      mINTEGER();
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1960:27: ( EXPONENT )?
      var alt8 = 2
      val LA8_0 = input.LA(1)

      if ((LA8_0 == 'e')) {
        alt8 = 1
      }
      alt8 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1960:27: EXPONENT
          mEXPONENT(); if (state.failed) return


        case _ =>
      }




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "INTEGER_LITERAL"

  // $ANTLR start "REAL_LITERAL"
  @throws(classOf[RecognitionException])
  def mREAL_LITERAL(): Unit = {
    try {
      var _type = REAL_LITERAL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1962:14: ( INTEGER DOT INTEGER ( EXPONENT )? )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1962:16: INTEGER DOT INTEGER ( EXPONENT )?
      mINTEGER();
      if (state.failed) return
      mDOT();
      if (state.failed) return
      mINTEGER();
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1962:38: ( EXPONENT )?
      var alt9 = 2
      val LA9_0 = input.LA(1)

      if ((LA9_0 == 'e')) {
        alt9 = 1
      }
      alt9 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1962:38: EXPONENT
          mEXPONENT(); if (state.failed) return


        case _ =>
      }




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "REAL_LITERAL"

  // $ANTLR start "APOSTROPHE"
  @throws(classOf[RecognitionException])
  def mAPOSTROPHE(): Unit = {
    try {
      var _type = APOSTROPHE
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1964:12: ( '\\'' ( ( ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )=> ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )? )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1965:2: '\\'' ( ( ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )=> ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )?
      `match`('\'');
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1966:2: ( ( ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )=> ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )?
      var alt10 = 2
      val LA10_0 = input.LA(1)

      if (((LA10_0 >= ' ' && LA10_0 <= '~') || (LA10_0 >= '\u00A0' && LA10_0 <= '\u00FF')) && (synpred1_VHDL())) {
        alt10 = 1
      }
      alt10 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1967:3: ( ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )=> ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\''
          if ((input.LA(1) >= ' ' && input.LA(1) <= '~') || (input.LA(1) >= '\u00A0' && input.LA(1) <= '\u00FF')) {
            input.consume()
            state.failed = false
          }
          else {
            if (state.backtracking > 0) {
              state.failed = true;
              return
            }
            val mse = new MismatchedSetException(null, input)
            recover(mse)
            throw mse
          }

          `match`('\'');
          if (state.failed) return
          if (state.backtracking == 0) {
            _type = CHARACTER_LITERAL;
          }


        case _ =>
      }




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "APOSTROPHE"

  // $ANTLR start "STRING_LITERAL"
  @throws(classOf[RecognitionException])
  def mSTRING_LITERAL(): Unit = {
    try {
      var _type = STRING_LITERAL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:16: ( '\\\"' ( '\\\"\\\"' | '\\\\' | GRAPHIC_CHARACTER )* '\\\"' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:18: '\\\"' ( '\\\"\\\"' | '\\\\' | GRAPHIC_CHARACTER )* '\\\"'
      `match`('\"');
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:23: ( '\\\"\\\"' | '\\\\' | GRAPHIC_CHARACTER )*
      var loop11 = true
      do {
        var alt11 = 4
        input.LA(1) match {
          case '\"' =>
            val LA11_1 = input.LA(2)

            if ((LA11_1 == '\"')) {
              alt11 = 1
            }


          case '\\' =>
            alt11 = 2
          case ' ' | '!' | '#' | '$' | '%' | '&' | '\'' | '(' | ')' | '*' | '+' | ',' | '-' | '.' | '/' | '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | ':' | ';' | '<' | '=' | '>' | '?' | '@' | 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' | 'N' | 'O' | 'P' | 'Q' | 'R' | 'S' | 'T' | 'U' | 'V' | 'W' | 'X' | 'Y' | 'Z' | '[' | ']' | '^' | '_' | '`' | 'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p' | 'q' | 'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z' | '{' | '|' | '}' | '~' | '\u00A0' | '\u00A1' | '\u00A2' | '\u00A3' | '\u00A4' | '\u00A5' | '\u00A6' | '\u00A7' | '\u00A8' | '\u00A9' | '\u00AA' | '\u00AB' | '\u00AC' | '\u00AD' | '\u00AE' | '\u00AF' | '\u00B0' | '\u00B1' | '\u00B2' | '\u00B3' | '\u00B4' | '\u00B5' | '\u00B6' | '\u00B7' | '\u00B8' | '\u00B9' | '\u00BA' | '\u00BB' | '\u00BC' | '\u00BD' | '\u00BE' | '\u00BF' | '\u00C0' | '\u00C1' | '\u00C2' | '\u00C3' | '\u00C4' | '\u00C5' | '\u00C6' | '\u00C7' | '\u00C8' | '\u00C9' | '\u00CA' | '\u00CB' | '\u00CC' | '\u00CD' | '\u00CE' | '\u00CF' | '\u00D0' | '\u00D1' | '\u00D2' | '\u00D3' | '\u00D4' | '\u00D5' | '\u00D6' | '\u00D7' | '\u00D8' | '\u00D9' | '\u00DA' | '\u00DB' | '\u00DC' | '\u00DD' | '\u00DE' | '\u00DF' | '\u00E0' | '\u00E1' | '\u00E2' | '\u00E3' | '\u00E4' | '\u00E5' | '\u00E6' | '\u00E7' | '\u00E8' | '\u00E9' | '\u00EA' | '\u00EB' | '\u00EC' | '\u00ED' | '\u00EE' | '\u00EF' | '\u00F0' | '\u00F1' | '\u00F2' | '\u00F3' | '\u00F4' | '\u00F5' | '\u00F6' | '\u00F7' | '\u00F8' | '\u00F9' | '\u00FA' | '\u00FB' | '\u00FC' | '\u00FD' | '\u00FE' | '\u00FF' =>
            alt11 = 3

          case _ =>
        }

        alt11 match {
          case 1 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:25: '\\\"\\\"'
            `match`("\"\""); if (state.failed) return


          case 2 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:34: '\\\\'
            `match`('\\'); if (state.failed) return

          case 3 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:41: GRAPHIC_CHARACTER
            mGRAPHIC_CHARACTER(); if (state.failed) return


          case _ => loop11 = false
        }
      } while (loop11);

      `match`('\"');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "STRING_LITERAL"

  // $ANTLR start "BIT_STRING_LITERAL"
  @throws(classOf[RecognitionException])
  def mBIT_STRING_LITERAL(): Unit = {
    try {
      var _type = BIT_STRING_LITERAL
      var _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1973:20: ( BASE_SPECIFIER '\\\"' ( BASED_INTEGER )? '\\\"' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1973:46: BASE_SPECIFIER '\\\"' ( BASED_INTEGER )? '\\\"'
      mBASE_SPECIFIER();
      if (state.failed) return
      `match`('\"');
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1973:66: ( BASED_INTEGER )?
      var alt12 = 2
      val LA12_0 = input.LA(1)

      if (((LA12_0 >= '0' && LA12_0 <= '9') || (LA12_0 >= 'A' && LA12_0 <= 'Z') || (LA12_0 >= 'a' && LA12_0 <= 'z') || (LA12_0 >= '\u00C0' && LA12_0 <= '\u00D6') || (LA12_0 >= '\u00D8' && LA12_0 <= '\u00F6') || (LA12_0 >= '\u00F8' && LA12_0 <= '\u00FF'))) {
        alt12 = 1
      }
      alt12 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1973:66: BASED_INTEGER
          mBASED_INTEGER(); if (state.failed) return


        case _ =>
      }

      `match`('\"');
      if (state.failed) return



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BIT_STRING_LITERAL"

  // $ANTLR start "BASE_SPECIFIER"
  @throws(classOf[RecognitionException])
  def mBASE_SPECIFIER(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1976:16: ( 'b' | 'o' | 'x' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if (input.LA(1) == 'b' || input.LA(1) == 'o' || input.LA(1) == 'x') {
        input.consume()
        state.failed = false
      }
      else {
        if (state.backtracking > 0) {
          state.failed = true;
          return
        }
        val mse = new MismatchedSetException(null, input)
        recover(mse)
        throw mse
      }


    }
    finally {
    }
  }

  // $ANTLR end "BASE_SPECIFIER"

  // $ANTLR start "BASED_INTEGER"
  @throws(classOf[RecognitionException])
  def mBASED_INTEGER(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1979:15: ( EXTENDED_DIGIT ( ( '_' )? EXTENDED_DIGIT )* )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1979:17: EXTENDED_DIGIT ( ( '_' )? EXTENDED_DIGIT )*
      mEXTENDED_DIGIT();
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1979:32: ( ( '_' )? EXTENDED_DIGIT )*
      var loop14 = true
      do {
        var alt14 = 2
        val LA14_0 = input.LA(1)

        if (((LA14_0 >= '0' && LA14_0 <= '9') || (LA14_0 >= 'A' && LA14_0 <= 'Z') || LA14_0 == '_' || (LA14_0 >= 'a' && LA14_0 <= 'z') || (LA14_0 >= '\u00C0' && LA14_0 <= '\u00D6') || (LA14_0 >= '\u00D8' && LA14_0 <= '\u00F6') || (LA14_0 >= '\u00F8' && LA14_0 <= '\u00FF'))) {
          alt14 = 1
        }


        alt14 match {
          case 1 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1979:34: ( '_' )? EXTENDED_DIGIT
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1979:34: ( '_' )?
            var alt13 = 2
            val LA13_0 = input.LA(1)

            if ((LA13_0 == '_')) {
              alt13 = 1
            }
            alt13 match {
              case 1 =>
              // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1979:34: '_'
                `match`('_'); if (state.failed) return


              case _ =>
            }

            mEXTENDED_DIGIT();
            if (state.failed) return


          case _ => loop14 = false
        }
      } while (loop14);


    }
    finally {
    }
  }

  // $ANTLR end "BASED_INTEGER"

  // $ANTLR start "EXTENDED_DIGIT"
  @throws(classOf[RecognitionException])
  def mEXTENDED_DIGIT(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1982:16: ( DIGIT | LETTER )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || (input.LA(1) >= 'a' && input.LA(1) <= 'z') || (input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6') || (input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6') || (input.LA(1) >= '\u00F8' && input.LA(1) <= '\u00FF')) {
        input.consume()
        state.failed = false
      }
      else {
        if (state.backtracking > 0) {
          state.failed = true;
          return
        }
        val mse = new MismatchedSetException(null, input)
        recover(mse)
        throw mse
      }


    }
    finally {
    }
  }

  // $ANTLR end "EXTENDED_DIGIT"

  // $ANTLR start "INTEGER"
  @throws(classOf[RecognitionException])
  def mINTEGER(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1985:9: ( DIGIT ( ( '_' )? DIGIT )* )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1985:11: DIGIT ( ( '_' )? DIGIT )*
      mDIGIT();
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1985:17: ( ( '_' )? DIGIT )*
      var loop16 = true
      do {
        var alt16 = 2
        val LA16_0 = input.LA(1)

        if (((LA16_0 >= '0' && LA16_0 <= '9') || LA16_0 == '_')) {
          alt16 = 1
        }


        alt16 match {
          case 1 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1985:19: ( '_' )? DIGIT
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1985:19: ( '_' )?
            var alt15 = 2
            val LA15_0 = input.LA(1)

            if ((LA15_0 == '_')) {
              alt15 = 1
            }
            alt15 match {
              case 1 =>
              // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1985:19: '_'
                `match`('_'); if (state.failed) return


              case _ =>
            }

            mDIGIT();
            if (state.failed) return


          case _ => loop16 = false
        }
      } while (loop16);


    }
    finally {
    }
  }

  // $ANTLR end "INTEGER"

  // $ANTLR start "EXPONENT"
  @throws(classOf[RecognitionException])
  def mEXPONENT(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1988:10: ( 'e' ( '+' | '-' )? INTEGER )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1988:12: 'e' ( '+' | '-' )? INTEGER
      `match`('e');
      if (state.failed) return
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1988:16: ( '+' | '-' )?
      var alt17 = 2
      val LA17_0 = input.LA(1)

      if ((LA17_0 == '+' || LA17_0 == '-')) {
        alt17 = 1
      }
      alt17 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
          if (input.LA(1) == '+' || input.LA(1) == '-') {
            input.consume()
            state.failed = false
          }
          else {
            if (state.backtracking > 0) {
              state.failed = true;
              return
            }
            val mse = new MismatchedSetException(null, input)
            recover(mse)
            throw mse
          }


        case _ =>
      }

      mINTEGER();
      if (state.failed) return


    }
    finally {
    }
  }

  // $ANTLR end "EXPONENT"

  // $ANTLR start "LETTER_OR_DIGIT"
  @throws(classOf[RecognitionException])
  def mLETTER_OR_DIGIT(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:17: ( LETTER | DIGIT )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || (input.LA(1) >= 'a' && input.LA(1) <= 'z') || (input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6') || (input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6') || (input.LA(1) >= '\u00F8' && input.LA(1) <= '\u00FF')) {
        input.consume()
        state.failed = false
      }
      else {
        if (state.backtracking > 0) {
          state.failed = true;
          return
        }
        val mse = new MismatchedSetException(null, input)
        recover(mse)
        throw mse
      }


    }
    finally {
    }
  }

  // $ANTLR end "LETTER_OR_DIGIT"

  // $ANTLR start "LETTER"
  @throws(classOf[RecognitionException])
  def mLETTER(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1994:8: ( UPPER_CASE_LETTER | LOWER_CASE_LETTER )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z') || (input.LA(1) >= 'a' && input.LA(1) <= 'z') || (input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6') || (input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6') || (input.LA(1) >= '\u00F8' && input.LA(1) <= '\u00FF')) {
        input.consume()
        state.failed = false
      }
      else {
        if (state.backtracking > 0) {
          state.failed = true;
          return
        }
        val mse = new MismatchedSetException(null, input)
        recover(mse)
        throw mse
      }


    }
    finally {
    }
  }

  // $ANTLR end "LETTER"

  // $ANTLR start "GRAPHIC_CHARACTER"
  @throws(classOf[RecognitionException])
  def mGRAPHIC_CHARACTER(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1997:19: ( UPPER_CASE_LETTER | DIGIT | SPECIAL_CHARACTER | SPACE_CHARACTER | LOWER_CASE_LETTER | OTHER_SPECIAL_CHARACTER )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= ' ' && input.LA(1) <= '!') || (input.LA(1) >= '#' && input.LA(1) <= '[') || (input.LA(1) >= ']' && input.LA(1) <= '~') || (input.LA(1) >= '\u00A0' && input.LA(1) <= '\u00FF')) {
        input.consume()
        state.failed = false
      }
      else {
        if (state.backtracking > 0) {
          state.failed = true;
          return
        }
        val mse = new MismatchedSetException(null, input)
        recover(mse)
        throw mse
      }


    }
    finally {
    }
  }

  // $ANTLR end "GRAPHIC_CHARACTER"

  // $ANTLR start "UPPER_CASE_LETTER"
  @throws(classOf[RecognitionException])
  def mUPPER_CASE_LETTER(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2007:19: ( 'A' .. 'Z' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00de' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z') || (input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6') || (input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00DE')) {
        input.consume()
        state.failed = false
      }
      else {
        if (state.backtracking > 0) {
          state.failed = true;
          return
        }
        val mse = new MismatchedSetException(null, input)
        recover(mse)
        throw mse
      }


    }
    finally {
    }
  }

  // $ANTLR end "UPPER_CASE_LETTER"

  // $ANTLR start "LOWER_CASE_LETTER"
  @throws(classOf[RecognitionException])
  def mLOWER_CASE_LETTER(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2011:19: ( 'a' .. 'z' | '\\u00df' .. '\\u00f6' | '\\u00f8' .. '\\u00ff' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= 'a' && input.LA(1) <= 'z') || (input.LA(1) >= '\u00DF' && input.LA(1) <= '\u00F6') || (input.LA(1) >= '\u00F8' && input.LA(1) <= '\u00FF')) {
        input.consume()
        state.failed = false
      }
      else {
        if (state.backtracking > 0) {
          state.failed = true;
          return
        }
        val mse = new MismatchedSetException(null, input)
        recover(mse)
        throw mse
      }


    }
    finally {
    }
  }

  // $ANTLR end "LOWER_CASE_LETTER"

  // $ANTLR start "DIGIT"
  @throws(classOf[RecognitionException])
  def mDIGIT(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2014:7: ( '0' .. '9' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2014:9: '0' .. '9'
      matchRange('0', '9'); if (state.failed) return


    }
    finally {
    }
  }

  // $ANTLR end "DIGIT"

  // $ANTLR start "SPECIAL_CHARACTER"
  @throws(classOf[RecognitionException])
  def mSPECIAL_CHARACTER(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2017:19: ( '#' | '&' | '\\'' | '(' | ')' | '*' | '+' | ',' | '-' | '.' | '/' | ':' | ';' | '<' | '=' | '>' | '[' | ']' | '_' | '|' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if (input.LA(1) == '#' || (input.LA(1) >= '&' && input.LA(1) <= '/') || (input.LA(1) >= ':' && input.LA(1) <= '>') || input.LA(1) == '[' || input.LA(1) == ']' || input.LA(1) == '_' || input.LA(1) == '|') {
        input.consume()
        state.failed = false
      }
      else {
        if (state.backtracking > 0) {
          state.failed = true;
          return
        }
        val mse = new MismatchedSetException(null, input)
        recover(mse)
        throw mse
      }


    }
    finally {
    }
  }

  // $ANTLR end "SPECIAL_CHARACTER"

  // $ANTLR start "SPACE_CHARACTER"
  @throws(classOf[RecognitionException])
  def mSPACE_CHARACTER(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2022:17: ( ' ' | '\\u00a0' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if (input.LA(1) == ' ' || input.LA(1) == '\u00A0') {
        input.consume()
        state.failed = false
      }
      else {
        if (state.backtracking > 0) {
          state.failed = true;
          return
        }
        val mse = new MismatchedSetException(null, input)
        recover(mse)
        throw mse
      }


    }
    finally {
    }
  }

  // $ANTLR end "SPACE_CHARACTER"

  // $ANTLR start "OTHER_SPECIAL_CHARACTER"
  @throws(classOf[RecognitionException])
  def mOTHER_SPECIAL_CHARACTER(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2025:25: ( '!' | '$' | '%' | '@' | '?' | '^' | '`' | '{' | '}' | '~' | '\\u00a1' .. '\\u00bf' | '\\u00d7' | '\\u00f7' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if (input.LA(1) == '!' || (input.LA(1) >= '$' && input.LA(1) <= '%') || (input.LA(1) >= '?' && input.LA(1) <= '@') || input.LA(1) == '^' || input.LA(1) == '`' || input.LA(1) == '{' || (input.LA(1) >= '}' && input.LA(1) <= '~') || (input.LA(1) >= '\u00A1' && input.LA(1) <= '\u00BF') || input.LA(1) == '\u00D7' || input.LA(1) == '\u00F7') {
        input.consume()
        state.failed = false
      }
      else {
        if (state.backtracking > 0) {
          state.failed = true;
          return
        }
        val mse = new MismatchedSetException(null, input)
        recover(mse)
        throw mse
      }


    }
    finally {
    }
  }

  // $ANTLR end "OTHER_SPECIAL_CHARACTER"

  // $ANTLR start "CHARACTER_LITERAL"
  @throws(classOf[RecognitionException])
  def mCHARACTER_LITERAL(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2030:19: ()
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2030:21:


    }
    finally {
    }
  }

  // $ANTLR end "CHARACTER_LITERAL"

  @throws(classOf[RecognitionException])
  def mTokens(): Unit = {
    // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:8: ( ABS | ACCESS | AFTER | ALIAS | ALL | AND | ARCHITECTURE | ARRAY | ASSERT | ATTRIBUTE | BEGIN | BLOCK | BODY | BUFFER | BUS | CASE | COMPONENT | CONFIGURATION | CONSTANT | DISCONNECT | DOWNTO | ELSE | ELSIF | END | ENTITY | EXIT | FILE | FOR | FUNCTION | GENERATE | GENERIC | GROUP | GUARDED | IF | IMPURE | IN | INERTIAL | INOUT | IS | LABEL | LIBRARY | LINKAGE | LITERAL | LOOP | MAP | MOD | NAND | NEW | NEXT | NOR | NOT | NULL | OF | ON | OPEN | OR | OTHERS | OUT | PACKAGE | PORT | POSTPONED | PROCEDURE | PROCESS | PROTECTED | PURE | RANGE | RECORD | REGISTER | REJECT | REM | REPORT | RETURN | ROL | ROR | SELECT | SEVERITY | SHARED | SIGNAL | SLA | SLL | SRA | SRL | SUBTYPE | THEN | TO | TRANSPORT | TYPE | UNAFFECTED | UNITS | UNTIL | USE | VARIABLE | WAIT | WHEN | WHILE | WITH | XNOR | XOR | NATURE | TERMINAL | QUANTITY | ACROSS | THROUGH | SPECTRUM | NOISE | SUBNATURE | LIMIT | REFERENCE | PROCEDURAL | DOUBLESTAR | AMS_ASSIGN | LEQ | GEQ | ARROW | NEQ | VAR_ASSIGN | BOX | DBLQUOTE | SEMICOLON | COMMA | AMPERSAND | LPAREN | RPAREN | LBRACKET | RBRACKET | COLON | MUL | DIV | PLUS | MINUS | LT | GT | EQ | BAR | DOT | BREAK | TOLERANCE | WS | NESTED_ML_COMMENT | LINECOMMENT | BASIC_IDENTIFIER | EXTENDED_IDENTIFIER | BASED_LITERAL | INTEGER_LITERAL | REAL_LITERAL | APOSTROPHE | STRING_LITERAL | BIT_STRING_LITERAL )
    var alt18 = 148
    alt18 = dfa18.predict(input)
    alt18 match {
      case 1 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:10: ABS
        mABS(); if (state.failed) return

      case 2 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:14: ACCESS
        mACCESS(); if (state.failed) return

      case 3 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:21: AFTER
        mAFTER(); if (state.failed) return

      case 4 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:27: ALIAS
        mALIAS(); if (state.failed) return

      case 5 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:33: ALL
        mALL(); if (state.failed) return

      case 6 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:37: AND
        mAND(); if (state.failed) return

      case 7 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:41: ARCHITECTURE
        mARCHITECTURE(); if (state.failed) return

      case 8 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:54: ARRAY
        mARRAY(); if (state.failed) return

      case 9 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:60: ASSERT
        mASSERT(); if (state.failed) return

      case 10 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:67: ATTRIBUTE
        mATTRIBUTE(); if (state.failed) return

      case 11 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:77: BEGIN
        mBEGIN(); if (state.failed) return

      case 12 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:83: BLOCK
        mBLOCK(); if (state.failed) return

      case 13 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:89: BODY
        mBODY(); if (state.failed) return

      case 14 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:94: BUFFER
        mBUFFER(); if (state.failed) return

      case 15 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:101: BUS
        mBUS(); if (state.failed) return

      case 16 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:105: CASE
        mCASE(); if (state.failed) return

      case 17 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:110: COMPONENT
        mCOMPONENT(); if (state.failed) return

      case 18 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:120: CONFIGURATION
        mCONFIGURATION(); if (state.failed) return

      case 19 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:134: CONSTANT
        mCONSTANT(); if (state.failed) return

      case 20 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:143: DISCONNECT
        mDISCONNECT(); if (state.failed) return

      case 21 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:154: DOWNTO
        mDOWNTO(); if (state.failed) return

      case 22 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:161: ELSE
        mELSE(); if (state.failed) return

      case 23 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:166: ELSIF
        mELSIF(); if (state.failed) return

      case 24 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:172: END
        mEND(); if (state.failed) return

      case 25 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:176: ENTITY
        mENTITY(); if (state.failed) return

      case 26 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:183: EXIT
        mEXIT(); if (state.failed) return

      case 27 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:188: FILE
        mFILE(); if (state.failed) return

      case 28 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:193: FOR
        mFOR(); if (state.failed) return

      case 29 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:197: FUNCTION
        mFUNCTION(); if (state.failed) return

      case 30 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:206: GENERATE
        mGENERATE(); if (state.failed) return

      case 31 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:215: GENERIC
        mGENERIC(); if (state.failed) return

      case 32 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:223: GROUP
        mGROUP(); if (state.failed) return

      case 33 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:229: GUARDED
        mGUARDED(); if (state.failed) return

      case 34 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:237: IF
        mIF(); if (state.failed) return

      case 35 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:240: IMPURE
        mIMPURE(); if (state.failed) return

      case 36 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:247: IN
        mIN(); if (state.failed) return

      case 37 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:250: INERTIAL
        mINERTIAL(); if (state.failed) return

      case 38 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:259: INOUT
        mINOUT(); if (state.failed) return

      case 39 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:265: IS
        mIS(); if (state.failed) return

      case 40 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:268: LABEL
        mLABEL(); if (state.failed) return

      case 41 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:274: LIBRARY
        mLIBRARY(); if (state.failed) return

      case 42 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:282: LINKAGE
        mLINKAGE(); if (state.failed) return

      case 43 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:290: LITERAL
        mLITERAL(); if (state.failed) return

      case 44 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:298: LOOP
        mLOOP(); if (state.failed) return

      case 45 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:303: MAP
        mMAP(); if (state.failed) return

      case 46 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:307: MOD
        mMOD(); if (state.failed) return

      case 47 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:311: NAND
        mNAND(); if (state.failed) return

      case 48 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:316: NEW
        mNEW(); if (state.failed) return

      case 49 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:320: NEXT
        mNEXT(); if (state.failed) return

      case 50 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:325: NOR
        mNOR(); if (state.failed) return

      case 51 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:329: NOT
        mNOT(); if (state.failed) return

      case 52 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:333: NULL
        mNULL(); if (state.failed) return

      case 53 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:338: OF
        mOF(); if (state.failed) return

      case 54 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:341: ON
        mON(); if (state.failed) return

      case 55 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:344: OPEN
        mOPEN(); if (state.failed) return

      case 56 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:349: OR
        mOR(); if (state.failed) return

      case 57 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:352: OTHERS
        mOTHERS(); if (state.failed) return

      case 58 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:359: OUT
        mOUT(); if (state.failed) return

      case 59 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:363: PACKAGE
        mPACKAGE(); if (state.failed) return

      case 60 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:371: PORT
        mPORT(); if (state.failed) return

      case 61 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:376: POSTPONED
        mPOSTPONED(); if (state.failed) return

      case 62 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:386: PROCEDURE
        mPROCEDURE(); if (state.failed) return

      case 63 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:396: PROCESS
        mPROCESS(); if (state.failed) return

      case 64 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:404: PROTECTED
        mPROTECTED(); if (state.failed) return

      case 65 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:414: PURE
        mPURE(); if (state.failed) return

      case 66 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:419: RANGE
        mRANGE(); if (state.failed) return

      case 67 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:425: RECORD
        mRECORD(); if (state.failed) return

      case 68 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:432: REGISTER
        mREGISTER(); if (state.failed) return

      case 69 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:441: REJECT
        mREJECT(); if (state.failed) return

      case 70 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:448: REM
        mREM(); if (state.failed) return

      case 71 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:452: REPORT
        mREPORT(); if (state.failed) return

      case 72 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:459: RETURN
        mRETURN(); if (state.failed) return

      case 73 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:466: ROL
        mROL(); if (state.failed) return

      case 74 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:470: ROR
        mROR(); if (state.failed) return

      case 75 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:474: SELECT
        mSELECT(); if (state.failed) return

      case 76 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:481: SEVERITY
        mSEVERITY(); if (state.failed) return

      case 77 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:490: SHARED
        mSHARED(); if (state.failed) return

      case 78 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:497: SIGNAL
        mSIGNAL(); if (state.failed) return

      case 79 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:504: SLA
        mSLA(); if (state.failed) return

      case 80 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:508: SLL
        mSLL(); if (state.failed) return

      case 81 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:512: SRA
        mSRA(); if (state.failed) return

      case 82 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:516: SRL
        mSRL(); if (state.failed) return

      case 83 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:520: SUBTYPE
        mSUBTYPE(); if (state.failed) return

      case 84 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:528: THEN
        mTHEN(); if (state.failed) return

      case 85 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:533: TO
        mTO(); if (state.failed) return

      case 86 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:536: TRANSPORT
        mTRANSPORT(); if (state.failed) return

      case 87 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:546: TYPE
        mTYPE(); if (state.failed) return

      case 88 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:551: UNAFFECTED
        mUNAFFECTED(); if (state.failed) return

      case 89 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:562: UNITS
        mUNITS(); if (state.failed) return

      case 90 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:568: UNTIL
        mUNTIL(); if (state.failed) return

      case 91 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:574: USE
        mUSE(); if (state.failed) return

      case 92 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:578: VARIABLE
        mVARIABLE(); if (state.failed) return

      case 93 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:587: WAIT
        mWAIT(); if (state.failed) return

      case 94 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:592: WHEN
        mWHEN(); if (state.failed) return

      case 95 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:597: WHILE
        mWHILE(); if (state.failed) return

      case 96 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:603: WITH
        mWITH(); if (state.failed) return

      case 97 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:608: XNOR
        mXNOR(); if (state.failed) return

      case 98 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:613: XOR
        mXOR(); if (state.failed) return

      case 99 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:617: NATURE
        mNATURE(); if (state.failed) return

      case 100 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:624: TERMINAL
        mTERMINAL(); if (state.failed) return

      case 101 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:633: QUANTITY
        mQUANTITY(); if (state.failed) return

      case 102 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:642: ACROSS
        mACROSS(); if (state.failed) return

      case 103 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:649: THROUGH
        mTHROUGH(); if (state.failed) return

      case 104 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:657: SPECTRUM
        mSPECTRUM(); if (state.failed) return

      case 105 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:666: NOISE
        mNOISE(); if (state.failed) return

      case 106 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:672: SUBNATURE
        mSUBNATURE(); if (state.failed) return

      case 107 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:682: LIMIT
        mLIMIT(); if (state.failed) return

      case 108 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:688: REFERENCE
        mREFERENCE(); if (state.failed) return

      case 109 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:698: PROCEDURAL
        mPROCEDURAL(); if (state.failed) return

      case 110 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:709: DOUBLESTAR
        mDOUBLESTAR(); if (state.failed) return

      case 111 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:720: AMS_ASSIGN
        mAMS_ASSIGN(); if (state.failed) return

      case 112 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:731: LEQ
        mLEQ(); if (state.failed) return

      case 113 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:735: GEQ
        mGEQ(); if (state.failed) return

      case 114 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:739: ARROW
        mARROW(); if (state.failed) return

      case 115 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:745: NEQ
        mNEQ(); if (state.failed) return

      case 116 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:749: VAR_ASSIGN
        mVAR_ASSIGN(); if (state.failed) return

      case 117 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:760: BOX
        mBOX(); if (state.failed) return

      case 118 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:764: DBLQUOTE
        mDBLQUOTE(); if (state.failed) return

      case 119 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:773: SEMICOLON
        mSEMICOLON(); if (state.failed) return

      case 120 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:783: COMMA
        mCOMMA(); if (state.failed) return

      case 121 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:789: AMPERSAND
        mAMPERSAND(); if (state.failed) return

      case 122 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:799: LPAREN
        mLPAREN(); if (state.failed) return

      case 123 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:806: RPAREN
        mRPAREN(); if (state.failed) return

      case 124 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:813: LBRACKET
        mLBRACKET(); if (state.failed) return

      case 125 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:822: RBRACKET
        mRBRACKET(); if (state.failed) return

      case 126 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:831: COLON
        mCOLON(); if (state.failed) return

      case 127 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:837: MUL
        mMUL(); if (state.failed) return

      case 128 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:841: DIV
        mDIV(); if (state.failed) return

      case 129 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:845: PLUS
        mPLUS(); if (state.failed) return

      case 130 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:850: MINUS
        mMINUS(); if (state.failed) return

      case 131 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:856: LT
        mLT(); if (state.failed) return

      case 132 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:859: GT
        mGT(); if (state.failed) return

      case 133 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:862: EQ
        mEQ(); if (state.failed) return

      case 134 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:865: BAR
        mBAR(); if (state.failed) return

      case 135 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:869: DOT
        mDOT(); if (state.failed) return

      case 136 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:873: BREAK
        mBREAK(); if (state.failed) return

      case 137 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:879: TOLERANCE
        mTOLERANCE(); if (state.failed) return

      case 138 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:889: WS
        mWS(); if (state.failed) return

      case 139 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:892: NESTED_ML_COMMENT
        mNESTED_ML_COMMENT(); if (state.failed) return

      case 140 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:910: LINECOMMENT
        mLINECOMMENT(); if (state.failed) return

      case 141 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:922: BASIC_IDENTIFIER
        mBASIC_IDENTIFIER(); if (state.failed) return

      case 142 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:939: EXTENDED_IDENTIFIER
        mEXTENDED_IDENTIFIER(); if (state.failed) return

      case 143 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:959: BASED_LITERAL
        mBASED_LITERAL(); if (state.failed) return

      case 144 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:973: INTEGER_LITERAL
        mINTEGER_LITERAL(); if (state.failed) return

      case 145 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:989: REAL_LITERAL
        mREAL_LITERAL(); if (state.failed) return

      case 146 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1002: APOSTROPHE
        mAPOSTROPHE(); if (state.failed) return

      case 147 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1013: STRING_LITERAL
        mSTRING_LITERAL(); if (state.failed) return

      case 148 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1028: BIT_STRING_LITERAL
        mBIT_STRING_LITERAL(); if (state.failed) return


      case _ =>
    }

  }

  // $ANTLR start synpred1_VHDL
  @throws(classOf[RecognitionException])
  def synpred1_VHDL_fragment(): Unit = {
    // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1967:3: ( ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )
    // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1967:4: ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\''
    if ((input.LA(1) >= ' ' && input.LA(1) <= '~') || (input.LA(1) >= '\u00A0' && input.LA(1) <= '\u00FF')) {
      input.consume()
      state.failed = false
    }
    else {
      if (state.backtracking > 0) {
        state.failed = true;
        return
      }
      val mse = new MismatchedSetException(null, input)
      recover(mse)
      throw mse
    }

    `match`('\'');
    if (state.failed) return


  }

  // $ANTLR end synpred1_VHDL

  def synpred1_VHDL(): Boolean = {
    state.backtracking += 1
    val start = input.mark()
    try {
      synpred1_VHDL_fragment() // can never throw exception
    } catch {
      case re: RecognitionException => error("impossible: " + re)
    }
    val success = !state.failed
    input.rewind(start)
    state.backtracking -= 1
    state.failed = false
    success
  }

  final

  private class DFA18(rec: BaseRecognizer) extends DFA {
    private val DFA18_eotS = "\1\uffff\25\51\1\170\1\173\1\176\1\u0080\1" +
      "\u0083\1\u0085\1\u0087\10\uffff\1\u0089\5\uffff\1\u008a\1\uffff\15\51" +
      "\1\uffff\15\51\1\u00af\1\51\1\u00b3\1\u00b4\11\51\1\u00c5\1\u00c6\1" +
      "\51\1\u00c8\21\51\1\u00e7\14\51\25\uffff\1\u008a\2\uffff\1\u00f7\4\51" +
      "\1\u00fc\1\u00fd\10\51\1\u0106\7\51\1\u0110\3\51\1\u0114\4\51\1\uffff" +
      "\3\51\2\uffff\6\51\1\u0122\1\u0123\2\51\1\u0126\1\51\1\u0128\1\u0129" +
      "\2\51\2\uffff\1\51\1\uffff\1\51\1\u012e\11\51\1\u0139\3\51\1\u013d\1" +
      "\u013e\4\51\1\u0143\1\u0144\1\u0145\1\u0146\5\51\1\uffff\6\51\1\u0153" +
      "\6\51\1\u015a\1\51\1\uffff\4\51\2\uffff\6\51\1\u0166\1\51\1\uffff\1" +
      "\51\1\u0169\5\51\1\u016f\1\51\1\uffff\1\51\1\u0172\1\u0173\1\uffff\14" +
      "\51\1\u0180\2\uffff\1\u0181\1\51\1\uffff\1\u0183\2\uffff\1\51\1\u0185" +
      "\1\u0186\1\51\1\uffff\1\51\1\u0189\3\51\1\u018d\4\51\1\uffff\3\51\2" +
      "\uffff\4\51\4\uffff\3\51\1\u019c\3\51\1\u01a0\4\51\1\uffff\1\51\1\u01a6" +
      "\1\u01a7\1\51\1\u01a9\1\u01aa\1\uffff\3\51\1\u01ae\1\u01af\1\51\1\u01b1" +
      "\2\51\1\u01b4\1\u01b5\1\uffff\1\51\1\u01b7\1\uffff\5\51\1\uffff\1\u01bd" +
      "\1\51\2\uffff\2\51\1\u01c2\3\51\1\u01c6\1\u01c7\3\51\1\u01cb\2\uffff" +
      "\1\51\1\uffff\1\u01cd\2\uffff\2\51\1\uffff\3\51\1\uffff\1\u01d4\15\51" +
      "\1\uffff\3\51\1\uffff\2\51\1\u01e7\1\u01e8\1\51\2\uffff\1\u01ea\2\uffff" +
      "\1\51\1\u01ec\1\u01ed\2\uffff\1\51\1\uffff\1\u01ef\1\51\2\uffff\1\u01f1" +
      "\1\uffff\4\51\1\u01f6\1\uffff\1\u01f7\3\51\1\uffff\1\51\1\u01fc\1\51" +
      "\2\uffff\3\51\1\uffff\1\u0201\1\uffff\1\u0202\5\51\1\uffff\1\u0208\1" +
      "\51\1\u020a\1\u020b\1\u020c\1\51\1\u020e\1\51\1\u0210\1\u0211\10\51" +
      "\2\uffff\1\51\1\uffff\1\51\2\uffff\1\51\1\uffff\1\51\1\uffff\4\51\2" +
      "\uffff\2\51\1\u0224\1\u0225\1\uffff\1\51\1\u0227\1\u0228\1\u0229\2\uffff" +
      "\1\u022a\2\51\1\u022d\1\51\1\uffff\1\51\3\uffff\1\51\1\uffff\1\51\2" +
      "\uffff\1\u0232\2\51\1\u0235\12\51\1\u0240\1\51\1\u0242\1\u0243\2\uffff" +
      "\1\u0244\4\uffff\2\51\1\uffff\1\51\1\u0249\1\51\1\u024b\1\uffff\1\51" +
      "\1\u024d\1\uffff\2\51\1\u0250\1\51\1\u0252\1\u0253\1\51\1\u0255\1\u0256" +
      "\1\51\1\uffff\1\51\3\uffff\1\u0259\1\u025a\1\51\1\u025c\1\uffff\1\u025d" +
      "\1\uffff\1\u025e\1\uffff\1\u025f\1\u0260\1\uffff\1\51\2\uffff\1\51\2" +
      "\uffff\1\51\1\u0264\2\uffff\1\u0265\5\uffff\1\u0266\2\51\3\uffff\1\u0269" +
      "\1\51\1\uffff\1\u026b\1\uffff"
    private val DFA18_eofS = "\u026c\uffff"
    private val DFA18_minS = "\1\11\1\142\1\42\1\141\1\151\1\154\1\151\1" +
      "\145\1\146\3\141\1\42\2\141\2\145\1\156\2\141\1\42\1\165\1\52\3\75\1" +
      "\52\1\75\1\40\10\uffff\1\55\5\uffff\1\43\1\uffff\1\163\1\143\1\164\1" +
      "\151\1\144\1\143\1\163\1\164\1\147\1\157\1\144\1\146\1\145\1\uffff\1" +
      "\163\1\155\1\163\1\167\1\163\1\144\1\151\1\154\1\162\2\156\1\157\1\141" +
      "\1\60\1\160\2\60\2\142\1\157\1\160\1\144\1\156\1\167\1\151\1\154\2\60" +
      "\1\145\1\60\1\150\1\164\1\143\1\162\1\157\1\162\1\156\1\143\2\154\1" +
      "\141\1\147\2\141\1\142\2\145\1\60\1\141\1\160\1\162\1\141\1\145\1\162" +
      "\1\151\1\145\1\164\1\157\1\162\1\141\24\uffff\1\60\1\43\2\uffff\1\60" +
      "\1\145\1\157\1\145\1\141\2\60\1\150\1\141\1\145\1\162\1\151\1\143\1" +
      "\171\1\146\1\60\1\141\1\145\1\160\1\146\1\143\1\156\1\145\1\60\1\151" +
      "\1\164\1\145\1\60\1\143\1\145\1\165\1\162\1\uffff\1\165\1\162\1\165" +
      "\2\uffff\1\145\1\162\1\153\1\145\1\151\1\160\2\60\1\144\1\165\1\60\1" +
      "\164\2\60\1\163\1\154\2\uffff\1\156\1\uffff\1\145\1\60\1\153\2\164\1" +
      "\143\1\145\1\147\1\157\1\151\1\145\1\60\1\157\1\165\1\145\2\60\2\145" +
      "\1\162\1\156\4\60\1\156\1\143\1\156\1\157\1\145\1\uffff\1\156\1\145" +
      "\1\155\1\146\1\164\1\151\1\60\1\151\1\164\1\156\1\154\1\150\1\162\1" +
      "\60\1\156\1\uffff\2\163\1\162\1\163\2\uffff\1\151\1\171\1\162\1\151" +
      "\1\156\1\153\1\60\1\145\1\uffff\1\153\1\60\1\157\1\151\1\164\1\157\1" +
      "\164\1\60\1\146\1\uffff\1\164\2\60\1\uffff\1\164\1\162\1\160\1\144\1" +
      "\162\2\164\1\154\2\141\1\162\1\164\1\60\2\uffff\1\60\1\162\1\uffff\1" +
      "\60\2\uffff\1\145\2\60\1\162\1\uffff\1\141\1\60\1\160\2\145\1\60\1\145" +
      "\1\162\1\163\1\143\1\uffff\3\162\2\uffff\1\143\1\162\1\145\1\141\4\uffff" +
      "\1\171\1\141\1\164\1\60\1\165\1\162\1\163\1\60\1\151\1\146\1\163\1\154" +
      "\1\uffff\1\141\2\60\1\145\2\60\1\uffff\1\164\2\163\2\60\1\164\1\60\1" +
      "\164\1\142\2\60\1\uffff\1\162\1\60\1\uffff\1\156\1\147\1\141\1\156\1" +
      "\157\1\uffff\1\60\1\171\2\uffff\1\151\1\141\1\60\2\145\1\151\2\60\1" +
      "\162\1\147\1\141\1\60\2\uffff\1\145\1\uffff\1\60\2\uffff\1\163\1\147" +
      "\1\uffff\1\157\1\144\1\143\1\uffff\1\60\1\144\3\164\1\156\1\145\1\164" +
      "\1\151\1\144\1\154\1\160\1\164\1\162\1\uffff\1\147\1\141\1\160\1\uffff" +
      "\1\156\1\145\2\60\1\142\2\uffff\1\60\2\uffff\1\151\2\60\2\uffff\1\145" +
      "\1\uffff\1\60\1\165\2\uffff\1\60\1\uffff\1\145\1\165\2\156\1\60\1\uffff" +
      "\1\60\1\157\1\164\1\143\1\uffff\1\144\1\60\1\141\2\uffff\1\171\1\145" +
      "\1\154\1\uffff\1\60\1\uffff\1\60\1\145\1\156\1\165\1\163\1\164\1\uffff" +
      "\1\60\1\145\3\60\1\156\1\60\1\164\2\60\1\145\2\165\1\150\1\156\1\157" +
      "\1\141\1\143\2\uffff\1\154\1\uffff\1\164\2\uffff\1\143\1\uffff\1\164" +
      "\1\uffff\1\156\1\162\1\164\1\145\2\uffff\1\156\1\145\2\60\1\uffff\1" +
      "\154\3\60\2\uffff\1\60\1\145\1\162\1\60\1\145\1\uffff\1\162\3\uffff" +
      "\1\143\1\uffff\1\171\2\uffff\1\60\1\162\1\155\1\60\1\143\1\162\1\154" +
      "\1\164\1\145\1\171\1\164\1\145\1\164\1\141\1\60\1\143\2\60\2\uffff\1" +
      "\60\4\uffff\1\144\1\141\1\uffff\1\144\1\60\1\145\1\60\1\uffff\1\145" +
      "\1\60\1\uffff\1\145\1\164\1\60\1\145\2\60\1\165\2\60\1\164\1\uffff\1" +
      "\164\3\uffff\2\60\1\154\1\60\1\uffff\1\60\1\uffff\1\60\1\uffff\2\60" +
      "\1\uffff\1\144\2\uffff\1\162\2\uffff\1\151\1\60\2\uffff\1\60\5\uffff" +
      "\1\60\1\145\1\157\3\uffff\1\60\1\156\1\uffff\1\60\1\uffff"
    private val DFA18_maxS = "\1\u00ff\1\164\1\165\2\157\1\170\2\165\1\163" +
      "\2\157\3\165\1\157\1\165\1\171\1\163\1\141\1\151\1\157\1\165\1\52\2" +
      "\76\3\75\1\u00ff\10\uffff\1\55\5\uffff\1\137\1\uffff\1\163\1\162\1\164" +
      "\1\154\1\144\1\162\1\163\1\164\1\147\1\157\1\144\1\163\1\145\1\uffff" +
      "\1\163\1\156\1\163\1\167\1\163\1\164\1\151\1\154\1\162\2\156\1\157\1" +
      "\141\1\u00ff\1\160\2\u00ff\1\142\1\164\1\157\1\160\1\144\1\164\1\170" +
      "\1\164\1\154\2\u00ff\1\145\1\u00ff\1\150\1\164\1\143\1\163\1\157\1\162" +
      "\1\156\1\164\1\162\1\166\1\141\1\147\2\154\1\142\1\145\1\162\1\u00ff" +
      "\1\141\1\160\1\162\1\164\1\145\1\162\2\151\1\164\1\157\1\162\1\141\24" +
      "\uffff\1\71\1\137\2\uffff\1\u00ff\1\145\1\157\1\145\1\141\2\u00ff\1" +
      "\150\1\141\1\145\1\162\1\151\1\143\1\171\1\146\1\u00ff\1\141\1\145\1" +
      "\160\1\163\1\143\1\156\1\151\1\u00ff\1\151\1\164\1\145\1\u00ff\1\143" +
      "\1\145\1\165\1\162\1\uffff\1\165\1\162\1\165\2\uffff\1\145\1\162\1\153" +
      "\1\145\1\151\1\160\2\u00ff\1\144\1\165\1\u00ff\1\164\2\u00ff\1\163\1" +
      "\154\2\uffff\1\156\1\uffff\1\145\1\u00ff\1\153\3\164\1\145\1\147\1\157" +
      "\1\151\1\145\1\u00ff\1\157\1\165\1\145\2\u00ff\2\145\1\162\1\156\4\u00ff" +
      "\1\164\1\143\1\156\1\157\1\145\1\uffff\1\156\1\145\1\155\1\146\1\164" +
      "\1\151\1\u00ff\1\151\1\164\1\156\1\154\1\150\1\162\1\u00ff\1\156\1\uffff" +
      "\2\163\1\162\1\163\2\uffff\1\151\1\171\1\162\1\151\1\156\1\153\1\u00ff" +
      "\1\145\1\uffff\1\153\1\u00ff\1\157\1\151\1\164\1\157\1\164\1\u00ff\1" +
      "\146\1\uffff\1\164\2\u00ff\1\uffff\1\164\1\162\1\160\1\144\1\162\2\164" +
      "\1\154\2\141\1\162\1\164\1\u00ff\2\uffff\1\u00ff\1\162\1\uffff\1\u00ff" +
      "\2\uffff\1\145\2\u00ff\1\162\1\uffff\1\141\1\u00ff\1\160\2\145\1\u00ff" +
      "\1\145\1\162\1\163\1\143\1\uffff\3\162\2\uffff\1\143\1\162\1\145\1\141" +
      "\4\uffff\1\171\1\141\1\164\1\u00ff\1\165\1\162\1\163\1\u00ff\1\151\1" +
      "\146\1\163\1\154\1\uffff\1\141\2\u00ff\1\145\2\u00ff\1\uffff\1\164\2" +
      "\163\2\u00ff\1\164\1\u00ff\1\164\1\142\2\u00ff\1\uffff\1\162\1\u00ff" +
      "\1\uffff\1\156\1\147\1\141\1\156\1\157\1\uffff\1\u00ff\1\171\2\uffff" +
      "\2\151\1\u00ff\2\145\1\151\2\u00ff\1\162\1\147\1\141\1\u00ff\2\uffff" +
      "\1\145\1\uffff\1\u00ff\2\uffff\1\163\1\147\1\uffff\1\157\1\163\1\143" +
      "\1\uffff\1\u00ff\1\144\3\164\1\156\1\145\1\164\1\151\1\144\1\154\1\160" +
      "\1\164\1\162\1\uffff\1\147\1\141\1\160\1\uffff\1\156\1\145\2\u00ff\1" +
      "\142\2\uffff\1\u00ff\2\uffff\1\151\2\u00ff\2\uffff\1\145\1\uffff\1\u00ff" +
      "\1\165\2\uffff\1\u00ff\1\uffff\1\145\1\165\2\156\1\u00ff\1\uffff\1\u00ff" +
      "\1\157\1\164\1\143\1\uffff\1\144\1\u00ff\1\141\2\uffff\1\171\1\145\1" +
      "\154\1\uffff\1\u00ff\1\uffff\1\u00ff\1\145\1\156\1\165\1\163\1\164\1" +
      "\uffff\1\u00ff\1\145\3\u00ff\1\156\1\u00ff\1\164\2\u00ff\1\145\2\165" +
      "\1\150\1\156\1\157\1\141\1\143\2\uffff\1\154\1\uffff\1\164\2\uffff\1" +
      "\143\1\uffff\1\164\1\uffff\1\156\1\162\1\164\1\145\2\uffff\1\156\1\145" +
      "\2\u00ff\1\uffff\1\154\3\u00ff\2\uffff\1\u00ff\1\145\1\162\1\u00ff\1" +
      "\145\1\uffff\1\162\3\uffff\1\143\1\uffff\1\171\2\uffff\1\u00ff\1\162" +
      "\1\155\1\u00ff\1\143\1\162\1\154\1\164\1\145\1\171\1\164\1\145\1\164" +
      "\1\141\1\u00ff\1\143\2\u00ff\2\uffff\1\u00ff\4\uffff\1\144\1\145\1\uffff" +
      "\1\144\1\u00ff\1\145\1\u00ff\1\uffff\1\145\1\u00ff\1\uffff\1\145\1\164" +
      "\1\u00ff\1\145\2\u00ff\1\165\2\u00ff\1\164\1\uffff\1\164\3\uffff\2\u00ff" +
      "\1\154\1\u00ff\1\uffff\1\u00ff\1\uffff\1\u00ff\1\uffff\2\u00ff\1\uffff" +
      "\1\144\2\uffff\1\162\2\uffff\1\151\1\u00ff\2\uffff\1\u00ff\5\uffff\1" +
      "\u00ff\1\145\1\157\3\uffff\1\u00ff\1\156\1\uffff\1\u00ff\1\uffff"
    private val DFA18_acceptS = "\35\uffff\1\167\1\170\1\171\1\172\1\173" +
      "\1\174\1\175\1\u0081\1\uffff\1\u0086\1\u0087\1\u008a\1\u008d\1\u008e" +
      "\1\uffff\1\u0092\15\uffff\1\u0094\74\uffff\1\156\1\177\1\157\1\162\1" +
      "\u0085\1\160\1\165\1\u0083\1\161\1\u0084\1\163\1\u008b\1\u0080\1\164" +
      "\1\176\1\u0093\1\166\1\u008c\1\u0082\1\u0090\2\uffff\1\u0091\1\u008f" +
      "\40\uffff\1\42\3\uffff\1\44\1\47\20\uffff\1\65\1\66\1\uffff\1\70\36" +
      "\uffff\1\125\17\uffff\1\1\4\uffff\1\5\1\6\10\uffff\1\17\11\uffff\1\30" +
      "\3\uffff\1\34\15\uffff\1\55\1\56\2\uffff\1\60\1\uffff\1\62\1\63\4\uffff" +
      "\1\72\12\uffff\1\106\3\uffff\1\111\1\112\4\uffff\1\117\1\120\1\121\1" +
      "\122\14\uffff\1\133\6\uffff\1\142\13\uffff\1\15\2\uffff\1\20\5\uffff" +
      "\1\26\2\uffff\1\32\1\33\14\uffff\1\54\1\57\1\uffff\1\61\1\uffff\1\64" +
      "\1\67\2\uffff\1\74\3\uffff\1\101\16\uffff\1\124\3\uffff\1\127\5\uffff" +
      "\1\135\1\136\1\uffff\1\140\1\141\3\uffff\1\3\1\4\1\uffff\1\10\2\uffff" +
      "\1\13\1\14\1\uffff\1\u0088\5\uffff\1\27\4\uffff\1\40\3\uffff\1\46\1" +
      "\50\3\uffff\1\153\1\uffff\1\151\6\uffff\1\102\22\uffff\1\131\1\132\1" +
      "\uffff\1\137\1\uffff\1\2\1\146\1\uffff\1\11\1\uffff\1\16\4\uffff\1\25" +
      "\1\31\4\uffff\1\43\4\uffff\1\143\1\71\5\uffff\1\103\1\uffff\1\105\1" +
      "\107\1\110\1\uffff\1\113\1\uffff\1\115\1\116\22\uffff\1\37\1\41\1\uffff" +
      "\1\51\1\52\1\53\1\73\2\uffff\1\77\4\uffff\1\123\2\uffff\1\147\12\uffff" +
      "\1\23\1\uffff\1\35\1\36\1\45\4\uffff\1\104\1\uffff\1\114\1\uffff\1\150" +
      "\2\uffff\1\144\1\uffff\1\134\1\145\1\uffff\1\12\1\21\2\uffff\1\75\1" +
      "\76\1\uffff\1\100\1\154\1\152\1\u0089\1\126\3\uffff\1\24\1\155\1\130" +
      "\2\uffff\1\7\1\uffff\1\22"
    private val DFA18_specialS = "\u026c\uffff}>"
    private val DFA18_transitionS = Array("\2\50\2\uffff\1\50\22\uffff\1" +
      "\50\1\uffff\1\34\3\uffff\1\37\1\54\1\40\1\41\1\26\1\44\1\36\1\45\1\47\1" +
      "\32\12\53\1\33\1\35\1\30\1\27\1\31\2\uffff\32\51\1\42\1\52\1\43\3\uffff" +
      "\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\51\1\10\2\51\1\11\1\12\1\13\1\14\1\15\1\25" +
      "\1\16\1\17\1\20\1\21\1\22\1\23\1\24\2\51\1\uffff\1\46\103\uffff\27\51\1" +
      "\uffff\37\51\1\uffff\10\51",
      "\1\55\1\56\2\uffff\1\57\5\uffff\1\60\1\uffff\1\61\3\uffff\1\62\1\63\1" +
        "\64",
      "\1\72\102\uffff\1\65\6\uffff\1\66\2\uffff\1\67\2\uffff\1\71\2\uffff\1" +
        "\70",
      "\1\73\15\uffff\1\74",
      "\1\75\5\uffff\1\76",
      "\1\77\1\uffff\1\100\11\uffff\1\101",
      "\1\102\5\uffff\1\103\5\uffff\1\104",
      "\1\105\14\uffff\1\106\2\uffff\1\107",
      "\1\110\6\uffff\1\111\1\112\4\uffff\1\113",
      "\1\114\7\uffff\1\115\5\uffff\1\116",
      "\1\117\15\uffff\1\120",
      "\1\121\3\uffff\1\122\11\uffff\1\123\5\uffff\1\124",
      "\1\72\103\uffff\1\125\7\uffff\1\126\1\uffff\1\127\1\uffff\1\130\1\uffff" +
        "\1\131\1\132",
      "\1\133\15\uffff\1\134\2\uffff\1\135\2\uffff\1\136",
      "\1\137\3\uffff\1\140\11\uffff\1\141",
      "\1\142\2\uffff\1\143\1\144\2\uffff\1\145\3\uffff\1\150\1\uffff\1\146\2" +
        "\uffff\1\147",
      "\1\155\2\uffff\1\151\6\uffff\1\152\2\uffff\1\153\6\uffff\1\154",
      "\1\156\4\uffff\1\157",
      "\1\160",
      "\1\161\6\uffff\1\162\1\163",
      "\1\72\113\uffff\1\164\1\165",
      "\1\166",
      "\1\167",
      "\1\171\1\172",
      "\1\174\1\175",
      "\1\177",
      "\1\u0082\22\uffff\1\u0081",
      "\1\u0084",
      "\137\u0086\41\uffff\140\u0086",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "\1\u0088",
      "",
      "",
      "",
      "",
      "",
      "\1\u008e\12\uffff\1\u008d\1\uffff\12\u008c\45\uffff\1\u008b",
      "",
      "\1\u008f",
      "\1\u0090\16\uffff\1\u0091",
      "\1\u0092",
      "\1\u0093\2\uffff\1\u0094",
      "\1\u0095",
      "\1\u0096\16\uffff\1\u0097",
      "\1\u0098",
      "\1\u0099",
      "\1\u009a",
      "\1\u009b",
      "\1\u009c",
      "\1\u009d\14\uffff\1\u009e",
      "\1\u009f",
      "",
      "\1\u00a0",
      "\1\u00a1\1\u00a2",
      "\1\u00a3",
      "\1\u00a4",
      "\1\u00a5",
      "\1\u00a6\17\uffff\1\u00a7",
      "\1\u00a8",
      "\1\u00a9",
      "\1\u00aa",
      "\1\u00ab",
      "\1\u00ac",
      "\1\u00ad",
      "\1\u00ae",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u00b0",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\4\51\1\u00b1\11\51\1\u00b2\13" +
        "\51\105\uffff\27\51\1\uffff\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u00b5",
      "\1\u00b6\12\uffff\1\u00b9\1\u00b7\5\uffff\1\u00b8",
      "\1\u00ba",
      "\1\u00bb",
      "\1\u00bc",
      "\1\u00bd\5\uffff\1\u00be",
      "\1\u00bf\1\u00c0",
      "\1\u00c3\10\uffff\1\u00c1\1\uffff\1\u00c2",
      "\1\u00c4",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u00c7",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u00c9",
      "\1\u00ca",
      "\1\u00cb",
      "\1\u00cc\1\u00cd",
      "\1\u00ce",
      "\1\u00cf",
      "\1\u00d0",
      "\1\u00d1\2\uffff\1\u00d7\1\u00d2\2\uffff\1\u00d3\2\uffff\1\u00d4\2\uffff" +
        "\1\u00d5\3\uffff\1\u00d6",
      "\1\u00d8\5\uffff\1\u00d9",
      "\1\u00da\11\uffff\1\u00db",
      "\1\u00dc",
      "\1\u00dd",
      "\1\u00de\12\uffff\1\u00df",
      "\1\u00e0\12\uffff\1\u00e1",
      "\1\u00e2",
      "\1\u00e3",
      "\1\u00e4\14\uffff\1\u00e5",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\13\51\1\u00e6\16\51\105\uffff" +
        "\27\51\1\uffff\37\51\1\uffff\10\51",
      "\1\u00e8",
      "\1\u00e9",
      "\1\u00ea",
      "\1\u00eb\7\uffff\1\u00ec\12\uffff\1\u00ed",
      "\1\u00ee",
      "\1\u00ef",
      "\1\u00f0",
      "\1\u00f1\3\uffff\1\u00f2",
      "\1\u00f3",
      "\1\u00f4",
      "\1\u00f5",
      "\1\u00f6",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "\12\u008c",
      "\1\u008e\12\uffff\1\u008d\1\uffff\12\u008c\45\uffff\1\u008b",
      "",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u00f8",
      "\1\u00f9",
      "\1\u00fa",
      "\1\u00fb",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u00fe",
      "\1\u00ff",
      "\1\u0100",
      "\1\u0101",
      "\1\u0102",
      "\1\u0103",
      "\1\u0104",
      "\1\u0105",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0107",
      "\1\u0108",
      "\1\u0109",
      "\1\u010a\14\uffff\1\u010b",
      "\1\u010c",
      "\1\u010d",
      "\1\u010e\3\uffff\1\u010f",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0111",
      "\1\u0112",
      "\1\u0113",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0115",
      "\1\u0116",
      "\1\u0117",
      "\1\u0118",
      "",
      "\1\u0119",
      "\1\u011a",
      "\1\u011b",
      "",
      "",
      "\1\u011c",
      "\1\u011d",
      "\1\u011e",
      "\1\u011f",
      "\1\u0120",
      "\1\u0121",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0124",
      "\1\u0125",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0127",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u012a",
      "\1\u012b",
      "",
      "",
      "\1\u012c",
      "",
      "\1\u012d",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u012f",
      "\1\u0130",
      "\1\u0131",
      "\1\u0132\20\uffff\1\u0133",
      "\1\u0134",
      "\1\u0135",
      "\1\u0136",
      "\1\u0137",
      "\1\u0138",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u013a",
      "\1\u013b",
      "\1\u013c",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u013f",
      "\1\u0140",
      "\1\u0141",
      "\1\u0142",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0148\5\uffff\1\u0147",
      "\1\u0149",
      "\1\u014a",
      "\1\u014b",
      "\1\u014c",
      "",
      "\1\u014d",
      "\1\u014e",
      "\1\u014f",
      "\1\u0150",
      "\1\u0151",
      "\1\u0152",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0154",
      "\1\u0155",
      "\1\u0156",
      "\1\u0157",
      "\1\u0158",
      "\1\u0159",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u015b",
      "",
      "\1\u015c",
      "\1\u015d",
      "\1\u015e",
      "\1\u015f",
      "",
      "",
      "\1\u0160",
      "\1\u0161",
      "\1\u0162",
      "\1\u0163",
      "\1\u0164",
      "\1\u0165",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0167",
      "",
      "\1\u0168",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u016a",
      "\1\u016b",
      "\1\u016c",
      "\1\u016d",
      "\1\u016e",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0170",
      "",
      "\1\u0171",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\1\u0174",
      "\1\u0175",
      "\1\u0176",
      "\1\u0177",
      "\1\u0178",
      "\1\u0179",
      "\1\u017a",
      "\1\u017b",
      "\1\u017c",
      "\1\u017d",
      "\1\u017e",
      "\1\u017f",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0182",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "",
      "\1\u0184",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0187",
      "",
      "\1\u0188",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u018a",
      "\1\u018b",
      "\1\u018c",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u018e",
      "\1\u018f",
      "\1\u0190",
      "\1\u0191",
      "",
      "\1\u0192",
      "\1\u0193",
      "\1\u0194",
      "",
      "",
      "\1\u0195",
      "\1\u0196",
      "\1\u0197",
      "\1\u0198",
      "",
      "",
      "",
      "",
      "\1\u0199",
      "\1\u019a",
      "\1\u019b",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u019d",
      "\1\u019e",
      "\1\u019f",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01a1",
      "\1\u01a2",
      "\1\u01a3",
      "\1\u01a4",
      "",
      "\1\u01a5",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01a8",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\1\u01ab",
      "\1\u01ac",
      "\1\u01ad",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01b0",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01b2",
      "\1\u01b3",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\1\u01b6",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\1\u01b8",
      "\1\u01b9",
      "\1\u01ba",
      "\1\u01bb",
      "\1\u01bc",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01be",
      "",
      "",
      "\1\u01bf",
      "\1\u01c0\7\uffff\1\u01c1",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01c3",
      "\1\u01c4",
      "\1\u01c5",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01c8",
      "\1\u01c9",
      "\1\u01ca",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "",
      "\1\u01cc",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "",
      "\1\u01ce",
      "\1\u01cf",
      "",
      "\1\u01d0",
      "\1\u01d1\16\uffff\1\u01d2",
      "\1\u01d3",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01d5",
      "\1\u01d6",
      "\1\u01d7",
      "\1\u01d8",
      "\1\u01d9",
      "\1\u01da",
      "\1\u01db",
      "\1\u01dc",
      "\1\u01dd",
      "\1\u01de",
      "\1\u01df",
      "\1\u01e0",
      "\1\u01e1",
      "",
      "\1\u01e2",
      "\1\u01e3",
      "\1\u01e4",
      "",
      "\1\u01e5",
      "\1\u01e6",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01e9",
      "",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "",
      "\1\u01eb",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "",
      "\1\u01ee",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01f0",
      "",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\1\u01f2",
      "\1\u01f3",
      "\1\u01f4",
      "\1\u01f5",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01f8",
      "\1\u01f9",
      "\1\u01fa",
      "",
      "\1\u01fb",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u01fd",
      "",
      "",
      "\1\u01fe",
      "\1\u01ff",
      "\1\u0200",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0203",
      "\1\u0204",
      "\1\u0205",
      "\1\u0206",
      "\1\u0207",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0209",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u020d",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u020f",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0212",
      "\1\u0213",
      "\1\u0214",
      "\1\u0215",
      "\1\u0216",
      "\1\u0217",
      "\1\u0218",
      "\1\u0219",
      "",
      "",
      "\1\u021a",
      "",
      "\1\u021b",
      "",
      "",
      "\1\u021c",
      "",
      "\1\u021d",
      "",
      "\1\u021e",
      "\1\u021f",
      "\1\u0220",
      "\1\u0221",
      "",
      "",
      "\1\u0222",
      "\1\u0223",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\1\u0226",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u022b",
      "\1\u022c",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u022e",
      "",
      "\1\u022f",
      "",
      "",
      "",
      "\1\u0230",
      "",
      "\1\u0231",
      "",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0233",
      "\1\u0234",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0236",
      "\1\u0237",
      "\1\u0238",
      "\1\u0239",
      "\1\u023a",
      "\1\u023b",
      "\1\u023c",
      "\1\u023d",
      "\1\u023e",
      "\1\u023f",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0241",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "",
      "",
      "",
      "\1\u0245",
      "\1\u0247\3\uffff\1\u0246",
      "",
      "\1\u0248",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u024a",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\1\u024c",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\1\u024e",
      "\1\u024f",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0251",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0254",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0257",
      "",
      "\1\u0258",
      "",
      "",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u025b",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "\1\u0261",
      "",
      "",
      "\1\u0262",
      "",
      "",
      "\1\u0263",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "",
      "",
      "",
      "",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u0267",
      "\1\u0268",
      "",
      "",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "\1\u026a",
      "",
      "\12\51\7\uffff\32\51\4\uffff\1\51\1\uffff\32\51\105\uffff\27\51\1\uffff" +
        "\37\51\1\uffff\10\51",
      "")

    private val DFA18_eot = DFA.unpackEncodedString(DFA18_eotS)
    private val DFA18_eof = DFA.unpackEncodedString(DFA18_eofS)
    private val DFA18_min = DFA.unpackEncodedStringToUnsignedChars(DFA18_minS)
    private val DFA18_max = DFA.unpackEncodedStringToUnsignedChars(DFA18_maxS)
    private val DFA18_accept = DFA.unpackEncodedString(DFA18_acceptS)
    private val DFA18_special = DFA.unpackEncodedString(DFA18_specialS)
    private val DFA18_transition = new Array[Array[Short]](DFA18_transitionS.length)
    for (i <- DFA18_transition.indices) {
      DFA18_transition(i) = DFA.unpackEncodedString(DFA18_transitionS(i))
    }

    this.recognizer = rec
    this.decisionNumber = 18
    this.eot = DFA18_eot
    this.eof = DFA18_eof
    this.min = DFA18_min
    this.max = DFA18_max
    this.accept = DFA18_accept
    this.special = DFA18_special
    this.transition = DFA18_transition

    val description = "1:1: Tokens : ( ABS | ACCESS | AFTER | ALIAS | ALL | AND | ARCHITECTURE | ARRAY | ASSERT | ATTRIBUTE | BEGIN | BLOCK | BODY | BUFFER | BUS | CASE | COMPONENT | CONFIGURATION | CONSTANT | DISCONNECT | DOWNTO | ELSE | ELSIF | END | ENTITY | EXIT | FILE | FOR | FUNCTION | GENERATE | GENERIC | GROUP | GUARDED | IF | IMPURE | IN | INERTIAL | INOUT | IS | LABEL | LIBRARY | LINKAGE | LITERAL | LOOP | MAP | MOD | NAND | NEW | NEXT | NOR | NOT | NULL | OF | ON | OPEN | OR | OTHERS | OUT | PACKAGE | PORT | POSTPONED | PROCEDURE | PROCESS | PROTECTED | PURE | RANGE | RECORD | REGISTER | REJECT | REM | REPORT | RETURN | ROL | ROR | SELECT | SEVERITY | SHARED | SIGNAL | SLA | SLL | SRA | SRL | SUBTYPE | THEN | TO | TRANSPORT | TYPE | UNAFFECTED | UNITS | UNTIL | USE | VARIABLE | WAIT | WHEN | WHILE | WITH | XNOR | XOR | NATURE | TERMINAL | QUANTITY | ACROSS | THROUGH | SPECTRUM | NOISE | SUBNATURE | LIMIT | REFERENCE | PROCEDURAL | DOUBLESTAR | AMS_ASSIGN | LEQ | GEQ | ARROW | NEQ | VAR_ASSIGN | BOX | DBLQUOTE | SEMICOLON | COMMA | AMPERSAND | LPAREN | RPAREN | LBRACKET | RBRACKET | COLON | MUL | DIV | PLUS | MINUS | LT | GT | EQ | BAR | DOT | BREAK | TOLERANCE | WS | NESTED_ML_COMMENT | LINECOMMENT | BASIC_IDENTIFIER | EXTENDED_IDENTIFIER | BASED_LITERAL | INTEGER_LITERAL | REAL_LITERAL | APOSTROPHE | STRING_LITERAL | BIT_STRING_LITERAL );"

  }

  private
  val dfa18 = new DFA18(this)
}