package models.json

import models.{ChildPresence, Child}
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object ChildPresenceJson {

  implicit val childPresenceReads: Reads[ChildPresence] = (
      (JsPath \ "childId").read[Long] and
      (JsPath \ "shiftId").read[Long]
    )(ChildPresence.apply _)

}
