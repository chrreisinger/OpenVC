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

package at.jku.ssw.openvc

import at.jku.ssw.openvs._
import at.jku.ssw.openvc.VHDLCompiler._
import at.jku.ssw.openvs.VHDLRuntime._
import java.io._

object Main {
  def main(arguments: Array[String]) {
    println(" OpenVC  Copyright (C) 2010  Christian Reisinger")
    try {
      val filter = new FilenameFilter() {
        override def accept(dir: File, name: String): Boolean = (name.endsWith(".vhd") || name.endsWith(".vhdl")) && !name.endsWith("in.vhd")
      }
      val fileOption = arguments.find(argument => !argument.startsWith("--") && (argument.endsWith(".vhd") || argument.endsWith(".vhdl")))
      //val files = List("math_real.vhd", "math_real-body.vhd")
      //val files=List("math_complex.vhd","math_complex-body.vhd")
      val files = List("numeric_bit.vhd", "numeric_bit-body.vhd")
      //val files = List("numeric_std.vhd", "numeric_std-body.vhd")
      //val files = List("std_logic_1164.vhd", "std_logic_1164-body.vhd")

      val configuration = parseCommandLineArguments(arguments)
      def toLines(sourceFile: String) = scala.io.Source.fromFile(sourceFile).getLines().toIndexedSeq

      fileOption match {
        case None =>
          files.map(file => VHDLCompiler.compileFile(configuration, file)).foreach(result => result.printErrors(new PrintWriter(System.out), Some(toLines(result.sourceFile))))
          Simulator.loadFiles(this.getClass.getClassLoader, configuration.designLibrary, List(files.tail.head.split("-body").head), List("std.jar", "ieee.jar"))
        case Some(file) =>
          VHDLCompiler.compileFile(configuration, file).printErrors(new PrintWriter(System.out), Some(toLines(file)))
          /*
          val clazz = new java.net.URLClassLoader(Array(new File("work/").toURI.toURL), this.getClass.getClassLoader).loadClass("alu_tb$main$foorec")
          val x=clazz.getConstructor(java.lang.Integer.TYPE,java.lang.Double.TYPE).newInstance(new java.lang.Integer(40),new java.lang.Double(40.0))
          println(x)
          val clazz2 = new java.net.URLClassLoader(Array(new File("work/").toURI.toURL), this.getClass.getClassLoader).loadClass("alu_tb$sharedcounter")
          val y=clazz2.newInstance
          clazz2.getMethod("increment",java.lang.Integer.TYPE).invoke(y,new java.lang.Integer(20))
          clazz2.getMethod("decrement",java.lang.Integer.TYPE).invoke(y,new java.lang.Integer(30))
          println(clazz2.getMethod("value").invoke(y))
          */
          Simulator.runClass(this.getClass.getClassLoader, configuration.designLibrary, "alu_tb", "main", List("std.jar", "ieee.jar"))
      }
      /*
            val (designFile, syntaxErrors) = ASTBuilder.fromFile("numeric_std-body.vhd", configuration)
            //val (designFile, syntaxErrors) = ASTBuilder.fromFile("""C:\Users\christian\Desktop\grlib-gpl-1.0.22-b4095\lib\gaisler\sim\pwm_check.vhd""", configuration)
            //val (designFile, syntaxErrors) = ASTBuilder.fromFile("alu_tb.vhd", configuration)
            for (msg <- syntaxErrors) {
              println("line:" + msg.position.line + " col:" + msg.position.charPosition + " " + msg.message)
            }
      */
      //if (true) return

      // time 124467 ms
      //files:651 lines:533921
      //neu time:29518 files:606 481626
      /*
            scala.Console.setOut(new PrintStream("out.txt"))
            val start = System.currentTimeMillis
            println("scala start:")
            for (f <- listFiles(new File("C:\\Users\\christian\\Desktop\\grlib-gpl-1.0.22-b4095\\"), filter, true)) {
              //for (f <- Main.listFiles(new File("C:\\Users\\christian\\Desktop\\vlsi\\"), filter, true)) {
              //for (f <- Main.listFiles(new File("C:\\Users\\christian\\Desktop\\vhdl\\"), filter, true)) {
              //for (f <- Main.listFiles(new File("C:\\Users\\christian\\Desktop\\DHE\\abschlussprojekt"), filter,true)) {
              //for (f <- Main.listFiles(new File("C:\\Users\\christian\\Desktop\\vhdl-ams-code\\"), filter, true)) {
              println("Compiling file:" + f.getAbsolutePath)
              val (designFile, syntaxErrors) = ASTBuilder.fromFile(f.getAbsolutePath, configuration)
              for (msg <- syntaxErrors) {
                println("--" + f.getName + ": line:" + msg.position.line + " col:" + msg.position.charPosition + " " + msg.message)
              }
            }
            println("scala ende:" + (System.currentTimeMillis - start))
      */
      /*
      var filesCount = 0
      var lines = 0
      for (f <- Main.listFiles(new File("C:\\Users\\christian\\Desktop\\grlib-gpl-1.0.22-b4095\\"), filter, true)) {
        lines += io.Source.fromFile(f).getLines.size
        filesCount += 1
      }
      println(filesCount + " " + lines)
      */
    } catch {
      case ex@(_: java.lang.reflect.InvocationTargetException | _: java.lang.ExceptionInInitializerError) =>
        ex.getCause match {
          case exception@(_: VHDLRuntimeException | _: java.lang.NullPointerException) =>
            exception.setStackTrace(exception.getStackTrace.filterNot(element => element.getFileName == null || element.getFileName.endsWith(".scala") || element.getFileName.endsWith(".java")))
            exception.printStackTrace()
          case t => t.printStackTrace()
        }
      case e: Exception => e.printStackTrace
    }
  }

