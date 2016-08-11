package be.thomastoye.speelsysteem.models

case class Child(
  firstName: String,
  lastName: String,

  address: Address,
  contact: ContactInfo,

  birthDate: Option[DayDate],

  attendances: Seq[Attendance]
)

object Child {
  type Id = String
}
