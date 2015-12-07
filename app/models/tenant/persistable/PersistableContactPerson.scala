package models.tenant.persistable

import java.time.LocalDate
import java.util.UUID

import models.helpers.BelongsToTenant
import models.tenant.Address
import models.tenant.ContactPerson
import models.tenant.{ContactPerson, Address}

case class PersistableContactPerson(
  id: Option[UUID],
  firstName: String,
  lastName: String,

  street: Option[String],
  zipCode: Option[Int],
  city: Option[String],
  country: Option[String],

  landline: Option[String],
  mobilePhone: Option[String],

  override val tenantName: String
) extends BelongsToTenant
{
  def convert: ContactPerson = {
    val address = for { street <- street; zipCode <- zipCode; city <- city; country <- country }
      yield { Address(street, zipCode, city, country) }
    ContactPerson(id, firstName, lastName, address, landline, mobilePhone, tenantName)
  }
}

object PersistableContactPerson extends ((Option[UUID], String, String, Option[String],
  Option[Int], Option[String], Option[String], Option[String], Option[String], String) => PersistableContactPerson)
{
  def build(contactPerson: ContactPerson): PersistableContactPerson = {
    PersistableContactPerson(
      contactPerson.id,
      contactPerson.firstName,
      contactPerson.lastName,
      contactPerson.address.map(_.street),
      contactPerson.address.map(_.zipCode),
      contactPerson.address.map(_.city),
      contactPerson.address.map(_.country),
      contactPerson.landline,
      contactPerson.mobilePhone,
      contactPerson.tenantName)
  }
}
