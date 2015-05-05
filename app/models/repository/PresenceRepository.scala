package models.repository

import models._
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ForeignKeyQuery, ProvenShape}

private[models] class ChildrenToShifts(tag: Tag) extends Table[ChildPresence](tag, "child_to_shift") {
  private[models] val children = TableQuery[ChildRepository]
  private[models] val shifts = TableQuery[ShiftRepository]

  def * : ProvenShape[ChildPresence] = (childId, shiftId) <>(ChildPresence.tupled, ChildPresence.unapply)

  def childFK: ForeignKeyQuery[ChildRepository, Child] = foreignKey("child_fk", childId, children)(_.id)

  private[models] def childId = column[Long]("child_id")

  def shiftFK: ForeignKeyQuery[ShiftRepository, Shift] = foreignKey("shift_fk",
    shiftId, shifts)(_.id, onDelete = ForeignKeyAction.Cascade)

  private[models] def pk = primaryKey("child_to_shift_pk", (childId, shiftId))

  private[models] def shiftId = column[Long]("shift_id")
}

object ChildPresenceRepository {
  private val children = TableQuery[ChildRepository]
  private val shifts = TableQuery[ShiftRepository]
  private val presences = TableQuery[ChildrenToShifts]

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
