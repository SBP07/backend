package models.tenant

import java.util.UUID

case class Address(id: Option[UUID], street: String, number: String, zipCode: Int, city: String)
