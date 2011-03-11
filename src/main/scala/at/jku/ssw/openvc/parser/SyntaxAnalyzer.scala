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

import org.antlr.runtime.{CommonTokenStream, CharStream}
import at.jku.ssw.openvc.{CompilationUnit, Phase}

object SyntaxAnalyzer extends Phase {
  val name = "parser"

  override def apply(unit: CompilationUnit): CompilationUnit = {
    val lexer = new Lexer(unit.source.asInstanceOf[CharStream])
    lexer.ams = unit.configuration.amsEnabled
    lexer.vhdl2008 = unit.configuration.vhdl2008
    val tokens = new CommonTokenStream(lexer)
    val parser = new Parser(tokens)
    parser.ams = unit.configuration.amsEnabled
    parser.vhdl2008 = unit.configuration.vhdl2008
    val designFile = parser.design_file()
    unit.addErrors(parser.syntaxErrors)
    unit.addErrors(lexer.lexerErrors)
    unit.copy(astNode = designFile)
  }
}