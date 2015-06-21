package models.table

import models._
import slick.driver.PostgresDriver.api._
import slick.lifted.ProvenShape

private[models] class ShiftTypeTable(tag: Tag) extends Table[ShiftType](tag, "shift_type") {
  def * : ProvenShape[ShiftType] = (id.?, mnemonic, description) <>
    (ShiftType.tupled, ShiftType.unapply)

  private[models] def id = column[Long]("id", O.PrimaryKey)

  private[models] def mnemonic = column[String]("mnemonic")

  private[models] def description = column[String]("description")
}