  def listFiles(directory: File, filter: FilenameFilter, recursive: Boolean): Seq[File] =
    directory.listFiles().toSeq.flatMap {
      case entry if (filter.accept(directory, entry.getName())) => Seq(entry)
      case entry if (recursive && entry.isDirectory()) => listFiles(entry, filter, recursive)
      case _ => Seq()
    }

  def parseCommandLineArguments(arguments: Array[String]): Configuration = {
    var ams = false
    val designLibrary = "work"
    var debugCompiler = false
    var debugCodeGenerator = false
    for (arg <- arguments) {
      arg match {
        case "-debugCompiler" => debugCompiler = true
        case "-debugCodeGenerator" => debugCodeGenerator = true
        case "-ams" => ams = true
        case _ => println("illegal argument:" + arg)
      }
    }
    new Configuration(ams, designLibrary, debugCompiler, debugCodeGenerator)
  }
}
/*
         ____________                           ___
        /  _________/\                         /  /\
       /  /\________\/_________   _________   /  / /_________
      /  /_/______   /  ______/\ /_____   /\ /  / //_____   /\
     /________   /\ /  /\_____\/_\____/  / //  / /_\____/  / /
     \______ /  / //  / /      /  ___   / //  / //  ___   / /
   _________/  / //  /_/___   /  /__/  / //  / //  /__/  / /
  /___________/ //________/\ /________/ //__/ //________/ /
  \___________\/ \________\/ \________\/ \__\/ \________\/
  
            ______________________________________________________________
         | / ____________    ____________________   ___    __________  /\
        _|/ /  _________/\  /_/_/_/_/_/_/_/_/_/_/  /  /\  /_/_/_/_/_/ / /
       | / /  /\________\/_________   _________   /  / /_________    / /
      _|/ /  /_/______   /  ______/\ /_____   /\ /  / //_____   /\  / /
     | / /________   /\ /  /\_____\/_\____/  / //  / /_\____/  / / / /
    _|/  \______ /  / //  / /      /  ___   / //  / //  ___   / / / /
   | / _________/  / //  /_/___   /  /__/  / //  / //  /__/  / / / /
  _|/ /___________/ //________/\ /________/ //__/ //________/ / / /
 | /  \___________\/ \________\/ \________\/ \__\/ \________\/ / /
 |/___________________________________________________________/ /
  \___________________________________________________________\/
  
                                                    ___
                                                  /  /\
MyClass      _________   _________   _________   /  / /_________
            /  ______/\ /  ______/\ /_____   /\ /  / //_____   /\
           /  /_____ \//  /\_____\/_\____/  / //  / /_\____/  / /
          /_____   /\ /  / /      /  ___   / //  / //  ___   / /
   ___   ______/  / //  /_/___   /  /__/  / //  / //  /__/  / /
  /__/\ /________/ //________/\ /________/ //__/ //________/ /
  \__\/ \________\/ \________\/ \________\/ \__\/ \________\/
  
  
         ________  __________________________________________________
      / ____  /\  __________________________________________________
     /_______/  \  __________________________________________________
     \_______\ \ \  __________________________________________________
     / ____  / /\ \  ____________   _____________________   ___   _____
    /_______/ /\/ / /  _________/\   ___________________   /  /\   _____
    \_______\ \/ / /  /\________\/_________   _________   /  / /_________
    / ____  / / / /  /_/______   /  ______/\ /_____   /\ /  / //_____   /\
   /_______/ / / /________   /\ /  /\_____\/_\____/  / //  / /_\____/  / /
   \_______\  /  \______ /  / //  / /      /  ___   / //  / //  ___   / /
   / ____  / / _________/  / //  /_/___   /  /__/  / //  / //  /__/  / /
  /_______/ / /___________/ //________/\ /________/ //__/ //________/ /
  \_______\/  \___________\/ \________\/ \________\/ \__\/ \________\/ 
    ________________________________________________________________
     ______________________________________________________________
      ____________________________________________________________
      */