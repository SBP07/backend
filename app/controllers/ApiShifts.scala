package controllers

import java.time.LocalDate
import java.time.format.DateTimeParseException

import models.json.ShiftJson
import models.dao.ShiftDao
import javax.inject.Inject
import models.Shift
import play.api.mvc.{AnyContent, Action}
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ApiShifts @Inject() (shiftDao: ShiftDao)
  extends GenericApiController[Shift]
  with GenericApiControllerRetrieveSlice[Shift]
{
  override val dao = shiftDao
  override val jsonWrites = ShiftJson.shiftWrites


  def byDate(dateString: String): Action[AnyContent] = Action.async { req =>
    import helpers.DateTime.fmt
    import ShiftJson._

    try {
      val date: LocalDate = LocalDate.parse(dateString, fmt)

      shiftDao.findByDateWithTypeAndChildren(date).map { data =>
        Ok(Json.toJson(data))
      }
    }
    catch {
      case e: DateTimeParseException => Future(BadRequest(s"Could not parse date $dateString"))
    }
  }
}
