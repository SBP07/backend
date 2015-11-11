package models.organisation

case class Address(id: Option[Long], street: String, number: String, zipCode: Int, city: String)
