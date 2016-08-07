package be.thomastoye.speelsysteem.legacy.data.slick

import java.time.LocalDate
import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.models.{LegacyChild, Shift}
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.ProvenShape
import Helpers.javaLocalDateToSqldateMapper
import be.thomastoye.speelsysteem.data.ChildRepository
import be.thomastoye.speelsysteem.models.Child

import scala.concurrent.Future

class ChildTable(tag: Tag) extends Table[LegacyChild](tag, "child") {

  def id = column[String]("id")
  def firstName = column[String]("first_name")
  def lastName = column[String]("last_name")
  def mobilePhone = column[String]("mobile_phone")
  def landline = column[String]("landline")

  def street = column[String]("street")
  def streetNumber = column[String]("street_number")
  def zipCode = column[Int]("zip_code")
  def city = column[String]("city")

  def birthDate = column[LocalDate]("birth_date")

  def * : ProvenShape[LegacyChild] = (id.?, firstName, lastName, mobilePhone.?, landline.?, street.?, streetNumber.?,
    zipCode.?, city.?, birthDate.?) <> ((LegacyChild.apply _).tupled, LegacyChild.unapply)

  def shifts: Query[ShiftTable, Shift, Seq] = {
    TableQuery[ChildrenToShiftsTable].filter(_.childId === id).flatMap(_.shiftFK)
  }
}

class SlickChildRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) extends ChildRepository {
  import LegacyChild._

  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  val children = TableQuery[ChildTable]

  override def findById(id: Child.Id) = {
    db.run(children.filter(_.id === id).result.headOption).map(_.map(legacyModel2childAndId).map(x => (x._1.get, x._2)))
  }

  override def findAll: Future[Seq[(Child.Id, Child)]] = {
    db.run(children.sortBy(s => (s.lastName, s.firstName)).result)
      .map(_.map(legacyModel2childAndId).map(x => (x._1.get, x._2)).sortBy(x => (x._2.lastName, x._2.firstName)))
  }

  override def insert(id: Child.Id, child: Child): Future[Child.Id] = db.run {
    (children returning children.map(_.id)) += child2legacyModel(Some(id), child)
  }

  override def count: Future[Int] = db.run(children.length.result)

  override def update(id: Child.Id, child: Child): Future[Unit] = {
    val legacyChild = child2legacyModel(Some(id), child)
    db.run(children.filter(_.id === id).update(legacyChild)).map(_ => ())
  }
}
