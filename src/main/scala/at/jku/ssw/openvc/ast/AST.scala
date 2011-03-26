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

package at.jku.ssw.openvc.ast {

import at.jku.ssw.openvc.util.{TripleEither, Position, NoPosition}
import at.jku.ssw.openvc.symbolTable.dataTypes.{DataType, RangeType, ConstrainedRangeType, NoType}
import at.jku.ssw.openvc.symbolTable.symbols.NoSymbol
import expressions.{NoExpression, Name, Aggregate, Expression}

/**
 * Marks all classes that contain a position information
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 */
trait Locatable {
  /**
   * the position in the source file
   */
  val position: Position
}

/**
 * Base class for all abstract syntax tree nodes
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 */
abstract sealed class ASTNode extends Locatable

case object NoNode extends ASTNode with sequentialStatements.SequentialStatement
with concurrentStatements.ConcurrentStatement
with declarativeItems.DeclarativeItem
with simultaneousStatements.SimultaneousStatement
with designUnits.ContextItem
with designUnits.LibraryUnit {
  val position = NoPosition
  val label = None
  val identifier = Identifier.NoIdentifier
  val endIdentifier = None
  val symbol = NoSymbol
}

object Identifier {
  val NoIdentifier = Identifier("$NoIdentifier")

  def apply(position: Position, text: String) = new Identifier(position, text)

  def apply(text: String) = new Identifier(NoPosition, text)
}

final class Identifier(val position: Position, val originalText: String) extends Locatable {
  val text = originalText.replace("\"", "")
  override val toString: String = this.text

  override def equals(other: Any): Boolean = other match {
    case anyRef: AnyRef if (anyRef eq this) => true
    case str: String => str == this.text
    case id: Identifier => id.text == this.text
    case _ => false
  }

  override val hashCode = text.hashCode
}

/**
 * Represents a target of a variable or signal assignment
 *
 * grammar: <pre>
 *  name
 *
 * | aggregate
 * </pre>
 *
 * example: {{{ a(0).x  }}} {{{ (a(0),a(1),a(2) for aggregate  }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.target]]
 * @see [[at.jku.ssw.openvc.ast.sequentialStatements.SignalAssignmentStatement]]
 * @see [[at.jku.ssw.openvc.ast.sequentialStatements.VariableAssignmentStatement]]
 * @see [[at.jku.ssw.openvc.ast.concurrentStatements.ConcurrentSignalAssignmentStatement]]
 * @param nameOrAggregate the target of the assignment wich is either a name or aggregate
 * @param expression the expression used in the code generator
 */
final case class Target(nameOrAggregate: Either[Name, Aggregate], expression: Expression = NoExpression)

object Range {
  type Direction = Direction.Value

  object Direction extends Enumeration {
    val To, Downto = Value
  }

}

final class Range(val expressionsOrName: Either[(Expression, Range.Direction, Expression), Expression], val dataType: RangeType = null) extends Locatable {
  val position = expressionsOrName match {
    case Left((expr, _, _)) => expr.firstPosition
    case Right(attributeName) => attributeName.position
  }
}

object DelayMechanism {
  type DelayType = DelayType.Value

  object DelayType extends Enumeration {
    val TRANSPORT, INERTIAL = Value
  }

}

final class DelayMechanism(val delayType: DelayMechanism.DelayType, val rejectExpression: Option[Expression])

final class DiscreteRange(val rangeOrSubTypeIndication: Either[Range, SubTypeIndication], val dataType: ConstrainedRangeType = null) extends Locatable {
  val position = rangeOrSubTypeIndication match {
    case Left(range) => range.position
    case Right(subType) => subType.position
  }
}

/**
 * Represents a subtype indication
 *
 * grammar: <pre>
 * selected_name [selected_name] [constraint] [{ams}?=> '''TOLERANCE''' expression]
 *
 * where contraint is:
 *
 *  '''RANGE''' range
 *
 * | ( discrete_range {,  discrete_range} )
 * </pre>
 *
 * example: {{{ integer range 0 to 10 }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.subtype_indication]]
 * @param resolutionFunction the optional name of a resolution function
 * @param typeName the name of the type or subtype
 * @param constraint the optional contraint for the new subtype
 * @param dataType the new created data Type for this sub type indication
 * @param amsToleranceExpression the AMS tolerance expression
 */
final case class SubTypeIndication(resolutionFunction: Option[SelectedName], typeName: SelectedName, constraint: Option[Either[Range, Seq[DiscreteRange]]], amsToleranceExpression: Option[Expression], dataType: DataType = NoType) extends Locatable {
  val position = resolutionFunction.getOrElse(typeName).position
}

final case class SubNatureIndication(natureMark: SelectedName, ranges: Option[Seq[DiscreteRange]], toleranceExpression: Option[Expression], acrossExpression: Option[Expression])

/**
 * Represents a signature for a subprogram or a enumeration literal
 *
 * grammar: <pre> [ selected_name_list ] [ return type_mark ] </pre>
 *
 * example: {{{ [integer,integer,real return integer] }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.signature]]
 * @param parameterTypes the optional list of parameter types for a subprogram
 * @param returnType the optional return type of a subprogram or a enumeration literal
 */
final class Signature(val position: Position, val parameterTypes: Option[Seq[SelectedName]], val returnType: Option[SelectedName]) extends Locatable

object Waveform {

  /**
   * Represents a element of a waveform
   *
   * grammar: <pre> ''value_''expression ['''AFTER''' ''time_''expression] </pre>
   *
   * example: {{{ '0' after 20 ns }}}
   * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
   * @see [[at.jku.ssw.openvc.parser.VHDLParser.waveform_element]]
   * @param valueExpression the value of this element
   * @param timeExpression the optional delay of this element
   */
  final class Element(val valueExpression: Expression, val timeExpression: Option[Expression])

}

/**
 * Represents a waveform
 *
 * grammar: <pre>
 * waveform_element{, waveform_element}
 *
 * | '''UNAFFECTED'''
 * </pre>
 *
 * example: {{{
 *  '0' after 20 ns,
 *  '1' after 30 ns,
 *  '0' after 40 ns;
 * }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.waveform]]
 * @param elements the elements of this wavefrom
 */
final class Waveform(val position: Position, val elements: Seq[Waveform.Element]) extends Locatable {
  val isUnaffected = this.elements.isEmpty
}

object Choices {

  final class Choice(val position: Position, val rangeOrExpressionOrIdentifier: Option[TripleEither[DiscreteRange, Expression, Identifier]]) extends Locatable {
    // expression,discreteRange == None => OTHERS
    val isOthers = rangeOrExpressionOrIdentifier.isEmpty
  }

}

object AssociationList {

  final case class Element(formalPart: Option[Either[Identifier, (SelectedName, Name.Part)]], actualPart: Either[Expression, Identifier]) {
    val isActualPartOpen = actualPart.isLeft
    val actualPosition = actualPart match {
      case Left(expression) => expression.firstPosition
      case Right(openIdentifier) => openIdentifier.position
    }
  }

}

final case class AssociationList(elements: Seq[AssociationList.Element], parameters: Seq[Expression] = Seq(), symbols: Seq[at.jku.ssw.openvc.symbolTable.symbols.RuntimeSymbol] = Seq())

/**
 * Represents a selected name, which is used to identify a VHDL entity e.g a procedure in a package
 *
 * grammar: <pre> name_prefix { name_selected_part } </pre>
 *
 * example: {{{ std.standard.integer }}} {{{ Work.myPackage.myProcedure }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.selected_name]]
 * @param identifiers the identifiers that identify the VHDL entity
 */
final class SelectedName(val identifiers: Seq[Identifier]) extends Locatable {
  val position = identifiers match {
    case Seq(identifier, _*) => identifier.position
    case _ => NoPosition
  }
  /**
   * returns the identifiers with dots between them, e.g. "std.standard.integer"
   */
  override val toString = identifiers.mkString(".")
}

object InterfaceList {
  type InterfaceMode = Mode.Value

  object Mode extends Enumeration {
    val IN, OUT, INOUT, BUFFER, LINKAGE = Value
  }

  sealed trait AbstractInterfaceElement

  case object NoElement extends AbstractInterfaceElement
  
  abstract sealed class InterfaceObjectDeclaration extends AbstractInterfaceElement with Locatable {
    val identifiers: Seq[Identifier]
    val expression: Option[Expression]
    val mode: Option[InterfaceMode]
    val subType: SubTypeIndication
    val position = identifiers match {
      case Seq(identifier, _*) => identifier.position
      case _ => NoPosition
    }
  }

  final case class InterfaceConstantDeclaration(identifiers: Seq[Identifier], subType: SubTypeIndication, expression: Option[Expression]) extends InterfaceObjectDeclaration {
    val mode = Some(Mode.IN)
  }

  final case class InterfaceVariableDeclaration(identifiers: Seq[Identifier], mode: Option[InterfaceMode], subType: SubTypeIndication, expression: Option[Expression]) extends InterfaceObjectDeclaration

  final case class InterfaceSignalDeclaration(identifiers: Seq[Identifier], mode: Option[InterfaceMode], subType: SubTypeIndication, isBus: Boolean, expression: Option[Expression]) extends InterfaceObjectDeclaration

  final case class InterfaceFileDeclaration(identifiers: Seq[Identifier], subType: SubTypeIndication) extends InterfaceObjectDeclaration {
    val expression: Option[Expression] = None
    val mode = Some(Mode.IN)
  }

  final case class InterfaceTerminalDeclaration(identifiers: Seq[Identifier], subNature: SubNatureIndication) extends AbstractInterfaceElement

  final case class InterfaceQuantityDeclaration(identifiers: Seq[Identifier], mode: Option[InterfaceMode], subType: SubTypeIndication, expression: Option[Expression]) extends AbstractInterfaceElement

  final case class InterfaceTypeDeclaration(typeIdentifier: Identifier) extends AbstractInterfaceElement

  final case class InterfacePackageDeclaration(identifier: Identifier, packageName: SelectedName, genericMapAspect: Either[AssociationList, Boolean]) extends AbstractInterfaceElement

  final case class InterfaceProcedureDeclaration(identifier: Identifier, parameterInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], default: Option[Option[SelectedName]]) extends AbstractInterfaceElement

  final case class InterfaceFunctionDeclaration(isPure: Boolean, identifier: Identifier, parameterInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], returnType: SelectedName, default: Option[Option[SelectedName]]) extends AbstractInterfaceElement

}

