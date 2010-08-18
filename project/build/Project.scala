import sbt._
import de.element34.sbteclipsify._

class Project(info: ProjectInfo) extends DefaultProject(info) with Eclipsify with IdeaProject with ProguardProject {
  val antlrRuntimeJar = "org.antlr" % "antlr-runtime" % "3.2"
  val asmJar = "asm" % "asm-all" % "3.2"
  val scalaTestJar = "org.scalatest" % "scalatest" % "1.2"

  override val compileOptions = super.compileOptions ++ (Unchecked :: Deprecation :: /*Optimize ::*/ Nil)
  //overrides the name of the produced jar.
  override val artifactID = "OpenVC"

  override val mainResources = super.mainResources +++ "NOTICE.txt" +++ "LICENSE.txt" +++ (path("licenses") * "*")

  override val proguardInJars = super.proguardInJars.filter(!_.asFile.getName.contains("scalatest")) +++ Path.fromFile(scalaLibraryJar)
  
  //override val minJarName = artifactBaseName + ".min.jar"
  
  val proguardKeepMain = """-keepclasseswithmembers public class at.jku.ssw.openvc.* {
    public static void main(java.lang.String[]);
  }"""

  val proguardKeepRuntime = """-keep public class at.jku.ssw.openvs.*"""
  
  override val proguardOptions = List(proguardKeepMain, proguardKeepRuntime, "-dontskipnonpubliclibraryclasses", "-dontskipnonpubliclibraryclassmembers",
    "-printconfiguration", "-whyareyoukeeping class org.antlr.runtime.CommonTree")
}