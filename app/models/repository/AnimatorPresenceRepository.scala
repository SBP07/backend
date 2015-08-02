package models.repository

import javax.inject.Inject

import com.google.inject.ImplementedBy

import scala.concurrent.Future
import play.api.Play
import models.{AnimatorPresence, ShiftType, Shift}

import play.api.db.slick.DatabaseConfigProvider
import play.api.db.slick.HasDatabaseConfig
import slick.driver.JdbcProfile
import models.table.{AnimatorToShiftsTableSlice, ShiftTypeTableSlice, ShiftTableSlice}


@ImplementedBy(classOf[SlickAnimatorPresenceRepository])
trait AnimatorPresenceRepository {
  def register(presence: AnimatorPresence): Future[Int]
  def unregister(presence: AnimatorPresence): Future[Int]

  def findPresencesForAnimator(animatorId: Long): Future[Seq[(Shift, ShiftType)]]
}


class SlickAnimatorPresenceRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends AnimatorPresenceRepository
  with HasDatabaseConfig[JdbcProfile]
  with AnimatorToShiftsTableSlice
  with ShiftTableSlice
  with ShiftTypeTableSlice
{
  import driver.api._

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val presences = TableQuery[AnimatorToShiftsTable]
  val shifts = TableQuery[ShiftTable]
  val shiftTypes = TableQuery[ShiftTypeTable]

  override def register(presence: AnimatorPresence): Future[Int] = db.run(presences += presence)
  override def unregister(presence: AnimatorPresence): Future[Int] = db.run {
    presences
      .filter(_.shiftId === presence.shiftId)
      .filter(_.animatorId === presence.animatorId)
      .delete
  }

  override def findPresencesForAnimator(animatorId: Long): Future[Seq[(Shift, ShiftType)]] = {
    db.run {
      presences
        .filter(_.animatorId === animatorId)
        .join(shifts).on(_.shiftId === _.id).map(_._2)
        .join(shiftTypes).on(_.shiftTypeId === _.id)
        .result
    }
  }
}
