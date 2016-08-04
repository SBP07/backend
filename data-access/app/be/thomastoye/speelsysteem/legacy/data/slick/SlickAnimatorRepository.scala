package be.thomastoye.speelsysteem.legacy.data.slick

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.models.Animator
import org.joda.time.LocalDate
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.ProvenShape
import Helpers.jodaDatetimeToSqldateMapper
import be.thomastoye.speelsysteem.legacy.data.AnimatorRepository
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future

class AnimatorTable(tag: Tag) extends Table[Animator](tag, "animator") {

  def id = column[String]("id")
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def mobilePhone = column[String]("mobile_phone")
  def landline = column[String]("landline")
  def email = column[String]("email")

  def street = column[String]("street")
  def streetNumber = column[String]("street_number")
  def zipCode = column[Int]("zip_code")
  def city = column[String]("city")

  def bankAccount = column[String]("bank_account")
  def yearStartedVolunteering = column[Int]("year_started_volunteering")
  def isPartOfCore = column[Boolean]("is_core")

  def birthDate = column[LocalDate]("birthdate")

  def * : ProvenShape[Animator] = (id.?, firstName, lastName, mobilePhone.?, landline.?, email.?,
    street.?, streetNumber.?, zipCode.?, city.?, bankAccount.?, yearStartedVolunteering.?, isPartOfCore, birthDate.?) <>
    (Animator.tupled, Animator.unapply)
}

class SlickAnimatorRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) extends AnimatorRepository with StrictLogging {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  val animators = TableQuery[AnimatorTable]

  override def findById(id: String): Future[Option[Animator]] = db.run(animators.filter(_.id === id).result.headOption)
  override def findAll: Future[Seq[Animator]] = db.run(animators.result).map(_.sortBy(s => (s.lastName, s.firstName)))
  override def insert(animator: Animator): Future[Unit] = db.run(animators += animator).map(_ => ())
  override def count: Future[Int] = db.run(animators.length.result)
  override def update(animator: Animator): Future[Unit] = {
    animator.id match {
      case Some(id) =>
        logger.debug("Updating animator")
        db.run(animators.filter(_.id === id).update(animator)).map(_ => ())
      case _ =>
        logger.warn("No id, not updating")
        Future.successful(())
    }
  }
}
