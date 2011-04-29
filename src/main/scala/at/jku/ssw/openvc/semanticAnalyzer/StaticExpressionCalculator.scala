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
import at.jku.ssw.openvc.symbolTable.dataTypes.{EnumerationType, ScalarType, PhysicalType}
import at.jku.ssw.openvc.symbolTable.symbols.ConstantSymbol

object StaticExpressionCalculator {
  def calcValue[A <: AnyVal](expression: Expression)(implicit numeric: Integral[A]): Option[A] = {
    import numeric.{mkNumericOps, mkOrderingOps}
    implicit def toOption[A](value: A) = Option(value)
    expression match {
      case SimpleExpression(_, signOption, left, op, rightOption, _) =>
        import SimpleExpression._

        val value = rightOption match {
          case Some(right) =>
            for (l <- calcValue(left);
                 r <- calcValue(right)) yield op.get match {
              case AddOperator.CONCATENATION => sys.error("not implemented")
              case AddOperator.MINUS => l - r
              case AddOperator.PLUS => l + r
            }
          case None => calcValue(left)
        }
        signOption match {
          case None => value
          case Some(sign) =>
            if (sign == SignOperator.MINUS) value.flatMap(numeric.negate)
            else value
        }
      case Factor(_, left, op, right, _) =>
        import Factor.Operator._

        op match {
          case NOT => sys.error("not implemented")
          case POW => for (l <- calcValue(left);
                           r <- calcValue(right.get)) yield numeric.fromInt(math.pow(numeric.toDouble(l), numeric.toDouble(r)).toInt)
          case ABS =>
            require(right.isEmpty)
            calcValue(left).flatMap(numeric.abs)
        }
      case literal: Literal =>
        import Literal.Type._

        literal.literalType match {
          case INTEGER_LITERAL =>
            if (numeric == Numeric.LongIsIntegral) literal.toLong.asInstanceOf[A]
            else literal.toInt.asInstanceOf[A]
          case REAL_LITERAL => literal.toDouble.asInstanceOf[A]
          case CHARACTER_LITERAL =>
            literal.dataType match {
              case enumType: EnumerationType => enumType.value(literal.text.replace("'", "")).asInstanceOf[A]
              case _ => None
            }
          case _ => None
        }
      case Term(_, left, op, right, _) =>
        import Term.Operator._

        for (l <- calcValue(left);
             r <- calcValue(right)) yield op match {
          case DIV => l / r
          case MUL => l * r
          case REM => l % r
          case MOD =>
            val mod = l % r
            if ((mod < numeric.zero && r > numeric.zero) || (mod > numeric.zero && r < numeric.zero)) mod + r else mod
        }
      case e@AttributeExpression(_, symbol, attribute, Seq(), None) =>
        e.dataType match {
          case scalar: ScalarType =>
            attribute.name match {
              case "left" => scalar.left.asInstanceOf[A]
              case "right" => scalar.right.asInstanceOf[A]
              case "low" => scalar.lowerBound.asInstanceOf[A]
              case "high" => scalar.upperBound.asInstanceOf[A]
              case "ascending" => scalar.isAscending.asInstanceOf[A]
              case _ => None
            }
          case _ => None
        }
      case literal@PhysicalLiteral(_, _, _, literalType, symbol) =>
        if (symbol != null) {
          val dataType = literal.dataType.asInstanceOf[PhysicalType]
          literalType match {
            case Literal.Type.INTEGER_LITERAL => (literal.toLong * dataType.units(literal.unitSymbol.name)).asInstanceOf[A]
            case Literal.Type.REAL_LITERAL => (literal.toDouble * dataType.units(literal.unitSymbol.name)).toLong.asInstanceOf[A]
            case _ => None
          }
        } else None
      case ItemExpression(_, symbol) => symbol match {
        case constantSymbol: ConstantSymbol => constantSymbol.value.map(_.asInstanceOf[A])
        case _ => None
      }
      case NoExpression => None
      case e =>
        println("not implemented:" + e.position + " " + e.getClass.getCanonicalName + "\n")
        None
    }
  }
}