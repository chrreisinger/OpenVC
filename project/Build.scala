import sbt._
import Keys._
import Package.ManifestAttributes
import java.util.jar.Attributes.Name._

object OpenVCBuild extends Build {
  val dependencies = Seq(
    //download antlr from github with my scala target changes, switch back to a maven version when the scala target is good enough
    "org.antlr" % "antlr" % "3.3" % "antlr->default" from "http://cloud.github.com/downloads/chrreisinger/OpenVC/antlr-3.3.jar" intransitive(),
    "org.antlr" % "antlr-runtime" % "3.4" intransitive(),
    "asm" % "asm-all" % "3.3.1",
    "commons-cli" % "commons-cli" % "1.2",
    "org.scalatest" % "scalatest_2.9.0" % "1.6.1" % "test",
    "junit" % "junit" % "4.8.2" % "test"
  )

  val buildVersion = "0.1"
  val buildSettings = Defaults.defaultSettings ++ sbtassembly.Plugin.assemblySettings ++ SbtShPlugin.settings ++ ProguardPlugin.proguardSettings ++ Seq(
    name := "OpenVC",
    organization := "com.github.chrreisinger",
    scalaVersion := "2.9.0-1",
    //TODO resources := Seq("NOTICE.txt", "LICENSE.txt"), // ++ (path("licenses") * "*"),
    //TODO clean <<= clean.dependsOn(cleanAntlr), //Make the antlr clean task run on project clean.
    //TODO compile <<= compile.dependsOn(antlr), //Run the antlr task before compilation.
    javacOptions ++= Seq("-source", "1.7", "-target", "1.7"),
    scalacOptions ++= Seq("-deprecation", "-unchecked", "-g:vars", "-target:jvm-1.5", "-explaintypes"),
    scaladocOptions := Seq("-unchecked", "-doc-title", "OpenVC scaladoc", "-doc-source-url", "https://github.com/chrreisinger/OpenVC/tree/master/src/main/scala", "-doc-version", buildVersion),
    version := buildVersion,
    packageOptions ++= Seq[PackageOption](ManifestAttributes(
      (IMPLEMENTATION_TITLE, "OpenVC"),
      (IMPLEMENTATION_URL, "https://github.com/chrreisinger/OpenVC"),
      (IMPLEMENTATION_VENDOR, "Christian Reisinger"),
      (IMPLEMENTATION_VERSION, buildVersion),
      (SEALED, "true"))
    ),
    ivyConfigurations += config("antlr").hide,
    libraryDependencies := dependencies,
    ProguardPlugin.makeInJarFilter <<= (ProguardPlugin.makeInJarFilter) {
      (makeInJarFilter) => {
        (file) => file match {
          case "scala-library.jar" => "!META-INF/**" + ",!library.properties"
          case _ => "!META-INF/**"
        }
      }
    },
    ProguardPlugin.proguardOptions ++= Seq(
      ProguardPlugin.keepMain("at.jku.ssw.openvc.*"),
      ProguardPlugin.keepLimitedSerializability,
      "-keep class scala.ScalaObject",
      "-keepclassmembers class scala.Some,scala.Tuple*,scala.collection.** { *; }",
      "-keep class at.jku.ssw.openvs.** { *; }",
      "-dontskipnonpubliclibraryclasses",
      "-dontskipnonpubliclibraryclassmembers",
      "-dontnote",
      "-printconfiguration",
      "-whyareyoukeeping class scala.Tuple5"
    )
  )

  //TODO add task to generate parser and lexer from ANTLR grammar
  lazy val common = Project("OpenVC", file("."), settings = buildSettings)
}


object SbtShPlugin extends Plugin {
  override lazy val settings = Seq(Keys.commands += shCommand)

  def shCommand = Command.args("sh", "<shell command>") {
    (state, args) =>
      args.mkString(" ").!
      state
  }

}
