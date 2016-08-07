package be.thomastoye.speelsysteem.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class Address(street: Option[String] = None, number: Option[String] = None, zipCode: Option[Int] = None, city: Option[String] = None)

case class ContactInfo(phone: Seq[PhoneContact], email: Seq[String])

case class PhoneContact(kind: Option[String] = None, comment: Option[String] = None, phoneNumber: String)

case class Day(day: Int, month: Int, year: Int) {
  def toLocalDate: LocalDate = LocalDate.of(year, month, day)
  override def toString = toLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
