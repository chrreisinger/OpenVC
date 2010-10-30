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

import at.jku.ssw.openvc.ast.{Locatable, Identifier}
import at.jku.ssw.openvc.ast.declarations.SignalDeclaration
import at.jku.ssw.openvc.ast.InterfaceList.InterfaceMode
import at.jku.ssw.openvc.ast.declarations.EntityClass.{Value => EntityClass}
import at.jku.ssw.openvc.symbolTable.dataTypes.{PhysicalType, DataType, ScalarType, EnumerationType}
import at.jku.ssw.openvc.symbolTable.{AbstractLibraryArchive, SymbolTable}

@SerialVersionUID(6463078064067295176L)
abstract sealed class Symbol extends Serializable with Locatable {
  val identifier: Identifier

  def owner: Symbol

  lazy val position = identifier.position
  lazy val name = identifier.text
  lazy val attributes = mutable.Map[String, AttributeSymbol]()

  lazy val implementationName: String = this match {
    case entitySymbol: EntitySymbol => owner.name + "/" + entitySymbol.name + "/" + entitySymbol.name
    case configurationSymbol: ConfigurationSymbol => owner.name + "/" + name
    case architectureSymbol: ArchitectureSymbol => owner.name + "/" + architectureSymbol.entity.name + "/" + architectureSymbol.name
    case packageSymbol: PackageSymbol => owner.name + "/" + packageSymbol.name + (if (packageSymbol.isBody) "_body" else "_header")
    case process: ProcessSymbol => process.owner.implementationName + "$" + process.name
    case subprogramSymbol: SubprogramSymbol => subprogramSymbol.owner.implementationName + "$" + subprogramSymbol.name
    case typeSymbol: TypeSymbol => typeSymbol.dataType.implementationName
    case runtimeSymbol: RuntimeSymbol => runtimeSymbol.owner.implementationName
    case _ => name
  }
}

case object NoSymbol extends Symbol {
  val identifier = Identifier("NoSymbol")
  val owner = null
}

@SerialVersionUID(7944297999105157076L)
final case class AliasSymbol(identifier: Identifier, destination: Symbol) extends Symbol {
  val owner = destination.owner
}

@SerialVersionUID(-9011130501372090278L)
final case class LibrarySymbol(identifier: Identifier, @transient libraryArchive: AbstractLibraryArchive) extends Symbol {
  val owner = NoSymbol
}

@SerialVersionUID(6601473217072199115L)
final case class AttributeDeclarationSymbol(identifier: Identifier, dataType: DataType, owner: Symbol) extends Symbol

@SerialVersionUID(7323744940628474136L)
final case class LabelSymbol(label: Identifier, owner: Symbol) extends Symbol {
  val identifier = label
}

@SerialVersionUID(7323744940628474136L)
final case class UnitSymbol(identifier: Identifier, dataType: PhysicalType, owner: Symbol) extends Symbol

@SerialVersionUID(-7117008424236047819L)
final case class EnumerationSymbol(identifier: Identifier, dataType: EnumerationType, owner: Symbol) extends Symbol

@SerialVersionUID(-7441552410085677009L)
final case class ListOfEnumerations(identifier: Identifier, enumerations: Seq[EnumerationSymbol]) extends Symbol {
  val owner = NoSymbol
  override lazy val attributes = error("can not happen")
}

@SerialVersionUID(7013029205564976456L)
final case class ListOfSubprograms(identifier: Identifier, subprograms: Seq[SubprogramSymbol]) extends Symbol {
  val owner = NoSymbol
  override lazy val attributes = error("can not happen")
}

abstract class AttributeSymbol extends Symbol {
  val dataType: DataType
}

@SerialVersionUID(1238244612233452818L)
final case class PreDefinedAttributeSymbol(identifier: Identifier, dataType: DataType, parameter: Option[DataType], isParameterOptional: Boolean) extends AttributeSymbol {
  def this(name: String, dataType: DataType, parameter: Option[DataType], isParameterOptional: Boolean = false) = this (Identifier(name), dataType, parameter, isParameterOptional)

  val owner = NoSymbol
}

@SerialVersionUID(1238244612233452818L)
final case class UserDefinedAttributeSymbol(identifier: Identifier, dataType: DataType, attributeDeclaration: AttributeDeclarationSymbol, owner: Symbol, value: Either[(String, String), (String, String, String)]) extends AttributeSymbol

abstract sealed class SubprogramSymbol extends Symbol {
  var parameters: Seq[RuntimeSymbol]
  val owner: Symbol
  val (isStatic, isSynchronized) = owner match {
    case _: PackageSymbol | Runtime => (true, false)
    case typeSymbol: TypeSymbol => (false, true)
    case _ => (false, false)
  }
  val implemented: Boolean
  val returnTypeOption: Option[DataType]
}

@SerialVersionUID(-7427096092567821868L)
final case class FunctionSymbol(identifier: Identifier, var parameters: Seq[RuntimeSymbol], returnType: DataType, owner: Symbol, isPure: Boolean, implemented: Boolean = true) extends SubprogramSymbol {
  val returnTypeOption = Option(returnType)
}

