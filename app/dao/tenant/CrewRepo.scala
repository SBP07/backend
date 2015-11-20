package dao.tenant

import java.sql.Date
import java.time.LocalDate
import java.util.UUID

import io.strongtyped.active.slick.Lens._

import dao.RepoFor
import models.tenant.persistable.PersistableCrew
import slick.ast.BaseTypedType
import slick.lifted.ProvenShape


object CrewRepo extends RepoFor[PersistableCrew, UUID] {

  import jdbcProfile.api._

  val baseTypedType = implicitly[BaseTypedType[Id]]

  type EntityTable = CrewTable

  val tableQuery = TableQuery[CrewTable]

  def $id(table: CrewTable): Rep[Id] = table.id

  val idLens = lens { crew: PersistableCrew => crew.id } { (crew, id) => crew.copy(id = id) }

  implicit val myDateColumnType = MappedColumnType.base[LocalDate, Date](
    ld => Date.valueOf(ld),
    d => d.toLocalDate
  )

  class CrewTable(tag: Tag) extends Table[PersistableCrew](tag, "crew") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[Option[String]]("first_name")
    def lastName = column[Option[String]]("last_name")
    def fullName = column[Option[String]]("full_name")
    def email = column[Option[String]]("email")
    def avatarUrl = column[Option[String]]("avatar_url")
    def birthDate = column[Option[LocalDate]]("birth_date")
    def street = column[Option[String]]("address_street")
    def number = column[Option[String]]("address_number")
    def zipCode = column[Option[Int]]("address_zip_code")
    def city = column[Option[String]]("address_city")
    def tenantId = column[UUID]("tenant_id")

    def * : ProvenShape[PersistableCrew] = (id.?, firstName, lastName, fullName, email, avatarUrl, birthDate, street, number, zipCode, city, tenantId) <> (PersistableCrew.tupled, PersistableCrew.unapply _)

  }

}
