package be.thomastoye.speelsysteem.legacy.models

import java.time.LocalDate

import be.thomastoye.speelsysteem.models.{Child, Day}

case class LegacyChild(
  id: Option[String] = None,
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

object LegacyChild {
  def child2legacyModel(id: Option[Child.Id], child: Child): LegacyChild = {
    LegacyChild(
      id,
      child.firstName,
      child.lastName,
      child.mobilePhone,
      child.landline,
      child.street,
      child.streetNumber,
      child.zipCode,
      child.city,
      child.birthDate.map(_.toLocalDate)
    )
  }

  def legacyModel2childAndId(legacyChild: LegacyChild): (Option[Child.Id], Child) = {
    val child = Child(
      legacyChild.firstName,
      legacyChild.lastName,
      legacyChild.mobilePhone,
      legacyChild.landline,
      legacyChild.street,
      legacyChild.streetNumber,
      legacyChild.zipCode,
      legacyChild.city,
      legacyChild.birthDate.map(d => Day(d.getDayOfMonth, d.getMonthValue, d.getYear))
    )

    (legacyChild.id, child)
  }
}
