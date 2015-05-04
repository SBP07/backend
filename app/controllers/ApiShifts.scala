package controllers

import models.json.ShiftJson._
import models.repository.{ChildPresenceRepository, ShiftRepository}
import play.api.db.slick.DBAction
import play.api.libs.json._
import play.api.mvc._

object ApiShifts extends Controller {
  def shiftById(id: Long): Action[AnyContent] = DBAction { implicit req =>
    val presences = ShiftRepository.findByIdWithTypeAndNumberOfPresences(id)(req.dbSession)
    (for {
      shift <- presences.map(_._1)
      shiftType <- presences.map(_._2)
      shiftId <- shift.id
    } yield {
        val presentChildren = ChildPresenceRepository.findAllForShift(shiftId)(req.dbSession).map(_._1)
        Ok(Json.toJson((shiftType, shift, presentChildren)))
      }).getOrElse(BadRequest("Dagdeel niet gevonden"))
  }

  def allShifts = DBAction { implicit req =>
    Ok(Json.toJson(ShiftRepository.findAllWithTypeAndNumberOfPresences(req.dbSession)))
  }

  def delete(id: Long) = DBAction { implicit req =>
    ShiftRepository.delete(id)(req.dbSession)
    Ok
  }
}
