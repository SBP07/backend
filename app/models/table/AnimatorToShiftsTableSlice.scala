package models.table

import models._
import play.api.db.slick.HasDatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

trait AnimatorToShiftsTableSlice
  extends AnimatorTableSlice
  with ShiftTableSlice {
  this: HasDatabaseConfig[JdbcProfile] =>

  import driver.api._

  private[models] class AnimatorToShiftsTable(tag: Tag) extends Table[AnimatorPresence](tag, "animator_to_shift") {
    private[models] val animators = TableQuery[AnimatorTable]
    private[models] val shifts = TableQuery[ShiftTable]

    def * : ProvenShape[AnimatorPresence] = (animatorId, shiftId) <>(AnimatorPresence.tupled, AnimatorPresence.unapply)

    private[models] def animatorId = column[Long]("volunteer_id")

    private[models] def pk = primaryKey("animator_to_shift_pk", (animatorId, shiftId))

    private[models] def shiftId = column[Long]("shift_id")
  }

}
