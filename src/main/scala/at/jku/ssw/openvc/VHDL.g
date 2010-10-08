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

grammar VHDL;

options{
	language=Java;
	memoize=true;
}
tokens{
	//includes only VHDL 2002 keywords
	//VHDL 2008 keyword are commented out
	ABS='abs';
	ACCESS='access';
	AFTER='after';
	ALIAS='alias';
	ALL='all';
	AND='and';
	ARCHITECTURE='architecture';
	ARRAY='array';
	ASSERT='assert';
	//ASSUME='assume';
	//ASSUME_GUARANTEE='assume_guarantee';
	ATTRIBUTE='attribute';
	BEGIN='begin';
	BLOCK='block';
	BODY='body';
	BUFFER='buffer';
	BUS='bus';
	CASE='case';
	COMPONENT='component';
	CONFIGURATION='configuration';
	CONSTANT='constant';
	//CONTEXT='context';
	//COVER='cover';
	//DEFAULT='default';
	DISCONNECT='disconnect';
	DOWNTO='downto';
	ELSE='else';
	ELSIF='elsif';
	END='end';
	ENTITY='entity';
	EXIT='exit';
	//FAIRNESS='fairness';
	FILE='file';
	FOR='for';
	//FORCE='force';
	FUNCTION='function';
	GENERATE='generate';
	GENERIC='generic';
	GROUP='group';
	GUARDED='guarded';
	IF='if';
	IMPURE='impure';
	IN='in';
	INERTIAL='inertial';
	INOUT='inout';
	IS='is';
	LABEL='label';
	LIBRARY='library';
	LINKAGE='linkage';
	LITERAL='literal';
	LOOP='loop';
	MAP='map';
	MOD='mod';
	NAND='nand';
	NEW='new';
	NEXT='next';
	NOR='nor';
	NOT='not';
	NULL='null';
	OF='of';
	ON='on';
	OPEN='open';
	OR='or';
	OTHERS='others';
	OUT='out';
	PACKAGE='package';
	//PARAMETER='parameter';
	PORT='port';
	POSTPONED='postponed';
	PROCEDURE='procedure';
	PROCESS='process';
	//PROPERTY='property';
	PROTECTED='protected';
	PURE='pure';
	RANGE='range';
	RECORD='record';
	REGISTER='register';
	REJECT='reject';
	//RELEASE='release';
	REM='rem';
	REPORT='report';
	//RESTRICT='restrict';
	//RESTRICT_GUARANTEE='restrict_guarantee';
	RETURN='return';
	ROL='rol';
	ROR='ror';
	SELECT='select';
	//SEQUENCE='sequence';
	SEVERITY='severity';
	SHARED='shared';
	SIGNAL='signal';
	SLA='sla';
	SLL='sll';
	SRA='sra';
	SRL='srl';
	//STRONG='strong';
	SUBTYPE='subtype';
	THEN='then';
	TO='to';
	TRANSPORT='transport';
	TYPE='type';
	UNAFFECTED='unaffected';
	UNITS='units';
	UNTIL='until';
	USE='use';
	VARIABLE='variable';
	//VMODE='vmode';
	//VPROP='vprop';
	//VUNIT='vunit';
	WAIT='wait';
	WHEN='when';
	WHILE='while';
	WITH='with';
	XNOR='xnor';
	XOR='xor';
	  	
  	//VHDL-AMS KEYWORDS
  	NATURE='nature';
  	TERMINAL='terminal';
  	QUANTITY='quantity';
  	//TOLERANCE='tolerance';
  	ACROSS='across';
  	THROUGH='through';
  	SPECTRUM='spectrum';
  	NOISE='noise';
  	SUBNATURE='subnature';
  	LIMIT='limit';
  	REFERENCE='reference';
  	//BREAK='break';
  	PROCEDURAL='procedural';
  		
  	DOUBLESTAR    = '**';
  	AMS_ASSIGN    = '==';
  	LEQ           = '<=';
  	GEQ           = '>=';
  	ARROW         = '=>';
  	NEQ           = '/=';
  	VAR_ASSIGN    = ':=';
  	BOX           = '<>';
  	DBLQUOTE      = '\"';
  	SEMICOLON     = ';';
  	COMMA         = ',';
  	AMPERSAND     = '&';
  	LPAREN        = '(';
  	RPAREN        = ')';
  	LBRACKET      = '[';
  	RBRACKET      = ']';
  	COLON         = ':';
  	MUL           = '*';
  	DIV           = '/';
  	PLUS          = '+';
  	MINUS         = '-';
  	LT            = '<';
 	GT            = '>';
  	EQ            = '=';
  	BAR           = '|';
  	DOT           = '.';
  	//BACKSLASH     = '\\';
  	//AT = '@';
  	//QMARK 	= '?';
}
@lexer::header{
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

package at.jku.ssw.openvc.ast.parser
}
@lexer::members{
	var ams=false
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

package at.jku.ssw.openvc.ast.parser

import at.jku.ssw.openvc._
import at.jku.ssw.openvc.ast._
import at.jku.ssw.openvc.ast.concurrentStatements._
import at.jku.ssw.openvc.ast.declarations._
import at.jku.ssw.openvc.ast.expressions._
import at.jku.ssw.openvc.ast.sequentialStatements._
import at.jku.ssw.openvc.ast.ams._
}
@parser::members{
	var ams=false
	
	type Buffer[A] = scala.collection.mutable.ListBuffer[A]
	
	private val syntaxErrorList = new Buffer[CompilerMessage]()
	
    	def syntaxErrors :Seq[CompilerMessage] = this.syntaxErrorList.toList

        private def toPosition(token:Token):Position = new Position(line=token.getLine(),charPosition=token.getCharPositionInLine())       
        	
	private def toIdentifier(token:Token,toLowerCase:Boolean=true):Identifier = 
    		if (token.getType()!=STRING_LITERAL && token.getType()!=CHARACTER_LITERAL){
    			new Identifier(toPosition(token),if (toLowerCase) token.getText().toLowerCase() else token.getText().replace("""\\""","\\"))   
    		}else{
    			new Identifier(toPosition(token),token.getText())
    		}

        override def displayRecognitionError(tokenNames:Array[String],e:RecognitionException) =     
      		syntaxErrorList += new CompilerMessage(position=toPosition(e.token),message=super.getErrorMessage(e, tokenNames) )

        private implicit def anyToOption[A](value:A):Option[A] = Option(value)
}
//B.1 Design File
design_file returns [DesignFile designFile]
@init{
	val units=new Buffer[DesignUnit]()
}
	:	(design_unit{units += $design_unit.designUnit})+ EOF
		{$designFile=new DesignFile(units.toList)}
		;

design_unit returns [DesignUnit designUnit]
@init{
	val libraries=new Buffer[Identifier]()
	val useClauses=new Buffer[UseClause]()
	val firstToken=input.LT(1)
}
	:	(
		library_clause {libraries ++= $library_clause.identifierList}
		|use_clause {useClauses += $use_clause.useClause}
		/*|{vhdl2008}?=>context_reference*/
		)* library_unit 
		{$designUnit=new DesignUnit(toPosition(firstToken),libraries.toList,useClauses.toList,$library_unit.libraryUnit)}
		;
	
library_unit returns [LibraryUnit libraryUnit]
	:	entity_declaration {$libraryUnit=$entity_declaration.entityDecl}
		| architecture_body {$libraryUnit=$architecture_body.archDecl}
		| package_declaration {$libraryUnit=$package_declaration.packageDecl}
		| package_body {$libraryUnit=$package_body.packageBody}
		/*| {vhdl2008}?=>package_instantiation_declaration*/
		| configuration_declaration {$libraryUnit=$configuration_declaration.configDecl}
		//| {vhdl2008}?=>v2008_context_declaration
		//| {psl}?=>PSL_Verification_Unit
		;
	
library_clause returns [Seq[Identifier\] identifierList]
@init{
	$identifierList=Seq()
}
	: LIBRARY identifier_list SEMICOLON {$identifierList=$identifier_list.list}
	; 

// VHDL 2008
/*
v2008_context_reference
	:	CONTEXT name_list SEMICOLON;

*/	
//B.2 Library Unit Declarations

generic_clause returns [InterfaceList list]
	: (GENERIC LPAREN generic_interface_list RPAREN SEMICOLON)
	  {$list = $generic_interface_list.list }
	  ;

generic_interface_list returns [InterfaceList list]
@init{
	val elements=new Buffer[InterfaceList.InterfaceConstantDeclaration]()
}
    :   decl1=interface_constant_declaration {elements += $decl1.constElement} ( SEMICOLON decl2=interface_constant_declaration {elements += $decl2.constElement})*
    	{$list=new InterfaceList(elements.toList)}
    	;
    
port_clause returns [InterfaceList list]
	: (PORT LPAREN port_interface_list RPAREN SEMICOLON)
	 {$list = $port_interface_list.list }	
	;

port_interface_list returns [InterfaceList list]
@init{
	val elements=new Buffer[InterfaceList.InterfaceSignalDeclaration]()
}
    :   decl1=interface_signal_declaration_procedure {elements += $decl1.signalElement} ( SEMICOLON decl2=interface_signal_declaration_procedure {elements += $decl2.signalElement})*
    	{$list=new InterfaceList(elements.toList)}
    	;
    		
entity_declaration returns [EntityDeclaration entityDecl]
@init{
 	val declarativeItems=new Buffer[DeclarativeItem]()
 	val concurrentStmt=new Buffer[ConcurrentStatement]()
}
	:	ENTITY start_identifier=identifier IS
			generic_clause?
			port_clause?
			(entity_declarative_item{declarativeItems += $entity_declarative_item.node})* 
		(BEGIN
			(label=label_colon? postponed=POSTPONED? (concurrent_assertion_statement[$label.label,postponed!=null] {concurrentStmt += $concurrent_assertion_statement.assertStmt}
			| concurrent_procedure_call_statement[$label.label,postponed!=null]{concurrentStmt += $concurrent_procedure_call_statement.procedureCallStmt}
			| process_statement[$label.label,postponed!=null] {concurrentStmt += $process_statement.processStmt}
			))* )?
			//| {psl}?=>PSL_PSL_Directive
		END ENTITY? end_identifier=identifier? SEMICOLON
		{$entityDecl=new EntityDeclaration($start_identifier.id,$generic_clause.list,$port_clause.list,declarativeItems.toList,concurrentStmt.toList,$end_identifier.id)}
		;
		
entity_declarative_item returns [DeclarativeItem node]
	:	
		subprogram_declartion_or_body {node=$subprogram_declartion_or_body.declOrBody}
		/*| {vhdl2008}?=>subprogram_instantiation_declaration
		| {vhdl2008}?=>package_declaration | {vhdl2008}?=>package_body
		| {vhdl2008}?=>package_instantiation_declaration */
		| type_declaration {node=$type_declaration.typeDecl}
		| subtype_declaration {node=$subtype_declaration.subTypeDecl}
		| constant_declaration {node=$constant_declaration.constantDecl}
		| signal_declaration {node=$signal_declaration.signalDecl}
		| variable_declaration {node=$variable_declaration.varDecl}
		| file_declaration {node=$file_declaration.fileDecl}
		| alias_declaration {node=$alias_declaration.aliasDecl}
		| attribute_declaration {node=$attribute_declaration.attributeDecl}
		| attribute_specification {node=$attribute_specification.node}
		| disconnection_specification {node=$disconnection_specification.disconnectSpec}
		| use_clause {node=$use_clause.useClause}
		| group_template_declaration {node=$group_template_declaration.groupTemplateDecl}
		| group_declaration {node=$group_declaration.groupDecl}
		//| {psl}?=>PSL_Property_Declaration | {psl}?=>PSL_Sequence_Declaration
		//| {psl}?=>PSL_Clock_Declaration 
		| {ams}?=>
		(
		ams_step_limit_specification {$node=$ams_step_limit_specification.stepLimitSpec}
		| ams_nature_declaration {$node=$ams_nature_declaration.natureDecl}
		| ams_subnature_declaration {$node=$ams_subnature_declaration.subnatureDecl}
		| ams_quantity_declaration {$node=$ams_quantity_declaration.quantityDecl}
		| ams_terminal_declaration {$node=$ams_terminal_declaration.terminalDecl}
		)
		;
		
architecture_body returns [ArchitectureDeclaration archDecl]
@init{
	val declarativeItems=new Buffer[DeclarativeItem]()
}
	:	ARCHITECTURE start_identifier=identifier OF selected_name IS
			(block_declarative_item{declarativeItems += $block_declarative_item.node})*
		BEGIN
			architecture_statement_list
		END ARCHITECTURE? end_identifier=identifier? SEMICOLON
		{$archDecl=new ArchitectureDeclaration($start_identifier.id,declarativeItems.toList,$selected_name.name_,$architecture_statement_list.list,$end_identifier.id)}
		;
		
configuration_declarative_item returns [DeclarativeItem node]
	:	use_clause {$node=$use_clause.useClause}
		| attribute_specification {$node=$attribute_specification.node}
		| group_declaration {$node=$group_declaration.groupDecl}
		;
		
configuration_declaration returns [ConfigurationDeclaration configDecl]
@init{
 	val declarativeItems=new Buffer[DeclarativeItem]()
}
	:	CONFIGURATION start_identifier=identifier OF selected_name IS
			(configuration_declarative_item{declarativeItems += $configuration_declarative_item.node})*
			//{vhdl2008}?=>(USE VUNIT verification_unit_name=name ( COMMA verification_unit_name=name)* SEMICOLON)*
			block_configuration
		END CONFIGURATION? end_identifier=identifier? SEMICOLON
		{$configDecl=new ConfigurationDeclaration($start_identifier.id,declarativeItems.toList,$selected_name.name_,$block_configuration.blockConfig,$end_identifier.id)}
		;

