package models.json

import org.joda.time.LocalDate
import models.{Shift, ShiftType}

//case class Shift(id: Option[Long] = None, date: LocalDate, place: String, shiftId: Long)
//case class ShiftType(id: Option[Long], mnemonic: String, description: String)

object ShiftJson {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

  val fun: Function1[Tuple3[ShiftType, Shift, Int], Option[Tuple5[Option[Long], LocalDate, String, ShiftType, Int]]] = {
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
    )(unlift(fun))

  implicit val shiftWrites: Writes[Shift] = (
    (JsPath \ "id").writeNullable[Long] and

      (JsPath \ "date").write[LocalDate] and
      (JsPath \ "place").write[String] and
      (JsPath \ "shiftId").write[Long]

    )(unlift(Shift.unapply))
}
