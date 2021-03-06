package models.tenant.json

import java.time.LocalDate
import java.util.UUID

import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import models.tenant._
import models.Role

object AddressJson {
  implicit val addressReads: Reads[Address] = Json.reads[Address]
  implicit val addressWrites: Writes[Address] = Json.writes[Address]
}

object ChildJson {
  implicit val childReads: Reads[Child] = Json.reads[Child]
  implicit val childWrites: Writes[Child] = Json.writes[Child]

}

object ContactPersonJson {
  import AddressJson._

  implicit val contactPersonReads: Reads[ContactPerson] =  Json.reads[ContactPerson]
  implicit val contactPersonWrites: Writes[ContactPerson] = Json.writes[ContactPerson]
}

object RoleJson {
  implicit val roleReads: Reads[Role] = Json.reads[Role]

  implicit object RoleReads extends Reads[Role] {
    def reads(json: JsValue): JsResult[Role] = json match {
      case JsString(s) => JsSuccess(Role(s))
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.jsstring"))))
    }
  }

  implicit object RoleWrites extends Writes[Role] {
    def writes(o: Role): JsString = JsString(o.name)
  }
}

object CrewJson {
  import AddressJson._
  import RoleJson._

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

object ActivityJson {
  implicit val activityReads: Reads[Activity] = Json.reads[Activity]
  implicit val activityWrites: Writes[Activity] = Json.writes[Activity]
}

object ActivityTypeJson {
  implicit val activityTypeReads: Reads[ActivityType] = Json.reads[ActivityType]
  implicit val activityTypeWrites: Writes[ActivityType] = Json.writes[ActivityType]
}
