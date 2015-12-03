package models.tenant

import java.util.UUID

case class ContactPerson(
  id: Option[UUID],
  firstName: String,
  lastName: String,

  address: Option[Address],

  landline: Option[String],
  mobilePhone: Option[String],

  tenantId: String
)
