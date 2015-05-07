package models.repository

import java.time.LocalDate

import helpers.Db.localdateToSqldateMapper
import models._
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.ProvenShape

private[models] class ChildRepository(tag: Tag) extends Table[Child](tag, "child") {

  def * : ProvenShape[Child] = (id.?, firstName, lastName, mobilePhone.?, landline.?, street.?,
    city.?, birthDate.?, medicalRecordChecked.?) <>((Child.apply _).tupled, Child.unapply)

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  private[models] def firstName = column[String]("first_name")

  private[models] def lastName = column[String]("last_name")

  private[models] def mobilePhone = column[String]("mobile_phone", O.Nullable)

  private[models] def landline = column[String]("landline", O.Nullable)

  private[models] def street = column[String]("street", O.Nullable)

  private[models] def city = column[String]("city", O.Nullable)

  private[models] def birthDate = column[LocalDate]("birth_date", O.Nullable)

  private[models] def medicalRecordChecked = column[LocalDate]("medical_file_checked", O.Nullable)

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
