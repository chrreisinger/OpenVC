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

package at.jku.ssw.openvc.symbolTable.symbols

import java.io.Serializable

import collection.mutable

import at.jku.ssw.openvc.symbolTable.SymbolTable
import at.jku.ssw.openvc.symbolTable.dataTypes. {DataType, ScalarType}
import at.jku.ssw.openvc.ast. {ASTNode, Locatable, Identifier}
import at.jku.ssw.openvc.ast.declarations.SignalDeclaration
import at.jku.ssw.openvc.ast.declarations.EntityClass.{Value => EntityClass}

@SerialVersionUID(6463078064067295176L)
abstract sealed class Symbol extends Serializable with Locatable {
  val identifier: Identifier

  def owner: Symbol

  lazy val position = identifier.position
  lazy val name = identifier.text
  lazy val attributes = Map[String, AttributeSymbol]()
}

case object NoSymbol extends Symbol {
  val identifier = Identifier("NoSymbol")
  val owner = null
}

@SerialVersionUID(7944297999105157076L)
final case class AliasSymbol(identifier: Identifier, destination: Symbol) extends Symbol {
  val owner = NoSymbol
}

@SerialVersionUID(7013029205564976456L)
final case class ListOfFunctions(identifier: Identifier, functions: mutable.ListBuffer[FunctionSymbol]) extends Symbol {
  val owner = NoSymbol
}

@SerialVersionUID(2010307013874058143L)
final case class ListOfProcedures(identifier: Identifier, procedures: mutable.ListBuffer[ProcedureSymbol]) extends Symbol {
  val owner = NoSymbol
}

@SerialVersionUID(1238244612233452818L)
final case class AttributeSymbol(identifier: Identifier, dataType: DataType, parameter: Option[DataType], isParameterOptional: Boolean, owner: Symbol, isPredefined: Boolean) extends Symbol {
  def this(name: String, dataType: DataType, parameter: Option[DataType], isParameterOptional: Boolean = false) =
    this (Identifier(name), dataType, parameter, isParameterOptional = isParameterOptional, owner = null, isPredefined = true)
}

abstract sealed class SubprogramSymbol extends Symbol {
  val parameters: Seq[RuntimeSymbol]
  val owner: Symbol
  val (isStatic,isSynchronized)=owner match {
    case _: PackageHeaderSymbol | _: PackageBodySymbol | Runtime=>(true,false)
    case typeSymbol: TypeSymbol => (false,true)
    case _ => (false,false)
  }
  var implemented: Boolean
}
@SerialVersionUID(-7427096092567821868L)
final case class FunctionSymbol(identifier: Identifier, parameters: Seq[RuntimeSymbol], returnType: DataType, owner: Symbol, isPure: Boolean) extends SubprogramSymbol {
  var implemented = false
}

@SerialVersionUID(1492091303227052136L)
final case class ProcedureSymbol(identifier: Identifier, parameters: Seq[RuntimeSymbol], owner: Symbol, var isPassive: Boolean) extends SubprogramSymbol {
  var implemented = false
  val copyBackSymbols = parameters.collect {
    _ match {
      case x: VariableSymbol if (x.modifier != RuntimeSymbol.Modifier.IN && x.dataType.isInstanceOf[ScalarType]) => x
    }
  }
  val needsCopyBack = copyBackSymbols.nonEmpty
}

@SerialVersionUID(-1724650145084937620L)
case object Runtime extends Symbol {
  import at.jku.ssw.openvc.codeGenerator.ByteCodeGenerator
  val identifier = Identifier(ByteCodeGenerator.RUNTIME)
  val owner = NoSymbol
}

@SerialVersionUID(4341399901500604597L)
final case class GroupTemplateSymbol(identifier: Identifier, items: Seq[EntityClass], infinite: Boolean, owner: Symbol) extends Symbol

@SerialVersionUID(-3051930707092602812L)
final case class GroupSymbol(identifier: Identifier, owner: Symbol) extends Symbol

@SerialVersionUID(-6214450543318369430L)
final case class ProcessSymbol(identifier: Identifier, owner: Symbol, var isPassive: Boolean) extends Symbol

