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

import at.jku.ssw.openvc.symbolTable.dataTypes.{DataType, ConstrainedRangeType, NoType}
import at.jku.ssw.openvc.ast.expressions.{Name, Aggregate, Expression}

object Position {
  def apply(line: Int, charPosition: Int): Position = new Position(line, charPosition)

  val Empty = Position(-1, -1)
}

final class Position(val line: Int, val charPosition: Int) extends java.io.Serializable {
  override val toString = "Position(" + line + "," + charPosition + ")"

  def addLineOffset(lineOffset: Int) = new Position(this.line + lineOffset, this.charPosition)

  def addCharacterOffset(characterOffset: Int) = new Position(this.line, this.charPosition + characterOffset)
}

trait Locatable {
  val position: Position
}

abstract sealed class ASTNode extends Locatable

object Identifier {
  def apply(position: Position, text: String) = new Identifier(position, text)

  def apply(text: String) = new Identifier(Position.Empty, text)
}

final class Identifier(val position: Position, val originalText: String) extends Locatable with java.io.Serializable {
  val text = originalText.replace("\"", "")
  override val toString: String = this.text

  override def equals(other: Any): Boolean = other match {
    case id: Identifier => id.text == this.text
    case _ => false
  }

  override val hashCode = text.hashCode
}

final case class Target(nameOrAggregate: Either[Name, Aggregate], expression: Expression = null)

object Range {
  type Direction = Direction.Value

  object Direction extends Enumeration {
    val To, Downto = Value
  }

}

