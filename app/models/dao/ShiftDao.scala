package models.dao

import java.time.LocalDate
import javax.inject.Inject

import models.{ChildPresence, ShiftType, Shift, Child}
import models.table.{ChildTable, ShiftTypeTable, ShiftTable, ChildrenToShiftsTable}
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
    // SQL of what I'm trying to do here:
    //
    //    SELECT * FROM shift
    //      LEFT JOIN child_to_shift ON shift.id = shift_id
    //      LEFT JOIN child ON child.id = child_id
    //    ORDER BY shift.id
    // Then Seq[(Shift, Child)] => Seq[(Shift, Seq[Shift]) (children grouped by shift)


    val typeTable = TableQuery[ShiftTypeTable]
    val childTable = TableQuery[ChildTable]
    val childJoinTable = TableQuery[ChildrenToShiftsTable]

    // this will return all the shifts, but not with the attending children
    val allShiftsOnDay = for {
      shift <- shifts.filter(_.date === date)
      shiftType <- typeTable if shift.shiftTypeId === shiftType.id
    } yield (shift, shiftType)

    // this won't return the shifts on which no child is attending
    // we'll combine it with all the shifts on this day afterwards
    val shiftsWithChildrenOnDay = for {
      shift <- shifts filter (_.date === date)
      shiftType <- typeTable if shift.shiftTypeId === shiftType.id
      join <- childJoinTable if join.shiftId === shift.id
      child <- childTable if child.id === join.childId
    } yield (shift, shiftType, child)

    for {
      allShifts <- db.run(allShiftsOnDay.result)
      withChildren <- db.run(shiftsWithChildrenOnDay.result)
    } yield {
      val allShiftsTransformed: Seq[(Shift, ShiftType, Seq[Child])] = allShifts.map(a => (a._1, a._2, Nil))
      val withChildrenTransformed: Seq[(Shift, ShiftType, Seq[Child])] =
        withChildren.foldLeft(Nil: Seq[(Shift, ShiftType, Seq[Child])]) { (total, n) =>
          if (total.exists(_._1 == n._1)) {
            val tuple: (Shift, ShiftType, Seq[Child]) = (n._1, n._2, total.filter(_._1 == n._1).head._3 :+ n._3)
            total.filterNot(_._1 == n._1) :+ tuple
          } else {
            val toAdd: (Shift, ShiftType, Seq[Child]) = (n._1, n._2, Seq(n._3))
            total :+ toAdd
          }
      }

      allShiftsTransformed.map { tuple =>
        val children: Seq[Child] = withChildrenTransformed.find(_._1 == tuple._1).map(_._3).getOrElse(Nil)
        (tuple._2, tuple._1, children)
      }
    }
  }

}