final class ComponentSpecification(val instantiations: Either[Seq[Identifier], Identifier], val componentName: SelectedName)

final class BindingIndication(val entityAspect: Option[Option[Either[(SelectedName, Option[Identifier]), SelectedName]]], val genericMap: Option[AssociationList], val portMap: Option[AssociationList])

final class BlockConfigurationSpecification(val nameOrLabel: Either[SelectedName, (Identifier, Option[TripleEither[DiscreteRange, Identifier, Expression]])]) //SelectedName,(label,blockConfigureIndex)

final class BlockConfiguration(val blockConfigSpec: BlockConfigurationSpecification, val useClauses: Seq[designUnits.UseClause], val configurations: Seq[Either[BlockConfiguration, ComponentConfiguration]])

final class ComponentConfiguration(val componentSpecification: ComponentSpecification, val bindingIndication: Option[BindingIndication], val blockConfiguration: Option[BlockConfiguration])

final class BreakElement(val forQuantityName: Option[Name], val name: Name, val expression: Expression)

package designUnits {

import at.jku.ssw.openvc.ast.declarativeItems.DeclarativeItem
import at.jku.ssw.openvc.symbolTable.symbols._
import at.jku.ssw.openvc.ast.concurrentStatements.ConcurrentStatement

sealed trait ContextItem extends ASTNode

/**
 * Represents a use clause
 *
 * grammar: <pre> '''USE''' selected_name_list; </pre>
 *
 * example: {{{ use ieee.std_logic_1164.all; std.standard.all; ieee.math_complex."+"; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.use_clause]]
 * @param names the names of the symbols to import in the current scope of the symbol table
 */
final case class UseClause(position: Position, names: Seq[SelectedName]) extends declarativeItems.DeclarativeItem with ContextItem

final case class LibraryClause(position: Position, libraries: Seq[Identifier]) extends ContextItem

final case class ContextReference(position: Position, contexts: Seq[SelectedName]) extends ContextItem

/**
 * Represents a design file, a design file is the ast of the complete source file
 *
 * grammar: <pre> (design_unit)+ EOF </pre>
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.design_file]]
 * @param designUnits the design units in this design file
 */
final case class DesignFile(designUnits: Seq[DesignUnit]) extends ASTNode {
  lazy val position = designUnits match {
    case Seq(unit, _*) => unit.position
    case _ => NoPosition
  }
}

final case class DesignUnit(contextItems: Seq[ContextItem], libraryUnit: LibraryUnit) extends ASTNode {
  lazy val position = contextItems match {
    case Seq(contextItem, _*) => contextItem.position
    case _ => libraryUnit.position
  }
}

/**
 * Base class for all library units
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.library_unit]]
 */
sealed trait LibraryUnit extends ASTNode {
  val symbol: Symbol
  val identifier: Identifier
  val endIdentifier: Option[Identifier]
}

final case class ConfigurationDeclaration(position: Position, identifier: Identifier, declarativeItems: Seq[DeclarativeItem], entityName: SelectedName, blockConfiguration: BlockConfiguration,
                                          endIdentifier: Option[Identifier], symbol: ConfigurationSymbol = null) extends LibraryUnit

/**
 * Represents a architecture body
 *
 * grammar: <pre>
 * '''ARCHITECTURE''' identifier '''OF''' selected_name '''IS'''
 *
 * {block_declarative_item}
 *
 * '''BEGIN'''
 *
 * {concurrent_statement}
 *
 * '''END''' ['''ARCHITECTURE'''] [identifier];
 *
 * example: {{{
 * architecture rtl of alu is
 * begin
 *  a<='0';
 *  b<='1';
 * end architecture rtl;
 * }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.architecture_body]]
 * @param entityName the name of the entity to which this architecture body is associated
 * @param concurrentStatements the concurrent statments in this architecture body
 */
final case class ArchitectureDeclaration(position: Position, identifier: Identifier, declarativeItems: Seq[DeclarativeItem], entityName: SelectedName,
                                         concurrentStatements: Seq[ConcurrentStatement], endIdentifier: Option[Identifier],
                                         symbol: ArchitectureSymbol = null) extends LibraryUnit

final case class EntityDeclaration(position: Position, identifier: Identifier, genericInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], portInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]],
                                   declarativeItems: Seq[DeclarativeItem], concurrentStatements: Seq[ConcurrentStatement], endIdentifier: Option[Identifier], symbol: EntitySymbol = null) extends LibraryUnit

