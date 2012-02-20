//usage: sbt-antlr
resolvers += "stefri" at "http://stefri.github.com/repo/snapshots"

addSbtPlugin("com.github.stefri" % "sbt-antlr" % "0.2-SNAPSHOT")

//sbt-git-plugin
resolvers += Resolver.url("scalasbt", new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

resolvers += "jgit-repo" at "http://download.eclipse.org/jgit/maven"

addSbtPlugin("com.jsuereth" % "sbt-git-plugin" % "0.4")

//sbt-idea
//usage: gen-idea
resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0")

//xsbt-proguard-plugin
//usage: proguard
libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-proguard-plugin" % (v+"-0.1.1"))

//sbteclipse
//usage: eclipse same-targets withSource
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.0.0")

//sbt-assembly
//usage: assembly or assembly-package-dependency
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.7.3")

//sbt-netbeans-plugin
//usage: netbeans create transitive
resolvers += ScalaToolsSnapshots

resolvers += "remeniuk repo" at "http://remeniuk.github.com/maven"

libraryDependencies += "org.netbeans" %% "sbt-netbeans-plugin" % "0.1.4"