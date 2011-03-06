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

package at.jku.ssw.openvc.ast.expressions

import at.jku.ssw.openvc.ast._
import at.jku.ssw.openvc.symbolTable.dataTypes.{DataType, NoType}
import at.jku.ssw.openvc.symbolTable.symbols._
import at.jku.ssw.openvc.util.{Position, NoPosition}

/**
 * Base class for all expressions nodes
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 */
abstract sealed class Expression extends Locatable {
  /**
   * Data type of this expression, NoType if for some reason no data type could be calculated
   */
  val dataType: DataType
  /**
   * the first position of the expression
   *
   * for a expression with a binary operator this returns not the position of the operator but the position of the left expression
   *
   * for a operator b position is the position of the operator, firstPosition is the position of a
   */
  def firstPosition = position
}

/**
 * Represents a undefined expression, that is used in error cases instead of null values
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 */
case object NoExpression extends Expression {
  /**
   * is always NoType
   */
  val dataType = NoType
  /**
   * is always NoPosition
   */
  val position = NoPosition
}

final case class ConditionExpression(position: Position, left: Expression, dataType: DataType = NoType) extends Expression

object Term {
  type Operator = Operator.Value

  object Operator extends Enumeration {
    /**
     * multiplication operator
     */
    val MUL = Value("*")
    /**
     * division operator
     */
    val DIV = Value("/")
    /**
     * modulo operator
     */
    val MOD = Value("mod")
    /**
     * remainder operator
     */
    val REM = Value("rem")
  }

}

/**
 * Represents a term
 *
 * grammar: <pre> factor { multiplying_operator factor } </pre>
 *
 * example: {{{ a * b }}} {{{ a rem 5 }}} {{{ left / right }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.term]]
 * @param left the first factor (before the operator)
 * @param operator the operator of the term
 * @param right the second factor (after the operator)
 */
final case class Term(position: Position, left: Expression, operator: Term.Operator, right: Expression, dataType: DataType = NoType) extends Expression {
  override val firstPosition = left.firstPosition
}

object Aggregate {

  /**
   * Represents a element association in a aggregate
   *
   * grammar: <pre> [ choices ARROW ] expression </pre>
   *
   * example: {{{ a => 1 }}}
   * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
   * @see [[at.jku.ssw.openvc.parser.VHDLParser.element_association]]
   * @param choices the optional choices
   * @param expression the expression after the arrow
   */
  final case class ElementAssociation(choices: Option[Seq[Choices.Choice]], expression: Expression)

}

/**
 * Represents a aggregate
 *
 * grammar: <pre> ( element_association {, element_association } ) </pre>
 *
 * example: {{{ (0,1,2,3) }}} {{{ (a=>0,b=>1,c=>2,d=>3) }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.aggregate]]
 * @param elements the different element associations
 * @param expressions the calculated expression used for the code generator
 */
final case class Aggregate(position: Position, elements: Seq[Aggregate.ElementAssociation], expressions: Seq[Expression] = Seq(), dataType: DataType = NoType) extends Expression

/**
 * Represents a type cast expression
 *
 * grammar: <pre> ( expression ) </pre>
 *
 * example: {{{ integer(a) }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @param expression the expression to cast to [[dataType]]
 */
final case class TypeCastExpression(expression: Expression, dataType: DataType = NoType) extends Expression {
  val position = expression.position
}

object Relation {
  type Operator = Operator.Value

  object Operator extends Enumeration {
    /**
     * equal operator
     */
    val EQ = Value("=")
    /**
     * not equal operator
     */
    val NEQ = Value("/=")
    /**
     * less than operator
     */
    val LT = Value("<")
    /**
     * less than or equal operator
     */
    val LEQ = Value("<=")
    /**
     * greater than operator
     */
    val GT = Value(">")
    /**
     * greater than or equal operator
     */
    val GEQ = Value(">=")

    /**
     * matching equal operator
     */
    val MEQ = Value("?=")
    /**
     * matching not equal operator
     */
    val MNEQ = Value("?/=")
    /**
     * matching less than operator
     */
    val MLT = Value("?<")
    /**
     * matching less than or equal operator
     */
    val MLEQ = Value("?<=")
    /**
     * matching greater than operator
     */
    val MGT = Value("?>")
    /**
     * matching greater than or equal operator
     */
    val MGEQ = Value("?>=")
  }

}

