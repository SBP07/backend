package controllers

import models.json.ShiftJson._
import models.repository.ShiftRepository
import play.api.db.slick.DBAction
import play.api.libs.json._
import play.api.mvc._

object ApiShifts extends Controller {

  def shiftById(id: Long) = DBAction { implicit req =>
    ShiftRepository.findById(id)(req.dbSession).map { shift =>
      Ok(Json.toJson(shift))
    }.getOrElse(BadRequest("Id not found"))
  }

  def allShifts = DBAction { implicit req =>
    Ok(Json.toJson(ShiftRepository.findAllWithTypeAndNumberOfPresences(req.dbSession)))
  }
}
