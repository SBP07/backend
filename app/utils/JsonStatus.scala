package utils

import play.api.libs.json.{JsObject, Json}
import play.api.libs.json.Json.JsValueWrapper

object JsonStatus {
  def customStatus(statusName: String, fields: (String, JsValueWrapper)*): JsObject = {
    val status: (String, JsValueWrapper) = "status" -> statusName
    Json.obj(fields :+ status:_*)
  }

  def error(fields: (String, JsValueWrapper)*): JsObject = {
    customStatus("error", fields:_*)
  }

  def success(fields: (String, JsValueWrapper)*): JsObject = {
    customStatus("success", fields:_*)
  }
}
