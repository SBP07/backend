package models.repositories.slick

import javax.inject.Inject

import slick.driver.PostgresDriver.api._
import models._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.Future

class ChildPresenceRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)
{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  private val children = TableQuery[ChildTable]
  private val shifts = TableQuery[ShiftTable]
  private val presences = TableQuery[ChildrenToShiftsTable]

  def all: Future[Seq[(Child, Shift)]] = db.run {
    (for {
      child <- children
      act <- child.shifts
    } yield (child, act)).result
  }

  def findAllForChild(id: Long): Future[Seq[(Shift, ShiftType)]] = db.run {
    (for {
      child <- children if child.id === id
      act <- child.shifts
      actType <- act.shiftType
    } yield (act, actType)).result
  }

  def findAllForShift(id: Long): Future[Seq[(Child, Shift)]] = db.run {
    (for {
      child <- children
      act <- child.shifts if act.id === id
    } yield (child, act)).result
  }

  def register(presence: ChildPresence): Future[Unit] = db.run(presences += presence).map(_ => ())
  def register(presence: List[ChildPresence]): Future[Unit] = db.run(presences ++= presence).map(_ => ())

  def unregister(presence: ChildPresence): Future[Unit] = db.run {
    presences.filter(_.shiftId === presence.shiftId)
      .filter(_.childId === presence.childId)
      .delete
  } map(_ => ())

  def unregister(presence: List[ChildPresence]): Future[Unit] = db.run {
    presences.filter(_.shiftId inSet presence.map(_.shiftId))
      .filter(_.childId inSet presence.map(_.childId))
      .delete
  } map(_ => ())

  def count: Future[Int] = db.run(presences.length.result)
}
