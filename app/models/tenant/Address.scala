package models.tenant

case class Address(id: Option[Long], street: String, number: String, zipCode: Int, city: String)
