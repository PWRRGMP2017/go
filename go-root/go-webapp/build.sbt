import play.Project._

name := "go-webapp"

version := "1.0"

javacOptions += "-Xlint:deprecation"     

playJavaSettings

libraryDependencies += "org.springframework" % "spring-context" % "4.3.6.RELEASE"
