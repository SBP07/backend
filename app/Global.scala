import com.softwaremill.macwire.Macwire
import models.repository._
import play.api._
import play.api.mvc.{Result, RequestHeader}
import play.api.mvc.Results.BadRequest
import play.api.libs.json.Json

import scala.concurrent.Future


object Global extends GlobalSettings with Macwire {

  val wired = wiredInModule(Application)
  override def getControllerInstance[A](controllerClass: Class[A]): A = wired.lookupSingleOrThrow(controllerClass)

  val childRepository = wire[ChildRepository]
  val animatorRepository = wire[SlickAnimatorRepository]
  val shiftRepository = wire[ShiftRepository]

  override def onBadRequest(request: RequestHeader, error: String): Future[Result] = {
    Future.successful(BadRequest(Json.obj("status" -> "Bad Request", "error" -> error)))
  }
}