@SerialVersionUID(7523805927766617817L)
final case class TypeSymbol(identifier: Identifier, dataType: DataType, owner: Symbol) extends Symbol {
  override lazy val attributes = dataType.attributes
}

@SerialVersionUID(1111707794631502222L)
final case class ComponentSymbol(identifier: Identifier, generics: Seq[RuntimeSymbol], ports: Seq[RuntimeSymbol], owner: Symbol) extends Symbol

@SerialVersionUID(2222707794631501111L)
final case class EntitySymbol(identifier: Identifier, generics: Seq[RuntimeSymbol], ports: Seq[RuntimeSymbol]) extends Symbol {
  val owner = NoSymbol
}

@SerialVersionUID(5924008907619747454L)
final case class ArchitectureSymbol(identifier: Identifier) extends Symbol {
  val owner = NoSymbol
}

@SerialVersionUID(-848787780393119417L)
final case class PackageHeaderSymbol(identifier: Identifier, localSymbols: Map[String, Symbol]) extends Symbol {
  val owner = NoSymbol
}

@SerialVersionUID(-555587780393116666L)
final case class PackageBodySymbol(identifier: Identifier, localSymbols: Map[String, Symbol]) extends Symbol {
  val owner = NoSymbol
}

object RuntimeSymbol {
  type Modifier = Modifier.Value
  object Modifier extends Enumeration {
    val IN, OUT, IN_OUT = Value
  }
}

abstract sealed class RuntimeSymbol extends Symbol {
  @transient var used = false
  val dataType: DataType
  val index: Int
  val isOptional: Boolean //only used for parameters of functions and procedures
  var owner: Symbol
  override lazy val attributes = dataType.attributes

  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this): RuntimeSymbol
}

@SerialVersionUID(6644707794631509820L)
//isDefined is used for deferred constants
final case class ConstantSymbol(identifier: Identifier, dataType: DataType, index: Int, var owner: Symbol, isOptional: Boolean = false, isDefined: Boolean = true, isDeferred: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)
}

@SerialVersionUID(2790429723019134248L)
final case class FileSymbol(identifier: Identifier, dataType: DataType, index: Int, var owner: Symbol, isOptional: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)
}

@SerialVersionUID(3016929919136295810L)
final case class SignalSymbol(identifier: Identifier, dataType: DataType, modifier: RuntimeSymbol.Modifier, signalType: Option[SignalDeclaration.Type], index: Int, var owner: Symbol, isOptional: Boolean = false) extends RuntimeSymbol {
  @transient var driver: ASTNode = null

  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)

  val isResolved: Boolean = this.dataType.resolutionFunction.nonEmpty

  val isUnresolved: Boolean = this.dataType.resolutionFunction.isEmpty

  val signalAttributes = Map(
    ("delayed" -> new AttributeSymbol("delayed", dataType, Option(SymbolTable.timeType))),
    ("stable" -> new AttributeSymbol("stable", SymbolTable.booleanType, Option(SymbolTable.timeType))),
    ("quiet" -> new AttributeSymbol("quiet", SymbolTable.booleanType, Option(SymbolTable.timeType))),
    ("transaction" -> new AttributeSymbol("transaction", SymbolTable.bitType, None)),
    ("event" -> new AttributeSymbol("event", SymbolTable.booleanType, None)),
    ("active" -> new AttributeSymbol("active", SymbolTable.booleanType, None)),
    ("last_event" -> new AttributeSymbol("last_event", SymbolTable.timeType, None)),
    ("last_active" -> new AttributeSymbol("last_active", SymbolTable.timeType, None)),
    ("last_value" -> new AttributeSymbol("last_value", dataType, None)),
    ("driving" -> new AttributeSymbol("driving", SymbolTable.booleanType, None)),
    ("driving_value" -> new AttributeSymbol("driving_value", dataType, None))
    )

  override lazy val attributes = dataType.attributes ++ signalAttributes
}

@SerialVersionUID(1933275516688445881L)
final case class VariableSymbol(identifier: Identifier, dataType: DataType, modifier: RuntimeSymbol.Modifier, index: Int, var owner: Symbol, isOptional: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)
}