/**
 * Represents a package declaration
 *
 * grammar: <pre>
 * '''PACKAGE''' identifier '''IS'''
 *
 * 	[{vhdl2008}?=> generic_clause ;
 *
 *  [(generic_map_aspect ;)]]
 *
 * {package_body_declarative_item}
 *
 * '''END''' ['''PACKAGE'''] [identifier];
 * </pre>
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.package_declaration]]
 * @param genericInterfaceList generic interface list
 * @param genericAssociationList generic associations list
 */
final case class PackageDeclaration(position: Position, identifier: Identifier, genericInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], genericAssociationList: Option[AssociationList], declarativeItems: Seq[DeclarativeItem], endIdentifier: Option[Identifier], symbol: PackageSymbol = null) extends LibraryUnit with DeclarativeItem

/**
 * Represents a package body declaration
 *
 * grammar: <pre>
 * '''PACKAGE''' '''BODY''' identifier '''IS'''
 *
 * {package_body_declarative_item}
 *
 * '''END''' ['''PACKAGE''' '''BODY'''] [identifier];
 * </pre>
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.package_body]]
 */
final case class PackageBodyDeclaration(position: Position, identifier: Identifier, declarativeItems: Seq[DeclarativeItem], endIdentifier: Option[Identifier], symbol: PackageSymbol = null) extends LibraryUnit with DeclarativeItem

final case class PackageInstantiationDeclaration(position: Position, identifier: Identifier, packageName: SelectedName, genericAssociationList: Option[AssociationList], symbol: Symbol = NoSymbol) extends LibraryUnit with DeclarativeItem {
  val endIdentifier: Option[Identifier] = None
}

final case class ContextDeclaration(position: Position, identifier: Identifier, contextItems: Seq[ContextItem], endIdentifier: Option[Identifier], symbol: ContextSymbol = null) extends LibraryUnit

}

package declarativeItems {

import at.jku.ssw.openvc.ast.sequentialStatements.SequentialStatement
import at.jku.ssw.openvc.symbolTable.symbols._
import at.jku.ssw.openvc.symbolTable.dataTypes.ProtectedType

/**
 * Base class for all declarative items
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.entity_declarative_item]] ams_simultaneous_procedural_declarative_item
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.block_declarative_item]]
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.configuration_declarative_item]]
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.package_declarative_item]]
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.package_body_declarative_item]]
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.subprogram_declarative_item]]
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.protected_type_declarative_item]]
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.protected_type_body_declarative_item]]
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.process_declarative_item]]
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.ams_simultaneous_procedural_declarative_item]]
 */
sealed trait DeclarativeItem extends ASTNode

object EntityClass extends Enumeration {
  val ENTITY, ARCHITECTURE, CONFIGURATION, PACKAGE, PROCEDURE, FUNCTION, TYPE, SUBTYPE, CONSTANT, SIGNAL, VARIABLE, FILE, COMPONENT, LABEL,
  LITERAL, UNITS, GROUP, NATURE, SUBNATURE, QUANTITY, TERMINAL = Value
}

final case class AliasDeclaration(position: Position, identifier: Identifier, subType: Option[SubTypeIndication], name: Expression, signature: Option[Signature], symbol: RuntimeSymbol = null) extends DeclarativeItem

final case class AttributeSpecification(position: Position, identifier: Identifier, entityList: Either[Seq[(Identifier, Option[Signature])], Identifier], entityClass: EntityClass.Value, expression: Expression, symbol: ConstantSymbol = null) extends DeclarativeItem

/**
 * Represents a attribute declaration
 *
 * grammar: <pre> '''ATTRIBUTE''' identifier : type_mark; </pre>
 *
 * example: {{{ attribute myAttribute : string; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.attribute_declaration]]
 * @param identifier the name of the attribute
 * @param typeName the type of the attribute
 */
final case class AttributeDeclaration(position: Position, identifier: Identifier, typeName: SelectedName) extends DeclarativeItem

/**
 * Base class for all object declarations
 *
 * An object declaration declares an object of a specified type. Such an object is called an explicitly declared object.
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 */
abstract sealed class ObjectDeclaration extends DeclarativeItem {
  /**
   * the names of the new objects
   */
  val identifiers: Seq[Identifier]
  /**
   * the type of the new objects
   */
  val subType: SubTypeIndication
  /**
   * the new created symbols for this declaration
   */
  val symbols: Seq[RuntimeSymbol]
}

/**
 * Represents a variable declaration
 *
 * grammar: <pre> ['''SHARED'''] '''VARIABLE''' identifier_list : subtype_indication [:= expression]; </pre>
 *
 * example: {{{ variable i : INTEGER range 0 to 99 := 0; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.variable_declaration]]
 * @param isShared true if this is a shared variable declaration
 * @param initialValue the optional initial value of the variables
 */
final case class VariableDeclaration(position: Position, isShared: Boolean, identifiers: Seq[Identifier], subType: SubTypeIndication, initialValue: Option[Expression], symbols: Seq[RuntimeSymbol] = Seq())
  extends ObjectDeclaration

/**
 * Represents a constant declaration
 *
 * grammar: <pre> '''CONSTANT''' identifier_list : subtype_indication [:= expression];  </pre>
 *
 * example: {{{ constant pi : real := 3.141592; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.constant_declaration]]
 * @param value if None this are deferred constants else it specifies the value of the constants
 */
final case class ConstantDeclaration(position: Position, identifiers: Seq[Identifier], subType: SubTypeIndication, value: Option[Expression], symbols: Seq[RuntimeSymbol] = Seq())
  extends ObjectDeclaration

/**
 * Represents a file declaration
 *
 * grammar: <pre> '''FILE''' identifier_list : subtype_indication [ ['''OPEN''' ''file_open_kind_''expression ] '''IS''' ''file_logical_name_''expression ]; </pre>
 *
 * example: {{{ file f : myFileType open write_mode is "test.dat"; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.file_declaration]]
 * @param openKind the kind of the file (read_mode,write_mode or append_mode)
 * @param logicalName the name of the file
 */
final case class FileDeclaration(position: Position, identifiers: Seq[Identifier], subType: SubTypeIndication, openKind: Option[Expression], logicalName: Option[Expression], symbols: Seq[RuntimeSymbol] = Seq())
  extends ObjectDeclaration

object SignalDeclaration {
  type Type = Type.Value

  object Type extends Enumeration {
    val REGISTER, BUS = Value
  }

}

final case class SignalDeclaration(position: Position, identifiers: Seq[Identifier], subType: SubTypeIndication, signalType: Option[SignalDeclaration.Type], defaultExpression: Option[Expression], symbols: Seq[RuntimeSymbol] = Seq())
  extends ObjectDeclaration

/**
 * Represents a component declaration
 *
 * grammar: <pre>
 * '''COMPONENT''' identifier '''IS'''
 *
 *    [generic_clause]
 *
 *    [port_clause]
 *
 * '''END''' '''COMPONENT''' [identifier];
 * </pre>
 *
 * example:
 * {{{
 * component myComponent is
 *    generic (a : integer; b : integer);
 *    port(c : std_ulogic; d : std_ulogic);
 * end component myComponent;
 * }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.component_declaration]]
 * @param identifier the name of the component
 * @param genericInterfaceList the generics of the component
 * @param portInterfaceList the ports of the component
 * @param endIdentifier the optional repeated name of the component
 * @param symbol the symbol of the new declared component
 */
final case class ComponentDeclaration(position: Position, identifier: Identifier, genericInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], portInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], endIdentifier: Option[Identifier], symbol: ComponentSymbol = null)
  extends DeclarativeItem

