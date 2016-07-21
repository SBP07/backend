name := "speelsysteem"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  "org.webjars" %% "webjars-play" % "2.4.0-2",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "bootstrap-select" % "1.6.3",
  "org.webjars" % "bootstrap-datepicker" % "1.3.1",
  "org.webjars" % "jquery" % "1.11.3",
  "com.typesafe.slick" %% "slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick" % "1.0.1",
  "joda-time" % "joda-time" % "2.7",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc41",
  "com.ibm" %% "couchdb-scala" % "0.7.2"
)

routesGenerator := InjectedRoutesGenerator
