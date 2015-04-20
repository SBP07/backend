package models

import org.joda.time.LocalDate

case class Child(
                  id: Option[Long] = None,
                  firstName: String,
                  lastName: String,
                  mobilePhone: Option[String],
                  landline: Option[String],

                  street: Option[String],
                  city: Option[String],

                  birthDate: Option[LocalDate],

                  medicalRecordChecked: Option[LocalDate] = None // None means not ok
                  )

object Child {

  import play.api.libs.functional.syntax._
  import play.api.libs.json._

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




