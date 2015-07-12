package models.repository

import javax.inject.Inject

import com.google.inject.ImplementedBy

import scala.concurrent.Future
import play.api.Play
import models.{ShiftType, Shift, ChildPresence}

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import slick.driver.JdbcProfile
import models.table.{ShiftTypeTableSlice, ShiftTableSlice, ChildrenToShiftsTableSlice}


@ImplementedBy(classOf[SlickChildPresenceRepository])
trait ChildPresenceRepository {
  def register(presence: ChildPresence): Future[Int]
  def unregister(presence: ChildPresence): Future[Int]

  def findPresencesForChild(childId: Long): Future[Seq[(Shift, ShiftType)]]
}


class SlickChildPresenceRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends ChildPresenceRepository
  with HasDatabaseConfig[JdbcProfile]
  with ChildrenToShiftsTableSlice
  with ShiftTableSlice
  with ShiftTypeTableSlice
{
  import driver.api._

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val presences = TableQuery[ChildrenToShiftsTable]
  val shifts = TableQuery[ShiftTable]
  val shiftTypes = TableQuery[ShiftTypeTable]

  override def register(presence: ChildPresence): Future[Int] = db.run(presences += presence)
  override def unregister(presence: ChildPresence): Future[Int] = db.run(presences.filter(_.shiftId === presence.shiftId).delete)

  override def findPresencesForChild(childId: Long): Future[Seq[(Shift, ShiftType)]] = {
    db.run {
      presences
        .filter(_.childId === childId)
        .join(shifts).on(_.shiftId === _.id).map(_._2)
        .join(shiftTypes).on(_.shiftTypeId === _.id)
        .result
    }
  }
}

