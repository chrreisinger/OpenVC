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

import at.jku.ssw.openvc.ast
import ast.{Identifier, Locatable}
import ast.InterfaceList.{InterfaceMode, Mode}
import ast.expressions.Expression
import ast.declarativeItems.SignalDeclaration
import ast.declarativeItems.EntityClass.{Value => EntityClass}
import at.jku.ssw.openvc.symbolTable.dataTypes.{PhysicalType, DataType, ScalarType, EnumerationType}
import at.jku.ssw.openvc.symbolTable.{AbstractLibraryArchive, SymbolTable}

@SerialVersionUID(6463078064067295176L)
abstract sealed class Symbol extends Serializable with Locatable {
  @transient val identifier: Identifier

  val owner: Symbol

  @transient lazy val position = identifier.position
  val name = identifier.text

  lazy val fullName: String =
    if (owner == NoSymbol) name
    else owner.fullName + '.' + name

  lazy val attributes = mutable.Map[String, AttributeSymbol]()

  lazy val implementationName: String = this match {
    case entitySymbol: EntitySymbol => owner.name + "/" + entitySymbol.name + "/" + entitySymbol.name
    case _: ConfigurationSymbol | _: ContextSymbol => owner.name + "/" + name
    case architectureSymbol: ArchitectureSymbol => owner.name + "/" + architectureSymbol.entity.name + "/" + architectureSymbol.name
    case packageSymbol: PackageSymbol => owner.name + "/" + packageSymbol.name + (if (packageSymbol.isBody) "_body" else "_header")
    case process: ProcessSymbol => process.owner.implementationName + "$" + process.name
    case subprogramSymbol: SubprogramSymbol => subprogramSymbol.owner.implementationName + "$" + subprogramSymbol.name
    case typeSymbol: TypeSymbol => typeSymbol.dataType.implementationName
    case runtimeSymbol: RuntimeSymbol => runtimeSymbol.owner.implementationName
    case _ => name
  }
}

@SerialVersionUID(2245320974016552352L)
case object NoSymbol extends Symbol {
  @transient lazy val identifier = Identifier("NoSymbol")
  lazy val owner = error("cannot happen")
}

@SerialVersionUID(7944297999105157076L)
final case class AliasSymbol(@transient identifier: Identifier, destination: Symbol) extends Symbol {
  val owner = destination.owner
}

@SerialVersionUID(191777956741368631L)
final case class AliasExpression(@transient identifier: Identifier, expression: Expression) extends Symbol {
  val owner = NoSymbol
}

@SerialVersionUID(-9011130501372090278L)
final case class LibrarySymbol(@transient identifier: Identifier, libraryArchive: AbstractLibraryArchive) extends Symbol {
  val owner = NoSymbol

  override def equals(other: Any): Boolean = other match {
    case anyRef: AnyRef if (anyRef eq this) => true
    case librarySymbol: LibrarySymbol => librarySymbol.name == this.name
    case _ => false
  }

  override val hashCode = name.hashCode
}

@SerialVersionUID(6601473217072199115L)
final case class AttributeDeclarationSymbol(@transient identifier: Identifier, dataType: DataType, owner: Symbol) extends Symbol

@SerialVersionUID(7323744940628474136L)
final case class LabelSymbol(@transient label: Identifier, owner: Symbol) extends Symbol {
  @transient lazy val identifier = label
}

@SerialVersionUID(7323744940628474136L)
final case class UnitSymbol(@transient identifier: Identifier, dataType: PhysicalType, owner: Symbol) extends Symbol

@SerialVersionUID(-7117008424236047819L)
final case class EnumerationSymbol(@transient identifier: Identifier, dataType: EnumerationType, owner: Symbol) extends Symbol

@SerialVersionUID(-7441552410085677009L)
final case class ListOfEnumerations(@transient identifier: Identifier, enumerations: Seq[EnumerationSymbol]) extends Symbol {
  val owner = NoSymbol
  override lazy val attributes = error("cannot happen")
}

@SerialVersionUID(7013029205564976456L)
final case class ListOfSubprograms(@transient identifier: Identifier, subprograms: Seq[SubprogramSymbol]) extends Symbol {
  val owner = NoSymbol
  override lazy val attributes = error("cannot happen")
}

@SerialVersionUID(-1709551041398947956L)
abstract class AttributeSymbol extends Symbol {
  val dataType: DataType
}

