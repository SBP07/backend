package models.json

import java.time.LocalDate

import models.Animator
import models.json.LocalDateJson.defaultJavaLocalDateWrites
import play.api.libs.functional.syntax._
import play.api.libs.json._

object AnimatorJson {

  implicit val animatorWrites: Writes[Animator] = (
    (JsPath \ "id").writeNullable[Long] and
      (JsPath \ "firstName").write[String] and
      (JsPath \ "lastName").write[String] and

      (JsPath \ "mobilePhone").writeNullable[String] and
      (JsPath \ "landline").writeNullable[String] and
      (JsPath \ "email").writeNullable[String] and

      (JsPath \ "street").writeNullable[String] and
      (JsPath \ "city").writeNullable[String] and

      (JsPath \ "bankAccount").writeNullable[String] and

      (JsPath \ "yearStartedVolunteering").writeNullable[Int] and
      (JsPath \ "isPartOfCore").write[Boolean] and
      (JsPath \ "birthDate").writeNullable[LocalDate]

    )(unlift(Animator.unapply))
}
