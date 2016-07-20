package models.repositories.slick

import play.api.db.slick.Config.driver.simple._
import models._

object ChildPresenceRepository {
  private val children = TableQuery[ChildRepository]
  private val shifts = TableQuery[ShiftRepository]
  private val presences = TableQuery[ChildrenToShifts]

  def all(implicit s: Session): Seq[(Child, Shift)] = (for {
    child <- children
    act <- child.shifts
  } yield (child, act)).run

  def findAllForChild(id: Long)(implicit s: Session): Seq[(Shift, ShiftType)] = (for {
    child <- children if child.id === id
    act <- child.shifts
    actType <- act.shiftType
  } yield (act, actType)).run

  def findAllForShift(id: Long)(implicit s: Session): Seq[(Child, Shift)] = (for {
    child <- children
    act <- child.shifts if act.id === id
  } yield (child, act)).run

  def register(presence: ChildPresence)(implicit s: Session): Unit = presences += presence
  def register(presence: List[ChildPresence])(implicit s: Session): Unit = presences ++= presence

  def unregister(presence: ChildPresence)(implicit s: Session): Unit =
    presences.filter(_.shiftId === presence.shiftId)
              .filter(_.childId === presence.childId)
              .delete
              .run

  def unregister(presence: List[ChildPresence])(implicit s: Session): Unit =
    presences.filter(_.shiftId inSet presence.map(_.shiftId))
              .filter(_.childId inSet presence.map(_.childId))
              .delete
              .run

  def count(implicit s: Session): Int = presences.length.run
}
