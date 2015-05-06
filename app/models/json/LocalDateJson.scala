package models.json

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import helpers.DateTime.fmt
import play.api.data.validation.ValidationError
import play.api.libs.json._


object LocalDateJson {

  implicit val defaultJavaLocalDateReads = javaLocalDateReads(fmt)

  def javaLocalDateWrites(pattern: String): Writes[LocalDate] = new Writes[LocalDate] {
    def writes(d: LocalDate): JsValue = JsString(d.format(fmt))
  }

  def javaLocalDateReads(format: DateTimeFormatter): Reads[LocalDate] = new Reads[LocalDate] {

    def reads(json: JsValue): JsResult[LocalDate] = json match {
      case JsString(s) => parseDate(s) match {
        case Some(d) => JsSuccess(d)
        case None => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.javadate.format", format.toString))))
      }
      case _ => JsError(Seq(JsPath() -> Seq(ValidationError("error.expected.date"))))
    }

    private def parseDate(input: String): Option[LocalDate] =
      scala.util.control.Exception.allCatch[LocalDate] opt LocalDate.parse(input, format)
  }

  implicit object defaultJavaLocalDateWrites extends Writes[java.time.LocalDate] {
    def writes(d: java.time.LocalDate): JsValue = JsString(d.format(fmt))
  }


}
