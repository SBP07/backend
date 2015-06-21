package models.dao

import javax.inject.Inject

import models.Animator
import com.google.inject.ImplementedBy
import models.table.AnimatorTable
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfig}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

@ImplementedBy(classOf[SlickAnimatorDao])
trait AnimatorDao extends GenericDao[Animator]

class SlickAnimatorDao @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends AnimatorDao with HasDatabaseConfig[JdbcProfile] {
  import driver.api._

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  val animators = TableQuery[AnimatorTable]

  override def findById(id: Long): Future[Option[Animator]] = {
    db.run(animators.filter(_.id === id).result.headOption)
  }

  override def findAll: Future[Seq[Animator]] = db.run(animators.result)

  override def insert(animator: Animator): Future[Int] = db.run(animators += animator)

  override def count: Future[Int] = db.run(animators.length.result)

  override def update(animator: Animator): Future[Int] = {
    animator.id match {
      case Some(id) => db.run(animators.filter(_.id === id).update(animator))
      case _ => Future(0)
    }
  }
}
