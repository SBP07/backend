package models.tenant

import java.util.UUID

import models.helpers.BelongsToTenant

case class ActivityType(
  id: Option[UUID],
  mnemonic: String,
  description: String,
  tenantCanonicalName: String
) extends BelongsToTenant[ActivityType] {
  def copyTenantCanonicalName(name: String): ActivityType = copy(tenantCanonicalName = name)
}
