package be.thomastoye.speelsysteem.legacy.models

import org.joda.time.LocalDate

case class Animator(
  id: Option[Long] = None,
  firstName: String,
  lastName: String,
  mobilePhone: Option[String],
  landline: Option[String],
  email: Option[String],

  street: Option[String],
  city: Option[String],
  bankAccount: Option[String],
  yearStartedVolunteering: Option[Int],
  isPartOfCore: Boolean = false,
  //attest: Option[Attest] = None,
  birthDate: Option[LocalDate]
)

object AnimatorConstants {
  val minimumYearStartedVolunteering: Int = 2000
  val maximumYearStartedVolunteering: Int = 2030
}