block_configuration_index returns [Either[DiscreteRange,Expression\] node]
	:	  (discrete_range)=>discrete_range {$node=Left($discrete_range.discreteRange)}
		  |expression {$node=Right($expression.expr)}
		  /*|{vhdl2008}?=>alternative_label=label*/
		  ;
	
block_specification returns [BlockConfigurationSpecification blockConfig]
		//could be a block_statement_label or generate_statement_label
	:	 (identifier)=>identifier (LPAREN block_configuration_index RPAREN)?
			{new BlockConfigurationSpecification(Right(($identifier.id,$block_configuration_index.node)))}
		 | selected_name {$blockConfig=new BlockConfigurationSpecification(Left($selected_name.name_))}
		 ;	
		 	
block_configuration returns [BlockConfiguration blockConfig]
@init{
	val useClauses=new Buffer[UseClause]()
	val configurations=new Buffer[AnyRef]()
}
	:	FOR block_specification
		(use_clause {useClauses += $use_clause.useClause})*
		(
			blockConfiguration=block_configuration {configurations += $blockConfiguration.blockConfig}
			|component_configuration {configurations += $component_configuration.componentConfig}
		)*
		END FOR SEMICOLON 
		{$blockConfig=new BlockConfiguration($block_specification.blockConfig,useClauses.toList,configurations.toList)}
		;
		
component_configuration returns [ComponentConfiguration componentConfig]
	:	FOR component_specification
			(binding_indication SEMICOLON)?
			//{vhdl2008}?=>(USE VUNIT verification_unit_name=name (COMMA verification_unit_name=name)* SEMICOLON)
			block_configuration?
		END FOR SEMICOLON
		{$componentConfig=new ComponentConfiguration($component_specification.spec,$binding_indication.indication,$block_configuration.blockConfig)} 
		;
// VHDL 2008
/*
v2008_context_declaration 
	:	CONTEXT identifier IS 
			(library_clause | use_clause[null] | v2008_context_reference)?
		END CONTEXT? identifier? SEMICOLON
		;
*/
		
//B.3 Declarations and Specifiations
package_declaration returns [PackageDeclaration packageDecl]
@init{
	val declarativeItems=new Buffer[DeclarativeItem]()
}
	:	PACKAGE start_identifier=identifier IS
			/*{vhdl2008}?=>(GENERIC LPAREN generic_interface_list=interface_list RPAREN SEMICOLON
			(GENERIC MAP LPAREN generic_association_list=association_list RPAREN SEMICOLON)?)?*/
			(package_declarative_item{declarativeItems += $package_declarative_item.node})*
		END PACKAGE? end_identifier=identifier? SEMICOLON
		{$packageDecl=new PackageDeclaration($start_identifier.id,declarativeItems.toList,$end_identifier.id)}
		;
		
package_declarative_item returns [DeclarativeItem node]
	:	subprogram_declaration {$node=$subprogram_declaration.subprogramDecl}
		/*| {vhdl2008}?=>subprogram_instantiation_declaration 
		| {vhdl2008}?=>package_declaration
		| {vhdl2008}?=>package_instantiation_declaration*/
		| type_declaration {$node=$type_declaration.typeDecl}
		| subtype_declaration {$node=$subtype_declaration.subTypeDecl}
		| constant_declaration {$node=$constant_declaration.constantDecl}
		| signal_declaration {$node=$signal_declaration.signalDecl}
		| variable_declaration {$node=$variable_declaration.varDecl}
		| file_declaration {$node=$file_declaration.fileDecl}
		| alias_declaration {$node=$alias_declaration.aliasDecl}
		| component_declaration {$node=$component_declaration.componentDecl}
		| attribute_declaration {$node=$attribute_declaration.attributeDecl}
		| attribute_specification {$node=$attribute_specification.node}
		| disconnection_specification {$node=$disconnection_specification.disconnectSpec}
		| use_clause {$node=$use_clause.useClause}
		| group_template_declaration {$node=$group_template_declaration.groupTemplateDecl}
		| group_declaration {$node=$group_declaration.groupDecl}
		| {ams}?=>
		(
		 ams_nature_declaration {$node=$ams_nature_declaration.natureDecl}
		| ams_subnature_declaration {$node=$ams_subnature_declaration.subnatureDecl}
		| ams_terminal_declaration {$node=$ams_terminal_declaration.terminalDecl}
		)
		//| {psl}?=>PSL_Property_Declaration | {psl}?=>PSL_Sequence_Declaration
		;

package_body returns [PackageBodyDeclaration packageBody]
@init{
	val declarativeItems=new Buffer[DeclarativeItem]()
}
	:	PACKAGE BODY start_identifier=identifier IS
			(package_body_declarative_item{declarativeItems += $package_body_declarative_item.node})*
		END (PACKAGE BODY)? end_identifier=identifier? SEMICOLON
		{$packageBody = new PackageBodyDeclaration($start_identifier.id,declarativeItems.toList,$end_identifier.id)}
		;
    
package_body_declarative_item returns [DeclarativeItem node]
	:	subprogram_declartion_or_body {$node=$subprogram_declartion_or_body.declOrBody}
		/*| {vhdl2008}?=>subprogram_instantiation_declaration
		| {vhdl2008}?=>package_declaration | {vhdl2008}?=>package_body
		| {vhdl2008}?=>package_instantiation_declaration*/
		| type_declaration {$node=$type_declaration.typeDecl}
		| subtype_declaration {$node=$subtype_declaration.subTypeDecl}
		| constant_declaration {$node=$constant_declaration.constantDecl}
		| variable_declaration {$node=$variable_declaration.varDecl}
		| file_declaration {$node=$file_declaration.fileDecl}
		| alias_declaration {$node=$alias_declaration.aliasDecl}
		//| {vhdl2008}?=>attribute_declaration | {vhdl2008}?=>attribute_specification
		| use_clause {$node=$use_clause.useClause}
		| attribute_specification {$node=$attribute_specification.node} //attribute_specification is not part of VHDL 2002, added to compile ghdl math_real-body.vhd
		| group_template_declaration {$node=$group_template_declaration.groupTemplateDecl}
		| group_declaration {$node=$group_declaration.groupDecl}
		;

/* VHDL 2008
package_instantiation_declaration 
	:	PACKAGE identifier IS NEW uninstantiated_package_name=name
			(GENERIC MAP LPAREN generic_association_list=association_list RPAREN)? SEMICOLON;
*/
designator returns [Identifier id]
	: identifier {$id=$identifier.id}
	  | STRING_LITERAL {$id=toIdentifier($STRING_LITERAL)} //STRING_LITERAL is a operator symbol
	  ;
	  
subprogram_specification returns [SubProgramDeclaration decl]
	:		PROCEDURE designator 
			/*{vhdl2008}?=>(GENERIC LPAREN generic_interface_list=interface_list RPAREN
			(GENERIC MAP LPAREN generic_association_list=association_list RPAREN)?)?*/
			(/*{vhdl2008}?=>PARAMETER?*/ LPAREN parameter_interface_list_procedure RPAREN )? 
			{$decl=new ProcedureDeclaration(toPosition($PROCEDURE),$designator.id,$parameter_interface_list_procedure.list)}
		| 	(PURE | i=IMPURE)? FUNCTION designator
			/*{vhdl2008}?=>(GENERIC LPAREN generic_interface_list=interface_list RPAREN
			(GENERIC MAP LPAREN generic_association_list=association_list RPAREN)?)?*/
			(/*{vhdl2008}?=>PARAMETER?*/ LPAREN parameter_interface_list_function RPAREN )? RETURN type_mark
			{$decl=new FunctionDeclaration(toPosition($FUNCTION),i==null,$designator.id,$parameter_interface_list_function.list,$type_mark.typeName)}
			;	

subprogram_declartion_or_body returns [DeclarativeItem declOrBody]
	:	subprogram_specification (subprogram_body[$subprogram_specification.decl])? SEMICOLON
		{$declOrBody=if ($subprogram_body.subProgramDef!=null) $subprogram_body.subProgramDef else $subprogram_specification.decl}	
		;	
		
subprogram_declaration returns [DeclarativeItem subprogramDecl]
	: subprogram_specification SEMICOLON
	{$subprogramDecl=$subprogram_specification.decl}
	;
	
subprogram_body[SubProgramDeclaration subprogramDecl] returns [SubProgramDefinition subProgramDef]
@init{
	val declItems=new Buffer[DeclarativeItem]()
}
	:	IS
			(subprogram_declarative_item{declItems += $subprogram_declarative_item.node})* 
		BEGIN
			sequence_of_statements
		END ({$subprogramDecl.isInstanceOf[ProcedureDeclaration]}?=>PROCEDURE | {$subprogramDecl.isInstanceOf[FunctionDeclaration]}?=>FUNCTION)? endIdent=designator?
		{
			$subProgramDef = $subprogramDecl match {
				case procDecl : ProcedureDeclaration => 
					new ProcedureDefinition($subprogramDecl.position,procDecl.identifier,procDecl.parameterInterfaceList,declItems.toList,$sequence_of_statements.list,endIdent)
				case funcDecl : FunctionDeclaration =>
					new FunctionDefinition($subprogramDecl.position,funcDecl.pure,funcDecl.identifier,funcDecl.parameterInterfaceList,funcDecl.returnType,declItems.toList,$sequence_of_statements.list,endIdent)
			}	
		}
		;
    	
subprogram_declarative_item returns [DeclarativeItem node]
	:	subprogram_declartion_or_body {$node=$subprogram_declartion_or_body.declOrBody}
		/*| {vhdl2008}?=>subprogram_instantiation_declaration
		| {vhdl2008}?=>package_declaration | {vhdl2008}?=>package_body
		| {vhdl2008}?=>package_instantiation_declaration*/
		| type_declaration {$node=$type_declaration.typeDecl}
		| subtype_declaration {$node=$subtype_declaration.subTypeDecl}
		| constant_declaration {$node=$constant_declaration.constantDecl}
		| variable_declaration {$node=$variable_declaration.varDecl}
		| file_declaration {$node=$file_declaration.fileDecl}
		| alias_declaration {$node=$alias_declaration.aliasDecl}
		| attribute_declaration {$node=$attribute_declaration.attributeDecl}
		| attribute_specification {$node=$attribute_specification.node}
		| use_clause {$node=$use_clause.useClause}
		| group_template_declaration {$node=$group_template_declaration.groupTemplateDecl}
		| group_declaration {$node=$group_declaration.groupDecl}
		;
/* VHDL 2008
subprogram_instantiation_declaration 
	:	(PROCEDURE | FUNCTION) IS
			NEW uninstantiated_subprogram_name=name signature?
				(GENERIC MAP LPAREN generic_association_list=association_list RPAREN)? SEMICOLON;
*/
type_declaration returns [AbstractTypeDeclaration typeDecl]
	:	TYPE identifier (
		IS type_definition[$identifier.id,toPosition($TYPE)] SEMICOLON {$typeDecl=$type_definition.typeDef}
		| SEMICOLON {$typeDecl=new IncompleteTypeDeclaration(toPosition($TYPE),$identifier.id)} 
		)
		;

type_definition[Identifier id,Position pos] returns [AbstractTypeDeclaration typeDef]
	:	enumeration_type_definition[$id,$pos] {$typeDef=$enumeration_type_definition.enumTypeDef}
		| (RANGE range UNITS) => physical_type_definition[$id,$pos] {$typeDef=$physical_type_definition.physicalTypeDef}
		| integer_or_floating_point_type_definition[$id,$pos] {$typeDef=$integer_or_floating_point_type_definition.scalarTypeDef}
		| array_type_definition[$id,$pos] {$typeDef=$array_type_definition.arrayTypeDef}
		| record_type_definition[$id,$pos] {$typeDef=$record_type_definition.recordTypeDef}
		| access_type_definition[$id,$pos] {$typeDef=$access_type_definition.accessTypeDef}
		| file_type_definition[$id,$pos] {$typeDef=$file_type_definition.fileTypeDef}
		| (PROTECTED BODY)=>protected_type_body[$id,$pos] {$typeDef=$protected_type_body.protectedTypeBody}
		| protected_type_declaration[$id,$pos] {$typeDef=$protected_type_declaration.protectedTypeDecl}
		;
		
ams_nature_declaration returns [AbstractTypeDeclaration natureDecl]
	:	NATURE identifier IS ams_nature_definition[$identifier.id,toPosition($NATURE)] SEMICOLON
		{$natureDecl=$ams_nature_definition.natureDef}
		;

ams_terminal_declaration returns [TerminalDeclaration terminalDecl]
	:	TERMINAL identifier_list COLON ams_subnature_indication SEMICOLON
		{$terminalDecl=new TerminalDeclaration(toPosition($TERMINAL),$identifier_list.list,$ams_subnature_indication.subNature)}
		;
	
ams_nature_definition[Identifier id,Position pos] returns [AbstractTypeDeclaration natureDef]
	:	ams_scalar_nature_definition[$id,pos] {$natureDef=$ams_scalar_nature_definition.natureDef}
		| ams_array_nature_definition[$id,pos] {$natureDef=$ams_array_nature_definition.natureDef}
		| ams_record_nature_definition[$id,pos] {$natureDef=$ams_record_nature_definition.natureDef}
		;

