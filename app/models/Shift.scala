package models

import org.joda.time.LocalDate

case class Shift(id: Option[Long] = None, date: LocalDate, place: String, shiftId: Long)
case class ShiftType(id: Option[Long], mnemonic: String, description: String)

object Shift {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  implicit val shiftWrites: Writes[Shift] = (
    (JsPath \ "id").writeNullable[Long] and

      (JsPath \ "date").write[LocalDate] and
      (JsPath \ "place").write[String] and
      (JsPath \ "shiftId").write[Long]

    )(unlift(Shift.unapply))
}
