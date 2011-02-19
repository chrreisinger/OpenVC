import sbt._
import sbt.Process._
import de.element34.sbteclipsify._

final class Project(info: ProjectInfo) extends DefaultProject(info) with Eclipsify with IdeaProject with ProguardProject {
  val antlrComplete = "org.antlr" % "antlr" % "3.3"
  val antlrRuntimeJar = "org.antlr" % "antlr-runtime" % "3.3" withSources()
  val asmJar = "asm" % "asm-all" % "3.3.1" withSources()
  val scalaTestJar = "org.scalatest" % "scalatest" % "1.3" withSources() withJavadoc()
  val commonsCLI = "commons-cli" % "commons-cli" % "1.2" withSources() withJavadoc()

  override val compileOptions = super.compileOptions ++ (Unchecked :: Deprecation :: /*Optimize ::*/ Nil)

  //scala doc options
  override def documentOptions = super.documentOptions ++ (documentTitle("OpenVC Scaladoc") /*:: LinkSource*/ :: Nil)

  //overrides the name of the produced jar.
  override val artifactID = "OpenVC"

  override val mainResources = super.mainResources +++ "NOTICE.txt" +++ "LICENSE.txt" +++ (path("licenses") * "*")

  //Task to generate antlr parser and lexer from the grammar, code and ideas taken from:
  //http://code.google.com/p/sbt-rats/source/browse/src/RatsProject.scala
  //http://code.google.com/p/simple-build-tool/wiki/ProjectDefinitionExamples#Conditional_Task

  lazy val grammarPath = mainScalaSourcePath / "at" / "jku" / "ssw" / "openvc" / "parser" / "VHDL.g"

  lazy val grammarName = grammarPath.base

  //The directory in which the main module is stored.
  lazy val dir = Path.fromFile(grammarPath.asFile.getParent)

  //A task to delete the generated Antlr parser.
  lazy val cleanAntlr =
    cleanTask((dir / (grammarName + "Lexer.scala")) +++ (dir / (grammarName + "Parser.scala"))) describedAs ("delete the generated Antlr parser and lexer")

  /**
   * A task to run Antlr on the main parser module.  The generated parser and lexer
   * will be written to a file with the same name as the grammar,
   * but with a .scala extension.  This task depends on all .g files
   * in the project source directories, whether they are actually used by
   * the main module or not.
   */
  lazy val antlr = {
    val srcs = descendents(mainSourceRoots, "*.g")
    val fmt = "java -cp %s org.antlr.Tool %s"
    //Path of the generated parser
    val parserPath = dir / (grammarName + "Parser.scala")
    val classpath = Path.makeString(compileClasspath.get)
    val cmd = fmt.format(classpath, grammarPath)
    fileTask("Generate parser from " + grammarPath, parserPath from srcs) {
      if (cmd ! log == 0) {
        FileUtilities.clean(dir / (grammarName + ".tokens"), log)
      } else
        Some("Antlr! failed")
    } describedAs ("Generate Antlr parser and lexer")
  }

  //TODO enable actions when antlr supports scala
  //Make the antlr clean task run on project clean.
  //override def cleanAction = super.cleanAction dependsOn (cleanAntlr)

  //Run the antlr task before compilation.
  //override def compileAction = super.compileAction dependsOn (antlr)

  override val proguardInJars = super.proguardInJars.filter {
    pathFinder =>
      val name = pathFinder.asFile.getName
      !(name.contains("scalatest") || name.contains("sources") || name.contains("javadoc") || name.contains("antlr-3.3") || name.contains("stringtemplate"))
  } +++ scalaLibraryPath

  override def makeInJarFilter(file: String) = file match {
    case "scala-library.jar" => "!META-INF/**" + ",!library.properties"
    case _ => "!META-INF/**"
  }

  //override val minJarName = artifactBaseName + ".min.jar"

  override val proguardOptions = List(proguardKeepMain("at.jku.ssw.openvc.*"), proguardKeepLimitedSerializability, "-keep class scala.ScalaObject", "-keepclassmembers class scala.Some,scala.Tuple*,scala.collection.** { *; }",
    """-keep class at.jku.ssw.openvs.** { *; }""", "-dontskipnonpubliclibraryclasses", "-dontskipnonpubliclibraryclassmembers", "-dontnote", "-printconfiguration", "-whyareyoukeeping class scala.Tuple5")
}
