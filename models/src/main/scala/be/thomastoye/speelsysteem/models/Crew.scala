package be.thomastoye.speelsysteem.models

case class Crew(firstName: String, lastName: String, address: Address, bankAccount: Option[String] = None,
  contact: CrewContact, yearStarted: Option[Int] = None, birthDate: Option[Day])

case class Address(street: Option[String] = None, number: Option[String] = None, zipCode: Option[Int] = None, city: Option[String] = None)
case class CrewContact(phone: Seq[PhoneContact], email: Seq[String])
case class PhoneContact(kind: Option[String] = None, comment: Option[String] = None, phoneNumber: String)
case class Day(day: Int, month: Int, year: Int)
