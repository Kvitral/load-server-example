
name := "load-server"

version := "0.1"

scalaVersion := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.11",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.0.11" % Test
)

libraryDependencies += "com.typesafe" % "config" % "1.3.2"

val circeVersion = "0.9.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-generic-extras",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies += "de.heikoseeberger" %% "akka-http-circe" % "1.19.0"


lazy val main = (project in file(".")).settings(
  assemblyJarName := "load-server.jar",
  mainClass := Some("ru.kvitral.Boot")
)
