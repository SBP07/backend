package models.tenant

import java.util.UUID

import models.helpers.BelongsToTenant

case class Child(id: Option[UUID], firstName: String, lastName: String)
