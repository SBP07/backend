package models.json

import java.time.LocalDate

import models.json.ChildJson.childWrites
import models.json.LocalDateJson.defaultJavaLocalDateWrites
import models.{Child, Shift, ShiftType}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object ShiftJson {

  val convertor: Tuple3[ShiftType, Shift, Int] => Option[(Option[Long], LocalDate, String, ShiftType, Int)] = {
    case (shiftType, shift, numberOfChildren) => Some(shift.id, shift.date, shift.place, shiftType, numberOfChildren)
  }

  implicit val shiftTypeWrites: Writes[ShiftType] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "mnemonic").write[String] and
      (JsPath \ "description").write[String]
    )(unlift(ShiftType.unapply))

  implicit val shiftShiftTypeNumberOfChildrenWrites: Writes[(ShiftType, Shift, Int)] = (
    (JsPath \ "shiftId").writeNullable[Long] and
      (JsPath \ "date").write[LocalDate] and
      (JsPath \ "place").write[String] and
      (JsPath \ "shiftType").write[ShiftType] and
      (JsPath \ "numberOfChildren").write[Int]
    )(unlift(convertor))

  implicit val shiftWrites: Writes[Shift] = (
    (JsPath \ "id").writeNullable[Long] and

      (JsPath \ "date").write[LocalDate] and
      (JsPath \ "place").write[String] and
      (JsPath \ "shiftId").write[Long]

    )(unlift(Shift.unapply))


  val shiftTypeShiftAndPresencesConvertor: Tuple3[ShiftType, Shift, Seq[Child]] =>
    Option[(Option[Long], LocalDate, String, ShiftType, Seq[Child])] = {
    case (shiftType, shift, presences) => Some(shift.id, shift.date, shift.place, shiftType, presences)
  }

  implicit val shiftWithTypeAndPresencesWrites: Writes[(ShiftType, Shift, Seq[Child])] = (
    (JsPath \ "shiftId").writeNullable[Long] and
      (JsPath \ "date").write[LocalDate] and
      (JsPath \ "place").write[String] and
      (JsPath \ "shiftType").write[ShiftType] and
      (JsPath \ "presentChildren").write[Seq[Child]]
    )(unlift(shiftTypeShiftAndPresencesConvertor))
}
