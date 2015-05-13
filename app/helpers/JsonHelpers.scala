package helpers

import play.api.libs.json._
import play.api.libs.functional.syntax._

object JsonHelpers {
  case class JsonStatus(error: String, extraInfo: JsValue)

  implicit val jsonStatusWrites: Writes[JsonStatus] = (
    (JsPath \ "error").write[String] and
      (JsPath \ "extraInfo").write[JsValue]
  )(unlift(JsonStatus.unapply))

  implicit val jsonStatusReads: Reads[JsonStatus] = (
    (JsPath \ "error").read[String] and
      (JsPath \ "extraInfo").read[JsValue]
  )(JsonStatus.apply _)

  /**
   * Creates a not found JSON object, useful to return this in controllers
   * @param msg A message that will be added to the object
   * @return A JSON response with an error
   */
  def notFound(msg: JsValue): JsValue = Json.toJson(JsonStatus("Not Found", msg))

  /**
   * Creates a bad request JSON object, useful to return this in controllers
   * @param msg A message that will be added to the object
   * @return A JSON response with an error
   */
  def badRequest(msg: JsValue): JsValue = Json.toJson(JsonStatus("Bad Request", msg))

  /**
   * Creates a success JSON object, useful to return this in controllers
   * @param msg A message that will be added to the object
   * @return A JSON response with an error
   */
  def success(msg: JsValue): JsValue = Json.toJson(JsonStatus("Success", msg))
}
