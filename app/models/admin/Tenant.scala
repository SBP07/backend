package models.admin

import java.util.UUID

case class Tenant(id: Option[UUID], canonicalName: String, name: String)