ams_quantity_declaration returns [AbstractQuantityDeclaration quantityDecl]
  : (ams_free_quantity_declaration)=> ams_free_quantity_declaration {$quantityDecl=$ams_free_quantity_declaration.quantityDecl}
  | (ams_branch_quantity_declaration)=> ams_branch_quantity_declaration {$quantityDecl=$ams_branch_quantity_declaration.quantityDecl}
  | ams_source_quantity_declaration {$quantityDecl=$ams_source_quantity_declaration.quantityDecl}
  ;
 
 ams_free_quantity_declaration returns [FreeQuantityDeclaration quantityDecl]
 	:	QUANTITY identifier_list COLON subtype_indication (VAR_ASSIGN expression)? SEMICOLON
 		{$quantityDecl=new FreeQuantityDeclaration(toPosition($QUANTITY),$identifier_list.list,$subtype_indication.subType,$expression.expr)}
  		;
  
ams_branch_quantity_declaration returns [BranchQuantityDeclaration quantityDecl]
	:	QUANTITY ( (ams_across_aspect)=> across_aspect=ams_across_aspect)? through_aspect=ams_through_aspect? terminal_aspect=ams_terminal_aspect SEMICOLON
	{
 		$quantityDecl=new BranchQuantityDeclaration(toPosition($QUANTITY),$across_aspect.across_aspect._1,$across_aspect.across_aspect._2,$across_aspect.across_aspect._3,
 				$through_aspect.through_aspect._1,$through_aspect.through_aspect._2,$through_aspect.through_aspect._3,
 				$terminal_aspect.terminal_aspect._1,$terminal_aspect.terminal_aspect._2)
	 }
 	 ;
  
ams_source_quantity_declaration returns [SourceQuantityDeclaration quantityDecl]
  	: 	QUANTITY identifier_list COLON subtype_indication source_aspect=ams_source_aspect SEMICOLON
  		{$quantityDecl=new SourceQuantityDeclaration(toPosition($QUANTITY),$identifier_list.list,$subtype_indication.subType,$source_aspect.source_aspect._1,$source_aspect.source_aspect._2,$source_aspect.source_aspect._3)}
  		;

ams_across_aspect returns [Tuple3[Seq[Identifier\],Expression,Expression\] across_aspect]
	:	identifier_list (TOLERANCE toleranceExpression=expression)? (VAR_ASSIGN defaultExpression=expression)? ACROSS
		{$across_aspect=($identifier_list.list,$toleranceExpression.expr,$defaultExpression.expr)}
		;
	
ams_through_aspect returns [Tuple3[Seq[Identifier\],Expression,Expression\] through_aspect]
	:	identifier_list (TOLERANCE toleranceExpression=expression)? (VAR_ASSIGN defaultExpression=expression)? THROUGH
		{$through_aspect=($identifier_list.list,$toleranceExpression.expr,$defaultExpression.expr)}
		;
	
ams_terminal_aspect returns [Tuple2[Name,Name\] terminal_aspect]
	:	plus_terminal_name=name (TO minus_terminal_name=name)?
		{$terminal_aspect=($plus_terminal_name.name_,$minus_terminal_name.name_)}
		;
	
ams_source_aspect returns [Tuple3[Expression,Expression,Expression\] source_aspect]
	:	SPECTRUM magnitude_simple_expression=simple_expression COMMA phase_simple_expression=simple_expression 
		| NOISE power_simple_expression=simple_expression
		{$source_aspect=($magnitude_simple_expression.simpleExpr,$phase_simple_expression.simpleExpr,$power_simple_expression.simpleExpr)}
		;

	
constant_declaration returns [ConstantDeclaration constantDecl]
	:	CONSTANT identifier_list COLON subtype_indication (VAR_ASSIGN expression)? SEMICOLON 
		{$constantDecl=new ConstantDeclaration(toPosition($CONSTANT),$identifier_list.list,$subtype_indication.subType,$expression.expr)}
		;
	
signal_declaration returns [SignalDeclaration signalDecl]
	:	SIGNAL identifier_list COLON subtype_indication (reg=REGISTER|bus=BUS)? (VAR_ASSIGN expression)? SEMICOLON
		{
			val signalType=
				if(reg!=null) Some(SignalDeclaration.Type.REGISTER)
				else if (bus!=null) Some(SignalDeclaration.Type.BUS)
				else None
			$signalDecl=new SignalDeclaration(toPosition($SIGNAL),$identifier_list.list,$subtype_indication.subType,signalType,$expression.expr)
		}
		;
	
variable_declaration returns [VariableDeclaration varDecl]
	:	SHARED? VARIABLE identifier_list COLON subtype_indication (VAR_ASSIGN expression)? SEMICOLON
		{$varDecl=new VariableDeclaration(toPosition($VARIABLE),$SHARED!=null,$identifier_list.list,$subtype_indication.subType,$expression.expr)}
		;
	
file_declaration returns [FileDeclaration fileDecl]
	:	FILE identifier_list COLON subtype_indication ((OPEN file_open_kind_expression=expression)? IS file_logical_name=expression)? SEMICOLON
		{$fileDecl=new FileDeclaration(toPosition($FILE),$identifier_list.list,$subtype_indication.subType,$file_open_kind_expression.expr,$file_logical_name.expr)}
		;
	
alias_declaration returns [AliasDeclaration aliasDecl]
	:	ALIAS alias_designator (COLON subtype_indication)? IS name signature? SEMICOLON
		{$aliasDecl=new AliasDeclaration(toPosition($ALIAS),$alias_designator.id,$subtype_indication.subType,$name.name_,$signature.signature_)}
		; 

alias_designator returns [Identifier id]
	:	(identifier {$id=$identifier.id}
		|CHARACTER_LITERAL{$id=toIdentifier($CHARACTER_LITERAL)}
		|STRING_LITERAL{$id=toIdentifier($STRING_LITERAL)}
		);	
	
component_declaration returns [ComponentDeclaration componentDecl]
	:	component=COMPONENT start_identifier=identifier IS?
			generic_clause?
			port_clause?
		END COMPONENT end_identifier=identifier? SEMICOLON
		{$componentDecl=new ComponentDeclaration(toPosition($component),$start_identifier.id,$generic_clause.list,$port_clause.list,$end_identifier.id)}
		;

attribute_declaration returns [AttributeDeclaration attributeDecl]
	:	ATTRIBUTE identifier COLON type_mark SEMICOLON 
		{$attributeDecl=new AttributeDeclaration(toPosition($ATTRIBUTE),$identifier.id,$type_mark.typeName)}
		;
	
attribute_specification returns [AttributeSpecification node]
	:	ATTRIBUTE identifier OF entity_name_list COLON entity_class IS expression SEMICOLON 
		{$node=new AttributeSpecification(toPosition($ATTRIBUTE),$identifier.id,$entity_name_list.list,$entity_class.entityClass,$expression.expr)}
		;
		
entity_designator returns [(Identifier,Option[Signature\]) designator]
@init{
	var id:Identifier=null
}
	:	(identifier {id=$identifier.id}
		|CHARACTER_LITERAL{id=toIdentifier($CHARACTER_LITERAL)}
		|STRING_LITERAL{id=toIdentifier($STRING_LITERAL)}
		) signature?
		{$designator=(id,Option($signature.signature_))}
		;
	
entity_name_list returns [Either[Seq[(Identifier,Option[Signature\])\],Identifier\] list]
@init{
	val elements=new Buffer[(Identifier,Option[Signature])]()
}
	:	 e1=entity_designator {elements += $e1.designator}(COMMA e2=entity_designator {elements += $e2.designator})* {$list=Left(elements.toList)}
		| OTHERS {$list=Right(toIdentifier($OTHERS))}
		| ALL {$list=Right(toIdentifier($ALL))}
		;

entity_class returns [EntityClass.Value entityClass]
	:	ENTITY {$entityClass=EntityClass.ENTITY}
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
		| {ams}?=>( 
		  NATURE {$entityClass=EntityClass.NATURE}
		| SUBNATURE {$entityClass=EntityClass.SUBNATURE}
		| QUANTITY {$entityClass=EntityClass.QUANTITY}
		| TERMINAL {$entityClass=EntityClass.TERMINAL}
		)
		//| {vhdl2008}?=>PROPERTY 
		//| {vhdl2008}?=>SEQUENCE
		;
//TODO		
configuration_specification returns [ConfigurationSpecification configSpec]
	:	FOR component_specification
			binding_indication SEMICOLON
			/*{vhdl2008}?=>(USE VUNIT verification_unit_name=identifier (COMMA verification_unit_name=identifier) SEMICOLON)* */
		/*{vhdl2008}?=>(END FOR SEMICOLON)?*/
		{$configSpec= new ConfigurationSpecification(toPosition($FOR))}
		;
		
instantiation_list returns [Either[Seq[Identifier\],Identifier\] list]
	: identifier_list {$list=Left($identifier_list.list)}
		| OTHERS {$list=Right(toIdentifier($OTHERS))}
		| ALL {$list=Right(toIdentifier($ALL))}
		;		

component_specification returns [AnyRef spec] 
	:	instantiation_list COLON selected_name
		;

entity_aspect
	:	ENTITY entity_name=selected_name (LPAREN architecture_identifier=identifier RPAREN)? 
		| CONFIGURATION  configuration_name=selected_name 
		| OPEN 
		;
		
binding_indication returns [AnyRef indication]
	:	(USE entity_aspect)?
		generic_map_aspect?
		port_map_aspect?
		;

disconnection_specification returns [DisconnectionSpecification disconnectSpec]
	:	DISCONNECT (selected_name_list | id=OTHERS | id=ALL) COLON type_mark AFTER expression SEMICOLON
		{
			val signal_list = if (id==null) Left($selected_name_list.list) else Right(toIdentifier(id))
			$disconnectSpec= new DisconnectionSpecification(toPosition($DISCONNECT),signal_list,$type_mark.typeName,$expression.expr)
		}
		;

ams_step_limit_specification returns [StepLimitSpecification stepLimitSpec]
	:	LIMIT (selected_name_list | id=OTHERS | id=ALL ) COLON type_mark WITH expression SEMICOLON	
		{
			val signal_list = if (id==null) Left($selected_name_list.list) else Right(toIdentifier(id))
			$stepLimitSpec= new StepLimitSpecification(toPosition($LIMIT),signal_list,$type_mark.typeName,$expression.expr)
		}
		;

entity_class_entry returns [GroupTemplateDeclaration.Element entry]
	 : 	entity_class BOX?
		{entry = new GroupTemplateDeclaration.Element($entity_class.entityClass,$BOX!=null)}
		;
		
group_template_declaration returns [GroupTemplateDeclaration groupTemplateDecl]
@init{
	val elements=new Buffer[GroupTemplateDeclaration.Element]()
}
	:	GROUP identifier IS LPAREN e1=entity_class_entry {elements += $e1.entry}(COMMA e2=entity_class_entry {elements += $e2.entry})*  RPAREN SEMICOLON
		{$groupTemplateDecl=new GroupTemplateDeclaration(toPosition($GROUP),$identifier.id,elements.toList)}
		;
		
		
group_declaration returns [GroupDeclaration groupDecl]
	:	GROUP identifier COLON selected_name LPAREN group_constituent_list RPAREN SEMICOLON
		{$groupDecl=new GroupDeclaration(toPosition($GROUP),$identifier.id,$selected_name.name_,$group_constituent_list.list)}
		;
	
group_constituent returns [Either[Name,Identifier\] constituent]
    :   name {$constituent=Left($name.name_)}
    |   CHARACTER_LITERAL{$constituent=Right(toIdentifier($CHARACTER_LITERAL))}
    ;

group_constituent_list returns [Seq[Either[Name,Identifier\]\] list]
@init{
	val elements=new Buffer[Either[Name,Identifier]]()
}
    :   c1=group_constituent{elements += $c1.constituent} ( COMMA c2=group_constituent {elements += $c2.constituent})*
    	{$list=elements.toList}
    ;
    
use_clause returns [UseClause useClause]
	:	USE selected_name_list SEMICOLON
		{$useClause=new UseClause(toPosition($USE),$selected_name_list.list)}
		;
	
	
// B.4 Type Definitions
enumeration_literal returns [Identifier id]
	:	identifier {$id=$identifier.id}
		|CHARACTER_LITERAL {$id=toIdentifier($CHARACTER_LITERAL)}
		;
	
enumeration_type_definition[Identifier id,Position pos] returns [EnumerationTypeDefinition enumTypeDef]
@init{
	val elements=new Buffer[Identifier]()
}
	:	LPAREN e1=enumeration_literal {elements += $e1.id}(COMMA e2=enumeration_literal {elements += $e2.id})* RPAREN
		{$enumTypeDef=new EnumerationTypeDefinition($pos,$id,elements.toList)}
		;
	
integer_or_floating_point_type_definition[Identifier id,Position pos] returns [IntegerOrFloatingPointTypeDefinition scalarTypeDef]
	:	RANGE range
		{$scalarTypeDef=new IntegerOrFloatingPointTypeDefinition($pos,$id,$range.range_)}
		;
			