/**
 * Represents a subtype declaration
 *
 * grammar: <pre> '''SUBTYPE''' identifier '''IS''' subtype_indication; </pre>
 *
 * example: {{{ subtype myType is integer range 5 to 10; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.subtype_declaration]]
 * @param identifier the name of the new subtype
 * @param subType the the type of the new subtype
 */
final case class SubTypeDeclaration(position: Position, identifier: Identifier, subType: SubTypeIndication) extends DeclarativeItem

abstract sealed class SubprogramDeclaration extends DeclarativeItem {
  val identifier: Identifier
  val parameterInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]]
  val genericInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]]
  val genericAssociationList: Option[AssociationList]
  val symbol: SubprogramSymbol
}

final case class FunctionDeclaration(position: Position, isPure: Boolean, identifier: Identifier, genericInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], genericAssociationList: Option[AssociationList], parameterInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], returnType: SelectedName, symbol: FunctionSymbol = null)
  extends SubprogramDeclaration

final case class ProcedureDeclaration(position: Position, identifier: Identifier, genericInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], genericAssociationList: Option[AssociationList], parameterInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], symbol: ProcedureSymbol = null)
  extends SubprogramDeclaration

final case class SubprogramInstantiationDeclaration(position: Position, isProcedure: Boolean, identifier: Identifier, subprogramName: SelectedName, signature: Signature, genericAssociationList: Option[AssociationList]) extends DeclarativeItem

abstract sealed class SubprogramDefinition extends DeclarativeItem {
  val identifier: Identifier
  val genericInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]]
  val genericAssociationList: Option[AssociationList]
  val parameterInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]]
  val declarativeItems: Seq[DeclarativeItem]
  val sequentialStatements: Seq[SequentialStatement]
  val symbol: SubprogramSymbol
}

final case class FunctionDefinition(position: Position, isPure: Boolean, identifier: Identifier, genericInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], genericAssociationList: Option[AssociationList],
                                    parameterInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], returnType: SelectedName,
                                    declarativeItems: Seq[DeclarativeItem], sequentialStatements: Seq[SequentialStatement], endIdentifier: Option[Identifier],
                                    localSymbols: Seq[Symbol] = Seq(), symbol: FunctionSymbol = null) extends SubprogramDefinition

final case class ProcedureDefinition(position: Position, identifier: Identifier, genericInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], genericAssociationList: Option[AssociationList],
                                     parameterInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], declarativeItems: Seq[DeclarativeItem],
                                     sequentialStatements: Seq[SequentialStatement], endIdentifier: Option[Identifier],
                                     localSymbols: Seq[Symbol] = Seq(), symbol: ProcedureSymbol = null) extends SubprogramDefinition

final case class ConfigurationSpecification(position: Position, componentSpecification: ComponentSpecification, bindingIndication: BindingIndication) extends DeclarativeItem

final case class GroupDeclaration(position: Position, identifier: Identifier, groupTemplateName: SelectedName, constituents: Seq[Either[Name, Identifier]]) extends DeclarativeItem

object GroupTemplateDeclaration {

  final class EntityClassEntry(val entityClass: EntityClass.Value, val isInfinite: Boolean)

}

/**
 * Represents a group template declaration
 *
 * grammar: <pre> '''GROUP''' identifier '''IS''' ( entity_class_entry {, entity_class_entry }  );
 *
 * entity_class_entry  : entity_class [BOX]
 *
 * </pre>
 *
 * example: {{{ group PIN2PIN is (signal, signal); }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.group_template_declaration]]
 * @param entries the entity class entires for this template
 */
final case class GroupTemplateDeclaration(position: Position, identifier: Identifier, entries: Seq[GroupTemplateDeclaration.EntityClassEntry]) extends DeclarativeItem

final case class DisconnectionSpecification(position: Position, signalListOrIdentifier: Either[Seq[SelectedName], Identifier], typeName: SelectedName, timeExpression: Expression) extends DeclarativeItem

final case class TerminalDeclaration(position: Position, identifiers: Seq[Identifier], subNature: SubNatureIndication) extends DeclarativeItem

final case class SubNatureDeclaration(position: Position, identifier: Identifier, subNature: SubNatureIndication) extends DeclarativeItem

final case class StepLimitSpecification(position: Position, signalListOrIdentifier: Either[Seq[SelectedName], Identifier], typeName: SelectedName, withExpression: Expression) extends DeclarativeItem

/**
 * Base class for all type declarations
 *
 * grammar: <pre> '''TYPE''' identifier ['''IS''' type_definition]
 *
 * where type_definition is :
 *
enumeration_type_definition

	| physical_type_definition

	| integer_or_floating_point_type_definition

	| array_type_definition

	| record_type_definition

	| access_type_definition

	| file_type_definition

	| protected_type_body

	| protected_type_declaration
 *  </pre>
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.type_declaration]]
 */
abstract sealed class AbstractTypeDeclaration extends DeclarativeItem {
  /**
   * the name of the new type
   */
  val identifier: Identifier
  /**
   * dataType is the new created type
   */
  val dataType: DataType
}

/**
 * Represents a incomplete type declaration
 *
 * example: {{{ type myType; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 */
final case class IncompleteTypeDeclaration(position: Position, identifier: Identifier, dataType: DataType = NoType) extends AbstractTypeDeclaration

/**
 * Represents a integer or real type definition
 *
 * grammar: <pre> '''TYPE''' identifier '''IS''' '''RANGE''' range </pre>
 *
 * example: {{{ type myInteger is range 0 to 10; }}} {{{ type myInteger is range a'range }}}
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.integer_or_floating_point_type_definition]]
 * @param range the range of the new integer or real type
 */
final case class IntegerOrFloatingPointTypeDefinition(position: Position, identifier: Identifier, range: Range, dataType: DataType = NoType) extends AbstractTypeDeclaration

/**
 * Represents a file type definition
 *
 * grammar: <pre> '''TYPE''' identifier '''IS''' '''ACCESS''' subtype_indication </pre>
 *
 * example: {{{ type myAcces is integer; }}}
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.access_type_definition]]
 * @param subType the type of the access type
 */
final case class AccessTypeDefinition(position: Position, identifier: Identifier, subType: SubTypeIndication, dataType: DataType = NoType) extends AbstractTypeDeclaration

object RecordTypeDefinition {

  final class Element(val identifiers: Seq[Identifier], val subType: SubTypeIndication)

}

final case class RecordTypeDefinition(position: Position, identifier: Identifier, elements: Seq[RecordTypeDefinition.Element], endIdentifier: Option[Identifier], dataType: DataType = NoType) extends AbstractTypeDeclaration

object PhysicalTypeDefinition {

  final class Element(val identifier: Identifier, val literal: expressions.PhysicalLiteral)

}

final case class PhysicalTypeDefinition(position: Position, identifier: Identifier, range: Range, baseIdentifier: Identifier, elements: Seq[PhysicalTypeDefinition.Element], endIdentifier: Option[Identifier], dataType: DataType = NoType) extends AbstractTypeDeclaration

/**
 * Represents a file type definition
 *
 * grammar: <pre> '''TYPE''' identifier '''IS''' '''FILE''' '''OF''' type_mark </pre>
 *
 * example: {{{ type myFile is file of integer; }}}
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.file_type_definition]]
 * @param typeName the type of the file
 */
