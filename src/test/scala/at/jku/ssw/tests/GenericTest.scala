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

import at.jku.ssw.openvs.Simulator
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import java.io.{File, PrintWriter}
import at.jku.ssw.openvc.{CompilationUnit, VHDLCompiler}
import at.jku.ssw.openvc.util.SourceFile

trait GenericTest extends FunSuite with ShouldMatchers {
  val library = "testLibrary"
  val configuration = new VHDLCompiler.Configuration(amsEnabled = false, parseOnly = false, vhdl2008 = false, outputDirectory = "output/", designLibrary = library, libraryDirectory = "vhdlLibs\\", debugCompiler = false, debugCodeGenerator = false)
  val directory = new File(configuration.libraryOutputDirectory)

  def compile(source: String) {
    if (directory.exists) directory.listFiles.foreach(_.delete())

    val unit = VHDLCompiler.compile(new CompilationUnit(SourceFile.fromText(source, "testFile"), configuration))
    unit.printErrors(new PrintWriter(System.out))
    unit.errors.size should equal(0)
  }

  def compileAndLoad(text: String)(source: String) =
    test(text) {
      compile(source)
      Simulator.loadFiles(this.getClass.getClassLoader, configuration.outputDirectory, directory.listFiles.filter(_.getName.endsWith(".class")).map(configuration.designLibrary + "." + _.getName.split('.').head), List("std.jar"))
      //Simulator.loadFiles(this.getClass.getClassLoader, listFiles(new File(configuration.libraryOutputDirectory), classFilter, true).map(file => file.getPath.substring(file.getPath.indexOf('\\') + 1).split('.').head.replace('\\', '.')), List("std.jar", "ieee.jar"))
    }

  def compileAndRun(text: String, packageName: String, procedure: String)(source: String) =
    test(text) {
      compile(source)
      Simulator.runClass(this.getClass.getClassLoader, configuration.outputDirectory, configuration.designLibrary + "." + packageName, procedure, List("std.jar", "ieee.jar"))
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

  def compileCodeInPackageAndRun(text: String, packageName: String = "dummy", procedure: String = "main$1106182723")(source: String) =
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