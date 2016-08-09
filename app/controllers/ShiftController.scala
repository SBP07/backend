package controllers

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.data.{ChildPresenceRepository, ShiftRepository, ShiftTypeRepository}
import be.thomastoye.speelsysteem.legacy.models.LegacyShift
import org.joda.time.LocalDate
import play.api.mvc._
import play.api.data.Forms._
import play.api.data._
import play.api.libs.concurrent.Execution.Implicits._
import helpers.DateTime._

import scala.concurrent.Future

object LegacyShiftController {
  case class ShiftsPost(date: LocalDate, shiftTypes: List[Long], externalLocation: String)
  case class ShiftDelete(id: Long)
}

class LegacyShiftController @Inject() (shiftRepository: ShiftRepository, shiftTypeRepository: ShiftTypeRepository,
  childPresenceRepository: ChildPresenceRepository) extends Controller
{
  import LegacyShiftController._

  val deleteForm = Form(
      mapping(
      "id" -> longNumber
    )(ShiftDelete.apply)(ShiftDelete.unapply)
  )

  val shiftsForm = Form(
    mapping(
      "date" -> jodaLocalDate("dd-MM-yyyy"),
      "selectedShiftTypes" -> Forms.list(longNumber),
      "externalLocation" -> text
    )(ShiftsPost.apply)(ShiftsPost.unapply)
  )

  def list: Action[AnyContent] = Action.async { implicit req =>
    shiftRepository.findAllWithTypeAndNumberOfPresences map { all =>
      Ok(views.html.legacyShifts.list.render(all, req.flash))
    }
  }

  def newShift: Action[AnyContent] = Action.async { implicit req =>
    shiftTypeRepository.findAll map { types => Ok(views.html.legacyShifts.form.render(shiftsForm, types, req.flash)) }
  }

  // TODO replace method body with for comprehension
  def saveShift: Action[AnyContent] = Action.async { implicit req =>
    shiftsForm.bindFromRequest.fold(
      formWithErrors => {
        shiftTypeRepository.findAll.map(all => BadRequest(views.html.legacyShifts.form.render(formWithErrors, all, req.flash)))
      },
      post => {

        Future.sequence(post.shiftTypes.map(shiftTypeRepository.findById)).map(_.flatten.toSet) flatMap { shiftTypes =>

          val alreadyPersistedFut: Future[Set[Long]] = Future.sequence(shiftTypes.map { t =>
            shiftRepository.findByDateAndType(post.date, t)
          }).map(_.flatten.map(_.shiftId))

          alreadyPersistedFut.flatMap { alreadyPersisted =>
            val notPersistedYet = shiftTypes.filterNot(_.id.map(a =>
              alreadyPersisted.contains(a)
            ).get)

            shiftTypeRepository.findByMnemonic("EXT") map(_.flatMap(_.id).getOrElse(-1)) flatMap { externalActivityId =>
              Future.sequence(notPersistedYet.map(_.id).flatten map { id =>
                val place = if (id == externalActivityId) post.externalLocation else "Speelplein"
                shiftRepository insert LegacyShift(None, post.date, place, id)
              }) map { _ =>
                Redirect(routes.LegacyShiftController.list).flashing("success" -> s"${notPersistedYet.size} dagdelen toegevoegd")
              }
            }
          }
        }
      }
    )
  }

  def updateShift(dateString: String): Action[AnyContent] = Action.async { implicit req =>
    try{
      val date: LocalDate = LocalDate.parse(dateString, fmt)
      val extPlace = "Test"
      shiftRepository.findByDate(date) flatMap { shift =>
        val fill = ShiftsPost(date, shift.map(_.shiftId).toList, extPlace)
        shiftTypeRepository.findAll map { types =>
          Ok(views.html.legacyShifts.form.render(shiftsForm.fill(fill), types, req.flash))
        }
      }
    } catch {
      case e: IllegalArgumentException => Future.successful(BadRequest("Could not parse date"))
    }
  }

  def deleteShift(id: Long): Action[AnyContent] = Action.async { implicit req =>
    shiftRepository.findByIdWithTypeAndNumberOfPresences(id) map { found =>
      found.map { found =>
        Ok(views.html.legacyShifts.confirm_delete(found._3, found._1, found._2))
      }.getOrElse(BadRequest("Dagdeel niet gevonden"))
    }
  }

  def reallyDeleteShift(): Action[AnyContent] = Action.async { implicit req =>
    deleteForm.bindFromRequest.fold(
      errorForm => Future.successful(BadRequest("Bad id")),
      deleteShift => {
        shiftRepository.findById(deleteShift.id) flatMap { shift =>
          shift.map { act =>
            shiftRepository.delete(act) map { _ =>
              Redirect(routes.LegacyShiftController.list()).flashing("success" -> "Dagdeel verwijderd")
            }
          }.getOrElse(Future.successful(BadRequest("Dagdeel niet gevonden")))
        }
      }
    )
  }

  def detailsShifts(id: Long): Action[AnyContent] = Action.async { implicit req =>
    shiftRepository.findByIdWithTypeAndNumberOfPresences(id) map (_.map(_._1)) flatMap {
      _.map { shift =>
        shiftRepository.findByIdWithTypeAndNumberOfPresences(id) map (_.map(_._2)) flatMap { shiftTypeOpt =>
          shiftTypeOpt map { shiftType =>
            shift.id map { shiftId =>
              childPresenceRepository.findAllForShift(shiftId) map (_.map(_._1)) map { presentChildren =>
                Ok(views.html.legacyShifts.details.render(shift, shiftType, presentChildren, req.flash))
              }
            } getOrElse Future.successful(BadRequest("Dagdeel niet gevonden"))
          } getOrElse Future.successful(BadRequest("Dagdeel niet gevonden"))
        }
      }.getOrElse(Future.successful(BadRequest("Dagdeel niet gevonden")))
    }
  }
}