final case class FileTypeDefinition(position: Position, identifier: Identifier, typeName: SelectedName, dataType: DataType = NoType) extends AbstractTypeDeclaration

/**
 * Represents a array type definition
 *
 * grammar: <pre> '''TYPE''' identifier '''IS''' '''ARRAY''' (
 *
 * ( index_subtype_definition {, index_subtype_definition} )
 *
 * | index_constraint
 *
 * ) '''OF''' subtype_indication
 *
 * index_subtype_definition : type_mark '''RANGE''' <>
 *
 * </pre>
 *
 * example: {{{ type myArray is array (integer RANGE <>, integer RANGE <> ) of integer; }}} {{{ type myArray is array (0 to 10, 20 to 30) of real; }}}
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.array_type_definition]]
 * @param dimensions the information about the dimension of the array type
 */
final case class ArrayTypeDefinition(position: Position, identifier: Identifier, dimensions: Either[Seq[SelectedName], Seq[DiscreteRange]], subType: SubTypeIndication, dataType: DataType = NoType) extends AbstractTypeDeclaration

/**
 * Represents a enumeration type definition
 *
 * grammar: <pre> '''TYPE''' identifier '''IS''' ( enumeration_literal {, enumeration_literal} ) </pre>
 *
 * example: {{{ type myEnum is ('0', '1', 'H', 'L'); }}}
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.enumeration_type_definition]]
 * @param elements the different enumeration values
 */
final case class EnumerationTypeDefinition(position: Position, identifier: Identifier, elements: Seq[Identifier], dataType: DataType = NoType) extends AbstractTypeDeclaration

final case class ProtectedTypeBodyDeclaration(position: Position, identifier: Identifier, declarativeItems: Seq[DeclarativeItem], endIdentifier: Option[Identifier], dataType: DataType = NoType, header: ProtectedType = null) extends AbstractTypeDeclaration

final case class ProtectedTypeDeclaration(position: Position, identifier: Identifier, declarativeItems: Seq[DeclarativeItem], endIdentifier: Option[Identifier], dataType: DataType = NoType) extends AbstractTypeDeclaration

final case class ScalarNatureDefinition(position: Position, identifier: Identifier, typeName: SelectedName, acrossType: SelectedName, throughIdentifier: Identifier, dataType: DataType = NoType) extends declarativeItems.AbstractTypeDeclaration

final case class ArrayNatureTypeDefinition(position: Position, identifier: Identifier, dimensions: Either[Seq[SelectedName], Seq[DiscreteRange]], subNature: SubNatureIndication, dataType: DataType = NoType) extends declarativeItems.AbstractTypeDeclaration

object RecordNatureDefinition {

  final class Element(val identifiers: Seq[Identifier], val subNature: SubNatureIndication)

}

final case class RecordNatureDefinition(position: Position, identifier: Identifier, elements: Seq[RecordNatureDefinition.Element], endIdentifier: Option[Identifier], dataType: DataType = NoType) extends declarativeItems.AbstractTypeDeclaration

abstract sealed class AbstractQuantityDeclaration extends declarativeItems.DeclarativeItem

final case class FreeQuantityDeclaration(position: Position, identifiers: Seq[Identifier], subType: SubTypeIndication, expression: Option[Expression]) extends AbstractQuantityDeclaration

final case class BranchQuantityDeclaration(position: Position, acrossAspect: Option[(Seq[Identifier], Option[Expression], Option[Expression])], throughAspect: Option[(Seq[Identifier], Option[Expression], Option[Expression])],
                                           terminalAspect: Option[(Name, Option[Name])]) extends AbstractQuantityDeclaration

final case class SourceQuantityDeclaration(position: Position, identifiers: Seq[Identifier], subType: SubTypeIndication, sourceAspect: Either[(Expression, Expression), Expression]) extends AbstractQuantityDeclaration

}

