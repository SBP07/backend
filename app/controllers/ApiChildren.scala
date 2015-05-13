package controllers

import models.Child
import models.json.ChildJson._
import models.repository.ChildRepository
import play.api.db.slick.DBAction
import play.api.libs.json._
import play.api.mvc._
import helpers.JsonHelpers.{notFound, badRequest, success}

class ApiChildren(childRepository: ChildRepository) extends Controller {

  def allChildren: Action[AnyContent] = DBAction { implicit req =>
    val json = Json.toJson(childRepository.findAll(req.dbSession))
    Ok(json)
  }

  def childById(id: Long): Action[AnyContent] = DBAction { implicit req =>
    childRepository.findById(id)(req.dbSession).fold(
      NotFound(notFound(JsString(s"No child found with id '$id'.")))
    ) { child =>
      Ok(Json.toJson(child))
    }
  }

  def update(id: Long): Action[JsValue] = DBAction(parse.json) { implicit req =>
    val childResult = req.body.validate[Child]
    childResult.fold(
      errors => {
        BadRequest(badRequest(JsError.toFlatJson(errors)))
      },
      child => {
        childRepository.update(child)(req.dbSession)
        Ok(success(JsString("Child '${child.firstName} ${child.lastName}' updated.")))
      }
    )
  }
}
