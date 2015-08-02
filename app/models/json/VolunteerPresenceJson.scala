package models.json

import models.{VolunteerPresence}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object VolunteerPresenceJson {

  implicit val volunteerPresenceReads: Reads[VolunteerPresence] = (
    (JsPath \ "volunteerId").read[Long] and
      (JsPath \ "shiftId").read[Long]
    )(VolunteerPresence.apply _)

}
