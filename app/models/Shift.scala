package models

import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ForeignKeyQuery, ProvenShape}

case class Shift(id: Option[Long] = None, date: LocalDate, place: String, shiftId: Long)
case class ShiftType(id: Option[Long], mnemonic: String, description: String)


private[models] class Shifts(tag: Tag) extends Table[Shift](tag, "shift") {
  import helpers.Db.jodaDatetimeToSqldateMapper

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private[models] def date = column[LocalDate]("date", O.Nullable)
  private[models] def place = column[String]("place", O.Nullable)
  private[models] def shiftId = column[Long]("shift_type", O.NotNull)

  def * : ProvenShape[Shift] = (id.?, date, place, shiftId) <> (Shift.tupled, Shift.unapply)

  def shiftType: ForeignKeyQuery[ShiftTypes, ShiftType] = {
    foreignKey("fk_shift_type", shiftId, TableQuery[ShiftTypes])(_.id)
  }
  def shiftTypeJoin: Query[ShiftTypes, ShiftTypes#TableElementType, Seq] = {
    TableQuery[ShiftTypes].filter(_.id === shiftId)
  }

  def childrenJoin: Query[Children, Children#TableElementType, Seq] = {
    TableQuery[ChildrenToShifts].filter(_.shiftId === id).flatMap(_.childFK)
  }

  def children: Query[Children, Child, Seq] = {
    TableQuery[ChildrenToShifts].filter(_.shiftId === id).flatMap(_.childFK)
  }
}

private[models] class ShiftTypes(tag: Tag) extends Table[ShiftType](tag, "shift_type") {
  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private[models] def mnemonic = column[String]("mnemonic", O.NotNull)
  private[models] def description = column[String]("description", O.NotNull)

  def * : ProvenShape[ShiftType] = (id.?, mnemonic, description) <>
    (ShiftType.tupled, ShiftType.unapply)
}

object Shifts {
  import helpers.Db.jodaDatetimeToSqldateMapper

  val shifts = TableQuery[Shifts]

  def findAll(implicit s: Session): List[Shift] = shifts.sortBy(_.date).list
  def findById(id: Long)(implicit s: Session): Option[Shift] = shifts.filter(_.id === id).firstOption
  def insert(shift: Shift)(implicit s: Session): Unit = shifts.insert(shift)
  def count(implicit s: Session): Int = shifts.length.run

  def findByDate(date: LocalDate)(implicit s: Session): Seq[Shift] = shifts.filter(_.date === date).run

  def findAllWithType(implicit s: Session): Seq[(ShiftType, Shift)] = (for {
    shift <- shifts.sortBy(_.date)
    t <- shift.shiftTypeJoin.sortBy(_.id)
  } yield {
    (t, shift)
  }).run

  def findAllWithTypeAndNumberOfPresences(implicit s: Session): Seq[(ShiftType, Shift, Int)] = (for {
    shift <- shifts.sortBy(_.date)
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
  def delete(shift: Shift)(implicit s: Session) = shifts.filter(_.id === shift.id).delete
}

object ShiftTypes {
  val types = TableQuery[ShiftTypes]

  def findAll(implicit s: Session): List[ShiftType] = types.list
  def findById(id: Long)(implicit s: Session): Option[ShiftType] = types.filter(_.id === id).firstOption
  def findByMnemonic(mnemonic: String)(implicit s: Session): Option[ShiftType] = {
    types.filter(_.mnemonic === mnemonic).firstOption
  }
  def insert(shiftType: ShiftType)(implicit s: Session): Unit = types.insert(shiftType)
  def count(implicit s: Session): Int = types.length.run
}
