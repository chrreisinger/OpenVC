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

import util.SourceFile
import CompilationUnit.Configuration
import at.jku.ssw.openvs.VHDLRuntime.VHDLRuntimeException
import at.jku.ssw.openvs.Simulator
import java.io.{PrintWriter, FilenameFilter, File}

object Main {
  def main(arguments: Array[String]) {
    try {
      /*
      parseCommandLineArguments(arguments).foreach {
        case (configuration, files) =>
          val filter = new FilenameFilter() {
            override def accept(dir: File, name: String): Boolean = (name.endsWith(".vhd") || name.endsWith(".vhdl")) && !name.endsWith("in.vhd")
          }
          val allVHDLFiles = listFiles(new File("""C:\Users\christian\Desktop\grlib-gpl-1.0.22-b4095\"""), filter, true)
          for (i <- 0 to 10) {
            val start = System.currentTimeMillis
            allVHDLFiles.foreach {
              file =>
                val result = VHDLCompiler.compile(new CompilationUnit(SourceFile.fromFile(file.getAbsolutePath), configuration))
                result.printMessages(new PrintWriter(System.out))
            }
            println("time:" + (System.currentTimeMillis - start))
          }
      }
      return
      */
      parseCommandLineArguments(arguments).foreach {
        case (configuration, files) =>
          val classFilter = new FilenameFilter() {
            override def accept(dir: File, name: String): Boolean = name.endsWith(".class")
          }
          files.map(file => VHDLCompiler.compile(new CompilationUnit(SourceFile.fromFile(file, configuration.encoding), configuration))).foreach(unit => unit.printMessages(new PrintWriter(System.out)))
          Simulator.loadFiles(this.getClass.getClassLoader, configuration.outputDirectory, listFiles(new File(configuration.libraryOutputDirectory), classFilter, true).map(file => file.getPath.substring(file.getPath.indexOf('\\') + 1).split('.').head.replace('\\', '.')), List("std.jar", "ieee.jar"))
          Simulator.runClass(this.getClass.getClassLoader, configuration.outputDirectory, configuration.designLibrary + ".alu_tb_body", "main$-1404437944", List("std.jar", "ieee.jar"))
      }
    } catch {
      case fileNotFoundException: java.io.FileNotFoundException => println("could not find file: " + fileNotFoundException.getMessage)
      case ex@(_: java.lang.reflect.InvocationTargetException | _: java.lang.ExceptionInInitializerError) if (ex.getCause.getStackTrace.exists(element => element != null && (element.getFileName.endsWith(".vhd") || element.getFileName.endsWith(".vhdl")))) =>
        ex.getCause match {
          case exception@(_: VHDLRuntimeException | _: java.lang.NullPointerException) =>
            exception.setStackTrace(exception.getStackTrace.filterNot(element => element.getFileName == null || element.getFileName.endsWith(".scala") || element.getFileName.endsWith(".java")))
            exception.printStackTrace()
          case t => t.printStackTrace()
        }
      case e: Exception => e.printStackTrace()
    }
  }

  def listFiles(directory: File, filter: FilenameFilter, recursive: Boolean): Seq[File] =
    directory.listFiles().toSeq.flatMap {
      case entry if (filter.accept(directory, entry.getName())) => Seq(entry)
      case entry if (recursive && entry.isDirectory()) => listFiles(entry, filter, recursive)
      case _ => Seq()
    }

