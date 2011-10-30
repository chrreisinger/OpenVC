//TODO sbt-antlr
//usage: sbt-antlr
//resolvers += "stefri" at "http://stefri.github.com/repo/snapshots"

//libraryDependencies += "com.github.stefri" %% "sbt-antlr" % "0.2-SNAPSHOT"

//sbt-idea
//usage: gen-idea
resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "0.11.0")

//xsbt-proguard-plugin
//usage: proguard
libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-proguard-plugin" % (v+"-0.1.1"))

//sbteclipse
//usage: eclipse same-targets with-sources
resolvers += Classpaths.typesafeResolver

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse" % "1.4.0")

//sbt-assembly
//usage: assembly or assembly-package-dependency
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.7.1")

//sbt-netbeans-plugin
//usage: netbeans create transitive
resolvers += ScalaToolsSnapshots

resolvers += "remeniuk repo" at "http://remeniuk.github.com/maven"

libraryDependencies += "org.netbeans" %% "sbt-netbeans-plugin" % "0.1.3"