package sequentialStatements {

import at.jku.ssw.openvc.symbolTable.symbols.{SignalSymbol, ConstantSymbol, ProcedureSymbol}

/**
 * Base class for all sequential statements
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.sequential_statement]]
 */
sealed trait SequentialStatement extends ASTNode {
  /**
   * the optional label for this sequential statement
   */
  val label: Option[Identifier]
}

/**
 * Represents a throw statement, that is not part of VHDL
 *
 * A throw statement is used to generate {{{throw new VHDLRuntimeException(message);}}} in case statements, when the others choice is missing to signal a runtime error
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @param message the message in the exception
 * @see [[at.jku.ssw.openvs.VHDLRuntime.VHDLRuntimeException]]
 */
final case class ThrowStatement(position: Position, message: String) extends SequentialStatement {
  /**
   * the label is always None, because a throw statement is only internally used
   */
  val label = None
}

/**
 * Represents an assertion statement
 *
 * grammar: <pre> [ identifier : ] '''ASSERT''' condition ['''REPORT''' ''report''_expression ] ['''SEVERITY''' ''severity''_expression]; </pre>
 *
 * example: {{{ assert a==b report "a!=b" severity failure; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.assertion_statement]]
 * @param condition the condition
 * @param reportExpression the optional report expression
 * @param severityExpression the optional severity expression
 */
final case class AssertionStatement(position: Position, label: Option[Identifier], condition: Expression, reportExpression: Option[Expression], severityExpression: Option[Expression]) extends SequentialStatement

/**
 * Represents a wait statement
 *
 * grammar: <pre> [ identifier : ] '''WAIT''' ['''ON''' name_list] ['''UNTIL''' ''until''_condition] ['''FOR''' ''expression]; </pre>
 *
 * example: {{{ wait on a,b until a for 10 ns; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.wait_statement]]
 * @param signals the optional signal names
 * @param untilCondition the optional condition
 * @param forExpression the optional time expression
 * @param sensitivitySignalList the signal symbols used for the code generator
 */
final case class WaitStatement(position: Position, label: Option[Identifier], signals: Option[Seq[Name]], untilCondition: Option[Expression], forExpression: Option[Expression], sensitivitySignalList: Seq[SignalSymbol] = Seq()) extends SequentialStatement

/**
 * Represents a next statement
 *
 * grammar: <pre> [ identifier : ] '''NEXT''' label ['''WHEN''' condition]; </pre>
 *
 * example: {{{ next some_label when a!=b; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.next_statement]]
 * @param loopLabel the label of the enclosing loop statement
 * @param condition the optional condition
 * @param loopStatement the position of the enclosing loop statement is used in the code generation phase to identify the jump labels
 */
final case class NextStatement(position: Position, label: Option[Identifier], loopLabel: Option[Identifier], condition: Option[Expression], loopStatement: Position = NoPosition) extends SequentialStatement

/**
 * Represents an exit statement
 *
 * grammar: <pre> [ identifier : ] '''EXIT''' label ['''WHEN''' condition]; </pre>
 *
 * example: {{{ exit some_label when a!=b; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.exit_statement]]
 * @param loopLabel the label of the enclosing loop statement
 * @param condition the optional condition
 * @param loopStatement the position of the enclosing loop statement is used in the code generation phase to identify the jump labels
 */
final case class ExitStatement(position: Position, label: Option[Identifier], loopLabel: Option[Identifier], condition: Option[Expression], loopStatement: Position = NoPosition) extends SequentialStatement

/**
 * Represents a null statement
 *
 * grammar: <pre> [ identifier : ] '''NULL'''; </pre>
 *
 * example: {{{ null; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.null_statement]]
 */
final case class NullStatement(position: Position, label: Option[Identifier]) extends SequentialStatement

/**
 * Represents a report statement
 *
 * grammar: <pre> [ identifier : ] '''REPORT''' ''report''_expression ['''SEVERITY''' ''severity''_expression]; </pre>
 *
 * example: {{{ report "some debug information" severity note; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.report_statement]]
 * @param reportExpression the report expression
 * @param severityExpression the optional severity expression
 */
final case class ReportStatement(position: Position, label: Option[Identifier], reportExpression: Expression, severityExpression: Option[Expression]) extends SequentialStatement

/**
 * Represents a return statement
 *
 * grammar: <pre> [ identifier : ] '''RETURN''' [expression]; </pre>
 *
 * example: {{{ return a+b-c*5; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.return_statement]]
 * @param expression the value to return
 * @param procedureSymbol the procedure which contains this return statement, else null
 */
final case class ReturnStatement(position: Position, label: Option[Identifier], expression: Option[Expression], procedureSymbol: ProcedureSymbol = null) extends SequentialStatement

/**
 * Base class for all loop statements
 *
 * grammar:
 * <pre>
 * [ identifier : ] [iteration_scheme] '''LOOP'''
 *
 *    sequence_of_statements
 *
 * '''END''' '''LOOP''' [identifier];
 *
 *
 * where iteration_scheme is:
 *
 * '''WHILE''' condition
 *
 * | '''FOR''' identifier '''IN''' discrete_range
 * </pre>
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.loop_statement]]
 */
abstract sealed class AbstractLoopStatement extends SequentialStatement {
  /**
   * the sequential statements that belong to this loop
   */
  val sequentialStatements: Seq[SequentialStatement]
  /**
   * the last optional identifier
   */
  val endLabel: Option[Identifier]
}

/**
 * Represents a loop statement without an iteration scheme
 *
 * example:
 * {{{
 * loop
 *  null;
 * end loop;
 * }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.loop_statement]]
 */
final case class LoopStatement(position: Position, label: Option[Identifier], sequentialStatements: Seq[SequentialStatement], endLabel: Option[Identifier]) extends AbstractLoopStatement

/**
 * Represents a loop statement with a while iteration scheme
 *
 * example:
 * {{{
 * while a < b loop
 *  null;
 * end loop;
 * }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.loop_statement]]
 * @param condition the condition of the while loop
 */
final case class WhileStatement(position: Position, label: Option[Identifier], condition: Expression, sequentialStatements: Seq[SequentialStatement], endLabel: Option[Identifier]) extends AbstractLoopStatement

/**
 * Represents a loop statement with a for iteration scheme
 *
 * example:
 * {{{
 * for i in a'range loop
 *  null;
 * end loop;
 * }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.loop_statement]]
 * @param identifier the name of the loop parameter
 * @param discreteRange the values to iterate over
 * @param symbol the symbol of the loop parameter
 */
final case class ForStatement(position: Position, label: Option[Identifier], identifier: Identifier, discreteRange: DiscreteRange, sequentialStatements: Seq[SequentialStatement],
                              endLabel: Option[Identifier], symbol: ConstantSymbol = null) extends AbstractLoopStatement

/**
 * Base class for all signal assignment statements
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.signal_assignment_statement]]
 */
abstract sealed class SignalAssignmentStatement extends SequentialStatement

/**
 * Represents a simple signal assignment statement
 *
 * grammar: <pre> [ identifier : ] target <= [delay_mechanism] waveform; </pre>
 *
 * example: {{{ a<=b when 0 }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.simple_signal_assignment]]
 * @param target the target of the assignment
 * @param delayMechanism the optional of the delays in the assignment
 * @param waveform the waveform to assign
 */
final case class SimpleWaveformAssignmentStatement(position: Position, label: Option[Identifier], target: Target, delayMechanism: Option[DelayMechanism], waveform: Waveform) extends SignalAssignmentStatement

final case class SimpleReleaseAssignment(position: Position, label: Option[Identifier], target: Target, forceMode: Option[InterfaceList.Mode.Value]) extends SignalAssignmentStatement

final case class SimpleForceAssignment(position: Position, label: Option[Identifier], target: Target, forceMode: Option[InterfaceList.Mode.Value], expression: Expression) extends SignalAssignmentStatement

trait WaveformAssignment {
  /**
   * the target of the assignment
   */
  val target: Target
  /**
   * the optional delay of the concurrent signal assignment
   */
  val delay: Option[DelayMechanism]
}

final case class SelectedWaveformAssignment(position: Position, label: Option[Identifier], expression: Expression, isMatchingCase: Boolean, target: Target,
                                            delay: Option[DelayMechanism], alternatives: Seq[concurrentStatements.ConcurrentSelectedSignalAssignment.When]) extends SignalAssignmentStatement with WaveformAssignment

final case class SelectedForceAssignment(position: Position, label: Option[Identifier], expression: Expression, isMatchingCase: Boolean, target: Target,
                                         forceMode: Option[InterfaceList.Mode.Value], alternatives: Seq[SelectedVariableAssignment.When]) extends SignalAssignmentStatement

final case class ConditionalWaveformAssignment(position: Position, label: Option[Identifier], target: Target, delay: Option[DelayMechanism],
                                               alternatives: Seq[concurrentStatements.ConcurrentConditionalSignalAssignment.When]) extends SignalAssignmentStatement with WaveformAssignment

final case class ConditionalForceAssignment(position: Position, label: Option[Identifier], target: Target, forceMode: Option[InterfaceList.Mode.Value],
                                            alternatives: Seq[ConditionalVariableAssignment.When]) extends SignalAssignmentStatement

/**
 * Base class for all variable assignment statements
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.variable_assignment_statement]]
 */
abstract sealed class VariableAssignmentStatement extends SequentialStatement

/**
 * Represents a simple variable assignment statement
 *
 * grammar: <pre> [ identifier : ] target := expression; </pre>
 *
 * example: {{{ a:=b*c*d-e; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.return_statement]]
 * @param target the target of the assignment
 * @param expression the value to assign
 */
final case class SimpleVariableAssignmentStatement(position: Position, label: Option[Identifier], target: Target, expression: Expression) extends VariableAssignmentStatement

object ConditionalVariableAssignment {

  final class When(val expression: Expression, val condition: Option[Expression])

}

final case class ConditionalVariableAssignment(position: Position, label: Option[Identifier], target: Target, alternatives: Seq[ConditionalVariableAssignment.When]) extends VariableAssignmentStatement

object SelectedVariableAssignment {

  final class When(val expression: Expression, val choices: Seq[Choices.Choice])

}

final case class SelectedVariableAssignment(position: Position, label: Option[Identifier], expression: Expression, isMatchingCase: Boolean, target: Target, alternatives: Seq[SelectedVariableAssignment.When]) extends VariableAssignmentStatement

/**
 * Represents a procedure call statement
 *
 * grammar: <pre> selected_name [( association_list )]; </pre>
 *
 * example: {{{ mypackage.foo(a,b,c,d); }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.procedure_call_statement]]
 * @param name the name of the procedure to call
 * @param parameterAssociation the parameters of the procedure
 * @param symbol the procedure to call
 */
final case class ProcedureCallStatement(label: Option[Identifier], name: SelectedName, parameterAssociation: Option[AssociationList], symbol: ProcedureSymbol = null) extends SequentialStatement {
  val position = name.position
}

object CaseStatement {

  /**
   * Represents exact one alternative in a case statement
   *
   * grammar: <pre> '''WHEN''' choices => sequence_of_statements </pre>
   *
   * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
   * @see [[at.jku.ssw.openvc.ast.sequentialStatements.CaseStatement]]
   * @param choices the different choices for this alternative
   * @param sequentialStatements the sequential statements that belong to this alternative
   */
  final class When(val choices: Seq[Choices.Choice], val sequentialStatements: Seq[SequentialStatement])

}

/**
 * Represents a case statement
 *
 * grammar:
 * <pre>
 * [ identifier : ] '''CASE''' [{vhdl2008}?=> ?] expression '''IS'''
 *
 *    ('''WHEN''' choices => sequence_of_statements)+
 *
 * '''END''' '''CASE''' [{vhdl2008}?=> ?] [identifier];
 * </pre>
 *
 * example:
 * {{{
 * case a is
 *  when '0' => null;
 *  when '1' | 'X' => null;
 *  when others => null;
 * end case;
 * }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.case_statement]]
 * @param isMatchingCase when true then this is a matching case statement else it is a ordinary case statement
 * @param expression the expression of the case statement
 * @param alternatives the different ''when'' alternatives
 * @param endLabel the last optional identifier
 * @param keys the calculated values for the different choices in the alternatives, used in the code generator
 */
final case class CaseStatement(position: Position, label: Option[Identifier], isMatchingCase: Boolean, expression: Expression, alternatives: Seq[CaseStatement.When], endLabel: Option[Identifier], keys: Seq[Int] = Seq()) extends SequentialStatement

object IfStatement {

  /**
   * Represents exact one alternative in a if statement
   *
   * grammar: <pre> '''ELSIF''' condition '''THEN''' sequence_of_statements </pre>
   *
   * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
   * @see [[at.jku.ssw.openvc.ast.sequentialStatements.IfStatement]]
   * @param condition the condition for this alternative
   * @param sequentialStatements the sequential statements that belong to this alternative
   */
  final class IfThenPart(val condition: Expression, val sequentialStatements: Seq[SequentialStatement])

}

/**
 * Represents an if statement
 *
 * grammar:
 * <pre>
 * [ identifier : ] '''IF''' condition '''THEN'''
 *
 *    sequence_of_statements
 *
 * {'''ELSIF''' condition '''THEN'''
 *
 *    sequence_of_statements}
 * ['''ELSE'''
 *
 *    sequence_of_statements]
 *
 * '''END''' '''IF''' [identifier];
 * </pre>
 *
 * example:
 * {{{
 * if a then
 *  null;
 * else if b then
 *  null;
 * else if c then
 *  null;
 * else
 *  null;
 * end if;
 * }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.if_statement]]
 * @param ifThenList the target of the assignment
 * @param elseSequentialStatements the value to assign
 * @param endLabel the value to assign
 */
final case class IfStatement(position: Position, label: Option[Identifier], ifThenList: Seq[IfStatement.IfThenPart], elseSequentialStatements: Option[Seq[SequentialStatement]], endLabel: Option[Identifier]) extends SequentialStatement

final case class AMSBreakStatement(position: Position, label: Option[Identifier], elements: Seq[BreakElement], whenExpression: Option[Expression]) extends SequentialStatement

}

