// $ANTLR 3.3 Nov 30, 2010 12:46:29 .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g 2011-03-09 22:29:28

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

object Lexer {
    val EOF = -1
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
    val AMS_ASSIGN = 102
    val VAR_ASSIGN = 103
    val BOX = 104
    val DBLQUOTE = 105
    val COMMA = 106
    val SEMICOLON = 107
    val LPAREN = 108
    val RPAREN = 109
    val LBRACKET = 110
    val RBRACKET = 111
    val COLON = 112
    val DOT = 113
    val AMPERSAND = 114
    val BAR = 115
    val ARROW = 116
    val AT = 117
    val QMARK = 118
    val DLT = 119
    val DGT = 120
    val CIRCUMFLEX = 121
    val DOUBLESTAR = 122
    val MUL = 123
    val DIV = 124
    val PLUS = 125
    val MINUS = 126
    val EQ = 127
    val NEQ = 128
    val LT = 129
    val GT = 130
    val LEQ = 131
    val GEQ = 132
    val MEQ = 133
    val MNEQ = 134
    val MLT = 135
    val MGT = 136
    val MLEQ = 137
    val MGEQ = 138
    val CONDITION_OPERATOR = 139
    val CONTEXT = 140
    val FORCE = 141
    val PARAMETER = 142
    val RELEASE = 143
    val DEFAULT = 144
    val NATURE = 145
    val TERMINAL = 146
    val QUANTITY = 147
    val TOLERANCE = 148
    val ACROSS = 149
    val THROUGH = 150
    val SPECTRUM = 151
    val NOISE = 152
    val SUBNATURE = 153
    val LIMIT = 154
    val REFERENCE = 155
    val BREAK = 156
    val PROCEDURAL = 157
    val WS = 158
    val NESTED_ML_COMMENT = 159
    val LINECOMMENT = 160
    val LETTER = 161
    val LETTER_OR_DIGIT = 162
    val BASIC_IDENTIFIER = 163
    val GRAPHIC_CHARACTER = 164
    val EXTENDED_IDENTIFIER = 165
    val INTEGER = 166
    val BASED_INTEGER = 167
    val EXPONENT = 168
    val BASED_LITERAL = 169
    val INTEGER_LITERAL = 170
    val REAL_LITERAL = 171
    val APOSTROPHE = 172
    val STRING_LITERAL = 173
    val BASE_SPECIFIER = 174
    val BIT_STRING_LITERAL = 175
    val EXTENDED_DIGIT = 176
    val DIGIT = 177
    val UPPER_CASE_LETTER = 178
    val LOWER_CASE_LETTER = 179
    val SPECIAL_CHARACTER = 180
    val SPACE_CHARACTER = 181
    val OTHER_SPECIAL_CHARACTER = 182
    val CHARACTER_LITERAL = 183

}

class Lexer(input: CharStream, state: RecognizerSharedState) extends AbstractLexer(input, state) {
    import Lexer._

    // delegates
    // delegators


    def this(input: CharStream) =
        this(input, new RecognizerSharedState())



    override val getGrammarFileName = ".\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g"

