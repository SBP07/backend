package controllers

import javax.inject.Inject

import models.{VolunteerPresence, ChildPresence}
import models.dao.{VolunteerDao, ChildDao, ShiftDao}
import models.json.VolunteerPresenceJson._
import models.json.ShiftJson._
import models.repository.{VolunteerPresenceRepository, ChildPresenceRepository}
import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ApiVolunteerAttendances @Inject()(volunteerPresenceRepository: VolunteerPresenceRepository,
                                       volunteerDao: VolunteerDao, shiftDao: ShiftDao)
  extends Controller
{
  def presencesForVolunteer(volunteerId: Long): Action[AnyContent] = Action.async {
    volunteerPresenceRepository.findPresencesForVolunteer(volunteerId).map(shifts =>
      Ok(Json.toJson(shifts))
    )
  }

  def registerPresence: Action[VolunteerPresence] = Action.async(parse.json(volunteerPresenceReads)) { implicit req =>

    volunteerDao
      .findById(req.body.volunteerId)
      .flatMap(_.fold(Future(BadRequest(s"No child found with id ${req.body.volunteerId}"))) { child =>
      shiftDao.findById(req.body.shiftId).flatMap(_.fold(
          Future(BadRequest(s"No shift found with id ${req.body.shiftId}"))
        )(someInt => volunteerPresenceRepository.register(req.body).map(_ => Ok))
      )
    })

  }

  def unregisterPresence: Action[VolunteerPresence] = Action.async(parse.json(volunteerPresenceReads)) { implicit req =>

    volunteerDao
      .findById(req.body.volunteerId)
      .flatMap(_.fold(Future(BadRequest(s"No child found with id ${req.body.volunteerId}"))) { child =>
      shiftDao.findById(req.body.shiftId).flatMap(_.fold(
          Future(BadRequest(s"No shift found with id ${req.body.shiftId}"))
        )(someInt => volunteerPresenceRepository.unregister(req.body).map(_ => Ok))
      )
    })

  }
}
