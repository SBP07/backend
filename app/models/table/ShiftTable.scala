package models.table

import java.time.LocalDate

import helpers.Db.localdateToSqldateMapper
import models._
import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}

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

  def childrenJoin: Query[ChildTable, ChildTable#TableElementType, Seq] = {
    TableQuery[ChildrenToShiftsTable].filter(_.shiftId === id).flatMap(_.childFK)
  }

  def children: Query[ChildTable, Child, Seq] = {
    TableQuery[ChildrenToShiftsTable].filter(_.shiftId === id).flatMap(_.childFK)
  }

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
}
