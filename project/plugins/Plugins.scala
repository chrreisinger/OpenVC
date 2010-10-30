import sbt._

class Plugins(info: ProjectInfo) extends PluginDefinition(info) {
  lazy val eclipsifyPlugin = "de.element34" % "sbt-eclipsify" % "0.6.1"

  val ideaRepo = "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"
  val ideaPlugin = "com.github.mpeltonen" % "sbt-idea-plugin" % "0.1-SNAPSHOT"
  val proguardPlugin = "org.scala-tools.sbt" % "sbt-proguard-plugin" % "0.0.+"
}

