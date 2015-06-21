package models.dao

import javax.inject.Inject

import _root_.models.ShiftType
import _root_.models.table.ShiftTypeTable
import com.google.inject.ImplementedBy
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[SlickChildDao])
trait ShiftTypeDao extends GenericDao[ShiftType]


class SlickShiftTypeDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends ShiftTypeDao with HasDatabaseConfig[JdbcProfile] {

  import driver.api._

  val dbConfig = dbConfigProvider.get[JdbcProfile]


  val shiftTypes = TableQuery[ShiftTypeTable]

  override def count: Future[Int] = db.run(shiftTypes.length.result)

  override def findAll: Future[Seq[ShiftType]] = db.run(shiftTypes.result)

  override def findById(id: Long): Future[Option[ShiftType]] = {
    db.run(shiftTypes.filter(_.id === id).result.headOption)
  }

  override def insert(toInsert: ShiftType): Future[Int] = db.run(shiftTypes += toInsert)

  override def update(toUpdate: ShiftType): Future[Int] = {
    toUpdate.id match {
      case Some(id) => db.run(shiftTypes.filter(_.id === id).update(toUpdate))
      case _ => Future(0)
    }
  }

}
