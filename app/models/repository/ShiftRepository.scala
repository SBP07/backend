package models.repository

import java.time.LocalDate

import helpers.Db.localdateToSqldateMapper
import models._
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ForeignKeyQuery, ProvenShape}

object ShiftRepository {

  val shifts = TableQuery[ShiftRepository]

  def insert(shift: Shift)(implicit s: Session): Unit = shifts.insert(shift)

  def count(implicit s: Session): Int = shifts.length.run

  def findByIdWithTypeAndNumberOfPresences(id: Long)(implicit s: Session): Option[(Shift, ShiftType, Int)] = (for {
    shift <- shifts.filter(_.id === id)
    shiftType <- shift.shiftTypeJoin
  } yield {
      (shift, shiftType, shift.children.length)
    }).firstOption

  def findAllWithTypeAndNumberOfPresences(implicit s: Session): Seq[(ShiftType, Shift, Int)] = (for {
    shift <- shifts.sortBy(_.date.desc)
    t <- shift.shiftTypeJoin.sortBy(_.id)
  } yield {
      (t, shift, shift.children.length)
    }).run

  def findByDateAndType(date: LocalDate, shiftType: ShiftType)(implicit s: Session): Option[Shift] = {
    shiftType.id.flatMap { shiftType =>
      shifts.filter(_.shiftId === shiftType).filter(_.date === date).firstOption
    }
  }

  def delete(id: Long)(implicit s: Session): Int = shifts.filter(_.id === id).delete
}

private[models] class ShiftRepository(tag: Tag) extends Table[Shift](tag, "shift") {

  def * : ProvenShape[Shift] = (id.?, date, place, shiftId) <>((Shift.apply _).tupled, Shift.unapply)

  private[models] def date = column[LocalDate]("date", O.Nullable)

  private[models] def place = column[String]("place", O.Nullable)

  def shiftType: ForeignKeyQuery[ShiftTypeRepository, ShiftType] = {
    foreignKey("fk_shift_type", shiftId, TableQuery[ShiftTypeRepository])(_.id)
  }

  def shiftTypeJoin: Query[ShiftTypeRepository, ShiftTypeRepository#TableElementType, Seq] = {
    TableQuery[ShiftTypeRepository].filter(_.id === shiftId)
  }

  private[models] def shiftId = column[Long]("shift_type", O.NotNull)

  def childrenJoin: Query[ChildTable, ChildTable#TableElementType, Seq] = {
    TableQuery[ChildrenToShifts].filter(_.shiftId === id).flatMap(_.childFK)
  }

  def children: Query[ChildTable, Child, Seq] = {
    TableQuery[ChildrenToShifts].filter(_.shiftId === id).flatMap(_.childFK)
  }

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
}
