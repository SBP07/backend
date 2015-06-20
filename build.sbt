name := "speelsysteem"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

scalaVersion := "2.11.6"

// Resolvers

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "scalaz-bintray http" at "http://dl.bintray.com/scalaz/releases"


resolvers += Classpaths.sbtPluginReleases

libraryDependencies ++= Seq(
  evolutions,
  cache,
  ws,
  specs2 % Test,

//  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.0"

  "com.typesafe.slick" %% "slick" % "3.0.0",
  "com.typesafe.play" %% "play-slick" % "1.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.0",
  "com.h2database" % "h2" % "1.3.175",
  "mysql" % "mysql-connector-java" % "5.1.21",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",

  "com.github.tototoshi" %% "scala-csv" % "1.2.1",

  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "org.webjars.bower" % "angular-bootstrap" % "0.12.1",
  "org.webjars.bower" % "angular" % "1.3.15",
  "org.webjars.bower" % "angular-route" % "1.3.15",
  "org.webjars.bower" % "xdate" % "0.8.1",
  "org.webjars" % "bootstrap" % "3.1.1-2"
)

routesGenerator := InjectedRoutesGenerator

fork in run := true
