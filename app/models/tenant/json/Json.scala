package models.tenant.json

import play.api.libs.json._
import models.tenant._

object AddressJson {
  implicit val addressReads: Reads[Address] = Json.reads[Address]
  implicit val addressWrites: Writes[Address] = Json.writes[Address]
}

object ChildJson {
  implicit val childReads: Reads[Child] = Json.reads[Child]
  implicit val childWrites: Writes[Child] = Json.writes[Child]
}

object ContactPersonJson {
  implicit val contactPersonReads: Reads[ContactPerson] = Json.reads[ContactPerson]
  implicit val contactPersonWrites: Writes[ContactPerson] = Json.writes[ContactPerson]
}

object CrewJson {
  implicit val crewReads: Reads[Crew] = Json.reads[Crew]
  implicit val crewWrites: Writes[Crew] = Json.writes[Crew]
}

object DayJson {
  implicit val dayReads: Reads[Day] = Json.reads[Day]
  implicit val dayWrites: Writes[Day] = Json.writes[Day]
}

object MedicalIncidentJson {
  implicit val medicalIncidentReads: Reads[MedicalIncident] = Json.reads[MedicalIncident]
  implicit val medicalIncidentWrites: Writes[MedicalIncident] = Json.writes[MedicalIncident]
}

object ShiftJson {
  implicit val shiftReads: Reads[Shift] = Json.reads[Shift]
  implicit val shiftWrites: Writes[Shift] = Json.writes[Shift]
}

object ShiftTypeJson {
  implicit val shiftTypeReads: Reads[ShiftType] = Json.reads[ShiftType]
  implicit val shiftTypeWrites: Writes[ShiftType] = Json.writes[ShiftType]
}