  def parseCommandLineArguments(arguments: Array[String]): Option[(Configuration, Seq[String])] = {
    import org.apache.commons.cli._
    val standardOptions = new Options()
    standardOptions.addOption("ams", false, "Enables VHDL-AMS in the parser")
    standardOptions.addOption("help", false, "Print a synopsis of standard options")
    standardOptions.addOption("version", false, "Print product version and exit")
    standardOptions.addOption("v2008", false, "Enables VHDL 2008 in the parser")
    standardOptions.addOption("nowarn", false, "Generate no warnings")
    standardOptions.addOption("X", false, "Print a synopsis of nonstandard options")

    val libraryNameOption = new Option("designLibrary", true, "Name of the design library to which this design file belongs")
    libraryNameOption.setArgName("libraryName")
    standardOptions.addOption(libraryNameOption)

    val encodingOption = new Option("encoding", true, "Specify character encoding used by source files. The default value is platform-specific " + """(Linux: " UTF8 ", Windows: " Cp1252 ").""")
    encodingOption.setArgName("encoding")
    standardOptions.addOption(encodingOption)

    val outputDirectoryOption = new Option("outputDirectory", true, "Specify where to place generated files")
    outputDirectoryOption.setArgName("directory")
    standardOptions.addOption(outputDirectoryOption)

    val libraryDirectoryOption = new Option("libraryDirectory", true, "Specify where to find existing libraries")
    libraryDirectoryOption.setArgName("directory")
    standardOptions.addOption(libraryDirectoryOption)

    val extendedOptions = new Options()
    extendedOptions.addOption("XparseOnly", false, "Parses only the souce code, no code will be generated. This is the same as -XrunOnlyToPhase parser")
    extendedOptions.addOption("XdebugCompiler", false, "Prints compiler debugging information")
    extendedOptions.addOption("XdebugCodeGenerator", false, "Prints the generated code to stdout")
    extendedOptions.addOption("XshowPhases", false, "Print a synopsis of compiler phases")

    val XrunOnlyToPhaseOption = new Option("XrunOnlyToPhase", true, "Run only to a specific compiler phase, skip the rest")
    XrunOnlyToPhaseOption.setArgName("phase")
    extendedOptions.addOption(XrunOnlyToPhaseOption)

    try {
      val parser = new PosixParser()
      val options = new Options()
      (Seq(standardOptions.getOptions.toArray(new Array[Option](0)): _*) ++ Seq(extendedOptions.getOptions.toArray(new Array[Option](0)): _*)).foreach(options.addOption)
      val line = parser.parse(options, arguments)

      if (line.hasOption("help")) {
        val helpFormatter = new HelpFormatter()
        helpFormatter.setSyntaxPrefix("Usage: ")
        helpFormatter.printHelp(100, "openvc <options> <source files>", "where possible options include:", standardOptions, null, false)
        None
      } else if (line.hasOption("version")) {
        println("OpenVC 0.1 Copyright (C) 2011 Christian Reisinger")
        None
      } else if (line.hasOption("XshowPhases")) {
        println("compiler phases:")
        VHDLCompiler.AllPhases.foreach(phase => println(phase.name + ": " + phase.description))
        None
      } else if (line.hasOption("X")) {
        val helpFormatter = new HelpFormatter()
        helpFormatter.setSyntaxPrefix("extended options: ")
        helpFormatter.printHelp(100, " ", null, extendedOptions, null, false)
        None
      } else {
        val outputDirectory = line.getOptionValue("outputDirectory", "output")
        val libraryDirectory = line.getOptionValue("libraryDirectory", "vhdlLibs")
        val XrunOnlyToPhase =
          if (line.hasOption("XparseOnly")) scala.Option(VHDLCompiler.AllPhases.head.name)
          else scala.Option(line.getOptionValue("XrunOnlyToPhase", null)).map {
            phase => VHDLCompiler.AllPhases.find(_.name == phase) match {
              case Some(phase) => phase.name
              case None =>
                println("phase:" + phase + " not found")
                VHDLCompiler.AllPhases.last.name
            }
          }

        val configuration = new Configuration(
          enableAMS = line.hasOption("ams"),
          enableVhdl2008 = line.hasOption("v2008"),
          noWarn = line.hasOption("nowarn"),
          encoding = scala.Option(line.getOptionValue("encoding", null)),
          outputDirectory = if (outputDirectory.last == File.separatorChar) outputDirectory else outputDirectory + File.separator,
          designLibrary = line.getOptionValue("designLibrary", "work"),
          libraryDirectory = if (libraryDirectory.last == File.separatorChar) libraryDirectory else libraryDirectory + File.separator,
          XrunOnlyToPhase = XrunOnlyToPhase,
          XdebugCompiler = line.hasOption("XdebugCompiler"),
          XdebugCodeGenerator = line.hasOption("XdebugCodeGenerator")
        )
        Some((configuration, line.getArgs.toSeq))
      }
    }
    catch {
      case exp: ParseException =>
        println(exp.getMessage())
        None
    }
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