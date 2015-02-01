package models

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate

case class Child(
  id: Option[Long] = None,
  firstName: String,
  lastName: String,
  mobilePhone: Option[String],
  landline: Option[String],
  
  street: Option[String],
  city: Option[String],
  
  birthDate: Option[LocalDate],
  
  medicalRecordChecked: Option[LocalDate] = None // None means not ok
)

class Children(tag: Tag) extends Table[Child](tag, "CHILDREN") {
  import helpers.Db.jodaDatetimeToSqldateMapper

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("FIRST_NAME")
  def lastName = column[String]("LAST_NAME")
  def mobilePhone = column[String]("MOBILE_PHONE", O.Nullable)
  def landline = column[String]("LANDLINE", O.Nullable)

  def street = column[String]("STREET", O.Nullable)
  def city = column[String]("CITY", O.Nullable)
  
  def birthDate = column[LocalDate]("BIRTHDATE", O.Nullable)

  def medicalRecordChecked = column[LocalDate]("MED_REC_CHECKED", O.Nullable)
  
  def * = (id.?, firstName, lastName, mobilePhone.?, landline.?, street.?, city.?, birthDate.?, medicalRecordChecked.?) <> (Child.tupled, Child.unapply _)
}

object Children {
  val children = TableQuery[Children]
  
  def findById(id: Long)(implicit s: Session): Option[Child] = children.filter(_.id === id).firstOption
  def findAll(implicit s: Session) = children.list
  def insert(child: Child)(implicit s: Session) = children.insert(child)
  def count(implicit s: Session) = children.length.run
  def update(child: Child)(implicit s: Session) = {
    child.id match {
      case Some(id) => children.filter(_.id === id).update(child)
      case _ =>
    }
  }
}