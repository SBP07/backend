package dao.organisation

import dao.RepoFor
import io.strongtyped.active.slick._
import slick.ast.BaseTypedType

import io.strongtyped.active.slick.Lens._
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import models.organisation.Address

object AddressRepo extends RepoFor[Address, Long] {

  import jdbcProfile.api._

  val baseTypedType = implicitly[BaseTypedType[Id]]

  type EntityTable = AddressTable

  val tableQuery = TableQuery[AddressTable]

  def $id(table: AddressTable): Rep[Id] = table.id

  val idLens = lens { address: Address => address.id } { (address, id) => address.copy(id = id) }

  class AddressTable(tag: Tag) extends Table[Address](tag, "address") {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def street = column[String]("street")
    def number = column[String]("street_number")
    def zipcode = column[Int]("zipcode")
    def city = column[String]("city")

    def * = (id.?, street, number, zipcode, city) <>(Address.tupled, Address.unapply)
  }

}
