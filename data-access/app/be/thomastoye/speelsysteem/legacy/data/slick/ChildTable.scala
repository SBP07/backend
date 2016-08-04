package be.thomastoye.speelsysteem.legacy.data.slick

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.models.{Child, Shift}
import org.joda.time.LocalDate
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.ProvenShape
import Helpers.jodaDatetimeToSqldateMapper
import be.thomastoye.speelsysteem.legacy.data.ChildRepository

import scala.concurrent.Future

class ChildTable(tag: Tag) extends Table[Child](tag, "child") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def mobilePhone = column[String]("mobile_phone")
  def landline = column[String]("landline")

  def street = column[String]("street")
  def streetNumber = column[String]("street_number")
  def zipCode = column[Int]("zip_code")
  def city = column[String]("city")

  def birthDate = column[LocalDate]("birth_date")

  def * : ProvenShape[Child] = (id.?, firstName, lastName, mobilePhone.?, landline.?, street.?, streetNumber.?,
    zipCode.?, city.?, birthDate.?) <> (Child.tupled, Child.unapply)

  def shifts: Query[ShiftTable, Shift, Seq] = {
    TableQuery[ChildrenToShiftsTable].filter(_.childId === id).flatMap(_.shiftFK)
  }
}

class SlickChildRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) extends ChildRepository {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  val children = TableQuery[ChildTable]

  override def findById(id: Long): Future[Option[Child]] = db.run(children.filter(_.id === id).result.headOption)
  override def findAll: Future[Seq[Child]] = db.run(children.sortBy(s => (s.lastName, s.firstName)).result)
  override def insert(child: Child): Future[Long] = db.run {
    (children returning children.map(_.id)) += child
  }
  override def count: Future[Int] = db.run(children.length.result)
  override def update(child: Child): Future[Unit] = child.id match {
    case Some(id) => db.run(children.filter(_.id === id).update(child)).map(_ => ())
    case _ => Future.successful(())
  }

  override def findByFirstAndLastname(firstName: String, lastName: String): Future[Option[Child]] = db.run {
    children.filter(_.firstName === firstName).filter(_.lastName === lastName).result.headOption
  }
}
