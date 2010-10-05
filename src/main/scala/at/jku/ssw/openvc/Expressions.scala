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

import at.jku.ssw.openvc.symbolTable._
import at.jku.ssw.openvc.ast._

abstract sealed class Expression extends Locatable {
  val dataType: DataType
}

case object EmptyExpression extends Expression {
  val dataType = NoType
  val position = Position.Empty
}

object Term {
  type Operator = Operator.Value
  object Operator extends Enumeration {
    val MUL, DIV, MOD, REM = Value
  }
}
final case class Term(position: Position, left: Expression, operator: Term.Operator, right: Expression, dataType: DataType = NoType) extends Expression

final case class AggregateExpression(aggregate: Aggregate, dataType: DataType = NoType) extends Expression {
  val position = aggregate.position
}

final case class TypeCastExpression(expression: Expression, dataType: DataType = NoType) extends Expression {
  val position = expression.position
}

object Relation {
  type Operator = Operator.Value
  object Operator extends Enumeration {
    val EQ, NEQ, LT, LEQ, GT, GEQ = Value
  }
}
final case class Relation(position: Position, left: Expression, operator: Relation.Operator, right: Expression, dataType: DataType = NoType) extends Expression

final case class QualifiedExpression(typeName: SelectedName, expression: Expression, dataType: DataType = NoType) extends Expression {
  val position = typeName.position
}

final case class NameExpression(name: Name) extends Expression {
  val position = name.position
  val dataType = NoType
}


trait WithSymbol[T >: Null <: Symbol] {
  val symbol: T
}

final case class ArrayAccessExpression(symbol: RuntimeSymbol, indexes: Seq[Expression], dataType: DataType, expression: Expression) extends Expression with WithSymbol[RuntimeSymbol] {
  val position = indexes.head.position
}

final case class AttributeExpression(position: Position, symbol: Symbol, attribute: AttributeSymbol, expression: Option[Expression], dataType: DataType) extends Expression with WithSymbol[Symbol]

final case class RangeAccessExpression(symbol: RuntimeSymbol, range: DiscreteRange, dataType: DataType) extends Expression with WithSymbol[RuntimeSymbol] {
  val position = range.position
}

final case class FieldAccessExpression(symbol: RuntimeSymbol, field: Identifier, fieldDataType: DataType, dataType: DataType, expression: Expression) extends Expression with WithSymbol[RuntimeSymbol] {
  val position = field.position
}

final case class ItemExpression(position: Position, symbol: RuntimeSymbol) extends Expression with WithSymbol[RuntimeSymbol] {
  val dataType = symbol.dataType
}

object ShiftExpression {
  type Operator = Operator.Value
  object Operator extends Enumeration {
    val SLL, SRL, SLA, SRA, ROL, ROR = Value
  }
}
final case class ShiftExpression(position: Position, left: Expression, operator: ShiftExpression.Operator, right: Expression, dataType: DataType = NoType) extends Expression

object Factor {
  type Operator = Operator.Value
  object Operator extends Enumeration {
    val POW, ABS, NOT = Value //, And, Nand, Or, Nor, Xor, XNor;
  }
}
final case class Factor(position: Position, left: Expression, operator: Factor.Operator, rightOption: Option[Expression] = None, dataType: DataType = NoType) extends Expression

final case class FunctionCallExpression(functionName: SelectedName, parameterAssociationList: Option[AssociationList],
                                        parameters: Seq[Expression] = Seq(), dataType: DataType = NoType, symbol: FunctionSymbol = null) extends Expression {
  val position = functionName.position
}
object LogicalExpression {
  type Operator = Operator.Value
  object Operator extends Enumeration {
    val AND, NAND, OR, NOR, XOR, XNOR = Value
  }
}
final case class LogicalExpression(position: Position, left: Expression, operator: LogicalExpression.Operator, right: Expression, dataType: DataType = NoType) extends Expression

object SimpleExpression {
  type AddOperator = AddOperator.Value
  object AddOperator extends Enumeration {
    val PLUS, MINUS, AMPERSAND = Value
  }
  type SignOperator = SignOperator.Value
  object SignOperator extends Enumeration {
    val PLUS, MINUS = Value
  }
}
final case class SimpleExpression(position: Position, signOperator: Option[SimpleExpression.SignOperator], left: Expression, addOperator: Option[SimpleExpression.AddOperator], rightOption: Option[Expression], dataType: DataType = NoType)
        extends Expression {
  require(addOperator.isDefined == rightOption.isDefined)
}

final case class NewExpression(position: Position, qualifiedExpressionOrSubTypeIndication: Either[Expression, SubTypeIndication], dataType: DataType = NoType) extends Expression

object Literal {
  type Type = Type.Value
  object Type extends Enumeration {
    val REAL_LITERAL, INTEGER_LITERAL, BASED_LITERAL, CHARACTER_LITERAL, STRING_LITERAL, BIT_STRING_LITERAL, NULL_LITERAL = Value
  }
}

final case class Literal(position: Position, text: String, literalType: Literal.Type, dataType: DataType = NoType, value: AnyVal = -1)
        extends Expression {
  import Literal.Type

  def toDouble: Double = {
    require(literalType == Type.REAL_LITERAL)
    // REAL_LITERAL : INTEGER DOT INTEGER EXPONENT? ;
    this.text.replace("_", "").toDouble
  }

  def toInt: Int = toLong.toInt

  def toLong: Long = {
    require(literalType == Type.INTEGER_LITERAL)
    // DECIMAL_LITERAL : INTEGER;
    if (text.contains("E"))
      this.text.replace("_", "").toDouble.toLong //for values that contain an exponent like 1E16
    else
      this.text.replace("_", "").toLong
  }

  def toAnyVal: AnyVal = {
    require(literalType == Type.INTEGER_LITERAL || literalType == Type.REAL_LITERAL)
    if (literalType == Type.INTEGER_LITERAL) toLong
    else toDouble
  }
}

final case class PhysicalLiteral(position: Position, text: String, unitName: Identifier, literalType: Literal.Type, dataType: DataType = NoType) extends Expression {
  def this(literal: Literal, unitName: Identifier) = this (literal.position, literal.text, unitName, literal.literalType)

  def toLong: Long = {
    require(literalType == Literal.Type.INTEGER_LITERAL)
    // DECIMAL_LITERAL : INTEGER;
    if (text.contains("E"))
      this.text.replace("_", "").toDouble.toLong
    else
      this.text.replace("_", "").toLong
  }

  def toDouble: Double = {
    require(literalType == Literal.Type.REAL_LITERAL)
    // REAL_LITERAL : INTEGER DOT INTEGER EXPONENT? ;
    this.text.replace("_", "").toDouble
  }
}