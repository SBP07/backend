package controllers

import javax.inject.Inject

import models.ChildPresence
import models.dao.{ChildDao, ShiftDao}
import models.json.ChildPresenceJson._
import models.json.ShiftJson._
import models.repository.ChildPresenceRepository
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApiAttendances @Inject()(childPresenceRepository: ChildPresenceRepository, childDao: ChildDao, shiftDao: ShiftDao) extends Controller {
  def presencesForChild(childId: Long): Action[AnyContent] = Action.async {
    childPresenceRepository.findPresencesForChild(childId).map(shifts =>
      Ok(Json.toJson(shifts))
    )
  }

  def registerPresence: Action[ChildPresence] = Action.async(parse.json(childPresenceReads)) { implicit req =>

    childDao.findById(req.body.childId)
      .flatMap(_.fold(Future(BadRequest(s"No child found with id ${req.body.childId}"))) { child =>
      shiftDao.findById(req.body.shiftId).flatMap(_.fold(
          Future(BadRequest(s"No shift found with id ${req.body.shiftId}"))
        )(someInt => childPresenceRepository.register(req.body).map(someInt => Ok))
      )
    })

  }
}
