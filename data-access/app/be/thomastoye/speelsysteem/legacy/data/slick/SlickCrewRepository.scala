package be.thomastoye.speelsysteem.legacy.data.slick

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.models.LegacyCrew
import org.joda.time.LocalDate
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.ProvenShape
import Helpers.jodaDatetimeToSqldateMapper
import be.thomastoye.speelsysteem.legacy.data.CrewRepository
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future

class LegacyCrewTable(tag: Tag) extends Table[LegacyCrew](tag, "animator") {

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

  def * : ProvenShape[LegacyCrew] = (id.?, firstName, lastName, mobilePhone.?, landline.?, email.?,
    street.?, streetNumber.?, zipCode.?, city.?, bankAccount.?, yearStartedVolunteering.?, isPartOfCore, birthDate.?) <>
    (LegacyCrew.tupled, LegacyCrew.unapply)
}

class SlickCrewRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) extends CrewRepository with StrictLogging {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  val crew = TableQuery[LegacyCrewTable]

  override def findById(id: String): Future[Option[LegacyCrew]] = db.run(crew.filter(_.id === id).result.headOption)
  override def findAll: Future[Seq[LegacyCrew]] = db.run(crew.result).map(_.sortBy(s => (s.lastName, s.firstName)))
  override def insert(crewMember: LegacyCrew): Future[Unit] = db.run(crew += crewMember).map(_ => ())
  override def count: Future[Int] = db.run(crew.length.result)
  override def update(crewMember: LegacyCrew): Future[Unit] = {
    crewMember.id match {
      case Some(id) =>
        logger.debug("Updating crew member")
        db.run(crew.filter(_.id === id).update(crewMember)).map(_ => ())
      case _ =>
        logger.warn("No id, not updating")
        Future.successful(())
    }
  }
}
