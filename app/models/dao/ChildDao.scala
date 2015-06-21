package models.dao

import javax.inject.Inject

import models.Child
import com.google.inject.ImplementedBy
import models.table.ChildTable
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.Future

@ImplementedBy(classOf[SlickChildDao])
trait ChildDao extends GenericDao[Child]


class SlickChildDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends ChildDao with HasDatabaseConfig[JdbcProfile] {
  import driver.api._

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val children = TableQuery[ChildTable]

  override def count: Future[Int] = db.run(children.length.result)
  override def findAll: Future[Seq[Child]] = db.run(children.result)
  override def findById(id: Long): Future[Option[Child]] = {
    db.run(children.filter(_.id === id).result.headOption)
  }
  override def insert(toInsert: Child): Future[Int] = db.run(children += toInsert)
  override def update(toUpdate: Child): Future[Int] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    toUpdate.id match {
      case Some(id) => db.run(children.filter(_.id === id).update(toUpdate))
      case _ => Future(0)
    }
  }

}
