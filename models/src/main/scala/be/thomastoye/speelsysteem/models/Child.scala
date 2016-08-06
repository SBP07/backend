package be.thomastoye.speelsysteem.models

case class Child(
  firstName: String,
  lastName: String,
  mobilePhone: Option[String],
  landline: Option[String],

  street: Option[String],
  streetNumber: Option[String],
  zipCode: Option[Int],
  city: Option[String],

  birthDate: Option[Day]
)

object Child {
  type Id = String
}
