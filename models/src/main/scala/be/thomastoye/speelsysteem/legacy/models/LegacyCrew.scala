package be.thomastoye.speelsysteem.legacy.models

import org.joda.time.LocalDate

case class LegacyCrew(
  id: Option[String] = None,
  firstName: String,
  lastName: String,
  mobilePhone: Option[String],
  landline: Option[String],
  email: Option[String],

  street: Option[String],
  streetNumber: Option[String],
  zipCode: Option[Int],
  city: Option[String],
  bankAccount: Option[String],
  yearStartedVolunteering: Option[Int],
  isPartOfCore: Boolean = false,
  //attest: Option[Attest] = None,
  birthDate: Option[LocalDate]
)

object LegacyCrewConstants {
  val minimumYearStartedVolunteering: Int = 2000
  val maximumYearStartedVolunteering: Int = 2030
}