physical_type_definition[Identifier id,Position pos] returns [PhysicalTypeDefinition physicalTypeDef]
@init{
	val elements=new Buffer[PhysicalTypeDefinition.Element]()
}
	:	RANGE r=range
		UNITS
			baseIdent=identifier SEMICOLON
			(
			idx=identifier EQ physical_literal SEMICOLON 
			{elements += new PhysicalTypeDefinition.Element($idx.id,$physical_literal.literal_)}
			)*
		END UNITS endIdent=identifier?
		{$physicalTypeDef=new PhysicalTypeDefinition($pos,$id,$r.range_,$baseIdent.id,elements.toList,$endIdent.id)}
		;		

index_subtype_definition returns [SelectedName typeMark]
	: 	type_mark RANGE BOX {$typeMark=$type_mark.typeName}
		;
				
array_type_definition[Identifier id,Position pos] returns [AbstractArrayTypeDefinition arrayTypeDef]
@init{
	val unConstraintList=new Buffer[SelectedName]()
}
	:	ARRAY (
		(LPAREN index_subtype_definition)=>LPAREN type1=index_subtype_definition {unConstraintList += $type1.typeMark} (COMMA type2=index_subtype_definition {unConstraintList += $type2.typeMark})* RPAREN OF subType=subtype_indication
		| index_constraint OF subType=subtype_indication
		)
		{
			if (unConstraintList.isEmpty) $arrayTypeDef=new ConstrainedArrayTypeDefinition($pos,$id,$index_constraint.ranges,$subType.subType)
			else $arrayTypeDef=new UnconstrainedArrayTypeDefinition($pos,$id,unConstraintList.toList,$subType.subType)
		}
		;
		
record_type_definition[Identifier id,Position pos] returns [RecordTypeDefinition recordTypeDef]
@init{
	val elements=new Buffer[RecordTypeDefinition.Element]()
}
	:	RECORD 
			(
			identifier_list COLON subtype_indication SEMICOLON
			{elements += new RecordTypeDefinition.Element($identifier_list.list, $subtype_indication.subType)}
			)+
		END RECORD identifier?
		{$recordTypeDef=new RecordTypeDefinition($pos,$id,elements.toList,$identifier.id)}
		;

access_type_definition[Identifier id,Position pos] returns [AccessTypeDefinition accessTypeDef]
	:	ACCESS subtype_indication 
		{$accessTypeDef=new AccessTypeDefinition($pos,$id,$subtype_indication.subType)}
		;
	
file_type_definition[Identifier id,Position pos] returns [FileTypeDefinition fileTypeDef]
	:	FILE OF type_mark 
		{$fileTypeDef=new FileTypeDefinition($pos,$id,$type_mark.typeName)}
		;

ams_scalar_nature_definition[Identifier id,Position pos] returns [ScalarNatureDefinition natureDef]
	:	t1=type_mark ACROSS t2=type_mark THROUGH identifier REFERENCE
		{$natureDef=new ScalarNatureDefinition(pos,$id,$t1.typeName,$t2.typeName,$identifier.id)}
		;

ams_array_nature_definition[Identifier id,Position pos] returns [AbstractArrayNatureTypeDefinition natureDef]
@init{
	val unConstraintList=new Buffer[SelectedName]()
}
	:	ARRAY (
		(LPAREN index_subtype_definition)=>LPAREN type1=index_subtype_definition {unConstraintList += $type1.typeMark} (COMMA type2=index_subtype_definition {unConstraintList += $type2.typeMark})* RPAREN OF subType=ams_subnature_indication
		| index_constraint OF subNature=ams_subnature_indication
		)
		{
			if (unConstraintList.isEmpty) $natureDef=new ConstrainedArrayNatureTypeDefinition($pos,$id,$index_constraint.ranges,$subNature.subNature)
			else $natureDef=new UnconstrainedArrayNatureTypeDefinition($pos,$id,unConstraintList.toList,$subNature.subNature)
		}
		;

ams_record_nature_definition[Identifier id,Position pos] returns [RecordNatureDefinition natureDef]
@init{
	val elements=new Buffer[RecordNatureDefinition.Element]()
}
	:	RECORD
			(
			identifier_list COLON ams_subnature_indication SEMICOLON
			{elements += new RecordNatureDefinition.Element($identifier_list.list, $ams_subnature_indication.subNature)}
			)+
		END RECORD identifier?
		{$natureDef=new RecordNatureDefinition(pos,$id,elements.toList,$identifier.id)}
		;

ams_subnature_declaration returns [SubNatureDeclaration subnatureDecl]
	:	 SUBNATURE identifier IS ams_subnature_indication SEMICOLON
		 {$subnatureDecl= new SubNatureDeclaration(toPosition($SUBNATURE),$identifier.id,$ams_subnature_indication.subNature)}
		 ;
	
ams_subnature_indication returns [SubNatureIndication subNature]
	:	ams_nature_mark index_constraint? (TOLERANCE e1=expression ACROSS e2=expression THROUGH)?
		{$subNature=new SubNatureIndication($ams_nature_mark.typeName,$index_constraint.ranges,$e1.expr,$e2.expr)}
		;
	
ams_nature_mark returns [SelectedName typeName]
	:	 selected_name {$typeName=$selected_name.name_}
		;
		
protected_type_declaration[Identifier id,Position pos] returns [ProtectedTypeDeclaration protectedTypeDecl]
@init{
	val items=new Buffer[DeclarativeItem]()
}
	:	PROTECTED
			(protected_type_declarative_item{items += $protected_type_declarative_item.node})*
		END PROTECTED identifier?
		{$protectedTypeDecl=new ProtectedTypeDeclaration($pos,$id,items.toList,$identifier.id)}
		;
		
protected_type_declarative_item returns [DeclarativeItem node]
	:	subprogram_declaration {$node=$subprogram_declaration.subprogramDecl}
		//| {vhdl2008}?=>subprogram_instantiation_declaration
		| attribute_specification {$node=$attribute_specification.node}
		| use_clause {$node=$use_clause.useClause}
		;

protected_type_body[Identifier id,Position pos] returns [ProtectedTypeBodyDeclaration protectedTypeBody]
@init{
	val items=new Buffer[DeclarativeItem]()
}
	:	PROTECTED BODY
			(protected_type_body_declarative_item{items += $protected_type_body_declarative_item.node})*
		END PROTECTED BODY identifier?
		{$protectedTypeBody=new ProtectedTypeBodyDeclaration($pos,$id,items.toList,$identifier.id)}
		;
		
protected_type_body_declarative_item returns [DeclarativeItem node]
	:	subprogram_declartion_or_body {$node=$subprogram_declartion_or_body.declOrBody}
		/*| {vhdl2008}?=>subprogram_instantiation_declaration
		| {vhdl2008}?=>package_declaration | {vhdl2008}?=>package_body
		| {vhdl2008}?=>package_instantiation_declaration*/
		| type_declaration {$node=$type_declaration.typeDecl}
		| subtype_declaration {$node=$subtype_declaration.subTypeDecl}
		| constant_declaration {$node=$constant_declaration.constantDecl}
		| variable_declaration{$node=$variable_declaration.varDecl}
		| file_declaration {$node=$file_declaration.fileDecl}
		| alias_declaration {$node=$alias_declaration.aliasDecl}
		| attribute_declaration {$node=$attribute_declaration.attributeDecl}
		| attribute_specification {$node=$attribute_specification.node}
		| use_clause {$node=$use_clause.useClause}
		| group_template_declaration {$node=$group_template_declaration.groupTemplateDecl}
		| group_declaration {$node=$group_declaration.groupDecl}
		;
		
subtype_declaration returns [SubTypeDeclaration subTypeDecl]
	:	SUBTYPE identifier IS subtype_indication SEMICOLON
		{$subTypeDecl=new SubTypeDeclaration(toPosition($SUBTYPE),$identifier.id,$subtype_indication.subType)}
		;
		
subtype_indication returns [SubTypeIndication subType]
	:	n1=selected_name n2=selected_name? constraint? //TODO {ams}?=>((TOLERANCE expression)?)
		{
			$subType=if (n2!=null) new SubTypeIndication($n1.name_,$n2.name_,$constraint.constraint_)
				else new SubTypeIndication(None,$n1.name_,$constraint.constraint_)
		}

		;
  
direction returns [Range.Direction.Value rangeDirection]
	:	TO {$rangeDirection=Range.Direction.To}
		|DOWNTO {$rangeDirection=Range.Direction.Downto}
		;

range_constraint returns [Range rangeContraint]
	:	RANGE range {$rangeContraint=$range.range_}
		;

index_constraint returns [Seq[DiscreteRange\] ranges]
@init{
	val list=new Buffer[DiscreteRange]()
}
	:	LPAREN d1=discrete_range{list += d1} (COMMA  d2=discrete_range {list += d2})* RPAREN
		{$ranges = list.toList}
		;
		
range returns [Range range_]
	:	(
		(simple_expression direction)=> from=simple_expression direction to=simple_expression 
		| name
		)
		{$range_ =new Range($from.simpleExpr,$direction.rangeDirection,$to.simpleExpr,$name.name_)}
		;

constraint returns [Either[Range,Seq[DiscreteRange\]\] constraint_]
	:	range_constraint {$constraint_ =Left($range_constraint.rangeContraint)}
		| index_constraint {$constraint_ =Right($index_constraint.ranges)}
		//| {vhdl2008}?=>record_constraint
		;

/* VHDL 2008	
record_constraint 
	:	LPAREN (record_element_identifier=identifier (array_constraint | record_constraint)) (COMMA (record_element_identifier=identifier (array_constraint | record_constraint)))* RPAREN
		;
*/

discrete_range returns [DiscreteRange discreteRange]
	:
		(selected_name RANGE)=>discrete_subtype_indication=subtype_indication {$discreteRange=new DiscreteRange(Right($discrete_subtype_indication.subType))}
		| range {$discreteRange=new DiscreteRange(Left($range.range_))}
		;		

type_mark returns [SelectedName typeName]
	:	selected_name {$typeName=$selected_name.name_} // could be type_name or subtype_name;
		;

// B.5 Concurrent Statements
//was concurrent_statement
architecture_statement_list returns [Seq[ConcurrentStatement\] list] 
@init{
	val statementList=new Buffer[ConcurrentStatement]()
	list=List()
}
	: (architecture_statement {statementList += $architecture_statement.stmt} )*
	  {$list=statementList.toList}
	;

architecture_statement returns [ConcurrentStatement stmt]
	:	  label=label_colon (
			(architecture_statement_with_label[null])=>architecture_statement_with_label[$label.label] {$stmt=$architecture_statement_with_label.stmt}
			|architecture_statement_optional_label[$label.label] {$stmt=$architecture_statement_optional_label.stmt}
			)
		| architecture_statement_optional_label[$label.label] {$stmt=$architecture_statement_optional_label.stmt}
		;

architecture_statement_with_label[Identifier label] returns [ConcurrentStatement stmt]
	:	component_instantiation_statement[$label] {$stmt=$component_instantiation_statement.stmt}
		| block_statement[$label] {$stmt=$block_statement.blockStmt}
		| generate_statement[$label] {$stmt=$generate_statement.generateStmt}
		;
				
architecture_statement_optional_label[Identifier label] returns [ConcurrentStatement stmt]
	:	
		(postponed=POSTPONED? (process_statement[$label,postponed!=null] {$stmt=$process_statement.processStmt}
		| concurrent_assertion_statement[$label,postponed!=null] {$stmt=$concurrent_assertion_statement.assertStmt}
		| (target LEQ | WITH)=>concurrent_signal_assignment_statement[$label,postponed!=null] {$stmt=$concurrent_signal_assignment_statement.node}
		| concurrent_procedure_call_statement[$label,postponed!=null] {$stmt=$concurrent_procedure_call_statement.procedureCallStmt}
		)
		) 
		| {ams}?=>(ams_concurrent_break_statement[$label] {$stmt=$ams_concurrent_break_statement.breakStmt}
		   	//| ams_simultaneous_statement[$label] {$stmt=$ams_simultaneous_statement.stmt} //TODO
		   )
		//| {psl}?=>PSL_PSL_Directive 
		;		

generic_map_aspect returns [AssociationList list]
	:	GENERIC MAP LPAREN association_list RPAREN
		{$list=$association_list.list}
		;
		
port_map_aspect returns [AssociationList list]
	:	PORT MAP LPAREN association_list RPAREN
		{$list=$association_list.list}
		;
					
block_statement[Identifier label] returns [BlockStatement blockStmt]
@init{
	val declItems=new Buffer[DeclarativeItem]()
}
	:	block=BLOCK (LPAREN guard_expression=expression RPAREN)? IS?
			(generic_clause (generic_map_aspect SEMICOLON)?)?
			(port_clause (port_map_aspect SEMICOLON)?)?
			(block_declarative_item{declItems += $block_declarative_item.node})*
		BEGIN
			architecture_statement_list
		END BLOCK end_block_label=identifier? SEMICOLON 
		{
			$blockStmt=new BlockStatement(toPosition($block),$label,$guard_expression.expr,$generic_clause.list,$generic_map_aspect.list,$port_clause.list,$port_map_aspect.list,
				declItems.toList,$architecture_statement_list.list,$end_block_label.id)
		}
		;
				
