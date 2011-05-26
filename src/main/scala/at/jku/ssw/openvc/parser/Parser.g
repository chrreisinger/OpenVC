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

parser grammar Parser;

options {
	language   = Scala;
	memoize    = true;
	superClass = BaseParser;
	tokenVocab = Lexer;
}

@parser::header {
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

package at.jku.ssw.openvc.parser

import at.jku.ssw.openvc._
import ast._
import ast.concurrentStatements._
import ast.sequentialStatements._
import ast.designUnits._
import ast.declarativeItems._
import ast.expressions._
import ast.simultaneousStatements._
import util._
}

saveFollowSet 
@init {followSet = state.following(state._fsp)} :;

sync [String ruleName]
@init {syncAndAddError($ruleName)} :; // Deliberately match nothing, causing this rule always to be entered.

//B.1 Design File
design_file returns [ASTNode file = NoNode]
@init {val units = new Buffer[DesignUnit]} :
	(design_unit {units += $design_unit.unit})+ EOF
	{$file = new DesignFile(units.result)};

context_item returns [ContextItem item = NoNode] :
	library_clause {$item = $library_clause.clause}
	| use_clause {$item = $use_clause.clause}
	| {vhdl2008}?=> v2008_context_reference {$item = $v2008_context_reference.reference};

context_items returns [Seq[ContextItem\] items = Nil]
@init {val buffer = new Buffer[ContextItem]} :
	(context_item {buffer += $context_item.item})* 
	{$items = buffer.result};

design_unit returns [DesignUnit unit] :
	context_items library_unit 
	{$unit = new DesignUnit($context_items.items, $library_unit.unit)};

library_unit returns [LibraryUnit unit = NoNode]
@after {$unit = if ($unit != null) $unit else NoNode} :
	entity_declaration {$unit = $entity_declaration.declaration}
	| architecture_body {$unit = $architecture_body.body}
	| package_declaration {$unit = $package_declaration.declaration}
	| package_body {$unit = $package_body.body}
	| {vhdl2008}?=> v2008_package_instantiation_declaration {$unit = $v2008_package_instantiation_declaration.declaration}
	| {vhdl2008}?=> v2008_context_declaration {$unit = $v2008_context_declaration.declaration}
	| configuration_declaration {$unit = $configuration_declaration.declaration}
	//| {psl}?=> PSL_Verification_Unit
	;

library_clause returns [ContextItem clause = NoNode] :
	LIBRARY identifier_list SEMICOLON {$clause = new LibraryClause($LIBRARY, $identifier_list.list)};

v2008_context_reference returns [ContextItem reference = NoNode] :
	CONTEXT selected_name_list SEMICOLON {$reference = new ContextReference($CONTEXT, $selected_name_list.list)};

//B.2 Library Unit Declarations
generic_clause returns [Seq[InterfaceList.AbstractInterfaceElement\] clause = Nil] :
	GENERIC LPAREN generic_list RPAREN SEMICOLON
	{$clause = $generic_list.list};

generic_list returns [Seq[InterfaceList.AbstractInterfaceElement\] list = Nil]
@init {val elements = new Buffer[InterfaceList.AbstractInterfaceElement]} :
	element=interface_element_generic {elements += element} (SEMICOLON element=interface_element_generic {elements += element})*
	{$list = elements.result};

port_clause returns [Seq[InterfaceList.AbstractInterfaceElement\] clause = Nil] :
	PORT LPAREN port_list RPAREN SEMICOLON
	{$clause = $port_list.list};

port_list returns [Seq[InterfaceList.AbstractInterfaceElement\] list = Nil]
@init {val elements = new Buffer[InterfaceList.AbstractInterfaceElement]} :
	element=interface_element_port {elements += element} (SEMICOLON element=interface_element_port {elements += element})*
	{$list = elements.result};

entity_declaration returns [LibraryUnit declaration = NoNode]
@init {
	val items = new Buffer[DeclarativeItem]
	val statements = new Buffer[ConcurrentStatement]
	val ruleName = "entity declarative item"
} :
	entityToken=ENTITY startIdentifier=identifier IS
		generic_clause?
		port_clause?
		sync[ruleName] (entity_declarative_item {items += $entity_declarative_item.item} sync[ruleName])*
	(BEGIN
		(label=label_colon? POSTPONED? (
			concurrent_assertion_statement[$label.label, $POSTPONED != null] {statements += $concurrent_assertion_statement.statement}
			| concurrent_procedure_call_statement[$label.label, $POSTPONED != null] {statements += $concurrent_procedure_call_statement.statement}
			| process_statement[$label.label, $POSTPONED != null] {statements += $process_statement.statement}
			//| {psl}?=> PSL_PSL_Directive
			)
		)* 
	)?
	END ENTITY? endIdentifier=identifier? SEMICOLON
	{$declaration = new EntityDeclaration($entityToken, $startIdentifier.ident, $generic_clause.clause, $port_clause.clause, items.result, statements.result, $endIdentifier.ident)};

entity_declarative_item returns [DeclarativeItem item = NoNode] :
	subprogram_specification_or_body {item = $subprogram_specification_or_body.specificationOrBody}
	| {vhdl2008}?=> (
		v2008_subprogram_instantiation_declaration {$item = $v2008_subprogram_instantiation_declaration.declaration}
		| package_declaration {$item = $package_declaration.declaration}
		| package_body {$item = $package_body.body}
		| v2008_package_instantiation_declaration {$item = $v2008_package_instantiation_declaration.declaration}
		)
	| type_declaration {$item = $type_declaration.declaration}
	| subtype_declaration {$item = $subtype_declaration.declaration}
	| constant_declaration {$item = $constant_declaration.declaration}
	| signal_declaration {$item = $signal_declaration.declaration}
	| variable_declaration {$item = $variable_declaration.declaration}
	| file_declaration {$item = $file_declaration.declaration}
	| alias_declaration {$item = $alias_declaration.declaration}
	| attribute_declaration {$item = $attribute_declaration.declaration}
	| attribute_specification {$item = $attribute_specification.specification}
	| disconnection_specification {$item = $disconnection_specification.specification}
	| use_clause {$item = $use_clause.clause}
	| group_template_declaration {$item = $group_template_declaration.declaration}
	| group_declaration {$item = $group_declaration.declaration}
	/*| {psl}?=> (
		PSL_Property_Declaration
		| PSL_Sequence_Declaration
		| PSL_Clock_Declaration
		)*/
	| {ams}?=> (
		ams_step_limit_specification {$item = $ams_step_limit_specification.specification}
		| ams_nature_declaration {$item = $ams_nature_declaration.declaration}
		| ams_subnature_declaration {$item = $ams_subnature_declaration.declaration}
		| ams_quantity_declaration {$item = $ams_quantity_declaration.declaration}
		| ams_terminal_declaration {$item = $ams_terminal_declaration.declaration}
		);

architecture_body returns [LibraryUnit body = NoNode]
@init {
	val items = new Buffer[DeclarativeItem]
	val ruleName = "block declarative item"
} :
	architectureToken=ARCHITECTURE startIdentifier=identifier OF selected_name IS
		sync[ruleName] (block_declarative_item {items += $block_declarative_item.item} sync[ruleName])*
	BEGIN
		concurrent_statement_list
	END ARCHITECTURE? endIdentifier=identifier? SEMICOLON
	{$body = new ArchitectureDeclaration($architectureToken, $startIdentifier.ident, items.result, $selected_name.name, $concurrent_statement_list.list, $endIdentifier.ident)};

configuration_declarative_item returns [DeclarativeItem item = NoNode] :
	use_clause {$item = $use_clause.clause}
	| attribute_specification {$item = $attribute_specification.specification}
	| group_declaration {$item = $group_declaration.declaration};

configuration_declaration returns [LibraryUnit declaration = NoNode]
@init {
	val items = new Buffer[DeclarativeItem]
	val ruleName = "configuration declarative item"
} :
	configurationToken=CONFIGURATION startIdentifier=identifier OF selected_name IS
		sync[ruleName] (configuration_declarative_item {items += $configuration_declarative_item.item} sync[ruleName])*
		//({psl}?=> psl_verification_unit_binding_indication)*
		block_configuration
	END CONFIGURATION? endIdentifier=identifier? SEMICOLON
	{$declaration = new ConfigurationDeclaration($configurationToken, $startIdentifier.ident, items.result, $selected_name.name, $block_configuration.configuration, $endIdentifier.ident)};

generate_specification returns [TripleEither[DiscreteRange, Identifier, Expression\] specification] :
	(discrete_range)=> discrete_range {$specification = First($discrete_range.discreteRange)}
	| {vhdl2008 && (input.LA(1) == BASIC_IDENTIFIER || input.LA(1) == EXTENDED_IDENTIFIER) && input.LA(2) == RPAREN}?=> identifier {$specification = Second($identifier.ident)}
	| expression {$specification = Third($expression.expr)};
	
block_specification returns [BlockConfigurationSpecification specification] :
	(identifier LPAREN)=> identifier (LPAREN generate_specification RPAREN)? {$specification = new BlockConfigurationSpecification(Right(($identifier.ident, $generate_specification.specification)))}
	| selected_name {$specification = new BlockConfigurationSpecification(Left($selected_name.name))};

block_configuration returns [BlockConfiguration configuration]
@init {
	val clauses = new Buffer[UseClause]
	val configurations = new Buffer[Either[BlockConfiguration, ComponentConfiguration]]
} :
	FOR block_specification
		(use_clause {clauses += $use_clause.clause})*
		(
			blockConfiguration=block_configuration {configurations += Left($blockConfiguration.configuration)}
			| component_configuration {configurations += Right($component_configuration.configuration)}
		)*
	END FOR SEMICOLON 
	{$configuration = new BlockConfiguration($block_specification.specification, clauses.result, configurations.result)};

//psl_verification_unit_binding_indication : USE VUNIT selected_name_list SEMICOLON;

component_configuration returns [ComponentConfiguration configuration] :
	FOR component_specification
		(binding_indication SEMICOLON)?
		//({psl}?=> psl_verification_unit_binding_indication)*
		block_configuration?
	END FOR SEMICOLON
	{$configuration = new ComponentConfiguration($component_specification.specification, $binding_indication.indication, $block_configuration.configuration)};

v2008_context_declaration returns [LibraryUnit declaration = NoNode] :
	contextToken=CONTEXT startIdentifier=identifier IS
		context_items
	END CONTEXT? endIdentifier=identifier? SEMICOLON
	{$declaration = new ContextDeclaration($contextToken, $startIdentifier.ident, $context_items.items, $endIdentifier.ident)};

//B.3 Declarations and Specifications
package_declaration returns [LibraryUnit with DeclarativeItem declaration = NoNode]
@init {
	val items = new Buffer[DeclarativeItem]
	val ruleName = "package declarative item"
} :
	packageToken=PACKAGE startIdentifier=identifier IS
		({vhdl2008}?=> generic_clause (generic_map_aspect SEMICOLON)?)?
		sync[ruleName] (package_declarative_item {items += $package_declarative_item.item} sync[ruleName])*
	END PACKAGE? endIdentifier=identifier? SEMICOLON
	{$declaration = new PackageDeclaration($packageToken, $startIdentifier.ident, $generic_clause.clause, $generic_map_aspect.aspect, items.result, $endIdentifier.ident)};

package_declarative_item returns [DeclarativeItem item = NoNode] :
	subprogram_declaration {$item = $subprogram_declaration.declaration}
	| {vhdl2008}?=> (
		v2008_subprogram_instantiation_declaration {$item = $v2008_subprogram_instantiation_declaration.declaration}
		| package_declaration {$item = $package_declaration.declaration}
		| v2008_package_instantiation_declaration {$item = $v2008_package_instantiation_declaration.declaration}
		)
	| type_declaration {$item = $type_declaration.declaration}
	| subtype_declaration {$item = $subtype_declaration.declaration}
	| constant_declaration {$item = $constant_declaration.declaration}
	| signal_declaration {$item = $signal_declaration.declaration}
	| variable_declaration {$item = $variable_declaration.declaration}
	| file_declaration {$item = $file_declaration.declaration}
	| alias_declaration {$item = $alias_declaration.declaration}
	| component_declaration {$item = $component_declaration.declaration}
	| attribute_declaration {$item = $attribute_declaration.declaration}
	| attribute_specification {$item = $attribute_specification.specification}
	| disconnection_specification {$item = $disconnection_specification.specification}
	| use_clause {$item = $use_clause.clause}
	| group_template_declaration {$item = $group_template_declaration.declaration}
	| group_declaration {$item = $group_declaration.declaration}
	//| {psl}?=> PSL_Property_Declaration 
	//| {psl}?=> PSL_Sequence_Declaration
	| {ams}?=> (
		ams_nature_declaration {$item = $ams_nature_declaration.declaration}
		| ams_subnature_declaration {$item = $ams_subnature_declaration.declaration}
		| ams_terminal_declaration {$item = $ams_terminal_declaration.declaration}
		);

