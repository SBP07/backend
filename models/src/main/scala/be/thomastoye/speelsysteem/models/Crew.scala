package be.thomastoye.speelsysteem.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class Crew(firstName: String, lastName: String, address: Address, bankAccount: Option[String] = None,
  contact: CrewContact, yearStarted: Option[Int] = None, birthDate: Option[Day])

object Crew {
  type Id = String
}

case class Address(street: Option[String] = None, number: Option[String] = None, zipCode: Option[Int] = None, city: Option[String] = None)
case class CrewContact(phone: Seq[PhoneContact], email: Seq[String])
case class PhoneContact(kind: Option[String] = None, comment: Option[String] = None, phoneNumber: String)
case class Day(day: Int, month: Int, year: Int) {
  def toLocalDate: LocalDate = LocalDate.of(year, month, day)
  override def toString = toLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
