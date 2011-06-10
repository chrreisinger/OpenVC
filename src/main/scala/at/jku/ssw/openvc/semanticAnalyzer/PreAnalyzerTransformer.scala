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

object PreAnalyzerTransformer extends Phase {
  val name = "preAnalyzerTransformer"
  override val description = "simple AST transformations before semanticAnalyzer"

  override def apply(unit: CompilationUnit): CompilationUnit = unit.copy(astNode = ASTStatementTransformer(unit.astNode)(transform))

  private def convertWaveformAssignment(waveformAssignment: WaveformAssignment): SequentialStatement = {
    def waveTransform(waveform: Waveform): SequentialStatement =
      if (waveform.isUnaffected) {
        NullStatement(waveform.position, label = None)
      } else {
        SimpleWaveformAssignmentStatement(waveform.position, None, waveformAssignment.target, waveformAssignment.delay, waveform)
      }

    waveformAssignment match {
      case conditionalWaveformAssignment: ConditionalWaveformAssignment => conditionalWaveformAssignment.alternatives match {
        case Seq(alternative) if (alternative.condition == None) => waveTransform(alternative.waveform)
        case _ =>
          val last = conditionalWaveformAssignment.alternatives.last
          val mapper = (alternative: ConcurrentConditionalSignalAssignment.When) => new IfStatement.IfThenPart(alternative.condition.get, Seq(waveTransform(alternative.waveform)))
          val (ifThenList, elseSequentialStatements) = if (last.condition == None) {
            (conditionalWaveformAssignment.alternatives.init.map(mapper), Some(Seq(waveTransform(last.waveform))))
          }
          else {
            (conditionalWaveformAssignment.alternatives.map(mapper), None)
          }
          IfStatement(conditionalWaveformAssignment.position, label = None, ifThenList = ifThenList, elseSequentialStatements = elseSequentialStatements, endLabel = None)
      }
      case selectedWaveformAssignment: SelectedWaveformAssignment =>
        val caseStmtAlternatives = selectedWaveformAssignment.alternatives.map(alternative => new CaseStatement.When(alternative.choices, Seq(waveTransform(alternative.waveform))))
        CaseStatement(selectedWaveformAssignment.position, isMatchingCase = selectedWaveformAssignment.isMatchingCase, label = None, expression = selectedWaveformAssignment.expression, alternatives = caseStmtAlternatives, endLabel = None)
    }
  }

  private def toProcessStatement(statement: SequentialStatement, label: Option[Identifier], isPostponed: Boolean): ASTNode = {
    val waitStatement = WaitStatement(statement.position, label = None, signals = None, untilCondition = None, forExpression = None)
    ProcessStatement(statement.position, label = label, isPostponed = isPostponed, sensitivityList = None, declarativeItems = Seq(),
      sequentialStatements = Seq(statement, waitStatement), endLabel = None)
  }