package concurrentStatements {

import at.jku.ssw.openvc.symbolTable.symbols.{ConstantSymbol, Symbol, ProcessSymbol}
import declarativeItems.DeclarativeItem

/**
 * Base class for all concurrent statements
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.concurrent_statement]]
 */
sealed trait ConcurrentStatement extends ASTNode {
  /**
   * the optional label for this concurrent statement
   */
  val label: Option[Identifier]
}

/**
 * Base class for all concurrent signal assignment statements
 *
 * grammar: <pre>
 * conditional_signal_assignment
 *
 * | selected_signal_assignment
 * </pre>
 *
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.concurrent_signal_assignment_statement]]
 */
abstract sealed class ConcurrentSignalAssignmentStatement extends ConcurrentStatement with sequentialStatements.WaveformAssignment {
  /**
   * postponed flag of the equivalent process
   */
  val isPostponed: Boolean
  /**
   * is this a guarded signal assignment
   */
  val isGuarded: Boolean
  /**
   * the target of the assignment
   */
  val target: Target
  /**
   * the optional delay of the concurrent signal assignment
   */
  val delay: Option[DelayMechanism]
}

object ConcurrentConditionalSignalAssignment {

  /**
   * Represents exact one alternative in a concurrent conditional signal assignment statement
   *
   * grammar: <pre> waveform ['''WHEN''' condition] </pre>
   *
   * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
   * @see [[at.jku.ssw.openvc.ast.parser.VHDLParser.conditional_waveforms]]
   * @param waveform the different values for this alternative
   * @param condition the optional condition of the alternative
   */
  final class When(val waveform: Waveform, val condition: Option[Expression])

}

/**
 * Represents a concurrent conditional signal assignment statement, that is equivalent to a process with one if statement
 *
 * grammar:
 * <pre>
 * [ identifier : ] target <= ['''GUARDED'''] [delay_mechanism]
 *
 *    conditional_waveforms
 *
 * where conditional_waveforms is:
 * waveform ['''WHEN''' condition [ '''ELSE''' conditional_waveforms ]]
 * </pre>
 *
 * example:
 * {{{
 * target <= options
 * wavefrom1 when condition1 else
 * waveform2 when condition2 else
 * waveformN-1 when conditionN-1;
 * waveformN when conditionN;
 *
 * then the signal transform in the corresponding process statement is of the form
 *
 * if condition1 then
 *  wave_transform1
 * elsif condition2 then
 *  wave_transform2
 * elsif conditionN-1 then
 *  wave_transformN-1
 * elsif conditionN then
 *  wave_transformN
 * end if;
 * }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.conditional_signal_assignment]]
 * @param alternatives the different ''when'' alternatives
 */
final case class ConcurrentConditionalSignalAssignment(position: Position, label: Option[Identifier], isPostponed: Boolean, target: Target, isGuarded: Boolean,
                                                       delay: Option[DelayMechanism], alternatives: Seq[ConcurrentConditionalSignalAssignment.When]) extends ConcurrentSignalAssignmentStatement

object ConcurrentSelectedSignalAssignment {

  /**
   * Represents exact one alternative in a concurrent selected signal assignment statement
   *
   * grammar: <pre> waveform '''WHEN''' choices </pre>
   *
   * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
   * @see [[at.jku.ssw.openvc.ast.parser.VHDLParser.selected_waveform]]
   * @param waveform the different values for this alternative
   * @param choices the choices of the alternative
   */
  final class When(val waveform: Waveform, val choices: Seq[Choices.Choice])

}

/**
 * Represents a concurrent selected signal assignment statement, that is equivalent to a process with one case statement
 *
 * grammar:
 * <pre>
 * [ identifier : ] '''WITH''' expression '''SELECT''' [{vhdl2008}?=> ? ]
 *
 * target <= ['''GUARDED'''] [delay_mechanism]
 *
 *    selected_waveform {, selected_waveform};
 *
 * where selected_waveform is:
 * waveform '''WHEN''' choices
 * </pre>
 * example:
 * {{{
 * with expression select
 *  target <= options waveform1 when choice_list1,
 *  waveform2 when choice_list2,
 *  waveformN-1 when choice_listN-1,
 *  waveformN when choice_listN;
 *
 * then the signal transform in the corresponding process statement is of the form
 *
 * case [?] expression is
 *    when choice_list1 => wave_transform1
 *    when choice_list2 => wave_transform2
 *    when choice_listN-1=> wave_transformN-1
 *    when choice_listN => wave_transformN
 * end case;
 * }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.selected_signal_assignment]]
 * @param expression the expression of the equivalent case statement
 * @param isMatchingCase is this a matching case statement see [[at.jku.ssw.openvc.sequentialStatement.CaseStatement]]
 * @param alternatives the different ''when'' alternatives
 */
final case class ConcurrentSelectedSignalAssignment(position: Position, label: Option[Identifier], isPostponed: Boolean, expression: Expression, isMatchingCase: Boolean, target: Target,
                                                    isGuarded: Boolean, delay: Option[DelayMechanism], alternatives: Seq[ConcurrentSelectedSignalAssignment.When]) extends ConcurrentSignalAssignmentStatement

/**
 * Represents a concurrent procedure call statement, which is similiar to a sequential procedure call statement
 *
 * grammar: <pre> [ identifier : ] '''POSTPONED''' selected_name [( association_list )]; </pre>
 *
 * example: {{{ foo: postponed mypackage.foo(a,b,c,d); }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.concurrent_procedure_call_statement]]
 * @see [[at.jku.ssw.openvc.ast.sequentialStatements.ProcedureCallStatement]]
 * @param isPostponed postponed flag of the equivalent process
 * @param name the name of the procedure to call
 * @param parameterAssociation the parameters of the procedure
 */
final case class ConcurrentProcedureCallStatement(label: Option[Identifier], isPostponed: Boolean, name: SelectedName, parameterAssociation: Option[AssociationList]) extends ConcurrentStatement {
  val position = name.position
}

/**
 * Represents a concurrent assertion statement, which is similiar to a sequential assert statement
 *
 * grammar: <pre> [ identifier : ] '''POSTPONED''' '''ASSERT''' condition ['''REPORT''' ''report''_expression ] ['''SEVERITY''' ''severity''_expression]; </pre>
 *
 * example: {{{ foo: postponed assert a==b report "a!=b" severity failure; }}}
 * @author <a href="mailto:chr_reisinger@yahoo.de">Christian Reisinger</a>
 * @see [[at.jku.ssw.openvc.parser.VHDLParser.concurrent_assertion_statement]]
 * @see [[at.jku.ssw.openvc.ast.sequentialStatements.AssertionStatement]]
 * @param isPostponed postponed flag of the equivalent process
 * @param condition the condition
 * @param reportExpression the optional report expression
 * @param severityExpression the optional severity expression
 */
final case class ConcurrentAssertionStatement(position: Position, label: Option[Identifier], isPostponed: Boolean, condition: Expression, reportExpression: Option[Expression], severityExpression: Option[Expression]) extends ConcurrentStatement

sealed trait GenerateStatement extends ConcurrentStatement

object IfGenerateStatement {

  final case class IfThenPart(label: Option[Identifier], condition: Expression, declarativeItems: Seq[DeclarativeItem], concurrentStatements: Seq[ConcurrentStatement], endLabel: Option[Identifier])

}

final case class IfGenerateStatement(position: Position, label: Option[Identifier], ifThenList: Seq[IfGenerateStatement.IfThenPart], elsePart: Option[IfGenerateStatement.IfThenPart], endLabel: Option[Identifier])
  extends GenerateStatement

final case class ForGenerateStatement(position: Position, label: Option[Identifier], loopIdentifier: Identifier, discreteRange: DiscreteRange, declarativeItems: Seq[DeclarativeItem],
                                      concurrentStatements: Seq[ConcurrentStatement], alternativeEndLabel: Option[Identifier], endLabel: Option[Identifier], symbol: ConstantSymbol = null) extends GenerateStatement

object CaseGenerateStatement {

  final case class When(label: Option[Identifier], choices: Seq[Choices.Choice], declarativeItems: Seq[DeclarativeItem], concurrentStatements: Seq[ConcurrentStatement], endLabel: Option[Identifier])

}

final case class CaseGenerateStatement(position: Position, label: Option[Identifier], expression: Expression, alternatives: Seq[CaseGenerateStatement.When], endLabel: Option[Identifier]) extends GenerateStatement

object ComponentInstantiationStatement {
  type ComponentType = ComponentType.Value

  object ComponentType extends Enumeration {
    val COMPONENT, ENTITY, CONFIGURATION = Value
  }

}

final case class ComponentInstantiationStatement(position: Position, label: Option[Identifier], componentType: ComponentInstantiationStatement.ComponentType, name: SelectedName, architectureIdentifier: Option[Identifier],
                                                 genericAssociationList: Option[AssociationList], portAssociationList: Option[AssociationList], symbol: Symbol = null) extends ConcurrentStatement

final case class ProcessStatement(position: Position, label: Option[Identifier], isPostponed: Boolean, sensitivityList: Option[Seq[Name]], declarativeItems: Seq[DeclarativeItem],
                                  sequentialStatements: Seq[at.jku.ssw.openvc.ast.sequentialStatements.SequentialStatement], endLabel: Option[Identifier],
                                  localSymbols: Seq[Symbol] = Seq(), symbol: ProcessSymbol = null) extends ConcurrentStatement

final case class BlockStatement(position: Position, label: Option[Identifier], guardExpression: Option[Expression], genericInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]],
                                genericAssociationList: Option[AssociationList], portInterfaceList: Option[Seq[InterfaceList.AbstractInterfaceElement]], portAssociationList: Option[AssociationList],
                                declarativeItems: Seq[DeclarativeItem], concurrentStatements: Seq[ConcurrentStatement], endLabel: Option[Identifier]) extends ConcurrentStatement

