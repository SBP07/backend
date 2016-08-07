package be.thomastoye.speelsysteem.models

import play.api.libs.json.Json

object JsonFormats {
  implicit val addressFormat = Json.format[Address]
  implicit val phoneContactFormat = Json.format[PhoneContact]
  implicit val crewContactFormat = Json.format[ContactInfo]
  implicit val dayFormat = Json.format[DayDate]
  implicit val crewFormat = Json.format[Crew]
  implicit val childFormat = Json.format[Child]
}
