package controllers

import models.Animator.animatorWrites
import models.repository.AnimatorRepository
import play.api.mvc._
import play.api.db.slick.DBAction
import play.api.libs.json.Json

object ApiAnimators extends Controller {
  def allAnimators = DBAction { implicit req =>
    Ok(Json.toJson(AnimatorRepository.findAll(req.dbSession)))
  }

  def animatorById(id: Long) = DBAction { implicit req =>
    AnimatorRepository.findById(id)(req.dbSession).map { animator =>
      Ok(Json.toJson(animator))
    }.getOrElse(BadRequest("geen animator met die id"))
  }
}