final class Range(val expressionsOrName: Either[(Expression, Range.Direction, Expression), Name], val dataType: DataType = NoType) extends Locatable {
  val position = expressionsOrName match {
    case Left((expr, _, _)) => expr.position
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

final class SubTypeIndication(val resolutionFunction: Option[SelectedName], val typeName: SelectedName, val constraint: Option[Either[Range, Seq[DiscreteRange]]]) extends Locatable {
  val position = resolutionFunction.getOrElse(typeName).position
}

final class Signature(val position: Position, val parameterList: Option[Seq[SelectedName]], val returnType: Option[SelectedName]) extends Locatable

object Waveform {
  final class Element(val valueExpression: Expression, val timeExpression: Option[Expression])
}

final class Waveform(val position: Position, val elements: Seq[Waveform.Element]) extends Locatable {
  val isUnaffected = this.elements.isEmpty
}

object Choices {
  final class Choice(val position: Position, val rangeOrExpression: Option[Either[DiscreteRange, Expression]]) extends Locatable {
    // expression,discreteRange == None => OTHERS
    val isOthers = rangeOrExpression.isEmpty
  }
}

final class Choices(val elements: Seq[Choices.Choice])

object AssociationList {
  final class Element(val formalPart: Option[Either[Identifier, (SelectedName, Name)]], val actualPart: Option[Expression]) {
    val isActualPartOpen = actualPart.isEmpty
  }
}

final case class AssociationList(elements: Seq[AssociationList.Element], parameters: Seq[Expression] = Seq())

final class SelectedName(val identifiers: Seq[Identifier]) extends Locatable {
  val position = identifiers.head.position
  override val toString = identifiers.mkString(".")
}

object InterfaceList {
  type InterfaceMode = InterfaceMode.Value

  object InterfaceMode extends Enumeration {
    val IN, OUT, INOUT, BUFFER, LINKAGE = Value
  }

  abstract sealed class AbstractInterfaceElement extends Locatable {
    val identifierList: Seq[Identifier]
    var expression: Option[Expression]
    //TODO remove var
    val interfaceMode: Option[InterfaceMode]
    val subType: SubTypeIndication
    val position = identifierList.head.position
  }

  final case class InterfaceConstantDeclaration(identifierList: Seq[Identifier], subType: SubTypeIndication, var expression: Option[Expression]) extends AbstractInterfaceElement {
    val interfaceMode = Some(InterfaceMode.IN)
  }

  final case class InterfaceVariableDeclaration(identifierList: Seq[Identifier], interfaceMode: Option[InterfaceMode], subType: SubTypeIndication, var expression: Option[Expression]) extends AbstractInterfaceElement

  final case class InterfaceSignalDeclaration(identifierList: Seq[Identifier], interfaceMode: Option[InterfaceMode], subType: SubTypeIndication, bus: Boolean, var expression: Option[Expression]) extends AbstractInterfaceElement

  final case class InterfaceFileDeclaration(identifierList: Seq[Identifier], subType: SubTypeIndication) extends AbstractInterfaceElement {
    var expression: Option[Expression] = None
    val interfaceMode = Some(InterfaceMode.IN)
  }

  final case class InterfaceTerminalDeclaration(identifierList: Seq[Identifier], subNature: ams.SubNatureIndication) extends AbstractInterfaceElement {
    var expression: Option[Expression] = None
    val interfaceMode = Some(InterfaceMode.IN)
    val subType = null
  }

  final case class InterfaceQuantityDeclaration(identifierList: Seq[Identifier], interfaceMode: Option[InterfaceMode], subType: SubTypeIndication, var expression: Option[Expression]) extends AbstractInterfaceElement

}

final class InterfaceList(val elements: Seq[InterfaceList.AbstractInterfaceElement])

final class ComponentSpecification(val instantiationList: Either[Seq[Identifier], Identifier], val componentName: SelectedName)

final class BindingIndication(val entityAspect: Option[Option[Either[(SelectedName, Option[Identifier]), SelectedName]]], val genericMap: Option[AssociationList], val portMap: Option[AssociationList])

final class BlockConfigurationSpecification(val nameOrLabel: Either[SelectedName, (Identifier, Option[Either[DiscreteRange, Expression]])]) //SelectedName,(label,blockConfigureIndex)

final class BlockConfiguration(val blockConfigSpec: BlockConfigurationSpecification, val useClauses: Seq[declarations.UseClause], val configurations: Seq[Either[BlockConfiguration, ComponentConfiguration]])

final class ComponentConfiguration(val componentSpecification: ComponentSpecification, val bindingIndication: Option[BindingIndication], val blockConfiguration: Option[BlockConfiguration])

package sequentialStatements {

import at.jku.ssw.openvc.symbolTable.symbols.{SignalSymbol, ConstantSymbol, ProcedureSymbol}

abstract sealed class SequentialStatement extends ASTNode {
  val label: Option[Identifier]
}

final case class ThrowStatement(position: Position, message: String) extends SequentialStatement {
  val label = None
}

final case class AssertStatement(position: Position, label: Option[Identifier], condition: Expression, reportExpression: Option[Expression], severityExpression: Option[Expression]) extends SequentialStatement

final case class WaitStatement(position: Position, label: Option[Identifier], sensitivityList: Option[Seq[Name]], untilCondition: Option[Expression], forExpression: Option[Expression], sensitivitySignalList: Seq[SignalSymbol] = Seq()) extends SequentialStatement

final case class NextStatement(position: Position, label: Option[Identifier], loopLabel: Option[Identifier], condition: Option[Expression], loopStatement: Position = null) extends SequentialStatement

final case class ExitStatement(position: Position, label: Option[Identifier], loopLabel: Option[Identifier], condition: Option[Expression], loopStatement: Position = null) extends SequentialStatement

final case class NullStatement(position: Position, label: Option[Identifier]) extends SequentialStatement

final case class ReportStatement(position: Position, label: Option[Identifier], reportExpression: Expression, severityExpression: Option[Expression]) extends SequentialStatement

final case class ReturnStatement(position: Position, label: Option[Identifier], expression: Option[Expression], procedureSymbol: ProcedureSymbol = null) extends SequentialStatement

abstract sealed class AbstractLoopStatement extends SequentialStatement {
  val sequentialStatementList: Seq[SequentialStatement]
  val label: Option[Identifier]
}

final case class LoopStatement(position: Position, label: Option[Identifier], sequentialStatementList: Seq[SequentialStatement], endLabel: Option[Identifier]) extends AbstractLoopStatement

final case class WhileStatement(position: Position, label: Option[Identifier], condition: Expression, sequentialStatementList: Seq[SequentialStatement], endLabel: Option[Identifier]) extends AbstractLoopStatement

final case class ForStatement(position: Position, label: Option[Identifier], identifier: Identifier, discreteRange: DiscreteRange, sequentialStatementList: Seq[SequentialStatement],
                              endLabel: Option[Identifier], symbol: ConstantSymbol = null) extends AbstractLoopStatement

abstract sealed class SignalAssignmentStatement extends SequentialStatement

final case class SimpleSignalAssignmentStatement(position: Position, label: Option[Identifier], target: Target, delayMechanism: Option[DelayMechanism], waveForm: Waveform) extends SignalAssignmentStatement

abstract sealed class VariableAssignmentStatement extends SequentialStatement

final case class SimpleVariableAssignmentStatement(position: Position, label: Option[Identifier], target: Target, expression: Expression) extends VariableAssignmentStatement

final case class ProcedureCallStatement(label: Option[Identifier], procedureName: SelectedName, parameterAssociationList: Option[AssociationList], symbol: ProcedureSymbol = null) extends SequentialStatement {
  val position = procedureName.position
}

object CaseStatement {
  final class When(val choices: Choices, val statements: Seq[SequentialStatement])
}

final case class CaseStatement(position: Position, label: Option[Identifier], expression: Expression, alternatives: Seq[CaseStatement.When], endLabel: Option[Identifier], keys: Seq[Int] = Seq()) extends SequentialStatement

object IfStatement {
  final class IfThenPart(val condition: Expression, val statements: Seq[SequentialStatement])
}

final case class IfStatement(position: Position, label: Option[Identifier], ifThenList: Seq[IfStatement.IfThenPart], elseSequentialStatementList: Option[Seq[SequentialStatement]], endLabel: Option[Identifier]) extends SequentialStatement

final case class AMSBreakStatement(position: Position, label: Option[Identifier], elements: Seq[ams.BreakElement], whenExpression: Option[Expression]) extends SequentialStatement

}

package concurrentStatements {

import at.jku.ssw.openvc.symbolTable.symbols.{Symbol, ProcessSymbol}

abstract sealed class ConcurrentStatement extends ASTNode {
  val label: Option[Identifier]
}

abstract sealed class ConcurrentSignalAssignmentStatement extends ConcurrentStatement {
  val postponed: Boolean
  val label: Option[Identifier]
  val guarded: Boolean
  val target: Target
  val delayMechanism: Option[DelayMechanism]
}

object ConcurrentConditionalSignalAssignment {
  final class When(val waveForm: Waveform, val condition: Expression)
}

final case class ConcurrentConditionalSignalAssignment(position: Position, label: Option[Identifier], postponed: Boolean, target: Target, guarded: Boolean,
                                                       delayMechanism: Option[DelayMechanism], alternatives: Seq[ConcurrentConditionalSignalAssignment.When]) extends ConcurrentSignalAssignmentStatement

object ConcurrentSelectedSignalAssignment {
  final class When(val waveForm: Waveform, val choices: Choices)
}

final case class ConcurrentSelectedSignalAssignment(position: Position, label: Option[Identifier], postponed: Boolean, expression: Expression, target: Target,
                                                    guarded: Boolean, delayMechanism: Option[DelayMechanism], alternatives: Seq[ConcurrentSelectedSignalAssignment.When])
  extends ConcurrentSignalAssignmentStatement

final case class ConcurrentProcedureCallStatement(label: Option[Identifier], postponed: Boolean, procedureName: SelectedName, parameterAssociationList: Option[AssociationList])
  extends ConcurrentStatement {
  val position = procedureName.position
}

final case class ConcurrentAssertionStatement(position: Position, label: Option[Identifier], postponed: Boolean, condition: Expression, reportExpression: Option[Expression], severityExpression: Option[Expression]) extends ConcurrentStatement

final case class IfGenerateStatement(position: Position, label: Option[Identifier], condition: Expression, declarativeItems: Seq[declarations.DeclarativeItem], statementList: Seq[ConcurrentStatement], endLabel: Option[Identifier],
                                     value: Boolean = true) extends ConcurrentStatement

final case class ForGenerateStatement(position: Position, label: Option[Identifier], loopIdentifier: Identifier, discreteRange: DiscreteRange, declarativeItems: Seq[declarations.DeclarativeItem],
                                      statementList: Seq[ConcurrentStatement], endLabel: Option[Identifier]) extends ConcurrentStatement

object ComponentInstantiationStatement {
  type ComponentType = ComponentType.Value

  object ComponentType extends Enumeration {
    val COMPONENT, ENTITY, CONFIGURATION = Value
  }

}

final case class ComponentInstantiationStatement(position: Position, label: Option[Identifier], componentType: ComponentInstantiationStatement.ComponentType, name: SelectedName, architectureIdentifier: Option[Identifier],
                                                 genericAssociationList: Option[AssociationList], portAssociationList: Option[AssociationList], symbol: Symbol = null) extends ConcurrentStatement

final case class ProcessStatement(position: Position, label: Option[Identifier], postponed: Boolean, sensitivityList: Option[Seq[Name]], declarativeItems: Seq[declarations.DeclarativeItem],
                                  sequentialStatementList: Seq[sequentialStatements.SequentialStatement], endLabel: Option[Identifier],
                                  localSymbols: Seq[Symbol] = Seq(), symbol: ProcessSymbol = null) extends ConcurrentStatement

final case class BlockStatement(position: Position, label: Option[Identifier], guardExpression: Option[Expression], genericInterfaceList: Option[InterfaceList],
                                genericAssociationList: Option[AssociationList], portInterfaceList: Option[InterfaceList], portAssociationList: Option[AssociationList],
                                declarativeItems: Seq[declarations.DeclarativeItem], statementList: Seq[ConcurrentStatement], endLabel: Option[Identifier]) extends ConcurrentStatement

final case class ConcurrentBreakStatement(position: Position, label: Option[Identifier], breakElements: Option[Seq[ams.BreakElement]], onNameList: Option[Seq[SelectedName]], whenExpression: Option[Expression]) extends ConcurrentStatement

}

package declarations {

import at.jku.ssw.openvc.symbolTable.symbols._
import concurrentStatements.ConcurrentStatement

abstract sealed class DeclarativeItem extends ASTNode

object EntityClass extends Enumeration {
  val ENTITY, ARCHITECTURE, CONFIGURATION, PACKAGE, PROCEDURE, FUNCTION, TYPE, SUBTYPE, CONSTANT, SIGNAL, VARIABLE, FILE, COMPONENT, LABEL,
  LITERAL, UNITS, GROUP, NATURE, SUBNATURE, QUANTITY, TERMINAL = Value
}

final case class AliasDeclaration(position: Position, identifier: Identifier, subType: Option[SubTypeIndication], name: Name, signature: Option[Signature]) extends DeclarativeItem

final case class AttributeSpecification(position: Position, identifier: Identifier, entityList: Either[Seq[(Identifier, Option[Signature])], Identifier], entityClass: EntityClass.Value, expression: Expression, symbol: ConstantSymbol = null) extends DeclarativeItem

final case class AttributeDeclaration(position: Position, identifier: Identifier, typeName: SelectedName) extends DeclarativeItem

abstract sealed class ObjectDeclaration extends DeclarativeItem {
  val identifierList: Seq[Identifier]
  val subType: SubTypeIndication
  val symbols: Seq[RuntimeSymbol]
}

final case class VariableDeclaration(position: Position, shared: Boolean, identifierList: Seq[Identifier], subType: SubTypeIndication, initialValueExpression: Option[Expression], symbols: Seq[RuntimeSymbol] = Seq())
  extends ObjectDeclaration

final case class ConstantDeclaration(position: Position, identifierList: Seq[Identifier], subType: SubTypeIndication, defaultExpression: Option[Expression], symbols: Seq[RuntimeSymbol] = Seq())
  extends ObjectDeclaration

final case class FileDeclaration(position: Position, identifierList: Seq[Identifier], subType: SubTypeIndication, fileOpenKindExpression: Option[Expression], fileLogicalName: Option[Expression], symbols: Seq[RuntimeSymbol] = Seq())
  extends ObjectDeclaration

object SignalDeclaration {
  type Type = Type.Value

  object Type extends Enumeration {
    val REGISTER, BUS = Value
  }

}

final case class SignalDeclaration(position: Position, identifierList: Seq[Identifier], subType: SubTypeIndication, signalType: Option[SignalDeclaration.Type], defaultExpression: Option[Expression], symbols: Seq[RuntimeSymbol] = Seq())
  extends ObjectDeclaration

final case class ComponentDeclaration(position: Position, identifier: Identifier, genericInterfaceList: Option[InterfaceList], portInterfaceList: Option[InterfaceList], endIdentifier: Option[Identifier], symbol: ComponentSymbol = null)
  extends DeclarativeItem

final case class SubTypeDeclaration(position: Position, identifier: Identifier, subTypeIndication: SubTypeIndication) extends DeclarativeItem

abstract sealed class SubProgramDeclaration extends DeclarativeItem {
  val parameterInterfaceList: Option[InterfaceList]
  val identifier: Identifier
}

final case class FunctionDeclaration(position: Position, pure: Boolean, identifier: Identifier, parameterInterfaceList: Option[InterfaceList], returnType: SelectedName, symbol: FunctionSymbol = null)
  extends SubProgramDeclaration

final case class ProcedureDeclaration(position: Position, identifier: Identifier, parameterInterfaceList: Option[InterfaceList], symbol: ProcedureSymbol = null)
  extends SubProgramDeclaration

final case class UseClause(position: Position, useList: Seq[SelectedName]) extends DeclarativeItem

final case class DesignFile(designUnits: Seq[DesignUnit]) extends ASTNode {
  lazy val position = designUnits.head.position
}

final case class DesignUnit(position: Position, libraries: Seq[Identifier], useClauses: Seq[UseClause], libraryUnit: Option[LibraryUnit]) extends ASTNode

abstract sealed class LibraryUnit extends ASTNode {
  val symbol: Symbol
  val declarativeItems: Seq[DeclarativeItem]
  val identifier: Identifier
  val endIdentifier: Option[Identifier]
  val position = identifier.position
}

final case class ConfigurationDeclaration(identifier: Identifier, declarativeItems: Seq[DeclarativeItem], entityName: SelectedName, blockConfiguration: BlockConfiguration,
                                          endIdentifier: Option[Identifier], symbol: ConfigurationSymbol = null) extends LibraryUnit

final case class ArchitectureDeclaration(identifier: Identifier, declarativeItems: Seq[DeclarativeItem], entityName: SelectedName,
                                         concurrentStatements: Seq[ConcurrentStatement], endIdentifier: Option[Identifier],
                                         symbol: ArchitectureSymbol = null) extends LibraryUnit

final case class EntityDeclaration(identifier: Identifier, genericInterfaceList: Option[InterfaceList], portInterfaceList: Option[InterfaceList],
                                   declarativeItems: Seq[DeclarativeItem], concurrentStatements: Seq[ConcurrentStatement], endIdentifier: Option[Identifier], symbol: EntitySymbol = null) extends LibraryUnit

final case class PackageDeclaration(identifier: Identifier, declarativeItems: Seq[DeclarativeItem], endIdentifier: Option[Identifier], symbol: PackageSymbol = null) extends LibraryUnit

final case class PackageBodyDeclaration(identifier: Identifier, declarativeItems: Seq[DeclarativeItem], endIdentifier: Option[Identifier], symbol: PackageSymbol = null) extends LibraryUnit

abstract sealed class SubProgramDefinition extends DeclarativeItem {
  val parameterInterfaceList: Option[InterfaceList]
  val identifier: Identifier
}

final case class FunctionDefinition(position: Position, pure: Boolean, identifier: Identifier, parameterInterfaceList: Option[InterfaceList], returnType: SelectedName,
                                    declarativeItems: Seq[DeclarativeItem], sequentialStatementList: Seq[sequentialStatements.SequentialStatement], endIdentifier: Option[Identifier],
                                    localSymbols: Seq[Symbol] = Seq(), symbol: FunctionSymbol = null) extends SubProgramDefinition

final case class ProcedureDefinition(position: Position, identifier: Identifier, parameterInterfaceList: Option[InterfaceList], declarativeItems: Seq[DeclarativeItem],
                                     sequentialStatementList: Seq[sequentialStatements.SequentialStatement], endIdentifier: Option[Identifier],
                                     localSymbols: Seq[Symbol] = Seq(), symbol: ProcedureSymbol = null) extends SubProgramDefinition

final case class ConfigurationSpecification(position: Position, componentSpecification: ComponentSpecification, bindingIndication: BindingIndication) extends DeclarativeItem

final case class GroupDeclaration(position: Position, identifier: Identifier, groupTemplateName: SelectedName, constituentList: Seq[Either[Name, Identifier]]) extends DeclarativeItem

object GroupTemplateDeclaration {
  final class Element(val entityClass: EntityClass.Value, val box: Boolean)
}

final case class GroupTemplateDeclaration(position: Position, identifier: Identifier, elements: Seq[GroupTemplateDeclaration.Element]) extends DeclarativeItem

final case class DisconnectionSpecification(position: Position, signalListOrIdentifier: Either[Seq[SelectedName], Identifier], typeName: SelectedName, timeExpression: Expression) extends DeclarativeItem

abstract sealed class AbstractTypeDeclaration extends DeclarativeItem {
  val identifier: Identifier
  val dataType: DataType
}

final case class IncompleteTypeDeclaration(position: Position, identifier: Identifier, dataType: DataType = NoType) extends AbstractTypeDeclaration

final case class IntegerOrFloatingPointTypeDefinition(position: Position, identifier: Identifier, range: Range, dataType: DataType = NoType) extends AbstractTypeDeclaration

final case class AccessTypeDefinition(position: Position, identifier: Identifier, subType: SubTypeIndication, dataType: DataType = NoType) extends AbstractTypeDeclaration

object RecordTypeDefinition {
  final class Element(val identifierList: Seq[Identifier], val subType: SubTypeIndication)
}

final case class RecordTypeDefinition(position: Position, identifier: Identifier, elements: Seq[RecordTypeDefinition.Element], endIdentifier: Option[Identifier], dataType: DataType = NoType) extends AbstractTypeDeclaration

object PhysicalTypeDefinition {
  final class Element(val identifier: Identifier, val literal: expressions.PhysicalLiteral)
}

final case class PhysicalTypeDefinition(position: Position, identifier: Identifier, range: Range, baseIdentifier: Identifier, elements: Seq[PhysicalTypeDefinition.Element], endIdentifier: Option[Identifier], dataType: DataType = NoType) extends AbstractTypeDeclaration

final case class FileTypeDefinition(position: Position, identifier: Identifier, typeName: SelectedName, dataType: DataType = NoType) extends AbstractTypeDeclaration

abstract sealed class AbstractArrayTypeDefinition extends AbstractTypeDeclaration {
  val subType: SubTypeIndication
}

final case class UnconstrainedArrayTypeDefinition(position: Position, identifier: Identifier, dimensions: Seq[SelectedName], subType: SubTypeIndication, dataType: DataType = NoType) extends AbstractArrayTypeDefinition

final case class ConstrainedArrayTypeDefinition(position: Position, identifier: Identifier, dimensions: Seq[DiscreteRange], subType: SubTypeIndication, dataType: DataType = NoType) extends AbstractArrayTypeDefinition

final case class EnumerationTypeDefinition(position: Position, identifier: Identifier, elements: Seq[Identifier], dataType: DataType = NoType) extends AbstractTypeDeclaration

final case class ProtectedTypeBodyDeclaration(position: Position, identifier: Identifier, declarativeItems: Seq[DeclarativeItem], endIdentifier: Option[Identifier], dataType: DataType = NoType) extends AbstractTypeDeclaration

final case class ProtectedTypeDeclaration(position: Position, identifier: Identifier, declarativeItems: Seq[DeclarativeItem], endIdentifier: Option[Identifier], dataType: DataType = NoType) extends AbstractTypeDeclaration

}

package ams {

abstract sealed class SimultaneousStatement extends concurrentStatements.ConcurrentStatement

final case class SimultaneousNullStatement(position: Position, label: Option[Identifier]) extends SimultaneousStatement

final case class SimpleSimultaneousStatement(label: Option[Identifier], firstExpression: Expression, secondExpression: Expression, toleranceExpression: Option[Expression]) extends SimultaneousStatement {
  val position = firstExpression.position
}

final case class SimultaneousProceduralStatement(position: Position, label: Option[Identifier], declarativeItems: Seq[declarations.DeclarativeItem], sequentialStatementList: Seq[sequentialStatements.SequentialStatement], endLabel: Option[Identifier]) extends SimultaneousStatement

object SimultaneousCaseStatement {
  final class When(val choices: Choices, val statements: Seq[SimultaneousStatement])
}

final case class SimultaneousCaseStatement(position: Position, label: Option[Identifier], expression: Expression, alternatives: Seq[SimultaneousCaseStatement.When], endLabel: Option[Identifier]) extends SimultaneousStatement

object SimultaneousIfStatement {
  final class IfUsePart(val condition: Expression, val statements: Seq[SimultaneousStatement])
}

final case class SimultaneousIfStatement(position: Position, label: Option[Identifier], ifUseList: Seq[SimultaneousIfStatement.IfUsePart],
                                         elseSimultaneousStatementList: Option[Seq[SimultaneousStatement]], endLabel: Option[Identifier]) extends SimultaneousStatement

final case class TerminalDeclaration(position: Position, identifierList: Seq[Identifier], subNature: SubNatureIndication) extends declarations.DeclarativeItem

final case class SubNatureIndication(natureMark: SelectedName, ranges: Option[Seq[DiscreteRange]], toleranceExpression: Option[Expression], acrossExpression: Option[Expression])

final case class SubNatureDeclaration(position: Position, identifier: Identifier, subNature: SubNatureIndication) extends declarations.DeclarativeItem

final case class ScalarNatureDefinition(position: Position, identifier: Identifier, typeName: SelectedName, acrossType: SelectedName, throughIdentifier: Identifier, dataType: DataType = NoType) extends declarations.AbstractTypeDeclaration

abstract sealed class AbstractArrayNatureTypeDefinition extends declarations.AbstractTypeDeclaration {
  val subNature: SubNatureIndication
}

final case class UnconstrainedArrayNatureTypeDefinition(position: Position, identifier: Identifier, dimensions: Seq[SelectedName], subNature: SubNatureIndication, dataType: DataType = NoType) extends AbstractArrayNatureTypeDefinition

final case class ConstrainedArrayNatureTypeDefinition(position: Position, identifier: Identifier, dimensions: Seq[DiscreteRange], subNature: SubNatureIndication, dataType: DataType = NoType) extends AbstractArrayNatureTypeDefinition

object RecordNatureDefinition {
  final class Element(val identifierList: Seq[Identifier], val subNature: SubNatureIndication)
}

final case class RecordNatureDefinition(position: Position, identifier: Identifier, elements: Seq[RecordNatureDefinition.Element], endIdentifier: Option[Identifier], dataType: DataType = NoType) extends declarations.AbstractTypeDeclaration

final class BreakElement(val forQuantityName: Option[Name], val name: Name, val expression: Expression)

final case class StepLimitSpecification(position: Position, signalListOrIdentifier: Either[Seq[SelectedName], Identifier], typeName: SelectedName, withExpression: Expression) extends declarations.DeclarativeItem

abstract sealed class AbstractQuantityDeclaration extends declarations.DeclarativeItem

final case class FreeQuantityDeclaration(position: Position, identifierList: Seq[Identifier], subType: SubTypeIndication, expression: Option[Expression]) extends AbstractQuantityDeclaration

final case class BranchQuantityDeclaration(position: Position, acrossIdentifierList: Option[Seq[Identifier]], acrossToleranceExpression: Option[Expression], acrossExpression: Option[Expression],
                                           throughIdentifierList: Option[Seq[Identifier]], throughToleranceExpression: Option[Expression], throughExpression: Option[Expression],
                                           terminalPlusName: Name, terminalMinusName: Option[Name]) extends AbstractQuantityDeclaration

final case class SourceQuantityDeclaration(position: Position, identifierList: Seq[Identifier], subType: SubTypeIndication, spectrumMagnitudeExpression: Expression,
                                           spectrumPhaseExpression: Expression, noiseExpression: Expression) extends AbstractQuantityDeclaration

}

}