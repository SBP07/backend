package models.table

import java.time.LocalDate

import helpers.Db.localdateToSqldateMapper
import models._
import slick.lifted.{ForeignKeyQuery, ProvenShape}
import slick.driver.PostgresDriver.api._


private[models] class ShiftTable(tag: Tag) extends Table[Shift](tag, "shift") {

  def * : ProvenShape[Shift] = (id.?, date, place, shiftTypeId) <>((Shift.apply _).tupled, Shift.unapply)

  private[models] def date = column[LocalDate]("date")
  private[models] def place = column[String]("place")

  def shiftType: ForeignKeyQuery[ShiftTypeTable, ShiftType] = {
    foreignKey("fk_shift_type", shiftTypeId, TableQuery[ShiftTypeTable])(_.id)
  }

  def shiftTypeJoin: Query[ShiftTypeTable, ShiftTypeTable#TableElementType, Seq] = {
    TableQuery[ShiftTypeTable].filter(_.id === shiftTypeId)
  }

  private[models] def shiftTypeId = column[Long]("shift_type")
  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
}
