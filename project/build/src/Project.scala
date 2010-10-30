import sbt._
import de.element34.sbteclipsify._

class Project(info: ProjectInfo) extends DefaultProject(info) with Eclipsify with IdeaProject with ProguardProject {
  val antlrRuntimeJar = "org.antlr" % "antlr-runtime" % "3.2" withSources()
  val asmJar = "asm" % "asm-all" % "3.3" withSources()
  val scalaTestJar = "org.scalatest" % "scalatest" % "1.2" withSources() withJavadoc()
  val commonsCLI = "commons-cli" % "commons-cli" % "1.2" withSources() withJavadoc()

  override val compileOptions = super.compileOptions ++ (Unchecked :: Deprecation :: /*Optimize ::*/ Nil)

  //scala doc options
  override def documentOptions = super.documentOptions ++ (documentTitle("OpenVC Scaladoc") /*:: LinkSource*/ :: Nil)

  //overrides the name of the produced jar.
  override val artifactID = "OpenVC"

  override val mainResources = super.mainResources +++ "NOTICE.txt" +++ "LICENSE.txt" +++ (path("licenses") * "*")

  // TODO add a task to generate parser from grammar
  // http://code.google.com/p/sbt-rats/source/browse/src/RatsProject.scala
  // http://code.google.com/p/simple-build-tool/wiki/ProjectDefinitionExamples#Conditional_Task

  override val proguardInJars = super.proguardInJars.filter{
    pathFinder =>
      val name = pathFinder.asFile.getName
      !(name.contains("scalatest") || name.contains("sources") || name.contains("javadoc"))
  } +++ Path.fromFile(scalaLibraryJar)

  //override val minJarName = artifactBaseName + ".min.jar"

  val proguardKeepMain = """-keepclasseswithmembers public class at.jku.ssw.openvc.* {
    public static void main(java.lang.String[]);
  }"""

  val proguardKeepRuntime = """-keep public class at.jku.ssw.openvs.*"""

  override val proguardOptions = List(proguardKeepMain, proguardKeepRuntime, "-dontskipnonpubliclibraryclasses", "-dontskipnonpubliclibraryclassmembers",
    "-printconfiguration", "-whyareyoukeeping class org.antlr.runtime.CommonTree")
}
