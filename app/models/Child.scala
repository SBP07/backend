package models

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate

import scala.slick.lifted.ProvenShape

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

private[models] class ChildRepository(tag: Tag) extends Table[Child](tag, "child") {
  import helpers.Db.jodaDatetimeToSqldateMapper

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private[models] def firstName = column[String]("first_name")
  private[models] def lastName = column[String]("last_name")
  private[models] def mobilePhone = column[String]("mobile_phone", O.Nullable)
  private[models] def landline = column[String]("landline", O.Nullable)

  private[models] def street = column[String]("street", O.Nullable)
  private[models] def city = column[String]("city", O.Nullable)

  private[models] def birthDate = column[LocalDate]("birth_date", O.Nullable)

  private[models] def medicalRecordChecked = column[LocalDate]("medical_file_checked", O.Nullable)

  def * : ProvenShape[Child] = (id.?, firstName, lastName, mobilePhone.?, landline.?, street.?,
    city.?, birthDate.?, medicalRecordChecked.?) <> (Child.tupled, Child.unapply)

  def shifts: Query[ShiftRepository, Shift, Seq] = {
    TableQuery[ChildrenToShifts].filter(_.childId === id).flatMap(_.shiftFK)
  }
}

object ChildRepository {
  val children = TableQuery[ChildRepository]

  def findById(id: Long)(implicit s: Session): Option[Child] = children.filter(_.id === id).firstOption
  def findAll(implicit s: Session): List[Child] = children.list
  def insert(child: Child)(implicit s: Session): Int = children.insert(child)
  def count(implicit s: Session): Int = children.length.run
  def update(child: Child)(implicit s: Session): Unit = {
    child.id match {
      case Some(id) => children.filter(_.id === id).update(child)
      case _ =>
    }
  }
  def findByFirstAndLastname(firstName: String, lastName: String)(implicit s: Session): Option[Child] = {
    children.filter(_.firstName === firstName).filter(_.lastName === lastName).firstOption
  }
}
