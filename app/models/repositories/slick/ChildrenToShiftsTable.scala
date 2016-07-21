package models.repositories.slick

import models.{Child, ChildPresence, Shift}
import slick.driver.PostgresDriver.api._

import slick.lifted.{ForeignKeyQuery, ProvenShape}

private[models] class ChildrenToShiftsTable(tag: Tag) extends Table[ChildPresence](tag, "child_to_shift") {
  private[models] val children = TableQuery[ChildTable]
  private[models] val shifts = TableQuery[ShiftTable]

  private[models] def childId = column[Long]("child_id")
  private[models] def shiftId = column[Long]("shift_id")

  def * : ProvenShape[ChildPresence] = (childId, shiftId) <> (ChildPresence.tupled, ChildPresence.unapply _)

  def childFK: ForeignKeyQuery[ChildTable, Child] = foreignKey("child_fk", childId, children)(_.id)
  def shiftFK: ForeignKeyQuery[ShiftTable, Shift] = foreignKey("shift_fk",
    shiftId, shifts)(_.id, onDelete=ForeignKeyAction.Cascade)

  private[models] def pk = primaryKey("child_to_shift_pk", (childId, shiftId))
}
