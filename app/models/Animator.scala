package models

import play.api.db.slick.Config.driver.simple._
import org.joda.time.LocalDate

case class Animator(
  id: Option[Long] = None,
  firstName: String,
  lastName: String,
  mobilePhone: Option[String],
  landline: Option[String],
  email: Option[String],

  street: Option[String],
  city: Option[String],
  bankAccount: Option[String],
  yearStartedVolunteering: Option[Int],
  isPartOfCore: Boolean = false,
  //attest: Option[Attest] = None,
  birthDate: Option[LocalDate]
)

class Animators(tag: Tag) extends Table[Animator](tag, "ANIMATORS") {
  import helpers.Db.jodaDatetimeToSqldateMapper

def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def mobilePhone = column[String]("mobile_phone")
  def landline = column[String]("landline")
  def email = column[String]("email")

  def street = column[String]("street")
  def city = column[String]("city")

  def bankAccount = column[String]("bank_account")
  def yearStartedVolunteering = column[Int]("year_started_volunteering")
  def isPartOfCore = column[Boolean]("is_core")

  def birthDate = column[LocalDate]("birthdate")

  def * = (id.?, firstName, lastName, mobilePhone.?, landline.?, email.?, street.?, city.?, bankAccount.?,
    yearStartedVolunteering.?, isPartOfCore, birthDate.?) <> (Animator.tupled, Animator.unapply _)
}
object Animators {
  val animators = TableQuery[Animators]
  
  def findById(id: Long)(implicit s: Session): Option[Animator] = animators.filter(_.id === id).firstOption
  def findAll(implicit s: Session) = animators.list
  def insert(animator: Animator)(implicit s: Session) = animators.insert(animator)
  def count(implicit s: Session) = animators.length.run
  def update(animator: Animator)(implicit s: Session) = {
    animator.id match {
      case Some(id) => animators.filter(_.id === id).update(animator)
      case _ =>
    }
  }
}
