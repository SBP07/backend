package controllers

import models.Shift.shiftWrites
import models.repository.ShiftRepository
import play.api.db.slick.DBAction
import play.api.libs.json._
import play.api.mvc._

object ApiShifts extends Controller {

  def allShifts = DBAction { implicit req =>
    val json = Json.toJson(ShiftRepository.findAll(req.dbSession))
    Ok(json)
  }

  def shiftById(id: Long) = DBAction { implicit req =>
    ShiftRepository.findById(id)(req.dbSession).map { shift =>
      Ok(Json.toJson(shift))
    }.getOrElse(BadRequest("Id not found"))
  }
}