block_declarative_item returns [DeclarativeItem node]
	:	subprogram_declartion_or_body {$node=$subprogram_declartion_or_body.declOrBody}
		/*| {vhdl2008}?=>subprogram_instantiation_declaration
		| {vhdl2008}?=>package_declaration | {vhdl2008}?=>package_body
		| {vhdl2008}?=>package_instantiation_declaration*/
		| type_declaration {$node=$type_declaration.typeDecl}
		| subtype_declaration {$node=$subtype_declaration.subTypeDecl}
		| constant_declaration {$node=$constant_declaration.constantDecl}
		| signal_declaration {$node=$signal_declaration.signalDecl}
		| variable_declaration {$node=$variable_declaration.varDecl}
		| file_declaration {$node=$file_declaration.fileDecl}
		| alias_declaration {$node=$alias_declaration.aliasDecl}
		| component_declaration {$node=$component_declaration.componentDecl}
		| attribute_declaration {$node=$attribute_declaration.attributeDecl}
		| attribute_specification {$node=$attribute_specification.node}
		| configuration_specification {$node=$configuration_specification.configSpec}
		| disconnection_specification {$node=$disconnection_specification.disconnectSpec}
		| use_clause {$node=$use_clause.useClause}
		| group_template_declaration {$node=$group_template_declaration.groupTemplateDecl}
		| group_declaration {$node=$group_declaration.groupDecl}
		//| {psl}?=>PSL_Property_Declaration | {psl}?=>PSL_Sequence_Declaration
		//| {psl}?=>PSL_Clock_Declaration
		| {ams}?=>(
		ams_step_limit_specification {$node=$ams_step_limit_specification.stepLimitSpec}
		| ams_nature_declaration {$node=$ams_nature_declaration.natureDecl}
		| ams_subnature_declaration {$node=$ams_subnature_declaration.subnatureDecl}
		| ams_quantity_declaration {$node=$ams_quantity_declaration.quantityDecl}
		| ams_terminal_declaration {$node=$ams_terminal_declaration.terminalDecl}
		)
		;
		
process_statement[Identifier label,Boolean postponed] returns [ProcessStatement processStmt]
@init{
	val declItem=new Buffer[DeclarativeItem]()
}
	:	process=PROCESS (LPAREN name_list RPAREN)? IS?
			(process_declarative_item {declItem += $process_declarative_item.node})*
		BEGIN
			sequence_of_statements
		END POSTPONED? PROCESS end_process_label=identifier? SEMICOLON
		{$processStmt=new ProcessStatement(toPosition($process),$label,$postponed,$name_list.list,declItem.toList,$sequence_of_statements.list,$end_process_label.id)}
		;
    	
process_declarative_item returns [DeclarativeItem node]
	:	subprogram_declartion_or_body {$node=$subprogram_declartion_or_body.declOrBody}
		//| {vhdl2008}?=>subprogram_instantiation_declaration
		//| {vhdl2008}?=>package_declaration | {vhdl2008}?=>package_body
		//| {vhdl2008}?=>package_instantiation_declaration
		| type_declaration {$node=$type_declaration.typeDecl}
		| subtype_declaration {$node=$subtype_declaration.subTypeDecl}
		| constant_declaration {$node=$constant_declaration.constantDecl}
		| variable_declaration {$node=$variable_declaration.varDecl}
		| file_declaration {$node=$file_declaration.fileDecl}
		| alias_declaration {$node=$alias_declaration.aliasDecl}
		| attribute_declaration {$node=$attribute_declaration.attributeDecl}
		| attribute_specification {$node=$attribute_specification.node}
		| use_clause {$node=$use_clause.useClause}
		| group_template_declaration {$node=$group_template_declaration.groupTemplateDecl}
		| group_declaration {$node=$group_declaration.groupDecl}
		;
		
concurrent_procedure_call_statement[Identifier label,Boolean postponed] returns [ConcurrentProcedureCallStatement procedureCallStmt]
	:	procedure_name=selected_name (LPAREN association_list RPAREN)? SEMICOLON
		{$procedureCallStmt=new ConcurrentProcedureCallStatement($label,$postponed,$procedure_name.name_,$association_list.list)}
		;
		
concurrent_assertion_statement[Identifier label,Boolean postponed] returns [ConcurrentAssertionStatement assertStmt]
	:	ASSERT condition  (REPORT report_expression=expression)? (SEVERITY severity_expression=expression)? SEMICOLON
		{$assertStmt=new ConcurrentAssertionStatement(toPosition($ASSERT),$label,$postponed,$condition.con,$report_expression.expr,$severity_expression.expr)}
		;
							
concurrent_signal_assignment_statement[Identifier label,Boolean postponed] returns [ConcurrentSignalAssignmentStatement node]
	:	 conditional_signal_assignment[$label,$postponed]{$node=$conditional_signal_assignment.signalAssignment}
		|selected_signal_assignment[$label,$postponed]{$node=$selected_signal_assignment.signalAssignment}
		 	 ;

 conditional_signal_assignment[Identifier label,Boolean postponed] returns [ConcurrentConditionalSignalAssignment signalAssignment]
 @init{
 	val elements=new Buffer[ConcurrentConditionalSignalAssignment.When]()
 }
	:	target LEQ GUARDED? delay_mechanism?
  			conditional_waveforms[elements]
			SEMICOLON
		{$signalAssignment=new ConcurrentConditionalSignalAssignment(toPosition($LEQ),$label,$postponed,$target.target_,$GUARDED!=null,$delay_mechanism.mechanism,elements.toList)}
			;

conditional_waveforms[Buffer[ConcurrentConditionalSignalAssignment.When\] elements]
    :   waveform ( WHEN condition ( ELSE conditional_waveforms[elements] )? )? {new ConcurrentConditionalSignalAssignment.When($waveform.waveForm,$condition.con) +=: elements}
    ;
 
selected_waveform returns [ConcurrentSelectedSignalAssignment.When whenClause]
 	:	waveform WHEN choices {whenClause = new ConcurrentSelectedSignalAssignment.When($waveform.waveForm,$choices.choices_)}
 		;   
 		
selected_signal_assignment[Identifier label,Boolean postponed] returns [ConcurrentSelectedSignalAssignment signalAssignment]
@init{
	val elements=new Buffer[ConcurrentSelectedSignalAssignment.When]()
}
	:	WITH expression SELECT /*{vhdl2008}?=>QMARK?*/
			target LEQ GUARDED? delay_mechanism? 
			s1=selected_waveform {elements += $s1.whenClause}(COMMA s2=selected_waveform{elements += $s2.whenClause})* SEMICOLON
			{$signalAssignment=new ConcurrentSelectedSignalAssignment(toPosition($WITH),$label,$postponed,$expression.expr,$target.target_,$GUARDED!=null,$delay_mechanism.mechanism,elements.toList)}
			;
			
target returns [Target target_]
  : name {$target_ = new Target(Left($name.name_))}
  | aggregate {$target_ = new Target(Right($aggregate.aggregate_))} 
  ;	
  		
component_instantiation_statement[Identifier label] returns [ComponentInstantiationStatement stmt]
@init{
	var componentType:ComponentInstantiationStatement.ComponentType.Value=null
	val firstToken=input.LT(1)
}
	:	( COMPONENT? n=selected_name {componentType=ComponentInstantiationStatement.ComponentType.COMPONENT }
		  | ENTITY n=selected_name (LPAREN architecture_identifier=identifier RPAREN)? {componentType=ComponentInstantiationStatement.ComponentType.ENTITY}
		  | CONFIGURATION n=selected_name {componentType=ComponentInstantiationStatement.ComponentType.CONFIGURATION})
		generic_map_aspect?
		port_map_aspect? SEMICOLON
		{$stmt=new ComponentInstantiationStatement(toPosition(firstToken),$label,componentType,$n.name_,$architecture_identifier.id,$generic_map_aspect.list,$port_map_aspect.list)}
		;
		
generate_statement[Identifier label] returns [ConcurrentStatement generateStmt]

	:	for_generate_statement[$label] {$generateStmt=$for_generate_statement.forGenerateStmt}
		| if_generate_statement[$label] {$generateStmt=$if_generate_statement.ifGenerateStmt}
		//| {vhdl2008}?=>v2008_case_generate_statement[$label]
	;
	
for_generate_statement[Identifier label] returns [ForGenerateStatement forGenerateStmt]
	:	FOR loopIdentifier=identifier IN discrete_range GENERATE
			body=generate_statement_body
		END GENERATE end_generate_label=identifier? SEMICOLON
		{$forGenerateStmt=new ForGenerateStatement(toPosition($FOR),$label,$loopIdentifier.id,$discrete_range.discreteRange,$body.blockItems,$body.statementList,$end_generate_label.id)}
		;
		
if_generate_statement[Identifier label] returns [IfGenerateStatement ifGenerateStmt]
	:	IF /*{vhdl2008}?=>(alternative_label=label_colon)?*/ condition GENERATE
			body=generate_statement_body
		/*{vhdl2008}?=>(ELSIF 	(alternative_label=label_colon)? condition GENERATE
			generate_statement_body)*
		{vhdl2008}?=>(ELSE 	(alternative_label=label_colon)? condition GENERATE
			generate_statement_body)?*/
		END GENERATE end_generate_label=identifier? SEMICOLON
		{$ifGenerateStmt=new IfGenerateStatement(toPosition($IF),$label,$condition.con,$body.blockItems,$body.statementList,$end_generate_label.id)}
		;
//VHDL 2008
/*v2008_case_generate_statement 
	:	start_generate_label=label_colon
		CASE expression GENERATE
			generate_statement_body[null]
		(WHEN 	(alternative_label=label_colon)? choices ARROW
			generate_statement_body[null])+
		END GENERATE (end_generate_label=label)? SEMICOLON;
*/
		
generate_statement_body returns [Seq[DeclarativeItem\] blockItems,Seq[ConcurrentStatement\] statementList]
@init{
	val tmpBockItems=new Buffer[DeclarativeItem]()
	$blockItems=List()
	$statementList=List()
}
	:	((block_declarative_item{tmpBockItems += $block_declarative_item.node})* 
	    BEGIN)?
	   	architecture_statement_list
	   	{
	   		$blockItems=tmpBockItems.toList
			$statementList=$architecture_statement_list.list
	   	}
	   //{vhdl2008}?=>((END alternative_label=label? SEMICOLON)?)
	   ;

ams_concurrent_break_statement[Identifier label] returns [ConcurrentBreakStatement breakStmt]
	:	BREAK ams_break_element_list? (ON selected_name_list)? (WHEN expression)? SEMICOLON
		{$breakStmt=new ConcurrentBreakStatement(toPosition($BREAK),$label,$ams_break_element_list.list,$selected_name_list.list,$expression.expr)}
		;

// E.6 Simultaneous Statements
ams_simultaneous_statement[Identifier label] returns [SimultaneousStatement stmt]
	:	ams_simple_simultaneous_statement[$label] {$stmt=$ams_simple_simultaneous_statement.stmt}
		| ams_simultaneous_if_statement[$label] {$stmt=$ams_simultaneous_if_statement.ifStmt}
		| ams_simultaneous_procedural_statement[$label] {$stmt=$ams_simultaneous_procedural_statement.proceduralStmt}
		| ams_simultaneous_case_statement[$label] {$stmt=$ams_simultaneous_case_statement.caseStmt}
		| ams_simultaneous_null_statement[$label] {$stmt=$ams_simultaneous_null_statement.nullStmt}
		;

ams_simultaneous_statement_list returns [Seq[SimultaneousStatement\] list]
@init{
	val tmpList=new Buffer[SimultaneousStatement]()
	list=List()
}
	:	(label_colon? ams_simultaneous_statement[$label_colon.label]{tmpList += $ams_simultaneous_statement.stmt})*
		{$list=tmpList.toList}
		;
			
ams_simple_simultaneous_statement[Identifier label] returns [SimpleSimultaneousStatement stmt]
	:	e1=simple_expression AMS_ASSIGN e2=simple_expression (TOLERANCE tolerance_expression=expression)? SEMICOLON
		{$stmt=new SimpleSimultaneousStatement($label,$e1.simpleExpr,$e2.simpleExpr,$tolerance_expression.expr)}
		;
					
ams_simultaneous_if_statement[Identifier label] returns [SimultaneousIfStatement ifStmt]
@init{
	val ifList=new Buffer[SimultaneousIfStatement.IfUsePart]()
}
	:	ifToken=IF if_condition=condition USE
			if_simultaneous_statement=ams_simultaneous_statement_list {ifList += new SimultaneousIfStatement.IfUsePart($if_condition.con,$if_simultaneous_statement.list)}
		(ELSIF elsif_condition=condition USE
			else_if_simultaneous_statement=ams_simultaneous_statement_list
			{ifList += new SimultaneousIfStatement.IfUsePart($elsif_condition.con,$else_if_simultaneous_statement.list)}
			)*
		(ELSE
			else_simultaneous_statement=ams_simultaneous_statement_list)?
		END USE end_if_label=identifier? SEMICOLON
		{$ifStmt=new SimultaneousIfStatement(toPosition($ifToken),$label,ifList.toList,$else_simultaneous_statement.list,$end_if_label.id)}
		;
						
ams_simultaneous_case_statement[Identifier label] returns [SimultaneousCaseStatement caseStmt]
@init{
	val alternatives=new Buffer[SimultaneousCaseStatement.When]()
}
	:	caseToken=CASE expression USE
			(WHEN choices ARROW ams_simultaneous_statement_list {alternatives += new SimultaneousCaseStatement.When($choices.choices_,$ams_simultaneous_statement_list.list)})+
		END CASE end_case_label=identifier? SEMICOLON
		{$caseStmt=new SimultaneousCaseStatement(toPosition($caseToken),$label,$expression.expr,alternatives.toList,$end_case_label.id)}
		;
		
