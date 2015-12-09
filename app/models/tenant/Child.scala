package models.tenant

import java.time.LocalDate
import java.util.UUID

import models.helpers.BelongsToTenant

case class Child(
  id: Option[UUID],
  firstName: String,
  lastName: String,

  birthDate: Option[LocalDate],

  tenantCanonicalName: String

) extends BelongsToTenant[Child] {
  override def copyTenantCanonicalName(tenantCanonicalName: String): Child = copy(tenantCanonicalName = tenantCanonicalName)
}
