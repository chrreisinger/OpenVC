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

lexer grammar Lexer;

options {
    language=Java;
    superClass = AbstractLexer;
}

@lexer::header{
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
}

//includes only VHDL 2002 keywords
ABS:'abs';
ACCESS:'access';
AFTER:'after';
ALIAS:'alias';
ALL:'all';
AND:'and';
ARCHITECTURE:'architecture';
ARRAY:'array';
ASSERT:'assert';
ATTRIBUTE:'attribute';
BEGIN:'begin';
BLOCK:'block';
BODY:'body';
BUFFER:'buffer';
BUS:'bus';
CASE:'case';
COMPONENT:'component';
CONFIGURATION:'configuration';
CONSTANT:'constant';
DISCONNECT:'disconnect';
DOWNTO:'downto';
ELSE:'else';
ELSIF:'elsif';
END:'end';
ENTITY:'entity';
EXIT:'exit';
FILE:'file';
FOR:'for';
FUNCTION:'function';
GENERATE:'generate';
GENERIC:'generic';
GROUP:'group';
GUARDED:'guarded';
IF:'if';
IMPURE:'impure';
IN:'in';
INERTIAL:'inertial';
INOUT:'inout';
IS:'is';
LABEL:'label';
LIBRARY:'library';
LINKAGE:'linkage';
LITERAL:'literal';
LOOP:'loop';
MAP:'map';
MOD:'mod';
NAND:'nand';
NEW:'new';
NEXT:'next';
NOR:'nor';
NOT:'not';
NULL:'null';
OF:'of';
ON:'on';
OPEN:'open';
OR:'or';
OTHERS:'others';
OUT:'out';
PACKAGE:'package';
PORT:'port';
POSTPONED:'postponed';
PROCEDURE:'procedure';
PROCESS:'process';
PROTECTED:'protected';
PURE:'pure';
RANGE:'range';
RECORD:'record';
REGISTER:'register';
REJECT:'reject';
REM:'rem';
REPORT:'report';
RETURN:'return';
ROL:'rol';
ROR:'ror';
SELECT:'select';
SEVERITY:'severity';
SHARED:'shared';
SIGNAL:'signal';
SLA:'sla';
SLL:'sll';
SRA:'sra';
SRL:'srl';
SUBTYPE:'subtype';
THEN:'then';
TO:'to';
TRANSPORT:'transport';
TYPE:'type';
UNAFFECTED:'unaffected';
UNITS:'units';
UNTIL:'until';
USE:'use';
VARIABLE:'variable';
WAIT:'wait';
WHEN:'when';
WHILE:'while';
WITH:'with';
XNOR:'xnor';
XOR:'xor';

//PSL KEYWORDS
/*ASSUME='assume';
ASSUME_GUARANTEE='assume_guarantee';
COVER='cover';
FAIRNESS='fairness';
PROPERTY='property';
RESTRICT='restrict';
RESTRICT_GUARANTEE='restrict_guarantee';
SEQUENCE='sequence';
STRONG='strong';
VMODE='vmode';
VPROP='vprop';
VUNIT='vunit';*/

AMS_ASSIGN    : '==';
VAR_ASSIGN    : ':=';
BOX           : '<>';
DBLQUOTE      : '\"';
COMMA         : ',';
SEMICOLON     : ';';
LPAREN        : '(';
RPAREN        : ')';
LBRACKET      : '[';
RBRACKET      : ']';
COLON         : ':';
DOT           : '.';
AMPERSAND     : '&';
BAR           : '|';
ARROW         : '=>';
//BACKSLASH     = '\\';
AT            : '@';
QMARK 	      : '?';
DLT 	      : '<<';
DGT 	      : '>>';
CIRCUMFLEX    : '^';
//operators
DOUBLESTAR    : '**';
MUL           : '*';
DIV           : '/';
PLUS          : '+';
MINUS         : '-';
EQ            : '=';
NEQ           : '/=';
LT            : '<';
GT            : '>';
LEQ           : '<=';
GEQ           : '>=';
MEQ           : '?=';
MNEQ	      : '?/=';
MLT           : '?<';
MGT           : '?>';
MLEQ	      : '?<=';
MGEQ	      : '?>=';
CONDITION_OPERATOR : '??';

//VHDL-2008 KEYWORDS
CONTEXT : {vhdl2008}?=>'context';

FORCE : {vhdl2008}?=>'force';

PARAMETER : {vhdl2008}?=>'parameter';

RELEASE : {vhdl2008}?=>'release';

DEFAULT : {vhdl2008}?=>'default';

//VHDL-AMS KEYWORDS
NATURE : {ams}?=>'nature';

TERMINAL : {ams}?=>'terminal';

QUANTITY : {ams}?=>'quantity';

TOLERANCE : {ams}?=>'tolerance';

ACROSS : {ams}?=>'across';

THROUGH : {ams}?=>'through';

