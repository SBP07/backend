package models.repositories.slick

import models.{Child, ChildPresence, Shift}
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ForeignKeyQuery, ProvenShape}

private[models] class ChildrenToShifts(tag: Tag) extends Table[ChildPresence](tag, "child_to_shift") {
  private[models] val children = TableQuery[ChildRepository]
  private[models] val shifts = TableQuery[ShiftRepository]

  private[models] def childId = column[Long]("child_id")
  private[models] def shiftId = column[Long]("shift_id")

  def * : ProvenShape[ChildPresence] = (childId, shiftId) <> (ChildPresence.tupled, ChildPresence.unapply _)

  def childFK: ForeignKeyQuery[ChildRepository, Child] = foreignKey("child_fk", childId, children)(_.id)
  def shiftFK: ForeignKeyQuery[ShiftRepository, Shift] = foreignKey("shift_fk",
    shiftId, shifts)(_.id, onDelete=ForeignKeyAction.Cascade)

  private[models] def pk = primaryKey("child_to_shift_pk", (childId, shiftId))
}
