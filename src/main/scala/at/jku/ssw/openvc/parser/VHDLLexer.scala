// $ANTLR 3.3 Nov 30, 2010 12:45:30 C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g 2011-01-04 14:17:01

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
  val EOF = -1
  val T__181 = 181
  val T__182 = 182
  val T__183 = 183
  val T__184 = 184
  val ABS = 4
  val ACCESS = 5
  val AFTER = 6
  val ALIAS = 7
  val ALL = 8
  val AND = 9
  val ARCHITECTURE = 10
  val ARRAY = 11
  val ASSERT = 12
  val ATTRIBUTE = 13
  val BEGIN = 14
  val BLOCK = 15
  val BODY = 16
  val BUFFER = 17
  val BUS = 18
  val CASE = 19
  val COMPONENT = 20
  val CONFIGURATION = 21
  val CONSTANT = 22
  val DISCONNECT = 23
  val DOWNTO = 24
  val ELSE = 25
  val ELSIF = 26
  val END = 27
  val ENTITY = 28
  val EXIT = 29
  val FILE = 30
  val FOR = 31
  val FUNCTION = 32
  val GENERATE = 33
  val GENERIC = 34
  val GROUP = 35
  val GUARDED = 36
  val IF = 37
  val IMPURE = 38
  val IN = 39
  val INERTIAL = 40
  val INOUT = 41
  val IS = 42
  val LABEL = 43
  val LIBRARY = 44
  val LINKAGE = 45
  val LITERAL = 46
  val LOOP = 47
  val MAP = 48
  val MOD = 49
  val NAND = 50
  val NEW = 51
  val NEXT = 52
  val NOR = 53
  val NOT = 54
  val NULL = 55
  val OF = 56
  val ON = 57
  val OPEN = 58
  val OR = 59
  val OTHERS = 60
  val OUT = 61
  val PACKAGE = 62
  val PORT = 63
  val POSTPONED = 64
  val PROCEDURE = 65
  val PROCESS = 66
  val PROTECTED = 67
  val PURE = 68
  val RANGE = 69
  val RECORD = 70
  val REGISTER = 71
  val REJECT = 72
  val REM = 73
  val REPORT = 74
  val RETURN = 75
  val ROL = 76
  val ROR = 77
  val SELECT = 78
  val SEVERITY = 79
  val SHARED = 80
  val SIGNAL = 81
  val SLA = 82
  val SLL = 83
  val SRA = 84
  val SRL = 85
  val SUBTYPE = 86
  val THEN = 87
  val TO = 88
  val TRANSPORT = 89
  val TYPE = 90
  val UNAFFECTED = 91
  val UNITS = 92
  val UNTIL = 93
  val USE = 94
  val VARIABLE = 95
  val WAIT = 96
  val WHEN = 97
  val WHILE = 98
  val WITH = 99
  val XNOR = 100
  val XOR = 101
  val DOUBLESTAR = 102
  val AMS_ASSIGN = 103
  val LEQ = 104
  val GEQ = 105
  val ARROW = 106
  val NEQ = 107
  val VAR_ASSIGN = 108
  val BOX = 109
  val DBLQUOTE = 110
  val SEMICOLON = 111
  val COMMA = 112
  val AMPERSAND = 113
  val LPAREN = 114
  val RPAREN = 115
  val LBRACKET = 116
  val RBRACKET = 117
  val COLON = 118
  val MUL = 119
  val DIV = 120
  val PLUS = 121
  val MINUS = 122
  val LT = 123
  val GT = 124
  val EQ = 125
  val BAR = 126
  val DOT = 127
  val MEQ = 128
  val MNEQ = 129
  val MLT = 130
  val MLEQ = 131
  val MGT = 132
  val MGEQ = 133
  val AT = 134
  val QMARK = 135
  val CONDITION_OPERATOR = 136
  val CONTEXT = 137
  val STRING_LITERAL = 138
  val PARAMETER = 139
  val NATURE = 140
  val TERMINAL = 141
  val QUANTITY = 142
  val TOLERANCE = 143
  val ACROSS = 144
  val THROUGH = 145
  val SPECTRUM = 146
  val NOISE = 147
  val CHARACTER_LITERAL = 148
  val SUBNATURE = 149
  val LIMIT = 150
  val REFERENCE = 151
  val BREAK = 152
  val PROCEDURAL = 153
  val RELEASE = 154
  val FORCE = 155
  val DEFAULT = 156
  val APOSTROPHE = 157
  val REAL_LITERAL = 158
  val INTEGER_LITERAL = 159
  val BASED_LITERAL = 160
  val BIT_STRING_LITERAL = 161
  val BASIC_IDENTIFIER = 162
  val EXTENDED_IDENTIFIER = 163
  val GRAPHIC_CHARACTER = 164
  val WS = 165
  val NESTED_ML_COMMENT = 166
  val LINECOMMENT = 167
  val LETTER = 168
  val LETTER_OR_DIGIT = 169
  val INTEGER = 170
  val BASED_INTEGER = 171
  val EXPONENT = 172
  val BASE_SPECIFIER = 173
  val EXTENDED_DIGIT = 174
  val DIGIT = 175
  val UPPER_CASE_LETTER = 176
  val LOWER_CASE_LETTER = 177
  val SPECIAL_CHARACTER = 178
  val SPACE_CHARACTER = 179
  val OTHER_SPECIAL_CHARACTER = 180
}

final class VHDLLexer(input: CharStream, state: RecognizerSharedState, ams: Boolean, vhdl2008: Boolean) extends Lexer(input, state) {

  import VHDLLexer._
  import org.antlr.runtime.BaseRecognizer._

  type Buffer[A] = scala.collection.immutable.VectorBuilder[A]
  //scala.collection.mutable.ListBuffer[A]

  private val lexerErrorList = new Buffer[CompilerMessage]()

  def lexerErrors: Seq[CompilerMessage] = this.lexerErrorList.result

  override def displayRecognitionError(tokenNames: Array[String], e: RecognitionException) =
    lexerErrorList += new CompilerMessage(position = Position(e.line, e.charPositionInLine), message = super.getErrorMessage(e, tokenNames))


  // delegates
  // delegators

  def this(input: CharStream, ams: Boolean, vhdl2008: Boolean) = {
    this (input, new RecognizerSharedState(), ams, vhdl2008)

  }

  val grammarFileName = "C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g"

