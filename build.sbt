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

  "com.typesafe.play" %% "play-slick" % "1.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.0.0",
  "com.h2database" % "h2" % "1.3.175",
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",


  "org.webjars" %% "webjars-play" % "2.4.0-1",

  "org.webjars.bower" % "angular-bootstrap" % "0.12.1",
  "org.webjars.bower" % "angular" % "1.4.1",
  "org.webjars.bower" % "angular-ui-router" % "0.2.15",
  "org.webjars.npm" % "angular-material" % "0.10.0",
  "org.webjars.bower" % "angular-material-icons" % "0.5.0",
  "org.webjars.bower" % "angular-resource" % "1.4.0",
  "org.webjars.bower" % "angular-animate" % "1.4.0",
  "org.webjars.bower" % "angular-aria" % "1.4.0",
  "org.webjars.bower" % "angular-messages" % "1.4.0",
  "org.webjars.bower" % "angular-i18n" % "1.4.0",

  "org.webjars.bower" % "requirejs" % "2.1.18",


  "io.github.cloudify" %% "spdf" % "1.3.1"
)

routesGenerator := InjectedRoutesGenerator

fork in run := true
