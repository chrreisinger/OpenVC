import sbt._
import de.element34.sbteclipsify._

final class Project(info: ProjectInfo) extends DefaultProject(info) with Eclipsify with IdeaProject with ProguardProject {
  val antlrRuntimeJar = "org.antlr" % "antlr-runtime" % "3.3" withSources()
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
  } +++ scalaLibraryPath

  override def makeInJarFilter(file: String) = file match {
    case "scala-library.jar" => "!META-INF/**" + ",!library.properties"
    case _ => "!META-INF/**"
  }

  //override val minJarName = artifactBaseName + ".min.jar"

  override val proguardOptions = List(proguardKeepMain("at.jku.ssw.openvc.*"), proguardKeepLimitedSerializability, "-keep class scala.ScalaObject", "-keepclassmembers class scala.Some,scala.Tuple*,scala.collection.** { *; }",
    """-keep class at.jku.ssw.openvs.** { *; }""", "-dontskipnonpubliclibraryclasses", "-dontskipnonpubliclibraryclassmembers", "-dontnote", "-printconfiguration", "-whyareyoukeeping class scala.Tuple5")
}
