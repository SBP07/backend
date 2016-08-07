package be.thomastoye.speelsysteem.models

case class Address(street: Option[String] = None, number: Option[String] = None, zipCode: Option[Int] = None, city: Option[String] = None)