package_body returns [LibraryUnit with DeclarativeItem body = NoNode]
@init {
	val items = new Buffer[DeclarativeItem]
	val ruleName = "package body declarative item"
} :
	packageToken=PACKAGE BODY startIdentifier=identifier IS
		sync[ruleName] (package_body_declarative_item {items += $package_body_declarative_item.item} sync[ruleName])*
	END (PACKAGE BODY)? endIdentifier=identifier? SEMICOLON
	{$body = new PackageBodyDeclaration($packageToken, $startIdentifier.ident, items.result, $endIdentifier.ident)};

package_body_declarative_item returns [DeclarativeItem item = NoNode] :
	subprogram_specification_or_body {$item = $subprogram_specification_or_body.specificationOrBody}
	| {vhdl2008}?=> (
		v2008_subprogram_instantiation_declaration {$item = $v2008_subprogram_instantiation_declaration.declaration}
		| package_declaration {$item = $package_declaration.declaration}
		| package_body {$item = $package_body.body}
		| v2008_package_instantiation_declaration {$item = $v2008_package_instantiation_declaration.declaration}
		| attribute_declaration {$item = $attribute_declaration.declaration}
		)
	| type_declaration {$item = $type_declaration.declaration}
	| subtype_declaration {$item = $subtype_declaration.declaration}
	| constant_declaration {$item = $constant_declaration.declaration}
	| variable_declaration {$item = $variable_declaration.declaration}
	| file_declaration {$item = $file_declaration.declaration}
	| alias_declaration {$item = $alias_declaration.declaration}
	| use_clause {$item = $use_clause.clause}
	| /*{vhdl2008}?=>*/attribute_specification {$item = $attribute_specification.specification} //attribute_specification is not part of VHDL 2002, added to compile ghdl math_real-body.vhd
	| group_template_declaration {$item = $group_template_declaration.declaration}
	| group_declaration {$item = $group_declaration.declaration};

v2008_package_instantiation_declaration returns [LibraryUnit with DeclarativeItem declaration = NoNode] :
	PACKAGE identifier IS NEW selected_name
		generic_map_aspect? SEMICOLON
	{$declaration = new PackageInstantiationDeclaration($PACKAGE, $identifier.ident, $selected_name.name, $generic_map_aspect.aspect)};

designator returns [Identifier designator = NoIdentifier] :
	identifier {$designator = $identifier.ident}
	| STRING_LITERAL {$designator = toIdentifier($STRING_LITERAL)}; //STRING_LITERAL is a operator symbol

subprogram_specification returns [DeclarativeItem specification = NoNode] :
	PROCEDURE identifier
	({vhdl2008}?=> GENERIC LPAREN generic_list RPAREN generic_map_aspect?)?
	(({vhdl2008}?=> PARAMETER)? LPAREN parameter_interface_list_procedure RPAREN)?
	{$specification = new ProcedureDeclaration($PROCEDURE, $identifier.ident, $generic_list.list, $generic_map_aspect.aspect, $parameter_interface_list_procedure.list)}
	| (PURE | impure=IMPURE)? FUNCTION designator
	({vhdl2008}?=> GENERIC LPAREN generic_list RPAREN generic_map_aspect?)?
	(({vhdl2008}?=> PARAMETER)? LPAREN parameter_interface_list_function RPAREN)? RETURN type_mark
	{$specification = new FunctionDeclaration($FUNCTION, $impure == null, $designator.designator, $generic_list.list, $generic_map_aspect.aspect, $parameter_interface_list_function.list, $type_mark.mark)};

subprogram_specification_or_body returns [DeclarativeItem specificationOrBody = NoNode] :
	subprogram_specification (subprogram_body[$subprogram_specification.specification])? SEMICOLON
	{$specificationOrBody = if ($subprogram_body.body != null) $subprogram_body.body else $subprogram_specification.specification};

subprogram_declaration returns [DeclarativeItem declaration = NoNode] :
	subprogram_specification SEMICOLON
	{$declaration = $subprogram_specification.specification};

subprogram_body[DeclarativeItem declaration] returns [DeclarativeItem body = NoNode]
@init {
	val items = new Buffer[DeclarativeItem]
	val ruleName = "subprogram declarative item"
} :
	IS
		sync[ruleName] (subprogram_declarative_item {items += $subprogram_declarative_item.item} sync[ruleName])*
	BEGIN
		saveFollowSet sequence_of_statements
	END ({$declaration.isInstanceOf[ProcedureDeclaration]}?=> PROCEDURE | {$declaration.isInstanceOf[FunctionDeclaration]}?=> FUNCTION)? endIdent=designator?
	{
		$body = $declaration match {
			case procDecl: ProcedureDeclaration => 
				new ProcedureDefinition($declaration.position, procDecl.identifier, procDecl.genericInterfaceList, procDecl.genericAssociationList, procDecl.parameterInterfaceList, items.result, $sequence_of_statements.statements, endIdent)
			case funcDecl: FunctionDeclaration =>
				new FunctionDefinition($declaration.position, funcDecl.isPure, funcDecl.identifier, funcDecl.genericInterfaceList, funcDecl.genericAssociationList, funcDecl.parameterInterfaceList, funcDecl.returnType, items.result, $sequence_of_statements.statements, endIdent)
			case _ => NoNode
		}
	};

subprogram_declarative_item returns [DeclarativeItem item = NoNode] :
	subprogram_specification_or_body {$item = $subprogram_specification_or_body.specificationOrBody}
	| {vhdl2008}?=> (
		v2008_subprogram_instantiation_declaration {$item = $v2008_subprogram_instantiation_declaration.declaration}
		| package_declaration {$item = $package_declaration.declaration}
		| package_body {$item = $package_body.body}
		| v2008_package_instantiation_declaration {$item = $v2008_package_instantiation_declaration.declaration}
		)
	| type_declaration {$item = $type_declaration.declaration}
	| subtype_declaration {$item = $subtype_declaration.declaration}
	| constant_declaration {$item = $constant_declaration.declaration}
	| variable_declaration {$item = $variable_declaration.declaration}
	| file_declaration {$item = $file_declaration.declaration}
	| alias_declaration {$item = $alias_declaration.declaration}
	| attribute_declaration {$item = $attribute_declaration.declaration}
	| attribute_specification {$item = $attribute_specification.specification}
	| use_clause {$item = $use_clause.clause}
	| group_template_declaration {$item = $group_template_declaration.declaration}
	| group_declaration {$item = $group_declaration.declaration};

v2008_subprogram_instantiation_declaration returns [DeclarativeItem declaration = NoNode] :
	(PROCEDURE | functionToken=FUNCTION) identifier IS
		NEW selected_name signature?
			generic_map_aspect? SEMICOLON
	{$declaration = new SubprogramInstantiationDeclaration(if ($PROCEDURE != null) $PROCEDURE else $functionToken, $PROCEDURE != null, $identifier.ident, $selected_name.name, $signature.signature, $generic_map_aspect.aspect)};

type_declaration returns [DeclarativeItem declaration = NoNode] :
	TYPE identifier (IS type_definition[$identifier.ident, toPosition($TYPE)])? SEMICOLON
	{
		$declaration =
			if ($type_definition.definition != null) $type_definition.definition
			else new IncompleteTypeDeclaration($TYPE, $identifier.ident)
	};

type_definition[Identifier name, Position position] returns [DeclarativeItem definition = NoNode] :
	enumeration_type_definition[$name, $position] {$definition = $enumeration_type_definition.definition}
	| numeric_type_definition[$name, $position] {$definition = $numeric_type_definition.definition} //parses integer, real or physcial type definitions
	| array_type_definition[$name, $position] {$definition = $array_type_definition.definition}
	| record_type_definition[$name, $position] {$definition = $record_type_definition.definition}
	| access_type_definition[$name, $position] {$definition = $access_type_definition.definition}
	| file_type_definition[$name, $position] {$definition = $file_type_definition.definition}
	| protected_type_body[$name, $position] {$definition = $protected_type_body.body}
	| protected_type_declaration[$name, $position] {$definition = $protected_type_declaration.declaration};

ams_nature_declaration returns [DeclarativeItem declaration = NoNode] :
	NATURE identifier IS ams_nature_definition[$identifier.ident, toPosition($NATURE)] SEMICOLON
	{$declaration = $ams_nature_definition.definition};

ams_terminal_declaration returns [DeclarativeItem declaration = NoNode] :
	TERMINAL identifier_list COLON ams_subnature_indication SEMICOLON
	{$declaration = new TerminalDeclaration($TERMINAL, $identifier_list.list, $ams_subnature_indication.indication)};
	
ams_nature_definition[Identifier name, Position position] returns [AbstractTypeDeclaration definition] :
	ams_scalar_nature_definition[$name, $position] {$definition = $ams_scalar_nature_definition.definition}
	| ams_array_nature_definition[$name, $position] {$definition = $ams_array_nature_definition.definition}
	| ams_record_nature_definition[$name, $position] {$definition = $ams_record_nature_definition.definition};

ams_through_aspect returns [(Seq[Identifier\], Option[Expression\], Option[Expression\]) aspect] :
	identifier_list (TOLERANCE toleranceExpression=expression)? (VAR_ASSIGN defaultExpression=expression)? THROUGH
	{$aspect = ($identifier_list.list, $toleranceExpression.expr, $defaultExpression.expr)};

ams_quantity_declaration returns [DeclarativeItem declaration = NoNode] :
	QUANTITY (
		terminal=ams_terminal_aspect {$declaration = new BranchQuantityDeclaration($QUANTITY, None, None, $terminal.aspect)}
		| identifier_list (
			(TOLERANCE toleranceExpression=expression)? (VAR_ASSIGN defaultExpression=expression)? (across=ACROSS | through=THROUGH) ({$through == null}?=> ams_through_aspect)? terminal=ams_terminal_aspect
				{
					$declaration =
						if (across != null) new BranchQuantityDeclaration($QUANTITY, ($identifier_list.list, Option($toleranceExpression.expr), Option($defaultExpression.expr)), $ams_through_aspect.aspect, $terminal.aspect)
						else new BranchQuantityDeclaration($QUANTITY, None, ($identifier_list.list, Option($toleranceExpression.expr), Option($defaultExpression.expr)), $terminal.aspect)
				}
			| COLON subtype_indication
				(
				(VAR_ASSIGN expr=expression)? {$declaration = new FreeQuantityDeclaration($QUANTITY, $identifier_list.list, $subtype_indication.indication, $expr.expr)}
				| ams_source_aspect {$declaration = new SourceQuantityDeclaration($QUANTITY, $identifier_list.list, $subtype_indication.indication, $ams_source_aspect.aspect)}
				)
			)
		) SEMICOLON;

ams_terminal_aspect returns [(Name, Option[Name\]) aspect] :
	plusTerminalName=name (TO minusTerminalName=name)?
	{$aspect = ($plusTerminalName.name, $minusTerminalName.name)};

ams_source_aspect returns [Either[(Expression, Expression), Expression\] aspect] :
	SPECTRUM magnitudeSimpleExpression=simple_expression COMMA phaseSimpleExpression=simple_expression {$aspect = Left(($magnitudeSimpleExpression.expression, $phaseSimpleExpression.expression))}
	| NOISE powerSimpleExpression=simple_expression {$aspect = Right($powerSimpleExpression.expression)};

constant_declaration returns [DeclarativeItem declaration = NoNode] :
	CONSTANT identifier_list COLON subtype_indication (VAR_ASSIGN expression)? SEMICOLON 
	{$declaration = new ConstantDeclaration($CONSTANT, $identifier_list.list, $subtype_indication.indication, $expression.expr)};

signal_declaration returns [DeclarativeItem declaration = NoNode] :
	SIGNAL identifier_list COLON subtype_indication (reg=REGISTER | bus=BUS)? (VAR_ASSIGN expression)? SEMICOLON
	{
		val signalType=
			if(reg != null) Some(SignalDeclaration.Type.REGISTER)
			else if (bus != null) Some(SignalDeclaration.Type.BUS)
			else None
		$declaration = new SignalDeclaration($SIGNAL, $identifier_list.list, $subtype_indication.indication, signalType, $expression.expr)
	};

