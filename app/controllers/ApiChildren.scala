package controllers

import models.Child
import models.json.ChildJson._
import models.repository.ChildRepository
import play.api.db.slick.DBAction
import play.api.libs.json._
import play.api.mvc._

class ApiChildren(childRepository: ChildRepository) extends Controller {

  def allChildren = DBAction { implicit req =>
    val json = Json.toJson(childRepository.findAll(req.dbSession))
    Ok(json)
  }

  def childById(id: Long) = DBAction { implicit req =>
    childRepository.findById(id)(req.dbSession).fold(BadRequest("Id not found")) { child =>
      Ok(Json.toJson(child))
    }
  }

  def update(id: Long) = DBAction(parse.json) { implicit req =>
    val childResult = req.body.validate[Child]
    childResult.fold(
      errors => {
        BadRequest(Json.obj("status" -> "KO", "message" -> JsError.toFlatJson(errors)))
      },
      child => {
        childRepository.update(child)(req.dbSession)
        Ok(Json.obj("status" -> "OK", "message" -> ("Child '" + child.firstName + "' saved.")))
      }
    )
  }
}
