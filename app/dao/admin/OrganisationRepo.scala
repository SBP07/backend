package dao.admin

import dao.RepoFor
import io.strongtyped.active.slick._
import models.admin.Organisation
import slick.ast.BaseTypedType

import io.strongtyped.active.slick.Lens._
//import slick.profile.RelationalTableComponent.Table
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global

object OrganisationRepo extends RepoFor[Organisation, Long] {

  import jdbcProfile.api._

  val baseTypedType = implicitly[BaseTypedType[Id]]

  type EntityTable = OrganisationTable

  val tableQuery = TableQuery[OrganisationTable]

  def $id(table: OrganisationTable): Rep[Id] = table.id

  val idLens = lens { org: Organisation => org.id } { (org, id) => org.copy(id = id) }

  class OrganisationTable(tag: Tag) extends Table[Organisation](tag, "tenant") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def canonicalName = column[String]("canonical_name")
    def name = column[String]("name")

    def * = (id.?, canonicalName, name) <>(Organisation.tupled, Organisation.unapply)
  }

}
