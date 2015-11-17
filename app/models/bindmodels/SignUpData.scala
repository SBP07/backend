package models.bindmodels

import play.api.libs.json._

case class SignUpData(
                       firstName: String,
                       lastName: String,
                       email: String,
                       password: String
                     )

object SignUpDataJson {
  implicit val dataReads: Reads[SignUpData] = Json.reads[SignUpData]
  implicit val dataWrites: Writes[SignUpData] = Json.writes[SignUpData]
}
