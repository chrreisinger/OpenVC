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

package at.jku.ssw.tests

import at.jku.ssw.openvc.VHDLCompiler
import at.jku.ssw.openvs.Simulator
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import java.io.{File, PrintWriter}

trait GenericTest extends FunSuite with ShouldMatchers {
  val library = "testLibrary"
  val directory = new File(library + File.separator)
  val configuration = new VHDLCompiler.Configuration(false, library, false, false)

  def compile(source: String) {
    if (directory.exists) directory.listFiles.foreach(_.delete())

    val result = VHDLCompiler.compileFileFromText(configuration, source, "testFile")
    result.printErrors(new PrintWriter(System.out), Some(source.split('\n').toIndexedSeq))
    result.syntaxErrors.size should equal(0)
    result.semanticErrors.size should equal(0)
  }

  def compileAndLoad(text: String)(source: String) =
    test(text) {
      compile(source)
      Simulator.loadFiles(this.getClass.getClassLoader, library, directory.listFiles.filter(_.getName.endsWith(".class")).map(_.getName.split('.').head), List("std.jar"))
    }

  def compileAndRun(text: String, packageName: String, procedure: String)(source: String) =
    test(text) {
      compile(source)
      Simulator.runClass(this.getClass.getClassLoader, configuration.libraryOutputDirectory, packageName, procedure, List("std.jar", "ieee.jar"))
    }

  def compileCodeInPackageAndLoad(text: String)(source: String) =
    compileAndLoad(text) {
      """
      package Dummy is
      """ +
              source +
              """
              end Dummy ;
              """
    }

  def compileCodeInPackageAndRun(text: String, packageName: String = "dummy", procedure: String = "main")(source: String) =
    compileAndRun(text, packageName, procedure) {
      """
      package dummy is
      end package dummy;
      package body Dummy is
      """ +
              source +
              """
              end Dummy ;
              """
    }

}