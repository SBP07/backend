package be.thomastoye.speelsysteem.models

case class Child(
  firstName: String,
  lastName: String,

  address: Address,
  contact: ContactInfo,

  birthDate: Option[Day]
)

object Child {
  type Id = String
}
