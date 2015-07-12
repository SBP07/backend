package controllers

import javax.inject.Inject

import models.repository.ChildPresenceRepository

import play.api.libs.json.Writes
import play.api.mvc.{Action, AnyContent, Controller}
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import models.json.ShiftJson._

class ApiAttendances @Inject()(childPresenceRepository: ChildPresenceRepository) extends Controller
{
  def presencesForChild(childId: Long): Action[AnyContent] = Action.async {
    childPresenceRepository.findPresencesForChild(childId).map( shifts =>
      Ok(Json.toJson(shifts))
    )
  }
}
