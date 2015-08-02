package controllers

import javax.inject.Inject

import models.{AnimatorPresence, ChildPresence}
import models.dao.{AnimatorDao, ChildDao, ShiftDao}
import models.json.AnimatorPresenceJson._
import models.json.ShiftJson._
import models.repository.{AnimatorPresenceRepository, ChildPresenceRepository}
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApiAnimatorAttendances @Inject()(animatorPresenceRepository: AnimatorPresenceRepository,
                                       animatorDao: AnimatorDao, shiftDao: ShiftDao)
  extends Controller
{
  def presencesForAnimator(animatorId: Long): Action[AnyContent] = Action.async {
    animatorPresenceRepository.findPresencesForAnimator(animatorId).map(shifts =>
      Ok(Json.toJson(shifts))
    )
  }

  def registerPresence: Action[AnimatorPresence] = Action.async(parse.json(animatorPresenceReads)) { implicit req =>

    animatorDao
      .findById(req.body.animatorId)
      .flatMap(_.fold(Future(BadRequest(s"No child found with id ${req.body.animatorId}"))) { child =>
      shiftDao.findById(req.body.shiftId).flatMap(_.fold(
          Future(BadRequest(s"No shift found with id ${req.body.shiftId}"))
        )(someInt => animatorPresenceRepository.register(req.body).map(_ => Ok))
      )
    })

  }

  def unregisterPresence: Action[AnimatorPresence] = Action.async(parse.json(animatorPresenceReads)) { implicit req =>

    animatorDao
      .findById(req.body.animatorId)
      .flatMap(_.fold(Future(BadRequest(s"No child found with id ${req.body.animatorId}"))) { child =>
      shiftDao.findById(req.body.shiftId).flatMap(_.fold(
          Future(BadRequest(s"No shift found with id ${req.body.shiftId}"))
        )(someInt => animatorPresenceRepository.unregister(req.body).map(_ => Ok))
      )
    })

  }
}
