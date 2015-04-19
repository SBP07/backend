package controllers

import models.ChildRepository
import models.Child.childWrites
import play.api.db.slick.DBAction
import play.api.libs.json._
import play.api.mvc._

object ApiChildren extends Controller {

  def allChildren = DBAction { implicit req =>
    val allChildren = ChildRepository.findAll(req.dbSession)
    val json = Json.toJson(allChildren)
    Ok(json)
  }

  def childById(id: Long) = DBAction { implicit req =>
    ChildRepository.findById(id)(req.dbSession).map { child =>
      val json = Json.toJson(child)
      Ok(json)
    }.getOrElse(BadRequest("Id not found"))
  }
}
