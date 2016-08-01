package be.thomastoye.speelsysteem.models

import play.api.libs.json.Json

object JsonFormats {
  implicit val addressFormat = Json.format[Address]
  implicit val phoneContactFormat = Json.format[PhoneContact]
  implicit val crewContactFormat = Json.format[CrewContact]
  implicit val dayFormat = Json.format[Day]
  implicit val crewFormat = Json.format[Crew]
}