variable_declaration returns [DeclarativeItem declaration = NoNode] :
	SHARED? VARIABLE identifier_list COLON subtype_indication (VAR_ASSIGN expression)? SEMICOLON
	{$declaration = new VariableDeclaration($VARIABLE, $SHARED != null, $identifier_list.list, $subtype_indication.indication, $expression.expr)};

file_declaration returns [DeclarativeItem declaration = NoNode] :
	FILE identifier_list COLON subtype_indication ((OPEN fileOpenKindExpression=expression)? IS fileLogicalName=expression)? SEMICOLON
	{$declaration = new FileDeclaration($FILE, $identifier_list.list, $subtype_indication.indication, $fileOpenKindExpression.expr, $fileLogicalName.expr)};

alias_declaration returns [DeclarativeItem declaration = NoNode] :
	ALIAS alias_designator (COLON subtype_indication)? IS name signature? SEMICOLON
	{$declaration = new AliasDeclaration($ALIAS, $alias_designator.designator, $subtype_indication.indication, $name.name, $signature.signature)};

alias_designator returns [Identifier designator = NoIdentifier] :
	identifier {$designator = $identifier.ident}
	| CHARACTER_LITERAL {$designator = toIdentifier($CHARACTER_LITERAL)}
	| STRING_LITERAL {$designator = toIdentifier($STRING_LITERAL)};

component_declaration returns [DeclarativeItem declaration = NoNode] :
	componentToken=COMPONENT startIdentifier=identifier IS?
		generic_clause?
		port_clause?
	END COMPONENT endIdentifier=identifier? SEMICOLON
	{$declaration = new ComponentDeclaration($componentToken, $startIdentifier.ident, $generic_clause.clause, $port_clause.clause, $endIdentifier.ident)};

attribute_declaration returns [DeclarativeItem declaration = NoNode] :
	ATTRIBUTE identifier COLON type_mark SEMICOLON 
	{$declaration = new AttributeDeclaration($ATTRIBUTE, $identifier.ident, $type_mark.mark)};

attribute_specification returns [DeclarativeItem specification = NoNode] :
	ATTRIBUTE identifier OF entity_name_list COLON entity_class IS expression SEMICOLON 
	{$specification = new AttributeSpecification($ATTRIBUTE, $identifier.ident, $entity_name_list.list, $entity_class.entityClass, $expression.expr)};

entity_designator returns [(Identifier, Option[Signature\]) designator]
@init {var id: Identifier = null} :
	(
	identifier {id = $identifier.ident}
	| CHARACTER_LITERAL {id = toIdentifier($CHARACTER_LITERAL)}
	| STRING_LITERAL {id = toIdentifier($STRING_LITERAL)}
	) signature?
	{$designator = (id, $signature.signature)};

entity_name_list returns [Either[Seq[(Identifier, Option[Signature\])\], Identifier\] list]
@init {val designators = new Buffer[(Identifier, Option[Signature])]} :
	entityDesignator=entity_designator {designators += entityDesignator} (COMMA entityDesignator=entity_designator {designators += entityDesignator})* {$list = Left(designators.result)}
	| OTHERS {$list = Right(toIdentifier($OTHERS))}
	| ALL {$list = Right(toIdentifier($ALL))};

entity_class returns [EntityClass.Value entityClass] :
	ENTITY {$entityClass = EntityClass.ENTITY}
	| ARCHITECTURE {$entityClass = EntityClass.ARCHITECTURE}
	| CONFIGURATION {$entityClass = EntityClass.CONFIGURATION}
	| PACKAGE {$entityClass = EntityClass.PACKAGE}
	| PROCEDURE {$entityClass = EntityClass.PROCEDURE}
	| FUNCTION {$entityClass = EntityClass.FUNCTION}
	| TYPE {$entityClass = EntityClass.TYPE}
	| SUBTYPE {$entityClass = EntityClass.SUBTYPE}
	| CONSTANT {$entityClass = EntityClass.CONSTANT}
	| SIGNAL {$entityClass = EntityClass.SIGNAL}
	| VARIABLE {$entityClass = EntityClass.VARIABLE}
	| FILE {$entityClass = EntityClass.FILE}
	| COMPONENT {$entityClass = EntityClass.COMPONENT}
	| LABEL {$entityClass = EntityClass.LABEL}
	| LITERAL {$entityClass = EntityClass.LITERAL}
	| UNITS {$entityClass = EntityClass.UNITS}
	| GROUP {$entityClass = EntityClass.GROUP}
	//| {psl}?=> PROPERTY
	//| {psl}?=> SEQUENCE
	| {ams}?=> (
		NATURE {$entityClass = EntityClass.NATURE}
		| SUBNATURE {$entityClass = EntityClass.SUBNATURE}
		| QUANTITY {$entityClass = EntityClass.QUANTITY}
		| TERMINAL {$entityClass = EntityClass.TERMINAL}
		);

configuration_specification returns [DeclarativeItem specification = NoNode] :
	forToken=FOR component_specification
		binding_indication SEMICOLON
		//({psl}?=> psl_verification_unit_binding_indication)*
	({vhdl2008}?=> END FOR SEMICOLON)?
	{$specification = new ConfigurationSpecification($forToken, $component_specification.specification, $binding_indication.indication)};

instantiation_list returns [Either[Seq[Identifier\], Identifier\] list] :
	identifier_list {$list = Left($identifier_list.list)}
	| OTHERS {$list = Right(toIdentifier($OTHERS))}
	| ALL {$list = Right(toIdentifier($ALL))};

component_specification returns [ComponentSpecification specification] :
	instantiation_list COLON selected_name
	{$specification = new ComponentSpecification($instantiation_list.list, $selected_name.name)};

entity_aspect returns [Option[Either[(SelectedName, Option[Identifier\]), SelectedName\]\] aspect] :
	ENTITY selected_name (LPAREN identifier RPAREN)? {$aspect = Left(($selected_name.name, Option($identifier.ident)))}
	| CONFIGURATION selected_name {$aspect = Right($selected_name.name)}
	| OPEN {$aspect = None};

binding_indication returns [BindingIndication indication] :
	(USE entity_aspect)?
	generic_map_aspect?
	port_map_aspect?
	{$indication = new BindingIndication($entity_aspect.aspect, $generic_map_aspect.aspect, $port_map_aspect.aspect)};

disconnection_specification returns [DeclarativeItem specification = NoNode] :
	DISCONNECT (selected_name_list | id=OTHERS | id=ALL) COLON type_mark AFTER expression SEMICOLON
	{
		val signalList = if (id == null) Left($selected_name_list.list) else Right(toIdentifier(id))
		$specification = new DisconnectionSpecification($DISCONNECT, signalList, $type_mark.mark, $expression.expr)
	};

ams_step_limit_specification returns [DeclarativeItem specification = NoNode] :
	LIMIT (selected_name_list | id=OTHERS | id=ALL ) COLON type_mark WITH expression SEMICOLON
	{
		val signalList = if (id == null) Left($selected_name_list.list) else Right(toIdentifier(id))
		$specification = new StepLimitSpecification($LIMIT, signalList, $type_mark.mark, $expression.expr)
	};

entity_class_entry returns [GroupTemplateDeclaration.EntityClassEntry entry] :
	entity_class BOX?
	{$entry = new GroupTemplateDeclaration.EntityClassEntry($entity_class.entityClass, $BOX != null)};

group_template_declaration returns [DeclarativeItem declaration = NoNode]
@init {val entries = new Buffer[GroupTemplateDeclaration.EntityClassEntry]} :
	GROUP identifier IS LPAREN entry=entity_class_entry {entries += entry} (COMMA entry=entity_class_entry {entries += entry})* RPAREN SEMICOLON
	{$declaration = new GroupTemplateDeclaration($GROUP, $identifier.ident, entries.result)};

group_declaration returns [DeclarativeItem declaration = NoNode] :
	GROUP identifier COLON selected_name LPAREN group_constituent_list RPAREN SEMICOLON
	{$declaration = new GroupDeclaration($GROUP, $identifier.ident, $selected_name.name, $group_constituent_list.list)};

group_constituent returns [Either[Name, Identifier\] constituent] :
	name {$constituent = Left($name.name)}
	| CHARACTER_LITERAL {$constituent = Right(toIdentifier($CHARACTER_LITERAL))};

group_constituent_list returns [Seq[Either[Name, Identifier\]\] list = Nil]
@init {val constituents = new Buffer[Either[Name, Identifier]]} :
	constituent=group_constituent {constituents += constituent} (COMMA constituent=group_constituent {constituents += constituent})*
	{$list = constituents.result};

use_clause returns [UseClause clause] :
	USE selected_name_list SEMICOLON
	{$clause = new UseClause($USE, $selected_name_list.list)};

// B.4 Type Definitions
enumeration_literal returns [Identifier literal = NoIdentifier] :
	identifier {$literal = $identifier.ident}
	| CHARACTER_LITERAL {$literal = toIdentifier($CHARACTER_LITERAL)};

enumeration_type_definition[Identifier name, Position position] returns [DeclarativeItem definition = NoNode]
@init {val literals = new Buffer[Identifier]} :
	LPAREN enumLiteral=enumeration_literal {literals += enumLiteral} (COMMA enumLiteral=enumeration_literal {literals += enumLiteral})* RPAREN
	{$definition = new EnumerationTypeDefinition($position, $name, literals.result)};

numeric_type_definition[Identifier name, Position position] returns [DeclarativeItem definition = NoNode]
@init {val elements = new Buffer[PhysicalTypeDefinition.Element]} :
	range_constraint {$definition = new IntegerOrFloatingPointTypeDefinition($position, $name, $range_constraint.constraint)}
	(
		UNITS
		primaryUnit=identifier SEMICOLON
		(
			unit=identifier EQ physical_literal SEMICOLON 
			{elements += new PhysicalTypeDefinition.Element($unit.ident, $physical_literal.literal)}
		)*
		END UNITS endIdent=identifier?
		{$definition = new PhysicalTypeDefinition($position, $name, $range_constraint.constraint, $primaryUnit.ident, elements.result, $endIdent.ident)}
	)?;

index_subtype_definition returns [SelectedName definition] :
	type_mark RANGE BOX {$definition = $type_mark.mark};

array_type_definition[Identifier name, Position position] returns [DeclarativeItem definition = NoNode]
@init {val definitions = new Buffer[SelectedName]} :
	ARRAY (
		LPAREN indexSubtype=index_subtype_definition {definitions += indexSubtype} (COMMA indexSubtype=index_subtype_definition {definitions += indexSubtype})* RPAREN
		| index_constraint 
	) OF subtype_indication
	{
		val result = definitions.result
		$definition =
			if (result.nonEmpty) new ArrayTypeDefinition($position, $name, Left(result), $subtype_indication.indication)
			else new ArrayTypeDefinition($position, $name, Right($index_constraint.constraint), $subtype_indication.indication)
	};

record_type_definition[Identifier name, Position position] returns [DeclarativeItem definition = NoNode]
@init {val elements = new Buffer[RecordTypeDefinition.Element]} :
	RECORD 
	(
		identifier_list COLON subtype_indication SEMICOLON
		{elements += new RecordTypeDefinition.Element($identifier_list.list, $subtype_indication.indication)}
	)+
	END RECORD identifier?
	{$definition = new RecordTypeDefinition($position, $name, elements.result, $identifier.ident)};

access_type_definition[Identifier name, Position position] returns [DeclarativeItem definition = NoNode] :
	ACCESS subtype_indication 
	{$definition = new AccessTypeDefinition($position, $name, $subtype_indication.indication)};

file_type_definition[Identifier name, Position position] returns [DeclarativeItem definition = NoNode] :
	FILE OF type_mark 
	{$definition = new FileTypeDefinition($position, $name, $type_mark.mark)};

ams_scalar_nature_definition[Identifier name, Position position] returns [ScalarNatureDefinition definition] :
	t1=type_mark ACROSS t2=type_mark THROUGH identifier REFERENCE
	{$definition = new ScalarNatureDefinition(position, $name, $t1.mark, $t2.mark, $identifier.ident)};

ams_array_nature_definition[Identifier name, Position position] returns [ArrayNatureTypeDefinition definition]
@init {val definitions = new Buffer[SelectedName]} :
	ARRAY (
		LPAREN indexSubtype=index_subtype_definition {definitions += indexSubtype} (COMMA indexSubtype=index_subtype_definition {definitions += indexSubtype})* RPAREN
		| index_constraint
	) OF ams_subnature_indication
	{
		val result = definitions.result
		$definition =
			if (result.nonEmpty) new ArrayNatureTypeDefinition($position, $name, Left(result), $ams_subnature_indication.indication)
			else new ArrayNatureTypeDefinition($position, $name, Right($index_constraint.constraint), $ams_subnature_indication.indication)
	};

