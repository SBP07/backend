package models.repository

import javax.inject.Inject

import com.google.inject.ImplementedBy

import scala.concurrent.Future
import play.api.Play
import models.{VolunteerPresence, ShiftType, Shift}

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import slick.driver.JdbcProfile
import models.table.{VolunteerToShiftsTableSlice, ShiftTypeTableSlice, ShiftTableSlice}


@ImplementedBy(classOf[SlickVolunteerPresenceRepository])
trait VolunteerPresenceRepository {
  def register(presence: VolunteerPresence): Future[Int]
  def unregister(presence: VolunteerPresence): Future[Int]

  def findPresencesForVolunteer(volunteerId: Long): Future[Seq[(Shift, ShiftType)]]
}


class SlickVolunteerPresenceRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends VolunteerPresenceRepository
  with HasDatabaseConfig[JdbcProfile]
  with VolunteerToShiftsTableSlice
  with ShiftTableSlice
  with ShiftTypeTableSlice
{
  import driver.api._

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val presences = TableQuery[VolunteerToShiftsTable]
  val shifts = TableQuery[ShiftTable]
  val shiftTypes = TableQuery[ShiftTypeTable]

  override def register(presence: VolunteerPresence): Future[Int] = db.run(presences += presence)
  override def unregister(presence: VolunteerPresence): Future[Int] = db.run {
    presences
      .filter(_.shiftId === presence.shiftId)
      .filter(_.volunteerId === presence.volunteerId)
      .delete
  }

  override def findPresencesForVolunteer(volunteerId: Long): Future[Seq[(Shift, ShiftType)]] = {
    db.run {
      presences
        .filter(_.volunteerId === volunteerId)
        .join(shifts).on(_.shiftId === _.id).map(_._2)
        .join(shiftTypes).on(_.shiftTypeId === _.id)
        .result
    }
  }
}
