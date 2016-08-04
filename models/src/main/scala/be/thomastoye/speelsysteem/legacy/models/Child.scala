package be.thomastoye.speelsysteem.legacy.models

import org.joda.time.LocalDate

case class Child(
  id: Option[Long] = None,
  firstName: String,
  lastName: String,
  mobilePhone: Option[String],
  landline: Option[String],

  street: Option[String],
  streetNumber: Option[String],
  zipCode: Option[Int],
  city: Option[String],

  birthDate: Option[LocalDate]
)
