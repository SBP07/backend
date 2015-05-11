package controllers

import models.json.AnimatorJson.animatorWrites
import models.repository.{AnimatorRepository}
import play.api.db.slick.DBAction
import play.api.libs.json.{JsValue, Json}
import play.api.mvc._

class ApiAnimators(animatorRepo: AnimatorRepository) extends Controller {
  def allAnimators = DBAction { implicit req =>
    Ok(Json.toJson(animatorRepo.findAll(req.dbSession)))
  }

  def animatorById(id: Long) = DBAction { implicit req =>
    animatorRepo.findById(id)(req.dbSession).fold(BadRequest("geen animator met die id")) { animator =>
      Ok(Json.toJson(animator))
    }
  }
}
