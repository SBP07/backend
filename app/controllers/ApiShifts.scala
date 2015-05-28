package controllers

import models.json.ShiftJson._
import models.repository.{ChildPresenceRepository, ShiftRepository}
import play.api.db.slick.DBAction
import play.api.libs.json._
import play.api.mvc._
import javax.inject._

class ApiShifts @Inject() (shiftRepository: ShiftRepository) extends Controller {
  def shiftById(id: Long): Action[AnyContent] = DBAction { implicit req =>
    val presences = shiftRepository.findByIdWithTypeAndNumberOfPresences(id)(req.dbSession)
    (for {
      shift <- presences.map(_._1)
      shiftType <- presences.map(_._2)
      shiftId <- shift.id
    } yield {
        val presentChildren = ChildPresenceRepository.findAllForShift(shiftId)(req.dbSession).map(_._1)
        Ok(Json.toJson((shiftType, shift, presentChildren)))
      }).getOrElse(BadRequest("Dagdeel niet gevonden"))
  }

  def allShifts: Action[AnyContent] = DBAction { implicit req =>
    Ok(Json.toJson(shiftRepository.findAllWithTypeAndNumberOfPresences(req.dbSession)))
  }

  def delete(id: Long): Action[AnyContent] = DBAction { implicit req =>
    if (shiftRepository.delete(id)(req.dbSession) > 0) Ok else NotFound
  }
}
