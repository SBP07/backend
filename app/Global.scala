import play.api._
import play.api.mvc.{Result, RequestHeader}
import play.api.mvc.Results.BadRequest
import play.api.libs.json.Json

import scala.concurrent.Future


object Global extends GlobalSettings {
  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = {
    Future.successful(BadRequest(Json.obj("status" -> "Bad Request", "error" -> error)))
  }
}
