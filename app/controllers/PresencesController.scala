package controllers

import javax.inject.Inject

import be.thomastoye.speelsysteem.data.ChildRepository
import be.thomastoye.speelsysteem.legacy.data.{ChildPresenceRepository, ShiftRepository}
import be.thomastoye.speelsysteem.legacy.models.{ChildPresence, LegacyChild, LegacyShift}
import be.thomastoye.speelsysteem.models.Child
import org.joda.time.{DateTimeZone, LocalDate}
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models.formBindings.PresencesPost
import views.html.presences

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class PresencesController @Inject() (childRepository: ChildRepository, shiftRepository: ShiftRepository,
  childPresenceRepository: ChildPresenceRepository) extends Controller
{
  val registerForm: Form[PresencesPost] = Form(
    mapping(
      "childId" -> of[Child.Id],
      "selectedShiftIds" -> seq(longNumber),
      "possibleShiftIds" -> seq(longNumber)
    ) {
      (childId, selectedShiftIds, possibleShiftIds) => {
        Await.result(for {
          child <- childRepository.findById(childId)
          selectedShifts <- shiftRepository.findByIds(selectedShiftIds)
          possibleShifts <- shiftRepository.findByIds(possibleShiftIds)
        } yield PresencesPost(child, selectedShifts, possibleShifts), 3.seconds)
      }
    } {
      p: PresencesPost => {
        val id = p.child.map(_._1).get
        val shiftIds = p.selectedShifts.flatMap(_.id)
        val possibleShiftIds = p.possibleShifts.flatMap(_.id)
        Some((id, shiftIds, possibleShiftIds))
      }
    }
  )

  def updateWithId(childId: Child.Id): Action[AnyContent] = Action.async { implicit req =>
    childRepository.findById(childId) flatMap { childOpt =>
      childOpt map { case (_, child) =>
        childPresenceRepository.findAllForChild(childId) map (_.map(_._1).toList) flatMap { allPresences =>
          shiftRepository.findAllWithType map (_.toList) map { allShifts =>
            val filledForm = registerForm.fill(
              PresencesPost(Some((childId, child)), allPresences, allShifts.map(_._2))
            )
            Ok(presences.updatePresencesForm.render(filledForm, childId, child, LocalDate.now, allShifts, req.flash))
          }
        }
      } getOrElse Future.successful(BadRequest("Kind niet gevonden"))
    }
  }

  def saveUpdate: Action[AnyContent] = Action.async { implicit req =>
    registerForm.bindFromRequest.fold(
      formWithErrors => {
        (for {
          presencesPost <- formWithErrors.value
          presencesPostChild <- presencesPost.child
        } yield {
          shiftRepository.findAllWithType.map { allShifts =>
            BadRequest(presences.updatePresencesForm.render(formWithErrors, presencesPostChild._1, presencesPostChild._2, LocalDate.now, allShifts, req.flash) )
          }
        }) getOrElse Future.successful(BadRequest("Ongeldig kind (geen id gevonden)"))
      }, {
        case PresencesPost(None, _, _) => Future.successful(BadRequest("Ongeldig kind"))
        case PresencesPost(Some(child), selected, possible) =>
          val id = child._1
          childPresenceRepository.findAllForChild(id).map(_.map(_._1).toList) flatMap { alreadyPersisted =>
            val toPersist = PresencesPost.presencesToInsert(selected, possible, alreadyPersisted)
            val toDelete = PresencesPost.presencesToDelete(selected, possible, alreadyPersisted)

            Future.sequence(List(
              childPresenceRepository.register(toPersist.flatMap(_.id).map(ChildPresence(id, _))),
              childPresenceRepository.unregister(toDelete.flatMap(_.id).map(ChildPresence(id, _)))
            )) map { _ =>
              Redirect(routes.ChildController.details(id)).flashing("success" -> "Aanwezigheden upgedated")
            }
          }
        case _ => Future.successful(BadRequest("Ongeldig kind"))
      }
    )
  }

  def register: Action[AnyContent] = Action.async { implicit req =>
    shiftRepository.findAllWithTypeToday(LocalDate.now) map(_.toList) flatMap { allShifts =>
      childRepository.findAll.map { allChildren =>
        val filledForm = registerForm.fill(PresencesPost(None, Nil, allShifts.map(_._2)))
        Ok(presences.register.render(filledForm, allChildren.toList, LocalDate.now, allShifts, req.flash))
      }
    }
  }

  def registerWithId(childId: Child.Id): Action[AnyContent] = Action.async { implicit req =>
    def finalStep(childOpt: Option[(Child.Id, Child)], selectedShifts: Seq[LegacyShift]): Future[Result] = {
      shiftRepository.findAllWithType map(_.toList) flatMap { allShifts =>
        val filledForm = registerForm.fill(PresencesPost(childOpt, selectedShifts, allShifts.map(_._2)))
        childRepository.findAll map { allChildren =>
          Ok(presences.register.render(filledForm, allChildren.toList, LocalDate.now, allShifts, req.flash))
        }
      }
    }

    childRepository.findById(childId) flatMap { child =>
      child.map(_._1) match {
        case Some(test) =>
          childPresenceRepository.findAllForChild(test) flatMap { seq =>
            val selectedShifts = seq.map(_._1)
            finalStep(child, selectedShifts)

          }
        case _ => finalStep(child, Nil)
      }
    }
  }

  def savePresence: Action[AnyContent] = Action.async { implicit req =>
    registerForm.bindFromRequest.fold(formWithErrors => {
      for {
        allChildren <- childRepository.findAll
        allShifts <- shiftRepository.findAllWithType
      } yield {
        BadRequest(presences.register.render(formWithErrors, allChildren.toList, LocalDate.now, allShifts.toList, req.flash))
      }
    }, {
      case PresencesPost(None, _, _) => Future.successful(BadRequest("Ongeldig kind"))
      case PresencesPost(Some(child), selectedShifts, possibleShifts) =>
        val id = child._1
        childPresenceRepository.findAllForChild(id) map (_.map(_._1).toList) flatMap { alreadyPersisted =>
          val toPersist = PresencesPost.presencesToInsert(selectedShifts, possibleShifts, alreadyPersisted)
          childPresenceRepository.register(toPersist.flatMap(_.id).map(ChildPresence(id, _))) map { _ =>
            Redirect(routes.ChildController.details(id)).flashing("success" -> s"${toPersist.size} aanwezigheden toegevoegd")
          }
        }
    })
  }

  def presentToday: Action[AnyContent] = TODO
}
