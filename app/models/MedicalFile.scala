package models

import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

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

class MedicalFiles(tag: Tag) extends Table[MedicalFile](tag, "MEDICAL_FILES") {
  import helpers.Db.jodaDatetimeToSqldateMapper

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)

  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")

  def street = column[String]("street")
  def city = column[String]("city")

  def bloodType = column[String]("blood_type", O.Nullable)
  def isMale = column[Boolean]("is_male")

  def allergicToDust = column[Boolean]("allergic_to_dust")
  def allergicToFacePaint = column[Boolean]("allergic_to_face_paint")
  def allergicToBees = column[Boolean]("allergic_to_bees")
  def otherAllergies = column[String]("other_allergies", O.Nullable)

  def hasAsthma = column[Boolean]("has_asthma")
  def hasHayFever = column[Boolean]("has_hay_fever")
  def hasEpilepsy = column[Boolean]("has_epilepsy")
  def hasDiabetes = column[Boolean]("has_diabetes")
  def otherConditions = column[String]("other_conditions", O.Nullable)

  def extraInformation = column[String]("extra_information", O.Nullable)
  def tetanusShot = column[LocalDate]("birthdate", O.Nullable)

  def * = (id.?, firstName, lastName, street, city, bloodType.?, isMale,
    allergicToDust, allergicToFacePaint, allergicToBees, otherAllergies.?, hasAsthma, hasHayFever, hasEpilepsy,
    hasDiabetes, otherConditions.?, extraInformation.?, tetanusShot.?) <>
      (MedicalFile.tupled, MedicalFile.unapply _)
}

object MedicalFiles {
  val table = TableQuery[MedicalFiles]

  def insert(file: MedicalFile)(implicit s: Session) = table += file
}