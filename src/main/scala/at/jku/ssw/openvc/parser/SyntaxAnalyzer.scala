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
    val lexer = new Lexer(unit.sourceFile.asInstanceOf[CharStream])
    lexer.compilationUnit = unit
    val parser = new Parser(new CommonTokenStream(lexer))
    parser.compilationUnit = unit
    unit.copy(astNode = parser.design_file())
  }
}