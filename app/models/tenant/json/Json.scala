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
  import AddressJson._

  implicit val childReads: Reads[Child] = (
    (JsPath \ "id").readNullable[UUID] and

      (JsPath \ "firstName").read[String] and
      (JsPath \ "lastName").read[String] and

      (JsPath \ "birthDate").readNullable[LocalDate] and

      (JsPath \ "address").readNullable[Address] and

      (JsPath \ "tenantId").read[UUID]

    ) (Child.apply _)

  implicit val childWrites: Writes[Child] = (
    (JsPath \ "id").write[Option[UUID]] and

    (JsPath \ "firstName").write[String] and
    (JsPath \ "lastName").write[String] and

    (JsPath \ "birthDate").write[Option[LocalDate]] and

    (JsPath \ "address").write[Option[Address]] and

    (JsPath \ "tenantId").write[UUID]

    ) (unlift(Child.unapply))

}

object ContactPersonJson {
  implicit val contactPersonReads: Reads[ContactPerson] = Json.reads[ContactPerson]
  implicit val contactPersonWrites: Writes[ContactPerson] = Json.writes[ContactPerson]
}

object CrewJson {
  import AddressJson._

  implicit val crewReads: Reads[Crew] = Json.reads[Crew]
  implicit val crewWrites: Writes[Crew] = Json.writes[Crew]
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

object AuthCrewUserJson {
  import AddressJson._
  import RoleJson._

  implicit val crewReads: Reads[AuthCrewUser] = Json.reads[AuthCrewUser]
  implicit val crewWrites: Writes[AuthCrewUser] = Json.writes[AuthCrewUser]
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