/**
 * Represents a relation
 *
 * grammar: <pre> shift_expression { relational_operator shift_expression } </pre>
 *
 * example: {{{ a = b }}} {{{ a >= 5 }}} {{{ left /= right }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.relation]]
 * @param left the first shift_expression (before the operator)
 * @param operator the operator of the relation
 * @param right the second shift_expression (after the operator)
 */
final case class Relation(position: Position, left: Expression, operator: Relation.Operator, right: Expression, dataType: DataType = NoType) extends Expression {
  override val firstPosition = left.firstPosition
}

/**
 * Represents a qualified expression
 *
 * grammar: <pre> selected_name ' aggregate </pre>
 *
 * example: {{{ complex'(1.0,0.0) }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.qualified_expression]]
 * @param typeName the name of the data type
 * @param expression the expression after the apostrophe
 */
final case class QualifiedExpression(typeName: SelectedName, expression: Expression, dataType: DataType = NoType) extends Expression {
  val position = typeName.position
}

object Name {

  /**
   * Base class for name parts
   *
   * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
   */
  abstract sealed class Part extends Locatable

  final case class SelectedPart(identifier: Identifier) extends Part {
    val position = identifier.position
  }

  final case class SlicePart(range: DiscreteRange) extends Part {
    val position = range.position
  }

  final case class AttributePart(signature: Option[Signature], identifier: Identifier, expression: Option[Expression]) extends Part {
    val position = signature.map(_.position).getOrElse(identifier.position)
  }

  final case class AssociationListPart(position: Position, associationList: AssociationList) extends Part

}

final case class Name(identifier: Identifier, parts: Seq[Name.Part]) extends Expression {
  val position = identifier.position
  val dataType = NoType
}

trait WithSymbol {
  val symbol: Symbol
}

/**
 * Represents a parameter symbol, that has no actual expression in a AssociationList
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.ast.AssociationList]]
 * @param symbol the symbol of a interface list for which the default value should be loaded
 */
final case class DefaultExpression(symbol: RuntimeSymbol) extends Expression {
  val position = NoPosition
  val dataType = symbol.dataType
}

/**
 * Represents a parameter symbol, where the actual expression is open in a AssociationList
 *
 * example: {{{ foo(a=>open); }}}
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.ast.AssociationList]]
 * @param symbol the symbol of a interface list which is open
 */
final case class OpenExpression(symbol: RuntimeSymbol) extends Expression {
  val position = NoPosition
  val dataType = symbol.dataType
}

final case class ArrayAccessExpression(symbol: RuntimeSymbol, indexes: Seq[Expression], elementType: DataType, expression: Option[Expression]) extends Expression with WithSymbol {
  val position = indexes.head.position
  val dataType = expression match {
    case Some(expr) => expr.dataType
    case None => elementType
  }
}

final case class AttributeExpression(position: Position, symbol: Symbol, attribute: AttributeSymbol, parameterExpression: Option[Expression], expression: Option[Expression]) extends Expression with WithSymbol {
  val dataType = expression match {
    case Some(expr) => expr.dataType
    case None => attribute.dataType
  }
}

final case class SliceAccessExpression(symbol: RuntimeSymbol, range: DiscreteRange, rangeType: DataType, expression: Option[Expression]) extends Expression with WithSymbol {
  val position = range.position
  val dataType = expression match {
    case Some(expr) => expr.dataType
    case None => rangeType
  }
}

final case class FieldAccessExpression(symbol: RuntimeSymbol, field: Identifier, fieldDataType: DataType, expression: Option[Expression]) extends Expression with WithSymbol {
  val position = field.position
  val dataType = expression match {
    case Some(expr) => expr.dataType
    case None => fieldDataType
  }
}

/**
 * Represents a function call expression
 *
 * The grammar has no production for a function call, because the expressions are created in the semantic analyzer
 *
 * example: {{{ x:=mypackage.myFunction(a,b,c,d).recordField; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @param name the name of the function to call
 * @param parameterAssociation the parameters of the function
 * @param expression the optional expression to access the return value of the function call e.g. recordField in the example
 * @param symbol the function to call
 */
final case class FunctionCallExpression(name: Identifier, parameterAssociation: Option[AssociationList], expression: Option[Expression] = None, symbol: FunctionSymbol = null) extends Expression {
  val position = name.position
  val dataType = expression match {
    case Some(expr) => expr.dataType
    case None => if (symbol != null) symbol.returnType else NoType
  }
}

final case class ItemExpression(position: Position, symbol: RuntimeSymbol) extends Expression with WithSymbol {
  val dataType = symbol.dataType
}

object ShiftExpression {
  type Operator = Operator.Value

