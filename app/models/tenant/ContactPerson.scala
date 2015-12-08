package models.tenant

import java.util.UUID

import models.helpers.BelongsToTenant

case class ContactPerson(
  id: Option[UUID],
  firstName: String,
  lastName: String,

  address: Option[Address],

  landline: Option[String],
  mobilePhone: Option[String],

  override val tenantCanonicalName: String
) extends BelongsToTenant
