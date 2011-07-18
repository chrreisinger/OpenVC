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

import at.jku.ssw.openvc._
import ast._
import ast.concurrentStatements._
import ast.sequentialStatements._
import ast.designUnits._
import ast.declarativeItems._

object ASTStatementTransformer {
  type Transformer = ASTNode => ASTNode

  def apply(node: ASTNode)(transformer: Transformer): ASTNode = {

    def acceptNodes[A <: ASTNode](nodes: Seq[A]): Seq[A] = nodes.map(node => acceptNode(node).asInstanceOf[A])

    def transformNodes[A <: ASTNode](nodes: Seq[A]): Seq[A] = nodes.map(node => acceptNode(transformer(node)).asInstanceOf[A])

    def acceptNode(node: ASTNode): ASTNode = node match {
      case DesignFile(designUnits) => DesignFile(acceptNodes(designUnits))
      case DesignUnit(contextItems, libraryUnit) => DesignUnit(contextItems, acceptNode(libraryUnit).asInstanceOf[LibraryUnit])
      case packageBodyDeclaration: PackageBodyDeclaration => packageBodyDeclaration.copy(declarativeItems = acceptNodes(packageBodyDeclaration.declarativeItems))
      case entityDeclaration: EntityDeclaration => entityDeclaration.copy(declarativeItems = acceptNodes(entityDeclaration.declarativeItems), concurrentStatements = transformNodes(entityDeclaration.concurrentStatements))
      case architectureDeclaration: ArchitectureDeclaration => architectureDeclaration.copy(declarativeItems = acceptNodes(architectureDeclaration.declarativeItems), concurrentStatements = transformNodes(architectureDeclaration.concurrentStatements))
      //declarative Items
      case functionDefinition: FunctionDefinition => functionDefinition.copy(declarativeItems = acceptNodes(functionDefinition.declarativeItems), sequentialStatements = transformNodes(functionDefinition.sequentialStatements))
      case procedureDefinition: ProcedureDefinition => procedureDefinition.copy(declarativeItems = acceptNodes(procedureDefinition.declarativeItems), sequentialStatements = transformNodes(procedureDefinition.sequentialStatements))
      //sequential Statements
      case loopStmt: LoopStatement => loopStmt.copy(sequentialStatements = transformNodes(loopStmt.sequentialStatements))
      case forStmt: ForStatement => forStmt.copy(sequentialStatements = transformNodes(forStmt.sequentialStatements))
      case whileStmt: WhileStatement => whileStmt.copy(sequentialStatements = transformNodes(whileStmt.sequentialStatements))
      case caseStmt: CaseStatement => caseStmt.copy(alternatives = caseStmt.alternatives.map(alternative => new CaseStatement.When(alternative.choices, transformNodes(alternative.sequentialStatements))))
      case ifStmt: IfStatement =>
        val ifThenList = ifStmt.ifThenList.map(ifThen => new IfStatement.IfThenPart(ifThen.condition, transformNodes(ifThen.sequentialStatements)))
        val elseSequentialStatements = ifStmt.elseSequentialStatements.map(transformNodes)
        ifStmt.copy(ifThenList = ifThenList, elseSequentialStatements = elseSequentialStatements)
      //concurrent Statements
      case ifGenerateStmt: IfGenerateStatement =>
        val ifThenList = ifGenerateStmt.ifThenList.map(ifThen => ifThen.copy(declarativeItems = acceptNodes(ifThen.declarativeItems), concurrentStatements = transformNodes(ifThen.concurrentStatements)))
        val elsePart = ifGenerateStmt.elsePart.map(elsePart => elsePart.copy(declarativeItems = acceptNodes(elsePart.declarativeItems), concurrentStatements = transformNodes(elsePart.concurrentStatements)))
        ifGenerateStmt.copy(ifThenList = ifThenList, elsePart = elsePart)
      case caseGenerateStmt: CaseGenerateStatement =>
        caseGenerateStmt.copy(alternatives = caseGenerateStmt.alternatives.map(alternative => alternative.copy(declarativeItems = acceptNodes(alternative.declarativeItems), concurrentStatements = transformNodes(alternative.concurrentStatements))))
      case forGenerateStmt: ForGenerateStatement => forGenerateStmt.copy(declarativeItems = acceptNodes(forGenerateStmt.declarativeItems), concurrentStatements = transformNodes(forGenerateStmt.concurrentStatements))
      case processStmt: ProcessStatement => processStmt.copy(declarativeItems = acceptNodes(processStmt.declarativeItems), sequentialStatements = transformNodes(processStmt.sequentialStatements))
      case blockStmt: BlockStatement => blockStmt.copy(declarativeItems = acceptNodes(blockStmt.declarativeItems), concurrentStatements = transformNodes(blockStmt.concurrentStatements))
      case protectedTypeBody: ProtectedTypeBodyDeclaration => protectedTypeBody.copy(declarativeItems = acceptNodes(protectedTypeBody.declarativeItems))
      case _ => node
    }

    acceptNode(node)
  }
}