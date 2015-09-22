package models.json

import models.{Shift, ShiftType, Volunteer, VolunteerPresence}
import models.json.ShiftJson.{shiftWrites, shiftTypeWrites}
import models.json.VolunteerJson.volunteerWrites
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object VolunteerPresenceJson {

  implicit val volunteerPresenceReads: Reads[VolunteerPresence] = (
    (JsPath \ "volunteerId").read[Long] and
      (JsPath \ "shiftId").read[Long]
    )(VolunteerPresence.apply _)

  val volunteerPresenceUnlifter: ((Shift, ShiftType, Seq[Volunteer])) => Option[(Shift, ShiftType, Seq[Volunteer])] = {
    case (shift, shiftType, seq) => Some (shift, shiftType, seq)
  }

  implicit val volunteerPresenceWrites: Writes[(Shift, ShiftType, Seq[Volunteer])] = (
    (JsPath \ "shift").write[Shift] and
      (JsPath \ "shiftType").write[ShiftType] and
      (JsPath \ "presentVolunteers").write[Seq[Volunteer]]
    )(unlift(volunteerPresenceUnlifter))

}
