package models.repositories.slick

import javax.inject.Inject

import models.{Child, Shift, ShiftType}
import slick.driver.PostgresDriver.api._
import helpers.Db.jodaDatetimeToSqldateMapper
import org.joda.time.LocalDate
import play.api.libs.concurrent.Execution.Implicits._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.{ForeignKeyQuery, ProvenShape}

import scala.concurrent.Future

class ShiftRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  val shifts = TableQuery[ShiftTable]

  def findAll: Future[Seq[Shift]] = db.run(shifts.sortBy(_.date).result)
  def findById(id: Long): Future[Option[Shift]] = db.run(shifts.filter(_.id === id).result.headOption)
  def insert(shift: Shift): Future[Unit] = db.run(shifts += shift).map(_ => ())
  def count: Future[Int] = db.run(shifts.length.result)

  def findByIdWithTypeAndNumberOfPresences(id: Long): Future[Option[(Shift, ShiftType, Int)]] = db.run {
    (for {
      shift <- shifts.filter(_.id === id)
      shiftType <- shift.shiftTypeJoin
    } yield {
      (shift, shiftType, shift.children.length)
    }).result.headOption
  }

  def findByDate(date: LocalDate): Future[Seq[Shift]] = db.run(shifts.filter(_.date === date).result)

  def findAllWithType: Future[Seq[(ShiftType, Shift)]] = db.run {
    (for {
      shift <- shifts.sortBy(_.date.desc)
      t <- shift.shiftTypeJoin.sortBy(_.id)
    } yield {
      (t, shift)
    }).result
  }

  def findAllWithTypeToday(today: LocalDate): Future[Seq[(ShiftType, Shift)]] = db.run {
    (for {
      shift <- shifts.filter(_.date === today).sortBy(_.date.desc)
      t <- shift.shiftTypeJoin.sortBy(_.id)
    } yield {
      (t, shift)
    }).result
  }

  def findAllWithTypeAndNumberOfPresences: Future[Seq[(ShiftType, Shift, Int)]] = db.run{
    (for {
      shift <- shifts.sortBy(_.date.desc)
      t <- shift.shiftTypeJoin.sortBy(_.id)
    } yield {
      (t, shift, shift.children.length)
    }).result
  }

  def findByIds(ids: List[Long]): Future[Seq[Shift]] = db.run(shifts.filter(_.id inSet ids).result)

  def findByDateAndType(date: LocalDate, shiftType: ShiftType): Future[Option[Shift]] = {
    shiftType.id.map { shiftType =>
      db.run(shifts.filter(_.shiftId === shiftType).filter(_.date === date).result.headOption)
    } getOrElse Future.successful(None)
  }
  def delete(shift: Shift): Future[Int] = db.run(shifts.filter(_.id === shift.id).delete)
}

private[models] class ShiftTable(tag: Tag) extends Table[Shift](tag, "shift") {

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private[models] def date = column[LocalDate]("date")
  private[models] def place = column[String]("place")
  private[models] def shiftId = column[Long]("shift_type")

  def * : ProvenShape[Shift] = (id.?, date, place, shiftId) <> (Shift.tupled, Shift.unapply)

  def shiftType: ForeignKeyQuery[ShiftTypeTable, ShiftType] = {
    foreignKey("fk_shift_type", shiftId, TableQuery[ShiftTypeTable])(_.id)
  }
  def shiftTypeJoin: Query[ShiftTypeTable, ShiftTypeTable#TableElementType, Seq] = {
    TableQuery[ShiftTypeTable].filter(_.id === shiftId)
  }

  def childrenJoin: Query[ChildTable, ChildTable#TableElementType, Seq] = {
    TableQuery[ChildrenToShiftsTable].filter(_.shiftId === id).flatMap(_.childFK)
  }

  def children: Query[ChildTable, Child, Seq] = {
    TableQuery[ChildrenToShiftsTable].filter(_.shiftId === id).flatMap(_.childFK)
  }
}