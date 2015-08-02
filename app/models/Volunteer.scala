package models

import java.time.LocalDate

case class Volunteer(
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

object VolunteerConstants {
  val minimumYearStartedVolunteering: Int = 2000
  val maximumYearStartedVolunteering: Int = 2030
}
