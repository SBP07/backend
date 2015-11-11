package dao.tenant

import java.util.UUID

import dao.RepoFor
import io.strongtyped.active.slick._
import slick.ast.BaseTypedType

import io.strongtyped.active.slick.Lens._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import models.tenant.Child

object ChildRepo extends RepoFor[Child, UUID] {

  import jdbcProfile.api._

  val baseTypedType = implicitly[BaseTypedType[Id]]

  type EntityTable = ChildTable

  val tableQuery = TableQuery[ChildTable]

  def $id(table: ChildTable): Rep[Id] = table.id

  val idLens = lens { child: Child => child.id } { (child, id) => child.copy(id = id) }

  class ChildTable(tag: Tag) extends Table[Child](tag, "child") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def firstName = column[String]("first_name")
    def lastName = column[String]("last_name")

    def * = (id.?, firstName, lastName) <>(Child.tupled, Child.unapply)
  }

}
