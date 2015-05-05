package models

import org.joda.time.LocalDate

case class Child(
                  id: Option[Long] = None,
                  firstName: String,
                  lastName: String,
                  mobilePhone: Option[String],
                  landline: Option[String],

                  street: Option[String],
                  city: Option[String],

                  birthDate: Option[LocalDate],

                  medicalRecordChecked: Option[LocalDate] = None // None means not ok
                  )
