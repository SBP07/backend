package be.thomastoye.speelsysteem.legacy.data.slick

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.models.{LegacyChild, LegacyShift, ShiftType}
import org.joda.time.LocalDate
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.{ForeignKeyQuery, ProvenShape}
import Helpers.jodaDatetimeToSqldateMapper
import be.thomastoye.speelsysteem.legacy.data.ShiftRepository

import scala.concurrent.Future

class SlickShiftRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) extends ShiftRepository {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  val shifts = TableQuery[ShiftTable]

  override def findAll: Future[Seq[LegacyShift]] = db.run(shifts.sortBy(_.date).result)
  override def findById(id: Long): Future[Option[LegacyShift]] = db.run(shifts.filter(_.id === id).result.headOption)
  override def insert(shift: LegacyShift): Future[Unit] = db.run(shifts += shift).map(_ => ())
  override def count: Future[Int] = db.run(shifts.length.result)

  override def findByIdWithTypeAndNumberOfPresences(id: Long): Future[Option[(LegacyShift, ShiftType, Int)]] = db.run {
    (for {
      shift <- shifts.filter(_.id === id)
      shiftType <- shift.shiftTypeJoin
    } yield {
      (shift, shiftType, shift.children.length)
    }).result.headOption
  }

  override def findByDate(date: LocalDate): Future[Seq[LegacyShift]] = db.run(shifts.filter(_.date === date).result)

  override def findAllWithType: Future[Seq[(ShiftType, LegacyShift)]] = db.run {
    (for {
      shift <- shifts.sortBy(_.date.desc)
      t <- shift.shiftTypeJoin.sortBy(_.id)
    } yield {
      (t, shift)
    }).result
  }

  override def findAllWithTypeToday(today: LocalDate): Future[Seq[(ShiftType, LegacyShift)]] = db.run {
    (for {
      shift <- shifts.filter(_.date === today).sortBy(_.date.desc)
      t <- shift.shiftTypeJoin.sortBy(_.id)
    } yield {
      (t, shift)
    }).result
  }

  override def findAllWithTypeAndNumberOfPresences: Future[Seq[(ShiftType, LegacyShift, Int)]] = db.run{
    (for {
      shift <- shifts.sortBy(_.date.desc)
      t <- shift.shiftTypeJoin.sortBy(_.id)
    } yield {
      (t, shift, shift.children.length)
    }).result
  }

  override def findByIds(ids: Seq[Long]): Future[Seq[LegacyShift]] = db.run(shifts.filter(_.id inSet ids).result)

  override def findByDateAndType(date: LocalDate, shiftType: ShiftType): Future[Option[LegacyShift]] = {
    shiftType.id.map { shiftType =>
      db.run(shifts.filter(_.shiftId === shiftType).filter(_.date === date).result.headOption)
    } getOrElse Future.successful(None)
  }
  override def delete(shift: LegacyShift): Future[Int] = db.run(shifts.filter(_.id === shift.id).delete)
}

class ShiftTable(tag: Tag) extends Table[LegacyShift](tag, "shift") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def date = column[LocalDate]("date")
  def place = column[String]("place")
  def shiftId = column[Long]("shift_type")

  def * : ProvenShape[LegacyShift] = (id.?, date, place, shiftId) <> (LegacyShift.tupled, LegacyShift.unapply)

  def shiftType: ForeignKeyQuery[ShiftTypeTable, ShiftType] = {
    foreignKey("fk_shift_type", shiftId, TableQuery[ShiftTypeTable])(_.id)
  }
  def shiftTypeJoin: Query[ShiftTypeTable, ShiftTypeTable#TableElementType, Seq] = {
    TableQuery[ShiftTypeTable].filter(_.id === shiftId)
  }

  def childrenJoin: Query[ChildTable, ChildTable#TableElementType, Seq] = {
    TableQuery[ChildrenToShiftsTable].filter(_.shiftId === id).flatMap(_.childFK)
  }

  def children: Query[ChildTable, LegacyChild, Seq] = {
    TableQuery[ChildrenToShiftsTable].filter(_.shiftId === id).flatMap(_.childFK)
  }
}