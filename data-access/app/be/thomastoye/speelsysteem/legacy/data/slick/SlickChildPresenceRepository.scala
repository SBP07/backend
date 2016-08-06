package be.thomastoye.speelsysteem.legacy.data.slick

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.data.ChildPresenceRepository
import be.thomastoye.speelsysteem.legacy.models.{ChildPresence, LegacyChild, Shift, ShiftType}
import be.thomastoye.speelsysteem.models.Child
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

class SlickChildPresenceRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
  extends ChildPresenceRepository {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  private val children = TableQuery[ChildTable]
  private val shifts = TableQuery[ShiftTable]
  private val presences = TableQuery[ChildrenToShiftsTable]

  override def all: Future[Seq[(LegacyChild, Shift)]] = db.run {
    (for {
      child <- children
      act <- child.shifts
    } yield (child, act)).result
  }

  override def findAllForChild(id: Child.Id): Future[Seq[(Shift, ShiftType)]] = db.run {
    (for {
      child <- children if child.id === id
      act <- child.shifts
      actType <- act.shiftType
    } yield (act, actType)).result
  }

  override def findAllForShift(id: Long): Future[Seq[(LegacyChild, Shift)]] = db.run {
    (for {
      child <- children
      act <- child.shifts if act.id === id
    } yield (child, act)).result
  }

  override def register(presence: ChildPresence): Future[Unit] = db.run(presences += presence).map(_ => ())
  override def register(presence: Seq[ChildPresence]): Future[Unit] = db.run(presences ++= presence).map(_ => ())

  override def unregister(presence: ChildPresence): Future[Unit] = db.run {
    presences.filter(_.shiftId === presence.shiftId)
      .filter(_.childId === presence.childId)
      .delete
  } map(_ => ())

  override def unregister(presence: Seq[ChildPresence]): Future[Unit] = db.run {
    presences.filter(_.shiftId inSet presence.map(_.shiftId))
      .filter(_.childId inSet presence.map(_.childId))
      .delete
  } map(_ => ())

  override def count: Future[Int] = db.run(presences.length.result)
}