@SerialVersionUID(1238244612233452818L)
final case class PreDefinedAttributeSymbol(@transient identifier: Identifier, dataType: DataType, parameter: Option[DataType], isParameterOptional: Boolean) extends AttributeSymbol {
  def this(name: String, dataType: DataType, parameter: Option[DataType], isParameterOptional: Boolean = false) = this (Identifier(name), dataType, parameter, isParameterOptional)

  val owner = NoSymbol
}

@SerialVersionUID(1238244612233452818L)
final case class UserDefinedAttributeSymbol(@transient identifier: Identifier, dataType: DataType, attributeDeclaration: AttributeDeclarationSymbol, owner: Symbol, symbol: ConstantSymbol) extends AttributeSymbol

@SerialVersionUID(7108835305370866018L)
final case class ForeignAttributeSymbol(@transient identifier: Identifier, owner: Symbol, jvmSignature: Either[(String, String), (String, String, String)]) extends AttributeSymbol {
  lazy val dataType: DataType = SymbolTable.stringType
  lazy val attributeDeclaration: AttributeDeclarationSymbol = SymbolTable.foreignAttribute
}

@SerialVersionUID(3823095671559970597L)
abstract sealed class SubprogramSymbol extends Symbol {
  var parameters: Seq[RuntimeSymbol]
  val owner: Symbol
  val isImplemented: Boolean
  val returnTypeOption: Option[DataType]
  val (isStatic, isSynchronized) = owner match {
    case _: PackageSymbol | Runtime => (true, false)
    case typeSymbol: TypeSymbol => (false, true)
    case _ => (false, false)
  }

  /**
   * we need to mangle the name of a subprogram because different valid VHDL subprograms can map to the same JVM signature
   * e.g. in the example both procedures map to void test(int x);
   * {{{
   * type intA is range 0 to 10;
   * type intB is range 0 to 10;
   *
   * procedure test(x:IntA) is
   * begin
   * end;
   *
   * procedure test(x:IntB) is
   * begin
   * end;
   * }}}
   */
  lazy val mangledName = name + "$" + (parameters.map(_.dataType.name).hashCode + (31 * (returnTypeOption match {
    case Some(returnType) => returnType.name.hashCode
    case None => "$None".hashCode
  })))
}

@SerialVersionUID(-7427096092567821868L)
final case class FunctionSymbol(@transient identifier: Identifier, var parameters: Seq[RuntimeSymbol], returnType: DataType, owner: Symbol, isPure: Boolean, isImplemented: Boolean = true) extends SubprogramSymbol {
  val returnTypeOption = Option(returnType)
}

@SerialVersionUID(1492091303227052136L)
final case class ProcedureSymbol(@transient identifier: Identifier, var parameters: Seq[RuntimeSymbol], owner: Symbol, var isPassive: Boolean, isImplemented: Boolean = true) extends SubprogramSymbol {
  val returnTypeOption = None
  lazy val copyBackSymbols = parameters.collect {
    _ match {
      case varSymbol: VariableSymbol if (varSymbol.mode != Mode.IN && varSymbol.dataType.isInstanceOf[ScalarType]) => varSymbol
    }
  }
  lazy val needsCopyBack = copyBackSymbols.nonEmpty
}

@SerialVersionUID(-1724650145084937620L)
case object Runtime extends Symbol {
  @transient lazy val identifier = Identifier(at.jku.ssw.openvc.backend.jvm.ByteCodeGenerator.RUNTIME)
  val owner = NoSymbol
}

@SerialVersionUID(4341399901500604597L)
final case class GroupTemplateSymbol(@transient identifier: Identifier, items: Seq[EntityClass], isInfinite: Boolean, owner: Symbol) extends Symbol

@SerialVersionUID(-3051930707092602812L)
final case class GroupSymbol(@transient identifier: Identifier, owner: Symbol) extends Symbol

@SerialVersionUID(-6214450543318369430L)
final case class ProcessSymbol(@transient identifier: Identifier, owner: Symbol, var isPassive: Boolean, isPostponed: Boolean) extends Symbol

@SerialVersionUID(-5713001752033143203L)
final case class BlockSymbol(@transient identifier: Identifier, owner: Symbol) extends Symbol