ams_simultaneous_procedural_statement[Identifier label] returns [SimultaneousProceduralStatement proceduralStmt]
@init{
	val items=new Buffer[DeclarativeItem]()
}
	:	procedural=PROCEDURAL IS?
			(ams_simultaneous_procedural_declarative_item{items += $ams_simultaneous_procedural_declarative_item.node})*
		BEGIN
			sequence_of_statements
		END PROCEDURAL end_procedural_label=identifier? SEMICOLON
		{$proceduralStmt=new SimultaneousProceduralStatement(toPosition($procedural),$label,items.toList,$sequence_of_statements.list,$end_procedural_label.id)}
		;
		
ams_simultaneous_procedural_declarative_item returns [DeclarativeItem node]
	:	subprogram_declartion_or_body {$node=$subprogram_declartion_or_body.declOrBody}
		| type_declaration {$node=$type_declaration.typeDecl}
		| subtype_declaration {$node=$subtype_declaration.subTypeDecl}
		| constant_declaration {$node=$constant_declaration.constantDecl}
		| variable_declaration {$node=$variable_declaration.varDecl}
		| alias_declaration {$node=$alias_declaration.aliasDecl}
		| attribute_declaration {$node=$attribute_declaration.attributeDecl}
		| attribute_specification {$node=$attribute_specification.node}
		| use_clause {$node=$use_clause.useClause}
		| group_template_declaration {$node=$group_template_declaration.groupTemplateDecl}
		| group_declaration {$node=$group_declaration.groupDecl}
		;
	
ams_simultaneous_null_statement[Identifier label] returns [SimultaneousNullStatement nullStmt]
	:	NULL SEMICOLON
		{$nullStmt=new SimultaneousNullStatement(toPosition($NULL),$label)}
		;

// B.6 Sequential Statments
sequence_of_statements returns [Seq[SequentialStatement\] list]
@init{
	val tmpList=new Buffer[SequentialStatement]()
	list=List()
}
	:	(sequential_statement{tmpList +=$sequential_statement.stmt})*
		{$list=tmpList.toList}
		;

sequential_statement returns [SequentialStatement stmt]
	:	label=label_colon? 
		(wait_statement[$label.label] {$stmt=$wait_statement.waitStmt}
		| assertion_statement[$label.label] {$stmt=$assertion_statement.assertStmt}
		| report_statement[$label.label] {$stmt=$report_statement.reportStmt}
		| (target LEQ)=>signal_assignment_statement[$label.label] {$stmt=$signal_assignment_statement.signalAssignStmt}
		| (target VAR_ASSIGN)=>variable_assignment_statement[$label.label] {$stmt=$variable_assignment_statement.varAssignStmt}
		| if_statement[$label.label] {$stmt=$if_statement.ifStmt}
		| case_statement[$label.label] {$stmt=$case_statement.caseStmt}
		| loop_statement[$label.label] {$stmt=$loop_statement.loopStmt}
		| next_statement[$label.label] {$stmt=$next_statement.nextStmt}
		| exit_statement[$label.label] {$stmt=$exit_statement.exitStmt}
		| return_statement[$label.label] {$stmt=$return_statement.returnStmt}
		| null_statement[$label.label] {$stmt=$null_statement.nullStmt}
		| procedure_call_statement[$label.label] {$stmt=$procedure_call_statement.procedureCallStmt}
		| {ams}?=>ams_break_statement[$label.label] {$stmt=$ams_break_statement.breakStmt}
		)
		;
	
wait_statement[Identifier label] returns [WaitStatement waitStmt]	
	:	WAIT (ON name_list)? (UNTIL condition)? (FOR expression)? SEMICOLON
		{$waitStmt=new WaitStatement(toPosition($WAIT),$label,$name_list.list,$condition.con,$expression.expr)}
		;
				
assertion_statement[Identifier label] returns [AssertStatement assertStmt]
	:	ASSERT condition (REPORT report_expression=expression)? (SEVERITY severity_expression=expression)? SEMICOLON
		{$assertStmt=new AssertStatement(toPosition($ASSERT),$label,$condition.con,$report_expression.expr,$severity_expression.expr)}
		;
			     
report_statement[Identifier label] returns [ReportStatement reportStmt]
	:	REPORT report_expression=expression (SEVERITY severity_expression=expression)? SEMICOLON
		{$reportStmt=new ReportStatement(toPosition($REPORT),$label,$report_expression.expr,$severity_expression.expr)}
		;	
		     
signal_assignment_statement[Identifier label] returns [SignalAssignmentStatement signalAssignStmt]
	:	(
		simple_signal_assignment[$label] {$signalAssignStmt=$simple_signal_assignment.stmt}
		//|  {vhdl2008}?=>conditional_signal_assignment
		//|  {vhdl2008}?=>selected_signal_assignment
		)
		;
		
simple_signal_assignment[Identifier label] returns [SimpleSignalAssignmentStatement stmt]
	:	target LEQ delay_mechanism? waveform SEMICOLON
		{$stmt=new SimpleSignalAssignmentStatement(toPosition($LEQ),$label,$target.target_,$delay_mechanism.mechanism,$waveform.waveForm)}
		//|{vhdl2008}?=> name LEQ FORCE (IN|OUT)? expression SEMICOLON
		//| {vhdl2008}?=> name LEQ RELEASE (IN|OUT)? SEMICOLON
		;
		
/* VHDL 2008		
conditional_signal_assignment 
	:	conditional_waveform_assignment | conditional_force_assignment;
	
conditional_waveform_assignment 
	:	label_colon?
		target LEQ delay_mechanism?
			waveform WHEN condition
			(ELSE waveform WHEN condition)*
			(ELSE waveform)? SEMICOLON;

conditional_force_assignment 
	:	label_colon?
		name LEQ FORCE (IN|OUT)? 
			expression WHEN condition
			(ELSE expression WHEN condition)*
			(ELSE expression)? SEMICOLON;
			
selected_signal_assignment 
	:	selected_waveform_assignment | selected_force_assignment;
	
selected_waveform_assignment 
	:	label_colon?
		WITH expression SELECT QMARK?
			target LEQ delay_mechanism?
				(waveform WHEN choices COMMA)*
				waveform WHEN choices SEMICOLON;
				
	:	label_colon?
		WITH expression SELECT QMARK?
			name LEQ FORCE (IN|OUT)?
				(expression WHEN choices COMMA)*
				expression WHEN choices SEMICOLON;
*/				
delay_mechanism returns [DelayMechanism mechanism]
@after{	
	if ($time_expression.expr==null) $mechanism=new DelayMechanism(DelayMechanism.DelayType.TRANSPORT,None)
	else $mechanism=new DelayMechanism(DelayMechanism.DelayType.INERTIAL,$time_expression.expr)
}
	:	TRANSPORT 
		| (REJECT time_expression=expression)? INERTIAL
		;

waveform_element returns [Waveform.Element element]
 	: value_expression=expression (AFTER time_expression=expression)?  
 	{return new Waveform.Element($value_expression.expr,$time_expression.expr)}
 	;
	
waveform returns [Waveform waveForm]
@init{
	val elements=new Buffer[Waveform.Element]()
	val firstToken=input.LT(1)
}
	:	e1=waveform_element{elements += $e1.element} (COMMA e2=waveform_element{elements += $e2.element})*
		| UNAFFECTED 
		{$waveForm=new Waveform(toPosition(firstToken),elements.toList)}
		;
		
variable_assignment_statement[Identifier label] returns [VariableAssignmentStatement varAssignStmt]
	:	(simple_variable_assignment[$label] {$varAssignStmt=$simple_variable_assignment.stmt}
		//VHDL 2008
		//|{vhdl2008}?=>(conditional_variable_assignment
		//|{vhdl2008}?=>selected_variable_assignment
		) 
		;
		
simple_variable_assignment[Identifier label] returns [SimpleVariableAssignmentStatement stmt]
	:	target VAR_ASSIGN expression SEMICOLON
		{$stmt=new SimpleVariableAssignmentStatement(toPosition($VAR_ASSIGN),$label,$target.target_,$expression.expr)}
		;
/*	
conditional_variable_assignment 
	:	target VAR_ASSIGN 
		expression WHEN condition
		(ELSE expression WHEN condition)*
		(ELSE expression)? SEMICOLON;
				
selected_variable_assignment 
	:	WITH expression SELECT QMARK?
			target VAR_ASSIGN
				(expression WHEN choices COMMA)*
				expression WHEN choices SEMICOLON;
*/				
procedure_call_statement[Identifier label] returns [ProcedureCallStatement procedureCallStmt]
	:	procedure_name=selected_name (LPAREN association_list RPAREN)? SEMICOLON
		{$procedureCallStmt=new ProcedureCallStatement($label,$procedure_name.name_,$association_list.list)}
		;
	
if_statement[Identifier label] returns [IfStatement ifStmt]
@init{
	val ifList=new Buffer[IfStatement.IfThenPart]()
}
	:	ifToken=IF if_condition=condition THEN
			if_sequential_statement=sequence_of_statements {ifList += new IfStatement.IfThenPart($if_condition.con,$if_sequential_statement.list)}
		(ELSIF elsif_condition=condition THEN
			 elsif_sequential_statement=sequence_of_statements
			 {ifList += new IfStatement.IfThenPart($elsif_condition.con,$elsif_sequential_statement.list)}
			 )*
		(ELSE	
			else_sequential_statement=sequence_of_statements)?
		END IF end_if_label=identifier? SEMICOLON 
		{$ifStmt=new IfStatement(toPosition($ifToken),$label,ifList.toList,$else_sequential_statement.list,$end_if_label.id)}
		;

case_statement[Identifier label] returns [CaseStatement caseStmt]
@init{
	val alternatives=new Buffer[CaseStatement.When]()
}
	:	caseToken=CASE /*{vhdl2008}?=>QMARK?*/ expression IS
			(WHEN choices ARROW sequence_of_statements {alternatives += new CaseStatement.When($choices.choices_,$sequence_of_statements.list)})+
		END CASE /*{vhdl2008}?=>QMARK?*/ end_case_label=identifier? SEMICOLON
		{$caseStmt=new CaseStatement(toPosition($caseToken),$label,$expression.expr,alternatives.toList,$end_case_label.id)}
		;

iteration_scheme returns [Either[Expression,(Identifier,DiscreteRange)\] scheme]
	:	WHILE condition {$scheme=Left($condition.con)}
		| FOR identifier IN discrete_range {$scheme=Right(($identifier.id,$discrete_range.discreteRange))}
		;	
			
loop_statement[Identifier label] returns [SequentialStatement loopStmt]
@init{
	val firstToken=input.LT(1)
}
	:	stmtType=iteration_scheme? LOOP
			sequence_of_statements
		END LOOP end_loop_label=identifier? SEMICOLON
		{		
			val position=toPosition(firstToken)
			loopStmt=Option(stmtType) match {
				case Some(x)=> x match {
					case Left(condition)=>new WhileStatement(position,$label,condition,$sequence_of_statements.list,$end_loop_label.id)
					case Right((identifier,discreteRange)) =>new ForStatement(position,$label,identifier,discreteRange,$sequence_of_statements.list,$end_loop_label.id)
				}
				case None =>new LoopStatement(position,$label,$sequence_of_statements.list,$end_loop_label.id)
			}
		}
		;

next_statement[Identifier label] returns [NextStatement nextStmt]
	:	NEXT identifier? (WHEN condition)? SEMICOLON 
		{$nextStmt=new NextStatement(toPosition($NEXT),$label,$identifier.id,$condition.con)}
		;

exit_statement[Identifier label] returns [ExitStatement exitStmt]
	:	EXIT identifier? (WHEN condition)? SEMICOLON 
		{$exitStmt=new ExitStatement(toPosition($EXIT),$label,$identifier.id,$condition.con)}
		;

return_statement[Identifier label] returns [ReturnStatement returnStmt]
	:	RETURN expression? SEMICOLON 
		{$returnStmt=new ReturnStatement(toPosition($RETURN),$label,$expression.expr)}
		;
	
null_statement[Identifier label] returns [NullStatement nullStmt]
	:	NULL SEMICOLON
		{$nullStmt=new NullStatement(toPosition($NULL),$label)}
		;

ams_break_statement[Identifier label] returns [AMSBreakStatement breakStmt]
	:	BREAK ams_break_element_list? (WHEN expression)? SEMICOLON
		{$breakStmt=new AMSBreakStatement(toPosition($BREAK),$label,$ams_break_element_list.list,$expression.expr)}
		;

ams_break_element_list returns [Seq[BreakElement\] list]
@init{
	val elements=new Buffer[BreakElement]()
}
	:	e1=ams_break_element {elements += $e1.breakElement}(COMMA e2=ams_break_element {elements += $e2.breakElement})*
		{$list=elements.toList}
		;
			
ams_break_element returns [BreakElement breakElement]
	:	 (FOR quantity_name1=name USE)? quantity_name2=name ARROW expr=expression
		 {breakElement=new BreakElement($quantity_name1.name_,$quantity_name2.name_,$expr.expr)}
		 ;

