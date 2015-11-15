package models.bindmodels

import play.api.libs.json.{Writes, Json, Reads}

object SignInData {

  case class Data(
                   email: String,
                   password: String,
                   rememberMe: Boolean
                 )

  implicit val dataReads: Reads[Data] = Json.reads[Data]
  implicit val dataWrites: Writes[Data] = Json.writes[Data]
}

