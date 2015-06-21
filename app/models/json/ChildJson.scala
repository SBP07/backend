package models.json

import java.time.LocalDate

import models.Child
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._
import play.api.libs.json._

object ChildJson {
  implicit val format = helpers.DateTime.fmt

  implicit val childReads: Reads[Child] = (
    (JsPath \ "id").readNullable[Long] and
      (JsPath \ "firstName").read[String] and
      (JsPath \ "lastName").read[String] and
      (JsPath \ "mobilePhone").readNullable[String] and
      (JsPath \ "landline").readNullable[String] and

      (JsPath \ "street").readNullable[String] and
      (JsPath \ "city").readNullable[String] and

      (JsPath \ "birthDate").readNullable[LocalDate] and

      (JsPath \ "medicalRecordChecked").readNullable[LocalDate]
    )(Child.apply _)

  implicit val childWrites: Writes[Child] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "firstName").write[String] and
      (JsPath \ "lastName").write[String] and

      (JsPath \ "mobilePhone").writeNullable[String] and
      (JsPath \ "landline").writeNullable[String] and

      (JsPath \ "street").writeNullable[String] and
      (JsPath \ "city").writeNullable[String] and

      (JsPath \ "birthDate").writeNullable[LocalDate] and
      (JsPath \ "medicalRecordChecked").writeNullable[LocalDate]

    )(unlift(Child.unapply))

}
