package controllers

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.data.slick.{SlickChildPresenceRepository, SlickChildRepository, SlickShiftRepository}
import be.thomastoye.speelsysteem.legacy.models.{Child, ChildPresence, Shift}
import org.joda.time.{DateTimeZone, LocalDate}
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import models._
import models.formBindings.PresencesPost
import views.html.presences

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class PresencesController @Inject() (childRepository: SlickChildRepository, shiftRepository: SlickShiftRepository,
  childPresenceRepository: SlickChildPresenceRepository) extends Controller
{
  val registerForm: Form[PresencesPost] = Form(
    mapping(
      "childId" -> of[Long],
      "selectedShiftIds" -> list(longNumber),
      "possibleShiftIds" -> list(longNumber)
    ) {
      (childId, selectedShiftIds, possibleShiftIds) => {
        Await.result(for {
          child <- childRepository.findById(childId)
          selectedShifts <- shiftRepository.findByIds(selectedShiftIds)
          possibleShifts <- shiftRepository.findByIds(possibleShiftIds)
        } yield PresencesPost(child, selectedShifts.toList, possibleShifts.toList), 3.seconds)
      }
    } {
      p: PresencesPost => {
        val id = p.child.flatMap(_.id).getOrElse(-1L)
        val shiftIds = p.selectedShifts.map(_.id).flatten
        val possibleShiftIds = p.possibleShifts.map(_.id).flatten
        Some((id, shiftIds, possibleShiftIds))
      }
    }
  )

  def updateWithId(childId: Long): Action[AnyContent] = Action.async { implicit req =>
    childRepository.findById(childId) flatMap { childOpt =>
      childOpt map { child =>
        child.id map { childId =>
          childPresenceRepository.findAllForChild(childId) map (_.map(_._1).toList) flatMap { allPresences =>
            shiftRepository.findAllWithType map (_.toList) map { allShifts =>
              val filledForm = registerForm.fill(PresencesPost(Some(child), allPresences, allShifts.map(_._2)))
              Ok(presences.updatePresencesForm.render(filledForm, child, LocalDate.now, allShifts, req.flash))
            }
          }
        } getOrElse Future.successful(BadRequest("Kind niet gevonden"))
      } getOrElse Future.successful(BadRequest("Kind niet gevonden"))
    }
  }

  def saveUpdate: Action[AnyContent] = Action.async { implicit req =>
    registerForm.bindFromRequest.fold(
      formWithErrors => {
        formWithErrors.value.flatMap(_.child).flatMap(_.id) map { childId =>
          childRepository.findById(childId) flatMap {
            case Some(child) =>
              shiftRepository.findAllWithType.map(_.toList) map { allShifts =>
                BadRequest(
                  presences.updatePresencesForm.render(formWithErrors, child, LocalDate.now, allShifts, req.flash)
                )
              }
            case None => Future.successful(BadRequest("Kind niet gevonden"))
          }
        } getOrElse Future.successful(BadRequest("Ongeldig kind (geen id gevonden)"))
      }, {
        case PresencesPost(None, _, _) => Future.successful(BadRequest("Ongeldig kind"))
        case PresencesPost(Some(child), selected, possible) => {
          child.id match {
            case Some(id) => /*req.dbSession.withTransaction*/
              childPresenceRepository.findAllForChild(id).map(_.map(_._1).toList) flatMap { alreadyPersisted =>
                val toPersist = PresencesPost.presencesToInsert(selected, possible, alreadyPersisted)
                val toDelete = PresencesPost.presencesToDelete(selected, possible, alreadyPersisted)

                Future.sequence(List(
                  childPresenceRepository.register(toPersist.map(_.id).flatten.map(ChildPresence(id, _))),
                  childPresenceRepository.unregister(toDelete.map(_.id).flatten.map(ChildPresence(id, _)))
                )) map { _ =>
                  Redirect(routes.ChildController.details(id)).flashing("success" -> "Aanwezigheden upgedated")
                }
              }
            case _ => Future.successful(BadRequest("Ongeldig kind"))
          }

        }
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

  def registerWithId(childId: Long): Action[AnyContent] = Action.async { implicit req =>
    def finalStep(childOpt: Option[Child], selectedShifts: List[Shift]): Future[Result] = {
      shiftRepository.findAllWithType map(_.toList) flatMap { allShifts =>
        val filledForm = registerForm.fill(PresencesPost(childOpt, selectedShifts, allShifts.map(_._2)))
        childRepository.findAll map { allChildren =>
          Ok(presences.register.render(filledForm, allChildren.toList, LocalDate.now, allShifts, req.flash))
        }
      }
    }

    childRepository.findById(childId) flatMap { child =>
      child.flatMap(_.id) match {
        case Some(test) =>
          childPresenceRepository.findAllForChild(test) flatMap { seq =>
            val selectedShifts = seq.map(_._1).toList
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
        child.id match {
          case Some(id) =>
            childPresenceRepository.findAllForChild(id) map (_.map(_._1).toList) flatMap { alreadyPersisted =>
              val toPersist = PresencesPost.presencesToInsert(selectedShifts, possibleShifts, alreadyPersisted)
              childPresenceRepository.register(toPersist.flatMap(_.id).map(ChildPresence(id, _))) map { _ =>
                Redirect(routes.ChildController.details(id)).flashing("success" -> s"${toPersist.size} aanwezigheden toegevoegd")
              }
            }
          case _ => Future.successful(BadRequest("Ongeldig kind"))
        }
    })
  }

  def presentToday: Action[AnyContent] = TODO
}
