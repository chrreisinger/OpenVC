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

object SemanticMessage {
  val ATTRIBUTE_NO_PARAMETER = "The attribute %s does not take a parameter"
  val ATTRIBUTE_PARAMETER_REQUIRED = "The attribute %s requires a parameter of type %s"
  val ASSIGN_IN_PURE_FUNCTION = "%s assignment in pure function %s not allowed for %s"
  val ASSIGN_READ_ONLY = "can not write IN %s %s"
  val ALREADY_DECLARED = "%s already declared in current scope"
  val INVALID_SIMPLE_EXPRESSION = "invalid simple expression"
  val INVALID_TYPE_CAST = "invalid type cast expression"
  val INVALID_INDEXES_COUNT = "invalid indices count, found %s expected %s"
  val INVALID_BASED_LITERAL_CHARACTER = " invalid character %s in based literal with base %s"
  val INVALID_TO_DIRECTION = "the left value is bigger then the right value"
  val INVALID_DOWNTO_DIRECTION = "the left value is smaller then the right value"
  val INVALID_SYMBOL_TYPE = "%s is not a %s, it is a %s"
  val INVALID_NAME = "not a valid name"
  val INVALID_ARG_COUNT = "invalid argument count, found %s expected %s"
  val INVALID_ELEMENT_TYPE = "the type of a %s element can not be a %s type"
  val INVALID_TYPE = "the type can not be a %s type"
  val INCOMPLETE_TYPE_NOT_DEFINED="the incomplete type %s is not defined"
  val DUPLICATE_ENUMERATION_VALUE = "duplicate enumeration value %s"
  val DUPLICATE_RECORD_FIELD = "duplicate record field %s"
  val DUPLICATE_PHYSICAL_UNIT = "duplicate physical unit %s"
  val DEFERRED_CONSTANT_NOT_ALLOWED="deferred constant are only in package header allowed"
  val DEFERRED_CONSTANT_NOT_DECLARED="the deferred constant %s is not full declared"
  val NOT_A = "%s is not a %s"
  val NOT_ALLOWED = "%s not allowed"
  val NO_FIELD = "%s is not a field"
  val NO_ENUMERATION_VALUE = "value %s not found in enumeration %s"
  val NOT_FOUND = "%s %s not found"
  val NOT_INSIDE_A_LOOP = "the %s statement must be inside a loop"
  val FUNCTION_ALREADY_DECLARED = "function %s with parameter(s) %s and return type %s already defined"
  val FUNCTION_RETURN_WITHOUT_EXPRESSION = "A function must return a value"
  val PROCEDURE_ALREADY_DECLARED = "procedure %s with parameter(s) %s already defined"
  val EXPECTED_EXPRESSION_OF_TYPE = "expected a expression of type %s, found %s"
  val EXPECTED_INTEGER_EXPRESSION = "expected a integer expression"
  val EXPECTED_LITERAL_OF_TYPE = "expected a %s literal"
  val EXPECTED_TYPE = "expected a %s type"
  val OTHERS_CHOICE_NOT_LAST_ALTERNATIVE = "the others case must be the last case"
  val OTHERS_CHOICE_NOT_ALONE = "the others case must be alone"
  val OPERATOR_NOT_DEFINED="the operator %s is not defined for types %s and %s"
  val UNARY_OPERATOR_NOT_DEFINED="the unary operator %s is not defined for type %s"
  val RESOLVED_DUPLICATE_SIGNAL_ASSIGNMENT = "signal %s already has a driver at line: %s"
  val RETURN_STMT_IN_PROCESS = "A process can not contain a return statement!"
  val PROTECTED_TYPE_BODY_IN_PACKAGE = "protected type body declaration in package declaration is not allowed"
  val PROTECTED_TYPE_IN_BODY = "can not use the defined protected type in the body where it is defined"
  val PROTECTED_TYPE_INITIAL_VALUE_EXPRESSION = "a variable of a protected type can not have a initial value expression"
  val PROCESS_WITH_SENSITIVITY_LIST_AND_WAIT = "a process with a sensitivity list can not contain an exlicit wait statement"
  val PROCESS_WITH_SENSITIVITY_LIST_AND_PROCEDURE_CALL = "a process with a sensitivity list can not call a procedure with a wait statement"
  val PROCEDURE_RETURN_VALUE = "A procedure can not return a value"
  val START_LABEL_MISSING = "The statement is not labeld, %s is illegal here"
  val START_END_LABEL_DIFFERENT = "The start label:%s ist differend form this end label:%s"
  val STATEMENT_NOT_PASSIVE = "this concurrent statement is not passive, and only passive statement are allowed in a entitySymbol declaration"
  val SHARED_VARIABLE_NOT_PROTECTED_TYPE = "The base type of the subtype indication of a shared variable declaration must be a protected type."
  val UNIT_NOT_FOUND = "The physical type %s has no unit %s"
  // warnings
  val UNUSED_SYMBOL = "unused %s %s"
}
