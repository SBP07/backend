package models.dao

import javax.inject.Inject

import _root_.models.Shift
import _root_.models.table.ShiftTable
import com.google.inject.ImplementedBy
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[SlickShiftDao])
trait ShiftDao extends GenericDao[Shift]

class SlickShiftDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends ShiftDao with HasDatabaseConfig[JdbcProfile] {

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

}
