package models.repository

import models._
import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ForeignKeyQuery, ProvenShape}

object ShiftRepository {
  import helpers.Db.jodaDatetimeToSqldateMapper

  val shifts = TableQuery[ShiftRepository]

  def findAll(implicit s: Session): List[Shift] = shifts.sortBy(_.date).list
  def findById(id: Long)(implicit s: Session): Option[Shift] = shifts.filter(_.id === id).firstOption
  def insert(shift: Shift)(implicit s: Session): Unit = shifts.insert(shift)
  def count(implicit s: Session): Int = shifts.length.run

  def findByIdWithTypeAndNumberOfPresences(id: Long)(implicit s: Session): Option[(Shift, ShiftType, Int)] = (for {
    shift <- shifts.filter(_.id === id)
    shiftType <- shift.shiftTypeJoin
  } yield {
      (shift, shiftType, shift.children.length)
  }).firstOption

  def findByDate(date: LocalDate)(implicit s: Session): Seq[Shift] = shifts.filter(_.date === date).run

  def findAllWithType(implicit s: Session): Seq[(ShiftType, Shift)] = (for {
    shift <- shifts.sortBy(_.date.desc)
    t <- shift.shiftTypeJoin.sortBy(_.id)
  } yield {
    (t, shift)
  }).run

  def findAllWithTypeToday(today: LocalDate)(implicit s: Session): Seq[(ShiftType, Shift)] = (for {
    shift <- shifts.filter(_.date === today).sortBy(_.date.desc)
    t <- shift.shiftTypeJoin.sortBy(_.id)
  } yield {
      (t, shift)
  }).run

  def findAllWithTypeAndNumberOfPresences(implicit s: Session): Seq[(ShiftType, Shift, Int)] = (for {
    shift <- shifts.sortBy(_.date.desc)
    t <- shift.shiftTypeJoin.sortBy(_.id)
  } yield {
    (t, shift, shift.children.length)
  }).run

  def findByIds(ids: List[Long])(implicit s: Session): Seq[Shift] = shifts.filter(_.id inSet ids).run
  def findByDateAndType(date: LocalDate, shiftType: ShiftType)(implicit s: Session): Option[Shift] = {
    shiftType.id.flatMap { shiftType =>
      shifts.filter(_.shiftId === shiftType).filter(_.date === date).firstOption
    }
  }
  def delete(shift: Shift)(implicit s: Session): Int = shifts.filter(_.id === shift.id).delete
  def delete(id: Long)(implicit s: Session): Int = shifts.filter(_.id === id).delete
}

private[models] class ShiftRepository(tag: Tag) extends Table[Shift](tag, "shift") {
  import helpers.Db.jodaDatetimeToSqldateMapper

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private[models] def date = column[LocalDate]("date", O.Nullable)
  private[models] def place = column[String]("place", O.Nullable)
  private[models] def shiftId = column[Long]("shift_type", O.NotNull)

  def * : ProvenShape[Shift] = (id.?, date, place, shiftId) <> ((Shift.apply _).tupled, Shift.unapply)

  def shiftType: ForeignKeyQuery[ShiftTypeRepository, ShiftType] = {
    foreignKey("fk_shift_type", shiftId, TableQuery[ShiftTypeRepository])(_.id)
  }
  def shiftTypeJoin: Query[ShiftTypeRepository, ShiftTypeRepository#TableElementType, Seq] = {
    TableQuery[ShiftTypeRepository].filter(_.id === shiftId)
  }

  def childrenJoin: Query[ChildRepository, ChildRepository#TableElementType, Seq] = {
    TableQuery[ChildrenToShifts].filter(_.shiftId === id).flatMap(_.childFK)
  }

  def children: Query[ChildRepository, Child, Seq] = {
    TableQuery[ChildrenToShifts].filter(_.shiftId === id).flatMap(_.childFK)
  }
}