package models.tenant.persistable

import java.time.LocalDate
import java.util.UUID

import models.helpers.BelongsToTenant
import models.tenant.{Child, Address}

case class PersistableChild(
                  id: Option[UUID],
                  firstName: String,
                  lastName: String,

                  birthDate: Option[LocalDate],

                  street: Option[String],
                  number: Option[String],
                  zipCode: Option[Int],
                  city: Option[String],

                  override val tenantId: UUID
                ) extends BelongsToTenant
{
  def convert: Child = {
    val address = for { street <- street; number <- number; zipCode <- zipCode; city <- city }
      yield { Address(street, number, zipCode, city) }
    Child(id, firstName, lastName, birthDate, address, tenantId)
  }
}

object PersistableChild extends ((Option[UUID], String, String, Option[LocalDate], Option[String], Option[String],
  Option[Int], Option[String], UUID) => PersistableChild)
{
  def build(child: Child): PersistableChild = {
    PersistableChild(
      child.id,
      child.firstName,
      child.lastName,
      child.birthDate,
      child.address.map(_.street),
      child.address.map(_.number),
      child.address.map(_.zipCode),
      child.address.map(_.city),
      child.tenantId)
  }
}