ams_record_nature_definition[Identifier name, Position position] returns [RecordNatureDefinition definition]
@init {val elements = new Buffer[RecordNatureDefinition.Element]} :
	RECORD
	(
		identifier_list COLON ams_subnature_indication SEMICOLON
		{elements += new RecordNatureDefinition.Element($identifier_list.list, $ams_subnature_indication.indication)}
	)+
	END RECORD identifier?
	{$definition = new RecordNatureDefinition(position, $name, elements.result, $identifier.ident)};

ams_subnature_declaration returns [DeclarativeItem declaration = NoNode] :
	SUBNATURE identifier IS ams_subnature_indication SEMICOLON
	{$declaration = new SubNatureDeclaration($SUBNATURE, $identifier.ident, $ams_subnature_indication.indication)};

ams_subnature_indication returns [SubNatureIndication indication] :
	ams_nature_mark index_constraint? (TOLERANCE toleranceExpr=expression ACROSS acrossExpr=expression THROUGH)?
	{$indication = new SubNatureIndication($ams_nature_mark.mark, $index_constraint.constraint, $toleranceExpr.expr, $acrossExpr.expr)};

ams_nature_mark returns [SelectedName mark] :
	selected_name {$mark = $selected_name.name};

protected_type_declaration[Identifier name, Position position] returns [DeclarativeItem declaration = NoNode]
@init {
	val items = new Buffer[DeclarativeItem]
	val ruleName = "protected type declarative item"
} :
	PROTECTED
		sync[ruleName] (protected_type_declarative_item {items += $protected_type_declarative_item.item} sync[ruleName])*
	END PROTECTED identifier?
	{$declaration = new ProtectedTypeDeclaration($position, $name, items.result, $identifier.ident)};

protected_type_declarative_item returns [DeclarativeItem item = NoNode] :
	subprogram_declaration {$item = $subprogram_declaration.declaration}
	| {vhdl2008}?=> v2008_subprogram_instantiation_declaration {$item = $v2008_subprogram_instantiation_declaration.declaration}
	| attribute_specification {$item = $attribute_specification.specification}
	| use_clause {$item = $use_clause.clause};

protected_type_body[Identifier name, Position position] returns [DeclarativeItem body = NoNode]
@init {
	val items = new Buffer[DeclarativeItem]
	val ruleName = "protected type body declarative item"
} :
	PROTECTED BODY
		sync[ruleName] (protected_type_body_declarative_item {items += $protected_type_body_declarative_item.item} sync[ruleName])*
	END PROTECTED BODY identifier?
	{$body = new ProtectedTypeBodyDeclaration($position, $name, items.result, $identifier.ident)};

protected_type_body_declarative_item returns [DeclarativeItem item = NoNode] :
	subprogram_specification_or_body {$item = $subprogram_specification_or_body.specificationOrBody}
	| {vhdl2008}?=> (
		v2008_subprogram_instantiation_declaration {$item = $v2008_subprogram_instantiation_declaration.declaration}
		| package_declaration {$item = $package_declaration.declaration}
		| package_body {$item = $package_body.body}
		| v2008_package_instantiation_declaration {$item = $v2008_package_instantiation_declaration.declaration}
		)
	| type_declaration {$item = $type_declaration.declaration}
	| subtype_declaration {$item = $subtype_declaration.declaration}
	| constant_declaration {$item = $constant_declaration.declaration}
	| variable_declaration {$item = $variable_declaration.declaration}
	| file_declaration {$item = $file_declaration.declaration}
	| alias_declaration {$item = $alias_declaration.declaration}
	| attribute_declaration {$item = $attribute_declaration.declaration}
	| attribute_specification {$item = $attribute_specification.specification}
	| use_clause {$item = $use_clause.clause}
	| group_template_declaration {$item = $group_template_declaration.declaration}
	| group_declaration {$item = $group_declaration.declaration};

subtype_declaration returns [DeclarativeItem declaration = NoNode] :
	SUBTYPE identifier IS subtype_indication SEMICOLON
	{$declaration = new SubTypeDeclaration($SUBTYPE, $identifier.ident, $subtype_indication.indication)};

subtype_indication returns [SubTypeIndication indication] :
	//TODO {vhdl2008}?=> ((v2008_resolution_indication selected_name)=> v2008_resolution_indication)? selected_name v2008_constraint? ({ams}?=> TOLERANCE expression)?
	//{$indication = new SubTypeIndication($v2008_resolution_indication.resolutionIndication, $selected_name.name, $v2008_constraint.constraint, $expression.expr)}
	n1=selected_name n2=selected_name? constraint? ({ams}?=> TOLERANCE expression)?
	{
		$indication =
			if (n2 != null) new SubTypeIndication($n1.name, $n2.name, $constraint.constraint, $expression.expr)
			else new SubTypeIndication(None, $n1.name, $constraint.constraint, $expression.expr)
	};

v2008_resolution_indication returns [SelectedName indication] :
	selected_name {$indication = $selected_name.name} //TODO
	| LPAREN (v2008_resolution_indication | identifier v2008_resolution_indication (COMMA identifier v2008_resolution_indication)*) RPAREN;

direction returns [Range.Direction.Value direction] :
	TO {$direction = Range.Direction.To}
	| DOWNTO {$direction = Range.Direction.Downto};

range_constraint returns [Range constraint] :
	RANGE range {$constraint = $range.range};

index_constraint returns [Seq[DiscreteRange\] constraint = Nil]
@init {val ranges = new Buffer[DiscreteRange]} :
	LPAREN discreteRange=discrete_range {ranges += discreteRange} (COMMA discreteRange=discrete_range {ranges += discreteRange})* RPAREN
	{$constraint = ranges.result};

range returns [Range range] :
	(simple_expression direction)=> from=simple_expression direction to=simple_expression {$range = new Range(Left($from.expression, $direction.direction, $to.expression))}
	| name {$range = new Range(Right($name.name))};

v2008_constraint returns [Either[Range, Seq[DiscreteRange\]\] constraint] :
	array_constraint
	| v2008_record_constraint //TODO
	| range_constraint {$constraint = Left($range_constraint.constraint)};

constraint returns [Either[Range, Seq[DiscreteRange\]\] constraint] :
	range_constraint {$constraint = Left($range_constraint.constraint)}
	| index_constraint {$constraint = Right($index_constraint.constraint)};

array_constraint :
	index_constraint (array_constraint | v2008_record_constraint)?
	| LPAREN OPEN RPAREN (array_constraint | v2008_record_constraint)?;

v2008_record_constraint :
	(identifier (array_constraint | v2008_record_constraint)) (COMMA (identifier (array_constraint | v2008_record_constraint)));

discrete_range returns [DiscreteRange discreteRange] :
	(subtype_indication (RPAREN | COMMA | GENERATE | LOOP | BAR | ARROW | SEMICOLON))=> subtype_indication {$discreteRange = new DiscreteRange(Right($subtype_indication.indication))}
	| range {$discreteRange = new DiscreteRange(Left($range.range))};

type_mark returns [SelectedName mark] :
	selected_name {$mark = $selected_name.name}; // could be type_name or subtype_name

// B.5 Concurrent Statements
concurrent_statement_list returns [Seq[ConcurrentStatement\] list = Nil]
@init {val statements = new Buffer[ConcurrentStatement]} :
	(concurrent_statement {statements += $concurrent_statement.statement})*
	{$list = statements.result};

concurrent_statement returns [ConcurrentStatement statement = NoNode]
@after {$statement = if ($statement != null) $statement else NoNode} :
	label=label_colon (
			(COMPONENT | (selected_name (GENERIC | PORT) MAP) | ENTITY | CONFIGURATION | BLOCK | IF ({vhdl2008}?=> label_colon)? condition GENERATE | FOR | {vhdl2008}?=> CASE expression GENERATE)=> concurrent_statement_with_label[$label.label] {$statement = $concurrent_statement_with_label.statement}
			| concurrent_statement_optional_label[$label.label] {$statement = $concurrent_statement_optional_label.statement}
			)
		| concurrent_statement_optional_label[$label.label] {$statement = $concurrent_statement_optional_label.statement};

concurrent_statement_with_label[Identifier label] returns [ConcurrentStatement statement = NoNode] :
	component_instantiation_statement[$label] {$statement = $component_instantiation_statement.statement}
	| block_statement[$label] {$statement = $block_statement.statement}
	| generate_statement[$label] {$statement = $generate_statement.statement};

concurrent_statement_optional_label[Identifier label] returns [ConcurrentStatement statement = NoNode] :
	{ams}?=> ams_concurrent_break_statement[$label] {$statement = $ams_concurrent_break_statement.statement}
	| ({ams}?=> ((simple_expression AMS_ASSIGN) | IF | PROCEDURAL | CASE | NULL))=> ams_simultaneous_statement[$label] {$statement = $ams_simultaneous_statement.statement}
	| POSTPONED? (
		process_statement[$label, $POSTPONED != null] {$statement = $process_statement.statement}
		| concurrent_assertion_statement[$label, $POSTPONED != null] {$statement = $concurrent_assertion_statement.statement}
		| (concurrent_procedure_call_statement[null, true])=> concurrent_procedure_call_statement[$label, $POSTPONED != null] {$statement = $concurrent_procedure_call_statement.statement}
		| concurrent_signal_assignment_statement[$label, $POSTPONED != null] {$statement = $concurrent_signal_assignment_statement.statement}
		);
	//| {psl}?=> PSL_PSL_Directive

generic_map_aspect returns [AssociationList aspect] :
	GENERIC MAP LPAREN association_list RPAREN {$aspect = $association_list.list};

port_map_aspect returns [AssociationList aspect] :
	PORT MAP LPAREN association_list RPAREN {$aspect = $association_list.list};

block_statement[Identifier label] returns [ConcurrentStatement statement = NoNode]
@init {
	val items = new Buffer[DeclarativeItem]
	val ruleName = "block declarative item"
} :
	blockToken=BLOCK (LPAREN expression RPAREN)? IS?
		(generic_clause (generic_map_aspect SEMICOLON)?)?
		(port_clause (port_map_aspect SEMICOLON)?)?
		sync[ruleName] (block_declarative_item {items += $block_declarative_item.item} sync[ruleName])*
	BEGIN
		concurrent_statement_list
	END BLOCK identifier? SEMICOLON
	{
		$statement = new BlockStatement($blockToken, $label, $expression.expr, $generic_clause.clause, $generic_map_aspect.aspect, $port_clause.clause, $port_map_aspect.aspect,
			items.result, $concurrent_statement_list.list, $identifier.ident)
	};

block_declarative_item returns [DeclarativeItem item = NoNode] :
	subprogram_specification_or_body {$item = $subprogram_specification_or_body.specificationOrBody}
	| {vhdl2008}?=> (
		v2008_subprogram_instantiation_declaration {$item = $v2008_subprogram_instantiation_declaration.declaration}
		| package_declaration {$item = $package_declaration.declaration}
		| package_body {$item = $package_body.body}
		| v2008_package_instantiation_declaration {$item = $v2008_package_instantiation_declaration.declaration}
		)
	| type_declaration {$item = $type_declaration.declaration}
	| subtype_declaration {$item = $subtype_declaration.declaration}
	| constant_declaration {$item = $constant_declaration.declaration}
	| signal_declaration {$item = $signal_declaration.declaration}
	| variable_declaration {$item = $variable_declaration.declaration}
	| file_declaration {$item = $file_declaration.declaration}
	| alias_declaration {$item = $alias_declaration.declaration}
	| component_declaration {$item = $component_declaration.declaration}
	| attribute_declaration {$item = $attribute_declaration.declaration}
	| attribute_specification {$item = $attribute_specification.specification}
	| configuration_specification {$item = $configuration_specification.specification}
	| disconnection_specification {$item = $disconnection_specification.specification}
	| use_clause {$item = $use_clause.clause}
	| group_template_declaration {$item = $group_template_declaration.declaration}
	| group_declaration {$item = $group_declaration.declaration}
	/*| {psl}?=> (
		PSL_Property_Declaration
		| PSL_Sequence_Declaration
		| PSL_Clock_Declaration
		)*/
	| {ams}?=> (
		ams_step_limit_specification {$item = $ams_step_limit_specification.specification}
		| ams_nature_declaration {$item = $ams_nature_declaration.declaration}
		| ams_subnature_declaration {$item = $ams_subnature_declaration.declaration}
		| ams_quantity_declaration {$item = $ams_quantity_declaration.declaration}
		| ams_terminal_declaration {$item = $ams_terminal_declaration.declaration}
		);

