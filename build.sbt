name := """speelsysteem"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test,

  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  "io.strongtyped" %% "active-slick" % "0.3.3",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.h2database" % "h2" % "1.3.176",

  "net.ceedubs" %% "ficus" % "1.1.2",
  "net.codingwell" %% "scala-guice" % "4.0.0",

  "com.adrianhurt" %% "play-bootstrap3" % "0.4.4-P24",
  "org.webjars" %% "webjars-play" % "2.4.0-1",

  "com.mohiva" %% "play-silhouette" % "3.0.0",
  filters,


  "org.scalaz" %% "scalaz-core" % "7.1.5"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

resolvers += "Atlassian Releases" at "https://maven.atlassian.com/public/"

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)
