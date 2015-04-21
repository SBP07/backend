package models

import org.joda.time.LocalDate

case class Animator(
  id: Option[Long] = None,
  firstName: String,
  lastName: String,
  mobilePhone: Option[String],
  landline: Option[String],
  email: Option[String],

  street: Option[String],
  city: Option[String],
  bankAccount: Option[String],
  yearStartedVolunteering: Option[Int],
  isPartOfCore: Boolean = false,
  //attest: Option[Attest] = None,
  birthDate: Option[LocalDate]
)

object Animator {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

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

object AnimatorConstants {
  val minimumYearStartedVolunteering: Int = 2000
  val maximumYearStartedVolunteering: Int = 2030
}
