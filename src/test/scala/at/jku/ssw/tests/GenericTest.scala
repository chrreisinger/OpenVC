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

import java.io.{File, PrintWriter}

import at.jku.ssw.openvc.util.SourceFile
import at.jku.ssw.openvs.Simulator
import at.jku.ssw.openvc.{CompilationUnit, VHDLCompiler}

import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{BeforeAndAfter, FunSuite}

trait GenericTest extends FunSuite with ShouldMatchers with BeforeAndAfter {
  val library = "testLibrary"
  val configuration = new CompilationUnit.Configuration(
    enableAMS = false,
    enableVhdl2008 = true,
    noWarn = false,
    encoding = None,
    outputDirectory = "output/",
    designLibrary = library,
    libraryDirectory = "vhdlLibs\\",
    XrunOnlyToPhase = None,
    XdebugCompiler = false,
    XdebugCodeGenerator = false
  )
  val directory = new File(configuration.libraryOutputDirectory)

  after {
    if (directory.exists) directory.listFiles.foreach {
      file =>
        if (file.isDirectory)
          file.listFiles.foreach(_.delete())
        file.delete()
    }
  }

  def compile(source: String) {
    val unit = VHDLCompiler.compile(new CompilationUnit(SourceFile.fromString(source, "testFile"), configuration))
    unit.printMessages(new PrintWriter(System.out))
    unit.hasErrors should equal(false)
  }

  def compileAndLoad(text: String)(source: String) {
    test(text) {
      compile(source)
      Simulator.loadFiles(this.getClass.getClassLoader, configuration.outputDirectory, directory.listFiles.filter(_.getName.endsWith(".class")).map(configuration.designLibrary + "." + _.getName.split('.').head), List("std.jar"))
      //Simulator.loadFiles(this.getClass.getClassLoader, listFiles(new File(configuration.libraryOutputDirectory), classFilter, true).map(file => file.getPath.substring(file.getPath.indexOf('\\') + 1).split('.').head.replace('\\', '.')), List("std.jar", "ieee.jar"))
    }
  }

  def compileAndRun(text: String, packageName: String, procedure: String)(source: String) {
    test(text) {
      compile(source)
      Simulator.runClass(this.getClass.getClassLoader, configuration.outputDirectory, configuration.designLibrary + "." + packageName + "_body", procedure, List("std.jar", "ieee.jar"))
    }
  }

  def compileCodeInPackageAndLoad(text: String)(source: String) {
    compileAndLoad(text) {
      """
      package Dummy is
      """ +
        source +
        """
        end Dummy ;
        """
    }
  }

  def compileCodeInPackageAndRun(text: String, packageName: String = "dummy", procedure: String = "main$-1404437944")(source: String) {
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

}