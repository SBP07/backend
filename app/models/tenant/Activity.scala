package models.tenant

import java.time.{Instant, LocalDate}
import java.util.UUID

import models.helpers.BelongsToTenant

case class Activity(
  id: Option[UUID] = None,
  place: Option[String],
  activityTypeId: UUID,
  date: LocalDate,
  startTime: Option[Instant],
  endTime: Option[Instant],
  tenantCanonicalName: String
) extends BelongsToTenant[Activity] {
  def copyTenantCanonicalName(tenantCanonicalName: String): Activity = {
    copy(tenantCanonicalName = tenantCanonicalName)
  }
}
