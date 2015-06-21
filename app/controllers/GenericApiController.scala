package controllers

import models.dao.GenericDao
import play.api.libs.json.Writes
import play.api.mvc.{Action, AnyContent, Controller}
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

trait GenericApiController[M] extends Controller {
  val dao: GenericDao[M]
}

trait GenericApiControllerRetrieveSlice[M] extends GenericApiController[M] {
  implicit val jsonWrites: Writes[M]

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

trait GenericApiControllerJsonReads[M] extends GenericApiController[M] {
  implicit val jsonReads: Reads[M]
}

trait GenericApiControllerCreateSlice[M] extends GenericApiControllerJsonReads[M] {
  def create: Action[M] = Action.async(parse.json(jsonReads)) { implicit req =>
    dao.insert(req.body).map(numCreated => if (numCreated == 0) BadRequest else Created)
  }
}

trait GenericApiControllerUpdateSlice[M] extends GenericApiControllerJsonReads[M] {
  def update: Action[M] = Action.async(parse.json(jsonReads)) { implicit req =>
    dao.update(req.body).map(numCreated => if (numCreated == 0) BadRequest else Ok)
  }
}

trait GenericApiControllerDeleteSlice[M] extends  GenericApiController[M] {
  def delete(id: Long): Action[AnyContent] = TODO
}

trait GenericApiControllerFullCrud[M]
  extends GenericApiController[M]
  with GenericApiControllerRetrieveSlice[M]
  with GenericApiControllerCreateSlice[M]
  with GenericApiControllerUpdateSlice[M]
  with GenericApiControllerDeleteSlice[M]
