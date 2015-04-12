name := "speelsysteem"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"


libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "org.webjars" %% "webjars-play" % "2.3.0-2",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "bootstrap-select" % "1.6.3",
  "org.webjars" % "bootstrap-datepicker" % "1.3.1",
  "net.sf.opencsv" % "opencsv" % "2.0",
  "com.typesafe.slick" %% "slick" % "2.1.0",
  "com.typesafe.play" %% "play-slick" % "0.8.0",
  "com.h2database" % "h2" % "1.3.175",
  "mysql" % "mysql-connector-java" % "5.1.21",
  "joda-time" % "joda-time" % "2.7",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.github.tototoshi" %% "scala-csv" % "1.2.1",
  "com.github.nscala-time" %% "nscala-time" % "1.8.0"
)     

