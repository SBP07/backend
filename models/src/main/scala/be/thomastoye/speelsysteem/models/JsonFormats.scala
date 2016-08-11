package be.thomastoye.speelsysteem.models

import be.thomastoye.speelsysteem.models.Shift.ShiftKind
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._

object JsonFormats {
  val emptyJsonObject = Json.obj()

  implicit val attendanceFormat = Json.format[Attendance]
  implicit val addressFormat = Json.format[Address]
  implicit val phoneContactFormat = Json.format[PhoneContact]
  implicit val crewContactFormat = Json.format[ContactInfo]
  implicit val dayDateFormat = Json.format[DayDate]
  implicit val crewFormat = Json.format[Crew]
  implicit val childFormat = Json.format[Child]
  implicit val priceFormat = Json.format[Price]
  implicit val startAndEndTimeFormat = Json.format[StartAndEndTime]

  implicit val shiftKindFormat: Format[ShiftKind] = new Format[ShiftKind] {
    override def writes(o: ShiftKind): JsValue = JsString(o.mnemonic)

    override def reads(json: JsValue): JsResult[ShiftKind] = json.validate[String].map(ShiftKind.apply)
  }

  private val shiftWrites: Writes[Shift] = (
      (JsPath \ "id").write[String] and
      (JsPath \ "price").write[Price] and
      (JsPath \ "childrenCanBePresent").write[Boolean] and
      (JsPath \ "crewCanBePresent").write[Boolean] and
      (JsPath \ "kind").write[ShiftKind] and
      (JsPath \ "location").writeNullable[String] and
      (JsPath \ "description").writeNullable[String] and
      (JsPath \ "startAndEnd").writeNullable[StartAndEndTime]
    )(unlift(Shift.unapply))

  private val shiftReads: Reads[Shift] = (
      (JsPath \ "id").read[String] and
      (JsPath \ "price").read[Price] and
      (JsPath \ "childrenCanBePresent").read[Boolean] and
      (JsPath \ "crewCanBePresent").read[Boolean] and
      (JsPath \ "kind").read[ShiftKind] and
      (JsPath \ "location").readNullable[String] and
      (JsPath \ "description").readNullable[String] and
      (JsPath \ "startAndEnd").readNullable[StartAndEndTime]
    )(Shift.apply _)


  implicit val shiftFormat: Format[Shift] = Format(shiftReads, shiftWrites)

  implicit val dayFormat = Json.format[Day]

  implicit val dayWithIdWrites = new Writes[(Day.Id, Day)] {
    override def writes(o: (Day.Id, Day)): JsValue = Json.obj("id" -> o._1) ++ Json.toJson(o._2).as[JsObject]
  }
}
