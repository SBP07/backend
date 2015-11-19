package models.helpers

import java.util.UUID

trait BelongsToTenant {
  val tenantId: UUID
}