final case class ConcurrentBreakStatement(position: Position, label: Option[Identifier], breakElements: Option[Seq[BreakElement]], onNameList: Option[Seq[Name]], whenExpression: Option[Expression]) extends ConcurrentStatement

}

package simultaneousStatements {

import at.jku.ssw.openvc.ast.declarativeItems.DeclarativeItem
import at.jku.ssw.openvc.ast.sequentialStatements.SequentialStatement

sealed trait SimultaneousStatement extends concurrentStatements.ConcurrentStatement

final case class SimultaneousNullStatement(position: Position, label: Option[Identifier]) extends SimultaneousStatement

final case class SimpleSimultaneousStatement(label: Option[Identifier], firstExpression: Expression, secondExpression: Expression, toleranceExpression: Option[Expression]) extends SimultaneousStatement {
  val position = firstExpression.position
}

final case class SimultaneousProceduralStatement(position: Position, label: Option[Identifier], declarativeItems: Seq[DeclarativeItem], sequentialStatements: Seq[SequentialStatement], endLabel: Option[Identifier]) extends SimultaneousStatement

object SimultaneousCaseStatement {

  final class When(val choices: Seq[Choices.Choice], val simultaneousStatements: Seq[SimultaneousStatement])

}

final case class SimultaneousCaseStatement(position: Position, label: Option[Identifier], expression: Expression, alternatives: Seq[SimultaneousCaseStatement.When], endLabel: Option[Identifier]) extends SimultaneousStatement

object SimultaneousIfStatement {

  final class IfUsePart(val condition: Expression, val simultaneousStatements: Seq[SimultaneousStatement])

}

final case class SimultaneousIfStatement(position: Position, label: Option[Identifier], ifUseList: Seq[SimultaneousIfStatement.IfUsePart],
                                         elseSimultaneousStatements: Option[Seq[SimultaneousStatement]], endLabel: Option[Identifier]) extends SimultaneousStatement

}

}