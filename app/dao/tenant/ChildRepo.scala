package dao.tenant

import java.sql.Date
import java.time.LocalDate
import java.util.UUID

import dao.RepoFor
import io.strongtyped.active.slick._
import slick.ast.BaseTypedType

import io.strongtyped.active.slick.Lens._
import slick.lifted.ProvenShape
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import models.tenant.persistable.PersistableChild

object ChildRepo extends RepoFor[PersistableChild, UUID] {

  import jdbcProfile.api._

  val baseTypedType = implicitly[BaseTypedType[Id]]

  type EntityTable = ChildTable

  val tableQuery = TableQuery[ChildTable]

  def $id(table: ChildTable): Rep[Id] = table.id

  val idLens = lens { child: PersistableChild => child.id } { (child, id) => child.copy(id = id) }

  implicit val myDateColumnType = MappedColumnType.base[LocalDate, Date](
    ld => Date.valueOf(ld),
    d => d.toLocalDate
  )

  class ChildTable(tag: Tag) extends Table[PersistableChild](tag, "child") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)

    def firstName = column[String]("first_name")

    def lastName = column[String]("last_name")

    def birthDate = column[Option[LocalDate]]("birth_date")

    def street = column[Option[String]]("address_street")

    def number = column[Option[String]]("address_number")

    def zipCode = column[Option[Int]]("address_zip_code")

    def city = column[Option[String]]("address_city")

    def tenantId = column[UUID]("tenant_id")

    def * : ProvenShape[PersistableChild] = (id.?, firstName, lastName, birthDate, street, number, zipCode, city, tenantId) <> (PersistableChild.tupled, PersistableChild.unapply _)

  }

}
