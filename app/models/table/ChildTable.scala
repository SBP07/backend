package models.table

import java.time.LocalDate

import helpers.Db._

import models._
import slick.lifted.ProvenShape
import slick.driver.PostgresDriver.api._


private[models] class ChildTable(tag: Tag) extends Table[Child](tag, "child") {

  def * : ProvenShape[Child] = (id.?, firstName, lastName, mobilePhone, landline, street,
    city, birthDate, medicalRecordChecked) <>((Child.apply _).tupled, Child.unapply)

  def id: Rep[Long] = column[Long]("id", O.PrimaryKey, O.AutoInc)

  private[models] def firstName = column[String]("first_name")
  private[models] def lastName = column[String]("last_name")
  private[models] def mobilePhone = column[Option[String]]("mobile_phone")
  private[models] def landline = column[Option[String]]("landline")
  private[models] def street = column[Option[String]]("street")
  private[models] def city = column[Option[String]]("city")
  private[models] def birthDate = column[Option[LocalDate]]("birth_date")
  private[models] def medicalRecordChecked = column[Option[LocalDate]]("medical_file_checked")

  //    def shifts: Query[ShiftTable, Shift, Seq] = {
  //      TableQuery[ChildrenToShiftsTable].filter(_.childId === id).flatMap(_.shiftFK)
  //    }
}
