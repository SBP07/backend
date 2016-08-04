package be.thomastoye.speelsysteem.legacy.data.slick

import be.thomastoye.speelsysteem.legacy.models.{Child, ChildPresence, Shift}
import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

class ChildrenToShiftsTable(tag: Tag) extends Table[ChildPresence](tag, "child_to_shift") {
  val children = TableQuery[ChildTable]
  val shifts = TableQuery[ShiftTable]

  def childId = column[Long]("child_id")
  def shiftId = column[Long]("shift_id")

  def * : ProvenShape[ChildPresence] = (childId, shiftId) <> (ChildPresence.tupled, ChildPresence.unapply _)

  def childFK: ForeignKeyQuery[ChildTable, Child] = foreignKey("child_fk", childId, children)(_.id)
  def shiftFK: ForeignKeyQuery[ShiftTable, Shift] = foreignKey("shift_fk",
    shiftId, shifts)(_.id, onDelete=ForeignKeyAction.Cascade)

  def pk = primaryKey("child_to_shift_pk", (childId, shiftId))
}