process_statement[Identifier label, Boolean postponed] returns [ConcurrentStatement statement = NoNode]
@init {
	val items = new Buffer[DeclarativeItem]
	val ruleName = "process declarative item"
} :
	processToken=PROCESS (LPAREN name_list RPAREN)? IS?
		sync[ruleName] (process_declarative_item {items += $process_declarative_item.item} sync[ruleName])*
	BEGIN
		saveFollowSet sequence_of_statements
	END POSTPONED? PROCESS identifier? SEMICOLON
	{$statement = new ProcessStatement($processToken, $label, $postponed, $name_list.list, items.result, $sequence_of_statements.statements, $identifier.ident)};

process_declarative_item returns [DeclarativeItem item = NoNode] :
	subprogram_specification_or_body {$item = $subprogram_specification_or_body.specificationOrBody}
	| {vhdl2008}?=> (
		v2008_subprogram_instantiation_declaration {$item = $v2008_subprogram_instantiation_declaration.declaration}
		| package_declaration {$item = $package_declaration.declaration}
		| package_body {$item = $package_body.body}
		| v2008_package_instantiation_declaration {$item = $v2008_package_instantiation_declaration.declaration}
		)
	| type_declaration {$item = $type_declaration.declaration}
	| subtype_declaration {$item = $subtype_declaration.declaration}
	| constant_declaration {$item = $constant_declaration.declaration}
	| variable_declaration {$item = $variable_declaration.declaration}
	| file_declaration {$item = $file_declaration.declaration}
	| alias_declaration {$item = $alias_declaration.declaration}
	| attribute_declaration {$item = $attribute_declaration.declaration}
	| attribute_specification {$item = $attribute_specification.specification}
	| use_clause {$item = $use_clause.clause}
	| group_template_declaration {$item = $group_template_declaration.declaration}
	| group_declaration {$item = $group_declaration.declaration};

concurrent_procedure_call_statement[Identifier label, Boolean postponed] returns [ConcurrentStatement statement = NoNode] :
	selected_name (LPAREN association_list RPAREN)? SEMICOLON
	{$statement = new ConcurrentProcedureCallStatement($label, $postponed, $selected_name.name, $association_list.list)};

concurrent_assertion_statement[Identifier label, Boolean postponed] returns [ConcurrentStatement statement = NoNode] :
	ASSERT condition (REPORT reportExpression=expression)? (SEVERITY severityExpression=expression)? SEMICOLON
	{$statement = new ConcurrentAssertionStatement($ASSERT, $label, $postponed, $condition.condition, $reportExpression.expr, $severityExpression.expr)};

concurrent_signal_assignment_statement[Identifier label, Boolean postponed] returns [ConcurrentStatement statement = NoNode] :
	concurrent_conditional_signal_assignment[$label, $postponed] {$statement = $concurrent_conditional_signal_assignment.assignment}
	| concurrent_selected_signal_assignment[$label, $postponed] {$statement = $concurrent_selected_signal_assignment.assignment};

concurrent_conditional_signal_assignment[Identifier label, Boolean postponed] returns [ConcurrentStatement assignment = NoNode]
@init {val waveforms = new Buffer[ConcurrentConditionalSignalAssignment.When]} :
	target LEQ GUARDED? delay_mechanism?
		conditional_waveforms[waveforms] SEMICOLON
	{$assignment = new ConcurrentConditionalSignalAssignment($LEQ, $label, $postponed, $target.target, $GUARDED != null, $delay_mechanism.mechanism, waveforms.result.reverse)};

conditional_waveforms[Buffer[ConcurrentConditionalSignalAssignment.When\] waveforms] :
	waveform (WHEN condition (ELSE conditional_waveforms[waveforms])?)? {waveforms += new ConcurrentConditionalSignalAssignment.When($waveform.waveform, $condition.condition)};

concurrent_selected_signal_assignment[Identifier label, Boolean postponed] returns [ConcurrentStatement assignment = NoNode] :
	WITH expression SELECT ({vhdl2008}?=> QMARK)?
		target LEQ GUARDED? delay_mechanism? selected_waveforms SEMICOLON
		{$assignment = new ConcurrentSelectedSignalAssignment($WITH, $label, $postponed, $expression.expr, $QMARK != null, $target.target, $GUARDED != null, $delay_mechanism.mechanism, $selected_waveforms.waveforms)};

selected_waveform returns [ConcurrentSelectedSignalAssignment.When selectedWaveform] :
	waveform WHEN choices {$selectedWaveform = new ConcurrentSelectedSignalAssignment.When($waveform.waveform, $choices.choices)};
	
selected_waveforms returns [Seq[ConcurrentSelectedSignalAssignment.When\] waveforms = Nil]
@init {val buffer = new Buffer[ConcurrentSelectedSignalAssignment.When]} :
	selectedWaveform=selected_waveform {buffer += selectedWaveform} (COMMA selectedWaveform=selected_waveform {buffer += selectedWaveform})*
	{$waveforms = buffer.result};

target returns [Target target] :
	name {$target = new Target(Left($name.name))}
	| aggregate {$target = new Target(Right($aggregate.aggregate))};

component_instantiation_statement[Identifier label] returns [ConcurrentStatement statement = NoNode]
@init {
	var componentType: ComponentInstantiationStatement.ComponentType.Value = null
	val position = toPosition(input.LT(1))
} :
	(
		COMPONENT? componentName=selected_name {componentType = ComponentInstantiationStatement.ComponentType.COMPONENT}
		| ENTITY componentName=selected_name (LPAREN identifier RPAREN)? {componentType = ComponentInstantiationStatement.ComponentType.ENTITY}
		| CONFIGURATION componentName=selected_name {componentType = ComponentInstantiationStatement.ComponentType.CONFIGURATION}
	)
	generic_map_aspect?
	port_map_aspect? SEMICOLON
	{$statement = new ComponentInstantiationStatement(position, $label, componentType, $componentName.name, $identifier.ident, $generic_map_aspect.aspect, $port_map_aspect.aspect)};

generate_statement[Identifier label] returns [ConcurrentStatement statement = NoNode] :
	for_generate_statement[$label] {$statement = $for_generate_statement.statement}
	| if_generate_statement[$label] {$statement = $if_generate_statement.statement}
	| {vhdl2008}?=> v2008_case_generate_statement[$label] {$statement = $v2008_case_generate_statement.statement};

for_generate_statement[Identifier label] returns [ConcurrentStatement statement = NoNode] :
	FOR loopIdentifier=identifier IN discrete_range GENERATE
		body=generate_statement_body
	END GENERATE endIdent=identifier? SEMICOLON
	{$statement = new ForGenerateStatement($FOR, $label, $loopIdentifier.ident, $discrete_range.discreteRange, $body.items, $body.statements, $body.endLabel, $endIdent.ident)};

if_generate_statement[Identifier label] returns [ConcurrentStatement statement = NoNode]
@init {
	val ifList = new Buffer[IfGenerateStatement.IfThenPart]
	var elsePart: Option[IfGenerateStatement.IfThenPart] = None
} :
	IF ({vhdl2008}?=> ifLabel=label_colon)? ifCondition=condition GENERATE
		ifBody=generate_statement_body
		{ifList += new IfGenerateStatement.IfThenPart($ifLabel.label, $ifCondition.condition, $ifBody.items, $ifBody.statements, $ifBody.endLabel)}
	({vhdl2008}?=> (ELSIF elseifLabel=label_colon? elsifCondition=condition GENERATE
		elseIfBody=generate_statement_body
		{ifList += new IfGenerateStatement.IfThenPart($elseifLabel.label, $elsifCondition.condition, $elseIfBody.items, $elseIfBody.statements, $elseIfBody.endLabel)}
	)*
	(ELSE elseLabel=label_colon? GENERATE
		elseBody=generate_statement_body
		{elsePart = Option(new IfGenerateStatement.IfThenPart($elseLabel.label, NoExpression, $elseBody.items, $elseBody.statements, $elseBody.endLabel))}
	)?)?
	END GENERATE identifier? SEMICOLON
	{$statement = new IfGenerateStatement($IF, $label, ifList.result, elsePart, $identifier.ident)};

v2008_case_generate_statement[Identifier label] returns [ConcurrentStatement statement = NoNode]
@init {val alternatives = new Buffer[CaseGenerateStatement.When]} :
	CASE expression GENERATE
		(WHEN label_colon? choices ARROW body=generate_statement_body {alternatives += new CaseGenerateStatement.When($label_colon.label, $choices.choices, $body.items, $body.statements, $body.endLabel)})+
	END GENERATE identifier? SEMICOLON
	{$statement = new CaseGenerateStatement($CASE, $label, $expression.expr, alternatives.result, $identifier.ident)};

generate_statement_body returns [Seq[DeclarativeItem\] items = Nil, Seq[ConcurrentStatement\] statements = Nil, Identifier endLabel]
@init {
	val buffer = new Buffer[DeclarativeItem]
	val ruleName = "block declarative item"
} :
	(
		(block_declarative_item {buffer += $block_declarative_item.item} sync[ruleName])*
		BEGIN
	)?
		concurrent_statement_list
	({vhdl2008}?=> END identifier? SEMICOLON)?
	{
		$items = buffer.result
		$statements = $concurrent_statement_list.list
		$endLabel = $identifier.ident
	};

ams_concurrent_break_statement[Identifier label] returns [ConcurrentStatement statement = NoNode] :
	BREAK ams_break_element_list? (ON name_list)? (WHEN expression)? SEMICOLON
	{$statement = new ConcurrentBreakStatement($BREAK, $label, $ams_break_element_list.list, $name_list.list, $expression.expr)};

// E.6 Simultaneous Statements
ams_simultaneous_statement[Identifier label] returns [SimultaneousStatement statement = NoNode] :
	ams_simple_simultaneous_statement[$label] {$statement = $ams_simple_simultaneous_statement.statement}
	| ams_simultaneous_if_statement[$label] {$statement = $ams_simultaneous_if_statement.statement}
	| ams_simultaneous_procedural_statement[$label] {$statement = $ams_simultaneous_procedural_statement.statement}
	| ams_simultaneous_case_statement[$label] {$statement = $ams_simultaneous_case_statement.statement}
	| ams_simultaneous_null_statement[$label] {$statement = $ams_simultaneous_null_statement.statement};

ams_simultaneous_statement_list returns [Seq[SimultaneousStatement\] list = Nil]
@init {val statements = new Buffer[SimultaneousStatement]} :
	(label_colon? ams_simultaneous_statement[$label_colon.label] {statements += $ams_simultaneous_statement.statement})*
	{$list = statements.result};

ams_simple_simultaneous_statement[Identifier label] returns [SimultaneousStatement statement = NoNode] :
	left=simple_expression AMS_ASSIGN right=simple_expression (TOLERANCE toleranceExpression=expression)? SEMICOLON
	{$statement = new SimpleSimultaneousStatement($label, $left.expression, $right.expression, $toleranceExpression.expr)};

ams_simultaneous_if_statement[Identifier label] returns [SimultaneousStatement statement = NoNode]
@init {val ifList = new Buffer[SimultaneousIfStatement.IfUsePart]} :
	IF ifCondition=condition USE
		ifStatements=ams_simultaneous_statement_list {ifList += new SimultaneousIfStatement.IfUsePart($ifCondition.condition, $ifStatements.list)}
	(ELSIF elsifCondition=condition USE
		elseIfStatements=ams_simultaneous_statement_list
		{ifList += new SimultaneousIfStatement.IfUsePart($elsifCondition.condition, $elseIfStatements.list)}
	)*
	(ELSE
		elseStatements=ams_simultaneous_statement_list)?
	END USE identifier? SEMICOLON
	{$statement = new SimultaneousIfStatement($IF, $label, ifList.result, $elseStatements.list, $identifier.ident)};

ams_simultaneous_case_statement[Identifier label] returns [SimultaneousStatement statement = NoNode]
@init {val alternatives = new Buffer[SimultaneousCaseStatement.When]} :
	caseToken=CASE expression USE
		(WHEN choices ARROW ams_simultaneous_statement_list {alternatives += new SimultaneousCaseStatement.When($choices.choices, $ams_simultaneous_statement_list.list)})+
	END CASE identifier? SEMICOLON
	{$statement = new SimultaneousCaseStatement($caseToken, $label, $expression.expr, alternatives.result, $identifier.ident)};

