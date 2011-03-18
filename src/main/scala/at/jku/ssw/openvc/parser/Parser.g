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

options{
	language=Scala;
	memoize=true;
	superClass=AbstractParser;
	tokenVocab=Lexer;
}

@parser::header{
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
@init{followSet=state.following(state._fsp)} :;

sync [String message]
@init{
    // Consume any garbled tokens that come before the next statement
    // or the end of the block. The only slight risk here is that the
    // block becomes MORE inclusive than it should but as the script is
    // in error, this is a better course than throwing out the block
    // when the error occurs and screwing up the whole meaning of
    // the rest of the token stream.
    //
    val startToken=input.LT(1)
    syncToSet()
} 
@after{
	// If we consume any tokens at this point then we create an error.
	if (startToken ne input.LT(1)) {
		syntaxErrorList += new CompilerMessage(position=toPosition(startToken),message="garbled " + message)
	}
}:;   // Deliberately match nothing, causing this rule always to be entered.
    
//B.1 Design File

design_file returns [DesignFile designFile]
@init{
	val units=new Buffer[DesignUnit]()
} :
	(design_unit{units += $design_unit.designUnit})+ EOF
	{$designFile=new DesignFile(units.result)};

context_item returns [ContextItem contextItem=NoNode] :
	library_clause {contextItem = $library_clause.libraryClause}
	| use_clause {contextItem = $use_clause.useClause}
	| {vhdl2008}?=>v2008_context_reference {contextItem = $v2008_context_reference.contextReference};
	
context_items returns [Seq[ContextItem\] contextItems] 
@init {
	val items=new Buffer[ContextItem]()
} :
	(context_item{items += $context_item.contextItem})* {$contextItems=items.result};
	
design_unit returns [DesignUnit designUnit] :
	context_items library_unit 
	{$designUnit=new DesignUnit($context_items.contextItems,$library_unit.libraryUnit)};
	
library_unit returns [LibraryUnit libraryUnit=NoNode]
@after{$libraryUnit=if ($libraryUnit!=null) $libraryUnit else NoNode} :
	entity_declaration {$libraryUnit=$entity_declaration.entityDecl}
	| architecture_body {$libraryUnit=$architecture_body.archDecl}
	| package_declaration {$libraryUnit=$package_declaration.packageDecl}
	| package_body {$libraryUnit=$package_body.packageBody}
	| {vhdl2008}?=>v2008_package_instantiation_declaration {$libraryUnit=$v2008_package_instantiation_declaration.packageInstantiationDecl}
	| {vhdl2008}?=>v2008_context_declaration {$libraryUnit=$v2008_context_declaration.contextDecl}
	| configuration_declaration {$libraryUnit=$configuration_declaration.configDecl}
	//| {psl}?=>PSL_Verification_Unit
	;
	
library_clause returns [LibraryClause libraryClause] : 
	LIBRARY identifier_list SEMICOLON {$libraryClause = new LibraryClause($LIBRARY,$identifier_list.list)}; 

v2008_context_reference returns [ContextReference contextReference] :
	CONTEXT selected_name_list SEMICOLON {$contextReference = new ContextReference($CONTEXT,$selected_name_list.list)};

//B.2 Library Unit Declarations
generic_clause returns [Seq[InterfaceList.AbstractInterfaceElement\] list] :
	GENERIC LPAREN generic_interface_list RPAREN
	{$list = $generic_interface_list.list};

generic_interface_list returns [Seq[InterfaceList.AbstractInterfaceElement\] list]
@init{
	val elements=new Buffer[InterfaceList.AbstractInterfaceElement]()
} :
	decl1=interface_element_generic {elements += $decl1.element} (SEMICOLON decl2=interface_element_generic {elements += $decl2.element})*
	{$list=elements.result};
    
port_clause returns [Seq[InterfaceList.AbstractInterfaceElement\] list] :
	PORT LPAREN port_interface_list RPAREN SEMICOLON
	{$list = $port_interface_list.list};

port_interface_list returns [Seq[InterfaceList.AbstractInterfaceElement\] list]
@init{
	val elements=new Buffer[InterfaceList.AbstractInterfaceElement]()
} :
	decl1=interface_element_port {elements += $decl1.element} (SEMICOLON decl2=interface_element_port {elements += $decl2.element})*
	{$list = elements.result};
    		
entity_declaration returns [EntityDeclaration entityDecl]
@init{
 	val declarativeItems=new Buffer[DeclarativeItem]()
 	val concurrentStmt=new Buffer[ConcurrentStatement]()
 	val syncMessage="package declarative item"
} :
	entityToken=ENTITY start_identifier=identifier IS
		(generic_clause SEMICOLON)?
		port_clause?
		sync[syncMessage] (entity_declarative_item{declarativeItems += $entity_declarative_item.item} sync[syncMessage])* 
	(BEGIN
		(label=label_colon? postponed=POSTPONED? (
			concurrent_assertion_statement[$label.label,postponed!=null] {concurrentStmt += $concurrent_assertion_statement.assertStmt}
			| concurrent_procedure_call_statement[$label.label,postponed!=null]{concurrentStmt += $concurrent_procedure_call_statement.procedureCallStmt}
			| process_statement[$label.label,postponed!=null] {concurrentStmt += $process_statement.processStmt}
			//| {psl}?=>PSL_PSL_Directive
			)
		)* 
	)?
	END ENTITY? end_identifier=identifier? SEMICOLON
	{$entityDecl=new EntityDeclaration($entityToken,$start_identifier.id,$generic_clause.list,$port_clause.list,declarativeItems.result,concurrentStmt.result,$end_identifier.id)};
		
entity_declarative_item returns [DeclarativeItem item=NoNode] :
	subprogram_declaration_or_body {item=$subprogram_declaration_or_body.declOrBody}
	| {vhdl2008}?=>(v2008_subprogram_instantiation_declaration {$item=$v2008_subprogram_instantiation_declaration.subprogramInstantiationDecl}
			| package_declaration {$item=$package_declaration.packageDecl}
			| package_body {$item=$package_body.packageBody}
			| v2008_package_instantiation_declaration {$item=$v2008_package_instantiation_declaration.packageInstantiationDecl}
			)
	| type_declaration {$item=$type_declaration.typeDecl}
	| subtype_declaration {$item=$subtype_declaration.subTypeDecl}
	| constant_declaration {$item=$constant_declaration.constantDecl}
	| signal_declaration {$item=$signal_declaration.signalDecl}
	| variable_declaration {$item=$variable_declaration.varDecl}
	| file_declaration {$item=$file_declaration.fileDecl}
	| alias_declaration {$item=$alias_declaration.aliasDecl}
	| attribute_declaration {$item=$attribute_declaration.attributeDecl}
	| attribute_specification {$item=$attribute_specification.attributeSpec}
	| disconnection_specification {$item=$disconnection_specification.disconnectSpec}
	| use_clause {$item=$use_clause.useClause}
	| group_template_declaration {$item=$group_template_declaration.groupTemplateDecl}
	| group_declaration {$item=$group_declaration.groupDecl}
	//| {psl}?=>PSL_Property_Declaration | {psl}?=>PSL_Sequence_Declaration
	//| {psl}?=>PSL_Clock_Declaration 
	| {ams}?=>
		(
		ams_step_limit_specification {$item=$ams_step_limit_specification.stepLimitSpec}
		| ams_nature_declaration {$item=$ams_nature_declaration.natureDecl}
		| ams_subnature_declaration {$item=$ams_subnature_declaration.subnatureDecl}
		| ams_quantity_declaration {$item=$ams_quantity_declaration.quantityDecl}
		| ams_terminal_declaration {$item=$ams_terminal_declaration.terminalDecl}
		);
		
architecture_body returns [ArchitectureDeclaration archDecl]
@init{
	val declarativeItems=new Buffer[DeclarativeItem]()
	val syncMessage="block declarative item"
} :
	architectureToken=ARCHITECTURE start_identifier=identifier OF selected_name IS
		sync[syncMessage] (block_declarative_item{declarativeItems += $block_declarative_item.item} sync[syncMessage])*
	BEGIN
		concurrent_statement_list
	END ARCHITECTURE? end_identifier=identifier? SEMICOLON
	{$archDecl=new ArchitectureDeclaration($architectureToken,$start_identifier.id,declarativeItems.result,$selected_name.name_,$concurrent_statement_list.list,$end_identifier.id)};
		
configuration_declarative_item returns [DeclarativeItem item=NoNode] :
	use_clause {$item=$use_clause.useClause}
	| attribute_specification {$item=$attribute_specification.attributeSpec}
	| group_declaration {$item=$group_declaration.groupDecl};
		
configuration_declaration returns [ConfigurationDeclaration configDecl]
@init{
 	val declarativeItems=new Buffer[DeclarativeItem]()
 	val syncMessage="configuration declarative item"
} :
	configurationToken=CONFIGURATION start_identifier=identifier OF selected_name IS
		sync[syncMessage] (configuration_declarative_item{declarativeItems += $configuration_declarative_item.item} sync[syncMessage])*
		//({psl}?=> psl_verification_unit_binding_indication)*
		block_configuration
	END CONFIGURATION? end_identifier=identifier? SEMICOLON
	{$configDecl=new ConfigurationDeclaration($configurationToken,$start_identifier.id,declarativeItems.result,$selected_name.name_,$block_configuration.blockConfig,$end_identifier.id)};

generate_specification returns [TripleEither[DiscreteRange,Identifier,Expression\] blockIndex] :
	  (discrete_range)=>discrete_range {$blockIndex=First($discrete_range.discreteRange)}
  	  | {vhdl2008 && (input.LA(1)==BASIC_IDENTIFIER || input.LA(1)==EXTENDED_IDENTIFIER) && input.LA(2)==RPAREN}?=>identifier {$blockIndex=Second($identifier.id)}
	  | expression {$blockIndex=Third($expression.expr)};
	
block_specification returns [BlockConfigurationSpecification blockConfig] :	 
	(identifier LPAREN)=>identifier (LPAREN generate_specification RPAREN)? {new BlockConfigurationSpecification(Right(($identifier.id,$generate_specification.blockIndex)))}
	| selected_name {$blockConfig=new BlockConfigurationSpecification(Left($selected_name.name_))};	
		 	
block_configuration returns [BlockConfiguration blockConfig]
@init{
	val useClauses=new Buffer[UseClause]()
	val configurations=new Buffer[Either[BlockConfiguration,ComponentConfiguration]]
} :
	FOR block_specification
		(use_clause {useClauses += $use_clause.useClause})*
		(
			config=block_configuration {configurations += Left($config.blockConfig)}
			| component_configuration {configurations += Right($component_configuration.componentConfig)}
		)*
	END FOR SEMICOLON 
	{$blockConfig=new BlockConfiguration($block_specification.blockConfig,useClauses.result,configurations.result)};

//psl_verification_unit_binding_indication : USE VUNIT selected_name_list SEMICOLON;		

component_configuration returns [ComponentConfiguration componentConfig] :
	FOR component_specification
		(binding_indication SEMICOLON)?
		//({psl}?=> psl_verification_unit_binding_indication)*
		block_configuration?
	END FOR SEMICOLON
	{$componentConfig=new ComponentConfiguration($component_specification.spec,$binding_indication.indication,$block_configuration.blockConfig)};
	
v2008_context_declaration returns [ContextDeclaration contextDecl] :
	contextToken=CONTEXT start_identifier=identifier IS 
		context_items
	END CONTEXT? end_identifier=identifier? SEMICOLON
	{$contextDecl=new ContextDeclaration($contextToken,$start_identifier.id,$context_items.contextItems,$end_identifier.id)};
		
//B.3 Declarations and Specifications
package_declaration returns [PackageDeclaration packageDecl]
@init{
	val declarativeItems=new Buffer[DeclarativeItem]()
	val syncMessage="package declarative item"
} :
	packageToken=PACKAGE start_identifier=identifier IS
		({vhdl2008}?=> generic_clause SEMICOLON
		(generic_map_aspect SEMICOLON)?)?
		sync[syncMessage] (package_declarative_item{declarativeItems += $package_declarative_item.item} sync[syncMessage])*
	END PACKAGE? end_identifier=identifier? SEMICOLON
	{$packageDecl=new PackageDeclaration($packageToken,$start_identifier.id,$generic_clause.list,$generic_map_aspect.list,declarativeItems.result,$end_identifier.id)};
		
package_declarative_item returns [DeclarativeItem item=NoNode] :
	subprogram_declaration {$item=$subprogram_declaration.subprogramDecl}
	| {vhdl2008}?=>(v2008_subprogram_instantiation_declaration {$item=$v2008_subprogram_instantiation_declaration.subprogramInstantiationDecl}
			| package_declaration {$item=$package_declaration.packageDecl}
			| v2008_package_instantiation_declaration {$item=$v2008_package_instantiation_declaration.packageInstantiationDecl}
			)
	| type_declaration {$item=$type_declaration.typeDecl}
	| subtype_declaration {$item=$subtype_declaration.subTypeDecl}
	| constant_declaration {$item=$constant_declaration.constantDecl}
	| signal_declaration {$item=$signal_declaration.signalDecl}
	| variable_declaration {$item=$variable_declaration.varDecl}
	| file_declaration {$item=$file_declaration.fileDecl}
	| alias_declaration {$item=$alias_declaration.aliasDecl}
	| component_declaration {$item=$component_declaration.componentDecl}
	| attribute_declaration {$item=$attribute_declaration.attributeDecl}
	| attribute_specification {$item=$attribute_specification.attributeSpec}
	| disconnection_specification {$item=$disconnection_specification.disconnectSpec}
	| use_clause {$item=$use_clause.useClause}
	| group_template_declaration {$item=$group_template_declaration.groupTemplateDecl}
	| group_declaration {$item=$group_declaration.groupDecl}
	//| {psl}?=>PSL_Property_Declaration | {psl}?=>PSL_Sequence_Declaration
	| {ams}?=> (
		ams_nature_declaration {$item=$ams_nature_declaration.natureDecl}
		| ams_subnature_declaration {$item=$ams_subnature_declaration.subnatureDecl}
		| ams_terminal_declaration {$item=$ams_terminal_declaration.terminalDecl}
	);

package_body returns [PackageBodyDeclaration packageBody]
@init{
	val declarativeItems = new Buffer[DeclarativeItem]()
	val syncMessage="package declarative item"
} :
	packageToken=PACKAGE BODY start_identifier=identifier IS
		sync[syncMessage] (package_body_declarative_item{declarativeItems += $package_body_declarative_item.item} sync[syncMessage])*
	END (PACKAGE BODY)? end_identifier=identifier? SEMICOLON
	{$packageBody = new PackageBodyDeclaration($packageToken,$start_identifier.id,declarativeItems.result,$end_identifier.id)};
    
package_body_declarative_item returns [DeclarativeItem item=NoNode] :
	subprogram_declaration_or_body {$item=$subprogram_declaration_or_body.declOrBody}
	| {vhdl2008}?=>(v2008_subprogram_instantiation_declaration {$item=$v2008_subprogram_instantiation_declaration.subprogramInstantiationDecl}
			| package_declaration {$item=$package_declaration.packageDecl}
			| package_body {$item=$package_body.packageBody}
			| v2008_package_instantiation_declaration {$item=$v2008_package_instantiation_declaration.packageInstantiationDecl}
			)
	| type_declaration {$item=$type_declaration.typeDecl}
	| subtype_declaration {$item=$subtype_declaration.subTypeDecl}
	| constant_declaration {$item=$constant_declaration.constantDecl}
	| variable_declaration {$item=$variable_declaration.varDecl}
	| file_declaration {$item=$file_declaration.fileDecl}
	| alias_declaration {$item=$alias_declaration.aliasDecl}
	| use_clause {$item=$use_clause.useClause}
	| {vhdl2008}?=>attribute_declaration {$item=$attribute_declaration.attributeDecl}
	| /*{vhdl2008}?=>*/attribute_specification {$item=$attribute_specification.attributeSpec} //attribute_specification is not part of VHDL 2002, added to compile ghdl math_real-body.vhd
	| group_template_declaration {$item=$group_template_declaration.groupTemplateDecl}
	| group_declaration {$item=$group_declaration.groupDecl};

v2008_package_instantiation_declaration returns [PackageInstantiationDeclaration packageInstantiationDecl] :
	PACKAGE identifier IS NEW selected_name
		generic_map_aspect? SEMICOLON
	{$packageInstantiationDecl=new PackageInstantiationDeclaration($PACKAGE,$identifier.id,$selected_name.name_,$generic_map_aspect.list)};

designator returns [Identifier id=Identifier.NoIdentifier] :
	identifier {$id=$identifier.id}
	| STRING_LITERAL {$id=toIdentifier($STRING_LITERAL)}; //STRING_LITERAL is a operator symbol	
	  
subprogram_specification returns [SubprogramDeclaration decl] :
	PROCEDURE identifier
	({vhdl2008}?=>generic_clause generic_map_aspect?)?
	(({vhdl2008}?=>PARAMETER)? LPAREN parameter_interface_list_procedure RPAREN)? 
	{$decl=new ProcedureDeclaration($PROCEDURE,$identifier.id,$generic_clause.list,$generic_map_aspect.list,$parameter_interface_list_procedure.list)}
	| (PURE | impure=IMPURE)? FUNCTION designator
	({vhdl2008}?=>generic_clause generic_map_aspect?)?
	(({vhdl2008}?=>PARAMETER)? LPAREN parameter_interface_list_function RPAREN)? RETURN type_mark
	{$decl=new FunctionDeclaration($FUNCTION,$impure==null,$designator.id,$generic_clause.list,$generic_map_aspect.list,$parameter_interface_list_function.list,$type_mark.typeName)};	

subprogram_declaration_or_body returns [DeclarativeItem declOrBody] :
	subprogram_specification (subprogram_body[$subprogram_specification.decl])? SEMICOLON
	{$declOrBody=if ($subprogram_body.subProgramDef!=null) $subprogram_body.subProgramDef else $subprogram_specification.decl};	
		
subprogram_declaration returns [DeclarativeItem subprogramDecl=NoNode] :
	subprogram_specification SEMICOLON
	{$subprogramDecl=$subprogram_specification.decl};
	
subprogram_body[SubprogramDeclaration subprogramDecl] returns [SubprogramDefinition subProgramDef]
@init{
	val declItems=new Buffer[DeclarativeItem]()
	val syncMessage="subprogram declarative item"
} :
	IS
		sync[syncMessage] (subprogram_declarative_item{declItems += $subprogram_declarative_item.item} sync[syncMessage])* 
	BEGIN
		saveFollowSet sequence_of_statements
	END ({$subprogramDecl.isInstanceOf[ProcedureDeclaration]}?=>PROCEDURE | {$subprogramDecl.isInstanceOf[FunctionDeclaration]}?=>FUNCTION)? endIdent=designator?
	{
		$subProgramDef = $subprogramDecl match {
			case procDecl : ProcedureDeclaration => 
				new ProcedureDefinition($subprogramDecl.position,procDecl.identifier,procDecl.genericInterfaceList,procDecl.genericAssociationList,procDecl.parameterInterfaceList,declItems.result,$sequence_of_statements.list,endIdent)
			case funcDecl : FunctionDeclaration =>
				new FunctionDefinition($subprogramDecl.position,funcDecl.isPure,funcDecl.identifier,funcDecl.genericInterfaceList,funcDecl.genericAssociationList,funcDecl.parameterInterfaceList,funcDecl.returnType,declItems.result,$sequence_of_statements.list,endIdent)
		}	
	};
    	
subprogram_declarative_item returns [DeclarativeItem item=NoNode] :
	subprogram_declaration_or_body {$item=$subprogram_declaration_or_body.declOrBody}
	| {vhdl2008}?=>(v2008_subprogram_instantiation_declaration {$item=$v2008_subprogram_instantiation_declaration.subprogramInstantiationDecl}
			| package_declaration {$item=$package_declaration.packageDecl}
			| package_body {$item=$package_body.packageBody}
			| v2008_package_instantiation_declaration {$item=$v2008_package_instantiation_declaration.packageInstantiationDecl}
			)
	| type_declaration {$item=$type_declaration.typeDecl}
	| subtype_declaration {$item=$subtype_declaration.subTypeDecl}
	| constant_declaration {$item=$constant_declaration.constantDecl}
	| variable_declaration {$item=$variable_declaration.varDecl}
	| file_declaration {$item=$file_declaration.fileDecl}
	| alias_declaration {$item=$alias_declaration.aliasDecl}
	| attribute_declaration {$item=$attribute_declaration.attributeDecl}
	| attribute_specification {$item=$attribute_specification.attributeSpec}
	| use_clause {$item=$use_clause.useClause}
	| group_template_declaration {$item=$group_template_declaration.groupTemplateDecl}
	| group_declaration {$item=$group_declaration.groupDecl};

v2008_subprogram_instantiation_declaration returns [SubprogramInstantiationDeclaration subprogramInstantiationDecl] :
	(PROCEDURE | functionToken=FUNCTION) identifier IS
		NEW selected_name signature?
			generic_map_aspect? SEMICOLON
	{$subprogramInstantiationDecl=new SubprogramInstantiationDeclaration(if ($PROCEDURE!=null) $PROCEDURE else $functionToken,$PROCEDURE!=null,$identifier.id,$selected_name.name_,$signature.signature_,$generic_map_aspect.list)};

type_declaration returns [DeclarativeItem typeDecl=NoNode] :
	TYPE identifier (IS type_definition[$identifier.id,toPosition($TYPE)])? SEMICOLON 
	{
		$typeDecl=if ($type_definition.typeDef!=null) $type_definition.typeDef
			  else new IncompleteTypeDeclaration($TYPE,$identifier.id)
	};

type_definition[Identifier id,Position pos] returns [DeclarativeItem typeDef=NoNode] :
	enumeration_type_definition[$id,$pos] {$typeDef=$enumeration_type_definition.enumTypeDef}
	| numeric_type_definition[$id,pos] {$typeDef=$numeric_type_definition.numericTypeDef} //parses integer,real or physcial type definitions
	| array_type_definition[$id,$pos] {$typeDef=$array_type_definition.arrayTypeDef}
	| record_type_definition[$id,$pos] {$typeDef=$record_type_definition.recordTypeDef}
	| access_type_definition[$id,$pos] {$typeDef=$access_type_definition.accessTypeDef}
	| file_type_definition[$id,$pos] {$typeDef=$file_type_definition.fileTypeDef}
	| protected_type_body[$id,$pos] {$typeDef=$protected_type_body.protectedTypeBody}
	| protected_type_declaration[$id,$pos] {$typeDef=$protected_type_declaration.protectedTypeDecl};
		
ams_nature_declaration returns [DeclarativeItem natureDecl=NoNode] :
	NATURE identifier IS ams_nature_definition[$identifier.id,toPosition($NATURE)] SEMICOLON
	{$natureDecl=$ams_nature_definition.natureDef};

ams_terminal_declaration returns [DeclarativeItem terminalDecl=NoNode] :
	TERMINAL identifier_list COLON ams_subnature_indication SEMICOLON
	{$terminalDecl=new TerminalDeclaration($TERMINAL,$identifier_list.list,$ams_subnature_indication.subNature)};
	
ams_nature_definition[Identifier id,Position pos] returns [AbstractTypeDeclaration natureDef] :
	ams_scalar_nature_definition[$id,pos] {$natureDef=$ams_scalar_nature_definition.natureDef}
	| ams_array_nature_definition[$id,pos] {$natureDef=$ams_array_nature_definition.natureDef}
	| ams_record_nature_definition[$id,pos] {$natureDef=$ams_record_nature_definition.natureDef};

ams_through_aspect returns [(Seq[Identifier\],Option[Expression\],Option[Expression\]) through_aspect] :
	identifier_list (TOLERANCE toleranceExpression=expression)? (VAR_ASSIGN defaultExpression=expression)? THROUGH
	{$through_aspect=($identifier_list.list,$toleranceExpression.expr,$defaultExpression.expr)};

ams_quantity_declaration returns [DeclarativeItem quantityDecl=NoNode] :
 	QUANTITY (terminal=ams_terminal_aspect {$quantityDecl=new BranchQuantityDeclaration($QUANTITY,None,None,$terminal.terminal_aspect)}
 		  | identifier_list 
 		  	(
 			(TOLERANCE toleranceExpression=expression)? (VAR_ASSIGN defaultExpression=expression)? (across=ACROSS|through=THROUGH) ({$through==null}?=>ams_through_aspect)? terminal=ams_terminal_aspect
 					{
 						$quantityDecl = if (across!=null) new BranchQuantityDeclaration($QUANTITY,($identifier_list.list,Option($toleranceExpression.expr),Option($defaultExpression.expr)),$ams_through_aspect.through_aspect,$terminal.terminal_aspect)
 								else new BranchQuantityDeclaration($QUANTITY,None,($identifier_list.list,Option($toleranceExpression.expr),Option($defaultExpression.expr)),$terminal.terminal_aspect)
 					}
 			| COLON subtype_indication 
	 			(
	 			(VAR_ASSIGN expr=expression)? {$quantityDecl=new FreeQuantityDeclaration($QUANTITY,$identifier_list.list,$subtype_indication.subType,$expr.expr)}
				| ams_source_aspect {$quantityDecl=new SourceQuantityDeclaration($QUANTITY,$identifier_list.list,$subtype_indication.subType,$ams_source_aspect.source_aspect)}
				)
			)
		)SEMICOLON;	
	
ams_terminal_aspect returns [(Name,Option[Name\]) terminal_aspect] :
	plus_terminal_name=name (TO minus_terminal_name=name)?
	{$terminal_aspect=($plus_terminal_name.name_,$minus_terminal_name.name_)};
	
ams_source_aspect returns [Either[(Expression,Expression),Expression\] source_aspect] :
	SPECTRUM magnitude_simple_expression=simple_expression COMMA phase_simple_expression=simple_expression {$source_aspect=Left(($magnitude_simple_expression.simpleExpr,$phase_simple_expression.simpleExpr))}
	| NOISE power_simple_expression=simple_expression {$source_aspect=Right($power_simple_expression.simpleExpr)};
	
constant_declaration returns [DeclarativeItem constantDecl=NoNode] :
	CONSTANT identifier_list COLON subtype_indication (VAR_ASSIGN expression)? SEMICOLON 
	{$constantDecl=new ConstantDeclaration($CONSTANT,$identifier_list.list,$subtype_indication.subType,$expression.expr)};
	
signal_declaration returns [DeclarativeItem signalDecl=NoNode] :
	SIGNAL identifier_list COLON subtype_indication (reg=REGISTER|bus=BUS)? (VAR_ASSIGN expression)? SEMICOLON
	{
		val signalType=
			if(reg!=null) Some(SignalDeclaration.Type.REGISTER)
			else if (bus!=null) Some(SignalDeclaration.Type.BUS)
			else None
		$signalDecl=new SignalDeclaration($SIGNAL,$identifier_list.list,$subtype_indication.subType,signalType,$expression.expr)
	};
	
variable_declaration returns [DeclarativeItem varDecl=NoNode] :
	SHARED? VARIABLE identifier_list COLON subtype_indication (VAR_ASSIGN expression)? SEMICOLON
	{$varDecl=new VariableDeclaration($VARIABLE,$SHARED!=null,$identifier_list.list,$subtype_indication.subType,$expression.expr)};
	
file_declaration returns [DeclarativeItem fileDecl=NoNode] :
	FILE identifier_list COLON subtype_indication ((OPEN file_open_kind_expression=expression)? IS file_logical_name=expression)? SEMICOLON
	{$fileDecl=new FileDeclaration($FILE,$identifier_list.list,$subtype_indication.subType,$file_open_kind_expression.expr,$file_logical_name.expr)};
	
alias_declaration returns [DeclarativeItem aliasDecl=NoNode] :
	ALIAS alias_designator (COLON subtype_indication)? IS name signature? SEMICOLON
	{$aliasDecl=new AliasDeclaration($ALIAS,$alias_designator.id,$subtype_indication.subType,$name.name_,$signature.signature_)}; 

alias_designator returns [Identifier id=Identifier.NoIdentifier] :
	identifier {$id=$identifier.id}
	| CHARACTER_LITERAL{$id=toIdentifier($CHARACTER_LITERAL)}
	| STRING_LITERAL{$id=toIdentifier($STRING_LITERAL)};	
	
component_declaration returns [DeclarativeItem componentDecl=NoNode] :
	componentToken=COMPONENT start_identifier=identifier IS?
		(generic_clause SEMICOLON)?
		port_clause?
	END COMPONENT end_identifier=identifier? SEMICOLON
	{$componentDecl=new ComponentDeclaration($componentToken,$start_identifier.id,$generic_clause.list,$port_clause.list,$end_identifier.id)};

attribute_declaration returns [DeclarativeItem attributeDecl=NoNode] :
	ATTRIBUTE identifier COLON type_mark SEMICOLON 
	{$attributeDecl=new AttributeDeclaration($ATTRIBUTE,$identifier.id,$type_mark.typeName)};
	
attribute_specification returns [DeclarativeItem attributeSpec=NoNode] :
	ATTRIBUTE identifier OF entity_name_list COLON entity_class IS expression SEMICOLON 
	{$attributeSpec=new AttributeSpecification($ATTRIBUTE,$identifier.id,$entity_name_list.list,$entity_class.entityClass,$expression.expr)};
		
entity_designator returns [(Identifier,Option[Signature\]) designator]
@init{
	var id:Identifier=null
} :	
	(
	identifier {id=$identifier.id}
	| CHARACTER_LITERAL{id=toIdentifier($CHARACTER_LITERAL)}
	| STRING_LITERAL{id=toIdentifier($STRING_LITERAL)}
	) signature?
	{$designator=(id,Option($signature.signature_))};
	
entity_name_list returns [Either[Seq[(Identifier,Option[Signature\])\],Identifier\] list]
@init{
	val elements=new Buffer[(Identifier,Option[Signature])]()
} :
	e1=entity_designator {elements += $e1.designator}(COMMA e2=entity_designator {elements += $e2.designator})* {$list=Left(elements.result)}
	| OTHERS {$list=Right(toIdentifier($OTHERS))}
	| ALL {$list=Right(toIdentifier($ALL))};

entity_class returns [EntityClass.Value entityClass] :
	ENTITY {$entityClass=EntityClass.ENTITY}
	| ARCHITECTURE {$entityClass=EntityClass.ARCHITECTURE}
	| CONFIGURATION {$entityClass=EntityClass.CONFIGURATION}
	| PACKAGE {$entityClass=EntityClass.PACKAGE}
	| PROCEDURE {$entityClass=EntityClass.PROCEDURE}
	| FUNCTION {$entityClass=EntityClass.FUNCTION}
	| TYPE {$entityClass=EntityClass.TYPE}
	| SUBTYPE {$entityClass=EntityClass.SUBTYPE}
	| CONSTANT {$entityClass=EntityClass.CONSTANT}
	| SIGNAL {$entityClass=EntityClass.SIGNAL}
	| VARIABLE {$entityClass=EntityClass.VARIABLE}
	| FILE {$entityClass=EntityClass.FILE}
	| COMPONENT {$entityClass=EntityClass.COMPONENT}
	| LABEL {$entityClass=EntityClass.LABEL}
	| LITERAL {$entityClass=EntityClass.LITERAL}
	| UNITS {$entityClass=EntityClass.UNITS}
	| GROUP {$entityClass=EntityClass.GROUP}
	//| {psl}?=>PROPERTY 
	//| {psl}?=>SEQUENCE
	| {ams}?=>( 
	  	NATURE {$entityClass=EntityClass.NATURE}
		| SUBNATURE {$entityClass=EntityClass.SUBNATURE}
		| QUANTITY {$entityClass=EntityClass.QUANTITY}
		| TERMINAL {$entityClass=EntityClass.TERMINAL}
		);

configuration_specification returns [DeclarativeItem configSpec=NoNode] :
	forToken=FOR component_specification
		binding_indication SEMICOLON
		//({psl}?=>psl_verification_unit_binding_indication)*
	({vhdl2008}?=>END FOR SEMICOLON)?
	{$configSpec= new ConfigurationSpecification($forToken,$component_specification.spec,$binding_indication.indication)};
		
instantiation_list returns [Either[Seq[Identifier\],Identifier\] list] :
	identifier_list {$list=Left($identifier_list.list)}
	| OTHERS {$list=Right(toIdentifier($OTHERS))}
	| ALL {$list=Right(toIdentifier($ALL))};		

component_specification returns [ComponentSpecification spec] :
	instantiation_list COLON selected_name
	{spec = new ComponentSpecification($instantiation_list.list,$selected_name.name_)};

entity_aspect returns [Option[Either[(SelectedName,Option[Identifier\]),SelectedName\]\] entityAspect] :
	ENTITY entity_name=selected_name (LPAREN architecture_identifier=identifier RPAREN)? {entityAspect=Option(Left(($entity_name.name_,Option(architecture_identifier))))}
	| CONFIGURATION  configuration_name=selected_name {entityAspect=Option(Right($configuration_name.name_))}
	| OPEN {entityAspect=None};
		
binding_indication returns [BindingIndication indication] :
	(USE entity_aspect)?
	generic_map_aspect?
	port_map_aspect?
	{indication = new BindingIndication($entity_aspect.entityAspect,$generic_map_aspect.list,$port_map_aspect.list)};

disconnection_specification returns [DeclarativeItem disconnectSpec=NoNode] :
	DISCONNECT (selected_name_list | id=OTHERS | id=ALL) COLON type_mark AFTER expression SEMICOLON
	{
		val signal_list = if (id==null) Left($selected_name_list.list) else Right(toIdentifier(id))
		$disconnectSpec= new DisconnectionSpecification($DISCONNECT,signal_list,$type_mark.typeName,$expression.expr)
	};

ams_step_limit_specification returns [DeclarativeItem stepLimitSpec=NoNode] :
	LIMIT (selected_name_list | id=OTHERS | id=ALL ) COLON type_mark WITH expression SEMICOLON	
	{
		val signal_list = if (id==null) Left($selected_name_list.list) else Right(toIdentifier(id))
		$stepLimitSpec = new StepLimitSpecification($LIMIT,signal_list,$type_mark.typeName,$expression.expr)
	};

entity_class_entry returns [GroupTemplateDeclaration.EntityClassEntry entry] :
	entity_class BOX?
	{entry = new GroupTemplateDeclaration.EntityClassEntry($entity_class.entityClass,$BOX!=null)};
		
group_template_declaration returns [DeclarativeItem groupTemplateDecl=NoNode]
@init{
	val elements=new Buffer[GroupTemplateDeclaration.EntityClassEntry]()
}:
	GROUP identifier IS LPAREN e1=entity_class_entry {elements += $e1.entry}(COMMA e2=entity_class_entry {elements += $e2.entry})*  RPAREN SEMICOLON
	{$groupTemplateDecl=new GroupTemplateDeclaration($GROUP,$identifier.id,elements.result)};		
		
group_declaration returns [DeclarativeItem groupDecl=NoNode] :
	GROUP identifier COLON selected_name LPAREN group_constituent_list RPAREN SEMICOLON
	{$groupDecl=new GroupDeclaration($GROUP,$identifier.id,$selected_name.name_,$group_constituent_list.list)};
	
group_constituent returns [Either[Name,Identifier\] constituent] :
	name {$constituent=Left($name.name_)}
	| CHARACTER_LITERAL {$constituent=Right(toIdentifier($CHARACTER_LITERAL))};

group_constituent_list returns [Seq[Either[Name,Identifier\]\] list]
@init{
	val elements=new Buffer[Either[Name,Identifier]]()
} :
	c1=group_constituent{elements += $c1.constituent} (COMMA c2=group_constituent {elements += $c2.constituent})*
	{$list=elements.result};
    
use_clause returns [UseClause useClause] :
	USE selected_name_list SEMICOLON
	{$useClause=new UseClause($USE,$selected_name_list.list)};	
	
// B.4 Type Definitions
enumeration_literal returns [Identifier id=Identifier.NoIdentifier] :
	identifier {$id=$identifier.id}
	| CHARACTER_LITERAL {$id=toIdentifier($CHARACTER_LITERAL)};
	
enumeration_type_definition[Identifier id,Position pos] returns [EnumerationTypeDefinition enumTypeDef]
@init{
	val elements=new Buffer[Identifier]()
} :
	LPAREN e1=enumeration_literal {elements += $e1.id}(COMMA e2=enumeration_literal {elements += $e2.id})* RPAREN
	{$enumTypeDef=new EnumerationTypeDefinition($pos,$id,elements.result)};

numeric_type_definition[Identifier id,Position pos] returns [AbstractTypeDeclaration numericTypeDef] 
@init{
	val elements=new Buffer[PhysicalTypeDefinition.Element]()
} :
	range_constraint {$numericTypeDef=new IntegerOrFloatingPointTypeDefinition($pos,$id,$range_constraint.rangeConstraint)}
	(
		UNITS
		baseIdent=identifier SEMICOLON
		(
			idx=identifier EQ physical_literal SEMICOLON 
			{elements += new PhysicalTypeDefinition.Element($idx.id,$physical_literal.literal_)}
		)*
		END UNITS endIdent=identifier?
		{$numericTypeDef=new PhysicalTypeDefinition($pos,$id,$range_constraint.rangeConstraint,$baseIdent.id,elements.result,$endIdent.id)}
	)?;	

index_subtype_definition returns [SelectedName typeMark] :
	type_mark RANGE BOX {$typeMark=$type_mark.typeName};
				
array_type_definition[Identifier id,Position pos] returns [ArrayTypeDefinition arrayTypeDef]
@init{
	val unConstraintList=new Buffer[SelectedName]()
} :
	ARRAY (
		LPAREN type1=index_subtype_definition {unConstraintList += $type1.typeMark} (COMMA type2=index_subtype_definition {unConstraintList += $type2.typeMark})* RPAREN
		| index_constraint 
	) OF subtype_indication
	{
		val result = unConstraintList.result
		$arrayTypeDef = if (result.nonEmpty) new ArrayTypeDefinition($pos,$id,Left(result),$subtype_indication.subType)
				else new ArrayTypeDefinition($pos,$id,Right($index_constraint.ranges),$subtype_indication.subType)
	};
		
record_type_definition[Identifier id,Position pos] returns [RecordTypeDefinition recordTypeDef]
@init{
	val elements=new Buffer[RecordTypeDefinition.Element]()
} :
	RECORD 
	(
		identifier_list COLON subtype_indication SEMICOLON
		{elements += new RecordTypeDefinition.Element($identifier_list.list, $subtype_indication.subType)}
	)+
	END RECORD identifier?
	{$recordTypeDef=new RecordTypeDefinition($pos,$id,elements.result,$identifier.id)};

access_type_definition[Identifier id,Position pos] returns [AccessTypeDefinition accessTypeDef] :
	ACCESS subtype_indication 
	{$accessTypeDef=new AccessTypeDefinition($pos,$id,$subtype_indication.subType)};
	
file_type_definition[Identifier id,Position pos] returns [FileTypeDefinition fileTypeDef] :
	FILE OF type_mark 
	{$fileTypeDef=new FileTypeDefinition($pos,$id,$type_mark.typeName)};

ams_scalar_nature_definition[Identifier id,Position pos] returns [ScalarNatureDefinition natureDef] :
	t1=type_mark ACROSS t2=type_mark THROUGH identifier REFERENCE
	{$natureDef=new ScalarNatureDefinition(pos,$id,$t1.typeName,$t2.typeName,$identifier.id)};

ams_array_nature_definition[Identifier id,Position pos] returns [ArrayNatureTypeDefinition natureDef]
@init{
	val unConstraintList=new Buffer[SelectedName]()
} :
	ARRAY (
		LPAREN type1=index_subtype_definition {unConstraintList += $type1.typeMark} (COMMA type2=index_subtype_definition {unConstraintList += $type2.typeMark})* RPAREN
		| index_constraint
	) OF ams_subnature_indication
	{
		val result = unConstraintList.result
		$natureDef = if (result.nonEmpty) new ArrayNatureTypeDefinition($pos,$id,Left(result),$ams_subnature_indication.subNature)
			     else new ArrayNatureTypeDefinition($pos,$id,Right($index_constraint.ranges),$ams_subnature_indication.subNature)
	};

ams_record_nature_definition[Identifier id,Position pos] returns [RecordNatureDefinition natureDef]
@init{
	val elements=new Buffer[RecordNatureDefinition.Element]()
} :
	RECORD
	(
		identifier_list COLON ams_subnature_indication SEMICOLON
		{elements += new RecordNatureDefinition.Element($identifier_list.list, $ams_subnature_indication.subNature)}
	)+
	END RECORD identifier?
	{$natureDef=new RecordNatureDefinition(pos,$id,elements.result,$identifier.id)};

ams_subnature_declaration returns [DeclarativeItem subnatureDecl=NoNode] :
	SUBNATURE identifier IS ams_subnature_indication SEMICOLON
	{$subnatureDecl= new SubNatureDeclaration($SUBNATURE,$identifier.id,$ams_subnature_indication.subNature)};
	
ams_subnature_indication returns [SubNatureIndication subNature] :
	ams_nature_mark index_constraint? (TOLERANCE e1=expression ACROSS e2=expression THROUGH)?
	{$subNature=new SubNatureIndication($ams_nature_mark.typeName,$index_constraint.ranges,$e1.expr,$e2.expr)};
	
ams_nature_mark returns [SelectedName typeName] :
	 selected_name {$typeName=$selected_name.name_};
		
protected_type_declaration[Identifier id,Position pos] returns [ProtectedTypeDeclaration protectedTypeDecl]
@init{
	val items=new Buffer[DeclarativeItem]()
	val syncMessage="protected_type declarative item"
} :
	PROTECTED
		sync[syncMessage] (protected_type_declarative_item{items += $protected_type_declarative_item.item} sync[syncMessage])*
	END PROTECTED identifier?
	{$protectedTypeDecl=new ProtectedTypeDeclaration($pos,$id,items.result,$identifier.id)};
		
protected_type_declarative_item returns [DeclarativeItem item=NoNode] :
	subprogram_declaration {$item=$subprogram_declaration.subprogramDecl}
	| {vhdl2008}?=>v2008_subprogram_instantiation_declaration {$item=$v2008_subprogram_instantiation_declaration.subprogramInstantiationDecl}
	| attribute_specification {$item=$attribute_specification.attributeSpec}
	| use_clause {$item=$use_clause.useClause};

protected_type_body[Identifier id,Position pos] returns [ProtectedTypeBodyDeclaration protectedTypeBody]
@init{
	val items=new Buffer[DeclarativeItem]()
	val syncMessage="protected type declarative item"
} :
	PROTECTED BODY
		sync[syncMessage] (protected_type_body_declarative_item{items += $protected_type_body_declarative_item.item} sync[syncMessage])*
	END PROTECTED BODY identifier?
	{$protectedTypeBody=new ProtectedTypeBodyDeclaration($pos,$id,items.result,$identifier.id)};
		
protected_type_body_declarative_item returns [DeclarativeItem item=NoNode] :
	subprogram_declaration_or_body {$item=$subprogram_declaration_or_body.declOrBody}
	| {vhdl2008}?=>(v2008_subprogram_instantiation_declaration {$item=$v2008_subprogram_instantiation_declaration.subprogramInstantiationDecl}
			| package_declaration {$item=$package_declaration.packageDecl}
			| package_body {$item=$package_body.packageBody}
			| v2008_package_instantiation_declaration {$item=$v2008_package_instantiation_declaration.packageInstantiationDecl}
			)
	| type_declaration {$item=$type_declaration.typeDecl}
	| subtype_declaration {$item=$subtype_declaration.subTypeDecl}
	| constant_declaration {$item=$constant_declaration.constantDecl}
	| variable_declaration{$item=$variable_declaration.varDecl}
	| file_declaration {$item=$file_declaration.fileDecl}
	| alias_declaration {$item=$alias_declaration.aliasDecl}
	| attribute_declaration {$item=$attribute_declaration.attributeDecl}
	| attribute_specification {$item=$attribute_specification.attributeSpec}
	| use_clause {$item=$use_clause.useClause}
	| group_template_declaration {$item=$group_template_declaration.groupTemplateDecl}
	| group_declaration {$item=$group_declaration.groupDecl};
		
subtype_declaration returns [DeclarativeItem subTypeDecl=NoNode] :
	SUBTYPE identifier IS subtype_indication SEMICOLON
	{$subTypeDecl=new SubTypeDeclaration($SUBTYPE,$identifier.id,$subtype_indication.subType)};
	
subtype_indication returns [SubTypeIndication subType] :
	{vhdl2008}?=>((v2008_resolution_indication selected_name)=>v2008_resolution_indication)? selected_name v2008_constraint? ({ams}?=> TOLERANCE expression)? //TODO
		{$subType=new SubTypeIndication($v2008_resolution_indication.resolutionIndication,$selected_name.name_,$v2008_constraint.constraint_,$expression.expr)}
	| n1=selected_name n2=selected_name? constraint? ({ams}?=> TOLERANCE expression)?
		{
		$subType=if (n2!=null) new SubTypeIndication($n1.name_,$n2.name_,$constraint.constraint_,$expression.expr)
			else new SubTypeIndication(None,$n1.name_,$constraint.constraint_,$expression.expr)
		};

v2008_resolution_indication returns [SelectedName resolutionIndication]: 
	selected_name {$resolutionIndication=$selected_name.name_}//TODO
	| LPAREN (v2008_resolution_indication | identifier v2008_resolution_indication (COMMA identifier v2008_resolution_indication)*) RPAREN;

direction returns [Range.Direction.Value rangeDirection] :
	TO {$rangeDirection=Range.Direction.To}
	| DOWNTO {$rangeDirection=Range.Direction.Downto};

range_constraint returns [Range rangeConstraint] :
	RANGE range {$rangeConstraint=$range.range_};

index_constraint returns [Seq[DiscreteRange\] ranges]
@init{
	val list=new Buffer[DiscreteRange]()
} :
	LPAREN d1=discrete_range{list += d1} (COMMA  d2=discrete_range {list += d2})* RPAREN
	{$ranges = list.result};
		
range returns [Range range_] :
	(simple_expression direction)=> from=simple_expression direction to=simple_expression {$range_ =new Range(Left($from.simpleExpr,$direction.rangeDirection,$to.simpleExpr))}
	| name {$range_ =new Range(Right($name.name_))};

v2008_constraint returns [Either[Range,Seq[DiscreteRange\]\] constraint_] :	
	array_constraint
	| v2008_record_constraint //TODO
	| range_constraint{$constraint_ =Left($range_constraint.rangeConstraint)};
		
constraint returns [Either[Range,Seq[DiscreteRange\]\] constraint_] :
	index_constraint {$constraint_ =Right($index_constraint.ranges)}
	| range_constraint {$constraint_ =Left($range_constraint.rangeConstraint)};

array_constraint :	
	index_constraint ( array_constraint | v2008_record_constraint )?
	| LPAREN OPEN RPAREN ( array_constraint | v2008_record_constraint )?;

v2008_record_constraint :
	(identifier (array_constraint | v2008_record_constraint ) ) (COMMA (identifier (array_constraint | v2008_record_constraint ) ));

discrete_range returns [DiscreteRange discreteRange] :	
	(subtype_indication (RPAREN | COMMA | GENERATE | LOOP | BAR | ARROW | SEMICOLON))=>subtype_indication {$discreteRange=new DiscreteRange(Right($subtype_indication.subType))}
	| range {$discreteRange=new DiscreteRange(Left($range.range_))};		

type_mark returns [SelectedName typeName] :
	selected_name {$typeName=$selected_name.name_}; // could be type_name or subtype_name

// B.5 Concurrent Statements
concurrent_statement_list returns [Seq[ConcurrentStatement\] list] 
@init{
	val statementList=new Buffer[ConcurrentStatement]()
} : 
	(concurrent_statement {statementList += $concurrent_statement.stmt})*
	{$list=statementList.result};

concurrent_statement returns [ConcurrentStatement stmt] :
	  label=label_colon (
			(COMPONENT | (selected_name (GENERIC | PORT) MAP) | ENTITY | CONFIGURATION | BLOCK | IF ({vhdl2008}?=>label_colon)? condition GENERATE | FOR | {vhdl2008}?=> CASE expression GENERATE)=>concurrent_statement_with_label[$label.label] {$stmt=$concurrent_statement_with_label.stmt}
			| concurrent_statement_optional_label[$label.label] {$stmt=$concurrent_statement_optional_label.stmt}
			)
		| concurrent_statement_optional_label[$label.label] {$stmt=$concurrent_statement_optional_label.stmt};

concurrent_statement_with_label[Identifier label] returns [ConcurrentStatement stmt] :
	component_instantiation_statement[$label] {$stmt=$component_instantiation_statement.stmt}
	| block_statement[$label] {$stmt=$block_statement.blockStmt}
	| generate_statement[$label] {$stmt=$generate_statement.generateStmt};
				
concurrent_statement_optional_label[Identifier label] returns [ConcurrentStatement stmt] :
	{ams}?=>ams_concurrent_break_statement[$label] {$stmt=$ams_concurrent_break_statement.breakStmt}
	| ({ams}?=>((simple_expression AMS_ASSIGN) | IF | PROCEDURAL | CASE | NULL))=>ams_simultaneous_statement[$label] {$stmt=$ams_simultaneous_statement.stmt}	
	| postponed=POSTPONED? 
		(
		process_statement[$label,postponed!=null] {$stmt=$process_statement.processStmt}
		| concurrent_assertion_statement[$label,postponed!=null] {$stmt=$concurrent_assertion_statement.assertStmt}
		| (concurrent_procedure_call_statement[null,true])=>concurrent_procedure_call_statement[$label,postponed!=null] {$stmt=$concurrent_procedure_call_statement.procedureCallStmt}
		| concurrent_signal_assignment_statement[$label,postponed!=null] {$stmt=$concurrent_signal_assignment_statement.concurrentSignalAssignStmt}
		);
	//| {psl}?=>PSL_PSL_Directive 		

generic_map_aspect returns [AssociationList list] :
	GENERIC MAP LPAREN association_list RPAREN {$list=$association_list.list};
		
port_map_aspect returns [AssociationList list] :
	PORT MAP LPAREN association_list RPAREN {$list=$association_list.list};
					
block_statement[Identifier label] returns [BlockStatement blockStmt]
@init{
	val declItems=new Buffer[DeclarativeItem]()
	val syncMessage="block declarative item"
} :
	blockToken=BLOCK (LPAREN guard_expression=expression RPAREN)? IS?
		(generic_clause SEMICOLON (generic_map_aspect SEMICOLON)?)?
		(port_clause (port_map_aspect SEMICOLON)?)?
		sync[syncMessage] (block_declarative_item{declItems += $block_declarative_item.item} sync[syncMessage])*
	BEGIN
		concurrent_statement_list
	END BLOCK end_block_label=identifier? SEMICOLON 
	{
		$blockStmt=new BlockStatement($blockToken,$label,$guard_expression.expr,$generic_clause.list,$generic_map_aspect.list,$port_clause.list,$port_map_aspect.list,
			declItems.result,$concurrent_statement_list.list,$end_block_label.id)
	};
				
block_declarative_item returns [DeclarativeItem item=NoNode] :
	subprogram_declaration_or_body {$item=$subprogram_declaration_or_body.declOrBody}
	| {vhdl2008}?=>(v2008_subprogram_instantiation_declaration {$item=$v2008_subprogram_instantiation_declaration.subprogramInstantiationDecl}
			| package_declaration {$item=$package_declaration.packageDecl}
			| package_body {$item=$package_body.packageBody}
			| v2008_package_instantiation_declaration {$item=$v2008_package_instantiation_declaration.packageInstantiationDecl}
			)
	| type_declaration {$item=$type_declaration.typeDecl}
	| subtype_declaration {$item=$subtype_declaration.subTypeDecl}
	| constant_declaration {$item=$constant_declaration.constantDecl}
	| signal_declaration {$item=$signal_declaration.signalDecl}
	| variable_declaration {$item=$variable_declaration.varDecl}
	| file_declaration {$item=$file_declaration.fileDecl}
	| alias_declaration {$item=$alias_declaration.aliasDecl}
	| component_declaration {$item=$component_declaration.componentDecl}
	| attribute_declaration {$item=$attribute_declaration.attributeDecl}
	| attribute_specification {$item=$attribute_specification.attributeSpec}
	| configuration_specification {$item=$configuration_specification.configSpec}
	| disconnection_specification {$item=$disconnection_specification.disconnectSpec}
	| use_clause {$item=$use_clause.useClause}
	| group_template_declaration {$item=$group_template_declaration.groupTemplateDecl}
	| group_declaration {$item=$group_declaration.groupDecl}
	//| {psl}?=>PSL_Property_Declaration | {psl}?=>PSL_Sequence_Declaration
	//| {psl}?=>PSL_Clock_Declaration
	| {ams}?=>(
		ams_step_limit_specification {$item=$ams_step_limit_specification.stepLimitSpec}
		| ams_nature_declaration {$item=$ams_nature_declaration.natureDecl}
		| ams_subnature_declaration {$item=$ams_subnature_declaration.subnatureDecl}
		| ams_quantity_declaration {$item=$ams_quantity_declaration.quantityDecl}
		| ams_terminal_declaration {$item=$ams_terminal_declaration.terminalDecl}
	);
		
process_statement[Identifier label,Boolean postponed] returns [ProcessStatement processStmt]
@init{
	val declItem=new Buffer[DeclarativeItem]()
	val syncMessage="process declarative item"
} :
	processToken=PROCESS (LPAREN name_list RPAREN)? IS?
		sync[syncMessage] (process_declarative_item {declItem += $process_declarative_item.item} sync[syncMessage])*
	BEGIN
		saveFollowSet sequence_of_statements
	END POSTPONED? PROCESS end_process_label=identifier? SEMICOLON
	{$processStmt=new ProcessStatement($processToken,$label,$postponed,$name_list.list,declItem.result,$sequence_of_statements.list,$end_process_label.id)};
    	
process_declarative_item returns [DeclarativeItem item=NoNode] :
	subprogram_declaration_or_body {$item=$subprogram_declaration_or_body.declOrBody}
	| {vhdl2008}?=>(v2008_subprogram_instantiation_declaration {$item=$v2008_subprogram_instantiation_declaration.subprogramInstantiationDecl}
			| package_declaration {$item=$package_declaration.packageDecl}
			| package_body {$item=$package_body.packageBody}
			| v2008_package_instantiation_declaration {$item=$v2008_package_instantiation_declaration.packageInstantiationDecl}
			)
	| type_declaration {$item=$type_declaration.typeDecl}
	| subtype_declaration {$item=$subtype_declaration.subTypeDecl}
	| constant_declaration {$item=$constant_declaration.constantDecl}
	| variable_declaration {$item=$variable_declaration.varDecl}
	| file_declaration {$item=$file_declaration.fileDecl}
	| alias_declaration {$item=$alias_declaration.aliasDecl}
	| attribute_declaration {$item=$attribute_declaration.attributeDecl}
	| attribute_specification {$item=$attribute_specification.attributeSpec}
	| use_clause {$item=$use_clause.useClause}
	| group_template_declaration {$item=$group_template_declaration.groupTemplateDecl}
	| group_declaration {$item=$group_declaration.groupDecl};
		
concurrent_procedure_call_statement[Identifier label,Boolean postponed] returns [ConcurrentProcedureCallStatement procedureCallStmt] :
	selected_name (LPAREN association_list RPAREN)? SEMICOLON
	{$procedureCallStmt=new ConcurrentProcedureCallStatement($label,$postponed,$selected_name.name_,$association_list.list)};
		
concurrent_assertion_statement[Identifier label,Boolean postponed] returns [ConcurrentAssertionStatement assertStmt] :
	ASSERT condition  (REPORT report_expression=expression)? (SEVERITY severity_expression=expression)? SEMICOLON
	{$assertStmt=new ConcurrentAssertionStatement($ASSERT,$label,$postponed,$condition.con,$report_expression.expr,$severity_expression.expr)};
							
concurrent_signal_assignment_statement[Identifier label,Boolean postponed] returns [ConcurrentSignalAssignmentStatement concurrentSignalAssignStmt] :
	concurrent_conditional_signal_assignment[$label,$postponed]{$concurrentSignalAssignStmt=$concurrent_conditional_signal_assignment.signalAssignment}
	| concurrent_selected_signal_assignment[$label,$postponed]{$concurrentSignalAssignStmt=$concurrent_selected_signal_assignment.signalAssignment};

concurrent_conditional_signal_assignment[Identifier label,Boolean postponed] returns [ConcurrentConditionalSignalAssignment signalAssignment]
@init{
 	val elements=new Buffer[ConcurrentConditionalSignalAssignment.When]()
} :
	target LEQ GUARDED? delay_mechanism?
  		conditional_waveforms[elements] SEMICOLON
	{$signalAssignment=new ConcurrentConditionalSignalAssignment($LEQ,$label,$postponed,$target.target_,$GUARDED!=null,$delay_mechanism.mechanism,elements.result.reverse)};

conditional_waveforms[Buffer[ConcurrentConditionalSignalAssignment.When\] elements] :
	waveform ( WHEN condition ( ELSE conditional_waveforms[elements] )? )? {elements += new ConcurrentConditionalSignalAssignment.When($waveform.waveForm,$condition.con)};
 		
concurrent_selected_signal_assignment[Identifier label,Boolean postponed] returns [ConcurrentSelectedSignalAssignment signalAssignment] :
	WITH expression SELECT ({vhdl2008}?=>QMARK)?
		target LEQ GUARDED? delay_mechanism? selected_waveforms SEMICOLON
		{$signalAssignment=new ConcurrentSelectedSignalAssignment($WITH,$label,$postponed,$expression.expr,$QMARK!=null,$target.target_,$GUARDED!=null,$delay_mechanism.mechanism,$selected_waveforms.waveforms)};
	
selected_waveform returns [ConcurrentSelectedSignalAssignment.When whenClause] :
	waveform WHEN choices {whenClause = new ConcurrentSelectedSignalAssignment.When($waveform.waveForm,$choices.choices_)};
	
selected_waveforms returns [Seq[ConcurrentSelectedSignalAssignment.When\] waveforms]
@init{
	val elements=new Buffer[ConcurrentSelectedSignalAssignment.When]()
} :
	s1=selected_waveform {elements += $s1.whenClause}(COMMA s2=selected_waveform{elements += $s2.whenClause})*
	{$waveforms=elements.result};
				
target returns [Target target_] :
	name {$target_ = new Target(Left($name.name_))}
	| aggregate {$target_ = new Target(Right($aggregate.aggregate_))};	
  		
component_instantiation_statement[Identifier label] returns [ComponentInstantiationStatement stmt]
@init{
	var componentType:ComponentInstantiationStatement.ComponentType.Value=null
	val position=toPosition(input.LT(1))
} :
	(
		COMPONENT? n=selected_name {componentType=ComponentInstantiationStatement.ComponentType.COMPONENT }
		| ENTITY n=selected_name (LPAREN architecture_identifier=identifier RPAREN)? {componentType=ComponentInstantiationStatement.ComponentType.ENTITY}
		| CONFIGURATION n=selected_name {componentType=ComponentInstantiationStatement.ComponentType.CONFIGURATION}
	)
	generic_map_aspect?
	port_map_aspect? SEMICOLON
	{$stmt=new ComponentInstantiationStatement(position,$label,componentType,$n.name_,$architecture_identifier.id,$generic_map_aspect.list,$port_map_aspect.list)};
		
generate_statement[Identifier label] returns [ConcurrentStatement generateStmt] :
	for_generate_statement[$label] {$generateStmt=$for_generate_statement.forGenerateStmt}
	| if_generate_statement[$label] {$generateStmt=$if_generate_statement.ifGenerateStmt}
	| {vhdl2008}?=>v2008_case_generate_statement[$label] {$generateStmt=$v2008_case_generate_statement.caseGenerateStmt};
	
for_generate_statement[Identifier label] returns [ForGenerateStatement forGenerateStmt] :
	FOR loopIdentifier=identifier IN discrete_range GENERATE
		body=generate_statement_body
	END GENERATE end_generate_label=identifier? SEMICOLON
	{$forGenerateStmt=new ForGenerateStatement($FOR,$label,$loopIdentifier.id,$discrete_range.discreteRange,$body.declarativeItems,$body.statementList,$body.endLabel,$end_generate_label.id)};
		
if_generate_statement[Identifier label] returns [IfGenerateStatement ifGenerateStmt]
@init{
	val ifList=new Buffer[IfGenerateStatement.IfThenPart]()
	var elsePart:Option[IfGenerateStatement.IfThenPart]=None
} :
	IF ({vhdl2008}?=>ifLabel=label_colon)? ifCondition=condition GENERATE
		ifBody=generate_statement_body {ifList += new IfGenerateStatement.IfThenPart($ifLabel.label,$ifCondition.con,$ifBody.declarativeItems,$ifBody.statementList,$ifBody.endLabel)}
	({vhdl2008}?=>(ELSIF elseifLabel=label_colon? elsifCondition=condition GENERATE
		elseIfBody=generate_statement_body
		{ifList += new IfGenerateStatement.IfThenPart($elseifLabel.label,$elsifCondition.con,$elseIfBody.declarativeItems,$elseIfBody.statementList,$elseIfBody.endLabel)}
	)*
	(ELSE 	elseLabel=label_colon? GENERATE
		elseBody=generate_statement_body
		{elsePart=Option(new IfGenerateStatement.IfThenPart($elseLabel.label,NoExpression,$elseBody.declarativeItems,$elseBody.statementList,$elseBody.endLabel))}
	)?)?
	END GENERATE identifier? SEMICOLON
	{$ifGenerateStmt=new IfGenerateStatement($IF,$label,ifList.result,elsePart,$identifier.id)};
	
v2008_case_generate_statement[Identifier label] returns [CaseGenerateStatement caseGenerateStmt] 
@init{
	val alternatives=new Buffer[CaseGenerateStatement.When]()
} :
	CASE expression GENERATE
		(WHEN label_colon? choices ARROW body=generate_statement_body {alternatives += new CaseGenerateStatement.When($label_colon.label,$choices.choices_,$body.declarativeItems,$body.statementList,$body.endLabel)})+
	END GENERATE identifier? SEMICOLON
	{$caseGenerateStmt=new CaseGenerateStatement($CASE,$label,$expression.expr,alternatives.result,$identifier.id)};
		
generate_statement_body returns [Seq[DeclarativeItem\] declarativeItems,Seq[ConcurrentStatement\] statementList,Identifier endLabel]
@init{
	val items=new Buffer[DeclarativeItem]()
	val syncMessage="block declarative item"
} :
	(
		(block_declarative_item{items += $block_declarative_item.item} sync[syncMessage])*
		BEGIN
	)?
		concurrent_statement_list
	({vhdl2008}?=>END identifier? SEMICOLON)?
	{
		$declarativeItems=items.result
		$statementList=$concurrent_statement_list.list
		$endLabel=$identifier.id
	};

ams_concurrent_break_statement[Identifier label] returns [ConcurrentBreakStatement breakStmt] :
	BREAK ams_break_element_list? (ON name_list)? (WHEN expression)? SEMICOLON
	{$breakStmt=new ConcurrentBreakStatement($BREAK,$label,$ams_break_element_list.list,$name_list.list,$expression.expr)};

// E.6 Simultaneous Statements
ams_simultaneous_statement[Identifier label] returns [SimultaneousStatement stmt] :
	ams_simple_simultaneous_statement[$label] {$stmt=$ams_simple_simultaneous_statement.stmt}
	| ams_simultaneous_if_statement[$label] {$stmt=$ams_simultaneous_if_statement.ifStmt}
	| ams_simultaneous_procedural_statement[$label] {$stmt=$ams_simultaneous_procedural_statement.proceduralStmt}
	| ams_simultaneous_case_statement[$label] {$stmt=$ams_simultaneous_case_statement.caseStmt}
	| ams_simultaneous_null_statement[$label] {$stmt=$ams_simultaneous_null_statement.nullStmt};

ams_simultaneous_statement_list returns [Seq[SimultaneousStatement\] list]
@init{
	val tmpList=new Buffer[SimultaneousStatement]()
} :
	(label_colon? ams_simultaneous_statement[$label_colon.label]{tmpList += $ams_simultaneous_statement.stmt})*
	{$list=tmpList.result};
			
ams_simple_simultaneous_statement[Identifier label] returns [SimpleSimultaneousStatement stmt] :
	e1=simple_expression AMS_ASSIGN e2=simple_expression (TOLERANCE tolerance_expression=expression)? SEMICOLON
	{$stmt=new SimpleSimultaneousStatement($label,$e1.simpleExpr,$e2.simpleExpr,$tolerance_expression.expr)};
					
ams_simultaneous_if_statement[Identifier label] returns [SimultaneousIfStatement ifStmt]
@init{
	val ifList=new Buffer[SimultaneousIfStatement.IfUsePart]()
} :
	IF if_condition=condition USE
		if_simultaneous_statement=ams_simultaneous_statement_list {ifList += new SimultaneousIfStatement.IfUsePart($if_condition.con,$if_simultaneous_statement.list)}
	(ELSIF elsif_condition=condition USE
		else_if_simultaneous_statement=ams_simultaneous_statement_list
		{ifList += new SimultaneousIfStatement.IfUsePart($elsif_condition.con,$else_if_simultaneous_statement.list)}
	)*
	(ELSE
		else_simultaneous_statement=ams_simultaneous_statement_list)?
	END USE identifier? SEMICOLON
	{$ifStmt=new SimultaneousIfStatement($IF,$label,ifList.result,$else_simultaneous_statement.list,$identifier.id)};
						
ams_simultaneous_case_statement[Identifier label] returns [SimultaneousCaseStatement caseStmt]
@init{
	val alternatives=new Buffer[SimultaneousCaseStatement.When]()
} :
	caseToken=CASE expression USE
		(WHEN choices ARROW ams_simultaneous_statement_list {alternatives += new SimultaneousCaseStatement.When($choices.choices_,$ams_simultaneous_statement_list.list)})+
	END CASE identifier? SEMICOLON
	{$caseStmt=new SimultaneousCaseStatement($caseToken,$label,$expression.expr,alternatives.result,$identifier.id)};
		
ams_simultaneous_procedural_statement[Identifier label] returns [SimultaneousProceduralStatement proceduralStmt]
@init{
	val items=new Buffer[DeclarativeItem]()
	val syncMessage="simultaneous procedural declarative item"
} :
	proceduralToken=PROCEDURAL IS?
		sync[syncMessage] (ams_simultaneous_procedural_declarative_item{items += $ams_simultaneous_procedural_declarative_item.item} sync[syncMessage])*
	BEGIN
		saveFollowSet sequence_of_statements
	END PROCEDURAL end_procedural_label=identifier? SEMICOLON
	{$proceduralStmt=new SimultaneousProceduralStatement($proceduralToken,$label,items.result,$sequence_of_statements.list,$end_procedural_label.id)};
		
ams_simultaneous_procedural_declarative_item returns [DeclarativeItem item=NoNode] :
	subprogram_declaration_or_body {$item=$subprogram_declaration_or_body.declOrBody}
	| type_declaration {$item=$type_declaration.typeDecl}
	| subtype_declaration {$item=$subtype_declaration.subTypeDecl}
	| constant_declaration {$item=$constant_declaration.constantDecl}
	| variable_declaration {$item=$variable_declaration.varDecl}
	| alias_declaration {$item=$alias_declaration.aliasDecl}
	| attribute_declaration {$item=$attribute_declaration.attributeDecl}
	| attribute_specification {$item=$attribute_specification.attributeSpec}
	| use_clause {$item=$use_clause.useClause}
	| group_template_declaration {$item=$group_template_declaration.groupTemplateDecl}
	| group_declaration {$item=$group_declaration.groupDecl};
	
ams_simultaneous_null_statement[Identifier label] returns [SimultaneousNullStatement nullStmt] :
	NULL SEMICOLON
	{$nullStmt=new SimultaneousNullStatement($NULL,$label)};

// B.6 Sequential Statements
sequence_of_statements returns [Seq[SequentialStatement\] list]
@init{
	val tmpList=new Buffer[SequentialStatement]()
	val set=followSet
} :
	{syncToFollowSet(set)} (sequential_statement{tmpList +=$sequential_statement.stmt} {syncToFollowSet(set)})*
	{$list=tmpList.result};

sequential_statement returns [SequentialStatement stmt]
@after{$stmt=if ($stmt!=null) $stmt else NoNode} :
	label=label_colon?
	(wait_statement[$label.label] {$stmt=$wait_statement.waitStmt}
	| assertion_statement[$label.label] {$stmt=$assertion_statement.assertStmt}
	| report_statement[$label.label] {$stmt=$report_statement.reportStmt}
	| (procedure_call_statement[null])=>procedure_call_statement[$label.label] {$stmt=$procedure_call_statement.procedureCallStmt}
	| assignment_statement[$label.label] {$stmt=$assignment_statement.assignmentStmt} //signal or variable assignment statement
	| if_statement[$label.label] {$stmt=$if_statement.ifStmt}
	| case_statement[$label.label] {$stmt=$case_statement.caseStmt}
	| loop_statement[$label.label] {$stmt=$loop_statement.loopStmt}
	| next_statement[$label.label] {$stmt=$next_statement.nextStmt}
	| exit_statement[$label.label] {$stmt=$exit_statement.exitStmt}
	| return_statement[$label.label] {$stmt=$return_statement.returnStmt}
	| null_statement[$label.label] {$stmt=$null_statement.nullStmt}
	| {ams}?=>ams_break_statement[$label.label] {$stmt=$ams_break_statement.breakStmt}
	);
	
wait_statement[Identifier label] returns [WaitStatement waitStmt] :
	WAIT (ON name_list)? (UNTIL condition)? (FOR expression)? SEMICOLON
	{$waitStmt=new WaitStatement($WAIT,$label,$name_list.list,$condition.con,$expression.expr)};
				
assertion_statement[Identifier label] returns [AssertionStatement assertStmt] :
	ASSERT condition (REPORT report_expression=expression)? (SEVERITY severity_expression=expression)? SEMICOLON
	{$assertStmt=new AssertionStatement($ASSERT,$label,$condition.con,$report_expression.expr,$severity_expression.expr)};
			     
report_statement[Identifier label] returns [ReportStatement reportStmt] :
	REPORT report_expression=expression (SEVERITY severity_expression=expression)? SEMICOLON
	{$reportStmt=new ReportStatement($REPORT,$label,$report_expression.expr,$severity_expression.expr)};	

force_mode returns [InterfaceList.Mode.Value mode] :
	IN {$mode=InterfaceList.Mode.IN}
	| OUT {$mode=InterfaceList.Mode.OUT};

v2008_conditional_expressions[Buffer[ConditionalVariableAssignment.When\] elements] :
	expression ( WHEN condition ( ELSE v2008_conditional_expressions[$elements])? )? {$elements += new ConditionalVariableAssignment.When($expression.expr,$condition.con)};	

v2008_selected_expression returns [SelectedVariableAssignment.When whenClause]: 
	expression WHEN choices {whenClause = new SelectedVariableAssignment.When($expression.expr,$choices.choices_)};
	
v2008_selected_expressions returns [Seq[SelectedVariableAssignment.When\] expressions] 
@init{
	val elements=new Buffer[SelectedVariableAssignment.When]()
} :
	s1=v2008_selected_expression {elements += $s1.whenClause} (COMMA s2=v2008_selected_expression {elements += $s2.whenClause})*
	{$expressions=elements.result};

assignment_statement[Identifier label] returns [SequentialStatement assignmentStmt] :	
	{vhdl2008}?=>(
		     v2008_conditional_assignment[$label] {$assignmentStmt=$v2008_conditional_assignment.stmt}
		     | v2008_selected_assignment[$label] {$assignmentStmt=$v2008_selected_assignment.stmt}
		     )
	| simple_assignment[$label] {$assignmentStmt=$simple_assignment.stmt};
		
simple_assignment[Identifier label] returns [SequentialStatement stmt] :
	target (
		VAR_ASSIGN expression {$stmt=new SimpleVariableAssignmentStatement($VAR_ASSIGN,$label,$target.target_,$expression.expr)}
		| LEQ delay_mechanism? waveform {$stmt=new SimpleWaveformAssignmentStatement($LEQ,$label,$target.target_,$delay_mechanism.mechanism,$waveform.waveForm)}
	       )SEMICOLON;
	
v2008_conditional_assignment[Identifier label] returns [SequentialStatement stmt]
@init{
 	val waveforms=new Buffer[ConcurrentConditionalSignalAssignment.When]()
 	val expressions=new Buffer[ConditionalVariableAssignment.When]()
} :
	target (
		LEQ (
			RELEASE forceMode=force_mode? {$stmt=new SimpleReleaseAssignment($LEQ,$label,$target.target_,$forceMode.mode)}
			| delay_mechanism? conditional_waveforms[waveforms] {$stmt=new ConditionalWaveformAssignment($LEQ,$label,$target.target_,$delay_mechanism.mechanism,waveforms.result.reverse)}
			| FORCE forceMode=force_mode? v2008_conditional_expressions[expressions] {$stmt=new ConditionalForceAssignment($LEQ,$label,$target.target_,$forceMode.mode,expressions.result.reverse)}
		    )
	        | VAR_ASSIGN v2008_conditional_expressions[expressions] {$stmt=new ConditionalVariableAssignment($VAR_ASSIGN,$label,$target.target_,expressions.result.reverse)}
	       )SEMICOLON;
		
v2008_selected_assignment[Identifier label] returns [SequentialStatement stmt] :
	WITH expression SELECT QMARK? target 
	(
		LEQ (
			delay_mechanism? selected_waveforms {$stmt=new SelectedWaveformAssignment($WITH,$label,$expression.expr,$QMARK!=null,$target.target_,$delay_mechanism.mechanism,$selected_waveforms.waveforms)}
			| FORCE force_mode? selectedExpression=v2008_selected_expressions {$stmt=new SelectedForceAssignment($WITH,$label,$expression.expr,$QMARK!=null,$target.target_,$force_mode.mode,$selectedExpression.expressions)}
		    )
		| VAR_ASSIGN selectedExpression=v2008_selected_expressions {$stmt=new SelectedVariableAssignment($WITH,$label,$expression.expr,$QMARK!=null,$target.target_,$selectedExpression.expressions)}
	) SEMICOLON;			
					
delay_mechanism returns [DelayMechanism mechanism] :
	TRANSPORT {$mechanism=new DelayMechanism(DelayMechanism.DelayType.TRANSPORT,None)}
	| (REJECT expression)? INERTIAL {$mechanism=new DelayMechanism(DelayMechanism.DelayType.INERTIAL,$expression.expr)};

waveform_element returns [Waveform.Element element] :
	value_expression=expression (AFTER time_expression=expression)?  
 	{$element=new Waveform.Element($value_expression.expr,$time_expression.expr)};
	
waveform returns [Waveform waveForm]
@init{
	val elements=new Buffer[Waveform.Element]()
	val position=toPosition(input.LT(1))
} :
	e1=waveform_element{elements += $e1.element} (COMMA e2=waveform_element{elements += $e2.element})* {$waveForm=new Waveform(position,elements.result)}
	| UNAFFECTED {$waveForm=new Waveform(position,Seq())};
				
procedure_call_statement[Identifier label] returns [ProcedureCallStatement procedureCallStmt] :
	selected_name (LPAREN association_list RPAREN)? SEMICOLON
	{$procedureCallStmt=new ProcedureCallStatement($label,$selected_name.name_,$association_list.list)};
	
if_statement[Identifier label] returns [IfStatement ifStmt]
@init{
	val ifList=new Buffer[IfStatement.IfThenPart]()
} :
	ifToken=IF if_condition=condition THEN
		saveFollowSet if_sequential_statement=sequence_of_statements {ifList += new IfStatement.IfThenPart($if_condition.con,$if_sequential_statement.list)}
	(ELSIF elsif_condition=condition THEN
		 saveFollowSet elsif_sequential_statement=sequence_of_statements
		 {ifList += new IfStatement.IfThenPart($elsif_condition.con,$elsif_sequential_statement.list)}
	)*
	(ELSE	
		saveFollowSet else_sequential_statement=sequence_of_statements)?
	END IF identifier? SEMICOLON 
	{$ifStmt=new IfStatement($ifToken,$label,ifList.result,$else_sequential_statement.list,$identifier.id)};

case_statement[Identifier label] returns [CaseStatement caseStmt]
@init{
	val alternatives=new Buffer[CaseStatement.When]()
} :
	caseToken=CASE ({vhdl2008}?=>qmarkToken=QMARK)? expression IS
		(WHEN choices ARROW saveFollowSet sequence_of_statements {alternatives += new CaseStatement.When($choices.choices_,$sequence_of_statements.list)})+
	END CASE ({vhdl2008}?=>QMARK)? identifier? SEMICOLON
	{$caseStmt=new CaseStatement($caseToken,$label,qmarkToken!=null,$expression.expr,alternatives.result,$identifier.id)};

iteration_scheme returns [Either[Expression,(Identifier,DiscreteRange)\] scheme] :
	WHILE condition {$scheme=Left($condition.con)}
	| FOR identifier IN discrete_range {$scheme=Right(($identifier.id,$discrete_range.discreteRange))};	
			
loop_statement[Identifier label] returns [SequentialStatement loopStmt]
@init{
	val position=toPosition(input.LT(1))
} :
	stmtType=iteration_scheme? LOOP
		saveFollowSet sequence_of_statements
	END LOOP end_loop_label=identifier? SEMICOLON
	{		
		loopStmt=Option(stmtType) match {
			case Some(x) => x match {
				case Left(condition) =>new WhileStatement(position,$label,condition,$sequence_of_statements.list,$end_loop_label.id)
				case Right((identifier,discreteRange)) =>new ForStatement(position,$label,identifier,discreteRange,$sequence_of_statements.list,$end_loop_label.id)
			}
			case None =>new LoopStatement(position,$label,$sequence_of_statements.list,$end_loop_label.id)
		}
	};

next_statement[Identifier label] returns [NextStatement nextStmt] :
	NEXT identifier? (WHEN condition)? SEMICOLON 
	{$nextStmt=new NextStatement($NEXT,$label,$identifier.id,$condition.con)};

exit_statement[Identifier label] returns [ExitStatement exitStmt] :
	EXIT identifier? (WHEN condition)? SEMICOLON 
	{$exitStmt=new ExitStatement($EXIT,$label,$identifier.id,$condition.con)};

return_statement[Identifier label] returns [ReturnStatement returnStmt] :
	RETURN expression? SEMICOLON 
	{$returnStmt=new ReturnStatement($RETURN,$label,$expression.expr)};
	
null_statement[Identifier label] returns [NullStatement nullStmt] :
	NULL SEMICOLON
	{$nullStmt=new NullStatement($NULL,$label)};

ams_break_statement[Identifier label] returns [AMSBreakStatement breakStmt] :
	BREAK ams_break_element_list? (WHEN expression)? SEMICOLON
	{$breakStmt=new AMSBreakStatement($BREAK,$label,$ams_break_element_list.list,$expression.expr)};

ams_break_element_list returns [Seq[BreakElement\] list]
@init{
	val elements=new Buffer[BreakElement]()
} :
	e1=ams_break_element {elements += $e1.breakElement}(COMMA e2=ams_break_element {elements += $e2.breakElement})*
	{$list=elements.result};
			
ams_break_element returns [BreakElement breakElement] :
	(FOR quantity_name1=name USE)? quantity_name2=name ARROW expr=expression
	{breakElement=new BreakElement($quantity_name1.name_,$quantity_name2.name_,$expr.expr)};

// B.7 Interfaces and Associations
interface_element_generic returns [InterfaceList.AbstractInterfaceElement element] :
	interface_constant_declaration  {$element=$interface_constant_declaration.constElement}
	| {vhdl2008}?=>(
		| v2008_interface_type_declaration {$element=$v2008_interface_type_declaration.typeDecl}
		| v2008_interface_subprogram_declaration {$element=$v2008_interface_subprogram_declaration.subprogramDecl}
		| v2008_interface_package_declaration {$element=$v2008_interface_package_declaration.packageDecl}
		);

interface_element_port returns [InterfaceList.AbstractInterfaceElement element] :
	interface_signal_declaration_port {$element=$interface_signal_declaration_port.signalElement}
	| {ams}?=>(
		ams_interface_terminal_declaration {$element=$ams_interface_terminal_declaration.terminalDecl}
		| ams_interface_quantity_declaration {$element=$ams_interface_quantity_declaration.quantityDecl}
		);
	
interface_element_procedure returns [InterfaceList.AbstractInterfaceElement element] :
	interface_variable_or_constant_declaration  {$element=$interface_variable_or_constant_declaration.element}	
	| interface_signal_declaration_procedure {$element=$interface_signal_declaration_procedure.signalElement}
	| interface_file_declaration  {$element=$interface_file_declaration.fileElement}
	| {ams}?=>(
		ams_interface_terminal_declaration {$element=$ams_interface_terminal_declaration.terminalDecl}
		| ams_interface_quantity_declaration {$element=$ams_interface_quantity_declaration.quantityDecl}
		);
		
interface_element_function returns [InterfaceList.AbstractInterfaceElement element] :
	interface_constant_declaration  {$element=$interface_constant_declaration.constElement}
	| interface_signal_declaration_function {$element=$interface_signal_declaration_function.signalElement}
	| interface_file_declaration  {$element=$interface_file_declaration.fileElement}
	| {ams}?=>(
		ams_interface_terminal_declaration {$element=$ams_interface_terminal_declaration.terminalDecl}
		| ams_interface_quantity_declaration {$element=$ams_interface_quantity_declaration.quantityDecl}
		);
		
parameter_interface_list_procedure returns [Seq[InterfaceList.AbstractInterfaceElement\] list]
@init{
	val elements=new Buffer[InterfaceList.AbstractInterfaceElement]()
} :
	e1=interface_element_procedure{elements += $e1.element} (SEMICOLON e2=interface_element_procedure {elements += $e2.element})* 
	{$list=elements.result};
		
parameter_interface_list_function returns [Seq[InterfaceList.AbstractInterfaceElement\] list]
@init{
	val elements=new Buffer[InterfaceList.AbstractInterfaceElement]()
} :
	e1=interface_element_function {elements += $e1.element} (SEMICOLON e2=interface_element_function {elements += $e2.element})* 
	{$list=elements.result};

interface_variable_or_constant_declaration returns [InterfaceList.AbstractInterfaceElement element] :
	VARIABLE identifier_list COLON interface_mode? subtype_indication (VAR_ASSIGN expression)?
		{$element=new InterfaceList.InterfaceVariableDeclaration($identifier_list.list,$interface_mode.mode,$subtype_indication.subType,$expression.expr)}
	| CONSTANT identifier_list COLON IN? subtype_indication (VAR_ASSIGN expression)? 
		{$element=new InterfaceList.InterfaceConstantDeclaration($identifier_list.list,$subtype_indication.subType,$expression.expr)}
	| identifier_list COLON interface_mode? subtype_indication (VAR_ASSIGN expression)? 
		{
		$element=if ($interface_mode.mode==InterfaceList.Mode.OUT || $interface_mode.mode==InterfaceList.Mode.IN) 
				new InterfaceList.InterfaceVariableDeclaration($identifier_list.list,$interface_mode.mode,$subtype_indication.subType,$expression.expr)
			 else new InterfaceList.InterfaceConstantDeclaration($identifier_list.list,$subtype_indication.subType,$expression.expr)			 
		};
					
interface_constant_declaration returns[InterfaceList.InterfaceConstantDeclaration constElement] :
	CONSTANT? identifier_list COLON IN? subtype_indication (VAR_ASSIGN expression)? 
	{$constElement=new InterfaceList.InterfaceConstantDeclaration($identifier_list.list,$subtype_indication.subType,$expression.expr)};

interface_signal_declaration_port returns [InterfaceList.InterfaceSignalDeclaration signalElement] :
	SIGNAL? identifier_list COLON interface_mode? subtype_indication BUS? (VAR_ASSIGN expression)?
	{$signalElement=new InterfaceList.InterfaceSignalDeclaration($identifier_list.list,$interface_mode.mode,$subtype_indication.subType,$BUS!=null,$expression.expr)};
	
interface_signal_declaration_procedure returns [InterfaceList.InterfaceSignalDeclaration signalElement] :
	SIGNAL identifier_list COLON interface_mode? subtype_indication BUS? (VAR_ASSIGN expression)?
	{$signalElement=new InterfaceList.InterfaceSignalDeclaration($identifier_list.list,$interface_mode.mode,$subtype_indication.subType,$BUS!=null,$expression.expr)};
		
interface_signal_declaration_function returns [InterfaceList.InterfaceSignalDeclaration signalElement] :
	SIGNAL identifier_list COLON IN? subtype_indication BUS? (VAR_ASSIGN expression)?
	{$signalElement=new InterfaceList.InterfaceSignalDeclaration($identifier_list.list,InterfaceList.Mode.IN,$subtype_indication.subType,$BUS!=null,$expression.expr)};
	
interface_mode returns [InterfaceList.Mode.Value mode] :
	IN {$mode=InterfaceList.Mode.IN}
	| OUT {$mode=InterfaceList.Mode.OUT}
	| INOUT {$mode=InterfaceList.Mode.INOUT}
	| BUFFER {$mode=InterfaceList.Mode.BUFFER}
	| LINKAGE {$mode=InterfaceList.Mode.LINKAGE};

interface_file_declaration returns [InterfaceList.InterfaceFileDeclaration fileElement] :
	FILE identifier_list COLON subtype_indication
	{$fileElement=new InterfaceList.InterfaceFileDeclaration($identifier_list.list,$subtype_indication.subType)};
		
ams_interface_terminal_declaration returns [InterfaceList.InterfaceTerminalDeclaration terminalDecl] :
	TERMINAL identifier_list COLON ams_subnature_indication
	{$terminalDecl=new InterfaceList.InterfaceTerminalDeclaration($identifier_list.list,$ams_subnature_indication.subNature)};

ams_interface_quantity_declaration returns [InterfaceList.InterfaceQuantityDeclaration quantityDecl] :
	QUANTITY identifier_list COLON (IN|out=OUT)? subtype_indication (VAR_ASSIGN expression)?
	{
		val mode = if ($out!=null) InterfaceList.Mode.OUT else InterfaceList.Mode.IN
		$quantityDecl=new InterfaceList.InterfaceQuantityDeclaration($identifier_list.list,mode,$subtype_indication.subType,$expression.expr)
	};
	
v2008_interface_type_declaration returns [InterfaceList.InterfaceTypeDeclaration typeDecl] : 
	TYPE identifier{$typeDecl=new InterfaceList.InterfaceTypeDeclaration($identifier.id)};

v2008_interface_subprogram_default returns [Option[SelectedName\] default] : 
	IS (selected_name {$default=Option($selected_name.name_)}| BOX{$default=None});

v2008_interface_subprogram_declaration returns [InterfaceList.AbstractInterfaceElement subprogramDecl]: 
		PROCEDURE identifier
			(PARAMETER? LPAREN parameter_interface_list_procedure RPAREN)? v2008_interface_subprogram_default?
		{$subprogramDecl=new InterfaceList.InterfaceProcedureDeclaration($identifier.id,$parameter_interface_list_procedure.list,$v2008_interface_subprogram_default.default)}
		|(PURE|impure=IMPURE)? FUNCTION designator
			(PARAMETER? LPAREN parameter_interface_list_function RPAREN)? RETURN type_mark v2008_interface_subprogram_default?
		{$subprogramDecl=new InterfaceList.InterfaceFunctionDeclaration($impure==null,$designator.id,$parameter_interface_list_function.list,$type_mark.typeName,$v2008_interface_subprogram_default.default)};
		
v2008_interface_package_declaration returns [InterfaceList.InterfacePackageDeclaration packageDecl] :
	PACKAGE identifier IS NEW selected_name
		GENERIC MAP LPAREN (association_list | BOX | DEFAULT) RPAREN
	{
		val generic=if ($association_list.list!=null) Left($association_list.list)
			    else if ($DEFAULT!=null) Right(true)
			    else Right(false)
		$packageDecl=new InterfaceList.InterfacePackageDeclaration($identifier.id,$selected_name.name_,generic)
	};

association_element returns [AssociationList.Element element] :
	((formal_part ARROW)=>formal_part ARROW)? actual_part
	{$element=new AssociationList.Element($formal_part.formal_part_,$actual_part.actual_part_) };	
		  
association_list returns [AssociationList list]
@init{
	val elements=new Buffer[AssociationList.Element]()
} :
	e1=association_element {elements += $e1.element} (COMMA e2=association_element {elements += $e2.element})*
	{$list=new AssociationList(elements.result)};	
	
formal_part returns [Either[Identifier,(SelectedName,Name.Part)\] formal_part_] :
	identifier {formal_part_ = Left($identifier.id)}
	| selected_name ((name_association_list_part)=>name_association_list_part | name_slice_part) {formal_part_ = Right(($selected_name.name_,null))};
		
actual_part returns [Either[Expression,Identifier\] actual_part_ ] :
	//could be a name(signal_name, variable_name, file_name, subprogram_name, package_name), function_name or type_mark ;could be signal_name or variable_name
	({vhdl2008}?=>INERTIAL)? expression {$actual_part_ = Left($expression.expr)} //TODO
	//|TODO {vhdl2008}?=>subtype_indication
	| OPEN {$actual_part_ = Right(toIdentifier($OPEN))};
			
// B.8 Expression and Names
condition returns [Expression con] :
	expression {$con=$expression.expr};  

expression returns [Expression expr]:
	 logical_expression {$expr=$logical_expression.expr}
	 | {vhdl2008}?=> CONDITION_OPERATOR primary {$expr=new ConditionExpression($CONDITION_OPERATOR,$primary.obj)};	

logical_expression returns [Expression expr]
@after{if ($expr==null) $expr=NoExpression}:
	r1=relation (	   
	   NAND r2=relation {$expr=new LogicalExpression($NAND,$r1.rel,LogicalExpression.Operator.NAND,$r2.rel)}
	   | NOR r2=relation {$expr=new LogicalExpression($NOR,$r1.rel,LogicalExpression.Operator.NOR,$r2.rel)}
	   | {$expr=$r1.rel} (logical_operator r2=relation {$expr=new LogicalExpression($logical_operator.pos,$expr,$logical_operator.logOp,$r2.rel)})*
	);

logical_operator returns [LogicalExpression.Operator.Value logOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
} :
	AND {$logOp=LogicalExpression.Operator.AND}
	| OR {$logOp=LogicalExpression.Operator.OR}
	| XOR {$logOp=LogicalExpression.Operator.XOR}
	| XNOR {$logOp=LogicalExpression.Operator.XNOR}
	//NAND and NOR are handled in expression
	;

relation returns [Expression rel]
@after{if ($rel==null) $rel=NoExpression} :
	s1=shift_expression {$rel=$s1.shiftExpr}
	(relational_operator s2=shift_expression {$rel=new Relation($relational_operator.pos,$s1.shiftExpr,$relational_operator.relOp,$s2.shiftExpr)})?;

relational_operator returns [Relation.Operator.Value relOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
} :
	EQ {$relOp=Relation.Operator.EQ}
	| NEQ {$relOp=Relation.Operator.NEQ}
	| LT {$relOp=Relation.Operator.LT}
	| LEQ {$relOp=Relation.Operator.LEQ}
	| GT {$relOp=Relation.Operator.GT}
	| GEQ {$relOp=Relation.Operator.GEQ}
	| {vhdl2008}?=> (MEQ {$relOp=Relation.Operator.MEQ}
			| MNEQ {$relOp=Relation.Operator.MNEQ}
			| MLT {$relOp=Relation.Operator.MLT}
			| MLEQ {$relOp=Relation.Operator.MLEQ}
			| MGT {$relOp=Relation.Operator.MGT}
			| MGEQ {$relOp=Relation.Operator.MGEQ}
			);
	
shift_expression returns [Expression shiftExpr]
@after{if ($shiftExpr==null) $shiftExpr=NoExpression} :
	s1=simple_expression { $shiftExpr=$s1.simpleExpr}
	(shift_operator s2=simple_expression {$shiftExpr=new ShiftExpression($shift_operator.pos,$s1.simpleExpr,$shift_operator.shiftOp,$s2.simpleExpr)})?;

shift_operator returns [ShiftExpression.Operator.Value shiftOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
} :
	SLL {$shiftOp=ShiftExpression.Operator.SLL}
	| SRL {$shiftOp=ShiftExpression.Operator.SRL}
	| SLA {$shiftOp=ShiftExpression.Operator.SLA}
	| SRA {$shiftOp=ShiftExpression.Operator.SRA}
	| ROL {$shiftOp=ShiftExpression.Operator.ROL}
	| ROR {$shiftOp=ShiftExpression.Operator.ROR};
		
simple_expression returns [Expression simpleExpr]
@after{if ($simpleExpr==null) $simpleExpr=NoExpression} :
	s=sign? t1=term
	{simpleExpr=if (s!=null) new SimpleExpression($s.pos,$s.signOp,$t1.term_,None,None) else $t1.term_}
	(op=adding_operator t2=term {$simpleExpr=new SimpleExpression($op.pos,None,$simpleExpr,$op.addOp,$t2.term_)})*;

sign returns [SimpleExpression.SignOperator.Value signOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
} :
	PLUS {$signOp=SimpleExpression.SignOperator.PLUS}
	| MINUS {$signOp=SimpleExpression.SignOperator.MINUS};	
		
adding_operator returns [SimpleExpression.AddOperator.Value addOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
} :	
	PLUS {$addOp=SimpleExpression.AddOperator.PLUS}
	| MINUS {$addOp=SimpleExpression.AddOperator.MINUS}
	| AMPERSAND {$addOp=SimpleExpression.AddOperator.CONCATENATION};

multiplying_operator returns [Term.Operator.Value mulOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
} :
	MUL {$mulOp=Term.Operator.MUL}
	| DIV {$mulOp=Term.Operator.DIV}
	| MOD {$mulOp=Term.Operator.MOD}
	| REM {$mulOp=Term.Operator.REM};

term returns [Expression term_]
@after{if ($term_ ==null) $term_ = NoExpression}:
	f1=factor {$term_ = $f1.factor_} 
	(multiplying_operator f2=factor {$term_ = new Term($multiplying_operator.pos,$term_,$multiplying_operator.mulOp,$f2.factor_)})*;
 
factor_operator returns [Factor.Operator.Value factorOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
} :
	ABS {$factorOp=Factor.Operator.ABS}
	| NOT {$factorOp=Factor.Operator.NOT}
	| {vhdl2008}?=>(NAND {$factorOp=Factor.Operator.NAND}
			| NOR {$factorOp=Factor.Operator.NOR}
			| AND {$factorOp=Factor.Operator.AND}
			| OR {$factorOp=Factor.Operator.OR}
			| XOR {$factorOp=Factor.Operator.XOR}
			| XNOR {$factorOp=Factor.Operator.XNOR}
			);
	
factor returns [Expression factor_]
@after{if ($factor_ ==null) $factor_ = NoExpression}:
	p1=primary{$factor_ = $p1.obj}(DOUBLESTAR p2=primary {$factor_ = new Factor($DOUBLESTAR,$p1.obj,Factor.Operator.POW,$p2.obj)})?
	| factor_operator primary {$factor_ = new Factor($factor_operator.pos,$primary.obj,$factor_operator.factorOp)};

primary returns [Expression obj]
@after{if ($obj==null) $obj=NoExpression}:
	selected_name qualified_expression[$selected_name.name_] {$obj=$qualified_expression.expr}
	| name {$obj=$name.name_}
	| literal {$obj=$literal.literal_} 
	| allocator {$obj=$allocator.newExpression}
	| aggregate {$obj=$aggregate.aggregate_}; //LPAREN expression RPAREN handled by aggregate

allocator returns [Expression newExpression] :
	NEW selected_name (
		qualified_expression[$selected_name.name_] {$newExpression=new NewExpression($NEW,Left($qualified_expression.expr))}
	 	| index_constraint? {$newExpression=new NewExpression($NEW,Right(new SubTypeIndication(None,$selected_name.name_,if ($index_constraint.ranges==null) None else Right($index_constraint.ranges),None)))}
	 	);
	
qualified_expression[SelectedName typeName] returns [QualifiedExpression expr] :
	APOSTROPHE aggregate
	{$expr=new QualifiedExpression(typeName,$aggregate.aggregate_)};

selected_name_list returns [Seq[SelectedName\] list]
@init{
	val tmpList=new Buffer[SelectedName]()
} :
	n1=selected_name {tmpList += $n1.name_} (COMMA n2=selected_name {tmpList += $n2.name_})*
	{$list=tmpList.result};
		
selected_name returns [SelectedName name_]
@init{
	val parts=new Buffer[Identifier]()
} :
	name_prefix ( name_selected_part {parts += $name_selected_part.part.identifier})*
	{$name_ =new SelectedName($name_prefix.id +: parts.result)};

name_list returns [Seq[Name\] list]
@init{
	val tmpList=new Buffer[Name]()
} :
	n1=name {tmpList += $n1.name_} (COMMA n2=name {tmpList += $n2.name_})*
	{$list=tmpList.result};
			
name returns [Name name_]
@init{
	val parts=new Buffer[Name.Part]()
} :
	name_prefix (name_part {parts += $name_part.part})* {$name_ =new Name($name_prefix.id,parts.result)}
	| {vhdl2008}?=>v2008_external_name; //TODO

name_prefix returns [Identifier id=Identifier.NoIdentifier] :
	identifier {$id=$identifier.id}
 	| STRING_LITERAL{id=toIdentifier($STRING_LITERAL)};
 	
name_part returns [Name.Part part] :
	name_selected_part {$part = $name_selected_part.part}
	| name_attribute_part {$part = $name_attribute_part.part}
	| (name_association_list_part)=>name_association_list_part {$part = $name_association_list_part.part}
	| name_slice_part {$part = $name_slice_part.part}; 
			
name_selected_part returns [Name.SelectedPart part] 
@init{$part=new Name.SelectedPart(Identifier.NoIdentifier)}:
	DOT (
	identifier{$part= new Name.SelectedPart($identifier.id)}
	| CHARACTER_LITERAL {$part= new Name.SelectedPart(toIdentifier($CHARACTER_LITERAL))}
	| STRING_LITERAL{$part= new Name.SelectedPart(toIdentifier($STRING_LITERAL))}
	| ALL{$part= new Name.SelectedPart(toIdentifier($ALL))}
	);

name_association_list_part returns [Name.AssociationListPart part] :
	LPAREN association_list RPAREN {$part=new Name.AssociationListPart($LPAREN,$association_list.list)};
				
name_slice_part returns [Name.SlicePart part] :
	LPAREN discrete_range RPAREN  {$part=new Name.SlicePart($discrete_range.discreteRange)};

name_attribute_part returns [Name.AttributePart part] :
	signature? APOSTROPHE (id=identifier|RANGE {id=toIdentifier($RANGE)} |TOLERANCE {id=toIdentifier($TOLERANCE)} |ACROSS {id=toIdentifier($ACROSS)}|THROUGH {id=toIdentifier($THROUGH)} |REFERENCE {id=toIdentifier($REFERENCE)})
	( (LPAREN)=> LPAREN expr=expression ({ams}?=>COMMA expression)* RPAREN)? {$part=new Name.AttributePart($signature.signature_,$id.id,$expr.expr)};
		
signature returns [Signature signature_] :
	LBRACKET selected_name_list? (RETURN type_mark)? RBRACKET
	{$signature_ =new Signature($LBRACKET,$selected_name_list.list,$type_mark.typeName)};	
//TODO
v2008_external_name :
	DLT (CONSTANT|SIGNAL|VARIABLE) v2008_external_pathname COLON subtype_indication DGT;
		
v2008_external_pathname :
	v2008_absolute_pathname | v2008_relative_pathname | v2008_package_pathname;
	
v2008_absolute_pathname :
	DOT (v2008_pathname_element DOT)* object_identifier=identifier;
	
v2008_relative_pathname :
	(CIRCUMFLEX DOT)* (v2008_pathname_element DOT)* object_identifier=identifier;
	
v2008_pathname_element :	
	//could be entity_identifier, component_instantiation_label, block_label, package_identifier or generate_statment_label
	element=identifier (LPAREN static_expression=expression RPAREN)?;
		
v2008_package_pathname :
	AT library_identifier=identifier DOT (package_identifier=identifier DOT)* object_identifier=identifier;
	
literal returns [Expression literal_]
@init{
	var literalType:Literal.Type.Value=null
	val position=toPosition(input.LT(1))
} :
	(
		REAL_LITERAL {literalType=Literal.Type.REAL_LITERAL}
		| INTEGER_LITERAL {literalType=Literal.Type.INTEGER_LITERAL}
		| BASED_LITERAL {literalType=Literal.Type.BASED_LITERAL}
		| CHARACTER_LITERAL {literalType=Literal.Type.CHARACTER_LITERAL}
		//| STRING_LITERAL {literalType=Literal.Type.STRING_LITERAL} handled by name_prefix
		| BIT_STRING_LITERAL {literalType=Literal.Type.BIT_STRING_LITERAL}
		| NULL {literalType=Literal.Type.NULL_LITERAL}
	)
	{$literal_ =new Literal(position,input.LT(-1).getText(),literalType)}
	({input.LA(-1)==INTEGER_LITERAL || input.LA(-1)==REAL_LITERAL || input.LA(-1)==BASED_LITERAL}?=> selected_name {$literal_ = new PhysicalLiteral($literal_.asInstanceOf[Literal],$selected_name.name_)})?;
	
physical_literal returns [PhysicalLiteral literal_]
@init{
	var text:String=null
	var literalType:Literal.Type.Value=null
	val position=toPosition(input.LT(1))
} :
	(
		INTEGER_LITERAL {text=input.LT(-1).getText(); literalType=Literal.Type.INTEGER_LITERAL}
		| REAL_LITERAL {text=input.LT(-1).getText(); literalType=Literal.Type.REAL_LITERAL}
		| BASED_LITERAL {text=input.LT(-1).getText(); literalType=Literal.Type.BASED_LITERAL}
	) selected_name
	{$literal_ =new PhysicalLiteral(position,text,$selected_name.name_,literalType)};
	
element_association returns [Aggregate.ElementAssociation element] :
	( (choices ARROW)=> choices ARROW )? expression
	{$element=new Aggregate.ElementAssociation($choices.choices_,$expression.expr)};
	
aggregate returns [Aggregate aggregate_]
@init{
	val elements=new Buffer[Aggregate.ElementAssociation]()
} :
	LPAREN e1=element_association{elements += $e1.element} (COMMA e2=element_association {elements += $e2.element})* RPAREN
	{aggregate_ =new Aggregate($LPAREN,elements.result)};

choice	returns [Choices.Choice choice_]
@init{
	val position=toPosition(input.LT(1))
} :	
	(identifier (BAR | ARROW | COMMA | SEMICOLON))=>identifier {choice_ =new Choices.Choice(position,Some(Third($identifier.id)))}
	| (simple_expression (BAR | ARROW | COMMA | SEMICOLON))=>simple_expression {choice_ =new Choices.Choice(position,Some(Second($simple_expression.simpleExpr)))}
	| discrete_range {choice_ =new Choices.Choice(position,Some(First($discrete_range.discreteRange)))}
	| OTHERS {choice_ =new Choices.Choice(position,None)};

choices returns [Seq[Choices.Choice\] choices_]
@init{
	val elements=new Buffer[Choices.Choice]()
} :
	c1=choice {elements += $c1.choice_}( BAR c2=choice {elements += $c2.choice_})*
	{$choices_ = elements.result};

/* VHDL 2008 PSL
PSL_Property_Declaration : ;
	
PSL_Sequence_Declaration : ;
	
PSL_Clock_Declaration  : ;

PSL_PSL_Directive : ;
	
PSL_Verification_Unit : ;
*/	
	
identifier_list returns [Seq[Identifier\] list]
@init{
	val identifiers=new Buffer[Identifier]()
} :
	id1=identifier {identifiers += $id1.id} (COMMA id2=identifier {identifiers += $id2.id} )* 
	{$list=identifiers.result};

identifier returns [Identifier id=Identifier.NoIdentifier] :
	(BASIC_IDENTIFIER
	| EXTENDED_IDENTIFIER)
	{$id = if (input.LA(-1) == EXTENDED_IDENTIFIER) toIdentifier(input.LT(-1),false) else toIdentifier(input.LT(-1))};
	
v2008_tool_directive : APOSTROPHE identifier GRAPHIC_CHARACTER*;

label_colon returns [Identifier label] :
	identifier COLON {$label=$identifier.id};