  private def transform(node: ASTNode): ASTNode = node match {
    case signalAssignmentStmt: SignalAssignmentStatement => signalAssignmentStmt match {
      //VHDL 2008
      case waveformAssignment: WaveformAssignment => convertWaveformAssignment(waveformAssignment)
      case conditionalForceAssignment: ConditionalForceAssignment =>
        def toForceAssignment(alternative: ConditionalVariableAssignment.When) = SimpleForceAssignment(alternative.expression.position, None, conditionalForceAssignment.target, conditionalForceAssignment.forceMode, alternative.expression)
        conditionalForceAssignment.alternatives match {
          case Seq(alternative) if (alternative.condition == None) => toForceAssignment(alternative)
          case _ =>
            val last = conditionalForceAssignment.alternatives.last
            val mapper = (alternative: ConditionalVariableAssignment.When) => new IfStatement.IfThenPart(alternative.condition.get, Seq(toForceAssignment(alternative)))
            val (ifThenList, elseSequentialStatements) = if (last.condition == None) {
              (conditionalForceAssignment.alternatives.init.map(mapper), Some(Seq(toForceAssignment(last))))
            }
            else {
              (conditionalForceAssignment.alternatives.map(mapper), None)
            }
            IfStatement(conditionalForceAssignment.position, conditionalForceAssignment.label, ifThenList, elseSequentialStatements, conditionalForceAssignment.label)
        }
      case selectedForceAssignment: SelectedForceAssignment =>
        val caseStmtAlternatives = selectedForceAssignment.alternatives.map(alternative => new CaseStatement.When(alternative.choices, Seq(SimpleForceAssignment(alternative.expression.position, None, selectedForceAssignment.target, selectedForceAssignment.forceMode, alternative.expression))))
        CaseStatement(selectedForceAssignment.position, isMatchingCase = selectedForceAssignment.isMatchingCase, label = selectedForceAssignment.label,
          expression = selectedForceAssignment.expression, alternatives = caseStmtAlternatives, endLabel = selectedForceAssignment.label)
      case _ => signalAssignmentStmt
    }
    case variableAssignmentStmt: VariableAssignmentStatement => variableAssignmentStmt match {
      //VHDL 2008
      case conditionalVariableAssignment: ConditionalVariableAssignment =>
        def toVariableAssignment(alternative: ConditionalVariableAssignment.When) = SimpleVariableAssignmentStatement(alternative.expression.position, None, conditionalVariableAssignment.target, alternative.expression)
        conditionalVariableAssignment.alternatives match {
          case Seq(alternative) if (alternative.condition == None) => toVariableAssignment(alternative)
          case _ =>
            val last = conditionalVariableAssignment.alternatives.last
            val mapper = (alternative: ConditionalVariableAssignment.When) => new IfStatement.IfThenPart(alternative.condition.get, Seq(toVariableAssignment(alternative)))
            val (ifThenList, elseSequentialStatements) = if (last.condition == None) {
              (conditionalVariableAssignment.alternatives.init.map(mapper), Some(Seq(toVariableAssignment(last))))
            }
            else {
              (conditionalVariableAssignment.alternatives.map(mapper), None)
            }
            IfStatement(conditionalVariableAssignment.position, conditionalVariableAssignment.label, ifThenList, elseSequentialStatements, conditionalVariableAssignment.label)
        }
      case selectedVariableAssignment: SelectedVariableAssignment =>
        val caseStmtAlternatives = selectedVariableAssignment.alternatives.map(alternative => new CaseStatement.When(alternative.choices, Seq(SimpleVariableAssignmentStatement(alternative.expression.position, None, selectedVariableAssignment.target, alternative.expression))))
        CaseStatement(selectedVariableAssignment.position, isMatchingCase = selectedVariableAssignment.isMatchingCase, label = selectedVariableAssignment.label,
          expression = selectedVariableAssignment.expression, alternatives = caseStmtAlternatives, endLabel = selectedVariableAssignment.label)
      case _ => variableAssignmentStmt
    }
    //concurrent Statements
    case concurrentProcedureCallStmt: ConcurrentProcedureCallStatement =>
      val procedureCallStmt = ProcedureCallStatement(None, concurrentProcedureCallStmt.name, concurrentProcedureCallStmt.parameterAssociation)
      toProcessStatement(procedureCallStmt, concurrentProcedureCallStmt.label, concurrentProcedureCallStmt.isPostponed)
    case concurrentAssertionStatement: ConcurrentAssertionStatement =>
      val assertStmt = AssertionStatement(concurrentAssertionStatement.position, None, concurrentAssertionStatement.condition, concurrentAssertionStatement.reportExpression, concurrentAssertionStatement.severityExpression)
      toProcessStatement(assertStmt, concurrentAssertionStatement.label, concurrentAssertionStatement.isPostponed)
    case concurrentSignalAssignmentStmt: ConcurrentSignalAssignmentStatement =>
      require(!concurrentSignalAssignmentStmt.isGuarded)
      val newStatement = concurrentSignalAssignmentStmt match {
        case ConcurrentConditionalSignalAssignment(position, label, _, target, _, delay, alternatives) => convertWaveformAssignment(ConditionalWaveformAssignment(position, label, target, delay, alternatives))
        case ConcurrentSelectedSignalAssignment(position, label, _, expression, isMatchingCase, target, _, delay, alternatives) => convertWaveformAssignment(SelectedWaveformAssignment(position, label, expression, isMatchingCase, target, delay, alternatives))
      }
      toProcessStatement(newStatement, concurrentSignalAssignmentStmt.label, concurrentSignalAssignmentStmt.isPostponed)
    case _ => node
  }
}