// B.7 Interfaces and Associations
interface_element_procedure returns [InterfaceList.AbstractInterfaceElement element]
	:	(CONSTANT? identifier_list COLON IN?)=> interface_constant_declaration  {$element=$interface_constant_declaration.constElement}
		| (VARIABLE? identifier_list COLON (OUT|INOUT)?)=>interface_variable_declaration  {$element=$interface_variable_declaration.varElement}
		| interface_signal_declaration_procedure {$element=$interface_signal_declaration_procedure.signalElement}
		| interface_file_declaration  {$element=$interface_file_declaration.fileElement}
		| {ams}?=> (
		ams_interface_terminal_declaration {$element=$ams_interface_terminal_declaration.terminalDecl}
		|ams_interface_quantity_declaration {$element=$ams_interface_quantity_declaration.quantityDecl}
		)
		//| {vhdl2008}?=>interface_type_declaration
		//| {vhdl2008}?=>interface_subprogram_declaration
		//| {vhdl2008}?=>interface_package_declaration
		;
		
interface_element_function returns [InterfaceList.AbstractInterfaceElement element]
	:	interface_constant_declaration  {$element=$interface_constant_declaration.constElement}
		| interface_signal_declaration_function {$element=$interface_signal_declaration_function.signalElement}
		| interface_file_declaration  {$element=$interface_file_declaration.fileElement}
		| {ams}?=> (
		ams_interface_terminal_declaration {$element=$ams_interface_terminal_declaration.terminalDecl}
		|ams_interface_quantity_declaration {$element=$ams_interface_quantity_declaration.quantityDecl}
		)
		//| {vhdl2008}?=>interface_type_declaration
		//| {vhdl2008}?=>interface_subprogram_declaration
		//| {vhdl2008}?=>interface_package_declaration
		;
		
parameter_interface_list_procedure returns [InterfaceList list]
@init{
	val elements=new Buffer[InterfaceList.AbstractInterfaceElement]()
}
	:	e1=interface_element_procedure{elements += $e1.element} (SEMICOLON e2=interface_element_procedure {elements += $e2.element})* 
		{$list=new InterfaceList(elements.toList)}
		;
		
parameter_interface_list_function returns [InterfaceList list]
@init{
	val elements=new Buffer[InterfaceList.AbstractInterfaceElement]()
}
	:	e1=interface_element_function {elements += $e1.element} (SEMICOLON e2=interface_element_function {elements += $e2.element})* 
		{$list=new InterfaceList(elements.toList)}
		;
			
interface_constant_declaration returns[InterfaceList.InterfaceConstantDeclaration constElement]
	:	CONSTANT? identifier_list COLON IN? subtype_indication (VAR_ASSIGN expression)? 
		{$constElement=new InterfaceList.InterfaceConstantDeclaration($identifier_list.list,$subtype_indication.subType,$expression.expr)}
		;

interface_signal_declaration_procedure returns [InterfaceList.InterfaceSignalDeclaration signalElement]
	:	SIGNAL? identifier_list COLON interface_mode? subtype_indication BUS? (VAR_ASSIGN expression)?
		{$signalElement=new InterfaceList.InterfaceSignalDeclaration($identifier_list.list,$interface_mode.mode,$subtype_indication.subType,$BUS!=null,$expression.expr)}
		;
		
interface_signal_declaration_function returns [InterfaceList.InterfaceSignalDeclaration signalElement]
	:	SIGNAL identifier_list COLON IN? subtype_indication BUS? (VAR_ASSIGN expression)?
		{$signalElement=new InterfaceList.InterfaceSignalDeclaration($identifier_list.list,InterfaceList.InterfaceMode.IN,$subtype_indication.subType,$BUS!=null,$expression.expr)}
		;
	
interface_variable_declaration returns [InterfaceList.InterfaceVariableDeclaration varElement]
	:	VARIABLE? identifier_list COLON interface_mode? subtype_indication (VAR_ASSIGN expression)?
		{$varElement=new InterfaceList.InterfaceVariableDeclaration($identifier_list.list,$interface_mode.mode,$subtype_indication.subType,$expression.expr)}
		;
	
interface_mode 	returns [InterfaceList.InterfaceMode.Value mode]
	:	IN {$mode=InterfaceList.InterfaceMode.IN}
		|OUT {$mode=InterfaceList.InterfaceMode.OUT}
		|INOUT {$mode=InterfaceList.InterfaceMode.INOUT}
		|BUFFER {$mode=InterfaceList.InterfaceMode.BUFFER}
		|LINKAGE {$mode=InterfaceList.InterfaceMode.LINKAGE}
		;

interface_file_declaration returns [InterfaceList.InterfaceFileDeclaration fileElement]
	:	FILE identifier_list COLON subtype_indication
		{$fileElement=new InterfaceList.InterfaceFileDeclaration($identifier_list.list,$subtype_indication.subType)}
		;
		
ams_interface_terminal_declaration returns [InterfaceList.InterfaceTerminalDeclaration terminalDecl]
	:	TERMINAL identifier_list COLON ams_subnature_indication
		{$terminalDecl=new InterfaceList.InterfaceTerminalDeclaration($identifier_list.list,$ams_subnature_indication.subNature)}
		;

ams_interface_quantity_declaration returns [InterfaceList.InterfaceQuantityDeclaration quantityDecl]
	:	QUANTITY identifier_list COLON (IN|out=OUT)? subtype_indication (VAR_ASSIGN expression)?
		{
			val mode= if ($out!=null) InterfaceList.InterfaceMode.OUT
				else InterfaceList.InterfaceMode.IN
			$quantityDecl=new InterfaceList.InterfaceQuantityDeclaration($identifier_list.list,mode,$subtype_indication.subType,$expression.expr)
		}
		;
/* VHDL 2008
interface_type_declaration 
	:	TYPE identifier;
	
interface_subprogram_declaration 
	:	(PROCEDURE identifier
			(PARAMETER? LPAREN parameter_interface_list=interface_list RPAREN)?
		|(PURE|IMPURE)? FUNCTION (identifier|operator_symbol)
			(PARAMETER? LPAREN parameter_interface_list=interface_list RPAREN)? RETURN type_mark
		)(IS (subprogram_name=name | BOX))?;
		
interface_package_declaration 
	:	PACKAGE identifier IS NEW uninstantiated_package_name=name
			GENERIC MAP LPAREN (generic_association_list=association_list | BOX | DEFAULT) RPAREN;
*/

association_element returns [AssociationList.Element element]
	:	((formal_part ARROW)=>formal_part ARROW)? actual_part
		{$element=new AssociationList.Element($formal_part.formal_part_,$actual_part.actual_part_) }
		;	
		  
association_list returns [AssociationList list]
@init{
	val elements=new Buffer[AssociationList.Element]()
}
	:	e1=association_element {elements += $e1.element} (COMMA e2=association_element {elements += $e2.element})*
		{$list=new AssociationList(elements.toList)}
		;
	
	
formal_part returns  [Name formal_part_]
		//could be a type, function_name, generic_name, port_name or parameter_name
	:	formal_name=name
		{$formal_part_ = $formal_name.name_}
		; // could be generic_name, port_name or parameter_name
		
actual_part returns [Expression actual_part_ ] 
	:	//could be a name(signal_name, variable_name, file_name, subprogram_name, package_name), function_name or type_mark ;could be signal_name or variable_name
		 /*{vhdl2008}?=>INERTIAL?*/ expression {$actual_part_ = $expression.expr}
		//| {vhdl2008}?=>subtype_indication
		| OPEN
		;
			
// B.8 Expression and Names
condition returns [Expression con]
	:	expression {$con=$expression.expr}
		;  
	 
expression returns [Expression expr]
	:	
	   r1=relation (
	   (nand=NAND|nor=NOR) r2=relation {$expr=new LogicalExpression(toPosition(if($nand ne null) $nand else $nor),$r1.rel,if ($nand ne null) LogicalExpression.Operator.NAND else LogicalExpression.Operator.NOR,$r2.rel)}
	   | {$expr=$r1.rel} (logical_operator r2=relation {$expr=new LogicalExpression($logical_operator.pos,$expr,$logical_operator.logOp,$r2.rel)})*
	   )
	   ;

logical_operator returns [LogicalExpression.Operator.Value logOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
}
	:	AND {$logOp=LogicalExpression.Operator.AND}
		|OR {$logOp=LogicalExpression.Operator.OR}
		|XOR {$logOp=LogicalExpression.Operator.XOR}
		|XNOR {$logOp=LogicalExpression.Operator.XNOR}
		//NAND and NOR are handled in expression
		;

relation returns [Expression rel]
  : s1=shift_expression {$rel=$s1.shiftExpr}
    (op=relational_operator s2=shift_expression {$rel=new Relation($op.pos,$s1.shiftExpr,$op.relOp,$s2.shiftExpr)})?
    ;

relational_operator returns [Relation.Operator.Value relOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
}
	:   EQ {$relOp=Relation.Operator.EQ}
	  | NEQ {$relOp=Relation.Operator.NEQ}
	  | LT {$relOp=Relation.Operator.LT}
	  | LEQ {$relOp=Relation.Operator.LEQ}
	  | GT {$relOp=Relation.Operator.GT}
	  | GEQ {$relOp=Relation.Operator.GEQ}
	  ;

	
shift_expression returns [Expression shiftExpr]
  : s1=simple_expression { $shiftExpr=$s1.simpleExpr}
    (op=shift_operator s2=simple_expression {$shiftExpr=new ShiftExpression($op.pos,$s1.simpleExpr,$op.shiftOp,$s2.simpleExpr)})?
  ;

shift_operator returns [ShiftExpression.Operator.Value shiftOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
}
	:	SLL {$shiftOp=ShiftExpression.Operator.SLL}
		|SRL {$shiftOp=ShiftExpression.Operator.SRL}
		|SLA {$shiftOp=ShiftExpression.Operator.SLA}
		|SRA {$shiftOp=ShiftExpression.Operator.SRA}
		|ROL {$shiftOp=ShiftExpression.Operator.ROL}
		|ROR {$shiftOp=ShiftExpression.Operator.ROR}
		;
		
simple_expression returns [Expression simpleExpr]
	  : s=sign? t1=term 
	  {simpleExpr=if (s!=null) new SimpleExpression($s.pos,$s.signOp,$t1.term_,None,None) else $t1.term_}
	  ( op=adding_operator t2=term {$simpleExpr=new SimpleExpression($op.pos,None,$simpleExpr,$op.addOp,$t2.term_)})*
	  ;

sign returns [SimpleExpression.SignOperator.Value signOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
}
	:	PLUS {$signOp=SimpleExpression.SignOperator.PLUS}
		|MINUS {$signOp=SimpleExpression.SignOperator.MINUS}
		;	
		
adding_operator returns [SimpleExpression.AddOperator.Value addOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
}
	:	PLUS {$addOp=SimpleExpression.AddOperator.PLUS}
		|MINUS {$addOp=SimpleExpression.AddOperator.MINUS}
		|AMPERSAND {$addOp=SimpleExpression.AddOperator.AMPERSAND}
		;

multiplying_operator	returns [Term.Operator.Value mulOp,Position pos]
@init{
	$pos=toPosition(input.LT(1))
}
	:	MUL {$mulOp=Term.Operator.MUL}
		|DIV {$mulOp=Term.Operator.DIV}
		|MOD {$mulOp=Term.Operator.MOD}
		|REM {$mulOp=Term.Operator.REM}
		;

term returns [Expression term_]
	  : f1=factor {$term_ = $f1.factor_}
	  ( multiplying_operator f2=factor {$term_ = new Term($multiplying_operator.pos,$term_,$multiplying_operator.mulOp,$f2.factor_)})*
	  ;
 
factor returns [Expression factor_]
	: p1=primary{$factor_ = $p1.obj}(DOUBLESTAR p2=primary {$factor_ = new Factor(toPosition($DOUBLESTAR),$p1.obj,Factor.Operator.POW,$p2.obj)})?
	| ABS primary {$factor_ = new Factor(toPosition($ABS),$primary.obj,Factor.Operator.ABS)}
	| NOT primary {$factor_ = new Factor(toPosition($NOT),$primary.obj,Factor.Operator.NOT)}
	/*
	| AND p5=primary {fac=new Factor($p5.obj,Factor.FactorOperation.AND)}
	| NAND p6=primary{fac=new Factor($p6.obj,Factor.FactorOperation.NAND)}
	| OR p7=primary  {fac=new Factor($p7.obj,Factor.FactorOperation.OR)}
	| NOR p8=primary {fac=new Factor($p8.obj,Factor.FactorOperation.NOR)}
	| XOR p9=primary {fac=new Factor($p9.obj,Factor.FactorOperation.XOR)}
	| XNOR p10=primary {fac=new Factor($p10.obj,Factor.FactorOperation.XNOR)}
	*/
	;

primary returns [Expression obj]
	:
	 (selected_name APOSTROPHE LPAREN) =>selected_name qualified_expression[$selected_name.name_] {$obj=$qualified_expression.expr}
	 | (name)=>name {$obj=new NameExpression($name.name_)}
  	 | (selected_name LPAREN association_list RPAREN)=>function_call {$obj=$function_call.functionCall}
	 | literal {$obj=$literal.literal_} 
	 | (LPAREN expression RPAREN)=> LPAREN expression RPAREN {$obj=$expression.expr}
	 | allocator {$obj=$allocator.newExpression}
	 | aggregate {$obj=new AggregateExpression($aggregate.aggregate_)}
	;

allocator returns [Expression newExpression]
	:
	NEW selected_name 
	 	( qualified_expression[$selected_name.name_] {$newExpression=new NewExpression(toPosition($NEW),Left($qualified_expression.expr))}
	 	| index_constraint? {$newExpression=new NewExpression(toPosition($NEW),Right(new SubTypeIndication(None,$selected_name.name_,Right($index_constraint.ranges))))}
	 	)
	;

