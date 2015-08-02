package models.json

import models.{AnimatorPresence}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object AnimatorPresenceJson {

  implicit val animatorPresenceReads: Reads[AnimatorPresence] = (
    (JsPath \ "volunteerId").read[Long] and
      (JsPath \ "shiftId").read[Long]
    )(AnimatorPresence.apply _)

}
