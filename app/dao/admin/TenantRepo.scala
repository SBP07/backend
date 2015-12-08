package dao.admin

import java.util.UUID

import dao.RepoFor
import io.strongtyped.active.slick.Lens
import models.admin.Tenant
import slick.ast.BaseTypedType
import io.strongtyped.active.slick.Lens._
import slick.lifted.ProvenShape
import scala.language.postfixOps


object TenantRepo extends RepoFor[Tenant, UUID] {
  import jdbcProfile.api._

  val baseTypedType: BaseTypedType[UUID] = implicitly[BaseTypedType[Id]]

  type EntityTable = TenantTable

  val tableQuery: TableQuery[TenantTable] = TableQuery[TenantTable]

  def $id(table: TenantTable): Rep[Id] = table.id

  val idLens: Lens[Tenant, Option[UUID]] = lens { org: Tenant => org.id } { (org, id) => org.copy(id = id) }

  class TenantTable(tag: Tag) extends Table[Tenant](tag, "tenant") {
    def id: Rep[Id] = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def canonicalName: Rep[String] = column[String]("canonical_name")
    def name: Rep[String] = column[String]("name")

    def `*`: ProvenShape[Tenant] = (id.?, canonicalName, name) <>(Tenant.tupled, Tenant.unapply)
  }

}