  object Operator extends Enumeration {
    /**
     * shift left logical operator
     */
    val SLL = Value("sll")
    /**
     * shift right logical operator
     */
    val SRL = Value("srl")
    /**
     * shift left arithmetic operator
     */
    val SLA = Value("sla")
    /**
     * shift right arithmetic operator
     */
    val SRA = Value("sra")
    /**
     * rotate left logical operator
     */
    val ROL = Value("rol")
    /**
     * rotate right logical operator
     */
    val ROR = Value("ror")
  }

}

/**
 * Represents a shift expression
 *
 * grammar: <pre> simple_expression { shift_operator simple_expression } </pre>
 *
 * example: {{{ a sll b }}} {{{ a ror 5 }}} {{{ left sra right }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.shift_expression]]
 * @param left the first simple_expression (before the operator)
 * @param operator the operator of the shift expression
 * @param right the second simple_expression (after the operator)
 */
final case class ShiftExpression(position: Position, left: Expression, operator: ShiftExpression.Operator, right: Expression, dataType: DataType = NoType) extends Expression {
  override val firstPosition = left.firstPosition
}

object Factor {
  type Operator = Operator.Value

  object Operator extends Enumeration {
    /**
     * exponentiation operator
     */
    val POW = Value("**")
    /**
     * absolute value operator
     */
    val ABS = Value("abs")
    /**
     * negation operator
     */
    val NOT = Value("not")
    /**
     * logical reduction AND operator
     */
    val AND = Value("and")
    /**
     * logical reduction NAND operator
     */
    val NAND = Value("nand")
    /**
     * logical reduction OR operator
     */
    val OR = Value("or")
    /**
     * logical reduction NOR operator
     */
    val NOR = Value("nor")
    /**
     * logical reduction XOR operator
     */
    val XOR = Value("xor")
    /**
     * logical reduction XNOR operator
     */
    val XNOR = Value("xnor")
  }

}

/**
 * Represents a factor
 *
 * grammar:
 * <pre>
 * primary [** primary]
 *
 * | '''ABS''' primary
 *
 * | '''NOT''' primary
 * </pre>
 *
 * example: {{{ abs a }}} {{{ not a }}} {{{ a ** b }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.factor]]
 * @param left the first primary (before the operator)
 * @param operator the operator of the factor
 * @param rightOption the second primary (after the operator)
 */
final case class Factor(position: Position, left: Expression, operator: Factor.Operator, rightOption: Option[Expression] = None, dataType: DataType = NoType) extends Expression {
  override val firstPosition = if (operator == Factor.Operator.POW) left.firstPosition else position
}

object LogicalExpression {
  type Operator = Operator.Value

  object Operator extends Enumeration {
    /**
     * logical AND operator
     */
    val AND = Value("and")
    /**
     * logical NAND operator
     */
    val NAND = Value("nand")
    /**
     * logical OR operator
     */
    val OR = Value("or")
    /**
     * logical NOR operator
     */
    val NOR = Value("nor")
    /**
     * logical XOR operator
     */
    val XOR = Value("xor")
    /**
     * logical XNOR operator
     */
    val XNOR = Value("xnor")
  }

}

/**
 * Represents a logical expression
 *
 * The special handling for nand and nor is because of this quote from the LRM:
 *
 * "The syntax for an expression involving logical operators allows a sequence of and, or, xor, or xnor operators
 * (whether predefined or user-defined), since the corresponding predefined operations are associative. For the operators
 * nand and nor (whether predefined or user-defined), however, such a sequence is not allowed, since the corresponding
 * predefined operations are not associative."
 *
 * grammar:
 * <pre> relation (
 *
 *  ('''NAND'''|'''NOR''') relation
 *
 *  | { logical_operator relation }
 *
 * ) </pre>
 *
 * example: {{{ a or b or c}}} {{{ a nand b }}} {{{ left xnor right }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.expression]]
 * @param left the first relation (before the operator)
 * @param operator the operator of the logical expression
 * @param right the second relation (after the operator)
 */
final case class LogicalExpression(position: Position, left: Expression, operator: LogicalExpression.Operator, right: Expression, dataType: DataType = NoType) extends Expression {
  override val firstPosition = left.firstPosition
}

object SimpleExpression {
  type AddOperator = AddOperator.Value

  object AddOperator extends Enumeration {
    /**
     * addition operator
     */
    val PLUS = Value("+")
    /**
     * subtraction operator
     */
    val MINUS = Value("-")
    /**
     * concatenation operator
     */
    val CONCATENATION = Value("&")
  }

