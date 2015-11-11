package models.admin.json

import models.admin.Tenant
import play.api.libs.json._

object TenantJson {
  implicit val tenantReads: Reads[Tenant] = Json.reads[Tenant]
  implicit val tenantWrites: Writes[Tenant] = Json.writes[Tenant]
}
