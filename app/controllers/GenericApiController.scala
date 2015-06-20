package controllers

import models.dao.GenericDao
import play.api.libs.json.Writes
import play.api.mvc.{Action, AnyContent, Controller}
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

trait GenericApiController[M] extends Controller {
  val dao: GenericDao[M]
  implicit val jsonWrites: Writes[M]
//  implicit val jsonReads: Reads[M]

  def all: Action[AnyContent] = Action.async {
    dao.findAll.map { all =>
      Ok(Json.toJson(all))
    }
  }

  def byId(id: Long): Action[AnyContent] = Action.async {
    dao.findById(id).map(_.fold {
      NotFound("")
    }{ entity =>
      Ok(Json.toJson(entity))
    })
  }

}