  type SignOperator = SignOperator.Value

  object SignOperator extends Enumeration {
    /**
     * unary plus operator
     */
    val PLUS = Value("+")
    /**
     * unary minus operator
     */
    val MINUS = Value("-")
  }

}

/**
 * Represents a simple expression
 *
 * grammar: <pre> [sign] term { adding_operator term } </pre>
 *
 * example: {{{ -a }}} {{{ a + b }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.simple_expression]]
 * @param signOperator the optional sign of the simple expression
 * @param left the first term (before the operator)
 * @param addOperator the optional adding_operator of the simple expression
 * @param rightOption the optional second term (after the operator)
 */
final case class SimpleExpression(position: Position, signOperator: Option[SimpleExpression.SignOperator], left: Expression, addOperator: Option[SimpleExpression.AddOperator], rightOption: Option[Expression], dataType: DataType = NoType)
  extends Expression {
  override val firstPosition = if (signOperator.isDefined) position else left.firstPosition
  require(addOperator.isDefined == rightOption.isDefined)
}

/**
 * Represents a new expression aka allocator
 *
 * grammar: <pre>
 * NEW selected_name (
 *  qualified_expression
 *  | [index_constraint]
 * )
 * </pre>
 *
 * example: {{{ new integer }}} {{{ new string(0 to 10) }}} {{{ new complex'(1.0,0.0) }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.allocator]]
 * @param qualifiedExpressionOrSubTypeIndication either the qualified expression or the subtype indication
 */
final case class NewExpression(position: Position, qualifiedExpressionOrSubTypeIndication: Either[Expression, SubTypeIndication]) extends Expression {
  val dataType = qualifiedExpressionOrSubTypeIndication match {
    case Left(expression) => expression.dataType
    case Right(subType) => subType.dataType
  }
}

object Literal {
  type Type = Type.Value

  object Type extends Enumeration {
    val REAL_LITERAL, INTEGER_LITERAL, BASED_LITERAL, CHARACTER_LITERAL, STRING_LITERAL, BIT_STRING_LITERAL, NULL_LITERAL = Value
  }

}

trait LiteralConverter {
  val text: String
  val literalType: Literal.Type

  import Literal.Type

  def toLong: Long = {
    require(literalType == Type.INTEGER_LITERAL)
    // DECIMAL_LITERAL : INTEGER;
    if (text.contains("E"))
      this.text.replace("_", "").toDouble.toLong //for values that contain an exponent like 1E16
    else
      this.text.replace("_", "").toLong
  }

  def toDouble: Double = {
    require(literalType == Type.REAL_LITERAL)
    // REAL_LITERAL : INTEGER DOT INTEGER EXPONENT? ;
    this.text.replace("_", "").toDouble
  }
}

/**
 * Represents a literal
 *
 * grammar:
 * <pre>
 * REAL_LITERAL
 *
 * | INTEGER_LITERAL
 *
 * | BASED_LITERAL
 *
 * | CHARACTER_LITERAL
 *
 * | STRING_LITERAL
 *
 * | BIT_STRING_LITERAL
 *
 * | '''NULL'''
 * </pre>
 *
 * example: {{{ 1.0 }}} {{{ 2.0 }}} {{{ 'a' }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.literal]]
 * @param text the text of the literal
 * @param literalType the type of the literal
 * @param value the value of the literal, only used for real, character and integer literals
 */
final case class Literal(position: Position, text: String, literalType: Literal.Type, dataType: DataType = NoType, value: AnyVal = -1) extends Expression with LiteralConverter {
  def toInt: Int = toLong.toInt
}

/**
 * Represents a physical literal
 *
 * grammar:
 * <pre>
 * (
 *
 * REAL_LITERAL
 *
 * | INTEGER_LITERAL
 *
 * | BASED_LITERAL
 *
 * ) selected_name
 * </pre>
 *
 * example: {{{ 1.0 ms }}} {{{ 2 ns }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.physical_literal]]
 * @param text the text of the literal
 * @param unitName the name of the unit
 * @param literalType the type of the literal
 * @param unitSymbol the symbol of the unit if it was found
 */
final case class PhysicalLiteral(position: Position, text: String, unitName: SelectedName, literalType: Literal.Type, unitSymbol: UnitSymbol = null) extends Expression with LiteralConverter {
  def this(literal: Literal, unitName: SelectedName) = this (literal.position, literal.text, unitName, literal.literalType)

  val dataType: DataType = if (unitSymbol != null) unitSymbol.dataType else NoType
}