    // $ANTLR start "ABS"
    @throws(classOf[RecognitionException])
    final def mABS():Unit = {
        try {
            var _type = ABS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:49:4: ( 'abs' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:49:5: 'abs'
            	`match`("abs")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ABS"

    // $ANTLR start "ACCESS"
    @throws(classOf[RecognitionException])
    final def mACCESS():Unit = {
        try {
            var _type = ACCESS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:50:7: ( 'access' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:50:8: 'access'
            	`match`("access")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ACCESS"

    // $ANTLR start "AFTER"
    @throws(classOf[RecognitionException])
    final def mAFTER():Unit = {
        try {
            var _type = AFTER
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:51:6: ( 'after' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:51:7: 'after'
            	`match`("after")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "AFTER"

    // $ANTLR start "ALIAS"
    @throws(classOf[RecognitionException])
    final def mALIAS():Unit = {
        try {
            var _type = ALIAS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:52:6: ( 'alias' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:52:7: 'alias'
            	`match`("alias")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ALIAS"

    // $ANTLR start "ALL"
    @throws(classOf[RecognitionException])
    final def mALL():Unit = {
        try {
            var _type = ALL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:53:4: ( 'all' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:53:5: 'all'
            	`match`("all")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ALL"

    // $ANTLR start "AND"
    @throws(classOf[RecognitionException])
    final def mAND():Unit = {
        try {
            var _type = AND
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:54:4: ( 'and' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:54:5: 'and'
            	`match`("and")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "AND"

    // $ANTLR start "ARCHITECTURE"
    @throws(classOf[RecognitionException])
    final def mARCHITECTURE():Unit = {
        try {
            var _type = ARCHITECTURE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:55:13: ( 'architecture' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:55:14: 'architecture'
            	`match`("architecture")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ARCHITECTURE"

    // $ANTLR start "ARRAY"
    @throws(classOf[RecognitionException])
    final def mARRAY():Unit = {
        try {
            var _type = ARRAY
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:56:6: ( 'array' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:56:7: 'array'
            	`match`("array")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ARRAY"

    // $ANTLR start "ASSERT"
    @throws(classOf[RecognitionException])
    final def mASSERT():Unit = {
        try {
            var _type = ASSERT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:57:7: ( 'assert' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:57:8: 'assert'
            	`match`("assert")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ASSERT"

    // $ANTLR start "ATTRIBUTE"
    @throws(classOf[RecognitionException])
    final def mATTRIBUTE():Unit = {
        try {
            var _type = ATTRIBUTE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:58:10: ( 'attribute' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:58:11: 'attribute'
            	`match`("attribute")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ATTRIBUTE"

    // $ANTLR start "BEGIN"
    @throws(classOf[RecognitionException])
    final def mBEGIN():Unit = {
        try {
            var _type = BEGIN
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:59:6: ( 'begin' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:59:7: 'begin'
            	`match`("begin")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "BEGIN"

    // $ANTLR start "BLOCK"
    @throws(classOf[RecognitionException])
    final def mBLOCK():Unit = {
        try {
            var _type = BLOCK
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:60:6: ( 'block' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:60:7: 'block'
            	`match`("block")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "BLOCK"

    // $ANTLR start "BODY"
    @throws(classOf[RecognitionException])
    final def mBODY():Unit = {
        try {
            var _type = BODY
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:61:5: ( 'body' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:61:6: 'body'
            	`match`("body")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "BODY"

    // $ANTLR start "BUFFER"
    @throws(classOf[RecognitionException])
    final def mBUFFER():Unit = {
        try {
            var _type = BUFFER
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:62:7: ( 'buffer' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:62:8: 'buffer'
            	`match`("buffer")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "BUFFER"

    // $ANTLR start "BUS"
    @throws(classOf[RecognitionException])
    final def mBUS():Unit = {
        try {
            var _type = BUS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:63:4: ( 'bus' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:63:5: 'bus'
            	`match`("bus")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "BUS"

    // $ANTLR start "CASE"
    @throws(classOf[RecognitionException])
    final def mCASE():Unit = {
        try {
            var _type = CASE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:64:5: ( 'case' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:64:6: 'case'
            	`match`("case")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "CASE"

    // $ANTLR start "COMPONENT"
    @throws(classOf[RecognitionException])
    final def mCOMPONENT():Unit = {
        try {
            var _type = COMPONENT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:65:10: ( 'component' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:65:11: 'component'
            	`match`("component")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "COMPONENT"

    // $ANTLR start "CONFIGURATION"
    @throws(classOf[RecognitionException])
    final def mCONFIGURATION():Unit = {
        try {
            var _type = CONFIGURATION
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:66:14: ( 'configuration' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:66:15: 'configuration'
            	`match`("configuration")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "CONFIGURATION"

    // $ANTLR start "CONSTANT"
    @throws(classOf[RecognitionException])
    final def mCONSTANT():Unit = {
        try {
            var _type = CONSTANT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:67:9: ( 'constant' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:67:10: 'constant'
            	`match`("constant")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "CONSTANT"

    // $ANTLR start "DISCONNECT"
    @throws(classOf[RecognitionException])
    final def mDISCONNECT():Unit = {
        try {
            var _type = DISCONNECT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:68:11: ( 'disconnect' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:68:12: 'disconnect'
            	`match`("disconnect")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "DISCONNECT"

    // $ANTLR start "DOWNTO"
    @throws(classOf[RecognitionException])
    final def mDOWNTO():Unit = {
        try {
            var _type = DOWNTO
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:69:7: ( 'downto' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:69:8: 'downto'
            	`match`("downto")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "DOWNTO"

    // $ANTLR start "ELSE"
    @throws(classOf[RecognitionException])
    final def mELSE():Unit = {
        try {
            var _type = ELSE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:70:5: ( 'else' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:70:6: 'else'
            	`match`("else")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ELSE"

    // $ANTLR start "ELSIF"
    @throws(classOf[RecognitionException])
    final def mELSIF():Unit = {
        try {
            var _type = ELSIF
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:71:6: ( 'elsif' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:71:7: 'elsif'
            	`match`("elsif")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ELSIF"

    // $ANTLR start "END"
    @throws(classOf[RecognitionException])
    final def mEND():Unit = {
        try {
            var _type = END
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:72:4: ( 'end' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:72:5: 'end'
            	`match`("end")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "END"

    // $ANTLR start "ENTITY"
    @throws(classOf[RecognitionException])
    final def mENTITY():Unit = {
        try {
            var _type = ENTITY
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:73:7: ( 'entity' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:73:8: 'entity'
            	`match`("entity")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ENTITY"

    // $ANTLR start "EXIT"
    @throws(classOf[RecognitionException])
    final def mEXIT():Unit = {
        try {
            var _type = EXIT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:74:5: ( 'exit' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:74:6: 'exit'
            	`match`("exit")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "EXIT"

    // $ANTLR start "FILE"
    @throws(classOf[RecognitionException])
    final def mFILE():Unit = {
        try {
            var _type = FILE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:75:5: ( 'file' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:75:6: 'file'
            	`match`("file")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "FILE"

    // $ANTLR start "FOR"
    @throws(classOf[RecognitionException])
    final def mFOR():Unit = {
        try {
            var _type = FOR
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:76:4: ( 'for' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:76:5: 'for'
            	`match`("for")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "FOR"

    // $ANTLR start "FUNCTION"
    @throws(classOf[RecognitionException])
    final def mFUNCTION():Unit = {
        try {
            var _type = FUNCTION
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:77:9: ( 'function' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:77:10: 'function'
            	`match`("function")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "FUNCTION"

    // $ANTLR start "GENERATE"
    @throws(classOf[RecognitionException])
    final def mGENERATE():Unit = {
        try {
            var _type = GENERATE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:78:9: ( 'generate' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:78:10: 'generate'
            	`match`("generate")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "GENERATE"

    // $ANTLR start "GENERIC"
    @throws(classOf[RecognitionException])
    final def mGENERIC():Unit = {
        try {
            var _type = GENERIC
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:79:8: ( 'generic' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:79:9: 'generic'
            	`match`("generic")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "GENERIC"

    // $ANTLR start "GROUP"
    @throws(classOf[RecognitionException])
    final def mGROUP():Unit = {
        try {
            var _type = GROUP
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:80:6: ( 'group' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:80:7: 'group'
            	`match`("group")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "GROUP"

    // $ANTLR start "GUARDED"
    @throws(classOf[RecognitionException])
    final def mGUARDED():Unit = {
        try {
            var _type = GUARDED
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:81:8: ( 'guarded' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:81:9: 'guarded'
            	`match`("guarded")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "GUARDED"

    // $ANTLR start "IF"
    @throws(classOf[RecognitionException])
    final def mIF():Unit = {
        try {
            var _type = IF
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:82:3: ( 'if' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:82:4: 'if'
            	`match`("if")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "IF"

    // $ANTLR start "IMPURE"
    @throws(classOf[RecognitionException])
    final def mIMPURE():Unit = {
        try {
            var _type = IMPURE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:83:7: ( 'impure' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:83:8: 'impure'
            	`match`("impure")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "IMPURE"

    // $ANTLR start "IN"
    @throws(classOf[RecognitionException])
    final def mIN():Unit = {
        try {
            var _type = IN
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:84:3: ( 'in' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:84:4: 'in'
            	`match`("in")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "IN"

    // $ANTLR start "INERTIAL"
    @throws(classOf[RecognitionException])
    final def mINERTIAL():Unit = {
        try {
            var _type = INERTIAL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:85:9: ( 'inertial' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:85:10: 'inertial'
            	`match`("inertial")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "INERTIAL"

    // $ANTLR start "INOUT"
    @throws(classOf[RecognitionException])
    final def mINOUT():Unit = {
        try {
            var _type = INOUT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:86:6: ( 'inout' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:86:7: 'inout'
            	`match`("inout")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "INOUT"

    // $ANTLR start "IS"
    @throws(classOf[RecognitionException])
    final def mIS():Unit = {
        try {
            var _type = IS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:87:3: ( 'is' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:87:4: 'is'
            	`match`("is")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "IS"

    // $ANTLR start "LABEL"
    @throws(classOf[RecognitionException])
    final def mLABEL():Unit = {
        try {
            var _type = LABEL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:88:6: ( 'label' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:88:7: 'label'
            	`match`("label")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "LABEL"

    // $ANTLR start "LIBRARY"
    @throws(classOf[RecognitionException])
    final def mLIBRARY():Unit = {
        try {
            var _type = LIBRARY
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:89:8: ( 'library' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:89:9: 'library'
            	`match`("library")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "LIBRARY"

    // $ANTLR start "LINKAGE"
    @throws(classOf[RecognitionException])
    final def mLINKAGE():Unit = {
        try {
            var _type = LINKAGE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:90:8: ( 'linkage' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:90:9: 'linkage'
            	`match`("linkage")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "LINKAGE"

    // $ANTLR start "LITERAL"
    @throws(classOf[RecognitionException])
    final def mLITERAL():Unit = {
        try {
            var _type = LITERAL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:91:8: ( 'literal' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:91:9: 'literal'
            	`match`("literal")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "LITERAL"

    // $ANTLR start "LOOP"
    @throws(classOf[RecognitionException])
    final def mLOOP():Unit = {
        try {
            var _type = LOOP
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:92:5: ( 'loop' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:92:6: 'loop'
            	`match`("loop")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "LOOP"

    // $ANTLR start "MAP"
    @throws(classOf[RecognitionException])
    final def mMAP():Unit = {
        try {
            var _type = MAP
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:93:4: ( 'map' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:93:5: 'map'
            	`match`("map")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "MAP"

    // $ANTLR start "MOD"
    @throws(classOf[RecognitionException])
    final def mMOD():Unit = {
        try {
            var _type = MOD
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:94:4: ( 'mod' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:94:5: 'mod'
            	`match`("mod")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "MOD"

    // $ANTLR start "NAND"
    @throws(classOf[RecognitionException])
    final def mNAND():Unit = {
        try {
            var _type = NAND
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:95:5: ( 'nand' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:95:6: 'nand'
            	`match`("nand")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "NAND"

    // $ANTLR start "NEW"
    @throws(classOf[RecognitionException])
    final def mNEW():Unit = {
        try {
            var _type = NEW
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:96:4: ( 'new' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:96:5: 'new'
            	`match`("new")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "NEW"

    // $ANTLR start "NEXT"
    @throws(classOf[RecognitionException])
    final def mNEXT():Unit = {
        try {
            var _type = NEXT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:97:5: ( 'next' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:97:6: 'next'
            	`match`("next")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "NEXT"

    // $ANTLR start "NOR"
    @throws(classOf[RecognitionException])
    final def mNOR():Unit = {
        try {
            var _type = NOR
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:98:4: ( 'nor' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:98:5: 'nor'
            	`match`("nor")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "NOR"

    // $ANTLR start "NOT"
    @throws(classOf[RecognitionException])
    final def mNOT():Unit = {
        try {
            var _type = NOT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:99:4: ( 'not' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:99:5: 'not'
            	`match`("not")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "NOT"

    // $ANTLR start "NULL"
    @throws(classOf[RecognitionException])
    final def mNULL():Unit = {
        try {
            var _type = NULL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:100:5: ( 'null' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:100:6: 'null'
            	`match`("null")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "NULL"

    // $ANTLR start "OF"
    @throws(classOf[RecognitionException])
    final def mOF():Unit = {
        try {
            var _type = OF
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:101:3: ( 'of' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:101:4: 'of'
            	`match`("of")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "OF"

    // $ANTLR start "ON"
    @throws(classOf[RecognitionException])
    final def mON():Unit = {
        try {
            var _type = ON
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:102:3: ( 'on' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:102:4: 'on'
            	`match`("on")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ON"

    // $ANTLR start "OPEN"
    @throws(classOf[RecognitionException])
    final def mOPEN():Unit = {
        try {
            var _type = OPEN
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:103:5: ( 'open' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:103:6: 'open'
            	`match`("open")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "OPEN"

    // $ANTLR start "OR"
    @throws(classOf[RecognitionException])
    final def mOR():Unit = {
        try {
            var _type = OR
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:104:3: ( 'or' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:104:4: 'or'
            	`match`("or")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "OR"

    // $ANTLR start "OTHERS"
    @throws(classOf[RecognitionException])
    final def mOTHERS():Unit = {
        try {
            var _type = OTHERS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:105:7: ( 'others' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:105:8: 'others'
            	`match`("others")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "OTHERS"

    // $ANTLR start "OUT"
    @throws(classOf[RecognitionException])
    final def mOUT():Unit = {
        try {
            var _type = OUT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:106:4: ( 'out' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:106:5: 'out'
            	`match`("out")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "OUT"

    // $ANTLR start "PACKAGE"
    @throws(classOf[RecognitionException])
    final def mPACKAGE():Unit = {
        try {
            var _type = PACKAGE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:107:8: ( 'package' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:107:9: 'package'
            	`match`("package")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "PACKAGE"

    // $ANTLR start "PORT"
    @throws(classOf[RecognitionException])
    final def mPORT():Unit = {
        try {
            var _type = PORT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:108:5: ( 'port' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:108:6: 'port'
            	`match`("port")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "PORT"

    // $ANTLR start "POSTPONED"
    @throws(classOf[RecognitionException])
    final def mPOSTPONED():Unit = {
        try {
            var _type = POSTPONED
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:109:10: ( 'postponed' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:109:11: 'postponed'
            	`match`("postponed")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "POSTPONED"

    // $ANTLR start "PROCEDURE"
    @throws(classOf[RecognitionException])
    final def mPROCEDURE():Unit = {
        try {
            var _type = PROCEDURE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:110:10: ( 'procedure' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:110:11: 'procedure'
            	`match`("procedure")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "PROCEDURE"

    // $ANTLR start "PROCESS"
    @throws(classOf[RecognitionException])
    final def mPROCESS():Unit = {
        try {
            var _type = PROCESS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:111:8: ( 'process' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:111:9: 'process'
            	`match`("process")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "PROCESS"

    // $ANTLR start "PROTECTED"
    @throws(classOf[RecognitionException])
    final def mPROTECTED():Unit = {
        try {
            var _type = PROTECTED
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:112:10: ( 'protected' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:112:11: 'protected'
            	`match`("protected")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "PROTECTED"

    // $ANTLR start "PURE"
    @throws(classOf[RecognitionException])
    final def mPURE():Unit = {
        try {
            var _type = PURE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:113:5: ( 'pure' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:113:6: 'pure'
            	`match`("pure")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "PURE"

    // $ANTLR start "RANGE"
    @throws(classOf[RecognitionException])
    final def mRANGE():Unit = {
        try {
            var _type = RANGE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:114:6: ( 'range' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:114:7: 'range'
            	`match`("range")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "RANGE"

    // $ANTLR start "RECORD"
    @throws(classOf[RecognitionException])
    final def mRECORD():Unit = {
        try {
            var _type = RECORD
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:115:7: ( 'record' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:115:8: 'record'
            	`match`("record")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "RECORD"

    // $ANTLR start "REGISTER"
    @throws(classOf[RecognitionException])
    final def mREGISTER():Unit = {
        try {
            var _type = REGISTER
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:116:9: ( 'register' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:116:10: 'register'
            	`match`("register")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "REGISTER"

    // $ANTLR start "REJECT"
    @throws(classOf[RecognitionException])
    final def mREJECT():Unit = {
        try {
            var _type = REJECT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:117:7: ( 'reject' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:117:8: 'reject'
            	`match`("reject")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "REJECT"

    // $ANTLR start "REM"
    @throws(classOf[RecognitionException])
    final def mREM():Unit = {
        try {
            var _type = REM
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:118:4: ( 'rem' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:118:5: 'rem'
            	`match`("rem")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "REM"

    // $ANTLR start "REPORT"
    @throws(classOf[RecognitionException])
    final def mREPORT():Unit = {
        try {
            var _type = REPORT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:119:7: ( 'report' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:119:8: 'report'
            	`match`("report")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "REPORT"

    // $ANTLR start "RETURN"
    @throws(classOf[RecognitionException])
    final def mRETURN():Unit = {
        try {
            var _type = RETURN
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:120:7: ( 'return' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:120:8: 'return'
            	`match`("return")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "RETURN"

    // $ANTLR start "ROL"
    @throws(classOf[RecognitionException])
    final def mROL():Unit = {
        try {
            var _type = ROL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:121:4: ( 'rol' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:121:5: 'rol'
            	`match`("rol")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ROL"

    // $ANTLR start "ROR"
    @throws(classOf[RecognitionException])
    final def mROR():Unit = {
        try {
            var _type = ROR
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:122:4: ( 'ror' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:122:5: 'ror'
            	`match`("ror")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ROR"

    // $ANTLR start "SELECT"
    @throws(classOf[RecognitionException])
    final def mSELECT():Unit = {
        try {
            var _type = SELECT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:123:7: ( 'select' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:123:8: 'select'
            	`match`("select")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SELECT"

    // $ANTLR start "SEVERITY"
    @throws(classOf[RecognitionException])
    final def mSEVERITY():Unit = {
        try {
            var _type = SEVERITY
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:124:9: ( 'severity' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:124:10: 'severity'
            	`match`("severity")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SEVERITY"

    // $ANTLR start "SHARED"
    @throws(classOf[RecognitionException])
    final def mSHARED():Unit = {
        try {
            var _type = SHARED
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:125:7: ( 'shared' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:125:8: 'shared'
            	`match`("shared")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SHARED"

    // $ANTLR start "SIGNAL"
    @throws(classOf[RecognitionException])
    final def mSIGNAL():Unit = {
        try {
            var _type = SIGNAL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:126:7: ( 'signal' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:126:8: 'signal'
            	`match`("signal")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SIGNAL"

    // $ANTLR start "SLA"
    @throws(classOf[RecognitionException])
    final def mSLA():Unit = {
        try {
            var _type = SLA
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:127:4: ( 'sla' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:127:5: 'sla'
            	`match`("sla")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SLA"

    // $ANTLR start "SLL"
    @throws(classOf[RecognitionException])
    final def mSLL():Unit = {
        try {
            var _type = SLL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:128:4: ( 'sll' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:128:5: 'sll'
            	`match`("sll")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SLL"

    // $ANTLR start "SRA"
    @throws(classOf[RecognitionException])
    final def mSRA():Unit = {
        try {
            var _type = SRA
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:129:4: ( 'sra' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:129:5: 'sra'
            	`match`("sra")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SRA"

    // $ANTLR start "SRL"
    @throws(classOf[RecognitionException])
    final def mSRL():Unit = {
        try {
            var _type = SRL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:130:4: ( 'srl' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:130:5: 'srl'
            	`match`("srl")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SRL"

    // $ANTLR start "SUBTYPE"
    @throws(classOf[RecognitionException])
    final def mSUBTYPE():Unit = {
        try {
            var _type = SUBTYPE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:131:8: ( 'subtype' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:131:9: 'subtype'
            	`match`("subtype")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SUBTYPE"

    // $ANTLR start "THEN"
    @throws(classOf[RecognitionException])
    final def mTHEN():Unit = {
        try {
            var _type = THEN
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:132:5: ( 'then' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:132:6: 'then'
            	`match`("then")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "THEN"

    // $ANTLR start "TO"
    @throws(classOf[RecognitionException])
    final def mTO():Unit = {
        try {
            var _type = TO
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:133:3: ( 'to' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:133:4: 'to'
            	`match`("to")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "TO"

    // $ANTLR start "TRANSPORT"
    @throws(classOf[RecognitionException])
    final def mTRANSPORT():Unit = {
        try {
            var _type = TRANSPORT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:134:10: ( 'transport' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:134:11: 'transport'
            	`match`("transport")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "TRANSPORT"

    // $ANTLR start "TYPE"
    @throws(classOf[RecognitionException])
    final def mTYPE():Unit = {
        try {
            var _type = TYPE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:135:5: ( 'type' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:135:6: 'type'
            	`match`("type")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "TYPE"

    // $ANTLR start "UNAFFECTED"
    @throws(classOf[RecognitionException])
    final def mUNAFFECTED():Unit = {
        try {
            var _type = UNAFFECTED
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:136:11: ( 'unaffected' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:136:12: 'unaffected'
            	`match`("unaffected")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "UNAFFECTED"

    // $ANTLR start "UNITS"
    @throws(classOf[RecognitionException])
    final def mUNITS():Unit = {
        try {
            var _type = UNITS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:137:6: ( 'units' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:137:7: 'units'
            	`match`("units")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "UNITS"

    // $ANTLR start "UNTIL"
    @throws(classOf[RecognitionException])
    final def mUNTIL():Unit = {
        try {
            var _type = UNTIL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:138:6: ( 'until' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:138:7: 'until'
            	`match`("until")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "UNTIL"

    // $ANTLR start "USE"
    @throws(classOf[RecognitionException])
    final def mUSE():Unit = {
        try {
            var _type = USE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:139:4: ( 'use' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:139:5: 'use'
            	`match`("use")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "USE"

    // $ANTLR start "VARIABLE"
    @throws(classOf[RecognitionException])
    final def mVARIABLE():Unit = {
        try {
            var _type = VARIABLE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:140:9: ( 'variable' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:140:10: 'variable'
            	`match`("variable")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "VARIABLE"

    // $ANTLR start "WAIT"
    @throws(classOf[RecognitionException])
    final def mWAIT():Unit = {
        try {
            var _type = WAIT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:141:5: ( 'wait' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:141:6: 'wait'
            	`match`("wait")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "WAIT"

    // $ANTLR start "WHEN"
    @throws(classOf[RecognitionException])
    final def mWHEN():Unit = {
        try {
            var _type = WHEN
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:142:5: ( 'when' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:142:6: 'when'
            	`match`("when")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "WHEN"

    // $ANTLR start "WHILE"
    @throws(classOf[RecognitionException])
    final def mWHILE():Unit = {
        try {
            var _type = WHILE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:143:6: ( 'while' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:143:7: 'while'
            	`match`("while")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "WHILE"

    // $ANTLR start "WITH"
    @throws(classOf[RecognitionException])
    final def mWITH():Unit = {
        try {
            var _type = WITH
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:144:5: ( 'with' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:144:6: 'with'
            	`match`("with")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "WITH"

    // $ANTLR start "XNOR"
    @throws(classOf[RecognitionException])
    final def mXNOR():Unit = {
        try {
            var _type = XNOR
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:145:5: ( 'xnor' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:145:6: 'xnor'
            	`match`("xnor")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "XNOR"

    // $ANTLR start "XOR"
    @throws(classOf[RecognitionException])
    final def mXOR():Unit = {
        try {
            var _type = XOR
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:146:4: ( 'xor' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:146:5: 'xor'
            	`match`("xor")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "XOR"

    // $ANTLR start "AMS_ASSIGN"
    @throws(classOf[RecognitionException])
    final def mAMS_ASSIGN():Unit = {
        try {
            var _type = AMS_ASSIGN
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:162:15: ( '==' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:162:17: '=='
            	`match`("==")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "AMS_ASSIGN"

    // $ANTLR start "VAR_ASSIGN"
    @throws(classOf[RecognitionException])
    final def mVAR_ASSIGN():Unit = {
        try {
            var _type = VAR_ASSIGN
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:163:15: ( ':=' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:163:17: ':='
            	`match`(":=")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "VAR_ASSIGN"

    // $ANTLR start "BOX"
    @throws(classOf[RecognitionException])
    final def mBOX():Unit = {
        try {
            var _type = BOX
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:164:15: ( '<>' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:164:17: '<>'
            	`match`("<>")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "BOX"

    // $ANTLR start "DBLQUOTE"
    @throws(classOf[RecognitionException])
    final def mDBLQUOTE():Unit = {
        try {
            var _type = DBLQUOTE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:165:15: ( '\\\"' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:165:17: '\\\"'
            	`match`('\"')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "DBLQUOTE"

    // $ANTLR start "COMMA"
    @throws(classOf[RecognitionException])
    final def mCOMMA():Unit = {
        try {
            var _type = COMMA
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:166:15: ( ',' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:166:17: ','
            	`match`(',')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "COMMA"

    // $ANTLR start "SEMICOLON"
    @throws(classOf[RecognitionException])
    final def mSEMICOLON():Unit = {
        try {
            var _type = SEMICOLON
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:167:15: ( ';' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:167:17: ';'
            	`match`(';')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SEMICOLON"

    // $ANTLR start "LPAREN"
    @throws(classOf[RecognitionException])
    final def mLPAREN():Unit = {
        try {
            var _type = LPAREN
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:168:15: ( '(' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:168:17: '('
            	`match`('(')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "LPAREN"

    // $ANTLR start "RPAREN"
    @throws(classOf[RecognitionException])
    final def mRPAREN():Unit = {
        try {
            var _type = RPAREN
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:169:15: ( ')' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:169:17: ')'
            	`match`(')')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "RPAREN"

    // $ANTLR start "LBRACKET"
    @throws(classOf[RecognitionException])
    final def mLBRACKET():Unit = {
        try {
            var _type = LBRACKET
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:170:15: ( '[' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:170:17: '['
            	`match`('[')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "LBRACKET"

    // $ANTLR start "RBRACKET"
    @throws(classOf[RecognitionException])
    final def mRBRACKET():Unit = {
        try {
            var _type = RBRACKET
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:171:15: ( ']' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:171:17: ']'
            	`match`(']')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "RBRACKET"

    // $ANTLR start "COLON"
    @throws(classOf[RecognitionException])
    final def mCOLON():Unit = {
        try {
            var _type = COLON
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:172:15: ( ':' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:172:17: ':'
            	`match`(':')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "COLON"

    // $ANTLR start "DOT"
    @throws(classOf[RecognitionException])
    final def mDOT():Unit = {
        try {
            var _type = DOT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:173:15: ( '.' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:173:17: '.'
            	`match`('.')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "DOT"

    // $ANTLR start "AMPERSAND"
    @throws(classOf[RecognitionException])
    final def mAMPERSAND():Unit = {
        try {
            var _type = AMPERSAND
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:174:15: ( '&' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:174:17: '&'
            	`match`('&')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "AMPERSAND"

    // $ANTLR start "BAR"
    @throws(classOf[RecognitionException])
    final def mBAR():Unit = {
        try {
            var _type = BAR
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:175:15: ( '|' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:175:17: '|'
            	`match`('|')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "BAR"

    // $ANTLR start "ARROW"
    @throws(classOf[RecognitionException])
    final def mARROW():Unit = {
        try {
            var _type = ARROW
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:176:15: ( '=>' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:176:17: '=>'
            	`match`("=>")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ARROW"

    // $ANTLR start "AT"
    @throws(classOf[RecognitionException])
    final def mAT():Unit = {
        try {
            var _type = AT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:178:15: ( '@' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:178:17: '@'
            	`match`('@')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "AT"

    // $ANTLR start "QMARK"
    @throws(classOf[RecognitionException])
    final def mQMARK():Unit = {
        try {
            var _type = QMARK
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:179:14: ( '?' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:179:16: '?'
            	`match`('?')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "QMARK"

    // $ANTLR start "DLT"
    @throws(classOf[RecognitionException])
    final def mDLT():Unit = {
        try {
            var _type = DLT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:180:12: ( '<<' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:180:14: '<<'
            	`match`("<<")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "DLT"

    // $ANTLR start "DGT"
    @throws(classOf[RecognitionException])
    final def mDGT():Unit = {
        try {
            var _type = DGT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:181:12: ( '>>' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:181:14: '>>'
            	`match`(">>")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "DGT"

    // $ANTLR start "CIRCUMFLEX"
    @throws(classOf[RecognitionException])
    final def mCIRCUMFLEX():Unit = {
        try {
            var _type = CIRCUMFLEX
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:182:15: ( '^' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:182:17: '^'
            	`match`('^')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "CIRCUMFLEX"

    // $ANTLR start "DOUBLESTAR"
    @throws(classOf[RecognitionException])
    final def mDOUBLESTAR():Unit = {
        try {
            var _type = DOUBLESTAR
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:184:15: ( '**' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:184:17: '**'
            	`match`("**")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "DOUBLESTAR"

    // $ANTLR start "MUL"
    @throws(classOf[RecognitionException])
    final def mMUL():Unit = {
        try {
            var _type = MUL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:185:15: ( '*' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:185:17: '*'
            	`match`('*')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "MUL"

    // $ANTLR start "DIV"
    @throws(classOf[RecognitionException])
    final def mDIV():Unit = {
        try {
            var _type = DIV
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:186:15: ( '/' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:186:17: '/'
            	`match`('/')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "DIV"

    // $ANTLR start "PLUS"
    @throws(classOf[RecognitionException])
    final def mPLUS():Unit = {
        try {
            var _type = PLUS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:187:15: ( '+' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:187:17: '+'
            	`match`('+')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "PLUS"

    // $ANTLR start "MINUS"
    @throws(classOf[RecognitionException])
    final def mMINUS():Unit = {
        try {
            var _type = MINUS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:188:15: ( '-' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:188:17: '-'
            	`match`('-')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "MINUS"

    // $ANTLR start "EQ"
    @throws(classOf[RecognitionException])
    final def mEQ():Unit = {
        try {
            var _type = EQ
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:189:15: ( '=' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:189:17: '='
            	`match`('=')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "EQ"

    // $ANTLR start "NEQ"
    @throws(classOf[RecognitionException])
    final def mNEQ():Unit = {
        try {
            var _type = NEQ
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:190:15: ( '/=' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:190:17: '/='
            	`match`("/=")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "NEQ"

    // $ANTLR start "LT"
    @throws(classOf[RecognitionException])
    final def mLT():Unit = {
        try {
            var _type = LT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:191:15: ( '<' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:191:17: '<'
            	`match`('<')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "LT"

    // $ANTLR start "GT"
    @throws(classOf[RecognitionException])
    final def mGT():Unit = {
        try {
            var _type = GT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:192:15: ( '>' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:192:17: '>'
            	`match`('>')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "GT"

    // $ANTLR start "LEQ"
    @throws(classOf[RecognitionException])
    final def mLEQ():Unit = {
        try {
            var _type = LEQ
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:193:15: ( '<=' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:193:17: '<='
            	`match`("<=")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "LEQ"

    // $ANTLR start "GEQ"
    @throws(classOf[RecognitionException])
    final def mGEQ():Unit = {
        try {
            var _type = GEQ
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:194:15: ( '>=' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:194:17: '>='
            	`match`(">=")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "GEQ"

    // $ANTLR start "MEQ"
    @throws(classOf[RecognitionException])
    final def mMEQ():Unit = {
        try {
            var _type = MEQ
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:195:15: ( '?=' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:195:17: '?='
            	`match`("?=")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "MEQ"

    // $ANTLR start "MNEQ"
    @throws(classOf[RecognitionException])
    final def mMNEQ():Unit = {
        try {
            var _type = MNEQ
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:196:12: ( '?/=' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:196:14: '?/='
            	`match`("?/=")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "MNEQ"

    // $ANTLR start "MLT"
    @throws(classOf[RecognitionException])
    final def mMLT():Unit = {
        try {
            var _type = MLT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:197:15: ( '?<' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:197:17: '?<'
            	`match`("?<")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "MLT"

    // $ANTLR start "MGT"
    @throws(classOf[RecognitionException])
    final def mMGT():Unit = {
        try {
            var _type = MGT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:198:15: ( '?>' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:198:17: '?>'
            	`match`("?>")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "MGT"

    // $ANTLR start "MLEQ"
    @throws(classOf[RecognitionException])
    final def mMLEQ():Unit = {
        try {
            var _type = MLEQ
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:199:12: ( '?<=' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:199:14: '?<='
            	`match`("?<=")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "MLEQ"

    // $ANTLR start "MGEQ"
    @throws(classOf[RecognitionException])
    final def mMGEQ():Unit = {
        try {
            var _type = MGEQ
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:200:12: ( '?>=' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:200:14: '?>='
            	`match`("?>=")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "MGEQ"

    // $ANTLR start "CONDITION_OPERATOR"
    @throws(classOf[RecognitionException])
    final def mCONDITION_OPERATOR():Unit = {
        try {
            var _type = CONDITION_OPERATOR
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:201:20: ( '??' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:201:22: '??'
            	`match`("??")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "CONDITION_OPERATOR"

    // $ANTLR start "CONTEXT"
    @throws(classOf[RecognitionException])
    final def mCONTEXT():Unit = {
        try {
            var _type = CONTEXT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:204:9: ({...}? => 'context' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:204:11: {...}? => 'context'
            	if ( !((vhdl2008)) ) {
            	    throw new FailedPredicateException(input, "CONTEXT", "vhdl2008")
            	}
            	`match`("context")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "CONTEXT"

    // $ANTLR start "FORCE"
    @throws(classOf[RecognitionException])
    final def mFORCE():Unit = {
        try {
            var _type = FORCE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:206:7: ({...}? => 'force' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:206:9: {...}? => 'force'
            	if ( !((vhdl2008)) ) {
            	    throw new FailedPredicateException(input, "FORCE", "vhdl2008")
            	}
            	`match`("force")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "FORCE"

    // $ANTLR start "PARAMETER"
    @throws(classOf[RecognitionException])
    final def mPARAMETER():Unit = {
        try {
            var _type = PARAMETER
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:208:11: ({...}? => 'parameter' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:208:13: {...}? => 'parameter'
            	if ( !((vhdl2008)) ) {
            	    throw new FailedPredicateException(input, "PARAMETER", "vhdl2008")
            	}
            	`match`("parameter")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "PARAMETER"

    // $ANTLR start "RELEASE"
    @throws(classOf[RecognitionException])
    final def mRELEASE():Unit = {
        try {
            var _type = RELEASE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:210:9: ({...}? => 'release' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:210:11: {...}? => 'release'
            	if ( !((vhdl2008)) ) {
            	    throw new FailedPredicateException(input, "RELEASE", "vhdl2008")
            	}
            	`match`("release")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "RELEASE"

    // $ANTLR start "DEFAULT"
    @throws(classOf[RecognitionException])
    final def mDEFAULT():Unit = {
        try {
            var _type = DEFAULT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:212:9: ({...}? => 'default' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:212:11: {...}? => 'default'
            	if ( !((vhdl2008)) ) {
            	    throw new FailedPredicateException(input, "DEFAULT", "vhdl2008")
            	}
            	`match`("default")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "DEFAULT"

    // $ANTLR start "NATURE"
    @throws(classOf[RecognitionException])
    final def mNATURE():Unit = {
        try {
            var _type = NATURE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:215:8: ({...}? => 'nature' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:215:10: {...}? => 'nature'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "NATURE", "ams")
            	}
            	`match`("nature")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "NATURE"

    // $ANTLR start "TERMINAL"
    @throws(classOf[RecognitionException])
    final def mTERMINAL():Unit = {
        try {
            var _type = TERMINAL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:217:10: ({...}? => 'terminal' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:217:12: {...}? => 'terminal'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "TERMINAL", "ams")
            	}
            	`match`("terminal")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "TERMINAL"

    // $ANTLR start "QUANTITY"
    @throws(classOf[RecognitionException])
    final def mQUANTITY():Unit = {
        try {
            var _type = QUANTITY
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:219:10: ({...}? => 'quantity' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:219:12: {...}? => 'quantity'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "QUANTITY", "ams")
            	}
            	`match`("quantity")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "QUANTITY"

    // $ANTLR start "TOLERANCE"
    @throws(classOf[RecognitionException])
    final def mTOLERANCE():Unit = {
        try {
            var _type = TOLERANCE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:221:11: ({...}? => 'tolerance' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:221:13: {...}? => 'tolerance'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "TOLERANCE", "ams")
            	}
            	`match`("tolerance")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "TOLERANCE"

    // $ANTLR start "ACROSS"
    @throws(classOf[RecognitionException])
    final def mACROSS():Unit = {
        try {
            var _type = ACROSS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:223:8: ({...}? => 'across' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:223:10: {...}? => 'across'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "ACROSS", "ams")
            	}
            	`match`("across")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "ACROSS"

    // $ANTLR start "THROUGH"
    @throws(classOf[RecognitionException])
    final def mTHROUGH():Unit = {
        try {
            var _type = THROUGH
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:225:9: ({...}? => 'through' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:225:11: {...}? => 'through'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "THROUGH", "ams")
            	}
            	`match`("through")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "THROUGH"

    // $ANTLR start "SPECTRUM"
    @throws(classOf[RecognitionException])
    final def mSPECTRUM():Unit = {
        try {
            var _type = SPECTRUM
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:227:10: ({...}? => 'spectrum' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:227:12: {...}? => 'spectrum'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "SPECTRUM", "ams")
            	}
            	`match`("spectrum")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SPECTRUM"

    // $ANTLR start "NOISE"
    @throws(classOf[RecognitionException])
    final def mNOISE():Unit = {
        try {
            var _type = NOISE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:229:7: ({...}? => 'noise' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:229:9: {...}? => 'noise'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "NOISE", "ams")
            	}
            	`match`("noise")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "NOISE"

    // $ANTLR start "SUBNATURE"
    @throws(classOf[RecognitionException])
    final def mSUBNATURE():Unit = {
        try {
            var _type = SUBNATURE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:231:11: ({...}? => 'subnature' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:231:13: {...}? => 'subnature'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "SUBNATURE", "ams")
            	}
            	`match`("subnature")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "SUBNATURE"

    // $ANTLR start "LIMIT"
    @throws(classOf[RecognitionException])
    final def mLIMIT():Unit = {
        try {
            var _type = LIMIT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:233:7: ({...}? => 'limit' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:233:9: {...}? => 'limit'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "LIMIT", "ams")
            	}
            	`match`("limit")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "LIMIT"

    // $ANTLR start "REFERENCE"
    @throws(classOf[RecognitionException])
    final def mREFERENCE():Unit = {
        try {
            var _type = REFERENCE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:235:11: ({...}? => 'reference' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:235:13: {...}? => 'reference'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "REFERENCE", "ams")
            	}
            	`match`("reference")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "REFERENCE"

    // $ANTLR start "BREAK"
    @throws(classOf[RecognitionException])
    final def mBREAK():Unit = {
        try {
            var _type = BREAK
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:237:7: ({...}? => 'break' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:237:9: {...}? => 'break'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "BREAK", "ams")
            	}
            	`match`("break")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "BREAK"

    // $ANTLR start "PROCEDURAL"
    @throws(classOf[RecognitionException])
    final def mPROCEDURAL():Unit = {
        try {
            var _type = PROCEDURAL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:239:12: ({...}? => 'procedural' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:239:14: {...}? => 'procedural'
            	if ( !((ams)) ) {
            	    throw new FailedPredicateException(input, "PROCEDURAL", "ams")
            	}
            	`match`("procedural")





            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "PROCEDURAL"

    // $ANTLR start "WS"
    @throws(classOf[RecognitionException])
    final def mWS():Unit = {
        try {
            var _type = WS
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:242:4: ( ( '\\t' | ' ' | '\\r' | '\\n' )+ )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:242:6: ( '\\t' | ' ' | '\\r' | '\\n' )+
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:242:6: ( '\\t' | ' ' | '\\r' | '\\n' )+
            	var cnt1 = 0
            	var loop1_quitflag = false
            	while (!loop1_quitflag) {
            	    val alt1 = {
            	val LA1_0 = input.LA(1)

            	if ( ((LA1_0>='\t' && LA1_0<='\n')||LA1_0=='\r'||LA1_0==' ') ) 1

            	else -1
            	}
            	    alt1 match {
            		case 1 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            		    	if ( (input.LA(1)>='\t' && input.LA(1)<='\n')||input.LA(1)=='\r'||input.LA(1)==' ' ) {
            		    	    input.consume()

            		    	}
            		    	else {
            		    	    val mse = new MismatchedSetException(null,input)
            		    	    recover(mse)
            		    	    throw mse}



            		case _ =>
            		    if ( cnt1 >= 1 ) loop1_quitflag = true
            		    else {
            	            val eee = new EarlyExitException(1, input)
            	            throw eee
            	      }
            	    }
            	    cnt1+=1
            	}

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
    final def mNESTED_ML_COMMENT():Unit = {
        try {
            var _type = NESTED_ML_COMMENT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:245:19: ( '/*' ( options {greedy=false; } : NESTED_ML_COMMENT | . )* '*/' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:246:2: '/*' ( options {greedy=false; } : NESTED_ML_COMMENT | . )* '*/'
            	`match`("/*")


            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:246:7: ( options {greedy=false; } : NESTED_ML_COMMENT | . )*
            	var loop2_quitflag = false
            	while (!loop2_quitflag) {
            	    val alt2 = {
            	val LA2_0 = input.LA(1)

            	if ( (LA2_0=='*') ) {
            	val LA2_1 = input.LA(2)

            	if ( (LA2_1=='/') ) 3
            	else if ( ((LA2_1>='\u0000' && LA2_1<='.')||(LA2_1>='0' && LA2_1<='\uFFFF')) ) 2

            	else -1
            	}
            	else if ( (LA2_0=='/') ) {
            	val LA2_2 = input.LA(2)

            	if ( (LA2_2=='*') ) 1
            	else if ( ((LA2_2>='\u0000' && LA2_2<=')')||(LA2_2>='+' && LA2_2<='\uFFFF')) ) 2

            	else -1
            	}
            	else if ( ((LA2_0>='\u0000' && LA2_0<=')')||(LA2_0>='+' && LA2_0<='.')||(LA2_0>='0' && LA2_0<='\uFFFF')) ) 2

            	else -1
            	}
            	    alt2 match {
            		case 1 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:246:34: NESTED_ML_COMMENT
            		    	mNESTED_ML_COMMENT()


            		case 2 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:246:54: .
            		    	matchAny()



            		case _ => loop2_quitflag = true
            	    }
            	}

            	`match`("*/")


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
    final def mLINECOMMENT():Unit = {
        try {
            var _type = LINECOMMENT
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:249:13: ( '--' (~ ( '\\r' | '\\n' | '\\u000C' ) )* )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:249:15: '--' (~ ( '\\r' | '\\n' | '\\u000C' ) )*
            	`match`("--")


            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:249:20: (~ ( '\\r' | '\\n' | '\\u000C' ) )*
            	var loop3_quitflag = false
            	while (!loop3_quitflag) {
            	    val alt3 = {
            	val LA3_0 = input.LA(1)

            	if ( ((LA3_0>='\u0000' && LA3_0<='\t')||LA3_0=='\u000B'||(LA3_0>='\u000E' && LA3_0<='\uFFFF')) ) 1

            	else -1
            	}
            	    alt3 match {
            		case 1 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:249:20: ~ ( '\\r' | '\\n' | '\\u000C' )
            		    	if ( (input.LA(1)>='\u0000' && input.LA(1)<='\t')||input.LA(1)=='\u000B'||(input.LA(1)>='\u000E' && input.LA(1)<='\uFFFF') ) {
            		    	    input.consume()

            		    	}
            		    	else {
            		    	    val mse = new MismatchedSetException(null,input)
            		    	    recover(mse)
            		    	    throw mse}



            		case _ => loop3_quitflag = true
            	    }
            	}

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
    final def mBASIC_IDENTIFIER():Unit = {
        try {
            var _type = BASIC_IDENTIFIER
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:251:18: ( LETTER ( LETTER_OR_DIGIT | '_' )* )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:251:20: LETTER ( LETTER_OR_DIGIT | '_' )*
            	mLETTER()

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:251:27: ( LETTER_OR_DIGIT | '_' )*
            	var loop4_quitflag = false
            	while (!loop4_quitflag) {
            	    val alt4 = {
            	val LA4_0 = input.LA(1)

            	if ( ((LA4_0>='0' && LA4_0<='9')||(LA4_0>='A' && LA4_0<='Z')||LA4_0=='_'||(LA4_0>='a' && LA4_0<='z')||(LA4_0>='\u00C0' && LA4_0<='\u00D6')||(LA4_0>='\u00D8' && LA4_0<='\u00F6')||(LA4_0>='\u00F8' && LA4_0<='\u00FF')) ) 1

            	else -1
            	}
            	    alt4 match {
            		case 1 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            		    	if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||input.LA(1)=='_'||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u00FF') ) {
            		    	    input.consume()

            		    	}
            		    	else {
            		    	    val mse = new MismatchedSetException(null,input)
            		    	    recover(mse)
            		    	    throw mse}



            		case _ => loop4_quitflag = true
            	    }
            	}




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "BASIC_IDENTIFIER"

    // $ANTLR start "EXTENDED_IDENTIFIER"
    @throws(classOf[RecognitionException])
    final def mEXTENDED_IDENTIFIER():Unit = {
        try {
            var _type = EXTENDED_IDENTIFIER
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:254:21: ( '\\\\' ( '\\\"' | '\\\\\\\\' | GRAPHIC_CHARACTER )+ '\\\\' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:254:23: '\\\\' ( '\\\"' | '\\\\\\\\' | GRAPHIC_CHARACTER )+ '\\\\'
            	`match`('\\')

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:254:28: ( '\\\"' | '\\\\\\\\' | GRAPHIC_CHARACTER )+
            	var cnt5 = 0
            	var loop5_quitflag = false
            	while (!loop5_quitflag) {
            	    val alt5 = input.LA(1) match {
            	case '\\' => {
            	val LA5_1 = input.LA(2)

            	if ( (LA5_1=='\\') ) 2

            	else -1
            	}
            	case '\"' => 1
            	case ' ' | '!' | '#' | '$' | '%' | '&' | '\'' | '(' | ')' | '*' | '+' | ',' | '-' | '.' | '/' | '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | ':' | ';' | '<' | '=' | '>' | '?' | '@' | 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' | 'N' | 'O' | 'P' | 'Q' | 'R' | 'S' | 'T' | 'U' | 'V' | 'W' | 'X' | 'Y' | 'Z' | '[' | ']' | '^' | '_' | '`' | 'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p' | 'q' | 'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z' | '{' | '|' | '}' | '~' | '\u00A0' | '\u00A1' | '\u00A2' | '\u00A3' | '\u00A4' | '\u00A5' | '\u00A6' | '\u00A7' | '\u00A8' | '\u00A9' | '\u00AA' | '\u00AB' | '\u00AC' | '\u00AD' | '\u00AE' | '\u00AF' | '\u00B0' | '\u00B1' | '\u00B2' | '\u00B3' | '\u00B4' | '\u00B5' | '\u00B6' | '\u00B7' | '\u00B8' | '\u00B9' | '\u00BA' | '\u00BB' | '\u00BC' | '\u00BD' | '\u00BE' | '\u00BF' | '\u00C0' | '\u00C1' | '\u00C2' | '\u00C3' | '\u00C4' | '\u00C5' | '\u00C6' | '\u00C7' | '\u00C8' | '\u00C9' | '\u00CA' | '\u00CB' | '\u00CC' | '\u00CD' | '\u00CE' | '\u00CF' | '\u00D0' | '\u00D1' | '\u00D2' | '\u00D3' | '\u00D4' | '\u00D5' | '\u00D6' | '\u00D7' | '\u00D8' | '\u00D9' | '\u00DA' | '\u00DB' | '\u00DC' | '\u00DD' | '\u00DE' | '\u00DF' | '\u00E0' | '\u00E1' | '\u00E2' | '\u00E3' | '\u00E4' | '\u00E5' | '\u00E6' | '\u00E7' | '\u00E8' | '\u00E9' | '\u00EA' | '\u00EB' | '\u00EC' | '\u00ED' | '\u00EE' | '\u00EF' | '\u00F0' | '\u00F1' | '\u00F2' | '\u00F3' | '\u00F4' | '\u00F5' | '\u00F6' | '\u00F7' | '\u00F8' | '\u00F9' | '\u00FA' | '\u00FB' | '\u00FC' | '\u00FD' | '\u00FE' | '\u00FF' => 3
            	case _ => -1
            	}

            	    alt5 match {
            		case 1 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:254:30: '\\\"'
            		    	`match`('\"')


            		case 2 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:254:37: '\\\\\\\\'
            		    	`match`("\\\\")



            		case 3 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:254:46: GRAPHIC_CHARACTER
            		    	mGRAPHIC_CHARACTER()



            		case _ =>
            		    if ( cnt5 >= 1 ) loop5_quitflag = true
            		    else {
            	            val eee = new EarlyExitException(5, input)
            	            throw eee
            	      }
            	    }
            	    cnt5+=1
            	}

            	`match`('\\')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "EXTENDED_IDENTIFIER"

    // $ANTLR start "BASED_LITERAL"
    @throws(classOf[RecognitionException])
    final def mBASED_LITERAL():Unit = {
        try {
            var _type = BASED_LITERAL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:256:15: ( INTEGER '#' BASED_INTEGER ( DOT BASED_INTEGER )? '#' ( EXPONENT )? )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:256:17: INTEGER '#' BASED_INTEGER ( DOT BASED_INTEGER )? '#' ( EXPONENT )?
            	mINTEGER()

            	`match`('#')

            	mBASED_INTEGER()

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:256:43: ( DOT BASED_INTEGER )?
            	val alt6 = {
            	val LA6_0 = input.LA(1)

            	if ( (LA6_0=='.') ) 1
            	else -1
            	}
            	alt6 match {
            	    case 1 =>
            	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:256:45: DOT BASED_INTEGER
            	        	mDOT()

            	        	mBASED_INTEGER()



            	    case _ =>
            	}

            	`match`('#')

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:256:70: ( EXPONENT )?
            	val alt7 = {
            	val LA7_0 = input.LA(1)

            	if ( (LA7_0=='e') ) 1
            	else -1
            	}
            	alt7 match {
            	    case 1 =>
            	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:256:70: EXPONENT
            	        	mEXPONENT()



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
    final def mINTEGER_LITERAL():Unit = {
        try {
            var _type = INTEGER_LITERAL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:258:17: ( INTEGER ( EXPONENT )? )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:258:19: INTEGER ( EXPONENT )?
            	mINTEGER()

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:258:27: ( EXPONENT )?
            	val alt8 = {
            	val LA8_0 = input.LA(1)

            	if ( (LA8_0=='e') ) 1
            	else -1
            	}
            	alt8 match {
            	    case 1 =>
            	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:258:27: EXPONENT
            	        	mEXPONENT()



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
    final def mREAL_LITERAL():Unit = {
        try {
            var _type = REAL_LITERAL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:260:14: ( INTEGER DOT INTEGER ( EXPONENT )? )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:260:16: INTEGER DOT INTEGER ( EXPONENT )?
            	mINTEGER()

            	mDOT()

            	mINTEGER()

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:260:38: ( EXPONENT )?
            	val alt9 = {
            	val LA9_0 = input.LA(1)

            	if ( (LA9_0=='e') ) 1
            	else -1
            	}
            	alt9 match {
            	    case 1 =>
            	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:260:38: EXPONENT
            	        	mEXPONENT()



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
    final def mAPOSTROPHE():Unit = {
        try {
            var _type = APOSTROPHE
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:265:12: ( '\\'' ({...}? => ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )? )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:266:2: '\\'' ({...}? => ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )?
            	`match`('\'')

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:266:7: ({...}? => ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\'' )?
            	val alt10 = {
            	val LA10_0 = input.LA(1)

            	if ( ((LA10_0>=' ' && LA10_0<='~')||(LA10_0>='\u00A0' && LA10_0<='\u00FF')) && ((input.LA(2)=='\'' && (input.LA(4)!='\'' || input.LA(3)==',' || input.LA(3)=='|')))) 1
            	else -1
            	}
            	alt10 match {
            	    case 1 =>
            	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:266:8: {...}? => ( '\\\"' | '\\\\' | GRAPHIC_CHARACTER ) '\\''
            	        	if ( !((input.LA(2)=='\'' && (input.LA(4)!='\'' || input.LA(3)==',' || input.LA(3)=='|'))) ) {
            	        	    throw new FailedPredicateException(input, "APOSTROPHE", "input.LA(2)=='\\'' && (input.LA(4)!='\\'' || input.LA(3)==',' || input.LA(3)=='|')")
            	        	}
            	        	if ( (input.LA(1)>=' ' && input.LA(1)<='~')||(input.LA(1)>='\u00A0' && input.LA(1)<='\u00FF') ) {
            	        	    input.consume()

            	        	}
            	        	else {
            	        	    val mse = new MismatchedSetException(null,input)
            	        	    recover(mse)
            	        	    throw mse}

            	        	`match`('\'')

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
    final def mSTRING_LITERAL():Unit = {
        try {
            var _type = STRING_LITERAL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:269:16: ( '\\\"' ( '\\\"\\\"' | '\\\\' | GRAPHIC_CHARACTER )* '\\\"' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:269:18: '\\\"' ( '\\\"\\\"' | '\\\\' | GRAPHIC_CHARACTER )* '\\\"'
            	`match`('\"')

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:269:23: ( '\\\"\\\"' | '\\\\' | GRAPHIC_CHARACTER )*
            	var loop11_quitflag = false
            	while (!loop11_quitflag) {
            	    val alt11 = input.LA(1) match {
            	case '\"' => {
            	val LA11_1 = input.LA(2)

            	if ( (LA11_1=='\"') ) 1

            	else -1
            	}
            	case '\\' => 2
            	case ' ' | '!' | '#' | '$' | '%' | '&' | '\'' | '(' | ')' | '*' | '+' | ',' | '-' | '.' | '/' | '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' | ':' | ';' | '<' | '=' | '>' | '?' | '@' | 'A' | 'B' | 'C' | 'D' | 'E' | 'F' | 'G' | 'H' | 'I' | 'J' | 'K' | 'L' | 'M' | 'N' | 'O' | 'P' | 'Q' | 'R' | 'S' | 'T' | 'U' | 'V' | 'W' | 'X' | 'Y' | 'Z' | '[' | ']' | '^' | '_' | '`' | 'a' | 'b' | 'c' | 'd' | 'e' | 'f' | 'g' | 'h' | 'i' | 'j' | 'k' | 'l' | 'm' | 'n' | 'o' | 'p' | 'q' | 'r' | 's' | 't' | 'u' | 'v' | 'w' | 'x' | 'y' | 'z' | '{' | '|' | '}' | '~' | '\u00A0' | '\u00A1' | '\u00A2' | '\u00A3' | '\u00A4' | '\u00A5' | '\u00A6' | '\u00A7' | '\u00A8' | '\u00A9' | '\u00AA' | '\u00AB' | '\u00AC' | '\u00AD' | '\u00AE' | '\u00AF' | '\u00B0' | '\u00B1' | '\u00B2' | '\u00B3' | '\u00B4' | '\u00B5' | '\u00B6' | '\u00B7' | '\u00B8' | '\u00B9' | '\u00BA' | '\u00BB' | '\u00BC' | '\u00BD' | '\u00BE' | '\u00BF' | '\u00C0' | '\u00C1' | '\u00C2' | '\u00C3' | '\u00C4' | '\u00C5' | '\u00C6' | '\u00C7' | '\u00C8' | '\u00C9' | '\u00CA' | '\u00CB' | '\u00CC' | '\u00CD' | '\u00CE' | '\u00CF' | '\u00D0' | '\u00D1' | '\u00D2' | '\u00D3' | '\u00D4' | '\u00D5' | '\u00D6' | '\u00D7' | '\u00D8' | '\u00D9' | '\u00DA' | '\u00DB' | '\u00DC' | '\u00DD' | '\u00DE' | '\u00DF' | '\u00E0' | '\u00E1' | '\u00E2' | '\u00E3' | '\u00E4' | '\u00E5' | '\u00E6' | '\u00E7' | '\u00E8' | '\u00E9' | '\u00EA' | '\u00EB' | '\u00EC' | '\u00ED' | '\u00EE' | '\u00EF' | '\u00F0' | '\u00F1' | '\u00F2' | '\u00F3' | '\u00F4' | '\u00F5' | '\u00F6' | '\u00F7' | '\u00F8' | '\u00F9' | '\u00FA' | '\u00FB' | '\u00FC' | '\u00FD' | '\u00FE' | '\u00FF' => 3
            	case _ => -1
            	}

            	    alt11 match {
            		case 1 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:269:25: '\\\"\\\"'
            		    	`match`("\"\"")



            		case 2 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:269:34: '\\\\'
            		    	`match`('\\')


            		case 3 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:269:41: GRAPHIC_CHARACTER
            		    	mGRAPHIC_CHARACTER()



            		case _ => loop11_quitflag = true
            	    }
            	}

            	`match`('\"')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "STRING_LITERAL"

    // $ANTLR start "BIT_STRING_LITERAL"
    @throws(classOf[RecognitionException])
    final def mBIT_STRING_LITERAL():Unit = {
        try {
            var _type = BIT_STRING_LITERAL
            var _channel = BaseRecognizer.DEFAULT_TOKEN_CHANNEL
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:271:20: ( ({...}? => INTEGER )? BASE_SPECIFIER '\\\"' ( ( BASED_INTEGER )? | {...}? => ( GRAPHIC_CHARACTER )* ) '\\\"' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:271:22: ({...}? => INTEGER )? BASE_SPECIFIER '\\\"' ( ( BASED_INTEGER )? | {...}? => ( GRAPHIC_CHARACTER )* ) '\\\"'
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:271:22: ({...}? => INTEGER )?
            	val alt12 = {
            	val LA12_0 = input.LA(1)

            	if ( ((LA12_0>='0' && LA12_0<='9')) && ((vhdl2008))) 1
            	else -1
            	}
            	alt12 match {
            	    case 1 =>
            	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:271:23: {...}? => INTEGER
            	        	if ( !((vhdl2008)) ) {
            	        	    throw new FailedPredicateException(input, "BIT_STRING_LITERAL", "vhdl2008")
            	        	}
            	        	mINTEGER()



            	    case _ =>
            	}

            	mBASE_SPECIFIER()

            	`match`('\"')

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:271:66: ( ( BASED_INTEGER )? | {...}? => ( GRAPHIC_CHARACTER )* )
            	val alt15 = {
            	val LA15_0 = input.LA(1)

            	if ( ((LA15_0>='0' && LA15_0<='9')||(LA15_0>='A' && LA15_0<='Z')||(LA15_0>='a' && LA15_0<='z')||(LA15_0>='\u00C0' && LA15_0<='\u00D6')||(LA15_0>='\u00D8' && LA15_0<='\u00F6')||(LA15_0>='\u00F8' && LA15_0<='\u00FF')) ) {
            	val LA15_1 = input.LA(2)

            	if ( (!(((vhdl2008)))) ) 1
            	else if ( ((vhdl2008)) ) 2
            	else {
            	    val nvae = new NoViableAltException("", 15, 1, input)

            	    throw nvae
            	}
            	}
            	else if ( (LA15_0=='\"') ) {
            	val LA15_2 = input.LA(2)

            	if ( (!(((vhdl2008)))) ) 1
            	else if ( ((vhdl2008)) ) 2
            	else {
            	    val nvae = new NoViableAltException("", 15, 2, input)

            	    throw nvae
            	}
            	}
            	else if ( ((LA15_0>=' ' && LA15_0<='!')||(LA15_0>='#' && LA15_0<='/')||(LA15_0>=':' && LA15_0<='@')||LA15_0=='['||(LA15_0>=']' && LA15_0<='`')||(LA15_0>='{' && LA15_0<='~')||(LA15_0>='\u00A0' && LA15_0<='\u00BF')||LA15_0=='\u00D7'||LA15_0=='\u00F7') && ((vhdl2008))) 2
            	else {
            	    val nvae = new NoViableAltException("", 15, 0, input)

            	    throw nvae
            	}
            	}
            	alt15 match {
            	    case 1 =>
            	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:271:67: ( BASED_INTEGER )?
            	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:271:67: ( BASED_INTEGER )?
            	        	val alt13 = {
            	        	val LA13_0 = input.LA(1)

            	        	if ( ((LA13_0>='0' && LA13_0<='9')||(LA13_0>='A' && LA13_0<='Z')||(LA13_0>='a' && LA13_0<='z')||(LA13_0>='\u00C0' && LA13_0<='\u00D6')||(LA13_0>='\u00D8' && LA13_0<='\u00F6')||(LA13_0>='\u00F8' && LA13_0<='\u00FF')) ) 1
            	        	else -1
            	        	}
            	        	alt13 match {
            	        	    case 1 =>
            	        	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:271:67: BASED_INTEGER
            	        	        	mBASED_INTEGER()



            	        	    case _ =>
            	        	}


            	    case 2 =>
            	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:271:84: {...}? => ( GRAPHIC_CHARACTER )*
            	        	if ( !((vhdl2008)) ) {
            	        	    throw new FailedPredicateException(input, "BIT_STRING_LITERAL", "vhdl2008")
            	        	}
            	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:271:98: ( GRAPHIC_CHARACTER )*
            	        	var loop14_quitflag = false
            	        	while (!loop14_quitflag) {
            	        	    val alt14 = {
            	        	val LA14_0 = input.LA(1)

            	        	if ( ((LA14_0>=' ' && LA14_0<='!')||(LA14_0>='#' && LA14_0<='[')||(LA14_0>=']' && LA14_0<='~')||(LA14_0>='\u00A0' && LA14_0<='\u00FF')) ) 1

            	        	else -1
            	        	}
            	        	    alt14 match {
            	        		case 1 =>
            	        		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:271:98: GRAPHIC_CHARACTER
            	        		    	mGRAPHIC_CHARACTER()



            	        		case _ => loop14_quitflag = true
            	        	    }
            	        	}



            	    case _ =>
            	}

            	`match`('\"')




            state.`type` = _type
            state.channel = _channel
        }
        finally {
        }
    }
    // $ANTLR end "BIT_STRING_LITERAL"

    // $ANTLR start "BASE_SPECIFIER"
    @throws(classOf[RecognitionException])
    final def mBASE_SPECIFIER():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:16: ( 'b' | 'o' | 'x' | {...}? => ( 'ub' | 'uo' | 'ux' | 'sb' | 'so' | 'sx' | 'd' ) )
            val alt17 = {
            val LA17_0 = input.LA(1)

            if ( (LA17_0=='b') ) 1
            else if ( (LA17_0=='o') ) 2
            else if ( (LA17_0=='x') ) 3
            else if ( (LA17_0=='d'||LA17_0=='s'||LA17_0=='u') && ((vhdl2008))) 4
            else {
                val nvae = new NoViableAltException("", 17, 0, input)

                throw nvae
            }
            }
            alt17 match {
                case 1 =>
                    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:18: 'b'
                    	`match`('b')


                case 2 =>
                    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:24: 'o'
                    	`match`('o')


                case 3 =>
                    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:30: 'x'
                    	`match`('x')


                case 4 =>
                    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:36: {...}? => ( 'ub' | 'uo' | 'ux' | 'sb' | 'so' | 'sx' | 'd' )
                    	if ( !((vhdl2008)) ) {
                    	    throw new FailedPredicateException(input, "BASE_SPECIFIER", "vhdl2008")
                    	}
                    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:49: ( 'ub' | 'uo' | 'ux' | 'sb' | 'so' | 'sx' | 'd' )
                    	val alt16 = dfa16.predict(input)
                    	alt16 match {
                    	    case 1 =>
                    	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:50: 'ub'
                    	        	`match`("ub")



                    	    case 2 =>
                    	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:57: 'uo'
                    	        	`match`("uo")



                    	    case 3 =>
                    	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:64: 'ux'
                    	        	`match`("ux")



                    	    case 4 =>
                    	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:71: 'sb'
                    	        	`match`("sb")



                    	    case 5 =>
                    	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:78: 'so'
                    	        	`match`("so")



                    	    case 6 =>
                    	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:85: 'sx'
                    	        	`match`("sx")



                    	    case 7 =>
                    	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:274:92: 'd'
                    	        	`match`('d')



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
    final def mBASED_INTEGER():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:277:15: ( EXTENDED_DIGIT ( ( '_' )? EXTENDED_DIGIT )* )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:277:17: EXTENDED_DIGIT ( ( '_' )? EXTENDED_DIGIT )*
            	mEXTENDED_DIGIT()

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:277:32: ( ( '_' )? EXTENDED_DIGIT )*
            	var loop19_quitflag = false
            	while (!loop19_quitflag) {
            	    val alt19 = {
            	val LA19_0 = input.LA(1)

            	if ( ((LA19_0>='0' && LA19_0<='9')||(LA19_0>='A' && LA19_0<='Z')||LA19_0=='_'||(LA19_0>='a' && LA19_0<='z')||(LA19_0>='\u00C0' && LA19_0<='\u00D6')||(LA19_0>='\u00D8' && LA19_0<='\u00F6')||(LA19_0>='\u00F8' && LA19_0<='\u00FF')) ) 1

            	else -1
            	}
            	    alt19 match {
            		case 1 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:277:34: ( '_' )? EXTENDED_DIGIT
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:277:34: ( '_' )?
            		    	val alt18 = {
            		    	val LA18_0 = input.LA(1)

            		    	if ( (LA18_0=='_') ) 1
            		    	else -1
            		    	}
            		    	alt18 match {
            		    	    case 1 =>
            		    	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:277:34: '_'
            		    	        	`match`('_')



            		    	    case _ =>
            		    	}

            		    	mEXTENDED_DIGIT()



            		case _ => loop19_quitflag = true
            	    }
            	}




        }
        finally {
        }
    }
    // $ANTLR end "BASED_INTEGER"

    // $ANTLR start "EXTENDED_DIGIT"
    @throws(classOf[RecognitionException])
    final def mEXTENDED_DIGIT():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:280:16: ( DIGIT | LETTER )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            	if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u00FF') ) {
            	    input.consume()

            	}
            	else {
            	    val mse = new MismatchedSetException(null,input)
            	    recover(mse)
            	    throw mse}




        }
        finally {
        }
    }
    // $ANTLR end "EXTENDED_DIGIT"

    // $ANTLR start "INTEGER"
    @throws(classOf[RecognitionException])
    final def mINTEGER():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:283:9: ( DIGIT ( ( '_' )? DIGIT )* )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:283:11: DIGIT ( ( '_' )? DIGIT )*
            	mDIGIT()

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:283:17: ( ( '_' )? DIGIT )*
            	var loop21_quitflag = false
            	while (!loop21_quitflag) {
            	    val alt21 = {
            	val LA21_0 = input.LA(1)

            	if ( ((LA21_0>='0' && LA21_0<='9')||LA21_0=='_') ) 1

            	else -1
            	}
            	    alt21 match {
            		case 1 =>
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:283:19: ( '_' )? DIGIT
            		    	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:283:19: ( '_' )?
            		    	val alt20 = {
            		    	val LA20_0 = input.LA(1)

            		    	if ( (LA20_0=='_') ) 1
            		    	else -1
            		    	}
            		    	alt20 match {
            		    	    case 1 =>
            		    	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:283:19: '_'
            		    	        	`match`('_')



            		    	    case _ =>
            		    	}

            		    	mDIGIT()



            		case _ => loop21_quitflag = true
            	    }
            	}




        }
        finally {
        }
    }
    // $ANTLR end "INTEGER"

    // $ANTLR start "EXPONENT"
    @throws(classOf[RecognitionException])
    final def mEXPONENT():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:286:10: ( 'e' ( '+' | '-' )? INTEGER )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:286:12: 'e' ( '+' | '-' )? INTEGER
            	`match`('e')

            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:286:16: ( '+' | '-' )?
            	val alt22 = {
            	val LA22_0 = input.LA(1)

            	if ( (LA22_0=='+'||LA22_0=='-') ) 1
            	else -1
            	}
            	alt22 match {
            	    case 1 =>
            	        	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            	        	if ( input.LA(1)=='+'||input.LA(1)=='-' ) {
            	        	    input.consume()

            	        	}
            	        	else {
            	        	    val mse = new MismatchedSetException(null,input)
            	        	    recover(mse)
            	        	    throw mse}



            	    case _ =>
            	}

            	mINTEGER()




        }
        finally {
        }
    }
    // $ANTLR end "EXPONENT"

    // $ANTLR start "LETTER_OR_DIGIT"
    @throws(classOf[RecognitionException])
    final def mLETTER_OR_DIGIT():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:289:17: ( LETTER | DIGIT )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            	if ( (input.LA(1)>='0' && input.LA(1)<='9')||(input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u00FF') ) {
            	    input.consume()

            	}
            	else {
            	    val mse = new MismatchedSetException(null,input)
            	    recover(mse)
            	    throw mse}




        }
        finally {
        }
    }
    // $ANTLR end "LETTER_OR_DIGIT"

    // $ANTLR start "LETTER"
    @throws(classOf[RecognitionException])
    final def mLETTER():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:292:8: ( UPPER_CASE_LETTER | LOWER_CASE_LETTER )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            	if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u00FF') ) {
            	    input.consume()

            	}
            	else {
            	    val mse = new MismatchedSetException(null,input)
            	    recover(mse)
            	    throw mse}




        }
        finally {
        }
    }
    // $ANTLR end "LETTER"

    // $ANTLR start "GRAPHIC_CHARACTER"
    @throws(classOf[RecognitionException])
    final def mGRAPHIC_CHARACTER():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:295:19: ( UPPER_CASE_LETTER | DIGIT | SPECIAL_CHARACTER | SPACE_CHARACTER | LOWER_CASE_LETTER | OTHER_SPECIAL_CHARACTER )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            	if ( (input.LA(1)>=' ' && input.LA(1)<='!')||(input.LA(1)>='#' && input.LA(1)<='[')||(input.LA(1)>=']' && input.LA(1)<='~')||(input.LA(1)>='\u00A0' && input.LA(1)<='\u00FF') ) {
            	    input.consume()

            	}
            	else {
            	    val mse = new MismatchedSetException(null,input)
            	    recover(mse)
            	    throw mse}




        }
        finally {
        }
    }
    // $ANTLR end "GRAPHIC_CHARACTER"

    // $ANTLR start "UPPER_CASE_LETTER"
    @throws(classOf[RecognitionException])
    final def mUPPER_CASE_LETTER():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:305:19: ( 'A' .. 'Z' | '\\u00c0' .. '\\u00d6' | '\\u00d8' .. '\\u00de' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            	if ( (input.LA(1)>='A' && input.LA(1)<='Z')||(input.LA(1)>='\u00C0' && input.LA(1)<='\u00D6')||(input.LA(1)>='\u00D8' && input.LA(1)<='\u00DE') ) {
            	    input.consume()

            	}
            	else {
            	    val mse = new MismatchedSetException(null,input)
            	    recover(mse)
            	    throw mse}




        }
        finally {
        }
    }
    // $ANTLR end "UPPER_CASE_LETTER"

    // $ANTLR start "LOWER_CASE_LETTER"
    @throws(classOf[RecognitionException])
    final def mLOWER_CASE_LETTER():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:309:19: ( 'a' .. 'z' | '\\u00df' .. '\\u00f6' | '\\u00f8' .. '\\u00ff' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            	if ( (input.LA(1)>='a' && input.LA(1)<='z')||(input.LA(1)>='\u00DF' && input.LA(1)<='\u00F6')||(input.LA(1)>='\u00F8' && input.LA(1)<='\u00FF') ) {
            	    input.consume()

            	}
            	else {
            	    val mse = new MismatchedSetException(null,input)
            	    recover(mse)
            	    throw mse}




        }
        finally {
        }
    }
    // $ANTLR end "LOWER_CASE_LETTER"

    // $ANTLR start "DIGIT"
    @throws(classOf[RecognitionException])
    final def mDIGIT():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:312:7: ( '0' .. '9' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:312:9: '0' .. '9'
            	matchRange('0','9'); 



        }
        finally {
        }
    }
    // $ANTLR end "DIGIT"

    // $ANTLR start "SPECIAL_CHARACTER"
    @throws(classOf[RecognitionException])
    final def mSPECIAL_CHARACTER():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:315:19: ( '#' | '&' | '\\'' | '(' | ')' | '*' | '+' | ',' | '-' | '.' | '/' | ':' | ';' | '<' | '=' | '>' | '[' | ']' | '_' | '|' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            	if ( input.LA(1)=='#'||(input.LA(1)>='&' && input.LA(1)<='/')||(input.LA(1)>=':' && input.LA(1)<='>')||input.LA(1)=='['||input.LA(1)==']'||input.LA(1)=='_'||input.LA(1)=='|' ) {
            	    input.consume()

            	}
            	else {
            	    val mse = new MismatchedSetException(null,input)
            	    recover(mse)
            	    throw mse}




        }
        finally {
        }
    }
    // $ANTLR end "SPECIAL_CHARACTER"

    // $ANTLR start "SPACE_CHARACTER"
    @throws(classOf[RecognitionException])
    final def mSPACE_CHARACTER():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:320:17: ( ' ' | '\\u00a0' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            	if ( input.LA(1)==' '||input.LA(1)=='\u00A0' ) {
            	    input.consume()

            	}
            	else {
            	    val mse = new MismatchedSetException(null,input)
            	    recover(mse)
            	    throw mse}




        }
        finally {
        }
    }
    // $ANTLR end "SPACE_CHARACTER"

    // $ANTLR start "OTHER_SPECIAL_CHARACTER"
    @throws(classOf[RecognitionException])
    final def mOTHER_SPECIAL_CHARACTER():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:323:25: ( '!' | '$' | '%' | '@' | '?' | '^' | '`' | '{' | '}' | '~' | '\\u00a1' .. '\\u00bf' | '\\u00d7' | '\\u00f7' )
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:
            	if ( input.LA(1)=='!'||(input.LA(1)>='$' && input.LA(1)<='%')||(input.LA(1)>='?' && input.LA(1)<='@')||input.LA(1)=='^'||input.LA(1)=='`'||input.LA(1)=='{'||(input.LA(1)>='}' && input.LA(1)<='~')||(input.LA(1)>='\u00A1' && input.LA(1)<='\u00BF')||input.LA(1)=='\u00D7'||input.LA(1)=='\u00F7' ) {
            	    input.consume()

            	}
            	else {
            	    val mse = new MismatchedSetException(null,input)
            	    recover(mse)
            	    throw mse}




        }
        finally {
        }
    }
    // $ANTLR end "OTHER_SPECIAL_CHARACTER"

    // $ANTLR start "CHARACTER_LITERAL"
    @throws(classOf[RecognitionException])
    final def mCHARACTER_LITERAL():Unit = {
        try {
            // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:328:19: ()
            	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:328:21: 


        }
        finally {
        }
    }
    // $ANTLR end "CHARACTER_LITERAL"

    @throws(classOf[RecognitionException])
    final def mTokens():Unit = {
        // .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:8: ( ABS | ACCESS | AFTER | ALIAS | ALL | AND | ARCHITECTURE | ARRAY | ASSERT | ATTRIBUTE | BEGIN | BLOCK | BODY | BUFFER | BUS | CASE | COMPONENT | CONFIGURATION | CONSTANT | DISCONNECT | DOWNTO | ELSE | ELSIF | END | ENTITY | EXIT | FILE | FOR | FUNCTION | GENERATE | GENERIC | GROUP | GUARDED | IF | IMPURE | IN | INERTIAL | INOUT | IS | LABEL | LIBRARY | LINKAGE | LITERAL | LOOP | MAP | MOD | NAND | NEW | NEXT | NOR | NOT | NULL | OF | ON | OPEN | OR | OTHERS | OUT | PACKAGE | PORT | POSTPONED | PROCEDURE | PROCESS | PROTECTED | PURE | RANGE | RECORD | REGISTER | REJECT | REM | REPORT | RETURN | ROL | ROR | SELECT | SEVERITY | SHARED | SIGNAL | SLA | SLL | SRA | SRL | SUBTYPE | THEN | TO | TRANSPORT | TYPE | UNAFFECTED | UNITS | UNTIL | USE | VARIABLE | WAIT | WHEN | WHILE | WITH | XNOR | XOR | AMS_ASSIGN | VAR_ASSIGN | BOX | DBLQUOTE | COMMA | SEMICOLON | LPAREN | RPAREN | LBRACKET | RBRACKET | COLON | DOT | AMPERSAND | BAR | ARROW | AT | QMARK | DLT | DGT | CIRCUMFLEX | DOUBLESTAR | MUL | DIV | PLUS | MINUS | EQ | NEQ | LT | GT | LEQ | GEQ | MEQ | MNEQ | MLT | MGT | MLEQ | MGEQ | CONDITION_OPERATOR | CONTEXT | FORCE | PARAMETER | RELEASE | DEFAULT | NATURE | TERMINAL | QUANTITY | TOLERANCE | ACROSS | THROUGH | SPECTRUM | NOISE | SUBNATURE | LIMIT | REFERENCE | BREAK | PROCEDURAL | WS | NESTED_ML_COMMENT | LINECOMMENT | BASIC_IDENTIFIER | EXTENDED_IDENTIFIER | BASED_LITERAL | INTEGER_LITERAL | REAL_LITERAL | APOSTROPHE | STRING_LITERAL | BIT_STRING_LITERAL )
        val alt23 = dfa23.predict(input)
        alt23 match {
            case 1 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:10: ABS
                	mABS()


            case 2 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:14: ACCESS
                	mACCESS()


            case 3 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:21: AFTER
                	mAFTER()


            case 4 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:27: ALIAS
                	mALIAS()


            case 5 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:33: ALL
                	mALL()


            case 6 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:37: AND
                	mAND()


            case 7 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:41: ARCHITECTURE
                	mARCHITECTURE()


            case 8 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:54: ARRAY
                	mARRAY()


            case 9 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:60: ASSERT
                	mASSERT()


            case 10 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:67: ATTRIBUTE
                	mATTRIBUTE()


            case 11 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:77: BEGIN
                	mBEGIN()


            case 12 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:83: BLOCK
                	mBLOCK()


            case 13 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:89: BODY
                	mBODY()


            case 14 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:94: BUFFER
                	mBUFFER()


            case 15 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:101: BUS
                	mBUS()


            case 16 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:105: CASE
                	mCASE()


            case 17 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:110: COMPONENT
                	mCOMPONENT()


            case 18 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:120: CONFIGURATION
                	mCONFIGURATION()


            case 19 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:134: CONSTANT
                	mCONSTANT()


            case 20 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:143: DISCONNECT
                	mDISCONNECT()


            case 21 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:154: DOWNTO
                	mDOWNTO()


            case 22 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:161: ELSE
                	mELSE()


            case 23 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:166: ELSIF
                	mELSIF()


            case 24 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:172: END
                	mEND()


            case 25 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:176: ENTITY
                	mENTITY()


            case 26 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:183: EXIT
                	mEXIT()


            case 27 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:188: FILE
                	mFILE()


            case 28 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:193: FOR
                	mFOR()


            case 29 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:197: FUNCTION
                	mFUNCTION()


            case 30 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:206: GENERATE
                	mGENERATE()


            case 31 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:215: GENERIC
                	mGENERIC()


            case 32 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:223: GROUP
                	mGROUP()


            case 33 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:229: GUARDED
                	mGUARDED()


            case 34 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:237: IF
                	mIF()


            case 35 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:240: IMPURE
                	mIMPURE()


            case 36 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:247: IN
                	mIN()


            case 37 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:250: INERTIAL
                	mINERTIAL()


            case 38 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:259: INOUT
                	mINOUT()


            case 39 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:265: IS
                	mIS()


            case 40 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:268: LABEL
                	mLABEL()


            case 41 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:274: LIBRARY
                	mLIBRARY()


            case 42 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:282: LINKAGE
                	mLINKAGE()


            case 43 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:290: LITERAL
                	mLITERAL()


            case 44 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:298: LOOP
                	mLOOP()


            case 45 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:303: MAP
                	mMAP()


            case 46 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:307: MOD
                	mMOD()


            case 47 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:311: NAND
                	mNAND()


            case 48 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:316: NEW
                	mNEW()


            case 49 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:320: NEXT
                	mNEXT()


            case 50 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:325: NOR
                	mNOR()


            case 51 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:329: NOT
                	mNOT()


            case 52 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:333: NULL
                	mNULL()


            case 53 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:338: OF
                	mOF()


            case 54 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:341: ON
                	mON()


            case 55 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:344: OPEN
                	mOPEN()


            case 56 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:349: OR
                	mOR()


            case 57 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:352: OTHERS
                	mOTHERS()


            case 58 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:359: OUT
                	mOUT()


            case 59 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:363: PACKAGE
                	mPACKAGE()


            case 60 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:371: PORT
                	mPORT()


            case 61 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:376: POSTPONED
                	mPOSTPONED()


            case 62 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:386: PROCEDURE
                	mPROCEDURE()


            case 63 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:396: PROCESS
                	mPROCESS()


            case 64 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:404: PROTECTED
                	mPROTECTED()


            case 65 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:414: PURE
                	mPURE()


            case 66 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:419: RANGE
                	mRANGE()


            case 67 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:425: RECORD
                	mRECORD()


            case 68 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:432: REGISTER
                	mREGISTER()


            case 69 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:441: REJECT
                	mREJECT()


            case 70 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:448: REM
                	mREM()


            case 71 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:452: REPORT
                	mREPORT()


            case 72 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:459: RETURN
                	mRETURN()


            case 73 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:466: ROL
                	mROL()


            case 74 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:470: ROR
                	mROR()


            case 75 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:474: SELECT
                	mSELECT()


            case 76 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:481: SEVERITY
                	mSEVERITY()


            case 77 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:490: SHARED
                	mSHARED()


            case 78 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:497: SIGNAL
                	mSIGNAL()


            case 79 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:504: SLA
                	mSLA()


            case 80 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:508: SLL
                	mSLL()


            case 81 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:512: SRA
                	mSRA()


            case 82 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:516: SRL
                	mSRL()


            case 83 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:520: SUBTYPE
                	mSUBTYPE()


            case 84 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:528: THEN
                	mTHEN()


            case 85 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:533: TO
                	mTO()


            case 86 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:536: TRANSPORT
                	mTRANSPORT()


            case 87 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:546: TYPE
                	mTYPE()


            case 88 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:551: UNAFFECTED
                	mUNAFFECTED()


            case 89 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:562: UNITS
                	mUNITS()


            case 90 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:568: UNTIL
                	mUNTIL()


            case 91 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:574: USE
                	mUSE()


            case 92 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:578: VARIABLE
                	mVARIABLE()


            case 93 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:587: WAIT
                	mWAIT()


            case 94 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:592: WHEN
                	mWHEN()


            case 95 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:597: WHILE
                	mWHILE()


            case 96 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:603: WITH
                	mWITH()


            case 97 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:608: XNOR
                	mXNOR()


            case 98 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:613: XOR
                	mXOR()


            case 99 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:617: AMS_ASSIGN
                	mAMS_ASSIGN()


            case 100 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:628: VAR_ASSIGN
                	mVAR_ASSIGN()


            case 101 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:639: BOX
                	mBOX()


            case 102 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:643: DBLQUOTE
                	mDBLQUOTE()


            case 103 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:652: COMMA
                	mCOMMA()


            case 104 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:658: SEMICOLON
                	mSEMICOLON()


            case 105 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:668: LPAREN
                	mLPAREN()


            case 106 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:675: RPAREN
                	mRPAREN()


            case 107 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:682: LBRACKET
                	mLBRACKET()


            case 108 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:691: RBRACKET
                	mRBRACKET()


            case 109 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:700: COLON
                	mCOLON()


            case 110 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:706: DOT
                	mDOT()


            case 111 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:710: AMPERSAND
                	mAMPERSAND()


            case 112 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:720: BAR
                	mBAR()


            case 113 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:724: ARROW
                	mARROW()


            case 114 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:730: AT
                	mAT()


            case 115 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:733: QMARK
                	mQMARK()


            case 116 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:739: DLT
                	mDLT()


            case 117 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:743: DGT
                	mDGT()


            case 118 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:747: CIRCUMFLEX
                	mCIRCUMFLEX()


            case 119 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:758: DOUBLESTAR
                	mDOUBLESTAR()


            case 120 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:769: MUL
                	mMUL()


            case 121 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:773: DIV
                	mDIV()


            case 122 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:777: PLUS
                	mPLUS()


            case 123 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:782: MINUS
                	mMINUS()


            case 124 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:788: EQ
                	mEQ()


            case 125 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:791: NEQ
                	mNEQ()


            case 126 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:795: LT
                	mLT()


            case 127 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:798: GT
                	mGT()


            case 128 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:801: LEQ
                	mLEQ()


            case 129 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:805: GEQ
                	mGEQ()


            case 130 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:809: MEQ
                	mMEQ()


            case 131 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:813: MNEQ
                	mMNEQ()


            case 132 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:818: MLT
                	mMLT()


            case 133 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:822: MGT
                	mMGT()


            case 134 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:826: MLEQ
                	mMLEQ()


            case 135 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:831: MGEQ
                	mMGEQ()


            case 136 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:836: CONDITION_OPERATOR
                	mCONDITION_OPERATOR()


            case 137 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:855: CONTEXT
                	mCONTEXT()


            case 138 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:863: FORCE
                	mFORCE()


            case 139 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:869: PARAMETER
                	mPARAMETER()


            case 140 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:879: RELEASE
                	mRELEASE()


            case 141 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:887: DEFAULT
                	mDEFAULT()


            case 142 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:895: NATURE
                	mNATURE()


            case 143 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:902: TERMINAL
                	mTERMINAL()


            case 144 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:911: QUANTITY
                	mQUANTITY()


            case 145 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:920: TOLERANCE
                	mTOLERANCE()


            case 146 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:930: ACROSS
                	mACROSS()


            case 147 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:937: THROUGH
                	mTHROUGH()


            case 148 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:945: SPECTRUM
                	mSPECTRUM()


            case 149 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:954: NOISE
                	mNOISE()


            case 150 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:960: SUBNATURE
                	mSUBNATURE()


            case 151 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:970: LIMIT
                	mLIMIT()


            case 152 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:976: REFERENCE
                	mREFERENCE()


            case 153 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:986: BREAK
                	mBREAK()


            case 154 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:992: PROCEDURAL
                	mPROCEDURAL()


            case 155 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:1003: WS
                	mWS()


            case 156 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:1006: NESTED_ML_COMMENT
                	mNESTED_ML_COMMENT()


            case 157 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:1024: LINECOMMENT
                	mLINECOMMENT()


            case 158 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:1036: BASIC_IDENTIFIER
                	mBASIC_IDENTIFIER()


            case 159 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:1053: EXTENDED_IDENTIFIER
                	mEXTENDED_IDENTIFIER()


            case 160 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:1073: BASED_LITERAL
                	mBASED_LITERAL()


            case 161 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:1087: INTEGER_LITERAL
                	mINTEGER_LITERAL()


            case 162 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:1103: REAL_LITERAL
                	mREAL_LITERAL()


            case 163 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:1116: APOSTROPHE
                	mAPOSTROPHE()


            case 164 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:1127: STRING_LITERAL
                	mSTRING_LITERAL()


            case 165 =>
                	// .\\src\\main\\scala\\at\\jku\\ssw\\openvc\\parser\\Lexer.g:1:1142: BIT_STRING_LITERAL
                	mBIT_STRING_LITERAL()



            case _ =>
        }

    }

    private val dfa16 = new DFA16(this)
    private val dfa23 = new DFA23(this)

    final private class DFA16(baseRecognizer: BaseRecognizer) extends DFA {
        val eotS = "\12\uffff"
        val eofS = "\12\uffff"
        val minS = "\1\144\2\142\7\uffff"
        val maxS = "\1\165\2\170\7\uffff"
        val acceptS = "\3\uffff\1\7\1\1\1\2\1\3\1\4\1\5\1\6"
        val specialS = "\12\uffff}>"
        val transitionS = Array("\1\3\16\uffff\1\2\1\uffff\1\1",
    "\1\4\14\uffff\1\5\10\uffff\1\6",
    "\1\7\14\uffff\1\10\10\uffff\1\11",
    "",
    "",
    "",
    "",
    "",
    "",
    "")

        this.recognizer = baseRecognizer
        this.decisionNumber = 16
        this.eot = DFA.unpackEncodedString(eotS)
        this.eof = DFA.unpackEncodedString(eofS)
        this.min = DFA.unpackEncodedStringToUnsignedChars(minS)
        this.max = DFA.unpackEncodedStringToUnsignedChars(maxS)
        this.accept = DFA.unpackEncodedString(acceptS)
        this.special = DFA.unpackEncodedString(specialS)
        this.transition = new Array[Array[Short]](transitionS.length)
        for (i <- this.transition.indices) {
            this.transition(i) = DFA.unpackEncodedString(transitionS(i))
        }

        override val getDescription = "274:49: ( 'ub' | 'uo' | 'ux' | 'sb' | 'so' | 'sx' | 'd' )"
    }
    final private class DFA23(baseRecognizer: BaseRecognizer) extends DFA {
        val eotS = "\1\uffff\24\54\1\u0083\1\u0085\1\u0089\1\u008a\12\uffff"+
        "\1\u0091\1\u0094\1\uffff\1\u0096\1\u0099\1\uffff\1\u009b\1\54\3"+
        "\uffff\1\u009d\1\uffff\15\54\1\uffff\5\54\1\uffff\11\54\1\u00c3"+
        "\1\54\1\u00c7\1\u00c8\11\54\1\u00d9\1\u00da\1\54\1\u00dc\24\54\1"+
        "\u00fd\16\54\15\uffff\1\u010d\1\u010f\14\uffff\1\54\2\uffff\1\u009d"+
        "\2\uffff\1\u0111\4\54\1\u0116\1\u0117\10\54\1\u0120\10\54\1\u012c"+
        "\3\54\1\u0131\4\54\1\uffff\3\54\2\uffff\6\54\1\u013f\1\u0140\2\54"+
        "\1\u0143\1\54\1\u0145\1\u0146\2\54\2\uffff\1\54\1\uffff\1\54\1\u014b"+
        "\12\54\1\u0157\4\54\1\u015c\1\u015d\4\54\1\u0162\1\u0163\1\u0164"+
        "\1\u0165\5\54\1\uffff\6\54\1\u0172\6\54\1\u0179\4\uffff\1\54\1\uffff"+
        "\4\54\2\uffff\6\54\1\u0185\1\54\1\uffff\1\54\1\u0188\7\54\1\u0190"+
        "\1\54\1\uffff\1\54\1\u0193\1\u0194\1\54\1\uffff\14\54\1\u01a2\2"+
        "\uffff\1\u01a3\1\54\1\uffff\1\u01a5\2\uffff\1\54\1\u01a7\1\u01a8"+
        "\1\54\1\uffff\2\54\1\u01ac\3\54\1\u01b0\4\54\1\uffff\4\54\2\uffff"+
        "\4\54\4\uffff\3\54\1\u01c0\3\54\1\u01c4\4\54\1\uffff\1\54\1\u01ca"+
        "\1\u01cb\1\54\1\u01cd\1\u01ce\1\uffff\3\54\1\u01d2\1\u01d3\1\54"+
        "\1\u01d5\2\54\1\u01d8\1\u01d9\1\uffff\1\54\1\u01db\1\uffff\7\54"+
        "\1\uffff\1\u01e3\1\54\2\uffff\1\u01e5\2\54\1\u01e9\3\54\1\u01ed"+
        "\1\u01ee\3\54\1\u01f2\2\uffff\1\54\1\uffff\1\u01f4\2\uffff\3\54"+
        "\1\uffff\3\54\1\uffff\1\u01fc\16\54\1\uffff\3\54\1\uffff\2\54\1"+
        "\u0210\1\u0211\1\54\2\uffff\1\u0213\2\uffff\1\54\1\u0215\1\u0216"+
        "\2\uffff\1\54\1\uffff\1\u0218\1\54\2\uffff\1\u021a\1\uffff\5\54"+
        "\1\u0221\1\54\1\uffff\1\u0223\1\uffff\3\54\1\uffff\1\54\1\u0229"+
        "\1\54\2\uffff\3\54\1\uffff\1\u022f\1\uffff\1\u0231\6\54\1\uffff"+
        "\1\u0238\1\54\1\u023a\1\u023b\1\u023c\2\54\1\u023f\1\54\1\u0241"+
        "\1\u0242\10\54\2\uffff\1\54\1\uffff\1\54\2\uffff\1\54\1\uffff\1"+
        "\54\2\uffff\3\54\1\u0253\1\54\1\uffff\1\u0255\2\uffff\2\54\1\u0258"+
        "\1\u0259\1\uffff\1\54\1\u025b\1\u025c\1\u025d\4\uffff\1\u025f\3"+
        "\54\1\u0263\1\54\1\uffff\1\54\3\uffff\1\u0266\1\54\1\uffff\1\54"+
        "\2\uffff\1\u0269\2\54\1\u026c\6\54\1\uffff\4\54\1\u0277\1\uffff"+
        "\1\54\1\uffff\1\u027b\1\u027c\2\uffff\1\u027d\5\uffff\3\54\1\uffff"+
        "\1\54\1\u0283\1\uffff\1\54\1\u0286\1\uffff\1\54\1\u0288\1\uffff"+
        "\2\54\1\u028c\1\54\1\u028e\1\u028f\1\54\1\u0291\1\u0292\1\54\2\uffff"+
        "\1\54\4\uffff\1\u0295\1\u0296\1\u0297\1\54\1\u0299\2\uffff\1\u029a"+
        "\1\uffff\1\u029b\2\uffff\1\u029d\1\u029e\1\uffff\1\54\2\uffff\1"+
        "\54\2\uffff\1\54\1\u02a4\3\uffff\1\u02a6\7\uffff\1\u02aa\1\uffff"+
        "\2\54\7\uffff\1\u02ae\1\54\2\uffff\1\u02b0\1\uffff"
        val eofS = "\u02b1\uffff"
        val minS = "\1\11\1\142\1\42\1\141\1\42\1\154\1\151\1\145\1\146"+
        "\3\141\1\42\2\141\1\142\1\145\1\142\2\141\1\42\2\75\1\74\1\40\12"+
        "\uffff\1\57\1\75\1\uffff\2\52\1\uffff\1\55\1\165\3\uffff\1\43\1"+
        "\uffff\1\163\1\143\1\164\1\151\1\144\1\143\1\163\1\164\1\147\1\157"+
        "\1\144\1\146\1\145\1\uffff\1\163\1\155\1\163\1\167\1\146\1\uffff"+
        "\1\163\1\144\1\151\1\154\1\162\2\156\1\157\1\141\1\60\1\160\2\60"+
        "\2\142\1\157\1\160\1\144\1\156\1\167\1\151\1\154\2\60\1\145\1\60"+
        "\1\150\1\164\1\143\1\162\1\157\1\162\1\156\1\143\2\154\1\141\1\147"+
        "\2\141\1\142\1\145\3\42\1\145\1\60\1\141\1\160\1\162\1\141\1\145"+
        "\3\42\1\162\1\151\1\145\1\164\1\157\1\162\15\uffff\2\75\14\uffff"+
        "\1\141\1\uffff\1\60\1\43\2\uffff\1\60\1\145\1\157\1\145\1\141\2"+
        "\60\1\150\1\141\1\145\1\162\1\151\1\143\1\171\1\146\1\60\1\141\1"+
        "\145\1\160\1\146\1\143\1\156\1\141\1\145\1\60\1\151\1\164\1\145"+
        "\1\60\1\143\1\145\1\165\1\162\1\uffff\1\165\1\162\1\165\2\uffff"+
        "\1\145\1\162\1\153\1\145\1\151\1\160\2\60\1\144\1\165\1\60\1\164"+
        "\2\60\1\163\1\154\2\uffff\1\156\1\uffff\1\145\1\60\1\153\1\141\2"+
        "\164\1\143\1\145\1\147\1\157\1\151\1\145\1\60\1\157\1\165\2\145"+
        "\2\60\2\145\1\162\1\156\4\60\1\156\1\143\1\156\1\157\1\145\1\uffff"+
        "\1\156\1\145\1\155\1\146\1\164\1\151\1\60\1\151\1\164\1\156\1\154"+
        "\1\150\1\162\1\60\4\uffff\1\156\1\uffff\2\163\1\162\1\163\2\uffff"+
        "\1\151\1\171\1\162\1\151\1\156\1\153\1\60\1\145\1\uffff\1\153\1"+
        "\60\1\157\1\151\1\164\1\145\1\157\1\164\1\165\1\60\1\146\1\uffff"+
        "\1\164\2\60\1\145\1\uffff\1\164\1\162\1\160\1\144\1\162\2\164\1"+
        "\154\2\141\1\162\1\164\1\60\2\uffff\1\60\1\162\1\uffff\1\60\2\uffff"+
        "\1\145\2\60\1\162\1\uffff\1\141\1\155\1\60\1\160\2\145\1\60\1\145"+
        "\1\162\1\163\1\143\1\uffff\2\162\1\141\1\162\2\uffff\1\143\1\162"+
        "\1\145\1\141\4\uffff\1\171\1\141\1\164\1\60\1\165\1\162\1\163\1"+
        "\60\1\151\1\146\1\163\1\154\1\uffff\1\141\2\60\1\145\2\60\1\uffff"+
        "\1\164\2\163\2\60\1\164\1\60\1\164\1\142\2\60\1\uffff\1\162\1\60"+
        "\1\uffff\1\156\1\147\1\141\1\170\1\156\1\157\1\154\1\uffff\1\60"+
        "\1\171\2\uffff\1\60\1\151\1\141\1\60\2\145\1\151\2\60\1\162\1\147"+
        "\1\141\1\60\2\uffff\1\145\1\uffff\1\60\2\uffff\1\163\1\147\1\145"+
        "\1\uffff\1\157\1\144\1\143\1\uffff\1\60\1\144\3\164\1\156\1\163"+
        "\1\145\1\164\1\151\1\144\1\154\1\160\1\164\1\162\1\uffff\1\147\1"+
        "\141\1\160\1\uffff\1\156\1\145\2\60\1\142\2\uffff\1\60\2\uffff\1"+
        "\151\2\60\2\uffff\1\145\1\uffff\1\60\1\165\2\uffff\1\60\1\0\1\145"+
        "\1\165\1\156\1\164\1\156\1\60\1\164\1\uffff\1\60\1\0\1\157\1\164"+
        "\1\143\1\uffff\1\144\1\60\1\141\2\uffff\1\171\1\145\1\154\1\0\1"+
        "\60\1\0\1\60\1\145\1\164\1\156\1\165\1\163\1\164\1\uffff\1\60\1"+
        "\145\3\60\1\145\1\156\1\60\1\164\2\60\1\145\2\165\1\150\1\156\1"+
        "\157\1\141\1\143\2\uffff\1\154\1\uffff\1\164\1\uffff\1\0\1\143\1"+
        "\uffff\1\164\2\uffff\1\156\1\162\1\164\1\60\1\145\1\uffff\1\60\2"+
        "\uffff\1\156\1\145\2\60\1\uffff\1\154\3\60\1\uffff\1\0\2\uffff\1"+
        "\60\2\145\1\162\1\60\1\145\1\uffff\1\162\3\uffff\1\60\1\143\1\uffff"+
        "\1\171\2\uffff\1\60\1\162\1\155\1\60\1\143\1\162\1\154\1\164\1\145"+
        "\1\171\1\uffff\1\164\1\145\1\164\1\141\1\60\1\0\1\143\1\0\2\60\2"+
        "\uffff\1\60\5\uffff\1\162\1\144\1\141\1\uffff\1\144\1\60\1\0\1\145"+
        "\1\60\1\uffff\1\145\1\60\1\0\1\145\1\164\1\60\1\145\2\60\1\165\2"+
        "\60\1\164\2\uffff\1\164\4\uffff\3\60\1\154\1\60\2\uffff\1\60\1\uffff"+
        "\1\60\1\0\1\uffff\2\60\1\0\1\144\1\uffff\1\0\1\162\2\uffff\1\151"+
        "\1\60\1\0\2\uffff\1\60\1\uffff\2\0\1\uffff\1\0\2\uffff\1\60\1\uffff"+
        "\1\145\1\157\2\uffff\1\0\4\uffff\1\60\1\156\2\uffff\1\60\1\uffff"
        val maxS = "\1\u00ff\1\164\1\165\2\157\1\170\2\165\1\163\2\157\3"+
        "\165\1\157\1\170\1\171\1\170\1\141\1\151\1\157\1\76\1\75\1\76\1"+
        "\u00ff\12\uffff\1\77\1\76\1\uffff\1\52\1\75\1\uffff\1\55\1\165\3"+
        "\uffff\1\170\1\uffff\1\163\1\162\1\164\1\154\1\144\1\162\1\163\1"+
        "\164\1\147\1\157\1\144\1\163\1\145\1\uffff\1\163\1\156\1\163\1\167"+
        "\1\146\1\uffff\1\163\1\164\1\151\1\154\1\162\2\156\1\157\1\141\1"+
        "\u00ff\1\160\2\u00ff\1\142\1\164\1\157\1\160\1\144\1\164\1\170\1"+
        "\164\1\154\2\u00ff\1\145\1\u00ff\1\150\1\164\1\162\1\163\1\157\1"+
        "\162\1\156\1\164\1\162\1\166\1\141\1\147\2\154\1\142\1\145\3\42"+
        "\1\162\1\u00ff\1\141\1\160\1\162\1\164\1\145\3\42\1\162\2\151\1"+
        "\164\1\157\1\162\15\uffff\2\75\14\uffff\1\141\1\uffff\1\71\1\170"+
        "\2\uffff\1\u00ff\1\145\1\157\1\145\1\141\2\u00ff\1\150\1\141\1\145"+
        "\1\162\1\151\1\143\1\171\1\146\1\u00ff\1\141\1\145\1\160\1\164\1"+
        "\143\1\156\1\141\1\151\1\u00ff\1\151\1\164\1\145\1\u00ff\1\143\1"+
        "\145\1\165\1\162\1\uffff\1\165\1\162\1\165\2\uffff\1\145\1\162\1"+
        "\153\1\145\1\151\1\160\2\u00ff\1\144\1\165\1\u00ff\1\164\2\u00ff"+
        "\1\163\1\154\2\uffff\1\156\1\uffff\1\145\1\u00ff\1\153\1\141\3\164"+
        "\1\145\1\147\1\157\1\151\1\145\1\u00ff\1\157\1\165\2\145\2\u00ff"+
        "\2\145\1\162\1\156\4\u00ff\1\164\1\143\1\156\1\157\1\145\1\uffff"+
        "\1\156\1\145\1\155\1\146\1\164\1\151\1\u00ff\1\151\1\164\1\156\1"+
        "\154\1\150\1\162\1\u00ff\4\uffff\1\156\1\uffff\2\163\1\162\1\163"+
        "\2\uffff\1\151\1\171\1\162\1\151\1\156\1\153\1\u00ff\1\145\1\uffff"+
        "\1\153\1\u00ff\1\157\1\151\1\164\1\145\1\157\1\164\1\165\1\u00ff"+
        "\1\146\1\uffff\1\164\2\u00ff\1\145\1\uffff\1\164\1\162\1\160\1\144"+
        "\1\162\2\164\1\154\2\141\1\162\1\164\1\u00ff\2\uffff\1\u00ff\1\162"+
        "\1\uffff\1\u00ff\2\uffff\1\145\2\u00ff\1\162\1\uffff\1\141\1\155"+
        "\1\u00ff\1\160\2\145\1\u00ff\1\145\1\162\1\163\1\143\1\uffff\2\162"+
        "\1\141\1\162\2\uffff\1\143\1\162\1\145\1\141\4\uffff\1\171\1\141"+
        "\1\164\1\u00ff\1\165\1\162\1\163\1\u00ff\1\151\1\146\1\163\1\154"+
        "\1\uffff\1\141\2\u00ff\1\145\2\u00ff\1\uffff\1\164\2\163\2\u00ff"+
        "\1\164\1\u00ff\1\164\1\142\2\u00ff\1\uffff\1\162\1\u00ff\1\uffff"+
        "\1\156\1\147\1\141\1\170\1\156\1\157\1\154\1\uffff\1\u00ff\1\171"+
        "\2\uffff\1\u00ff\2\151\1\u00ff\2\145\1\151\2\u00ff\1\162\1\147\1"+
        "\141\1\u00ff\2\uffff\1\145\1\uffff\1\u00ff\2\uffff\1\163\1\147\1"+
        "\145\1\uffff\1\157\1\163\1\143\1\uffff\1\u00ff\1\144\3\164\1\156"+
        "\1\163\1\145\1\164\1\151\1\144\1\154\1\160\1\164\1\162\1\uffff\1"+
        "\147\1\141\1\160\1\uffff\1\156\1\145\2\u00ff\1\142\2\uffff\1\u00ff"+
        "\2\uffff\1\151\2\u00ff\2\uffff\1\145\1\uffff\1\u00ff\1\165\2\uffff"+
        "\1\u00ff\1\0\1\145\1\165\1\156\1\164\1\156\1\u00ff\1\164\1\uffff"+
        "\1\u00ff\1\0\1\157\1\164\1\143\1\uffff\1\144\1\u00ff\1\141\2\uffff"+
        "\1\171\1\145\1\154\1\0\1\u00ff\1\0\1\u00ff\1\145\1\164\1\156\1\165"+
        "\1\163\1\164\1\uffff\1\u00ff\1\145\3\u00ff\1\145\1\156\1\u00ff\1"+
        "\164\2\u00ff\1\145\2\165\1\150\1\156\1\157\1\141\1\143\2\uffff\1"+
        "\154\1\uffff\1\164\1\uffff\1\0\1\143\1\uffff\1\164\2\uffff\1\156"+
        "\1\162\1\164\1\u00ff\1\145\1\uffff\1\u00ff\2\uffff\1\156\1\145\2"+
        "\u00ff\1\uffff\1\154\3\u00ff\1\uffff\1\0\2\uffff\1\u00ff\2\145\1"+
        "\162\1\u00ff\1\145\1\uffff\1\162\3\uffff\1\u00ff\1\143\1\uffff\1"+
        "\171\2\uffff\1\u00ff\1\162\1\155\1\u00ff\1\143\1\162\1\154\1\164"+
        "\1\145\1\171\1\uffff\1\164\1\145\1\164\1\141\1\u00ff\1\0\1\143\1"+
        "\0\2\u00ff\2\uffff\1\u00ff\5\uffff\1\162\1\144\1\145\1\uffff\1\144"+
        "\1\u00ff\1\0\1\145\1\u00ff\1\uffff\1\145\1\u00ff\1\0\1\145\1\164"+
        "\1\u00ff\1\145\2\u00ff\1\165\2\u00ff\1\164\2\uffff\1\164\4\uffff"+
        "\3\u00ff\1\154\1\u00ff\2\uffff\1\u00ff\1\uffff\1\u00ff\1\0\1\uffff"+
        "\2\u00ff\1\0\1\144\1\uffff\1\0\1\162\2\uffff\1\151\1\u00ff\1\0\2"+
        "\uffff\1\u00ff\1\uffff\2\0\1\uffff\1\0\2\uffff\1\u00ff\1\uffff\1"+
        "\145\1\157\2\uffff\1\0\4\uffff\1\u00ff\1\156\2\uffff\1\u00ff\1\uffff"
        val acceptS = "\31\uffff\1\147\1\150\1\151\1\152\1\153\1\154\1\156"+
        "\1\157\1\160\1\162\2\uffff\1\166\2\uffff\1\172\2\uffff\1\u009b\1"+
        "\u009e\1\u009f\1\uffff\1\u00a3\15\uffff\1\u00a5\5\uffff\1\u00a5"+
        "\75\uffff\1\143\1\161\1\174\1\144\1\155\1\145\1\164\1\u0080\1\176"+
        "\1\146\1\u00a4\1\u0082\1\u0083\2\uffff\1\u0088\1\163\1\165\1\u0081"+
        "\1\177\1\167\1\170\1\175\1\u009c\1\171\1\u009d\1\173\1\uffff\1\u00a1"+
        "\2\uffff\1\u00a0\1\u00a2\41\uffff\1\42\3\uffff\1\44\1\47\20\uffff"+
        "\1\65\1\66\1\uffff\1\70\40\uffff\1\125\16\uffff\1\u0086\1\u0084"+
        "\1\u0087\1\u0085\1\uffff\1\1\4\uffff\1\5\1\6\10\uffff\1\17\13\uffff"+
        "\1\30\4\uffff\1\34\15\uffff\1\55\1\56\2\uffff\1\60\1\uffff\1\62"+
        "\1\63\4\uffff\1\72\13\uffff\1\106\4\uffff\1\111\1\112\4\uffff\1"+
        "\117\1\120\1\121\1\122\14\uffff\1\133\6\uffff\1\142\13\uffff\1\15"+
        "\2\uffff\1\20\7\uffff\1\26\2\uffff\1\32\1\33\15\uffff\1\54\1\57"+
        "\1\uffff\1\61\1\uffff\1\64\1\67\3\uffff\1\74\3\uffff\1\101\17\uffff"+
        "\1\124\3\uffff\1\127\5\uffff\1\135\1\136\1\uffff\1\140\1\141\3\uffff"+
        "\1\3\1\4\1\uffff\1\10\2\uffff\1\13\1\14\11\uffff\1\27\5\uffff\1"+
        "\40\3\uffff\1\46\1\50\15\uffff\1\102\23\uffff\1\131\1\132\1\uffff"+
        "\1\137\1\uffff\1\2\2\uffff\1\11\1\uffff\1\16\1\u0099\5\uffff\1\25"+
        "\1\uffff\1\31\1\u008a\4\uffff\1\43\4\uffff\1\u0097\1\uffff\1\u0095"+
        "\1\71\6\uffff\1\103\1\uffff\1\105\1\107\1\110\2\uffff\1\113\1\uffff"+
        "\1\115\1\116\12\uffff\1\u0092\12\uffff\1\37\1\41\1\uffff\1\51\1"+
        "\52\1\53\1\u008e\1\73\3\uffff\1\77\5\uffff\1\123\15\uffff\1\23\1"+
        "\u0089\1\uffff\1\u008d\1\35\1\36\1\45\5\uffff\1\104\1\u008c\1\uffff"+
        "\1\114\2\uffff\1\u0093\4\uffff\1\134\2\uffff\1\12\1\21\3\uffff\1"+
        "\75\1\76\1\uffff\1\100\2\uffff\1\u0094\1\uffff\1\126\1\u008f\1\uffff"+
        "\1\u0090\2\uffff\1\24\1\u008b\1\uffff\1\u0098\1\u0096\1\u0091\1"+
        "\130\2\uffff\1\u009a\1\7\1\uffff\1\22"
        val specialS = "\4\uffff\1\0\51\uffff\1\2\77\uffff\1\30\1\27\1\32"+
        "\7\uffff\1\25\1\26\1\31\44\uffff\1\1\u013b\uffff\1\7\11\uffff\1"+
        "\4\14\uffff\1\14\1\uffff\1\12\41\uffff\1\17\30\uffff\1\23\43\uffff"+
        "\1\5\1\uffff\1\22\20\uffff\1\21\5\uffff\1\20\33\uffff\1\11\3\uffff"+
        "\1\24\2\uffff\1\15\5\uffff\1\3\4\uffff\1\6\1\13\1\uffff\1\16\10"+
        "\uffff\1\10\12\uffff}>"
        val transitionS = Array("\2\53\2\uffff\1\53\22\uffff\1\53\1\uffff"+
    "\1\30\3\uffff\1\40\1\57\1\33\1\34\1\46\1\50\1\31\1\51\1\37\1\47\12\56"+
    "\1\26\1\32\1\27\1\25\1\44\1\43\1\42\32\54\1\35\1\55\1\36\1\45\2\uffff"+
    "\1\1\1\2\1\3\1\4\1\5\1\6\1\7\1\54\1\10\2\54\1\11\1\12\1\13\1\14\1\15"+
    "\1\52\1\16\1\17\1\20\1\21\1\22\1\23\1\24\2\54\1\uffff\1\41\103\uffff"+
    "\27\54\1\uffff\37\54\1\uffff\10\54",
    "\1\60\1\61\2\uffff\1\62\5\uffff\1\63\1\uffff\1\64\3\uffff\1\65\1\66"+
    "\1\67",
    "\1\75\102\uffff\1\70\6\uffff\1\71\2\uffff\1\72\2\uffff\1\74\2\uffff"+
    "\1\73",
    "\1\76\15\uffff\1\77",
    "\1\103\102\uffff\1\102\3\uffff\1\100\5\uffff\1\101",
    "\1\104\1\uffff\1\105\11\uffff\1\106",
    "\1\107\5\uffff\1\110\5\uffff\1\111",
    "\1\112\14\uffff\1\113\2\uffff\1\114",
    "\1\115\6\uffff\1\116\1\117\4\uffff\1\120",
    "\1\121\7\uffff\1\122\5\uffff\1\123",
    "\1\124\15\uffff\1\125",
    "\1\126\3\uffff\1\127\11\uffff\1\130\5\uffff\1\131",
    "\1\75\103\uffff\1\132\7\uffff\1\133\1\uffff\1\134\1\uffff\1\135\1"+
    "\uffff\1\136\1\137",
    "\1\140\15\uffff\1\141\2\uffff\1\142\2\uffff\1\143",
    "\1\144\3\uffff\1\145\11\uffff\1\146",
    "\1\156\2\uffff\1\147\2\uffff\1\150\1\151\2\uffff\1\152\2\uffff\1\157"+
    "\1\155\1\uffff\1\153\2\uffff\1\154\2\uffff\1\160",
    "\1\165\2\uffff\1\161\6\uffff\1\162\2\uffff\1\163\6\uffff\1\164",
    "\1\170\13\uffff\1\166\1\171\3\uffff\1\167\4\uffff\1\172",
    "\1\173",
    "\1\174\6\uffff\1\175\1\176",
    "\1\75\113\uffff\1\177\1\u0080",
    "\1\u0081\1\u0082",
    "\1\u0084",
    "\1\u0087\1\u0088\1\u0086",
    "\137\u008b\41\uffff\140\u008b",
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
    "\1\u008d\14\uffff\1\u008e\1\u008c\1\u008f\1\u0090",
    "\1\u0093\1\u0092",
    "",
    "\1\u0095",
    "\1\u0098\22\uffff\1\u0097",
    "",
    "\1\u009a",
    "\1\u009c",
    "",
    "",
    "",
    "\1\u00a0\12\uffff\1\u00a1\1\uffff\12\u009f\45\uffff\1\u009e\2\uffff"+
    "\1\103\1\uffff\1\103\12\uffff\1\103\3\uffff\1\103\1\uffff\1\103\2\uffff"+
    "\1\103",
    "",
    "\1\u00a2",
    "\1\u00a3\16\uffff\1\u00a4",
    "\1\u00a5",
    "\1\u00a6\2\uffff\1\u00a7",
    "\1\u00a8",
    "\1\u00a9\16\uffff\1\u00aa",
    "\1\u00ab",
    "\1\u00ac",
    "\1\u00ad",
    "\1\u00ae",
    "\1\u00af",
    "\1\u00b0\14\uffff\1\u00b1",
    "\1\u00b2",
    "",
    "\1\u00b3",
    "\1\u00b4\1\u00b5",
    "\1\u00b6",
    "\1\u00b7",
    "\1\u00b8",
    "",
    "\1\u00b9",
    "\1\u00ba\17\uffff\1\u00bb",
    "\1\u00bc",
    "\1\u00bd",
    "\1\u00be",
    "\1\u00bf",
    "\1\u00c0",
    "\1\u00c1",
    "\1\u00c2",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u00c4",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\4\54\1\u00c5\11\54\1\u00c6"+
    "\13\54\105\uffff\27\54\1\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u00c9",
    "\1\u00ca\12\uffff\1\u00cd\1\u00cb\5\uffff\1\u00cc",
    "\1\u00ce",
    "\1\u00cf",
    "\1\u00d0",
    "\1\u00d1\5\uffff\1\u00d2",
    "\1\u00d3\1\u00d4",
    "\1\u00d7\10\uffff\1\u00d5\1\uffff\1\u00d6",
    "\1\u00d8",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u00db",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u00dd",
    "\1\u00de",
    "\1\u00df\16\uffff\1\u00e0",
    "\1\u00e1\1\u00e2",
    "\1\u00e3",
    "\1\u00e4",
    "\1\u00e5",
    "\1\u00e6\2\uffff\1\u00ed\1\u00e7\2\uffff\1\u00e8\1\uffff\1\u00ec\1"+
    "\u00e9\2\uffff\1\u00ea\3\uffff\1\u00eb",
    "\1\u00ee\5\uffff\1\u00ef",
    "\1\u00f0\11\uffff\1\u00f1",
    "\1\u00f2",
    "\1\u00f3",
    "\1\u00f4\12\uffff\1\u00f5",
    "\1\u00f6\12\uffff\1\u00f7",
    "\1\u00f8",
    "\1\u00f9",
    "\1\103",
    "\1\103",
    "\1\103",
    "\1\u00fa\14\uffff\1\u00fb",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\13\54\1\u00fc\16\54\105"+
    "\uffff\27\54\1\uffff\37\54\1\uffff\10\54",
    "\1\u00fe",
    "\1\u00ff",
    "\1\u0100",
    "\1\u0101\7\uffff\1\u0102\12\uffff\1\u0103",
    "\1\u0104",
    "\1\103",
    "\1\103",
    "\1\103",
    "\1\u0105",
    "\1\u0106",
    "\1\u0107\3\uffff\1\u0108",
    "\1\u0109",
    "\1\u010a",
    "\1\u010b",
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
    "\1\u010c",
    "\1\u010e",
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
    "\1\u0110",
    "",
    "\12\u009f",
    "\1\u00a0\12\uffff\1\u00a1\1\uffff\12\u009f\45\uffff\1\u009e\2\uffff"+
    "\1\103\1\uffff\1\103\12\uffff\1\103\3\uffff\1\103\1\uffff\1\103\2\uffff"+
    "\1\103",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0112",
    "\1\u0113",
    "\1\u0114",
    "\1\u0115",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0118",
    "\1\u0119",
    "\1\u011a",
    "\1\u011b",
    "\1\u011c",
    "\1\u011d",
    "\1\u011e",
    "\1\u011f",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0121",
    "\1\u0122",
    "\1\u0123",
    "\1\u0124\14\uffff\1\u0125\1\u0126",
    "\1\u0127",
    "\1\u0128",
    "\1\u0129",
    "\1\u012a\3\uffff\1\u012b",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u012d",
    "\1\u012e",
    "\1\u012f",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\2\54\1\u0130\27\54\105\uffff"+
    "\27\54\1\uffff\37\54\1\uffff\10\54",
    "\1\u0132",
    "\1\u0133",
    "\1\u0134",
    "\1\u0135",
    "",
    "\1\u0136",
    "\1\u0137",
    "\1\u0138",
    "",
    "",
    "\1\u0139",
    "\1\u013a",
    "\1\u013b",
    "\1\u013c",
    "\1\u013d",
    "\1\u013e",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0141",
    "\1\u0142",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0144",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0147",
    "\1\u0148",
    "",
    "",
    "\1\u0149",
    "",
    "\1\u014a",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u014c",
    "\1\u014d",
    "\1\u014e",
    "\1\u014f",
    "\1\u0150\20\uffff\1\u0151",
    "\1\u0152",
    "\1\u0153",
    "\1\u0154",
    "\1\u0155",
    "\1\u0156",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0158",
    "\1\u0159",
    "\1\u015a",
    "\1\u015b",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u015e",
    "\1\u015f",
    "\1\u0160",
    "\1\u0161",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0167\5\uffff\1\u0166",
    "\1\u0168",
    "\1\u0169",
    "\1\u016a",
    "\1\u016b",
    "",
    "\1\u016c",
    "\1\u016d",
    "\1\u016e",
    "\1\u016f",
    "\1\u0170",
    "\1\u0171",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0173",
    "\1\u0174",
    "\1\u0175",
    "\1\u0176",
    "\1\u0177",
    "\1\u0178",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "",
    "",
    "",
    "\1\u017a",
    "",
    "\1\u017b",
    "\1\u017c",
    "\1\u017d",
    "\1\u017e",
    "",
    "",
    "\1\u017f",
    "\1\u0180",
    "\1\u0181",
    "\1\u0182",
    "\1\u0183",
    "\1\u0184",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0186",
    "",
    "\1\u0187",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0189",
    "\1\u018a",
    "\1\u018b",
    "\1\u018c",
    "\1\u018d",
    "\1\u018e",
    "\1\u018f",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0191",
    "",
    "\1\u0192",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0195",
    "",
    "\1\u0196",
    "\1\u0197",
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
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01a4",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "",
    "\1\u01a6",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01a9",
    "",
    "\1\u01aa",
    "\1\u01ab",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01ad",
    "\1\u01ae",
    "\1\u01af",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01b1",
    "\1\u01b2",
    "\1\u01b3",
    "\1\u01b4",
    "",
    "\1\u01b5",
    "\1\u01b6",
    "\1\u01b7",
    "\1\u01b8",
    "",
    "",
    "\1\u01b9",
    "\1\u01ba",
    "\1\u01bb",
    "\1\u01bc",
    "",
    "",
    "",
    "",
    "\1\u01bd",
    "\1\u01be",
    "\1\u01bf",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01c1",
    "\1\u01c2",
    "\1\u01c3",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01c5",
    "\1\u01c6",
    "\1\u01c7",
    "\1\u01c8",
    "",
    "\1\u01c9",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01cc",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "\1\u01cf",
    "\1\u01d0",
    "\1\u01d1",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01d4",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01d6",
    "\1\u01d7",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "\1\u01da",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "\1\u01dc",
    "\1\u01dd",
    "\1\u01de",
    "\1\u01df",
    "\1\u01e0",
    "\1\u01e1",
    "\1\u01e2",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01e4",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01e6",
    "\1\u01e7\7\uffff\1\u01e8",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01ea",
    "\1\u01eb",
    "\1\u01ec",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01ef",
    "\1\u01f0",
    "\1\u01f1",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "",
    "\1\u01f3",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "",
    "\1\u01f5",
    "\1\u01f6",
    "\1\u01f7",
    "",
    "\1\u01f8",
    "\1\u01f9\16\uffff\1\u01fa",
    "\1\u01fb",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u01fd",
    "\1\u01fe",
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
    "",
    "\1\u020b",
    "\1\u020c",
    "\1\u020d",
    "",
    "\1\u020e",
    "\1\u020f",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0212",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "",
    "\1\u0214",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "",
    "\1\u0217",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0219",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\uffff",
    "\1\u021c",
    "\1\u021d",
    "\1\u021e",
    "\1\u021f",
    "\1\u0220",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0222",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\uffff",
    "\1\u0225",
    "\1\u0226",
    "\1\u0227",
    "",
    "\1\u0228",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u022a",
    "",
    "",
    "\1\u022b",
    "\1\u022c",
    "\1\u022d",
    "\1\uffff",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\uffff",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0232",
    "\1\u0233",
    "\1\u0234",
    "\1\u0235",
    "\1\u0236",
    "\1\u0237",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0239",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u023d",
    "\1\u023e",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0240",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0243",
    "\1\u0244",
    "\1\u0245",
    "\1\u0246",
    "\1\u0247",
    "\1\u0248",
    "\1\u0249",
    "\1\u024a",
    "",
    "",
    "\1\u024b",
    "",
    "\1\u024c",
    "",
    "\1\uffff",
    "\1\u024e",
    "",
    "\1\u024f",
    "",
    "",
    "\1\u0250",
    "\1\u0251",
    "\1\u0252",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0254",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "",
    "\1\u0256",
    "\1\u0257",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "\1\u025a",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "\1\uffff",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0260",
    "\1\u0261",
    "\1\u0262",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0264",
    "",
    "\1\u0265",
    "",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0267",
    "",
    "\1\u0268",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u026a",
    "\1\u026b",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u026d",
    "\1\u026e",
    "\1\u026f",
    "\1\u0270",
    "\1\u0271",
    "\1\u0272",
    "",
    "\1\u0273",
    "\1\u0274",
    "\1\u0275",
    "\1\u0276",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\uffff",
    "\1\u0279",
    "\1\uffff",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "",
    "",
    "",
    "",
    "\1\u027e",
    "\1\u027f",
    "\1\u0281\3\uffff\1\u0280",
    "",
    "\1\u0282",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\uffff",
    "\1\u0285",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "\1\u0287",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\uffff",
    "\1\u028a",
    "\1\u028b",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u028d",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0290",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0293",
    "",
    "",
    "\1\u0294",
    "",
    "",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u0298",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\uffff",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\uffff",
    "\1\u02a0",
    "",
    "\1\uffff",
    "\1\u02a2",
    "",
    "",
    "\1\u02a3",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\uffff",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "\1\uffff",
    "\1\uffff",
    "",
    "\1\uffff",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "",
    "\1\u02ab",
    "\1\u02ac",
    "",
    "",
    "\1\uffff",
    "",
    "",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "\1\u02af",
    "",
    "",
    "\12\54\7\uffff\32\54\4\uffff\1\54\1\uffff\32\54\105\uffff\27\54\1"+
    "\uffff\37\54\1\uffff\10\54",
    "")

        this.recognizer = baseRecognizer
        this.decisionNumber = 23
        this.eot = DFA.unpackEncodedString(eotS)
        this.eof = DFA.unpackEncodedString(eofS)
        this.min = DFA.unpackEncodedStringToUnsignedChars(minS)
        this.max = DFA.unpackEncodedStringToUnsignedChars(maxS)
        this.accept = DFA.unpackEncodedString(acceptS)
        this.special = DFA.unpackEncodedString(specialS)
        this.transition = new Array[Array[Short]](transitionS.length)
        for (i <- this.transition.indices) {
            this.transition(i) = DFA.unpackEncodedString(transitionS(i))
        }

        override val getDescription = "1:1: Tokens : ( ABS | ACCESS | AFTER | ALIAS | ALL | AND | ARCHITECTURE | ARRAY | ASSERT | ATTRIBUTE | BEGIN | BLOCK | BODY | BUFFER | BUS | CASE | COMPONENT | CONFIGURATION | CONSTANT | DISCONNECT | DOWNTO | ELSE | ELSIF | END | ENTITY | EXIT | FILE | FOR | FUNCTION | GENERATE | GENERIC | GROUP | GUARDED | IF | IMPURE | IN | INERTIAL | INOUT | IS | LABEL | LIBRARY | LINKAGE | LITERAL | LOOP | MAP | MOD | NAND | NEW | NEXT | NOR | NOT | NULL | OF | ON | OPEN | OR | OTHERS | OUT | PACKAGE | PORT | POSTPONED | PROCEDURE | PROCESS | PROTECTED | PURE | RANGE | RECORD | REGISTER | REJECT | REM | REPORT | RETURN | ROL | ROR | SELECT | SEVERITY | SHARED | SIGNAL | SLA | SLL | SRA | SRL | SUBTYPE | THEN | TO | TRANSPORT | TYPE | UNAFFECTED | UNITS | UNTIL | USE | VARIABLE | WAIT | WHEN | WHILE | WITH | XNOR | XOR | AMS_ASSIGN | VAR_ASSIGN | BOX | DBLQUOTE | COMMA | SEMICOLON | LPAREN | RPAREN | LBRACKET | RBRACKET | COLON | DOT | AMPERSAND | BAR | ARROW | AT | QMARK | DLT | DGT | CIRCUMFLEX | DOUBLESTAR | MUL | DIV | PLUS | MINUS | EQ | NEQ | LT | GT | LEQ | GEQ | MEQ | MNEQ | MLT | MGT | MLEQ | MGEQ | CONDITION_OPERATOR | CONTEXT | FORCE | PARAMETER | RELEASE | DEFAULT | NATURE | TERMINAL | QUANTITY | TOLERANCE | ACROSS | THROUGH | SPECTRUM | NOISE | SUBNATURE | LIMIT | REFERENCE | BREAK | PROCEDURAL | WS | NESTED_ML_COMMENT | LINECOMMENT | BASIC_IDENTIFIER | EXTENDED_IDENTIFIER | BASED_LITERAL | INTEGER_LITERAL | REAL_LITERAL | APOSTROPHE | STRING_LITERAL | BIT_STRING_LITERAL );"
    	@throws(classOf[NoViableAltException])
        override def specialStateTransition(s: Int, _input: IntStream):Int = {
            val input = _input
            s match {
                    case 0 => 
                        val LA23_4 = input.LA(1)

                         
                        val index23_4 = input.index()
                        input.rewind()
                        val nextS =
                        if ( (LA23_4=='i') ) 64
                        else if ( (LA23_4=='o') ) 65
                        else if ( (LA23_4=='e') ) 66
                        else if ( (LA23_4=='\"') && ((vhdl2008))) 67
                        else if (true) 44
                        else -1
                         
                        input.seek(index23_4)
                        if ( nextS>=0 ) return nextS
                    case 1 => 
                        val LA23_159 = input.LA(1)

                         
                        val index23_159 = input.index()
                        input.rewind()
                        val nextS =
                        if ( (LA23_159=='#') ) 160
                        else if ( (LA23_159=='_') ) 158
                        else if ( ((LA23_159>='0' && LA23_159<='9')) ) 159
                        else if ( (LA23_159=='.') ) 161
                        else if ( (LA23_159=='b'||LA23_159=='d'||LA23_159=='o'||LA23_159=='s'||LA23_159=='u'||LA23_159=='x') && ((vhdl2008))) 67
                        else if (true) 157
                        else -1
                         
                        input.seek(index23_159)
                        if ( nextS>=0 ) return nextS
                    case 2 => 
                        val LA23_46 = input.LA(1)

                         
                        val index23_46 = input.index()
                        input.rewind()
                        val nextS =
                        if ( (LA23_46=='_') ) 158
                        else if ( ((LA23_46>='0' && LA23_46<='9')) ) 159
                        else if ( (LA23_46=='#') ) 160
                        else if ( (LA23_46=='.') ) 161
                        else if ( (LA23_46=='b'||LA23_46=='d'||LA23_46=='o'||LA23_46=='s'||LA23_46=='u'||LA23_46=='x') && ((vhdl2008))) 67
                        else if (true) 157
                        else -1
                         
                        input.seek(index23_46)
                        if ( nextS>=0 ) return nextS
                    case 3 => 
                        val LA23_661 = input.LA(1)

                         
                        val index23_661 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((vhdl2008)) ) 677
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_661)
                        if ( nextS>=0 ) return nextS
                    case 4 => 
                        val LA23_485 = input.LA(1)

                         
                        val index23_485 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((vhdl2008)) ) 548
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_485)
                        if ( nextS>=0 ) return nextS
                    case 5 => 
                        val LA23_595 = input.LA(1)

                         
                        val index23_595 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((vhdl2008)) ) 632
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_595)
                        if ( nextS>=0 ) return nextS
                    case 6 => 
                        val LA23_666 = input.LA(1)

                         
                        val index23_666 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 679
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_666)
                        if ( nextS>=0 ) return nextS
                    case 7 => 
                        val LA23_475 = input.LA(1)

                         
                        val index23_475 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 539
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_475)
                        if ( nextS>=0 ) return nextS
                    case 8 => 
                        val LA23_678 = input.LA(1)

                         
                        val index23_678 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 685
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_678)
                        if ( nextS>=0 ) return nextS
                    case 9 => 
                        val LA23_648 = input.LA(1)

                         
                        val index23_648 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 668
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_648)
                        if ( nextS>=0 ) return nextS
                    case 10 => 
                        val LA23_500 = input.LA(1)

                         
                        val index23_500 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 560
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_500)
                        if ( nextS>=0 ) return nextS
                    case 11 => 
                        val LA23_667 = input.LA(1)

                         
                        val index23_667 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 680
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_667)
                        if ( nextS>=0 ) return nextS
                    case 12 => 
                        val LA23_498 = input.LA(1)

                         
                        val index23_498 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 558
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_498)
                        if ( nextS>=0 ) return nextS
                    case 13 => 
                        val LA23_655 = input.LA(1)

                         
                        val index23_655 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 673
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_655)
                        if ( nextS>=0 ) return nextS
                    case 14 => 
                        val LA23_669 = input.LA(1)

                         
                        val index23_669 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 681
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_669)
                        if ( nextS>=0 ) return nextS
                    case 15 => 
                        val LA23_534 = input.LA(1)

                         
                        val index23_534 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 589
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_534)
                        if ( nextS>=0 ) return nextS
                    case 16 => 
                        val LA23_620 = input.LA(1)

                         
                        val index23_620 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 649
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_620)
                        if ( nextS>=0 ) return nextS
                    case 17 => 
                        val LA23_614 = input.LA(1)

                         
                        val index23_614 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((vhdl2008)) ) 644
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_614)
                        if ( nextS>=0 ) return nextS
                    case 18 => 
                        val LA23_597 = input.LA(1)

                         
                        val index23_597 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((vhdl2008)) ) 634
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_597)
                        if ( nextS>=0 ) return nextS
                    case 19 => 
                        val LA23_559 = input.LA(1)

                         
                        val index23_559 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 606
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_559)
                        if ( nextS>=0 ) return nextS
                    case 20 => 
                        val LA23_652 = input.LA(1)

                         
                        val index23_652 = input.index()
                        input.rewind()
                        val nextS =
                        if ( ((ams)) ) 671
                        else if ( (true) ) 44
                        else -1
                         
                        input.seek(index23_652)
                        if ( nextS>=0 ) return nextS
                    case 21 => 
                        val LA23_120 = input.LA(1)

                         
                        val index23_120 = input.index()
                        input.rewind()
                        val nextS =
                        if ( (LA23_120=='\"') && ((vhdl2008))) 67
                        else if (true) 44
                        else -1
                         
                        input.seek(index23_120)
                        if ( nextS>=0 ) return nextS
                    case 22 => 
                        val LA23_121 = input.LA(1)

                         
                        val index23_121 = input.index()
                        input.rewind()
                        val nextS =
                        if ( (LA23_121=='\"') && ((vhdl2008))) 67
                        else if (true) 44
                        else -1
                         
                        input.seek(index23_121)
                        if ( nextS>=0 ) return nextS
                    case 23 => 
                        val LA23_111 = input.LA(1)

                         
                        val index23_111 = input.index()
                        input.rewind()
                        val nextS =
                        if ( (LA23_111=='\"') && ((vhdl2008))) 67
                        else if (true) 44
                        else -1
                         
                        input.seek(index23_111)
                        if ( nextS>=0 ) return nextS
                    case 24 => 
                        val LA23_110 = input.LA(1)

                         
                        val index23_110 = input.index()
                        input.rewind()
                        val nextS =
                        if ( (LA23_110=='\"') && ((vhdl2008))) 67
                        else if (true) 44
                        else -1
                         
                        input.seek(index23_110)
                        if ( nextS>=0 ) return nextS
                    case 25 => 
                        val LA23_122 = input.LA(1)

                         
                        val index23_122 = input.index()
                        input.rewind()
                        val nextS =
                        if ( (LA23_122=='\"') && ((vhdl2008))) 67
                        else if (true) 44
                        else -1
                         
                        input.seek(index23_122)
                        if ( nextS>=0 ) return nextS
                    case 26 => 
                        val LA23_112 = input.LA(1)

                         
                        val index23_112 = input.index()
                        input.rewind()
                        val nextS =
                        if ( (LA23_112=='\"') && ((vhdl2008))) 67
                        else if (true) 44
                        else -1
                         
                        input.seek(index23_112)
                        if ( nextS>=0 ) return nextS
            case _ =>
            }
            val nvae = new NoViableAltException(getDescription, 23, s, input)
            error(nvae)
            throw nvae
        }
    }
 
}