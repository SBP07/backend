name := "speelsysteem"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

//scalaVersion := "2.11.6"

// Resolvers

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/"

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += "scalaz-bintray http" at "http://dl.bintray.com/scalaz/releases"


resolvers += Classpaths.sbtPluginReleases

libraryDependencies ++= Seq(
  cache,
  ws,
  specs2 % Test,
  filters,

  "com.typesafe.play" %% "play-slick" % "1.0.1",
  "io.strongtyped" %% "active-slick" % "0.3.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.1",
  "com.h2database" % "h2" % "1.3.176",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",

  "org.apache.poi" % "poi" % "3.12",
  "org.apache.poi" % "poi-scratchpad" % "3.12"
)

routesGenerator := InjectedRoutesGenerator

fork in run := true
