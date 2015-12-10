package dao.tenant

import java.sql.Date
import java.time.LocalDate
import java.util.UUID

import io.strongtyped.active.slick._
import models.helpers.{TenantEntityActions, BelongsToTenantTable}
import slick.ast.BaseTypedType

import io.strongtyped.active.slick.Lens._
import slick.lifted.ProvenShape
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import models.tenant.Child

class ChildRepo extends TenantEntityActions[Child, UUID] {
  import jdbcProfile.api._

  val baseTypedType = implicitly[BaseTypedType[Id]]

  type EntityTable = ChildTable

  val tableQuery = TableQuery[ChildTable]

  def $id(table: ChildTable): Rep[Id] = table.id

  val idLens = lens { child: Child => child.id } { (child, id) => child.copy(id = id) }

  implicit val myDateColumnType = MappedColumnType.base[LocalDate, Date](
    ld => Date.valueOf(ld),
    d => d.toLocalDate
  )

  class ChildTable(tag: Tag) extends Table[Child](tag, "child") with BelongsToTenantTable {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)

    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")
    def birthDate = column[Option[LocalDate]]("birth_date")

    def tenantCanonicalName = column[String]("tenant_canonical_name")

    def * : ProvenShape[Child] = (id.?, firstName, lastName, birthDate, tenantCanonicalName) <> (Child.tupled, Child.unapply _)

  }

}
