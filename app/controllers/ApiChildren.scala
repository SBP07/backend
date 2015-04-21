package controllers

import models.Child.childWrites
import models.repository.ChildRepository
import play.api.db.slick.DBAction
import play.api.libs.json._
import play.api.mvc._

object ApiChildren extends Controller {

  def allChildren = DBAction { implicit req =>
    val json = Json.toJson(ChildRepository.findAll(req.dbSession))
    Ok(json)
  }

  def childById(id: Long) = DBAction { implicit req =>
    ChildRepository.findById(id)(req.dbSession).map { child =>
      Ok(Json.toJson(child))
    }.getOrElse(BadRequest("Id not found"))
  }
}