function_call returns [FunctionCallExpression functionCall]
	:	function_name=selected_name (LPAREN parameter_association_list=association_list RPAREN)?
		{$functionCall=new FunctionCallExpression($function_name.name_,$parameter_association_list.list)}
		;
	
qualified_expression[SelectedName typeName] returns [QualifiedExpression expr]
	:	APOSTROPHE aggregate
		{$expr=new QualifiedExpression(typeName,new AggregateExpression($aggregate.aggregate_))}
		;

selected_name_list returns [Seq[SelectedName\] list]
@init{
	val tmpList=new Buffer[SelectedName]()
	list=List()
}
	:	n1=selected_name {tmpList += $n1.name_} (COMMA n2=selected_name {tmpList += $n2.name_} )*
		{$list=tmpList.toList}
		;
		
selected_name returns [SelectedName name_]
@init{
	val parts=new Buffer[Identifier]()
}
	:	name_prefix ( name_selected_part {parts += $name_selected_part.part.identifier})*
		{$name_ =new SelectedName($name_prefix.id +: parts.toList)}
		;

name_list returns [Seq[Name\] list]
@init{
	val tmpList=new Buffer[Name]()
	list=List()
}
	:	n1=name {tmpList += $n1.name_} (COMMA n2=name {tmpList += $n2.name_} )*
		{$list=tmpList.toList}
		;
			
name returns [Name name_]
@init{
	val parts=new Buffer[Name.Part]()
}
	:	name_prefix (name_part {parts += $name_part.part})*
	  	{$name_ =new Name($name_prefix.id,parts.toList)}
  		;

name_prefix returns [Identifier id]
 	: identifier {$id=$identifier.id}
 	| STRING_LITERAL{id=toIdentifier($STRING_LITERAL)}
 	;
 	
name_part returns [Name.Part part]
  	: 
  	 name_selected_part {$part = $name_selected_part.part}
   	 | name_attribute_part {$part = $name_attribute_part.part}
   	 | (name_indexed_part)=>name_indexed_part {$part = $name_indexed_part.part}
  	 | name_slice_part {$part = $name_slice_part.part}
  	 ; 

			
name_selected_part returns [Name.SelectedPart part]
	:	DOT (
		identifier{$part= new Name.SelectedPart($identifier.id)}
		|CHARACTER_LITERAL {$part= new Name.SelectedPart(toIdentifier($CHARACTER_LITERAL))}
		|STRING_LITERAL{$part= new Name.SelectedPart(toIdentifier($STRING_LITERAL))}
		|ALL{$part= new Name.SelectedPart(toIdentifier($ALL))}
		)
		;
		
name_slice_part returns [Name.SlicePart part]
	:	LPAREN discrete_range RPAREN  {$part=new Name.SlicePart($discrete_range.discreteRange)}
		;
				
name_indexed_part returns [Name.IndexPart part]
@init{
	val indexes=new Buffer[Expression]
}
	:	 LPAREN e1=expression {indexes += $e1.expr}(COMMA e2=expression {indexes += $e2.expr})* RPAREN {$part=new Name.IndexPart(indexes.toList)} 
		;

name_attribute_part returns [Name.AttributePart part]
	:	signature? APOSTROPHE (id=identifier|RANGE {id=toIdentifier($RANGE)})
	( (LPAREN expression RPAREN)=> LPAREN expression RPAREN)? {$part=new Name.AttributePart($signature.signature_,$id.id,$expression.expr)}
	;
		
signature returns [Signature signature_]
	:	LBRACKET selected_name_list? (RETURN type_mark)? RBRACKET
		{$signature_ =new Signature(toPosition($LBRACKET),$selected_name_list.list,$type_mark.typeName)}
		;
/* VHDL 2008	
external_name 
	:	'<<' CONSTANT external_pathname COLON subtype_indication '>>'
		| '<<' SIGNAL external_pathname COLON subtype_indication '>>'
		| '<<' VARIABLE external_pathname COLON subtype_indication '>>';
		
external_pathname 
	:	absolute_pathname | relative_pathname | package_pathname;
	
absolute_pathname 
	:	DOT (pathname_element DOT)* object_identifier=identifier;
	
relative_pathname 
	:	('^' DOT)* (pathname_element DOT)* object_identifier=identifier;
	
pathname_element 
		//could be entity_identifier, component_instantiation_label, block_label, package_identifier or generate_statment_label
	:	element=identifier (LPAREN static_expression=expression RPAREN)?;
		
package_pathname 
	:	AT library_identifier=identifier DOT (package_identifier=identifier DOT)* object_identifier=identifier;
*/	
literal returns [Expression literal_]
@init{
	var literalType:Literal.Type.Value=null
	val firstToken=input.LT(1)
}
	:	
		(
		REAL_LITERAL {literalType=Literal.Type.REAL_LITERAL}
		| INTEGER_LITERAL {literalType=Literal.Type.INTEGER_LITERAL}
		| BASED_LITERAL {literalType=Literal.Type.BASED_LITERAL}
		| CHARACTER_LITERAL {literalType=Literal.Type.CHARACTER_LITERAL}
		| STRING_LITERAL {literalType=Literal.Type.STRING_LITERAL}
		| BIT_STRING_LITERAL {literalType=Literal.Type.BIT_STRING_LITERAL}
		| NULL {literalType=Literal.Type.NULL_LITERAL}
		)
		{$literal_ =new Literal(toPosition(firstToken),input.LT(-1).getText(),literalType)}
		({input.LA(-1)==INTEGER_LITERAL || input.LA(-1)==REAL_LITERAL /*|| input.LA(-1)==BASED_LITERAL*/}?=> identifier {$literal_ = new PhysicalLiteral($literal_.asInstanceOf[Literal],$identifier.id)})?
		;
	
physical_literal returns [PhysicalLiteral literal_]
@init{
	var text:String=null
	var literalType:Literal.Type.Value=null
	val firstToken=input.LT(1)
}
	:	(INTEGER_LITERAL {text=input.LT(-1).getText(); literalType=Literal.Type.INTEGER_LITERAL}
		|REAL_LITERAL {text=input.LT(-1).getText(); literalType=Literal.Type.REAL_LITERAL}
		//|BASED_LITERAL {str=input.LT(-1).getText(); literalType=Literal.Type.BASED_LITERAL} //TODO
		)
		unit_name=identifier
		{$literal_ =new PhysicalLiteral(toPosition(firstToken),text,$unit_name.id,literalType)}
		;
	
element_association returns [Aggregate.ElementAssociation element]
	  : ( (choices ARROW)=> choices ARROW )? expression 
	  	{$element=new Aggregate.ElementAssociation($choices.choices_,$expression.expr)}
	  	;
	
aggregate returns [Aggregate aggregate_]
@init{
	val elements=new Buffer[Aggregate.ElementAssociation]()
}
	:	LPAREN  e1=element_association{elements += $e1.element} (COMMA e2=element_association {elements += $e2.element})* RPAREN
		{aggregate_ =new Aggregate(toPosition($LPAREN),elements.toList)}
		;


choice	returns [Choices.Choice choice_]
@init{
	val firstToken=input.LT(1)
}
	:	
		(simple_expression direction)=> d=discrete_range {choice_ =new Choices.Choice(toPosition(firstToken),Some(Left($d.discreteRange)))}
		| expr=simple_expression {choice_ =new Choices.Choice(toPosition(firstToken),Some(Right($expr.simpleExpr)))}
		| OTHERS {choice_ =new Choices.Choice(toPosition(firstToken),None)}
		;

choices returns [Choices choices_]
@init{
	val elements=new Buffer[Choices.Choice]()
}
	:	c1=choice {elements += $c1.choice_}( BAR c2=choice {elements += $c2.choice_})*
		{$choices_ =new Choices(elements.toList)}
		;

/* VHDL 2008
PSL_Property_Declaration 
	:	;
	
PSL_Sequence_Declaration 
	:	;
	
PSL_Clock_Declaration 
	:	;

PSL_PSL_Directive 
	:	;
	
PSL_Verification_Unit 
	:	;
*/	
	
identifier_list returns [Seq[Identifier\] list]
@init{
	val tmpList=new Buffer[Identifier]()
	list=List()
}
	:	id1=identifier {tmpList += $id1.id} (COMMA id2=identifier {tmpList += $id2.id} )* 
		{$list=tmpList.toList}
		;
			
identifier returns [Identifier id]
	:	BASIC_IDENTIFIER  {$id=toIdentifier(input.LT(-1))}
		| EXTENDED_IDENTIFIER {$id=toIdentifier(input.LT(-1),false)}
		;
/* VHDL 2008	
tool_directive 
	:	'\'' identifier GRAPHIC_CHARACTER*
		;
*/	

label_colon returns [Identifier label]
	:	identifier COLON
		{$label=$identifier.id}
		;
    
BREAK	:	'break' {if (!ams) $type=BASIC_IDENTIFIER};

TOLERANCE :	'tolerance' {if (!ams) $type=BASIC_IDENTIFIER};
// Lexer rules
 WS	:	('\t'|' '|NEWLINE)+ {skip()};
fragment NEWLINE :	'\r'|'\n';

//mulitline comment with nested comments, http://www.antlr.org/wiki/pages/viewpage.action?pageId=1573 
NESTED_ML_COMMENT
    :   '/*'  (options {greedy=false;} : NESTED_ML_COMMENT | . )*  '*/' {skip()};//{$channel=HIDDEN;};

//A comment can appear on any line of a VHDL description and may contain any character except the format effectors vertical tab, carriage return, line feed, and form feed.		
LINECOMMENT :	'--' ~('\r'|'\n'|'\u000C')* {skip()};//{$channel=HIDDEN;};
   
BASIC_IDENTIFIER : LETTER ( LETTER_OR_DIGIT | '_' )*;

//extended identifiers can't contain a single backslash
EXTENDED_IDENTIFIER : '\\' ( '\"' | '\\\\' | GRAPHIC_CHARACTER )+ '\\';
   		 	
BASED_LITERAL : INTEGER '#' BASED_INTEGER ( DOT BASED_INTEGER )? '#' EXPONENT? ;

INTEGER_LITERAL : INTEGER EXPONENT?;
    
REAL_LITERAL : INTEGER  DOT INTEGER  EXPONENT? ;

APOSTROPHE
  : '\''
    ( (( '\"' | '\\' | GRAPHIC_CHARACTER ) '\'')=> ( '\"' | '\\' | GRAPHIC_CHARACTER ) '\''
      { $type = CHARACTER_LITERAL; }
    )?
  ; 

//string literals can't contain a single quotation mark
STRING_LITERAL : '\"' ( '\"\"' | '\\' | GRAPHIC_CHARACTER )* '\"';
  
BIT_STRING_LITERAL : /* VHDL 2008 INTEGER?*/ BASE_SPECIFIER '\"' BASED_INTEGER? '\"';

fragment
BASE_SPECIFIER : 'b' | 'o' | 'x' /* VHDL 2008 | 'ub' | 'uo' | 'ux ' | 'sb' | 'so' | 'sx ' | 'd'*/ ;

fragment
BASED_INTEGER : EXTENDED_DIGIT ( '_'? EXTENDED_DIGIT )*;

fragment
EXTENDED_DIGIT : DIGIT | LETTER;

fragment
INTEGER: DIGIT ( '_'? DIGIT )*;

fragment
EXPONENT: 'e' ( '+' | '-' )? INTEGER;

fragment
LETTER_OR_DIGIT: LETTER| DIGIT;

fragment
LETTER: UPPER_CASE_LETTER| LOWER_CASE_LETTER;
    
fragment
GRAPHIC_CHARACTER
  : UPPER_CASE_LETTER
  | DIGIT
  | SPECIAL_CHARACTER
  | SPACE_CHARACTER
  | LOWER_CASE_LETTER
  | OTHER_SPECIAL_CHARACTER
  ;  
  
//A B C D E F G H I J K L M N O P Q R S T U V W X Y Z À Á Â Ã Ä Å Æ Ç È É Ê Ë Ì Í Î Ï D . Ñ Ò Ó Ô Õ Ö Ø Ù Ú Û Ü ´ Y P '
fragment
UPPER_CASE_LETTER : 'A'..'Z' | '\u00c0'..'\u00d6' | '\u00d8' .. '\u00de'; //A - Z,À - Ö, Ø - Þ

//a b c d e f g h i j k l m n o p q r s t u v w x y z ß à á â ã ä å æ ç è é ê ë ì í î ï  ñ ò ó ô õ ö ø ù ú û ü ´ y ' p ÿ
fragment
LOWER_CASE_LETTER : 'a'..'z' | '\u00df'..'\u00f6' | '\u00f8'.. '\u00ff'; //a-z,ß - ö, ø - ÿ

fragment
DIGIT : '0'..'9';

fragment
SPECIAL_CHARACTER
  : '#' | '&' | '\'' | '(' | ')' | '*' | '+' | ',' | '-'
  | '.' | '/' | ':' | ';' | '<' | '=' | '>' | '[' | ']' | '_' | '|';
    
fragment
SPACE_CHARACTER : ' ' | '\u00a0'; //space, non-breaking space

fragment
OTHER_SPECIAL_CHARACTER
  : '!' | '$' | '%' | '@' | '?' | '^' | '`' | '{' | '}' | '~'
  | '\u00a1'..'\u00bf' | '\u00d7' | '\u00f7';//  ¡ - ¿, × - ÷

fragment
CHARACTER_LITERAL
    :
    ;