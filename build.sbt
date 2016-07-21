name := "speelsysteem"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.webjars" %% "webjars-play" % "2.4.0-2",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "bootstrap-select" % "1.6.3",
  "org.webjars" % "bootstrap-datepicker" % "1.3.1",
  "org.webjars" % "jquery" % "1.11.3",
  "net.sf.opencsv" % "opencsv" % "2.0",
  "com.typesafe.slick" %% "slick" % "3.0.3",
  "com.typesafe.play" %% "play-slick" % "1.0.1",
  "com.h2database" % "h2" % "1.3.175",
  "mysql" % "mysql-connector-java" % "5.1.21",
  "joda-time" % "joda-time" % "2.7",
  "org.postgresql" % "postgresql" % "9.4-1206-jdbc41",
  "com.github.tototoshi" %% "scala-csv" % "1.2.1",
  "com.github.nscala-time" %% "nscala-time" % "1.8.0",
  "com.ibm" %% "couchdb-scala" % "0.7.2"
)

routesGenerator := InjectedRoutesGenerator
