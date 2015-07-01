package models.json

import java.time.LocalDate

import models.json.ChildJson.childWrites
import models.{Child, Shift, ShiftType}
import play.api.libs.functional.syntax._
import play.api.libs.json._

object ShiftJson {

  val tripleConverter: Tuple3[ShiftType, Shift, Int] => Option[(Option[Long], LocalDate, String, ShiftType, Int)] = {
    case (shiftType, shift, numberOfChildren) => Some((shift.id, shift.date, shift.place, shiftType, numberOfChildren))
  }

  val tripleDeconverter: (Option[Long], LocalDate, String, ShiftType, Int) => (ShiftType, Shift, Int) =
  {
    case (shiftId, date, place, shiftType, numberOfChildren) => (shiftType, Shift(shiftId, date, place, shiftType.id.get), numberOfChildren)
  }

  implicit val shiftTypeWrites: Writes[ShiftType] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "mnemonic").write[String] and
      (JsPath \ "description").write[String]
    )(unlift(ShiftType.unapply))

  implicit val shiftTypeReads: Reads[ShiftType] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "mnemonic").read[String] and
      (JsPath \ "description").read[String]
    )(ShiftType.apply _)

  implicit val shiftShiftTypeNumberOfChildrenWrites: Writes[(ShiftType, Shift, Int)] = (
    (JsPath \ "shiftId").writeNullable[Long] and
      (JsPath \ "date").write[LocalDate] and
      (JsPath \ "place").write[String] and
      (JsPath \ "shiftType").write[ShiftType] and
      (JsPath \ "numberOfChildren").write[Int]
    )(unlift(tripleConverter))

  implicit val shiftWrites: Writes[Shift] = (
    (JsPath \ "id").writeNullable[Long] and

      (JsPath \ "date").write[LocalDate] and
      (JsPath \ "place").write[String] and
      (JsPath \ "shiftTypeId").write[Long]

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

  implicit val shiftShiftTypeNumberOfChildrenReads: Reads[(ShiftType, Shift, Int)] = (
    (JsPath \ "shiftId").readNullable[Long] and
      (JsPath \ "date").read[LocalDate] and
      (JsPath \ "place").read[String] and
      (JsPath \ "shiftType").read[ShiftType] and
      (JsPath \ "numberOfChildren").read[Int]
    )(tripleDeconverter)

}
