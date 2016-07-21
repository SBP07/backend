package models.repositories.slick

import javax.inject.Inject

import helpers.Db.jodaDatetimeToSqldateMapper
import slick.driver.PostgresDriver.api._
import models.{Child, Shift}
import org.joda.time.LocalDate
import play.api.libs.concurrent.Execution.Implicits._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.Future

private[models] class ChildTable(tag: Tag) extends Table[Child](tag, "child") {

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private[models] def firstName = column[String]("first_name")
  private[models] def lastName = column[String]("last_name")
  private[models] def mobilePhone = column[String]("mobile_phone")
  private[models] def landline = column[String]("landline")

  private[models] def street = column[String]("street")
  private[models] def city = column[String]("city")

  private[models] def birthDate = column[LocalDate]("birth_date")

  private[models] def medicalRecordChecked = column[LocalDate]("medical_file_checked")

  def * : ProvenShape[Child] = (id.?, firstName, lastName, mobilePhone.?, landline.?, street.?,
    city.?, birthDate.?, medicalRecordChecked.?) <> (Child.tupled, Child.unapply)

  def shifts: Query[ShiftTable, Shift, Seq] = {
    TableQuery[ChildrenToShiftsTable].filter(_.childId === id).flatMap(_.shiftFK)
  }
}

class ChildRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  val children = TableQuery[ChildTable]

  def findById(id: Long): Future[Option[Child]] = db.run(children.filter(_.id === id).result.headOption)
  def findAll: Future[Seq[Child]] = db.run(children.result)
  def insert(child: Child): Future[Long] = db.run {
    (children returning children.map(_.id)) += child
  }
  def count: Future[Int] = db.run(children.length.result)
  def update(child: Child): Future[Unit] = child.id match {
    case Some(id) => db.run(children.filter(_.id === id).update(child)).map(_ => ())
    case _ => Future.successful(())
  }

  def findByFirstAndLastname(firstName: String, lastName: String): Future[Option[Child]] = db.run {
    children.filter(_.firstName === firstName).filter(_.lastName === lastName).result.headOption
  }
}
