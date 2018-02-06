// Comment to get more information during initialization
logLevel := Level.Warn
resolvers += Resolver.sbtPluginRepo("releases")


resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.10")

addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")

addSbtPlugin("org.wartremover" % "sbt-wartremover" % "2.2.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.3.2")

// updates and dependency plugins
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.9.0")
addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.3.3")

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.1")