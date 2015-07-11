package models.table

import models._
import play.api.db.slick.HasDatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.{ForeignKeyQuery, ProvenShape}

trait ChildrenToShiftsTableSlice
  extends ChildTableSlice
  with ShiftTableSlice
{
  this: HasDatabaseConfig[JdbcProfile] =>

  import driver.api._

  private[models] class ChildrenToShiftsTable(tag: Tag) extends Table[ChildPresence](tag, "child_to_shift") {
    private[models] val children = TableQuery[ChildTable]
    private[models] val shifts = TableQuery[ShiftTable]

    def * : ProvenShape[ChildPresence] = (childId, shiftId) <>(ChildPresence.tupled, ChildPresence.unapply)

//    def childFK: ForeignKeyQuery[ChildTable, Child] = foreignKey("child_fk", childId, children)(_.id)

    private[models] def childId = column[Long]("child_id")

//    def shiftFK: ForeignKeyQuery[ShiftTable, Shift] = foreignKey("shift_fk",
//      shiftId, shifts)(_.id, onDelete = ForeignKeyAction.Cascade)

    private[models] def pk = primaryKey("child_to_shift_pk", (childId, shiftId))

    private[models] def shiftId = column[Long]("shift_id")
  }

}
