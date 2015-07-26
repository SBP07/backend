package models.table

import java.time.LocalDate

import helpers.Db.localdateToSqldateMapper
import models._
import play.api.db.slick.HasDatabaseConfig
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

trait AnimatorTableSlice { this: HasDatabaseConfig[JdbcProfile] =>

  import driver.api._

  private[models] class AnimatorTable(tag: Tag) extends Table[Animator](tag, "animator") {

    def * : ProvenShape[Animator] = (id.?, firstName, lastName, mobilePhone, landline, email,
      street, city, bankAccount, yearStartedVolunteering, isPartOfCore, birthDate) <>
      ((Animator.apply _).tupled, Animator.unapply)

    private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    private[models] def firstName = column[String]("first_name")

    private[models] def lastName = column[String]("last_name")

    private[models] def mobilePhone = column[Option[String]]("mobile_phone")

    private[models] def landline = column[Option[String]]("landline")

    private[models] def email = column[Option[String]]("email")

    private[models] def street = column[Option[String]]("street")

    private[models] def city = column[Option[String]]("city")

    private[models] def bankAccount = column[Option[String]]("bank_account")

    private[models] def yearStartedVolunteering = column[Option[Int]]("year_started_volunteering")

    private[models] def isPartOfCore = column[Boolean]("is_core")

    private[models] def birthDate = column[Option[LocalDate]]("birthdate")
  }

}