@SerialVersionUID(7523805927766617817L)
final case class TypeSymbol(@transient identifier: Identifier, dataType: DataType, owner: Symbol) extends Symbol {
  override lazy val attributes = mutable.Map(dataType.attributes.toSeq: _*)
}

@SerialVersionUID(4074315113387803680L)
final case class SubTypeSymbol(@transient identifier: Identifier, dataType: DataType, owner: Symbol) extends Symbol {
  override lazy val attributes = mutable.Map(dataType.attributes.toSeq: _*)
}

@SerialVersionUID(1111707794631502222L)
final case class ComponentSymbol(@transient identifier: Identifier, var generics: Seq[RuntimeSymbol], var ports: Seq[RuntimeSymbol], owner: Symbol) extends Symbol

@SerialVersionUID(-3111442902650095529L)
abstract sealed class DesignEntity extends Symbol with Serializable

@SerialVersionUID(2222707794631501111L)
final case class EntitySymbol(@transient identifier: Identifier, var generics: Seq[RuntimeSymbol], var ports: Seq[RuntimeSymbol], owner: Symbol) extends DesignEntity

@SerialVersionUID(-6241717326685460079L)
final case class ConfigurationSymbol(@transient identifier: Identifier, owner: Symbol) extends DesignEntity

@SerialVersionUID(5924008907619747454L)
final case class ArchitectureSymbol(@transient identifier: Identifier, entity: EntitySymbol, owner: Symbol) extends DesignEntity

@SerialVersionUID(-5716129771330926555L)
final case class ContextSymbol(@transient identifier: Identifier, localSymbols: Map[String, Symbol], owner: Symbol) extends DesignEntity

@SerialVersionUID(-848787780393119417L)
final case class PackageSymbol(@transient identifier: Identifier, var localSymbols: Map[String, Symbol], isBody: Boolean, owner: Symbol) extends DesignEntity {
  override def equals(other: Any): Boolean = other match {
    case anyRef: AnyRef if (anyRef eq this) => true
    case packageSymbol: PackageSymbol => packageSymbol.owner == this.owner && packageSymbol.name == this.name
    case _ => false
  }

  override val hashCode = name.hashCode + 31 * (if (isBody) 1231 else 1237) + (31 * owner.hashCode)

  override val toString = "PackageSymbol(" + name + "," + isBody + "," + owner + ")"
}

@SerialVersionUID(4927690090720014111L)
abstract sealed class RuntimeSymbol extends Symbol {
  val dataType: DataType
  val index: Int
  val isOptional: Boolean
  val isParameter: Boolean
  //only used for parameters of functions and procedures
  val owner: Symbol
  override lazy val attributes = mutable.Map(dataType.attributes.toSeq: _*)

  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this): RuntimeSymbol
}

@SerialVersionUID(6644707794631509820L)
//isDefined is used for deferred constants
final case class ConstantSymbol(@transient identifier: Identifier, dataType: DataType, index: Int, owner: Symbol, isOptional: Boolean = false, isDefined: Boolean = true, isDeferred: Boolean = false, isParameter: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)
}

@SerialVersionUID(2790429723019134248L)
final case class FileSymbol(@transient identifier: Identifier, dataType: DataType, index: Int, owner: Symbol, isOptional: Boolean = false, isParameter: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)
}

@SerialVersionUID(3016929919136295810L)
final case class SignalSymbol(@transient identifier: Identifier, dataType: DataType, mode: InterfaceMode, signalType: Option[SignalDeclaration.Type], index: Int, owner: Symbol, isOptional: Boolean = false, isParameter: Boolean = false) extends RuntimeSymbol {
  @transient var driver: ProcessSymbol = null

  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)

  val isResolved = this.dataType.resolutionFunction.isDefined

  val isUnresolved = this.dataType.resolutionFunction.isEmpty

  val isPort = !owner.isInstanceOf[SubprogramSymbol] && isParameter

  override lazy val attributes = mutable.Map(dataType.attributes.toSeq: _*) ++ Map(
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
final case class VariableSymbol(@transient identifier: Identifier, dataType: DataType, mode: InterfaceMode, index: Int, owner: Symbol, isOptional: Boolean = false, isParameter: Boolean = false) extends RuntimeSymbol {
  def makeCopy(identifier: Identifier, dataType: DataType, owner: Symbol = this) = this.copy(identifier = identifier, dataType = dataType, owner = owner)
}