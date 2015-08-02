package models.table

import models._
import play.api.db.slick.HasDatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

trait VolunteerToShiftsTableSlice
  extends VolunteerTableSlice
  with ShiftTableSlice {
  this: HasDatabaseConfig[JdbcProfile] =>

  import driver.api._

  private[models] class VolunteerToShiftsTable(tag: Tag) extends Table[VolunteerPresence](tag, "volunteer_to_shift") {
    private[models] val volunteers = TableQuery[VolunteerTable]
    private[models] val shifts = TableQuery[ShiftTable]

    def * : ProvenShape[VolunteerPresence] = (volunteerId, shiftId) <>(VolunteerPresence.tupled, VolunteerPresence.unapply)

    private[models] def volunteerId = column[Long]("volunteer_id")

    private[models] def pk = primaryKey("volunteer_to_shift_pk", (volunteerId, shiftId))

    private[models] def shiftId = column[Long]("shift_id")
  }

}
