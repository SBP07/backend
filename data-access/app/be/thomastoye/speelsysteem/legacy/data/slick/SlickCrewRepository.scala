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
import be.thomastoye.speelsysteem.data.CrewRepository
import be.thomastoye.speelsysteem.models._
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

  def birthDate = column[LocalDate]("birthdate")

  def * : ProvenShape[LegacyCrew] = (id.?, firstName, lastName, mobilePhone.?, landline.?, email.?,
    street.?, streetNumber.?, zipCode.?, city.?, bankAccount.?, yearStartedVolunteering.?, birthDate.?) <>
    (LegacyCrew.tupled, LegacyCrew.unapply)
}

class SlickCrewRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) extends CrewRepository with StrictLogging {
  import SlickCrewRepository.{crew2legacyModel, legacyModel2crewAndId}
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  val crew = TableQuery[LegacyCrewTable]

  override def findById(id: String): Future[Option[(Crew.Id, Crew)]] = {
    db
      .run(crew.filter(_.id === id).result.headOption)
      .map(x => x.map(legacyModel2crewAndId).map(y => (y._1.get, y._2)))
  }

  override def findAll: Future[Seq[(Crew.Id, Crew)]] = {
    db.run(crew.result).map(_.map(legacyModel2crewAndId).map(x => (x._1.get, x._2))).map(x => x.sortBy(s => (s._2.lastName, s._2.firstName)))
  }

  override def insert(crewMember: Crew): Future[Unit] = db.run(crew += crew2legacyModel(crewMember)).map(_ => ())
  override def count: Future[Int] = db.run(crew.length.result)
  override def update(id: Crew.Id, crewMember: Crew): Future[Unit] = {
    db.run(crew.filter(_.id === id).update(crew2legacyModel(crewMember, Some(id)))).map(_ => ())
  }
}

object SlickCrewRepository {
  def crew2legacyModel(crew: Crew, id: Option[Crew.Id] = None): LegacyCrew = {
    LegacyCrew(
      id,
      crew.firstName,
      crew.lastName,
      crew.contact.phone.find(_.kind.contains("mobile")).map(_.phoneNumber),
      crew.contact.phone.find(_.kind.contains("landline")).map(_.phoneNumber),
      crew.contact.email.headOption,
      crew.address.street,
      crew.address.number,
      crew.address.zipCode,
      crew.address.city,
      crew.bankAccount,
      crew.yearStarted,
      crew.birthDate.map(day => new LocalDate(day.year, day.month, day.day))
    )
  }

  def legacyModel2crewAndId(legacyModel: LegacyCrew): (Option[Crew.Id], Crew) = {
    val address = Address(legacyModel.street, legacyModel.streetNumber, legacyModel.zipCode, legacyModel.city)

    val contact = ContactInfo(
      legacyModel.mobilePhone.map(PhoneContact(Some("mobile"), None, _)).toSeq ++ legacyModel.landline.map(PhoneContact(Some("landline"), None, _)).toSeq,
      legacyModel.email.toSeq
    )
    val birthDate = legacyModel.birthDate.map(day => Day(day.getDayOfMonth, day.getMonthOfYear, day.getYear))

    val crew = Crew(
      legacyModel.firstName,
      legacyModel.lastName,
      address,
      legacyModel.bankAccount,
      contact,
      legacyModel.yearStartedVolunteering,
      birthDate
    )

    (legacyModel.id, crew)
  }
}