ams_simultaneous_procedural_statement[Identifier label] returns [SimultaneousStatement statement = NoNode]
@init {
	val items = new Buffer[DeclarativeItem]
	val ruleName = "simultaneous procedural declarative item"
} :
	proceduralToken=PROCEDURAL IS?
		sync[ruleName] (ams_simultaneous_procedural_declarative_item {items += $ams_simultaneous_procedural_declarative_item.item} sync[ruleName])*
	BEGIN
		saveFollowSet sequence_of_statements
	END PROCEDURAL identifier? SEMICOLON
	{$statement = new SimultaneousProceduralStatement($proceduralToken, $label, items.result, $sequence_of_statements.statements, $identifier.ident)};

ams_simultaneous_procedural_declarative_item returns [DeclarativeItem item = NoNode] :
	subprogram_specification_or_body {$item = $subprogram_specification_or_body.specificationOrBody}
	| type_declaration {$item = $type_declaration.declaration}
	| subtype_declaration {$item = $subtype_declaration.declaration}
	| constant_declaration {$item = $constant_declaration.declaration}
	| variable_declaration {$item = $variable_declaration.declaration}
	| alias_declaration {$item = $alias_declaration.declaration}
	| attribute_declaration {$item = $attribute_declaration.declaration}
	| attribute_specification {$item = $attribute_specification.specification}
	| use_clause {$item = $use_clause.clause}
	| group_template_declaration {$item = $group_template_declaration.declaration}
	| group_declaration {$item = $group_declaration.declaration};

ams_simultaneous_null_statement[Identifier label] returns [SimultaneousStatement statement = NoNode] :
	NULL SEMICOLON
	{$statement = new SimultaneousNullStatement($NULL, $label)};

// B.6 Sequential Statements
sequence_of_statements returns [Seq[SequentialStatement\] statements = Nil]
@init {
	val buffer = new Buffer[SequentialStatement]
	val ruleName = "sequential statement"
	val set = followSet
} :
	{syncAndAddError(ruleName, set)} (sequential_statement {buffer += $sequential_statement.statement} {syncAndAddError(ruleName, set)})*
	{$statements = buffer.result};

sequential_statement returns [SequentialStatement statement = NoNode]
@after {$statement = if ($statement != null) $statement else NoNode} :
	label=label_colon? (
	wait_statement[$label.label] {$statement = $wait_statement.statement}
	| assertion_statement[$label.label] {$statement = $assertion_statement.statement}
	| report_statement[$label.label] {$statement = $report_statement.statement}
	| (procedure_call_statement[null])=> procedure_call_statement[$label.label] {$statement = $procedure_call_statement.statement}
	| assignment_statement[$label.label] {$statement = $assignment_statement.statement} //signal or variable assignment statement
	| if_statement[$label.label] {$statement = $if_statement.statement}
	| case_statement[$label.label] {$statement = $case_statement.statement}
	| loop_statement[$label.label] {$statement = $loop_statement.statement}
	| next_statement[$label.label] {$statement = $next_statement.statement}
	| exit_statement[$label.label] {$statement = $exit_statement.statement}
	| return_statement[$label.label] {$statement = $return_statement.statement}
	| null_statement[$label.label] {$statement = $null_statement.statement}
	| {ams}?=> ams_break_statement[$label.label] {$statement = $ams_break_statement.statement}
	);

wait_statement[Identifier label] returns [SequentialStatement statement = NoNode] :
	WAIT (ON name_list)? (UNTIL condition)? (FOR expression)? SEMICOLON
	{$statement = new WaitStatement($WAIT, $label, $name_list.list, $condition.condition, $expression.expr)};

assertion_statement[Identifier label] returns [SequentialStatement statement = NoNode] :
	ASSERT condition (REPORT reportExpression=expression)? (SEVERITY severityExpression=expression)? SEMICOLON
	{$statement = new AssertionStatement($ASSERT, $label, $condition.condition, $reportExpression.expr, $severityExpression.expr)};

report_statement[Identifier label] returns [SequentialStatement statement = NoNode] :
	REPORT reportExpression=expression (SEVERITY severityExpression=expression)? SEMICOLON
	{$statement = new ReportStatement($REPORT, $label, $reportExpression.expr, $severityExpression.expr)};

force_mode returns [InterfaceList.Mode.Value mode] :
	IN {$mode = InterfaceList.Mode.IN}
	| OUT {$mode = InterfaceList.Mode.OUT};

v2008_conditional_expressions[Buffer[ConditionalVariableAssignment.When\] expressions] :
	expression (WHEN condition (ELSE v2008_conditional_expressions[$expressions])?)? {$expressions += new ConditionalVariableAssignment.When($expression.expr, $condition.condition)};

v2008_selected_expression returns [SelectedVariableAssignment.When selectedExpression] :
	expression WHEN choices {$selectedExpression = new SelectedVariableAssignment.When($expression.expr, $choices.choices)};

v2008_selected_expressions returns [Seq[SelectedVariableAssignment.When\] expressions = Nil]
@init {val buffer = new Buffer[SelectedVariableAssignment.When]} :
	selectedExpression=v2008_selected_expression {buffer += selectedExpression} (COMMA selectedExpression=v2008_selected_expression {buffer += selectedExpression})*
	{$expressions = buffer.result};

assignment_statement[Identifier label] returns [SequentialStatement statement = NoNode] :
	{vhdl2008}?=> (
		v2008_conditional_assignment[$label] {$statement = $v2008_conditional_assignment.assignment}
		| v2008_selected_assignment[$label] {$statement = $v2008_selected_assignment.assignment}
		)
	| simple_assignment[$label] {$statement = $simple_assignment.assignment};

simple_assignment[Identifier label] returns [SequentialStatement assignment = NoNode] :
	target (
		VAR_ASSIGN expression {$assignment = new SimpleVariableAssignmentStatement($VAR_ASSIGN, $label, $target.target, $expression.expr)}
		| LEQ delay_mechanism? waveform {$assignment = new SimpleWaveformAssignmentStatement($LEQ, $label, $target.target, $delay_mechanism.mechanism, $waveform.waveform)}
	) SEMICOLON;

v2008_conditional_assignment[Identifier label] returns [SequentialStatement assignment = NoNode]
@init {
	val waveforms = new Buffer[ConcurrentConditionalSignalAssignment.When]
	val expressions = new Buffer[ConditionalVariableAssignment.When]
} :
	target (
		LEQ (
			RELEASE forceMode=force_mode? {$assignment = new SimpleReleaseAssignment($LEQ, $label, $target.target, $forceMode.mode)}
			| delay_mechanism? conditional_waveforms[waveforms] {$assignment = new ConditionalWaveformAssignment($LEQ, $label, $target.target, $delay_mechanism.mechanism, waveforms.result.reverse)}
			| FORCE forceMode=force_mode? v2008_conditional_expressions[expressions] {$assignment = new ConditionalForceAssignment($LEQ, $label, $target.target, $forceMode.mode, expressions.result.reverse)}
			)
		| VAR_ASSIGN v2008_conditional_expressions[expressions] {$assignment = new ConditionalVariableAssignment($VAR_ASSIGN, $label, $target.target, expressions.result.reverse)}
	) SEMICOLON;

v2008_selected_assignment[Identifier label] returns [SequentialStatement assignment = NoNode] :
	WITH expression SELECT QMARK? target (
		LEQ (
			delay_mechanism? selected_waveforms {$assignment = new SelectedWaveformAssignment($WITH, $label, $expression.expr, $QMARK != null, $target.target, $delay_mechanism.mechanism, $selected_waveforms.waveforms)}
			| FORCE force_mode? selectedExpression=v2008_selected_expressions {$assignment = new SelectedForceAssignment($WITH, $label, $expression.expr, $QMARK != null, $target.target, $force_mode.mode, $selectedExpression.expressions)}
			)
		| VAR_ASSIGN selectedExpression=v2008_selected_expressions {$assignment = new SelectedVariableAssignment($WITH, $label, $expression.expr, $QMARK != null, $target.target, $selectedExpression.expressions)}
	) SEMICOLON;

delay_mechanism returns [DelayMechanism mechanism] :
	TRANSPORT {$mechanism = new DelayMechanism(DelayMechanism.DelayType.TRANSPORT, None)}
	| (REJECT expression)? INERTIAL {$mechanism = new DelayMechanism(DelayMechanism.DelayType.INERTIAL, $expression.expr)};

waveform_element returns [Waveform.Element element] :
	valueExpression=expression (AFTER timeExpression=expression)?
	{$element = new Waveform.Element($valueExpression.expr, $timeExpression.expr)};

waveform returns [Waveform waveform]
@init {
	val elements = new Buffer[Waveform.Element]
	val position = toPosition(input.LT(1))
} :
	element=waveform_element {elements += element} (COMMA element=waveform_element {elements += element})* {$waveform = new Waveform(position, elements.result)}
	| UNAFFECTED {$waveform = new Waveform(position, Nil)};

procedure_call_statement[Identifier label] returns [SequentialStatement statement = NoNode] :
	selected_name (LPAREN association_list RPAREN)? SEMICOLON
	{$statement = new ProcedureCallStatement($label, $selected_name.name, $association_list.list)};

if_statement[Identifier label] returns [SequentialStatement statement = NoNode]
@init {
	val ifList = new Buffer[IfStatement.IfThenPart]
} :
	ifToken=IF ifCondition=condition THEN
		saveFollowSet ifStatements=sequence_of_statements {ifList += new IfStatement.IfThenPart($ifCondition.condition, $ifStatements.statements)}
	(ELSIF elseIfCondition=condition THEN
		saveFollowSet elseIfStatements=sequence_of_statements
		{ifList += new IfStatement.IfThenPart($elseIfCondition.condition, $elseIfStatements.statements)}
	)*
	(ELSE
		saveFollowSet elseStatements=sequence_of_statements)?
	END IF identifier? SEMICOLON 
	{$statement = new IfStatement($ifToken, $label, ifList.result, $elseStatements.statements, $identifier.ident)};

case_statement[Identifier label] returns [SequentialStatement statement = NoNode]
@init {val alternatives = new Buffer[CaseStatement.When]} :
	caseToken=CASE ({vhdl2008}?=> qmarkToken=QMARK)? expression IS
		(WHEN choices ARROW saveFollowSet sequence_of_statements {alternatives += new CaseStatement.When($choices.choices, $sequence_of_statements.statements)})+
	END CASE ({vhdl2008}?=> QMARK)? identifier? SEMICOLON
	{$statement = new CaseStatement($caseToken, $label, qmarkToken != null, $expression.expr, alternatives.result, $identifier.ident)};

iteration_scheme returns [Either[Expression, (Identifier, DiscreteRange)\] scheme] :
	WHILE condition {$scheme = Left($condition.condition)}
	| FOR identifier IN discrete_range {$scheme = Right(($identifier.ident, $discrete_range.discreteRange))};

loop_statement[Identifier label] returns [SequentialStatement statement = NoNode]
@init {val position = toPosition(input.LT(1))} :
	iteration_scheme? LOOP
		saveFollowSet sequence_of_statements
	END LOOP identifier? SEMICOLON
	{
		$statement = Option($iteration_scheme.scheme) match {
			case Some(scheme) => scheme match {
				case Left(condition) => new WhileStatement(position, $label, condition, $sequence_of_statements.statements, $identifier.ident)
				case Right((identifier, discreteRange)) => new ForStatement(position, $label, identifier, discreteRange, $sequence_of_statements.statements, $identifier.ident)
			}
			case None => new LoopStatement(position, $label, $sequence_of_statements.statements, $identifier.ident)
		}
	};

next_statement[Identifier label] returns [SequentialStatement statement = NoNode] :
	NEXT identifier? (WHEN condition)? SEMICOLON 
	{$statement = new NextStatement($NEXT, $label, $identifier.ident, $condition.condition)};

exit_statement[Identifier label] returns [SequentialStatement statement = NoNode] :
	EXIT identifier? (WHEN condition)? SEMICOLON 
	{$statement = new ExitStatement($EXIT, $label, $identifier.ident, $condition.condition)};

return_statement[Identifier label] returns [SequentialStatement statement = NoNode] :
	RETURN expression? SEMICOLON 
	{$statement = new ReturnStatement($RETURN, $label, $expression.expr)};

null_statement[Identifier label] returns [SequentialStatement statement = NoNode] :
	NULL SEMICOLON
	{$statement = new NullStatement($NULL, $label)};

ams_break_statement[Identifier label] returns [SequentialStatement statement = NoNode] :
	BREAK ams_break_element_list? (WHEN expression)? SEMICOLON
	{$statement = new AMSBreakStatement($BREAK, $label, $ams_break_element_list.list, $expression.expr)};

ams_break_element_list returns [Seq[BreakElement\] list = Nil]
@init {val elements = new Buffer[BreakElement]} :
	element=ams_break_element {elements += element} (COMMA element=ams_break_element {elements += element})*
	{$list = elements.result};