@SerialVersionUID(1492091303227052136L)
final case class ProcedureSymbol(identifier: Identifier, var parameters: Seq[RuntimeSymbol], owner: Symbol, var isPassive: Boolean, implemented: Boolean = true) extends SubprogramSymbol {
  val returnTypeOption = None
  lazy val copyBackSymbols = parameters.collect{
    _ match {
      case varSymbol: VariableSymbol if (varSymbol.modifier != InterfaceMode.IN && varSymbol.dataType.isInstanceOf[ScalarType]) => varSymbol
    }
  }
  lazy val needsCopyBack = copyBackSymbols.nonEmpty
}

@SerialVersionUID(-1724650145084937620L)
case object Runtime extends Symbol {
  val identifier = Identifier(at.jku.ssw.openvc.codeGenerator.ByteCodeGenerator.RUNTIME)
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
  override lazy val attributes = mutable.Map(dataType.attributes.toList: _*)
}

@SerialVersionUID(4074315113387803680L)
final case class SubTypeSymbol(identifier: Identifier, dataType: DataType, owner: Symbol) extends Symbol {
  override lazy val attributes = mutable.Map(dataType.attributes.toList: _*)
}

@SerialVersionUID(1111707794631502222L)
final case class ComponentSymbol(identifier: Identifier, var generics: Seq[RuntimeSymbol], var ports: Seq[RuntimeSymbol], owner: Symbol) extends Symbol

@SerialVersionUID(2222707794631501111L)
final case class EntitySymbol(identifier: Identifier, var generics: Seq[RuntimeSymbol], var ports: Seq[RuntimeSymbol], owner: Symbol) extends Symbol

@SerialVersionUID(-6241717326685460079L)
final case class ConfigurationSymbol(identifier: Identifier, owner: Symbol) extends Symbol

@SerialVersionUID(5924008907619747454L)
final case class ArchitectureSymbol(identifier: Identifier, entity: EntitySymbol, owner: Symbol) extends Symbol

@SerialVersionUID(-848787780393119417L)
final case class PackageSymbol(identifier: Identifier, var localSymbols: Map[String, Symbol], isBody: Boolean, owner: Symbol) extends Symbol

abstract sealed class RuntimeSymbol extends Symbol {
  @transient var used = false
  val dataType: DataType
  val index: Int
  val isOptional: Boolean
  val isParameter: Boolean
  //only used for parameters of functions and procedures
  val owner: Symbol
  override lazy val attributes = mutable.Map(dataType.attributes.toList: _*)

  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this): RuntimeSymbol
}

@SerialVersionUID(6644707794631509820L)
//isDefined is used for deferred constants
final case class ConstantSymbol(identifier: Identifier, dataType: DataType, index: Int, owner: Symbol, isOptional: Boolean = false, isDefined: Boolean = true, isDeferred: Boolean = false, isParameter: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)
}

@SerialVersionUID(2790429723019134248L)
final case class FileSymbol(identifier: Identifier, dataType: DataType, index: Int, owner: Symbol, isOptional: Boolean = false, isParameter: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)
}

@SerialVersionUID(3016929919136295810L)
final case class SignalSymbol(identifier: Identifier, dataType: DataType, modifier: InterfaceMode, signalType: Option[SignalDeclaration.Type], index: Int, owner: Symbol, isOptional: Boolean = false, isParameter: Boolean = false) extends RuntimeSymbol {
  @transient var driver: ProcessSymbol = null

  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)

  val isResolved: Boolean = this.dataType.resolutionFunction.isDefined

  val isUnresolved: Boolean = this.dataType.resolutionFunction.isEmpty

  override lazy val attributes = mutable.Map(dataType.attributes.toList: _*) ++ Map(
    ("delayed" -> new PreDefinedAttributeSymbol("delayed", dataType, Option(SymbolTable.timeType))),
    ("stable" -> new PreDefinedAttributeSymbol("stable", SymbolTable.booleanType, Option(SymbolTable.timeType))),
    ("quiet" -> new PreDefinedAttributeSymbol("quiet", SymbolTable.booleanType, Option(SymbolTable.timeType))),
    ("transaction" -> new PreDefinedAttributeSymbol("transaction", SymbolTable.bitType, None)),
    ("event" -> new PreDefinedAttributeSymbol("event", SymbolTable.booleanType, None)),
    ("active" -> new PreDefinedAttributeSymbol("active", SymbolTable.booleanType, None)),
    ("last_event" -> new PreDefinedAttributeSymbol("last_event", SymbolTable.timeType, None)),
    ("last_active" -> new PreDefinedAttributeSymbol("last_active", SymbolTable.timeType, None)),
    ("last_value" -> new PreDefinedAttributeSymbol("last_value", dataType, None)),
    ("driving" -> new PreDefinedAttributeSymbol("driving", SymbolTable.booleanType, None)),
    ("driving_value" -> new PreDefinedAttributeSymbol("driving_value", dataType, None))
  )

}

@SerialVersionUID(1933275516688445881L)
final case class VariableSymbol(identifier: Identifier, dataType: DataType, modifier: InterfaceMode, index: Int, owner: Symbol, isOptional: Boolean = false, isParameter: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)
}