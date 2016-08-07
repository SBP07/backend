package be.thomastoye.speelsysteem.legacy.models

import java.time.LocalDate

import be.thomastoye.speelsysteem.models._

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
      child.contact.phone.find(_.kind.contains("mobile")).map(_.phoneNumber),
      child.contact.phone.find(_.kind.contains("landline")).map(_.phoneNumber),
      child.address.street,
      child.address.number,
      child.address.zipCode,
      child.address.city,
      child.birthDate.map(_.toLocalDate)
    )
  }

  def legacyModel2childAndId(legacyChild: LegacyChild): (Option[Child.Id], Child) = {
    val contact = ContactInfo(
      legacyChild.mobilePhone.map(PhoneContact(Some("mobile"), None, _)).toSeq ++ legacyChild.landline.map(PhoneContact(Some("landline"), None, _)),
      Seq.empty
    )

    val child = Child(
      legacyChild.firstName,
      legacyChild.lastName,
      Address(
        legacyChild.street,
        legacyChild.streetNumber,
        legacyChild.zipCode,
        legacyChild.city
      ),
      contact,
      legacyChild.birthDate.map(d => DayDate(d.getDayOfMonth, d.getMonthValue, d.getYear))
    )

    (legacyChild.id, child)
  }
}
