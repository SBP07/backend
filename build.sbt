name := "speelsysteem"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  "org.webjars" %% "webjars-play" % "2.2.1-2",
  "org.webjars" % "bootstrap" % "3.1.1-2",
  "net.sf.opencsv" % "opencsv" % "2.0"
)     

play.Project.playJavaSettings
