package be.thomastoye.speelsysteem.data

import com.typesafe.scalalogging.StrictLogging
import play.api.libs.json._
import upickle.Js.Value
import upickle.default._
import upickle.json.FastRenderer
import upickle.{json => upicklejson}

class PlayJsonReaderUpickleCompat[T](implicit reads: Reads[T]) extends Reader[T] with StrictLogging {
  override def read0: PartialFunction[Value, T] = {
    case value =>
      val render = FastRenderer.render(value)
      Json.parse(render).validate[T](reads) match {
        case JsSuccess(res, path) => res
        case JsError(errors) =>
          logger.error(s"""Errors encountered while parsing JSON\n offending JSON: $render\n errors: ${errors.mkString(", ")}""")
          throw new Exception()
      }
  }
}

class PlayJsonWriterUpickleCompat[T](implicit writes: Writes[T]) extends Writer[T] {
  override def write0: (T) => Value = value => upicklejson.read(Json.stringify(Json.toJson[T](value)(writes)))
}
