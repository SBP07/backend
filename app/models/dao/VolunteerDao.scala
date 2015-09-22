package models.dao

import javax.inject.Inject

import _root_.models.Volunteer
import models.table.VolunteerTable
import com.google.inject.ImplementedBy
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@ImplementedBy(classOf[SlickVolunteerDao])
trait VolunteerDao extends GenericDao[Volunteer]

class SlickVolunteerDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends VolunteerDao
  with HasDatabaseConfig[JdbcProfile]
{

  import driver.api._

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val volunteers = TableQuery[VolunteerTable]

  override def findById(id: Long): Future[Option[Volunteer]] = {
    db.run(volunteers.filter(_.id === id).result.headOption)
  }

  override def findAll: Future[Seq[Volunteer]] = db.run(volunteers.result)

  override def insert(volunteer: Volunteer): Future[Int] = db.run(volunteers += volunteer)

  override def count: Future[Int] = db.run(volunteers.length.result)

  override def update(volunteer: Volunteer): Future[Int] = {
    volunteer.id match {
      case Some(id) => db.run(volunteers.filter(_.id === id).update(volunteer))
      case _ => Future(0)
    }
  }
}
