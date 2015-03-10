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

private[models] class Children(tag: Tag) extends Table[Child](tag, "CHILDREN") {
  import helpers.Db.jodaDatetimeToSqldateMapper

  private[models] def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  private[models] def firstName = column[String]("FIRST_NAME")
  private[models] def lastName = column[String]("LAST_NAME")
  private[models] def mobilePhone = column[String]("MOBILE_PHONE", O.Nullable)
  private[models] def landline = column[String]("LANDLINE", O.Nullable)

  private[models] def street = column[String]("STREET", O.Nullable)
  private[models] def city = column[String]("CITY", O.Nullable)

  private[models] def birthDate = column[LocalDate]("BIRTHDATE", O.Nullable)

  private[models] def medicalRecordChecked = column[LocalDate]("MED_REC_CHECKED", O.Nullable)

  def * : ProvenShape[Child] = (id.?, firstName, lastName, mobilePhone.?, landline.?, street.?,
    city.?, birthDate.?, medicalRecordChecked.?) <> (Child.tupled, Child.unapply)

  def activities: Query[Activities, Activity, Seq] = {
    TableQuery[ChildrenToActivities].filter(_.childId === id).flatMap(_.activityFK)
  }
}

object Children {
  val children = TableQuery[Children]

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
}