SPECTRUM : {ams}?=>'spectrum';

NOISE : {ams}?=>'noise';

SUBNATURE : {ams}?=>'subnature';

LIMIT : {ams}?=>'limit';

REFERENCE : {ams}?=>'reference';

BREAK : {ams}?=>'break';

PROCEDURAL : {ams}?=>'procedural';

// Lexer rules
WS : ( '\t' | ' ' | '\r' | '\n' )+ {skip()};

//mulitline comment with nested comments, http://www.antlr.org/wiki/pages/viewpage.action?pageId=1573
NESTED_ML_COMMENT :
	'/*' (options {greedy=false;} : NESTED_ML_COMMENT | . )*  '*/' {skip()};

//A comment can appear on any line of a VHDL description and may contain any character except the format effectors vertical tab, carriage return, line feed, and form feed.
LINECOMMENT : '--' ~('\r'|'\n'|'\u000C')* {skip()};

BASIC_IDENTIFIER : LETTER ( LETTER_OR_DIGIT | '_' )*;

//extended identifiers can't contain a single backslash
EXTENDED_IDENTIFIER : '\\' ( '\"' | '\\\\' | GRAPHIC_CHARACTER )+ '\\';

BASED_LITERAL : INTEGER '#' BASED_INTEGER ( DOT BASED_INTEGER )? '#' EXPONENT? ;

INTEGER_LITERAL : INTEGER EXPONENT?;

REAL_LITERAL : INTEGER  DOT INTEGER  EXPONENT?;

//the semantic predicate is used for this corner case: signed'('1', '0', '1')
//this should create a BASIC_IDENTIFIER APOSTROPHE LPAREN and then a CHARACTER_LITERAL
//otherwise it would create a BASIC_IDENTIFIER CHARACTER_LITERAL ('(') and then an error
APOSTROPHE :
	'\'' ({input.LA(2)=='\'' && (input.LA(4)!='\'' || input.LA(3)==',' || input.LA(3)=='|')}?=> ( '\"' | '\\' | GRAPHIC_CHARACTER ) '\'' { $type = CHARACTER_LITERAL; })?;

//string literals can't contain a single quotation mark
STRING_LITERAL : '\"' ( '\"\"' | '\\' | GRAPHIC_CHARACTER )* '\"';

BIT_STRING_LITERAL : ({vhdl2008}?=>INTEGER)? BASE_SPECIFIER '\"' (BASED_INTEGER? | {vhdl2008}?=> GRAPHIC_CHARACTER*) '\"';

fragment
BASE_SPECIFIER : 'b' | 'o' | 'x' | {vhdl2008}?=>('ub' | 'uo' | 'ux' | 'sb' | 'so' | 'sx' | 'd');

fragment
BASED_INTEGER : EXTENDED_DIGIT ( '_'? EXTENDED_DIGIT )*;

fragment
EXTENDED_DIGIT : DIGIT | LETTER;

fragment
INTEGER : DIGIT ( '_'? DIGIT )*;

fragment
EXPONENT : 'e' ( '+' | '-' )? INTEGER;

fragment
LETTER_OR_DIGIT : LETTER | DIGIT;

fragment
LETTER : UPPER_CASE_LETTER | LOWER_CASE_LETTER;

fragment
GRAPHIC_CHARACTER :
	UPPER_CASE_LETTER
	| DIGIT
	| SPECIAL_CHARACTER
	| SPACE_CHARACTER
	| LOWER_CASE_LETTER
	| OTHER_SPECIAL_CHARACTER;

//A B C D E F G H I J K L M N O P Q R S T U V W X Y Z � � � � � � � � � � � � � � � � D . � � � � � � � � � � � � Y P '
fragment
UPPER_CASE_LETTER : 'A'..'Z' | '\u00c0'..'\u00d6' | '\u00d8' .. '\u00de'; //A - Z,� - �, � - �

//a b c d e f g h i j k l m n o p q r s t u v w x y z � � � � � � � � � � � � � � � � �  � � � � � � � � � � � � y ' p �
fragment
LOWER_CASE_LETTER : 'a'..'z' | '\u00df'..'\u00f6' | '\u00f8'.. '\u00ff'; //a-z,� - �, � - �

fragment
DIGIT : '0'..'9';

fragment
SPECIAL_CHARACTER :
	'#' | '&' | '\'' | '(' | ')' | '*' | '+' | ',' | '-'
	| '.' | '/' | ':' | ';' | '<' | '=' | '>' | '[' | ']' | '_' | '|';

fragment
SPACE_CHARACTER : ' ' | '\u00a0'; //space, non-breaking space

fragment
OTHER_SPECIAL_CHARACTER :
	'!' | '$' | '%' | '@' | '?' | '^' | '`' | '{' | '}' | '~'
	| '\u00a1'..'\u00bf' | '\u00d7' | '\u00f7';//  � - �, � - �

fragment
CHARACTER_LITERAL : ;