package models.table

import models._
import play.api.db.slick.HasDatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.{ForeignKeyQuery, ProvenShape}

trait ShiftTypeTableSlice { this: HasDatabaseConfig[JdbcProfile] =>

  import driver.api._

  private[models] class ShiftTypeTable(tag: Tag) extends Table[ShiftType](tag, "shift_type") {
    def * : ProvenShape[ShiftType] = (id.?, mnemonic, description) <>
      (ShiftType.tupled, ShiftType.unapply)

    private[models] def id = column[Long]("id", O.PrimaryKey)

    private[models] def mnemonic = column[String]("mnemonic")

    private[models] def description = column[String]("description")
  }
}
