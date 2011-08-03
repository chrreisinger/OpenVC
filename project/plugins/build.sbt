//sbt-idea
resolvers += "sbt-idea-repo" at "http://mpeltonen.github.com/maven/"

libraryDependencies += "com.github.mpeltonen" %% "sbt-idea" % "0.10.0"

//xsbt-proguard-plugin
resolvers += "Proguard plugin repo" at "http://siasia.github.com/maven2"

libraryDependencies <+= sbtVersion("com.github.siasia" %% "xsbt-proguard-plugin" % _)

//sbteclipse
resolvers += {
  val typesafeRepoUrl = new java.net.URL("http://repo.typesafe.com/typesafe/releases")
  val pattern = Patterns(false, "[organisation]/[module]/[sbtversion]/[revision]/[type]s/[module](-[classifier])-[revision].[ext]")
  Resolver.url("Typesafe Repository", typesafeRepoUrl)(pattern)
}

libraryDependencies <<= (libraryDependencies, sbtVersion) { (deps, version) => 
  deps :+ ("com.typesafe.sbteclipse" %% "sbteclipse" % "1.3-RC1" extra("sbtversion" -> version))
}

//sbt-assembly
libraryDependencies <+= (sbtVersion) { sv => "com.eed3si9n" %% "sbt-assembly" % ("sbt" + sv + "_0.5") }

//sbt-netbeans-plugin
resolvers += ScalaToolsSnapshots

resolvers += "remeniuk repo" at "http://remeniuk.github.com/maven"

libraryDependencies += "org.netbeans" %% "sbt-netbeans-plugin" % "0.1.2"