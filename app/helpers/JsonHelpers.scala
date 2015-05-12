package helpers

import play.api.libs.json._
import play.api.libs.functional.syntax._

object JsonHelpers {
  case class JsonError(error: String, extraInfo: String)

  implicit val jsonErrorWrites: Writes[JsonError] = (
    (JsPath \ "error").write[String] and
      (JsPath \ "extraInfo").write[String]
  )(unlift(JsonError.unapply))

  implicit val jsonErrorReads: Reads[JsonError] = (
    (JsPath \ "error").read[String] and
      (JsPath \ "extraInfo").read[String]
  )(JsonError.apply _)

  /**
   * Creates a not found JSON object, useful to return this in controllers
   * @param msg A message that will be added to the object
   * @return A JSON response with an error
   */
  def notFound(msg: String): JsValue = Json.toJson(JsonError("Not Found", msg))
}