  // $ANTLR start "ABS"
  @throws(classOf[RecognitionException])
  def mABS(): Unit = {
    try {
      var _type = ABS
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:42:5: ( 'abs' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:42:7: 'abs'
      `match`("abs");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:43:8: ( 'access' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:43:10: 'access'
      `match`("access");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:44:7: ( 'after' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:44:9: 'after'
      `match`("after");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:45:7: ( 'alias' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:45:9: 'alias'
      `match`("alias");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:46:5: ( 'all' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:46:7: 'all'
      `match`("all");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:47:5: ( 'and' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:47:7: 'and'
      `match`("and");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:48:14: ( 'architecture' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:48:16: 'architecture'
      `match`("architecture");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:49:7: ( 'array' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:49:9: 'array'
      `match`("array");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:50:8: ( 'assert' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:50:10: 'assert'
      `match`("assert");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:51:11: ( 'attribute' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:51:13: 'attribute'
      `match`("attribute");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:52:7: ( 'begin' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:52:9: 'begin'
      `match`("begin");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:53:7: ( 'block' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:53:9: 'block'
      `match`("block");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:54:6: ( 'body' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:54:8: 'body'
      `match`("body");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:55:8: ( 'buffer' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:55:10: 'buffer'
      `match`("buffer");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:56:5: ( 'bus' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:56:7: 'bus'
      `match`("bus");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:57:6: ( 'case' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:57:8: 'case'
      `match`("case");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:58:11: ( 'component' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:58:13: 'component'
      `match`("component");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:59:15: ( 'configuration' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:59:17: 'configuration'
      `match`("configuration");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:60:10: ( 'constant' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:60:12: 'constant'
      `match`("constant");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:61:12: ( 'disconnect' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:61:14: 'disconnect'
      `match`("disconnect");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:62:8: ( 'downto' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:62:10: 'downto'
      `match`("downto");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:63:6: ( 'else' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:63:8: 'else'
      `match`("else");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:64:7: ( 'elsif' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:64:9: 'elsif'
      `match`("elsif");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:65:5: ( 'end' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:65:7: 'end'
      `match`("end");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:66:8: ( 'entity' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:66:10: 'entity'
      `match`("entity");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:67:6: ( 'exit' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:67:8: 'exit'
      `match`("exit");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:68:6: ( 'file' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:68:8: 'file'
      `match`("file");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:69:5: ( 'for' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:69:7: 'for'
      `match`("for");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:70:10: ( 'function' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:70:12: 'function'
      `match`("function");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:71:10: ( 'generate' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:71:12: 'generate'
      `match`("generate");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:72:9: ( 'generic' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:72:11: 'generic'
      `match`("generic");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:73:7: ( 'group' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:73:9: 'group'
      `match`("group");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:74:9: ( 'guarded' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:74:11: 'guarded'
      `match`("guarded");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:75:4: ( 'if' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:75:6: 'if'
      `match`("if");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:76:8: ( 'impure' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:76:10: 'impure'
      `match`("impure");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:77:4: ( 'in' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:77:6: 'in'
      `match`("in");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:78:10: ( 'inertial' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:78:12: 'inertial'
      `match`("inertial");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:79:7: ( 'inout' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:79:9: 'inout'
      `match`("inout");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:80:4: ( 'is' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:80:6: 'is'
      `match`("is");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:81:7: ( 'label' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:81:9: 'label'
      `match`("label");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:82:9: ( 'library' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:82:11: 'library'
      `match`("library");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:83:9: ( 'linkage' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:83:11: 'linkage'
      `match`("linkage");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:84:9: ( 'literal' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:84:11: 'literal'
      `match`("literal");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:85:6: ( 'loop' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:85:8: 'loop'
      `match`("loop");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:86:5: ( 'map' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:86:7: 'map'
      `match`("map");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:87:5: ( 'mod' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:87:7: 'mod'
      `match`("mod");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:88:6: ( 'nand' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:88:8: 'nand'
      `match`("nand");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:89:5: ( 'new' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:89:7: 'new'
      `match`("new");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:90:6: ( 'next' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:90:8: 'next'
      `match`("next");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:91:5: ( 'nor' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:91:7: 'nor'
      `match`("nor");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:92:5: ( 'not' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:92:7: 'not'
      `match`("not");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:93:6: ( 'null' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:93:8: 'null'
      `match`("null");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:94:4: ( 'of' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:94:6: 'of'
      `match`("of");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:95:4: ( 'on' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:95:6: 'on'
      `match`("on");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:96:6: ( 'open' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:96:8: 'open'
      `match`("open");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:97:4: ( 'or' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:97:6: 'or'
      `match`("or");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:98:8: ( 'others' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:98:10: 'others'
      `match`("others");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:99:5: ( 'out' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:99:7: 'out'
      `match`("out");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:100:9: ( 'package' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:100:11: 'package'
      `match`("package");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:101:6: ( 'port' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:101:8: 'port'
      `match`("port");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:102:11: ( 'postponed' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:102:13: 'postponed'
      `match`("postponed");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:103:11: ( 'procedure' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:103:13: 'procedure'
      `match`("procedure");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:104:9: ( 'process' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:104:11: 'process'
      `match`("process");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:105:11: ( 'protected' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:105:13: 'protected'
      `match`("protected");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:106:6: ( 'pure' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:106:8: 'pure'
      `match`("pure");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:107:7: ( 'range' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:107:9: 'range'
      `match`("range");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:108:8: ( 'record' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:108:10: 'record'
      `match`("record");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:109:10: ( 'register' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:109:12: 'register'
      `match`("register");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:110:8: ( 'reject' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:110:10: 'reject'
      `match`("reject");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:111:5: ( 'rem' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:111:7: 'rem'
      `match`("rem");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:112:8: ( 'report' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:112:10: 'report'
      `match`("report");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:113:8: ( 'return' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:113:10: 'return'
      `match`("return");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:114:5: ( 'rol' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:114:7: 'rol'
      `match`("rol");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:115:5: ( 'ror' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:115:7: 'ror'
      `match`("ror");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:116:8: ( 'select' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:116:10: 'select'
      `match`("select");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:117:10: ( 'severity' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:117:12: 'severity'
      `match`("severity");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:118:8: ( 'shared' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:118:10: 'shared'
      `match`("shared");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:119:8: ( 'signal' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:119:10: 'signal'
      `match`("signal");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:120:5: ( 'sla' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:120:7: 'sla'
      `match`("sla");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:121:5: ( 'sll' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:121:7: 'sll'
      `match`("sll");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:122:5: ( 'sra' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:122:7: 'sra'
      `match`("sra");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:123:5: ( 'srl' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:123:7: 'srl'
      `match`("srl");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:124:9: ( 'subtype' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:124:11: 'subtype'
      `match`("subtype");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:125:6: ( 'then' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:125:8: 'then'
      `match`("then");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:126:4: ( 'to' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:126:6: 'to'
      `match`("to");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:127:11: ( 'transport' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:127:13: 'transport'
      `match`("transport");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:128:6: ( 'type' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:128:8: 'type'
      `match`("type");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:129:12: ( 'unaffected' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:129:14: 'unaffected'
      `match`("unaffected");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:130:7: ( 'units' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:130:9: 'units'
      `match`("units");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:131:7: ( 'until' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:131:9: 'until'
      `match`("until");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:132:5: ( 'use' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:132:7: 'use'
      `match`("use");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:133:10: ( 'variable' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:133:12: 'variable'
      `match`("variable");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:134:6: ( 'wait' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:134:8: 'wait'
      `match`("wait");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:135:6: ( 'when' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:135:8: 'when'
      `match`("when");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:136:7: ( 'while' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:136:9: 'while'
      `match`("while");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:137:6: ( 'with' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:137:8: 'with'
      `match`("with");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:138:6: ( 'xnor' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:138:8: 'xnor'
      `match`("xnor");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:139:5: ( 'xor' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:139:7: 'xor'
      `match`("xor");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "XOR"

  // $ANTLR start "DOUBLESTAR"
  @throws(classOf[RecognitionException])
  def mDOUBLESTAR(): Unit = {
    try {
      var _type = DOUBLESTAR
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:140:12: ( '**' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:140:14: '**'
      `match`("**");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:141:12: ( '==' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:141:14: '=='
      `match`("==");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:142:5: ( '<=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:142:7: '<='
      `match`("<=");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:143:5: ( '>=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:143:7: '>='
      `match`(">=");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:144:7: ( '=>' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:144:9: '=>'
      `match`("=>");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:145:5: ( '/=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:145:7: '/='
      `match`("/=");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:146:12: ( ':=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:146:14: ':='
      `match`(":=");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:147:5: ( '<>' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:147:7: '<>'
      `match`("<>");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:148:10: ( '\\\"' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:148:12: '\\\"'
      `match`('\"');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:149:11: ( ';' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:149:13: ';'
      `match`(';');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:150:7: ( ',' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:150:9: ','
      `match`(',');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:151:11: ( '&' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:151:13: '&'
      `match`('&');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:152:8: ( '(' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:152:10: '('
      `match`('(');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:153:8: ( ')' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:153:10: ')'
      `match`(')');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:154:10: ( '[' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:154:12: '['
      `match`('[');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:155:10: ( ']' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:155:12: ']'
      `match`(']');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:156:7: ( ':' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:156:9: ':'
      `match`(':');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:157:5: ( '*' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:157:7: '*'
      `match`('*');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:158:5: ( '/' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:158:7: '/'
      `match`('/');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:159:6: ( '+' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:159:8: '+'
      `match`('+');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:160:7: ( '-' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:160:9: '-'
      `match`('-');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:161:4: ( '<' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:161:6: '<'
      `match`('<');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:162:4: ( '>' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:162:6: '>'
      `match`('>');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:163:4: ( '=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:163:6: '='
      `match`('=');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:164:5: ( '|' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:164:7: '|'
      `match`('|');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:165:5: ( '.' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:165:7: '.'
      `match`('.');



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "DOT"

  // $ANTLR start "MEQ"
  @throws(classOf[RecognitionException])
  def mMEQ(): Unit = {
    try {
      var _type = MEQ
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:166:5: ( '?=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:166:7: '?='
      `match`("?=");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "MEQ"

  // $ANTLR start "MNEQ"
  @throws(classOf[RecognitionException])
  def mMNEQ(): Unit = {
    try {
      var _type = MNEQ
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:167:6: ( '?/=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:167:8: '?/='
      `match`("?/=");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "MNEQ"

  // $ANTLR start "MLT"
  @throws(classOf[RecognitionException])
  def mMLT(): Unit = {
    try {
      var _type = MLT
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:168:5: ( '?<' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:168:7: '?<'
      `match`("?<");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "MLT"

  // $ANTLR start "MLEQ"
  @throws(classOf[RecognitionException])
  def mMLEQ(): Unit = {
    try {
      var _type = MLEQ
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:169:6: ( '?<=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:169:8: '?<='
      `match`("?<=");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "MLEQ"

  // $ANTLR start "MGT"
  @throws(classOf[RecognitionException])
  def mMGT(): Unit = {
    try {
      var _type = MGT
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:170:5: ( '?>' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:170:7: '?>'
      `match`("?>");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "MGT"

  // $ANTLR start "MGEQ"
  @throws(classOf[RecognitionException])
  def mMGEQ(): Unit = {
    try {
      var _type = MGEQ
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:171:6: ( '?>=' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:171:8: '?>='
      `match`("?>=");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "MGEQ"

  // $ANTLR start "AT"
  @throws(classOf[RecognitionException])
  def mAT(): Unit = {
    try {
      var _type = AT
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:172:4: ( '@' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:172:6: '@'
      `match`('@');



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "AT"

  // $ANTLR start "QMARK"
  @throws(classOf[RecognitionException])
  def mQMARK(): Unit = {
    try {
      var _type = QMARK
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:173:7: ( '?' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:173:9: '?'
      `match`('?');



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "QMARK"

  // $ANTLR start "CONDITION_OPERATOR"
  @throws(classOf[RecognitionException])
  def mCONDITION_OPERATOR(): Unit = {
    try {
      var _type = CONDITION_OPERATOR
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:174:20: ( '??' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:174:22: '??'
      `match`("??");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "CONDITION_OPERATOR"

  // $ANTLR start "T__181"
  @throws(classOf[RecognitionException])
  def mT__181(): Unit = {
    try {
      var _type = T__181
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:175:8: ( '<<' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:175:10: '<<'
      `match`("<<");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "T__181"

  // $ANTLR start "T__182"
  @throws(classOf[RecognitionException])
  def mT__182(): Unit = {
    try {
      var _type = T__182
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:176:8: ( '>>' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:176:10: '>>'
      `match`(">>");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "T__182"

  // $ANTLR start "T__183"
  @throws(classOf[RecognitionException])
  def mT__183(): Unit = {
    try {
      var _type = T__183
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:177:8: ( '^' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:177:10: '^'
      `match`('^');



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "T__183"

  // $ANTLR start "T__184"
  @throws(classOf[RecognitionException])
  def mT__184(): Unit = {
    try {
      var _type = T__184
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:178:8: ( '\\'' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:178:10: '\\''
      `match`('\'');



      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "T__184"

  // $ANTLR start "CONTEXT"
  @throws(classOf[RecognitionException])
  def mCONTEXT(): Unit = {
    try {
      var _type = CONTEXT
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1921:9: ({...}? => 'context' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1921:11: {...}? => 'context'
      if (!((vhdl2008))) {
        throw new FailedPredicateException(input, "CONTEXT", "vhdl2008")
      }
      `match`("context");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "CONTEXT"

  // $ANTLR start "FORCE"
  @throws(classOf[RecognitionException])
  def mFORCE(): Unit = {
    try {
      var _type = FORCE
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1923:7: ({...}? => 'force' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1923:9: {...}? => 'force'
      if (!((vhdl2008))) {
        throw new FailedPredicateException(input, "FORCE", "vhdl2008")
      }
      `match`("force");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "FORCE"

  // $ANTLR start "PARAMETER"
  @throws(classOf[RecognitionException])
  def mPARAMETER(): Unit = {
    try {
      var _type = PARAMETER
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1925:11: ({...}? => 'parameter' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1925:13: {...}? => 'parameter'
      if (!((vhdl2008))) {
        throw new FailedPredicateException(input, "PARAMETER", "vhdl2008")
      }
      `match`("parameter");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "PARAMETER"

  // $ANTLR start "RELEASE"
  @throws(classOf[RecognitionException])
  def mRELEASE(): Unit = {
    try {
      var _type = RELEASE
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1927:9: ({...}? => 'release' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1927:11: {...}? => 'release'
      if (!((vhdl2008))) {
        throw new FailedPredicateException(input, "RELEASE", "vhdl2008")
      }
      `match`("release");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "RELEASE"

  // $ANTLR start "DEFAULT"
  @throws(classOf[RecognitionException])
  def mDEFAULT(): Unit = {
    try {
      var _type = DEFAULT
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1929:9: ({...}? => 'default' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1929:11: {...}? => 'default'
      if (!((vhdl2008))) {
        throw new FailedPredicateException(input, "DEFAULT", "vhdl2008")
      }
      `match`("default");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "DEFAULT"

  // $ANTLR start "NATURE"
  @throws(classOf[RecognitionException])
  def mNATURE(): Unit = {
    try {
      var _type = NATURE
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1932:8: ({...}? => 'nature' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1932:10: {...}? => 'nature'
      if (!((ams))) {
        throw new FailedPredicateException(input, "NATURE", "ams")
      }
      `match`("nature");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1934:10: ({...}? => 'terminal' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1934:12: {...}? => 'terminal'
      if (!((ams))) {
        throw new FailedPredicateException(input, "TERMINAL", "ams")
      }
      `match`("terminal");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1936:10: ({...}? => 'quantity' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1936:12: {...}? => 'quantity'
      if (!((ams))) {
        throw new FailedPredicateException(input, "QUANTITY", "ams")
      }
      `match`("quantity");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "QUANTITY"

  // $ANTLR start "TOLERANCE"
  @throws(classOf[RecognitionException])
  def mTOLERANCE(): Unit = {
    try {
      var _type = TOLERANCE
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1938:11: ({...}? => 'tolerance' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1938:13: {...}? => 'tolerance'
      if (!((ams))) {
        throw new FailedPredicateException(input, "TOLERANCE", "ams")
      }
      `match`("tolerance");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "TOLERANCE"

  // $ANTLR start "ACROSS"
  @throws(classOf[RecognitionException])
  def mACROSS(): Unit = {
    try {
      var _type = ACROSS
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1940:8: ({...}? => 'across' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1940:10: {...}? => 'across'
      if (!((ams))) {
        throw new FailedPredicateException(input, "ACROSS", "ams")
      }
      `match`("across");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1942:9: ({...}? => 'through' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1942:11: {...}? => 'through'
      if (!((ams))) {
        throw new FailedPredicateException(input, "THROUGH", "ams")
      }
      `match`("through");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1944:10: ({...}? => 'spectrum' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1944:12: {...}? => 'spectrum'
      if (!((ams))) {
        throw new FailedPredicateException(input, "SPECTRUM", "ams")
      }
      `match`("spectrum");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1946:7: ({...}? => 'noise' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1946:9: {...}? => 'noise'
      if (!((ams))) {
        throw new FailedPredicateException(input, "NOISE", "ams")
      }
      `match`("noise");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1948:11: ({...}? => 'subnature' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1948:13: {...}? => 'subnature'
      if (!((ams))) {
        throw new FailedPredicateException(input, "SUBNATURE", "ams")
      }
      `match`("subnature");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1950:7: ({...}? => 'limit' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1950:9: {...}? => 'limit'
      if (!((ams))) {
        throw new FailedPredicateException(input, "LIMIT", "ams")
      }
      `match`("limit");




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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1952:11: ({...}? => 'reference' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1952:13: {...}? => 'reference'
      if (!((ams))) {
        throw new FailedPredicateException(input, "REFERENCE", "ams")
      }
      `match`("reference");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "REFERENCE"

  // $ANTLR start "BREAK"
  @throws(classOf[RecognitionException])
  def mBREAK(): Unit = {
    try {
      var _type = BREAK
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1954:7: ({...}? => 'break' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1954:9: {...}? => 'break'
      if (!((ams))) {
        throw new FailedPredicateException(input, "BREAK", "ams")
      }
      `match`("break");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "BREAK"

  // $ANTLR start "PROCEDURAL"
  @throws(classOf[RecognitionException])
  def mPROCEDURAL(): Unit = {
    try {
      var _type = PROCEDURAL
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1956:12: ({...}? => 'procedural' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1956:14: {...}? => 'procedural'
      if (!((ams))) {
        throw new FailedPredicateException(input, "PROCEDURAL", "ams")
      }
      `match`("procedural");




      state.`type` = _type
      state.channel = _channel
    }
    finally {
    }
  }

  // $ANTLR end "PROCEDURAL"

  // $ANTLR start "WS"
  @throws(classOf[RecognitionException])
  def mWS(): Unit = {
    try {
      var _type = WS
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1959:4: ( ( '\\t' | ' ' | '\\r' | '\\n' )+ )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1959:6: ( '\\t' | ' ' | '\\r' | '\\n' )+
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1959:6: ( '\\t' | ' ' | '\\r' | '\\n' )+
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

            }
            else {
              val mse = new MismatchedSetException(null, input)
              recover(mse)
              throw mse
            }


          case _ =>
            if (cnt1 >= 1) loop1 = false
            else {
              val eee = new EarlyExitException(1, input)
              throw eee
            }
        }
        cnt1 += 1
      } while (loop1);

      skip()



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1962:19: ( '/*' ( options {greedy=false; } : NESTED_ML_COMMENT | . )* '*/' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1963:2: '/*' ( options {greedy=false; } : NESTED_ML_COMMENT | . )* '*/'
      `match`("/*");

      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1963:7: ( options {greedy=false; } : NESTED_ML_COMMENT | . )*
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
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1963:34: NESTED_ML_COMMENT
            mNESTED_ML_COMMENT();

          case 2 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1963:54: .
            matchAny();


          case _ => loop2 = false
        }
      } while (loop2);

      `match`("*/");

      skip()



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1966:13: ( '--' (~ ( '\\r' | '\\n' | '\\u000C' ) )* )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1966:15: '--' (~ ( '\\r' | '\\n' | '\\u000C' ) )*
      `match`("--");

      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1966:20: (~ ( '\\r' | '\\n' | '\\u000C' ) )*
      var loop3 = true
      do {
        var alt3 = 2
        val LA3_0 = input.LA(1)

        if (((LA3_0 >= '\u0000' && LA3_0 <= '\t') || LA3_0 == '\u000B' || (LA3_0 >= '\u000E' && LA3_0 <= '\uFFFF'))) {
          alt3 = 1
        }


        alt3 match {
          case 1 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1966:20: ~ ( '\\r' | '\\n' | '\\u000C' )
            if ((input.LA(1) >= '\u0000' && input.LA(1) <= '\t') || input.LA(1) == '\u000B' || (input.LA(1) >= '\u000E' && input.LA(1) <= '\uFFFF')) {
              input.consume()

            }
            else {
              val mse = new MismatchedSetException(null, input)
              recover(mse)
              throw mse
            }


          case _ => loop3 = false
        }
      } while (loop3);

      skip()



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1968:18: ( LETTER ( LETTER_OR_DIGIT | '_' )* )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1968:20: LETTER ( LETTER_OR_DIGIT | '_' )*
      mLETTER();
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1968:27: ( LETTER_OR_DIGIT | '_' )*
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

            }
            else {
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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:21: ( '\\\\' ( '\\\"' | '\\\\\\\\' | GRAPHIC_CHARACTER )+ '\\\\' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:23: '\\\\' ( '\\\"' | '\\\\\\\\' | GRAPHIC_CHARACTER )+ '\\\\'
      `match`('\\');
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:28: ( '\\\"' | '\\\\\\\\' | GRAPHIC_CHARACTER )+
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
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:30: '\\\"'
            `match`('\"');

          case 2 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:37: '\\\\\\\\'
            `match`("\\\\");


          case 3 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1971:46: GRAPHIC_CHARACTER
            mGRAPHIC_CHARACTER();


          case _ =>
            if (cnt5 >= 1) loop5 = false
            else {
              val eee = new EarlyExitException(5, input)
              throw eee
            }
        }
        cnt5 += 1
      } while (loop5);

      `match`('\\');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1973:15: ( INTEGER '#' BASED_INTEGER ( DOT BASED_INTEGER )? '#' ( EXPONENT )? )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1973:17: INTEGER '#' BASED_INTEGER ( DOT BASED_INTEGER )? '#' ( EXPONENT )?
      mINTEGER();
      `match`('#');
      mBASED_INTEGER();
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1973:43: ( DOT BASED_INTEGER )?
      var alt6 = 2
      val LA6_0 = input.LA(1)

      if ((LA6_0 == '.')) {
        alt6 = 1
      }
      alt6 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1973:45: DOT BASED_INTEGER
          mDOT();
          mBASED_INTEGER();


        case _ =>
      }

      `match`('#');
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1973:70: ( EXPONENT )?
      var alt7 = 2
      val LA7_0 = input.LA(1)

      if ((LA7_0 == 'e')) {
        alt7 = 1
      }
      alt7 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1973:70: EXPONENT
          mEXPONENT();


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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1975:17: ( INTEGER ( EXPONENT )? )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1975:19: INTEGER ( EXPONENT )?
      mINTEGER();
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1975:27: ( EXPONENT )?
      var alt8 = 2
      val LA8_0 = input.LA(1)

      if ((LA8_0 == 'e')) {
        alt8 = 1
      }
      alt8 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1975:27: EXPONENT
          mEXPONENT();


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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1977:14: ( INTEGER DOT INTEGER ( EXPONENT )? )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1977:16: INTEGER DOT INTEGER ( EXPONENT )?
      mINTEGER();
      mDOT();
      mINTEGER();
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1977:38: ( EXPONENT )?
      var alt9 = 2
      val LA9_0 = input.LA(1)

      if ((LA9_0 == 'e')) {
        alt9 = 1
      }
      alt9 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1977:38: EXPONENT
          mEXPONENT();


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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1982:12: ( '\\'' ({...}? => ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )? )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1983:2: '\\'' ({...}? => ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )?
      `match`('\'');
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1983:7: ({...}? => ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )?
      var alt10 = 2
      val LA10_0 = input.LA(1)

      if (((LA10_0 >= ' ' && LA10_0 <= '~') || (LA10_0 >= '\u00A0' && LA10_0 <= '\u00FF')) && ((input.LA(2) == '\'' && (input.LA(4) != '\'' || input.LA(3) == ',' || input.LA(3) == '|')))) {
        alt10 = 1
      }
      alt10 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1983:8: {...}? => ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\''
          if (!((input.LA(2) == '\'' && (input.LA(4) != '\'' || input.LA(3) == ',' || input.LA(3) == '|')))) {
            throw new FailedPredicateException(input, "APOSTROPHE", "input.LA(2)=='\\'' && (input.LA(4)!='\\'' || input.LA(3)==',' || input.LA(3)=='|')")
          }
          if ((input.LA(1) >= ' ' && input.LA(1) <= '~') || (input.LA(1) >= '\u00A0' && input.LA(1) <= '\u00FF')) {
            input.consume()

          }
          else {
            val mse = new MismatchedSetException(null, input)
            recover(mse)
            throw mse
          }

          `match`('\'');
          _type = CHARACTER_LITERAL;


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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1986:16: ( '\\\"' ( '\\\"\\\"' | '\\\\' | GRAPHIC_CHARACTER )* '\\\"' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1986:18: '\\\"' ( '\\\"\\\"' | '\\\\' | GRAPHIC_CHARACTER )* '\\\"'
      `match`('\"');
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1986:23: ( '\\\"\\\"' | '\\\\' | GRAPHIC_CHARACTER )*
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
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1986:25: '\\\"\\\"'
            `match`("\"\"");


          case 2 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1986:34: '\\\\'
            `match`('\\');

          case 3 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1986:41: GRAPHIC_CHARACTER
            mGRAPHIC_CHARACTER();


          case _ => loop11 = false
        }
      } while (loop11);

      `match`('\"');



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
      val _channel = DEFAULT_TOKEN_CHANNEL
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1988:20: ( ({...}? => INTEGER )? BASE_SPECIFIER '\\\"' ( BASED_INTEGER )? '\\\"' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1988:22: ({...}? => INTEGER )? BASE_SPECIFIER '\\\"' ( BASED_INTEGER )? '\\\"'
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1988:22: ({...}? => INTEGER )?
      var alt12 = 2
      val LA12_0 = input.LA(1)

      if (((LA12_0 >= '0' && LA12_0 <= '9')) && ((vhdl2008))) {
        alt12 = 1
      }
      alt12 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1988:23: {...}? => INTEGER
          if (!((vhdl2008))) {
            throw new FailedPredicateException(input, "BIT_STRING_LITERAL", "vhdl2008")
          }
          mINTEGER();


        case _ =>
      }

      mBASE_SPECIFIER();
      `match`('\"');
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1988:66: ( BASED_INTEGER )?
      var alt13 = 2
      val LA13_0 = input.LA(1)

      if (((LA13_0 >= '0' && LA13_0 <= '9') || (LA13_0 >= 'A' && LA13_0 <= 'Z') || (LA13_0 >= 'a' && LA13_0 <= 'z') || (LA13_0 >= '\u00C0' && LA13_0 <= '\u00D6') || (LA13_0 >= '\u00D8' && LA13_0 <= '\u00F6') || (LA13_0 >= '\u00F8' && LA13_0 <= '\u00FF'))) {
        alt13 = 1
      }
      alt13 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1988:66: BASED_INTEGER
          mBASED_INTEGER();


        case _ =>
      }

      `match`('\"');



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
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:16: ( 'b' | 'o' | 'x' | {...}? => ( 'ub' | 'uo' | 'ux ' | 'sb' | 'so' | 'sx ' | 'd' ) )
      var alt15 = 4
      val LA15_0 = input.LA(1)

      if ((LA15_0 == 'b')) {
        alt15 = 1
      }
      else if ((LA15_0 == 'o')) {
        alt15 = 2
      }
      else if ((LA15_0 == 'x')) {
        alt15 = 3
      }
      else if ((LA15_0 == 'd' || LA15_0 == 's' || LA15_0 == 'u') && ((vhdl2008))) {
        alt15 = 4
      }
      else {
        val nvae = new NoViableAltException("", 15, 0, input)

        throw nvae
      }
      alt15 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:18: 'b'
          `match`('b');

        case 2 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:24: 'o'
          `match`('o');

        case 3 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:30: 'x'
          `match`('x');

        case 4 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:36: {...}? => ( 'ub' | 'uo' | 'ux ' | 'sb' | 'so' | 'sx ' | 'd' )
          if (!((vhdl2008))) {
            throw new FailedPredicateException(input, "BASE_SPECIFIER", "vhdl2008")
          }
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:49: ( 'ub' | 'uo' | 'ux ' | 'sb' | 'so' | 'sx ' | 'd' )
          var alt14 = 7
          alt14 = dfa14.predict(input)
          alt14 match {
            case 1 =>
            // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:50: 'ub'
              `match`("ub");


            case 2 =>
            // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:57: 'uo'
              `match`("uo");


            case 3 =>
            // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:64: 'ux '
              `match`("ux ");


            case 4 =>
            // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:72: 'sb'
              `match`("sb");


            case 5 =>
            // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:79: 'so'
              `match`("so");


            case 6 =>
            // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:86: 'sx '
              `match`("sx ");


            case 7 =>
            // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1991:94: 'd'
              `match`('d');


            case _ =>
          }


        case _ =>
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
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1994:15: ( EXTENDED_DIGIT ( ( '_' )? EXTENDED_DIGIT )* )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1994:17: EXTENDED_DIGIT ( ( '_' )? EXTENDED_DIGIT )*
      mEXTENDED_DIGIT();
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1994:32: ( ( '_' )? EXTENDED_DIGIT )*
      var loop17 = true
      do {
        var alt17 = 2
        val LA17_0 = input.LA(1)

        if (((LA17_0 >= '0' && LA17_0 <= '9') || (LA17_0 >= 'A' && LA17_0 <= 'Z') || LA17_0 == '_' || (LA17_0 >= 'a' && LA17_0 <= 'z') || (LA17_0 >= '\u00C0' && LA17_0 <= '\u00D6') || (LA17_0 >= '\u00D8' && LA17_0 <= '\u00F6') || (LA17_0 >= '\u00F8' && LA17_0 <= '\u00FF'))) {
          alt17 = 1
        }


        alt17 match {
          case 1 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1994:34: ( '_' )? EXTENDED_DIGIT
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1994:34: ( '_' )?
            var alt16 = 2
            val LA16_0 = input.LA(1)

            if ((LA16_0 == '_')) {
              alt16 = 1
            }
            alt16 match {
              case 1 =>
              // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1994:34: '_'
                `match`('_');


              case _ =>
            }

            mEXTENDED_DIGIT();


          case _ => loop17 = false
        }
      } while (loop17);


    }
    finally {
    }
  }

  // $ANTLR end "BASED_INTEGER"

  // $ANTLR start "EXTENDED_DIGIT"
  @throws(classOf[RecognitionException])
  def mEXTENDED_DIGIT(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1997:16: ( DIGIT | LETTER )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || (input.LA(1) >= 'a' && input.LA(1) <= 'z') || (input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6') || (input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6') || (input.LA(1) >= '\u00F8' && input.LA(1) <= '\u00FF')) {
        input.consume()

      }
      else {
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
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2000:9: ( DIGIT ( ( '_' )? DIGIT )* )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2000:11: DIGIT ( ( '_' )? DIGIT )*
      mDIGIT();
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2000:17: ( ( '_' )? DIGIT )*
      var loop19 = true
      do {
        var alt19 = 2
        val LA19_0 = input.LA(1)

        if (((LA19_0 >= '0' && LA19_0 <= '9') || LA19_0 == '_')) {
          alt19 = 1
        }


        alt19 match {
          case 1 =>
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2000:19: ( '_' )? DIGIT
          // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2000:19: ( '_' )?
            var alt18 = 2
            val LA18_0 = input.LA(1)

            if ((LA18_0 == '_')) {
              alt18 = 1
            }
            alt18 match {
              case 1 =>
              // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2000:19: '_'
                `match`('_');


              case _ =>
            }

            mDIGIT();


          case _ => loop19 = false
        }
      } while (loop19);


    }
    finally {
    }
  }

  // $ANTLR end "INTEGER"

  // $ANTLR start "EXPONENT"
  @throws(classOf[RecognitionException])
  def mEXPONENT(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2003:10: ( 'e' ( '+' | '-' )? INTEGER )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2003:12: 'e' ( '+' | '-' )? INTEGER
      `match`('e');
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2003:16: ( '+' | '-' )?
      var alt20 = 2
      val LA20_0 = input.LA(1)

      if ((LA20_0 == '+' || LA20_0 == '-')) {
        alt20 = 1
      }
      alt20 match {
        case 1 =>
        // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
          if (input.LA(1) == '+' || input.LA(1) == '-') {
            input.consume()

          }
          else {
            val mse = new MismatchedSetException(null, input)
            recover(mse)
            throw mse
          }


        case _ =>
      }

      mINTEGER();


    }
    finally {
    }
  }

  // $ANTLR end "EXPONENT"

  // $ANTLR start "LETTER_OR_DIGIT"
  @throws(classOf[RecognitionException])
  def mLETTER_OR_DIGIT(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2006:17: ( LETTER | DIGIT )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= '0' && input.LA(1) <= '9') || (input.LA(1) >= 'A' && input.LA(1) <= 'Z') || (input.LA(1) >= 'a' && input.LA(1) <= 'z') || (input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6') || (input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6') || (input.LA(1) >= '\u00F8' && input.LA(1) <= '\u00FF')) {
        input.consume()

      }
      else {
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
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2009:8: ( UPPER_CASE_LETTER | LOWER_CASE_LETTER )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z') || (input.LA(1) >= 'a' && input.LA(1) <= 'z') || (input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6') || (input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00F6') || (input.LA(1) >= '\u00F8' && input.LA(1) <= '\u00FF')) {
        input.consume()

      }
      else {
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
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2012:19: ( UPPER_CASE_LETTER | DIGIT | SPECIAL_CHARACTER | SPACE_CHARACTER | LOWER_CASE_LETTER | OTHER_SPECIAL_CHARACTER )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= ' ' && input.LA(1) <= '!') || (input.LA(1) >= '#' && input.LA(1) <= '[') || (input.LA(1) >= ']' && input.LA(1) <= '~') || (input.LA(1) >= '\u00A0' && input.LA(1) <= '\u00FF')) {
        input.consume()

      }
      else {
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
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2022:19: ( 'A' .. 'Z' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00de' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= 'A' && input.LA(1) <= 'Z') || (input.LA(1) >= '\u00C0' && input.LA(1) <= '\u00D6') || (input.LA(1) >= '\u00D8' && input.LA(1) <= '\u00DE')) {
        input.consume()

      }
      else {
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
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2026:19: ( 'a' .. 'z' | '\\u00df' .. '\\u00f6' | '\\u00f8' .. '\\u00ff' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if ((input.LA(1) >= 'a' && input.LA(1) <= 'z') || (input.LA(1) >= '\u00DF' && input.LA(1) <= '\u00F6') || (input.LA(1) >= '\u00F8' && input.LA(1) <= '\u00FF')) {
        input.consume()

      }
      else {
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
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2029:7: ( '0' .. '9' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2029:9: '0' .. '9'
      matchRange('0', '9');


    }
    finally {
    }
  }

  // $ANTLR end "DIGIT"

  // $ANTLR start "SPECIAL_CHARACTER"
  @throws(classOf[RecognitionException])
  def mSPECIAL_CHARACTER(): Unit = {
    try {
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2032:19: ( '#' | '&' | '\\'' | '(' | ')' | '*' | '+' | ',' | '-' | '.' | '/' | ':' | ';' | '<' | '=' | '>' | '[' | ']' | '_' | '|' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if (input.LA(1) == '#' || (input.LA(1) >= '&' && input.LA(1) <= '/') || (input.LA(1) >= ':' && input.LA(1) <= '>') || input.LA(1) == '[' || input.LA(1) == ']' || input.LA(1) == '_' || input.LA(1) == '|') {
        input.consume()

      }
      else {
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
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2037:17: ( ' ' | '\\u00a0' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if (input.LA(1) == ' ' || input.LA(1) == '\u00A0') {
        input.consume()

      }
      else {
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
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2040:25: ( '!' | '$' | '%' | '@' | '?' | '^' | '`' | '{' | '}' | '~' | '\\u00a1' .. '\\u00bf' | '\\u00d7' | '\\u00f7' )
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:
      if (input.LA(1) == '!' || (input.LA(1) >= '$' && input.LA(1) <= '%') || (input.LA(1) >= '?' && input.LA(1) <= '@') || input.LA(1) == '^' || input.LA(1) == '`' || input.LA(1) == '{' || (input.LA(1) >= '}' && input.LA(1) <= '~') || (input.LA(1) >= '\u00A1' && input.LA(1) <= '\u00BF') || input.LA(1) == '\u00D7' || input.LA(1) == '\u00F7') {
        input.consume()

      }
      else {
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
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2045:19: ()
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:2045:21:


    }
    finally {
    }
  }

  // $ANTLR end "CHARACTER_LITERAL"

  @throws(classOf[RecognitionException])
  def mTokens(): Unit = {
    // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:8: ( ABS | ACCESS | AFTER | ALIAS | ALL | AND | ARCHITECTURE | ARRAY | ASSERT | ATTRIBUTE | BEGIN | BLOCK | BODY | BUFFER | BUS | CASE | COMPONENT | CONFIGURATION | CONSTANT | DISCONNECT | DOWNTO | ELSE | ELSIF | END | ENTITY | EXIT | FILE | FOR | FUNCTION | GENERATE | GENERIC | GROUP | GUARDED | IF | IMPURE | IN | INERTIAL | INOUT | IS | LABEL | LIBRARY | LINKAGE | LITERAL | LOOP | MAP | MOD | NAND | NEW | NEXT | NOR | NOT | NULL | OF | ON | OPEN | OR | OTHERS | OUT | PACKAGE | PORT | POSTPONED | PROCEDURE | PROCESS | PROTECTED | PURE | RANGE | RECORD | REGISTER | REJECT | REM | REPORT | RETURN | ROL | ROR | SELECT | SEVERITY | SHARED | SIGNAL | SLA | SLL | SRA | SRL | SUBTYPE | THEN | TO | TRANSPORT | TYPE | UNAFFECTED | UNITS | UNTIL | USE | VARIABLE | WAIT | WHEN | WHILE | WITH | XNOR | XOR | DOUBLESTAR | AMS_ASSIGN | LEQ | GEQ | ARROW | NEQ | VAR_ASSIGN | BOX | DBLQUOTE | SEMICOLON | COMMA | AMPERSAND | LPAREN | RPAREN | LBRACKET | RBRACKET | COLON | MUL | DIV | PLUS | MINUS | LT | GT | EQ | BAR | DOT | MEQ | MNEQ | MLT | MLEQ | MGT | MGEQ | AT | QMARK | CONDITION_OPERATOR | T__181 | T__182 | T__183 | T__184 | CONTEXT | FORCE | PARAMETER | RELEASE | DEFAULT | NATURE | TERMINAL | QUANTITY | TOLERANCE | ACROSS | THROUGH | SPECTRUM | NOISE | SUBNATURE | LIMIT | REFERENCE | BREAK | PROCEDURAL | WS | NESTED_ML_COMMENT | LINECOMMENT | BASIC_IDENTIFIER | EXTENDED_IDENTIFIER | BASED_LITERAL | INTEGER_LITERAL | REAL_LITERAL | APOSTROPHE | STRING_LITERAL | BIT_STRING_LITERAL )
    var alt21 = 166
    alt21 = dfa21.predict(input)
    alt21 match {
      case 1 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:10: ABS
        mABS();

      case 2 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:14: ACCESS
        mACCESS();

      case 3 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:21: AFTER
        mAFTER();

      case 4 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:27: ALIAS
        mALIAS();

      case 5 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:33: ALL
        mALL();

      case 6 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:37: AND
        mAND();

      case 7 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:41: ARCHITECTURE
        mARCHITECTURE();

      case 8 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:54: ARRAY
        mARRAY();

      case 9 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:60: ASSERT
        mASSERT();

      case 10 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:67: ATTRIBUTE
        mATTRIBUTE();

      case 11 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:77: BEGIN
        mBEGIN();

      case 12 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:83: BLOCK
        mBLOCK();

      case 13 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:89: BODY
        mBODY();

      case 14 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:94: BUFFER
        mBUFFER();

      case 15 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:101: BUS
        mBUS();

      case 16 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:105: CASE
        mCASE();

      case 17 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:110: COMPONENT
        mCOMPONENT();

      case 18 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:120: CONFIGURATION
        mCONFIGURATION();

      case 19 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:134: CONSTANT
        mCONSTANT();

      case 20 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:143: DISCONNECT
        mDISCONNECT();

      case 21 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:154: DOWNTO
        mDOWNTO();

      case 22 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:161: ELSE
        mELSE();

      case 23 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:166: ELSIF
        mELSIF();

      case 24 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:172: END
        mEND();

      case 25 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:176: ENTITY
        mENTITY();

      case 26 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:183: EXIT
        mEXIT();

      case 27 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:188: FILE
        mFILE();

      case 28 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:193: FOR
        mFOR();

      case 29 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:197: FUNCTION
        mFUNCTION();

      case 30 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:206: GENERATE
        mGENERATE();

      case 31 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:215: GENERIC
        mGENERIC();

      case 32 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:223: GROUP
        mGROUP();

      case 33 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:229: GUARDED
        mGUARDED();

      case 34 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:237: IF
        mIF();

      case 35 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:240: IMPURE
        mIMPURE();

      case 36 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:247: IN
        mIN();

      case 37 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:250: INERTIAL
        mINERTIAL();

      case 38 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:259: INOUT
        mINOUT();

      case 39 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:265: IS
        mIS();

      case 40 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:268: LABEL
        mLABEL();

      case 41 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:274: LIBRARY
        mLIBRARY();

      case 42 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:282: LINKAGE
        mLINKAGE();

      case 43 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:290: LITERAL
        mLITERAL();

      case 44 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:298: LOOP
        mLOOP();

      case 45 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:303: MAP
        mMAP();

      case 46 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:307: MOD
        mMOD();

      case 47 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:311: NAND
        mNAND();

      case 48 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:316: NEW
        mNEW();

      case 49 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:320: NEXT
        mNEXT();

      case 50 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:325: NOR
        mNOR();

      case 51 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:329: NOT
        mNOT();

      case 52 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:333: NULL
        mNULL();

      case 53 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:338: OF
        mOF();

      case 54 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:341: ON
        mON();

      case 55 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:344: OPEN
        mOPEN();

      case 56 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:349: OR
        mOR();

      case 57 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:352: OTHERS
        mOTHERS();

      case 58 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:359: OUT
        mOUT();

      case 59 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:363: PACKAGE
        mPACKAGE();

      case 60 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:371: PORT
        mPORT();

      case 61 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:376: POSTPONED
        mPOSTPONED();

      case 62 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:386: PROCEDURE
        mPROCEDURE();

      case 63 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:396: PROCESS
        mPROCESS();

      case 64 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:404: PROTECTED
        mPROTECTED();

      case 65 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:414: PURE
        mPURE();

      case 66 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:419: RANGE
        mRANGE();

      case 67 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:425: RECORD
        mRECORD();

      case 68 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:432: REGISTER
        mREGISTER();

      case 69 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:441: REJECT
        mREJECT();

      case 70 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:448: REM
        mREM();

      case 71 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:452: REPORT
        mREPORT();

      case 72 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:459: RETURN
        mRETURN();

      case 73 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:466: ROL
        mROL();

      case 74 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:470: ROR
        mROR();

      case 75 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:474: SELECT
        mSELECT();

      case 76 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:481: SEVERITY
        mSEVERITY();

      case 77 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:490: SHARED
        mSHARED();

      case 78 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:497: SIGNAL
        mSIGNAL();

      case 79 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:504: SLA
        mSLA();

      case 80 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:508: SLL
        mSLL();

      case 81 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:512: SRA
        mSRA();

      case 82 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:516: SRL
        mSRL();

      case 83 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:520: SUBTYPE
        mSUBTYPE();

      case 84 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:528: THEN
        mTHEN();

      case 85 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:533: TO
        mTO();

      case 86 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:536: TRANSPORT
        mTRANSPORT();

      case 87 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:546: TYPE
        mTYPE();

      case 88 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:551: UNAFFECTED
        mUNAFFECTED();

      case 89 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:562: UNITS
        mUNITS();

      case 90 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:568: UNTIL
        mUNTIL();

      case 91 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:574: USE
        mUSE();

      case 92 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:578: VARIABLE
        mVARIABLE();

      case 93 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:587: WAIT
        mWAIT();

      case 94 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:592: WHEN
        mWHEN();

      case 95 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:597: WHILE
        mWHILE();

      case 96 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:603: WITH
        mWITH();

      case 97 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:608: XNOR
        mXNOR();

      case 98 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:613: XOR
        mXOR();

      case 99 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:617: DOUBLESTAR
        mDOUBLESTAR();

      case 100 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:628: AMS_ASSIGN
        mAMS_ASSIGN();

      case 101 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:639: LEQ
        mLEQ();

      case 102 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:643: GEQ
        mGEQ();

      case 103 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:647: ARROW
        mARROW();

      case 104 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:653: NEQ
        mNEQ();

      case 105 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:657: VAR_ASSIGN
        mVAR_ASSIGN();

      case 106 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:668: BOX
        mBOX();

      case 107 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:672: DBLQUOTE
        mDBLQUOTE();

      case 108 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:681: SEMICOLON
        mSEMICOLON();

      case 109 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:691: COMMA
        mCOMMA();

      case 110 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:697: AMPERSAND
        mAMPERSAND();

      case 111 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:707: LPAREN
        mLPAREN();

      case 112 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:714: RPAREN
        mRPAREN();

      case 113 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:721: LBRACKET
        mLBRACKET();

      case 114 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:730: RBRACKET
        mRBRACKET();

      case 115 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:739: COLON
        mCOLON();

      case 116 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:745: MUL
        mMUL();

      case 117 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:749: DIV
        mDIV();

      case 118 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:753: PLUS
        mPLUS();

      case 119 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:758: MINUS
        mMINUS();

      case 120 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:764: LT
        mLT();

      case 121 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:767: GT
        mGT();

      case 122 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:770: EQ
        mEQ();

      case 123 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:773: BAR
        mBAR();

      case 124 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:777: DOT
        mDOT();

      case 125 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:781: MEQ
        mMEQ();

      case 126 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:785: MNEQ
        mMNEQ();

      case 127 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:790: MLT
        mMLT();

      case 128 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:794: MLEQ
        mMLEQ();

      case 129 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:799: MGT
        mMGT();

      case 130 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:803: MGEQ
        mMGEQ();

      case 131 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:808: AT
        mAT();

      case 132 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:811: QMARK
        mQMARK();

      case 133 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:817: CONDITION_OPERATOR
        mCONDITION_OPERATOR();

      case 134 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:836: T__181
        mT__181();

      case 135 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:843: T__182
        mT__182();

      case 136 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:850: T__183
        mT__183();

      case 137 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:857: T__184
        mT__184();

      case 138 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:864: CONTEXT
        mCONTEXT();

      case 139 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:872: FORCE
        mFORCE();

      case 140 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:878: PARAMETER
        mPARAMETER();

      case 141 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:888: RELEASE
        mRELEASE();

      case 142 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:896: DEFAULT
        mDEFAULT();

      case 143 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:904: NATURE
        mNATURE();

      case 144 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:911: TERMINAL
        mTERMINAL();

      case 145 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:920: QUANTITY
        mQUANTITY();

      case 146 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:929: TOLERANCE
        mTOLERANCE();

      case 147 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:939: ACROSS
        mACROSS();

      case 148 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:946: THROUGH
        mTHROUGH();

      case 149 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:954: SPECTRUM
        mSPECTRUM();

      case 150 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:963: NOISE
        mNOISE();

      case 151 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:969: SUBNATURE
        mSUBNATURE();

      case 152 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:979: LIMIT
        mLIMIT();

      case 153 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:985: REFERENCE
        mREFERENCE();

      case 154 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:995: BREAK
        mBREAK();

      case 155 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1001: PROCEDURAL
        mPROCEDURAL();

      case 156 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1012: WS
        mWS();

      case 157 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1015: NESTED_ML_COMMENT
        mNESTED_ML_COMMENT();

      case 158 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1033: LINECOMMENT
        mLINECOMMENT();

      case 159 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1045: BASIC_IDENTIFIER
        mBASIC_IDENTIFIER();

      case 160 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1062: EXTENDED_IDENTIFIER
        mEXTENDED_IDENTIFIER();

      case 161 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1082: BASED_LITERAL
        mBASED_LITERAL();

      case 162 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1096: INTEGER_LITERAL
        mINTEGER_LITERAL();

      case 163 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1112: REAL_LITERAL
        mREAL_LITERAL();

      case 164 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1125: APOSTROPHE
        mAPOSTROPHE();

      case 165 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1136: STRING_LITERAL
        mSTRING_LITERAL();

      case 166 =>
      // C:\\Users\\christian\\Desktop\\OpenVC\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\VHDL.g:1:1151: BIT_STRING_LITERAL
        mBIT_STRING_LITERAL();


      case _ =>
    }

  }

  final private class DFA14(rec: BaseRecognizer) extends DFA {
    private val DFA14_eotS = "\12\uffff"
    private val DFA14_eofS = "\12\uffff"
    private val DFA14_minS = "\1\144\2\142\7\uffff"
    private val DFA14_maxS = "\1\165\2\170\7\uffff"
    private val DFA14_acceptS = "\3\uffff\1\7\1\1\1\2\1\3\1\4\1\5\1\6"
    private val DFA14_specialS = "\12\uffff}>"
    private val DFA14_transitionS = Array("\1\3\16\uffff\1\2\1\uffff\1\1",
      "\1\4\14\uffff\1\5\10\uffff\1\6",
      "\1\7\14\uffff\1\10\10\uffff\1\11",
      "",
      "",
      "",
      "",
      "",
      "",
      "")

    private val DFA14_eot = DFA.unpackEncodedString(DFA14_eotS)
    private val DFA14_eof = DFA.unpackEncodedString(DFA14_eofS)
    private val DFA14_min = DFA.unpackEncodedStringToUnsignedChars(DFA14_minS)
    private val DFA14_max = DFA.unpackEncodedStringToUnsignedChars(DFA14_maxS)
    private val DFA14_accept = DFA.unpackEncodedString(DFA14_acceptS)
    private val DFA14_special = DFA.unpackEncodedString(DFA14_specialS)
    private val DFA14_transition = new Array[Array[Short]](DFA14_transitionS.length)
    for (i <- DFA14_transition.indices) {
      DFA14_transition(i) = DFA.unpackEncodedString(DFA14_transitionS(i))
    }

    this.recognizer = rec
    this.decisionNumber = 14
    this.eot = DFA14_eot
    this.eof = DFA14_eof
    this.min = DFA14_min
    this.max = DFA14_max
    this.accept = DFA14_accept
    this.special = DFA14_special
    this.transition = DFA14_transition

    val description = "1991:49: ( 'ub' | 'uo' | 'ux ' | 'sb' | 'so' | 'sx ' | 'd' )"

  }

  final private class DFA21(rec: BaseRecognizer) extends DFA {
    private val DFA21_eotS = "\1\uffff\24\55\1\u0082\1\u0085\1\u0089\1\u008c" +
      "\1\u008f\1\u0091\1\u0092\10\uffff\1\u0095\2\uffff\1\u009b\2\uffff\1" +
      "\u009c\1\55\3\uffff\1\u009f\15\55\1\uffff\5\55\1\uffff\11\55\1\u00c5" +
      "\1\55\1\u00c9\1\u00ca\11\55\1\u00db\1\u00dc\1\55\1\u00de\24\55\1\u00ff" +
      "\16\55\27\uffff\1\u010f\1\u0111\4\uffff\1\55\2\uffff\1\u009f\2\uffff" +
      "\1\u0113\4\55\1\u0118\1\u0119\10\55\1\u0122\10\55\1\u012e\3\55\1\u0133" +
      "\4\55\1\uffff\3\55\2\uffff\6\55\1\u0141\1\u0142\2\55\1\u0145\1\55\1" +
      "\u0147\1\u0148\2\55\2\uffff\1\55\1\uffff\1\55\1\u014d\12\55\1\u0159" +
      "\4\55\1\u015e\1\u015f\4\55\1\u0164\1\u0165\1\u0166\1\u0167\5\55\1\uffff" +
      "\6\55\1\u0174\6\55\1\u017b\4\uffff\1\55\1\uffff\4\55\2\uffff\6\55\1" +
      "\u0187\1\55\1\uffff\1\55\1\u018a\7\55\1\u0192\1\55\1\uffff\1\55\1\u0195" +
      "\1\u0196\1\55\1\uffff\14\55\1\u01a4\2\uffff\1\u01a5\1\55\1\uffff\1\u01a7" +
      "\2\uffff\1\55\1\u01a9\1\u01aa\1\55\1\uffff\2\55\1\u01ae\3\55\1\u01b2" +
      "\4\55\1\uffff\4\55\2\uffff\4\55\4\uffff\3\55\1\u01c2\3\55\1\u01c6\4" +
      "\55\1\uffff\1\55\1\u01cc\1\u01cd\1\55\1\u01cf\1\u01d0\1\uffff\3\55\1" +
      "\u01d4\1\u01d5\1\55\1\u01d7\2\55\1\u01da\1\u01db\1\uffff\1\55\1\u01dd" +
      "\1\uffff\7\55\1\uffff\1\u01e5\1\55\2\uffff\1\u01e7\2\55\1\u01eb\3\55" +
      "\1\u01ef\1\u01f0\3\55\1\u01f4\2\uffff\1\55\1\uffff\1\u01f6\2\uffff\3" +
      "\55\1\uffff\3\55\1\uffff\1\u01fe\16\55\1\uffff\3\55\1\uffff\2\55\1\u0212" +
      "\1\u0213\1\55\2\uffff\1\u0215\2\uffff\1\55\1\u0217\1\u0218\2\uffff\1" +
      "\55\1\uffff\1\u021a\1\55\2\uffff\1\u021c\1\uffff\5\55\1\u0223\1\55\1" +
      "\uffff\1\u0225\1\uffff\3\55\1\uffff\1\55\1\u022b\1\55\2\uffff\3\55\1" +
      "\uffff\1\u0231\1\uffff\1\u0233\6\55\1\uffff\1\u023a\1\55\1\u023c\1\u023d" +
      "\1\u023e\2\55\1\u0241\1\55\1\u0243\1\u0244\10\55\2\uffff\1\55\1\uffff" +
      "\1\55\2\uffff\1\55\1\uffff\1\55\2\uffff\3\55\1\u0255\1\55\1\uffff\1" +
      "\u0257\2\uffff\2\55\1\u025a\1\u025b\1\uffff\1\55\1\u025d\1\u025e\1\u025f" +
      "\4\uffff\1\u0261\3\55\1\u0265\1\55\1\uffff\1\55\3\uffff\1\u0268\1\55" +
      "\1\uffff\1\55\2\uffff\1\u026b\2\55\1\u026e\6\55\1\uffff\4\55\1\u0279" +
      "\1\uffff\1\55\1\uffff\1\u027d\1\u027e\2\uffff\1\u027f\5\uffff\3\55\1" +
      "\uffff\1\55\1\u0285\1\uffff\1\55\1\u0288\1\uffff\1\55\1\u028a\1\uffff" +
      "\2\55\1\u028e\1\55\1\u0290\1\u0291\1\55\1\u0293\1\u0294\1\55\2\uffff" +
      "\1\55\4\uffff\1\u0297\1\u0298\1\u0299\1\55\1\u029b\2\uffff\1\u029c\1" +
      "\uffff\1\u029d\2\uffff\1\u029f\1\u02a0\1\uffff\1\55\2\uffff\1\55\2\uffff" +
      "\1\55\1\u02a6\3\uffff\1\u02a8\7\uffff\1\u02ac\1\uffff\2\55\7\uffff\1" +
      "\u02b0\1\55\2\uffff\1\u02b2\1\uffff"
    private val DFA21_eofS = "\u02b3\uffff"
    private val DFA21_minS = "\1\11\1\142\1\42\1\141\1\42\1\154\1\151\1" +
      "\145\1\146\3\141\1\42\2\141\1\142\1\145\1\142\2\141\1\42\1\52\1\75\1" +
      "\74\1\75\1\52\1\75\1\40\10\uffff\1\55\2\uffff\1\57\2\uffff\1\40\1\165" +
      "\3\uffff\1\43\1\163\1\143\1\164\1\151\1\144\1\143\1\163\1\164\1\147" +
      "\1\157\1\144\1\146\1\145\1\uffff\1\163\1\155\1\163\1\167\1\146\1\uffff" +
      "\1\163\1\144\1\151\1\154\1\162\2\156\1\157\1\141\1\60\1\160\2\60\2\142" +
      "\1\157\1\160\1\144\1\156\1\167\1\151\1\154\2\60\1\145\1\60\1\150\1\164" +
      "\1\143\1\162\1\157\1\162\1\156\1\143\2\154\1\141\1\147\2\141\1\142\1" +
      "\145\2\42\1\40\1\145\1\60\1\141\1\160\1\162\1\141\1\145\2\42\1\40\1" +
      "\162\1\151\1\145\1\164\1\157\1\162\27\uffff\2\75\4\uffff\1\141\1\uffff" +
      "\1\60\1\43\2\uffff\1\60\1\145\1\157\1\145\1\141\2\60\1\150\1\141\1\145" +
      "\1\162\1\151\1\143\1\171\1\146\1\60\1\141\1\145\1\160\1\146\1\143\1" +
      "\156\1\141\1\145\1\60\1\151\1\164\1\145\1\60\1\143\1\145\1\165\1\162" +
      "\1\uffff\1\165\1\162\1\165\2\uffff\1\145\1\162\1\153\1\145\1\151\1\160" +
      "\2\60\1\144\1\165\1\60\1\164\2\60\1\163\1\154\2\uffff\1\156\1\uffff" +
      "\1\145\1\60\1\153\1\141\2\164\1\143\1\145\1\147\1\157\1\151\1\145\1" +
      "\60\1\157\1\165\2\145\2\60\2\145\1\162\1\156\4\60\1\156\1\143\1\156" +
      "\1\157\1\145\1\uffff\1\156\1\145\1\155\1\146\1\164\1\151\1\60\1\151" +
      "\1\164\1\156\1\154\1\150\1\162\1\60\4\uffff\1\156\1\uffff\2\163\1\162" +
      "\1\163\2\uffff\1\151\1\171\1\162\1\151\1\156\1\153\1\60\1\145\1\uffff" +
      "\1\153\1\60\1\157\1\151\1\164\1\145\1\157\1\164\1\165\1\60\1\146\1\uffff" +
      "\1\164\2\60\1\145\1\uffff\1\164\1\162\1\160\1\144\1\162\2\164\1\154" +
      "\2\141\1\162\1\164\1\60\2\uffff\1\60\1\162\1\uffff\1\60\2\uffff\1\145" +
      "\2\60\1\162\1\uffff\1\141\1\155\1\60\1\160\2\145\1\60\1\145\1\162\1" +
      "\163\1\143\1\uffff\2\162\1\141\1\162\2\uffff\1\143\1\162\1\145\1\141" +
      "\4\uffff\1\171\1\141\1\164\1\60\1\165\1\162\1\163\1\60\1\151\1\146\1" +
      "\163\1\154\1\uffff\1\141\2\60\1\145\2\60\1\uffff\1\164\2\163\2\60\1" +
      "\164\1\60\1\164\1\142\2\60\1\uffff\1\162\1\60\1\uffff\1\156\1\147\1" +
      "\141\1\170\1\156\1\157\1\154\1\uffff\1\60\1\171\2\uffff\1\60\1\151\1" +
      "\141\1\60\2\145\1\151\2\60\1\162\1\147\1\141\1\60\2\uffff\1\145\1\uffff" +
      "\1\60\2\uffff\1\163\1\147\1\145\1\uffff\1\157\1\144\1\143\1\uffff\1" +
      "\60\1\144\3\164\1\156\1\163\1\145\1\164\1\151\1\144\1\154\1\160\1\164" +
      "\1\162\1\uffff\1\147\1\141\1\160\1\uffff\1\156\1\145\2\60\1\142\2\uffff" +
      "\1\60\2\uffff\1\151\2\60\2\uffff\1\145\1\uffff\1\60\1\165\2\uffff\1" +
      "\60\1\0\1\145\1\165\1\156\1\164\1\156\1\60\1\164\1\uffff\1\60\1\0\1" +
      "\157\1\164\1\143\1\uffff\1\144\1\60\1\141\2\uffff\1\171\1\145\1\154" +
      "\1\0\1\60\1\0\1\60\1\145\1\164\1\156\1\165\1\163\1\164\1\uffff\1\60" +
      "\1\145\3\60\1\145\1\156\1\60\1\164\2\60\1\145\2\165\1\150\1\156\1\157" +
      "\1\141\1\143\2\uffff\1\154\1\uffff\1\164\1\uffff\1\0\1\143\1\uffff\1" +
      "\164\2\uffff\1\156\1\162\1\164\1\60\1\145\1\uffff\1\60\2\uffff\1\156" +
      "\1\145\2\60\1\uffff\1\154\3\60\1\uffff\1\0\2\uffff\1\60\2\145\1\162" +
      "\1\60\1\145\1\uffff\1\162\3\uffff\1\60\1\143\1\uffff\1\171\2\uffff\1" +
      "\60\1\162\1\155\1\60\1\143\1\162\1\154\1\164\1\145\1\171\1\uffff\1\164" +
      "\1\145\1\164\1\141\1\60\1\0\1\143\1\0\2\60\2\uffff\1\60\5\uffff\1\162" +
      "\1\144\1\141\1\uffff\1\144\1\60\1\0\1\145\1\60\1\uffff\1\145\1\60\1" +
      "\0\1\145\1\164\1\60\1\145\2\60\1\165\2\60\1\164\2\uffff\1\164\4\uffff" +
      "\3\60\1\154\1\60\2\uffff\1\60\1\uffff\1\60\1\0\1\uffff\2\60\1\0\1\144" +
      "\1\uffff\1\0\1\162\2\uffff\1\151\1\60\1\0\2\uffff\1\60\1\uffff\2\0\1" +
      "\uffff\1\0\2\uffff\1\60\1\uffff\1\145\1\157\2\uffff\1\0\4\uffff\1\60" +
      "\1\156\2\uffff\1\60\1\uffff"
    private val DFA21_maxS = "\1\u00ff\1\164\1\165\2\157\1\170\2\165\1\163" +
      "\2\157\3\165\1\157\1\170\1\171\1\170\1\141\1\151\1\157\1\52\3\76\2\75" +
      "\1\u00ff\10\uffff\1\55\2\uffff\1\77\2\uffff\1\u00ff\1\165\3\uffff\1" +
      "\170\1\163\1\162\1\164\1\154\1\144\1\162\1\163\1\164\1\147\1\157\1\144" +
      "\1\163\1\145\1\uffff\1\163\1\156\1\163\1\167\1\146\1\uffff\1\163\1\164" +
      "\1\151\1\154\1\162\2\156\1\157\1\141\1\u00ff\1\160\2\u00ff\1\142\1\164" +
      "\1\157\1\160\1\144\1\164\1\170\1\164\1\154\2\u00ff\1\145\1\u00ff\1\150" +
      "\1\164\1\162\1\163\1\157\1\162\1\156\1\164\1\162\1\166\1\141\1\147\2" +
      "\154\1\142\1\145\2\42\1\40\1\162\1\u00ff\1\141\1\160\1\162\1\164\1\145" +
      "\2\42\1\40\1\162\2\151\1\164\1\157\1\162\27\uffff\2\75\4\uffff\1\141" +
      "\1\uffff\1\71\1\170\2\uffff\1\u00ff\1\145\1\157\1\145\1\141\2\u00ff" +
      "\1\150\1\141\1\145\1\162\1\151\1\143\1\171\1\146\1\u00ff\1\141\1\145" +
      "\1\160\1\164\1\143\1\156\1\141\1\151\1\u00ff\1\151\1\164\1\145\1\u00ff" +
      "\1\143\1\145\1\165\1\162\1\uffff\1\165\1\162\1\165\2\uffff\1\145\1\162" +
      "\1\153\1\145\1\151\1\160\2\u00ff\1\144\1\165\1\u00ff\1\164\2\u00ff\1" +
      "\163\1\154\2\uffff\1\156\1\uffff\1\145\1\u00ff\1\153\1\141\3\164\1\145" +
      "\1\147\1\157\1\151\1\145\1\u00ff\1\157\1\165\2\145\2\u00ff\2\145\1\162" +
      "\1\156\4\u00ff\1\164\1\143\1\156\1\157\1\145\1\uffff\1\156\1\145\1\155" +
      "\1\146\1\164\1\151\1\u00ff\1\151\1\164\1\156\1\154\1\150\1\162\1\u00ff" +
      "\4\uffff\1\156\1\uffff\2\163\1\162\1\163\2\uffff\1\151\1\171\1\162\1" +
      "\151\1\156\1\153\1\u00ff\1\145\1\uffff\1\153\1\u00ff\1\157\1\151\1\164" +
      "\1\145\1\157\1\164\1\165\1\u00ff\1\146\1\uffff\1\164\2\u00ff\1\145\1" +
      "\uffff\1\164\1\162\1\160\1\144\1\162\2\164\1\154\2\141\1\162\1\164\1" +
      "\u00ff\2\uffff\1\u00ff\1\162\1\uffff\1\u00ff\2\uffff\1\145\2\u00ff\1" +
      "\162\1\uffff\1\141\1\155\1\u00ff\1\160\2\145\1\u00ff\1\145\1\162\1\163" +
      "\1\143\1\uffff\2\162\1\141\1\162\2\uffff\1\143\1\162\1\145\1\141\4\uffff" +
      "\1\171\1\141\1\164\1\u00ff\1\165\1\162\1\163\1\u00ff\1\151\1\146\1\163" +
      "\1\154\1\uffff\1\141\2\u00ff\1\145\2\u00ff\1\uffff\1\164\2\163\2\u00ff" +
      "\1\164\1\u00ff\1\164\1\142\2\u00ff\1\uffff\1\162\1\u00ff\1\uffff\1\156" +
      "\1\147\1\141\1\170\1\156\1\157\1\154\1\uffff\1\u00ff\1\171\2\uffff\1" +
      "\u00ff\2\151\1\u00ff\2\145\1\151\2\u00ff\1\162\1\147\1\141\1\u00ff\2" +
      "\uffff\1\145\1\uffff\1\u00ff\2\uffff\1\163\1\147\1\145\1\uffff\1\157" +
      "\1\163\1\143\1\uffff\1\u00ff\1\144\3\164\1\156\1\163\1\145\1\164\1\151" +
      "\1\144\1\154\1\160\1\164\1\162\1\uffff\1\147\1\141\1\160\1\uffff\1\156" +
      "\1\145\2\u00ff\1\142\2\uffff\1\u00ff\2\uffff\1\151\2\u00ff\2\uffff\1" +
      "\145\1\uffff\1\u00ff\1\165\2\uffff\1\u00ff\1\0\1\145\1\165\1\156\1\164" +
      "\1\156\1\u00ff\1\164\1\uffff\1\u00ff\1\0\1\157\1\164\1\143\1\uffff\1" +
      "\144\1\u00ff\1\141\2\uffff\1\171\1\145\1\154\1\0\1\u00ff\1\0\1\u00ff" +
      "\1\145\1\164\1\156\1\165\1\163\1\164\1\uffff\1\u00ff\1\145\3\u00ff\1" +
      "\145\1\156\1\u00ff\1\164\2\u00ff\1\145\2\165\1\150\1\156\1\157\1\141" +
      "\1\143\2\uffff\1\154\1\uffff\1\164\1\uffff\1\0\1\143\1\uffff\1\164\2" +
      "\uffff\1\156\1\162\1\164\1\u00ff\1\145\1\uffff\1\u00ff\2\uffff\1\156" +
      "\1\145\2\u00ff\1\uffff\1\154\3\u00ff\1\uffff\1\0\2\uffff\1\u00ff\2\145" +
      "\1\162\1\u00ff\1\145\1\uffff\1\162\3\uffff\1\u00ff\1\143\1\uffff\1\171" +
      "\2\uffff\1\u00ff\1\162\1\155\1\u00ff\1\143\1\162\1\154\1\164\1\145\1" +
      "\171\1\uffff\1\164\1\145\1\164\1\141\1\u00ff\1\0\1\143\1\0\2\u00ff\2" +
      "\uffff\1\u00ff\5\uffff\1\162\1\144\1\145\1\uffff\1\144\1\u00ff\1\0\1" +
      "\145\1\u00ff\1\uffff\1\145\1\u00ff\1\0\1\145\1\164\1\u00ff\1\145\2\u00ff" +
      "\1\165\2\u00ff\1\164\2\uffff\1\164\4\uffff\3\u00ff\1\154\1\u00ff\2\uffff" +
      "\1\u00ff\1\uffff\1\u00ff\1\0\1\uffff\2\u00ff\1\0\1\144\1\uffff\1\0\1" +
      "\162\2\uffff\1\151\1\u00ff\1\0\2\uffff\1\u00ff\1\uffff\2\0\1\uffff\1" +
      "\0\2\uffff\1\u00ff\1\uffff\1\145\1\157\2\uffff\1\0\4\uffff\1\u00ff\1" +
      "\156\2\uffff\1\u00ff\1\uffff"
    private val DFA21_acceptS = "\34\uffff\1\154\1\155\1\156\1\157\1\160" +
      "\1\161\1\162\1\166\1\uffff\1\173\1\174\1\uffff\1\u0083\1\u0088\2\uffff" +
      "\1\u009c\1\u009f\1\u00a0\16\uffff\1\u00a6\5\uffff\1\u00a6\75\uffff\1" +
      "\143\1\164\1\144\1\147\1\172\1\145\1\152\1\u0086\1\170\1\146\1\u0087" +
      "\1\171\1\150\1\u009d\1\165\1\151\1\163\1\153\1\u00a5\1\u009e\1\167\1" +
      "\175\1\176\2\uffff\1\u0085\1\u0084\1\u0089\1\u00a4\1\uffff\1\u00a2\2" +
      "\uffff\1\u00a1\1\u00a3\41\uffff\1\42\3\uffff\1\44\1\47\20\uffff\1\65" +
      "\1\66\1\uffff\1\70\40\uffff\1\125\16\uffff\1\u0080\1\177\1\u0082\1\u0081" +
      "\1\uffff\1\1\4\uffff\1\5\1\6\10\uffff\1\17\13\uffff\1\30\4\uffff\1\34" +
      "\15\uffff\1\55\1\56\2\uffff\1\60\1\uffff\1\62\1\63\4\uffff\1\72\13\uffff" +
      "\1\106\4\uffff\1\111\1\112\4\uffff\1\117\1\120\1\121\1\122\14\uffff" +
      "\1\133\6\uffff\1\142\13\uffff\1\15\2\uffff\1\20\7\uffff\1\26\2\uffff" +
      "\1\32\1\33\15\uffff\1\54\1\57\1\uffff\1\61\1\uffff\1\64\1\67\3\uffff" +
      "\1\74\3\uffff\1\101\17\uffff\1\124\3\uffff\1\127\5\uffff\1\135\1\136" +
      "\1\uffff\1\140\1\141\3\uffff\1\3\1\4\1\uffff\1\10\2\uffff\1\13\1\14" +
      "\11\uffff\1\27\5\uffff\1\40\3\uffff\1\46\1\50\15\uffff\1\102\23\uffff" +
      "\1\131\1\132\1\uffff\1\137\1\uffff\1\2\2\uffff\1\11\1\uffff\1\16\1\u009a" +
      "\5\uffff\1\25\1\uffff\1\31\1\u008b\4\uffff\1\43\4\uffff\1\u0098\1\uffff" +
      "\1\u0096\1\71\6\uffff\1\103\1\uffff\1\105\1\107\1\110\2\uffff\1\113" +
      "\1\uffff\1\115\1\116\12\uffff\1\u0093\12\uffff\1\37\1\41\1\uffff\1\51" +
      "\1\52\1\53\1\u008f\1\73\3\uffff\1\77\5\uffff\1\123\15\uffff\1\23\1\u008a" +
      "\1\uffff\1\u008e\1\35\1\36\1\45\5\uffff\1\104\1\u008d\1\uffff\1\114" +
      "\2\uffff\1\u0094\4\uffff\1\134\2\uffff\1\12\1\21\3\uffff\1\75\1\76\1" +
      "\uffff\1\100\2\uffff\1\u0095\1\uffff\1\126\1\u0090\1\uffff\1\u0091\2" +
      "\uffff\1\24\1\u008c\1\uffff\1\u0099\1\u0097\1\u0092\1\130\2\uffff\1" +
      "\u009b\1\7\1\uffff\1\22"
    private val DFA21_specialS = "\4\uffff\1\0\52\uffff\1\2\76\uffff\1\27" +
      "\1\25\1\31\7\uffff\1\26\1\30\1\32\46\uffff\1\1\u013b\uffff\1\10\11\uffff" +
      "\1\21\14\uffff\1\6\1\uffff\1\14\41\uffff\1\11\30\uffff\1\15\43\uffff" +
      "\1\3\1\uffff\1\24\20\uffff\1\23\5\uffff\1\12\33\uffff\1\13\3\uffff\1" +
      "\16\2\uffff\1\17\5\uffff\1\22\4\uffff\1\7\1\5\1\uffff\1\20\10\uffff" +
      "\1\4\12\uffff}>"
    private val DFA21_transitionS = Array("\2\54\2\uffff\1\54\22\uffff\1" +
      "\54\1\uffff\1\33\3\uffff\1\36\1\52\1\37\1\40\1\25\1\43\1\35\1\44\1\46\1" +
      "\31\12\57\1\32\1\34\1\27\1\26\1\30\1\47\1\50\32\55\1\41\1\56\1\42\1\51\2" +
      "\uffff\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\55\1\10\2\55\1\11\1\12\1\13\1\14\1" +
      "\15\1\53\1\16\1\17\1\20\1\21\1\22\1\23\1\24\2\55\1\uffff\1\45\103\uffff" +
      "\27\55\1\uffff\37\55\1\uffff\10\55",
      "\1\60\1\61\2\uffff\1\62\5\uffff\1\63\1\uffff\1\64\3\uffff\1\65\1\66\1" +
        "\67",
      "\1\75\102\uffff\1\70\6\uffff\1\71\2\uffff\1\72\2\uffff\1\74\2\uffff\1" +
        "\73",
      "\1\76\15\uffff\1\77",
      "\1\103\102\uffff\1\102\3\uffff\1\100\5\uffff\1\101",
      "\1\104\1\uffff\1\105\11\uffff\1\106",
      "\1\107\5\uffff\1\110\5\uffff\1\111",
      "\1\112\14\uffff\1\113\2\uffff\1\114",
      "\1\115\6\uffff\1\116\1\117\4\uffff\1\120",
      "\1\121\7\uffff\1\122\5\uffff\1\123",
      "\1\124\15\uffff\1\125",
      "\1\126\3\uffff\1\127\11\uffff\1\130\5\uffff\1\131",
      "\1\75\103\uffff\1\132\7\uffff\1\133\1\uffff\1\134\1\uffff\1\135\1\uffff" +
        "\1\136\1\137",
      "\1\140\15\uffff\1\141\2\uffff\1\142\2\uffff\1\143",
      "\1\144\3\uffff\1\145\11\uffff\1\146",
      "\1\156\2\uffff\1\147\2\uffff\1\150\1\151\2\uffff\1\152\2\uffff\1\157\1" +
        "\155\1\uffff\1\153\2\uffff\1\154\2\uffff\1\160",
      "\1\165\2\uffff\1\161\6\uffff\1\162\2\uffff\1\163\6\uffff\1\164",
      "\1\170\13\uffff\1\166\1\171\3\uffff\1\167\4\uffff\1\172",
      "\1\173",
      "\1\174\6\uffff\1\175\1\176",
      "\1\75\113\uffff\1\177\1\u0080",
      "\1\u0081",
      "\1\u0083\1\u0084",
      "\1\u0088\1\u0086\1\u0087",
      "\1\u008a\1\u008b",
      "\1\u008e\22\uffff\1\u008d",
      "\1\u0090",
      "\137\u0093\41\uffff\140\u0093",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "",
      "\1\u0094",
      "",
      "",
      "\1\u0097\14\uffff\1\u0098\1\u0096\1\u0099\1\u009a",
      "",
      "",
      "\137\u009d\41\uffff\140\u009d",
      "\1\u009e",
      "",
      "",
      "",
      "\1\u00a2\12\uffff\1\u00a3\1\uffff\12\u00a1\45\uffff\1\u00a0\2\uffff\1" +
        "\103\1\uffff\1\103\12\uffff\1\103\3\uffff\1\103\1\uffff\1\103\2\uffff\1" +
        "\103",
      "\1\u00a4",
      "\1\u00a5\16\uffff\1\u00a6",
      "\1\u00a7",
      "\1\u00a8\2\uffff\1\u00a9",
      "\1\u00aa",
      "\1\u00ab\16\uffff\1\u00ac",
      "\1\u00ad",
      "\1\u00ae",
      "\1\u00af",
      "\1\u00b0",
      "\1\u00b1",
      "\1\u00b2\14\uffff\1\u00b3",
      "\1\u00b4",
      "",
      "\1\u00b5",
      "\1\u00b6\1\u00b7",
      "\1\u00b8",
      "\1\u00b9",
      "\1\u00ba",
      "",
      "\1\u00bb",
      "\1\u00bc\17\uffff\1\u00bd",
      "\1\u00be",
      "\1\u00bf",
      "\1\u00c0",
      "\1\u00c1",
      "\1\u00c2",
      "\1\u00c3",
      "\1\u00c4",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u00c6",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\4\55\1\u00c7\11\55\1\u00c8\13" +
        "\55\105\uffff\27\55\1\uffff\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u00cb",
      "\1\u00cc\12\uffff\1\u00cf\1\u00cd\5\uffff\1\u00ce",
      "\1\u00d0",
      "\1\u00d1",
      "\1\u00d2",
      "\1\u00d3\5\uffff\1\u00d4",
      "\1\u00d5\1\u00d6",
      "\1\u00d9\10\uffff\1\u00d7\1\uffff\1\u00d8",
      "\1\u00da",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u00dd",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u00df",
      "\1\u00e0",
      "\1\u00e1\16\uffff\1\u00e2",
      "\1\u00e3\1\u00e4",
      "\1\u00e5",
      "\1\u00e6",
      "\1\u00e7",
      "\1\u00e8\2\uffff\1\u00ef\1\u00e9\2\uffff\1\u00ea\1\uffff\1\u00ee\1\u00eb" +
        "\2\uffff\1\u00ec\3\uffff\1\u00ed",
      "\1\u00f0\5\uffff\1\u00f1",
      "\1\u00f2\11\uffff\1\u00f3",
      "\1\u00f4",
      "\1\u00f5",
      "\1\u00f6\12\uffff\1\u00f7",
      "\1\u00f8\12\uffff\1\u00f9",
      "\1\u00fa",
      "\1\u00fb",
      "\1\103",
      "\1\103",
      "\1\103",
      "\1\u00fc\14\uffff\1\u00fd",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\13\55\1\u00fe\16\55\105\uffff" +
        "\27\55\1\uffff\37\55\1\uffff\10\55",
      "\1\u0100",
      "\1\u0101",
      "\1\u0102",
      "\1\u0103\7\uffff\1\u0104\12\uffff\1\u0105",
      "\1\u0106",
      "\1\103",
      "\1\103",
      "\1\103",
      "\1\u0107",
      "\1\u0108",
      "\1\u0109\3\uffff\1\u010a",
      "\1\u010b",
      "\1\u010c",
      "\1\u010d",
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
      "",
      "",
      "",
      "\1\u010e",
      "\1\u0110",
      "",
      "",
      "",
      "",
      "\1\u0112",
      "",
      "\12\u00a1",
      "\1\u00a2\12\uffff\1\u00a3\1\uffff\12\u00a1\45\uffff\1\u00a0\2\uffff\1" +
        "\103\1\uffff\1\103\12\uffff\1\103\3\uffff\1\103\1\uffff\1\103\2\uffff\1" +
        "\103",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0114",
      "\1\u0115",
      "\1\u0116",
      "\1\u0117",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u011a",
      "\1\u011b",
      "\1\u011c",
      "\1\u011d",
      "\1\u011e",
      "\1\u011f",
      "\1\u0120",
      "\1\u0121",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0123",
      "\1\u0124",
      "\1\u0125",
      "\1\u0126\14\uffff\1\u0127\1\u0128",
      "\1\u0129",
      "\1\u012a",
      "\1\u012b",
      "\1\u012c\3\uffff\1\u012d",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u012f",
      "\1\u0130",
      "\1\u0131",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\2\55\1\u0132\27\55\105\uffff" +
        "\27\55\1\uffff\37\55\1\uffff\10\55",
      "\1\u0134",
      "\1\u0135",
      "\1\u0136",
      "\1\u0137",
      "",
      "\1\u0138",
      "\1\u0139",
      "\1\u013a",
      "",
      "",
      "\1\u013b",
      "\1\u013c",
      "\1\u013d",
      "\1\u013e",
      "\1\u013f",
      "\1\u0140",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0143",
      "\1\u0144",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0146",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0149",
      "\1\u014a",
      "",
      "",
      "\1\u014b",
      "",
      "\1\u014c",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u014e",
      "\1\u014f",
      "\1\u0150",
      "\1\u0151",
      "\1\u0152\20\uffff\1\u0153",
      "\1\u0154",
      "\1\u0155",
      "\1\u0156",
      "\1\u0157",
      "\1\u0158",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u015a",
      "\1\u015b",
      "\1\u015c",
      "\1\u015d",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0160",
      "\1\u0161",
      "\1\u0162",
      "\1\u0163",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0169\5\uffff\1\u0168",
      "\1\u016a",
      "\1\u016b",
      "\1\u016c",
      "\1\u016d",
      "",
      "\1\u016e",
      "\1\u016f",
      "\1\u0170",
      "\1\u0171",
      "\1\u0172",
      "\1\u0173",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0175",
      "\1\u0176",
      "\1\u0177",
      "\1\u0178",
      "\1\u0179",
      "\1\u017a",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "",
      "",
      "",
      "\1\u017c",
      "",
      "\1\u017d",
      "\1\u017e",
      "\1\u017f",
      "\1\u0180",
      "",
      "",
      "\1\u0181",
      "\1\u0182",
      "\1\u0183",
      "\1\u0184",
      "\1\u0185",
      "\1\u0186",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0188",
      "",
      "\1\u0189",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u018b",
      "\1\u018c",
      "\1\u018d",
      "\1\u018e",
      "\1\u018f",
      "\1\u0190",
      "\1\u0191",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0193",
      "",
      "\1\u0194",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0197",
      "",
      "\1\u0198",
      "\1\u0199",
      "\1\u019a",
      "\1\u019b",
      "\1\u019c",
      "\1\u019d",
      "\1\u019e",
      "\1\u019f",
      "\1\u01a0",
      "\1\u01a1",
      "\1\u01a2",
      "\1\u01a3",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01a6",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "",
      "\1\u01a8",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01ab",
      "",
      "\1\u01ac",
      "\1\u01ad",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01af",
      "\1\u01b0",
      "\1\u01b1",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01b3",
      "\1\u01b4",
      "\1\u01b5",
      "\1\u01b6",
      "",
      "\1\u01b7",
      "\1\u01b8",
      "\1\u01b9",
      "\1\u01ba",
      "",
      "",
      "\1\u01bb",
      "\1\u01bc",
      "\1\u01bd",
      "\1\u01be",
      "",
      "",
      "",
      "",
      "\1\u01bf",
      "\1\u01c0",
      "\1\u01c1",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01c3",
      "\1\u01c4",
      "\1\u01c5",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01c7",
      "\1\u01c8",
      "\1\u01c9",
      "\1\u01ca",
      "",
      "\1\u01cb",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01ce",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "\1\u01d1",
      "\1\u01d2",
      "\1\u01d3",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01d6",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01d8",
      "\1\u01d9",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "\1\u01dc",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "\1\u01de",
      "\1\u01df",
      "\1\u01e0",
      "\1\u01e1",
      "\1\u01e2",
      "\1\u01e3",
      "\1\u01e4",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01e6",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01e8",
      "\1\u01e9\7\uffff\1\u01ea",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01ec",
      "\1\u01ed",
      "\1\u01ee",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01f1",
      "\1\u01f2",
      "\1\u01f3",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "",
      "\1\u01f5",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "",
      "\1\u01f7",
      "\1\u01f8",
      "\1\u01f9",
      "",
      "\1\u01fa",
      "\1\u01fb\16\uffff\1\u01fc",
      "\1\u01fd",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u01ff",
      "\1\u0200",
      "\1\u0201",
      "\1\u0202",
      "\1\u0203",
      "\1\u0204",
      "\1\u0205",
      "\1\u0206",
      "\1\u0207",
      "\1\u0208",
      "\1\u0209",
      "\1\u020a",
      "\1\u020b",
      "\1\u020c",
      "",
      "\1\u020d",
      "\1\u020e",
      "\1\u020f",
      "",
      "\1\u0210",
      "\1\u0211",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0214",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "",
      "\1\u0216",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "",
      "\1\u0219",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u021b",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\uffff",
      "\1\u021e",
      "\1\u021f",
      "\1\u0220",
      "\1\u0221",
      "\1\u0222",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0224",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\uffff",
      "\1\u0227",
      "\1\u0228",
      "\1\u0229",
      "",
      "\1\u022a",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u022c",
      "",
      "",
      "\1\u022d",
      "\1\u022e",
      "\1\u022f",
      "\1\uffff",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\uffff",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0234",
      "\1\u0235",
      "\1\u0236",
      "\1\u0237",
      "\1\u0238",
      "\1\u0239",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u023b",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u023f",
      "\1\u0240",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0242",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0245",
      "\1\u0246",
      "\1\u0247",
      "\1\u0248",
      "\1\u0249",
      "\1\u024a",
      "\1\u024b",
      "\1\u024c",
      "",
      "",
      "\1\u024d",
      "",
      "\1\u024e",
      "",
      "\1\uffff",
      "\1\u0250",
      "",
      "\1\u0251",
      "",
      "",
      "\1\u0252",
      "\1\u0253",
      "\1\u0254",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0256",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "",
      "\1\u0258",
      "\1\u0259",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "\1\u025c",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "\1\uffff",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0262",
      "\1\u0263",
      "\1\u0264",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0266",
      "",
      "\1\u0267",
      "",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0269",
      "",
      "\1\u026a",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u026c",
      "\1\u026d",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u026f",
      "\1\u0270",
      "\1\u0271",
      "\1\u0272",
      "\1\u0273",
      "\1\u0274",
      "",
      "\1\u0275",
      "\1\u0276",
      "\1\u0277",
      "\1\u0278",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\uffff",
      "\1\u027b",
      "\1\uffff",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "",
      "",
      "",
      "",
      "\1\u0280",
      "\1\u0281",
      "\1\u0283\3\uffff\1\u0282",
      "",
      "\1\u0284",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\uffff",
      "\1\u0287",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "\1\u0289",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\uffff",
      "\1\u028c",
      "\1\u028d",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u028f",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0292",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u0295",
      "",
      "",
      "\1\u0296",
      "",
      "",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u029a",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\uffff",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\uffff",
      "\1\u02a2",
      "",
      "\1\uffff",
      "\1\u02a4",
      "",
      "",
      "\1\u02a5",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\uffff",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "\1\uffff",
      "\1\uffff",
      "",
      "\1\uffff",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "",
      "\1\u02ad",
      "\1\u02ae",
      "",
      "",
      "\1\uffff",
      "",
      "",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "\1\u02b1",
      "",
      "",
      "\12\55\7\uffff\32\55\4\uffff\1\55\1\uffff\32\55\105\uffff\27\55\1\uffff" +
        "\37\55\1\uffff\10\55",
      "")

    private val DFA21_eot = DFA.unpackEncodedString(DFA21_eotS)
    private val DFA21_eof = DFA.unpackEncodedString(DFA21_eofS)
    private val DFA21_min = DFA.unpackEncodedStringToUnsignedChars(DFA21_minS)
    private val DFA21_max = DFA.unpackEncodedStringToUnsignedChars(DFA21_maxS)
    private val DFA21_accept = DFA.unpackEncodedString(DFA21_acceptS)
    private val DFA21_special = DFA.unpackEncodedString(DFA21_specialS)
    private val DFA21_transition = new Array[Array[Short]](DFA21_transitionS.length)
    for (i <- DFA21_transition.indices) {
      DFA21_transition(i) = DFA.unpackEncodedString(DFA21_transitionS(i))
    }

    this.recognizer = rec
    this.decisionNumber = 21
    this.eot = DFA21_eot
    this.eof = DFA21_eof
    this.min = DFA21_min
    this.max = DFA21_max
    this.accept = DFA21_accept
    this.special = DFA21_special
    this.transition = DFA21_transition

    val description = "1:1: Tokens : ( ABS | ACCESS | AFTER | ALIAS | ALL | AND | ARCHITECTURE | ARRAY | ASSERT | ATTRIBUTE | BEGIN | BLOCK | BODY | BUFFER | BUS | CASE | COMPONENT | CONFIGURATION | CONSTANT | DISCONNECT | DOWNTO | ELSE | ELSIF | END | ENTITY | EXIT | FILE | FOR | FUNCTION | GENERATE | GENERIC | GROUP | GUARDED | IF | IMPURE | IN | INERTIAL | INOUT | IS | LABEL | LIBRARY | LINKAGE | LITERAL | LOOP | MAP | MOD | NAND | NEW | NEXT | NOR | NOT | NULL | OF | ON | OPEN | OR | OTHERS | OUT | PACKAGE | PORT | POSTPONED | PROCEDURE | PROCESS | PROTECTED | PURE | RANGE | RECORD | REGISTER | REJECT | REM | REPORT | RETURN | ROL | ROR | SELECT | SEVERITY | SHARED | SIGNAL | SLA | SLL | SRA | SRL | SUBTYPE | THEN | TO | TRANSPORT | TYPE | UNAFFECTED | UNITS | UNTIL | USE | VARIABLE | WAIT | WHEN | WHILE | WITH | XNOR | XOR | DOUBLESTAR | AMS_ASSIGN | LEQ | GEQ | ARROW | NEQ | VAR_ASSIGN | BOX | DBLQUOTE | SEMICOLON | COMMA | AMPERSAND | LPAREN | RPAREN | LBRACKET | RBRACKET | COLON | MUL | DIV | PLUS | MINUS | LT | GT | EQ | BAR | DOT | MEQ | MNEQ | MLT | MLEQ | MGT | MGEQ | AT | QMARK | CONDITION_OPERATOR | T__181 | T__182 | T__183 | T__184 | CONTEXT | FORCE | PARAMETER | RELEASE | DEFAULT | NATURE | TERMINAL | QUANTITY | TOLERANCE | ACROSS | THROUGH | SPECTRUM | NOISE | SUBNATURE | LIMIT | REFERENCE | BREAK | PROCEDURAL | WS | NESTED_ML_COMMENT | LINECOMMENT | BASIC_IDENTIFIER | EXTENDED_IDENTIFIER | BASED_LITERAL | INTEGER_LITERAL | REAL_LITERAL | APOSTROPHE | STRING_LITERAL | BIT_STRING_LITERAL );"

    @throws(classOf[NoViableAltException])
    override def specialStateTransition(__s: Int, _input: IntStream): Int = {
      var s = __s
      val input = _input
      val _s = __s
      s match {
        case 0 =>
          val LA21_4 = input.LA(1)


          val index21_4 = _input.index()
          _input.rewind()
          s = -1
          if ((LA21_4 == 'i')) {
            s = 64
          }

          else if ((LA21_4 == 'o')) {
            s = 65
          }

          else if ((LA21_4 == 'e')) {
            s = 66
          }

          else if ((LA21_4 == '\"') && ((vhdl2008))) {
            s = 67
          }

          else s = 45


          _input.seek(index21_4)
          if (s >= 0) return s
        case 1 =>
          val LA21_161 = input.LA(1)


          val index21_161 = _input.index()
          _input.rewind()
          s = -1
          if ((LA21_161 == '#')) {
            s = 162
          }

          else if ((LA21_161 == '_')) {
            s = 160
          }

          else if (((LA21_161 >= '0' && LA21_161 <= '9'))) {
            s = 161
          }

          else if ((LA21_161 == '.')) {
            s = 163
          }

          else if ((LA21_161 == 'b' || LA21_161 == 'd' || LA21_161 == 'o' || LA21_161 == 's' || LA21_161 == 'u' || LA21_161 == 'x') && ((vhdl2008))) {
            s = 67
          }

          else s = 159


          _input.seek(index21_161)
          if (s >= 0) return s
        case 2 =>
          val LA21_47 = input.LA(1)


          val index21_47 = _input.index()
          _input.rewind()
          s = -1
          if ((LA21_47 == '_')) {
            s = 160
          }

          else if (((LA21_47 >= '0' && LA21_47 <= '9'))) {
            s = 161
          }

          else if ((LA21_47 == '#')) {
            s = 162
          }

          else if ((LA21_47 == '.')) {
            s = 163
          }

          else if ((LA21_47 == 'b' || LA21_47 == 'd' || LA21_47 == 'o' || LA21_47 == 's' || LA21_47 == 'u' || LA21_47 == 'x') && ((vhdl2008))) {
            s = 67
          }

          else s = 159


          _input.seek(index21_47)
          if (s >= 0) return s
        case 3 =>
          val LA21_597 = input.LA(1)


          val index21_597 = _input.index()
          _input.rewind()
          s = -1
          if (((vhdl2008))) {
            s = 634
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_597)
          if (s >= 0) return s
        case 4 =>
          val LA21_680 = input.LA(1)


          val index21_680 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 687
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_680)
          if (s >= 0) return s
        case 5 =>
          val LA21_669 = input.LA(1)


          val index21_669 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 682
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_669)
          if (s >= 0) return s
        case 6 =>
          val LA21_500 = input.LA(1)


          val index21_500 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 560
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_500)
          if (s >= 0) return s
        case 7 =>
          val LA21_668 = input.LA(1)


          val index21_668 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 681
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_668)
          if (s >= 0) return s
        case 8 =>
          val LA21_477 = input.LA(1)


          val index21_477 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 541
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_477)
          if (s >= 0) return s
        case 9 =>
          val LA21_536 = input.LA(1)


          val index21_536 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 591
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_536)
          if (s >= 0) return s
        case 10 =>
          val LA21_622 = input.LA(1)


          val index21_622 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 651
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_622)
          if (s >= 0) return s
        case 11 =>
          val LA21_650 = input.LA(1)


          val index21_650 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 670
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_650)
          if (s >= 0) return s
        case 12 =>
          val LA21_502 = input.LA(1)


          val index21_502 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 562
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_502)
          if (s >= 0) return s
        case 13 =>
          val LA21_561 = input.LA(1)


          val index21_561 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 608
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_561)
          if (s >= 0) return s
        case 14 =>
          val LA21_654 = input.LA(1)


          val index21_654 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 673
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_654)
          if (s >= 0) return s
        case 15 =>
          val LA21_657 = input.LA(1)


          val index21_657 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 675
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_657)
          if (s >= 0) return s
        case 16 =>
          val LA21_671 = input.LA(1)


          val index21_671 = _input.index()
          _input.rewind()
          s = -1
          if (((ams))) {
            s = 683
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_671)
          if (s >= 0) return s
        case 17 =>
          val LA21_487 = input.LA(1)


          val index21_487 = _input.index()
          _input.rewind()
          s = -1
          if (((vhdl2008))) {
            s = 550
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_487)
          if (s >= 0) return s
        case 18 =>
          val LA21_663 = input.LA(1)


          val index21_663 = _input.index()
          _input.rewind()
          s = -1
          if (((vhdl2008))) {
            s = 679
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_663)
          if (s >= 0) return s
        case 19 =>
          val LA21_616 = input.LA(1)


          val index21_616 = _input.index()
          _input.rewind()
          s = -1
          if (((vhdl2008))) {
            s = 646
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_616)
          if (s >= 0) return s
        case 20 =>
          val LA21_599 = input.LA(1)


          val index21_599 = _input.index()
          _input.rewind()
          s = -1
          if (((vhdl2008))) {
            s = 636
          }

          else if ((true)) {
            s = 45
          }


          _input.seek(index21_599)
          if (s >= 0) return s
        case 21 =>
          val LA21_111 = input.LA(1)


          val index21_111 = _input.index()
          _input.rewind()
          s = -1
          if ((LA21_111 == '\"') && ((vhdl2008))) {
            s = 67
          }

          else s = 45


          _input.seek(index21_111)
          if (s >= 0) return s
        case 22 =>
          val LA21_120 = input.LA(1)


          val index21_120 = _input.index()
          _input.rewind()
          s = -1
          if ((LA21_120 == '\"') && ((vhdl2008))) {
            s = 67
          }

          else s = 45


          _input.seek(index21_120)
          if (s >= 0) return s
        case 23 =>
          val LA21_110 = input.LA(1)


          val index21_110 = _input.index()
          _input.rewind()
          s = -1
          if ((LA21_110 == '\"') && ((vhdl2008))) {
            s = 67
          }

          else s = 45


          _input.seek(index21_110)
          if (s >= 0) return s
        case 24 =>
          val LA21_121 = input.LA(1)


          val index21_121 = _input.index()
          _input.rewind()
          s = -1
          if ((LA21_121 == '\"') && ((vhdl2008))) {
            s = 67
          }

          else s = 45


          _input.seek(index21_121)
          if (s >= 0) return s
        case 25 =>
          val LA21_112 = input.LA(1)


          val index21_112 = _input.index()
          _input.rewind()
          s = -1
          if ((LA21_112 == ' ') && ((vhdl2008))) {
            s = 67
          }

          else s = 45


          _input.seek(index21_112)
          if (s >= 0) return s
        case 26 =>
          val LA21_122 = input.LA(1)


          val index21_122 = _input.index()
          _input.rewind()
          s = -1
          if ((LA21_122 == ' ') && ((vhdl2008))) {
            s = 67
          }

          else s = 45


          _input.seek(index21_122)
          if (s >= 0) return s
      }

      val nvae = new NoViableAltException(description, 21, __s, input)
      error(nvae)
      throw nvae
    }
  }

  private val dfa14 = new DFA14(this)
  private val dfa21 = new DFA21(this)
}