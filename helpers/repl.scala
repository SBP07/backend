import play.core.StaticApplication, models._, play.api.Play.current, scala.slick.jdbc.JdbcBackend.Session, play.api.db.slick.Config.driver.simple._

try {
  new StaticApplication(new java.io.File("."))
} catch {
  case e: play.api.db.evolutions.InvalidDatabaseRevision => {
    println("\n\n\n WARNING: You should run sbt with the -DapplyEvolutions.default=true switch to automatically apply evolutions!")
    println("Currently, the database is in an invalid state, so queries won't work.")
  }
  case e : Throwable => throw e;
}

implicit val session = play.api.db.slick.DB.createSession
println("An implicit Session is now available")

val tq = TableQuery[ChildrenToShifts]
val children = TableQuery[ChildRepository]
val activities = TableQuery[ShiftRepository]
