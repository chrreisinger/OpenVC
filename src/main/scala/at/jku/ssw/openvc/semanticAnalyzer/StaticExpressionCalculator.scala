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

package at.jku.ssw.openvc.semanticAnalyzer

import at.jku.ssw.openvc.ast.expressions._
import at.jku.ssw.openvc.symbolTable.dataTypes. {EnumerationType, ScalarType}

object StaticExpressionCalculator {
  def calcValue[A](e: Expression)(implicit numeric: Integral[A]): A = {
    def calcValueInner(expr: Expression): A = expr match {
      case SimpleExpression(_, signOption, left, op, rightOption, _) =>
        import SimpleExpression._
        val value = rightOption match {
          case Some(right) => op.get match {
            case AddOperator.MINUS => numeric.minus(calcValueInner(left), calcValueInner(right))
            case AddOperator.PLUS => numeric.plus(calcValueInner(left), calcValueInner(right))
          }
          case None => calcValueInner(left)
        }
        signOption match {
          case None => value
          case Some(sign) =>
            if (sign == SignOperator.MINUS) numeric.negate(value)
            else value
        }
      case Factor(_, left, op, right, _) =>
        import Factor.Operator._
        op match {
          case POW => math.pow(numeric.toDouble(calcValueInner(left)), numeric.toDouble(calcValueInner(right.get))).asInstanceOf[A]
          case ABS =>
            require(right.isEmpty)
            numeric.abs(calcValueInner(left))
        }
      case literal: Literal =>
        import Literal.Type._
        literal.literalType match {
          case INTEGER_LITERAL => literal.toLong.asInstanceOf[A]
          case REAL_LITERAL => literal.toDouble.asInstanceOf[A]
          case CHARACTER_LITERAL =>
            literal.dataType match {
              case enumType: EnumerationType => enumType.intValue(literal.text).asInstanceOf[A]
            }
        }
      case Term(_, left, op, right, _) =>
        import Term.Operator._
        op match {
          case DIV => numeric.quot(calcValueInner(left), calcValueInner(right))
          case MUL => numeric.times(calcValueInner(left), calcValueInner(right))
        }
      case AttributeExpression(_, symbol, attribute, None, dataType) =>
        dataType match {
          case scalar: ScalarType =>
            attribute.name match {
              case "left" => scalar.left.asInstanceOf[A]
              case "right" => scalar.right.asInstanceOf[A]
              case "low" => scalar.lowerBound.asInstanceOf[A]
              case "high" => scalar.upperBound.asInstanceOf[A]
              case "ascending" => scalar.ascending.asInstanceOf[A]
            }
        }
      case e => println(e.position); println(e); throw new UnsupportedOperationException(expr.toString)
    }

    return calcValueInner(e)
  }
}