ams_break_element returns [BreakElement element] :
	(FOR forName=name USE)? useName=name ARROW expr=expression
	{$element = new BreakElement($forName.name, $useName.name, $expr.expr)};

// B.7 Interfaces and Associations
interface_element_generic returns [InterfaceList.AbstractInterfaceElement element = InterfaceList.NoElement] :
	interface_constant_declaration {$element = $interface_constant_declaration.declaration}
	| {vhdl2008}?=> (
		| v2008_interface_type_declaration {$element = $v2008_interface_type_declaration.declaration}
		| v2008_interface_subprogram_declaration {$element = $v2008_interface_subprogram_declaration.declaration}
		| v2008_interface_package_declaration {$element = $v2008_interface_package_declaration.declaration}
		);

interface_element_port returns [InterfaceList.AbstractInterfaceElement element = InterfaceList.NoElement] :
	interface_signal_declaration_port {$element = $interface_signal_declaration_port.declaration}
	| {ams}?=> (
		ams_interface_terminal_declaration {$element = $ams_interface_terminal_declaration.declaration}
		| ams_interface_quantity_declaration {$element = $ams_interface_quantity_declaration.declaration}
		);

interface_element_procedure returns [InterfaceList.AbstractInterfaceElement element = InterfaceList.NoElement] :
	interface_variable_or_constant_declaration {$element = $interface_variable_or_constant_declaration.declaration}
	| interface_signal_declaration_procedure {$element = $interface_signal_declaration_procedure.declaration}
	| interface_file_declaration {$element = $interface_file_declaration.declaration}
	| {ams}?=> (
		ams_interface_terminal_declaration {$element = $ams_interface_terminal_declaration.declaration}
		| ams_interface_quantity_declaration {$element = $ams_interface_quantity_declaration.declaration}
		);

interface_element_function returns [InterfaceList.AbstractInterfaceElement element = InterfaceList.NoElement] :
	interface_constant_declaration {$element = $interface_constant_declaration.declaration}
	| interface_signal_declaration_function {$element = $interface_signal_declaration_function.declaration}
	| interface_file_declaration {$element = $interface_file_declaration.declaration}
	| {ams}?=> (
		ams_interface_terminal_declaration {$element = $ams_interface_terminal_declaration.declaration}
		| ams_interface_quantity_declaration {$element = $ams_interface_quantity_declaration.declaration}
		);

parameter_interface_list_procedure returns [Seq[InterfaceList.AbstractInterfaceElement\] list = Nil]
@init {val elements = new Buffer[InterfaceList.AbstractInterfaceElement]} :
	element=interface_element_procedure {elements += element} (SEMICOLON element=interface_element_procedure {elements += element})*
	{$list = elements.result};

parameter_interface_list_function returns [Seq[InterfaceList.AbstractInterfaceElement\] list = Nil]
@init {val elements = new Buffer[InterfaceList.AbstractInterfaceElement]} :
	element=interface_element_function {elements += element} (SEMICOLON element=interface_element_function {elements += element})*
	{$list = elements.result};

interface_variable_or_constant_declaration returns [InterfaceList.AbstractInterfaceElement declaration = InterfaceList.NoElement] :
	VARIABLE identifier_list COLON interface_mode? subtype_indication (VAR_ASSIGN expression)?
		{$declaration = new InterfaceList.InterfaceVariableDeclaration($identifier_list.list, $interface_mode.mode, $subtype_indication.indication, $expression.expr)}
	| CONSTANT identifier_list COLON IN? subtype_indication (VAR_ASSIGN expression)? 
		{$declaration = new InterfaceList.InterfaceConstantDeclaration($identifier_list.list, $subtype_indication.indication, $expression.expr)}
	| identifier_list COLON interface_mode? subtype_indication (VAR_ASSIGN expression)? 
		{
			$declaration =
				if ($interface_mode.mode == InterfaceList.Mode.OUT || $interface_mode.mode == InterfaceList.Mode.IN)
					new InterfaceList.InterfaceVariableDeclaration($identifier_list.list, $interface_mode.mode, $subtype_indication.indication, $expression.expr)
				else new InterfaceList.InterfaceConstantDeclaration($identifier_list.list, $subtype_indication.indication, $expression.expr)
		};

interface_constant_declaration returns [InterfaceList.AbstractInterfaceElement declaration = InterfaceList.NoElement] :
	CONSTANT? identifier_list COLON IN? subtype_indication (VAR_ASSIGN expression)?
	{$declaration = new InterfaceList.InterfaceConstantDeclaration($identifier_list.list, $subtype_indication.indication, $expression.expr)};

interface_signal_declaration_port returns [InterfaceList.AbstractInterfaceElement declaration = InterfaceList.NoElement] :
	SIGNAL? identifier_list COLON interface_mode? subtype_indication BUS? (VAR_ASSIGN expression)?
	{$declaration = new InterfaceList.InterfaceSignalDeclaration($identifier_list.list, $interface_mode.mode, $subtype_indication.indication, $BUS != null, $expression.expr)};

interface_signal_declaration_procedure returns [InterfaceList.AbstractInterfaceElement declaration = InterfaceList.NoElement] :
	SIGNAL identifier_list COLON interface_mode? subtype_indication BUS? (VAR_ASSIGN expression)?
	{$declaration = new InterfaceList.InterfaceSignalDeclaration($identifier_list.list, $interface_mode.mode, $subtype_indication.indication, $BUS != null, $expression.expr)};

interface_signal_declaration_function returns [InterfaceList.AbstractInterfaceElement declaration = InterfaceList.NoElement] :
	SIGNAL identifier_list COLON IN? subtype_indication BUS? (VAR_ASSIGN expression)?
	{$declaration = new InterfaceList.InterfaceSignalDeclaration($identifier_list.list, InterfaceList.Mode.IN, $subtype_indication.indication, $BUS != null, $expression.expr)};

interface_mode returns [InterfaceList.Mode.Value mode] :
	IN {$mode = InterfaceList.Mode.IN}
	| OUT {$mode = InterfaceList.Mode.OUT}
	| INOUT {$mode = InterfaceList.Mode.INOUT}
	| BUFFER {$mode = InterfaceList.Mode.BUFFER}
	| LINKAGE {$mode = InterfaceList.Mode.LINKAGE};

interface_file_declaration returns [InterfaceList.AbstractInterfaceElement declaration = InterfaceList.NoElement] :
	FILE identifier_list COLON subtype_indication
	{$declaration = new InterfaceList.InterfaceFileDeclaration($identifier_list.list, $subtype_indication.indication)};

ams_interface_terminal_declaration returns [InterfaceList.AbstractInterfaceElement declaration = InterfaceList.NoElement] :
	TERMINAL identifier_list COLON ams_subnature_indication
	{$declaration = new InterfaceList.InterfaceTerminalDeclaration($identifier_list.list, $ams_subnature_indication.indication)};

ams_interface_quantity_declaration returns [InterfaceList.AbstractInterfaceElement declaration = InterfaceList.NoElement] :
	QUANTITY identifier_list COLON (IN | out=OUT)? subtype_indication (VAR_ASSIGN expression)?
	{
		val mode = if ($out != null) InterfaceList.Mode.OUT else InterfaceList.Mode.IN
		$declaration = new InterfaceList.InterfaceQuantityDeclaration($identifier_list.list, mode, $subtype_indication.indication, $expression.expr)
	};

v2008_interface_type_declaration returns [InterfaceList.AbstractInterfaceElement declaration = InterfaceList.NoElement] :
	TYPE identifier {$declaration = new InterfaceList.InterfaceTypeDeclaration($identifier.ident)};

v2008_interface_subprogram_default returns [Option[SelectedName\] default] : 
	IS (selected_name {$default = Option($selected_name.name)} | BOX {$default = None});

v2008_interface_subprogram_declaration returns [InterfaceList.AbstractInterfaceElement declaration = InterfaceList.NoElement] :
	PROCEDURE identifier
		(PARAMETER? LPAREN parameter_interface_list_procedure RPAREN)? v2008_interface_subprogram_default?
		{$declaration = new InterfaceList.InterfaceProcedureDeclaration($identifier.ident, $parameter_interface_list_procedure.list, $v2008_interface_subprogram_default.default)}
	| (PURE | impure=IMPURE)? FUNCTION designator
		(PARAMETER? LPAREN parameter_interface_list_function RPAREN)? RETURN type_mark v2008_interface_subprogram_default?
		{$declaration = new InterfaceList.InterfaceFunctionDeclaration($impure == null, $designator.designator, $parameter_interface_list_function.list, $type_mark.mark, $v2008_interface_subprogram_default.default)};

v2008_interface_package_declaration returns [InterfaceList.AbstractInterfaceElement declaration = InterfaceList.NoElement] :
	PACKAGE identifier IS NEW selected_name
		GENERIC MAP LPAREN (association_list | BOX | DEFAULT) RPAREN
	{
		val generic =
			if ($association_list.list != null) Left($association_list.list)
			else if ($DEFAULT != null) Right(true)
			else Right(false)
		$declaration = new InterfaceList.InterfacePackageDeclaration($identifier.ident, $selected_name.name, generic)
	};

association_element returns [AssociationList.Element element] :
	((formal_part ARROW)=> formal_part ARROW)? actual_part
	{$element = new AssociationList.Element($formal_part.part, $actual_part.part)};

association_list returns [AssociationList list]
@init {val buffer = new Buffer[AssociationList.Element]} :
	element=association_element {buffer += element} (COMMA element=association_element {buffer += element})*
	{$list = new AssociationList(buffer.result)};
	
formal_part returns [Either[Identifier, (SelectedName, Name.Part)\] part] :
	identifier {$part = Left($identifier.ident)}
	| selected_name ((name_association_list_part)=> name_association_list_part | name_slice_part) {$part = Right(($selected_name.name, null))};

actual_part returns [Either[Expression, Identifier\] part] :
	//could be a name(signal_name, variable_name, file_name, subprogram_name, package_name), function_name or type_mark ;could be signal_name or variable_name
	({vhdl2008}?=> INERTIAL)? expression {$part = Left($expression.expr)} //TODO
	//| TODO {vhdl2008}?=> subtype_indication
	| OPEN {$part = Right(toIdentifier($OPEN))};

// B.8 Expression and Names
condition returns [Expression condition = NoExpression] :
	expression {$condition = $expression.expr};

expression returns [Expression expr]
@after {if ($expr == null) $expr = NoExpression} :
	logical_expression {$expr = $logical_expression.expression}
	| {vhdl2008}?=> CONDITION_OPERATOR primary {$expr = new ConditionExpression($CONDITION_OPERATOR, $primary.primary)};

logical_expression returns [Expression expression = NoExpression]
@after {if ($expression == null) $expression = NoExpression} :
	left=relation (
		NAND right=relation {$expression = new LogicalExpression($NAND, $left.relation, LogicalExpression.Operator.NAND, $right.relation)}
		| NOR right=relation {$expression = new LogicalExpression($NOR, $left.relation, LogicalExpression.Operator.NOR, $right.relation)}
		| {$expression = $left.relation} (logical_operator right=relation {$expression = new LogicalExpression($logical_operator.position, $expression, $logical_operator.operator, $right.relation)})*
	);

logical_operator returns [LogicalExpression.Operator.Value operator, Position position]
@init {$position = toPosition(input.LT(1))} :
	AND {$operator = LogicalExpression.Operator.AND}
	| OR {$operator = LogicalExpression.Operator.OR}
	| XOR {$operator = LogicalExpression.Operator.XOR}
	| XNOR {$operator = LogicalExpression.Operator.XNOR}
	//NAND and NOR are handled in logical_expression
	;

relation returns [Expression relation = NoExpression]
@after {if ($relation == null) $relation = NoExpression} :
	left=shift_expression {$relation = $left.expression}
	(relational_operator right=shift_expression {$relation = new Relation($relational_operator.position, $left.expression, $relational_operator.operator, $right.expression)})?;

relational_operator returns [Relation.Operator.Value operator, Position position]
@init {$position = toPosition(input.LT(1))} :
	EQ {$operator = Relation.Operator.EQ}
	| NEQ {$operator = Relation.Operator.NEQ}
	| LT {$operator = Relation.Operator.LT}
	| LEQ {$operator = Relation.Operator.LEQ}
	| GT {$operator = Relation.Operator.GT}
	| GEQ {$operator = Relation.Operator.GEQ}
	| {vhdl2008}?=> (
		MEQ {$operator = Relation.Operator.MEQ}
		| MNEQ {$operator = Relation.Operator.MNEQ}
		| MLT {$operator = Relation.Operator.MLT}
		| MLEQ {$operator = Relation.Operator.MLEQ}
		| MGT {$operator = Relation.Operator.MGT}
		| MGEQ {$operator = Relation.Operator.MGEQ}
		);

