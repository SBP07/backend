package dao.tenant

import java.sql.Date
import java.time.LocalDate
import java.util.UUID

import dao.RepoFor
import io.strongtyped.active.slick._
import models.tenant.persistable.PersistableContactPerson
import slick.ast.BaseTypedType

import io.strongtyped.active.slick.Lens._
import slick.lifted.ProvenShape
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

object ContactPersonRepo extends RepoFor[PersistableContactPerson, UUID] {
  import jdbcProfile.api._

  val baseTypedType = implicitly[BaseTypedType[Id]]

  type EntityTable = ContactPersonTable

  val tableQuery = TableQuery[ContactPersonTable]

  def $id(table: ContactPersonTable): Rep[Id] = table.id

  val idLens = lens { person: PersistableContactPerson => person.id } { (person, id) => person.copy(id = id) }

  class ContactPersonTable(tag: Tag) extends Table[PersistableContactPerson](tag, "contact_person") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)

    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")

    def street = column[Option[String]]("address_street")
    def zipCode = column[Option[Int]]("address_zip_code")
    def city = column[Option[String]]("address_city")
    def country = column[Option[String]]("address_country")

    def landline = column[Option[String]]("landline")
    def mobilePhone = column[Option[String]]("mobile_phone")

    def tenantId = column[String]("tenant_cname")

    def * : ProvenShape[PersistableContactPerson] = (id.?, firstName, lastName, street, zipCode, city, country, landline, mobilePhone,
      tenantId) <> (PersistableContactPerson.tupled, PersistableContactPerson.unapply)

  }
}
