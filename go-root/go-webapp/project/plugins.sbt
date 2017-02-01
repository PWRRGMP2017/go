// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.6")

addSbtPlugin("com.typesafe.sbt" % "sbt-aspectj" % "0.10.6")

// addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.0-M8")

addSbtPlugin("com.gilt.sbt" % "sbt-aspectjweaver" % "0.0.2")