shift_expression returns [Expression expression = NoExpression]
@after {if ($expression == null) $expression = NoExpression} :
	left=simple_expression {$expression = $left.expression}
	(shift_operator right=simple_expression {$expression = new ShiftExpression($shift_operator.position, $left.expression, $shift_operator.operator, $right.expression)})?;

shift_operator returns [ShiftExpression.Operator.Value operator, Position position]
@init {$position = toPosition(input.LT(1))} :
	SLL {$operator = ShiftExpression.Operator.SLL}
	| SRL {$operator = ShiftExpression.Operator.SRL}
	| SLA {$operator = ShiftExpression.Operator.SLA}
	| SRA {$operator = ShiftExpression.Operator.SRA}
	| ROL {$operator = ShiftExpression.Operator.ROL}
	| ROR {$operator = ShiftExpression.Operator.ROR};

simple_expression returns [Expression expression = NoExpression]
@after {if ($expression == null) $expression = NoExpression} :
	s=sign? left=term
	{$expression = if (s != null) new SimpleExpression($sign.position, $sign.sign, $left.term, None, None) else $left.term}
	(adding_operator right=term {$expression = new SimpleExpression($adding_operator.position, None, $expression, $adding_operator.operator, $right.term)})*;

sign returns [SimpleExpression.SignOperator.Value sign, Position position]
@init {$position = toPosition(input.LT(1))} :
	PLUS {$sign = SimpleExpression.SignOperator.PLUS}
	| MINUS {$sign = SimpleExpression.SignOperator.MINUS};

adding_operator returns [SimpleExpression.AddOperator.Value operator, Position position]
@init {$position = toPosition(input.LT(1))} :
	PLUS {$operator = SimpleExpression.AddOperator.PLUS}
	| MINUS {$operator = SimpleExpression.AddOperator.MINUS}
	| AMPERSAND {$operator = SimpleExpression.AddOperator.CONCATENATION};

multiplying_operator returns [Term.Operator.Value operator, Position position]
@init {$position = toPosition(input.LT(1))} :
	MUL {$operator = Term.Operator.MUL}
	| DIV {$operator = Term.Operator.DIV}
	| MOD {$operator = Term.Operator.MOD}
	| REM {$operator = Term.Operator.REM};

term returns [Expression term = NoExpression]
@after {if ($term == null) $term = NoExpression} :
	left=factor {$term = $left.factor}
	(multiplying_operator right=factor {$term = new Term($multiplying_operator.position, $term, $multiplying_operator.operator, $right.factor)})*;
 
factor_operator returns [Factor.Operator.Value operator, Position position]
@init {$position = toPosition(input.LT(1))} :
	ABS {$operator = Factor.Operator.ABS}
	| NOT {$operator = Factor.Operator.NOT}
	| {vhdl2008}?=> (
		NAND {$operator = Factor.Operator.NAND}
		| NOR {$operator = Factor.Operator.NOR}
		| AND {$operator = Factor.Operator.AND}
		| OR {$operator = Factor.Operator.OR}
		| XOR {$operator = Factor.Operator.XOR}
		| XNOR {$operator = Factor.Operator.XNOR}
		);

factor returns [Expression factor = NoExpression]
@after {if ($factor == null) $factor = NoExpression} :
	left=primary {$factor = $left.primary} (DOUBLESTAR right=primary {$factor = new Factor($DOUBLESTAR, $left.primary, Factor.Operator.POW, $right.primary)})?
	| factor_operator primary {$factor = new Factor($factor_operator.position, $primary.primary, $factor_operator.operator)};

primary returns [Expression primary = NoExpression]
@after {if ($primary == null) $primary = NoExpression} :
	selected_name qualified_expression[$selected_name.name] {$primary = $qualified_expression.expression}
	| name {$primary = $name.name}
	| literal {$primary = $literal.literal}
	| allocator {$primary = $allocator.allocator}
	| aggregate {$primary = $aggregate.aggregate}; //LPAREN expression RPAREN handled by aggregate

allocator returns [Expression allocator = NoExpression] :
	NEW selected_name (
		qualified_expression[$selected_name.name] {$allocator = new Allocator($NEW, Left($qualified_expression.expression))}
		| index_constraint? {$allocator = new Allocator($NEW, Right(new SubTypeIndication(None, $selected_name.name, if ($index_constraint.constraint == null) None else Right($index_constraint.constraint), None)))}
		);

qualified_expression[SelectedName mark] returns [Expression expression = NoExpression] :
	APOSTROPHE aggregate
	{$expression = new QualifiedExpression(mark, $aggregate.aggregate)};

selected_name_list returns [Seq[SelectedName\] list = Nil]
@init {val names = new Buffer[SelectedName]} :
	n=selected_name {names += $n.name} (COMMA n=selected_name {names += $n.name})*
	{$list = names.result};

selected_name returns [SelectedName name]
@init {val parts = new Buffer[Identifier]} :
	name_prefix (name_selected_part {parts += $name_selected_part.part.identifier})*
	{$name = new SelectedName(if ($name_prefix.prefix != NoIdentifier) $name_prefix.prefix +: parts.result else Nil)};

name_list returns [Seq[Name\] list = Nil]
@init {val names = new Buffer[Name]} :
	n=name {names += $n.name} (COMMA n=name {names += $n.name})*
	{$list = names.result};

name returns [Name name]
@init {val parts = new Buffer[Name.Part]} :
	name_prefix (name_part {parts += $name_part.part})* {$name = new Name($name_prefix.prefix, parts.result)}
	| {vhdl2008}?=> v2008_external_name; //TODO

name_prefix returns [Identifier prefix = NoIdentifier] :
	identifier {$prefix = $identifier.ident}
	| STRING_LITERAL {$prefix = toIdentifier($STRING_LITERAL)};
 
name_part returns [Name.Part part] :
	name_selected_part {$part = $name_selected_part.part}
	| name_attribute_part {$part = $name_attribute_part.part}
	| (name_association_list_part)=> name_association_list_part {$part = $name_association_list_part.part}
	| name_slice_part {$part = $name_slice_part.part}; 

name_selected_part returns [Name.SelectedPart part] 
@init {$part = new Name.SelectedPart(NoIdentifier)} :
	DOT (
	identifier {$part = new Name.SelectedPart($identifier.ident)}
	| CHARACTER_LITERAL {$part = new Name.SelectedPart(toIdentifier($CHARACTER_LITERAL))}
	| STRING_LITERAL {$part = new Name.SelectedPart(toIdentifier($STRING_LITERAL))}
	| ALL {$part = new Name.SelectedPart(toIdentifier($ALL))}
	);

name_association_list_part returns [Name.AssociationListPart part] :
	LPAREN association_list RPAREN {$part = new Name.AssociationListPart($LPAREN, $association_list.list)};

name_slice_part returns [Name.SlicePart part] :
	LPAREN discrete_range RPAREN {$part = new Name.SlicePart($discrete_range.discreteRange)};

name_attribute_part returns [Name.AttributePart part]
@init {val expressions = new Buffer[Expression]; var id = NoIdentifier} :
	signature? APOSTROPHE (BASIC_IDENTIFIER | EXTENDED_IDENTIFIER | RANGE | TOLERANCE | ACROSS | THROUGH | REFERENCE) {id = toIdentifier(input.LT(-1))}
	((LPAREN)=> LPAREN expr=expression {expressions += expr} ({ams}?=> COMMA expr=expression {expressions += expr})* RPAREN)? {$part = new Name.AttributePart($signature.signature, id, expressions.result)};

signature returns [Signature signature] :
	LBRACKET selected_name_list? (RETURN type_mark)? RBRACKET
	{$signature = new Signature($LBRACKET, $selected_name_list.list, $type_mark.mark)};

//TODO
v2008_external_name :
	DLT (CONSTANT | SIGNAL | VARIABLE) v2008_external_pathname COLON subtype_indication DGT;

v2008_external_pathname :
	v2008_absolute_pathname | v2008_relative_pathname | v2008_package_pathname;

v2008_absolute_pathname :
	DOT (v2008_pathname_element DOT)* identifier;

v2008_relative_pathname :
	(CIRCUMFLEX DOT)* (v2008_pathname_element DOT)* identifier;

v2008_pathname_element :
	//could be entity_identifier, component_instantiation_label, block_label, package_identifier or generate_statment_label
	identifier (LPAREN expression RPAREN)?;

v2008_package_pathname :
	AT libraryIdentifier=identifier DOT (packageIdentifier=identifier DOT)* objectIdentifier=identifier;

literal returns [Expression literal = NoExpression]
@init {
	var literalType: Literal.Type.Value = null
	val position = toPosition(input.LT(1))
} :
	(
		REAL_LITERAL {literalType = Literal.Type.REAL_LITERAL}
		| INTEGER_LITERAL {literalType = Literal.Type.INTEGER_LITERAL}
		| BASED_LITERAL {literalType = Literal.Type.BASED_LITERAL}
		| CHARACTER_LITERAL {literalType = Literal.Type.CHARACTER_LITERAL}
		//| STRING_LITERAL {literalType = Literal.Type.STRING_LITERAL} handled by name_prefix
		| BIT_STRING_LITERAL {literalType = Literal.Type.BIT_STRING_LITERAL}
		| NULL {literalType = Literal.Type.NULL_LITERAL}
	)
	{$literal = new Literal(position, input.LT(-1).getText, literalType)}
	({input.LA(-1) == INTEGER_LITERAL || input.LA(-1) == REAL_LITERAL || input.LA(-1) == BASED_LITERAL}?=> selected_name {$literal = new PhysicalLiteral($literal.asInstanceOf[Literal], $selected_name.name)})?;

physical_literal returns [PhysicalLiteral literal]
@init {
	var literalType: Literal.Type.Value = null
	val position = toPosition(input.LT(1))
	val text = input.LT(1).getText
} :
	(
		INTEGER_LITERAL {literalType = Literal.Type.INTEGER_LITERAL}
		| REAL_LITERAL {literalType = Literal.Type.REAL_LITERAL}
		| BASED_LITERAL {literalType = Literal.Type.BASED_LITERAL}
	) selected_name
	{$literal = new PhysicalLiteral(position, text, $selected_name.name, literalType)};

element_association returns [Aggregate.ElementAssociation association] :
	((choices ARROW)=> choices ARROW)? expression
	{$association = new Aggregate.ElementAssociation($choices.choices, $expression.expr)};

aggregate returns [Aggregate aggregate]
@init {val associations = new Buffer[Aggregate.ElementAssociation]} :
	LPAREN association=element_association {associations += association} (COMMA association=element_association {associations += association})* RPAREN
	{$aggregate = new Aggregate($LPAREN, associations.result)};

choice returns [Choices.Choice choice]
@init {val position = toPosition(input.LT(1))} :
	(identifier (BAR | ARROW | COMMA | SEMICOLON))=> identifier {$choice = new Choices.Choice(position, Some(Third($identifier.ident)))}
	| (simple_expression (BAR | ARROW | COMMA | SEMICOLON))=> simple_expression {$choice = new Choices.Choice(position, Some(Second($simple_expression.expression)))}
	| discrete_range {$choice = new Choices.Choice(position, Some(First($discrete_range.discreteRange)))}
	| OTHERS {$choice = new Choices.Choice(position, None)};

choices returns [Seq[Choices.Choice\] choices = Nil]
@init {val buffer = new Buffer[Choices.Choice]} :
	c=choice {buffer += $c.choice} (BAR c=choice {buffer += $c.choice})*
	{$choices = buffer.result};

/* 
VHDL 2008 PSL
PSL_Property_Declaration : ;

PSL_Sequence_Declaration : ;

PSL_Clock_Declaration : ;

PSL_PSL_Directive : ;

PSL_Verification_Unit : ;
*/

identifier_list returns [Seq[Identifier\] list = Nil]
@init {val identifiers = new Buffer[Identifier]} :
	id=identifier {identifiers += id} (COMMA id=identifier {identifiers += id})*
	{$list = identifiers.result.filter(_ != NoIdentifier)};

identifier returns [Identifier ident = NoIdentifier] :
	BASIC_IDENTIFIER {$ident = toIdentifier(input.LT(-1))}
	| EXTENDED_IDENTIFIER {$ident = toIdentifier(input.LT(-1))};

v2008_tool_directive : APOSTROPHE identifier GRAPHIC_CHARACTER*;

label_colon returns [Identifier label] :
	identifier COLON {$label = $identifier.ident};