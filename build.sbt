name := "speelsysteem"

version := "1.0-SNAPSHOT"

lazy val commonSettings = Seq(
  scalaVersion := "2.11.8",
  scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .enablePlugins(PlayScala)
  .dependsOn(dataAccess)
  .aggregate(dataAccess)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  jdbc,
  "org.webjars" %% "webjars-play" % "2.4.0-2",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "org.webjars" % "bootstrap-select" % "1.6.3",
  "org.webjars" % "bootstrap-datepicker" % "1.3.1",
  "org.webjars" % "jquery" % "1.11.3",
  //  "joda-time" % "joda-time" % "2.7",
  "com.ibm" %% "couchdb-scala" % "0.7.2"
)

routesGenerator := InjectedRoutesGenerator

lazy val models = project
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "joda-time" % "joda-time" % "2.7",
      "org.joda" % "joda-convert" % "1.2"
    )
  )

lazy val dataAccess = Project("data-access", file("data-access"))
  .settings(commonSettings)
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      "org.postgresql" % "postgresql" % "9.4-1206-jdbc41",
      "com.typesafe.slick" %% "slick" % "3.0.3",
      "com.typesafe.play" %% "play-slick" % "1.0.1"
    )
  )
  .dependsOn(models)
  .aggregate(models)
