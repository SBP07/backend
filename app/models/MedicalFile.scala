package models

import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.ProvenShape

case class ContactPerson(name: String, landline: String, mobilePhone: String, relationToChild: String)
case class Doctor(name: String, landline: String, mobilePhone: String, streetAndNumber: String, city: String)

case class MedicalFile(
  id: Option[Long] = None,

  firstName: String,
  lastName: String,

  street: String,
  city: String,

  bloodType: Option[String],
  isMale: Boolean,

  allergicToDust: Boolean,
  allergicToFacePaint: Boolean,
  allergicToBees: Boolean,
  otherAllergies: Option[String], // medication, animals, ...

  hasAsthma: Boolean,
  hasHayFever: Boolean,
  hasEpilepsy: Boolean,
  hasDiabetes: Boolean,
  otherConditions: Option[String],

  extraInformation: Option[String],
  tetanusShot: Option[LocalDate]
)

private[models] class MedicalFileRepository(tag: Tag) extends Table[MedicalFile](tag, "medical_file") {
  import _root_.helpers.Db.jodaDatetimeToSqldateMapper

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  private[models] def firstName = column[String]("first_name")
  private[models] def lastName = column[String]("last_name")

  private[models] def street = column[String]("street")
  private[models] def city = column[String]("city")

  private[models] def bloodType = column[String]("blood_type", O.Nullable)
  private[models] def isMale = column[Boolean]("is_male")

  private[models] def allergicToDust = column[Boolean]("allergic_to_dust")
  private[models] def allergicToFacePaint = column[Boolean]("allergic_to_face_paint")
  private[models] def allergicToBees = column[Boolean]("allergic_to_bees")
  private[models] def otherAllergies = column[String]("other_allergies", O.Nullable)

  private[models] def hasAsthma = column[Boolean]("has_asthma")
  private[models] def hasHayFever = column[Boolean]("has_hay_fever")
  private[models] def hasEpilepsy = column[Boolean]("has_epilepsy")
  private[models] def hasDiabetes = column[Boolean]("has_diabetes")
  private[models] def otherConditions = column[String]("other_conditions", O.Nullable)

  private[models] def extraInformation = column[String]("extra_information", O.Nullable)
  private[models] def tetanusShot = column[LocalDate]("birthdate", O.Nullable)

  def * : ProvenShape[MedicalFile] = (id.?, firstName, lastName, street, city, bloodType.?, isMale,
    allergicToDust, allergicToFacePaint, allergicToBees, otherAllergies.?, hasAsthma, hasHayFever, hasEpilepsy,
    hasDiabetes, otherConditions.?, extraInformation.?, tetanusShot.?) <>
      (MedicalFile.tupled, MedicalFile.unapply)
}

object MedicalFileRepository {
  private val table = TableQuery[MedicalFileRepository]

  def insert(file: MedicalFile)(implicit s: Session): Unit = table += file
}
