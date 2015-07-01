package models.repository

import javax.inject.Inject

import com.google.inject.ImplementedBy


import java.util.Date
import scala.concurrent.Future
import play.api.Play
import _root_.models.ChildPresence

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import slick.driver.JdbcProfile
import _root_.models.table.ChildrenToShiftsTable


@ImplementedBy(classOf[SlickChildPresenceRepository])
trait ChildPresenceRepository {
  def register(presence: ChildPresence): Future[Int]
  def unregister(presence: ChildPresence): Future[Int]
}


class SlickChildPresenceRepository extends ChildPresenceRepository with HasDatabaseConfig[JdbcProfile] with ChildrenToShiftsTable {
  protected val dbConfig = DatabaseConfigProvider.get[JdbcProfile](Play.current)

  import driver.api._

//  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val presences = TableQuery[ChildrenToShifts]

  override def register(presence: ChildPresence): Future[Int] = db.run(presences += presence)
  override def unregister(presence: ChildPresence): Future[Int] = db.run(presences.filter(_.shiftId === presence.shiftId).delete)

}

