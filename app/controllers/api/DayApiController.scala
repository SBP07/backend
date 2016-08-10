package controllers.api

import javax.inject.Inject

import be.thomastoye.speelsysteem.data.couchdb.CouchDayService
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import be.thomastoye.speelsysteem.models.JsonFormats.dayWithIdWrites

class DayApiController @Inject() (couchDayService: CouchDayService) extends Controller {

  def all = Action.async { req => couchDayService.findAll.map(days => Ok(Json.toJson(days))) }

}
