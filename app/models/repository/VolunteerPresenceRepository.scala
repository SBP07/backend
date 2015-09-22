package models.repository

import java.time.LocalDate
import javax.inject.Inject

import com.google.inject.ImplementedBy

import scala.concurrent.Future
import models.{Volunteer, VolunteerPresence, ShiftType, Shift}

import helpers.Db._
import scala.concurrent.ExecutionContext.Implicits.global

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import slick.driver.JdbcProfile
import models.table.{VolunteerTableSlice, VolunteerToShiftsTableSlice, ShiftTypeTableSlice, ShiftTableSlice}


@ImplementedBy(classOf[SlickVolunteerPresenceRepository])
trait VolunteerPresenceRepository {
  def register(presence: VolunteerPresence): Future[Int]
  def unregister(presence: VolunteerPresence): Future[Int]

  def findPresencesForVolunteer(volunteerId: Long): Future[Seq[(Shift, ShiftType)]]
  def findPresencesByDate(date: LocalDate): Future[Seq[(Shift, ShiftType, Seq[Volunteer])]]
}


class SlickVolunteerPresenceRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends VolunteerPresenceRepository
  with HasDatabaseConfig[JdbcProfile]
  with VolunteerToShiftsTableSlice
  with ShiftTableSlice
  with ShiftTypeTableSlice
  with VolunteerTableSlice
{
  import driver.api._

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val presences = TableQuery[VolunteerToShiftsTable]
  val shifts = TableQuery[ShiftTable]
  val shiftTypes = TableQuery[ShiftTypeTable]
  val volunteers = TableQuery[VolunteerTable]

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

  override def findPresencesByDate(date: LocalDate): Future[Seq[(Shift, ShiftType, Seq[Volunteer])]] = {
    // TODO does not return shifts where no one is attending

    val result = db.run {
      (for {
        presence <- presences
        shift <- shifts if presence.shiftId === shift.id && shift.date === date
        shiftType <- shiftTypes if shiftType.id === shift.shiftTypeId
        volunteer <- volunteers if volunteer.id === presence.volunteerId
      } yield (shift, shiftType, volunteer)).result
    }

    result.map { res =>
      res
        .groupBy(_._1)
        .map { case (key, value) => (key, value.head._2, value.map(_._3))}
        .toSeq
    }
  }
}
