package models.dao

import java.time.LocalDate
import javax.inject.Inject

import _root_.models.{ShiftType, Shift, Child}
import models.table.{ChildTableSlice, ShiftTypeTableSlice, ShiftTableSlice, ChildrenToShiftsTableSlice}
import com.google.inject.ImplementedBy
import helpers.Db.localdateToSqldateMapper
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[SlickShiftDao])
trait ShiftDao extends GenericDao[Shift] {
  def findByDate(date: LocalDate): Future[Seq[Shift]]
  def findByDateWithTypeAndChildren(date: LocalDate): Future[Seq[(ShiftType, Shift, Seq[Child])]]
}

class SlickShiftDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends ShiftDao
  with HasDatabaseConfig[JdbcProfile]
  with ChildrenToShiftsTableSlice
  with ShiftTableSlice
  with ShiftTypeTableSlice
  with ChildTableSlice
{

  import driver.api._

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val shifts = TableQuery[ShiftTable]

  override def count: Future[Int] = db.run(shifts.length.result)

  override def findAll: Future[Seq[Shift]] = db.run(shifts.result)

  override def findById(id: Long): Future[Option[Shift]] = {
    db.run(shifts.filter(_.id === id).result.headOption)
  }

  override def insert(toInsert: Shift): Future[Int] = db.run(shifts += toInsert)

  override def update(toUpdate: Shift): Future[Int] = {
    toUpdate.id match {
      case Some(id) => db.run(shifts.filter(_.id === id).update(toUpdate))
      case _ => Future(0)
    }
  }

  override def findByDate(date: LocalDate): Future[Seq[Shift]] = {
    db.run(shifts.filter(_.date === date).result)
  }

  override def findByDateWithTypeAndChildren(date: LocalDate): Future[Seq[(ShiftType, Shift, Seq[Child])]] = {
    val typeTable = TableQuery[ShiftTypeTable]
    val childTable = TableQuery[ChildTable]
    val childJoinTable = TableQuery[ChildrenToShiftsTable]

    val query = for {
      shift <- shifts.filter(_.date === date)
      shiftType <- typeTable if shiftType.id === shift.shiftTypeId
      childJoinTable <- childJoinTable if childJoinTable.shiftId === shift.id
      child <- childTable if child.id === childJoinTable.childId
    } yield (shiftType, shift, child)

    db.run(query.result).map { list =>
      list.groupBy(_._2.id).map{ tuple =>
        val head = tuple._2.head
        (head._1, head._2, tuple._2.map(_._3))
      }.toSeq
    }
  }

}
