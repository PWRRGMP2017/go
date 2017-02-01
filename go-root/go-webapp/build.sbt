import play.Project._
import com.typesafe.sbt.SbtAspectj._
import com.typesafe.sbt.SbtAspectj.AspectjKeys._

name := "go-webapp"

version := "1.0"

javacOptions += "-Xlint:deprecation"     

playJavaSettings

libraryDependencies += "org.aspectj" % "aspectjrt" % "1.8.10"

libraryDependencies += "org.aspectj" % "aspectjweaver" % "1.8.10"

// enablePlugins(AspectJWeaver)

val main = (project in file("."))
.settings(aspectjSettings: _*)
.settings(
    inputs in Aspectj <+= compiledClasses,
    products in Compile <<= products in Aspectj,
    products in Runtime <<= products in Compile)