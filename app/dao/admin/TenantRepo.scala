package dao.admin

import java.util.UUID

import dao.RepoFor
import models.admin.Tenant
import slick.ast.BaseTypedType
import io.strongtyped.active.slick._
import slick.driver.PostgresDriver.MappedColumnType

import io.strongtyped.active.slick.Lens._
import scala.language.postfixOps


object TenantRepo extends RepoFor[Tenant, UUID] {
  import jdbcProfile.api._

  val baseTypedType = implicitly[BaseTypedType[Id]]

  type EntityTable = OrganisationTable

  val tableQuery = TableQuery[OrganisationTable]

  def $id(table: OrganisationTable): Rep[Id] = table.id

  val idLens = lens { org: Tenant => org.id } { (org, id) => org.copy(id = id) }

  class OrganisationTable(tag: Tag) extends Table[Tenant](tag, "tenant") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def canonicalName = column[String]("canonical_name")
    def name = column[String]("name")

    def * = (id.?, canonicalName, name) <>(Tenant.tupled, Tenant.unapply)
  }

}
