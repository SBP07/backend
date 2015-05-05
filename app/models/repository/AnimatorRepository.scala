package models.repository


import java.time.LocalDate

import helpers.Db.localdateToSqldateMapper
import models.Animator
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ProvenShape, TableQuery}

private[models] class AnimatorRepository(tag: Tag) extends Table[Animator](tag, "animator") {

  def * : ProvenShape[Animator] = (id.?, firstName, lastName, mobilePhone.?, landline.?, email.?,
    street.?, city.?, bankAccount.?, yearStartedVolunteering.?, isPartOfCore, birthDate.?) <>
    ((Animator.apply _).tupled, Animator.unapply)

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  private[models] def firstName = column[String]("first_name")

  private[models] def lastName = column[String]("last_name")

  private[models] def mobilePhone = column[String]("mobile_phone", O.Nullable)

  private[models] def landline = column[String]("landline", O.Nullable)

  private[models] def email = column[String]("email", O.Nullable)

  private[models] def street = column[String]("street", O.Nullable)

  private[models] def city = column[String]("city", O.Nullable)

  private[models] def bankAccount = column[String]("bank_account", O.Nullable)

  private[models] def yearStartedVolunteering = column[Int]("year_started_volunteering", O.Nullable)

  private[models] def isPartOfCore = column[Boolean]("is_core")

  private[models] def birthDate = column[LocalDate]("birthdate", O.Nullable)
}

object AnimatorRepository {
  val animators = TableQuery[AnimatorRepository]

  def findById(id: Long)(implicit s: Session): Option[Animator] = animators.filter(_.id === id).firstOption

  def findAll(implicit s: Session): List[Animator] = animators.list

  def insert(animator: Animator)(implicit s: Session): Unit = animators.insert(animator)

  def count(implicit s: Session): Int = animators.length.run

  def update(animator: Animator)(implicit s: Session): Unit = {
    animator.id match {
      case Some(id) => animators.filter(_.id === id).update(animator)
      case _ =>
    }
  }
}
