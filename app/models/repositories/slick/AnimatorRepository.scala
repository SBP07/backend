package models.repositories.slick

import javax.inject.Inject

import helpers.Db.jodaDatetimeToSqldateMapper
import models.Animator
import slick.driver.PostgresDriver.api._
import scala.concurrent.Future
import org.joda.time.LocalDate
import play.api.libs.concurrent.Execution.Implicits._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

private[models] class AnimatorTable(tag: Tag) extends Table[Animator](tag, "animator") {

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private[models] def firstName = column[String]("first_name")
  private[models] def lastName = column[String]("last_name")
  private[models] def mobilePhone = column[String]("mobile_phone")
  private[models] def landline = column[String]("landline")
  private[models] def email = column[String]("email")

  private[models] def street = column[String]("street")
  private[models] def city = column[String]("city")

  private[models] def bankAccount = column[String]("bank_account")
  private[models] def yearStartedVolunteering = column[Int]("year_started_volunteering")
  private[models] def isPartOfCore = column[Boolean]("is_core")

  private[models] def birthDate = column[LocalDate]("birthdate")

  def * : ProvenShape[Animator] = (id.?, firstName, lastName, mobilePhone.?, landline.?, email.?,
    street.?, city.?, bankAccount.?, yearStartedVolunteering.?, isPartOfCore, birthDate.?) <>
    (Animator.tupled, Animator.unapply)
}

class AnimatorRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  val animators = TableQuery[AnimatorTable]

  def findById(id: Long): Future[Option[Animator]] = db.run(animators.filter(_.id === id).result.headOption)
  def findAll: Future[Seq[Animator]] = db.run(animators.result)
  def insert(animator: Animator): Future[Unit] = db.run(animators += animator).map(_ => ())
  def count: Future[Int] = db.run(animators.length.result)
  def update(animator: Animator): Future[Unit] = {
    animator.id match {
      case Some(id) => db.run(animators.filter(_.id === id).update(animator)).map(_ => ())
      case _ => Future.successful(())
    }
  }
}