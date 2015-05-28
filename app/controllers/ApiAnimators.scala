package controllers

import helpers.JsonHelpers
import models.json.AnimatorJson.animatorWrites
import models.repository.AnimatorRepository
import play.api.db.slick.DBAction
import play.api.libs.json.{JsString, Json}
import play.api.mvc._
import javax.inject._

class ApiAnimators @Inject() (animatorRepo: AnimatorRepository) extends Controller {
  def allAnimators: Action[AnyContent] = DBAction { implicit req =>
    Ok(Json.toJson(animatorRepo.findAll(req.dbSession)))
  }

  def animatorById(id: Long): Action[AnyContent] = DBAction { implicit req =>
    animatorRepo.findById(id)(req.dbSession).fold(
      NotFound(JsonHelpers.notFound(JsString(s"No item found with id '$id'.")))
    ) { animator =>
      Ok(Json.toJson(animator))
    }
  }
}
