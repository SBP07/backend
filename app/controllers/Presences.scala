package controllers

import models.formBindings.PresencesPost
import org.joda.time.{DateTimeZone, LocalDate}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.db.slick._
import models._
import models.repositories.slick.{ChildPresenceRepository, ChildRepository, ShiftRepository}
import play.api.Play.current
import views.html.presences

object Presences extends Controller {
  val registerForm = Form(
    mapping(
      "childId" -> of[Long],
      "selectedShiftIds" -> list(longNumber),
      "possibleShiftIds" -> list(longNumber)
    ) {
      (childId, selectedShiftIds, possibleShiftIds) => {
        DB.withSession(s => {
          val child = ChildRepository.findById(childId)(s)
          val selectedShifts = ShiftRepository.findByIds(selectedShiftIds)(s).toList
          val possibleShifts = ShiftRepository.findByIds(possibleShiftIds)(s).toList
          PresencesPost(child, selectedShifts, possibleShifts)
        })
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

  def updateWithId(childId: Long): Action[AnyContent] = DBAction { implicit req =>
    val child = ChildRepository.findById(childId)

    val allPresences = (for {
      child <- ChildRepository.findById(childId)
      id <- child.id
    } yield ChildPresenceRepository.findAllForChild(id).map(_._1).toList).getOrElse(Nil)

    child match {
      case Some(c) =>
        val allShifts = ShiftRepository.findAllWithType.toList
        val filledForm = registerForm.fill(PresencesPost(child, allPresences, allShifts.map(_._2)))
        Ok(presences.updatePresencesForm.render(filledForm, c, LocalDate.now, allShifts, req.flash))
      case _ => BadRequest("Kind niet gevonden")
    }
  }

  def saveUpdate: Action[AnyContent] = DBAction { implicit req =>
    registerForm.bindFromRequest.fold(
      formWithErrors => {
        formWithErrors.value.flatMap(_.child).flatMap(_.id.flatMap(ChildRepository.findById)) match {
          case Some(child) =>
            val allShifts = ShiftRepository.findAllWithType.toList
            BadRequest(
              presences.updatePresencesForm.render(formWithErrors, child, LocalDate.now, allShifts, req.flash)
            )
          case _ => BadRequest("Kind niet gevonden")
        }
      },
      _ match {
        case PresencesPost(None, _, _) => BadRequest("Ongeldig kind");
        case PresencesPost(Some(child), selected, possible) => {
          child.id match {
            case Some(id) => req.dbSession.withTransaction {
              val alreadyPersisted = ChildPresenceRepository.findAllForChild(id).map(_._1).toList

              val toPersist = PresencesPost.presencesToInsert(selected, possible, alreadyPersisted)
              val toDelete = PresencesPost.presencesToDelete(selected, possible, alreadyPersisted)

              ChildPresenceRepository.register(toPersist.map(_.id).flatten.map(ChildPresence(id, _)))
              ChildPresenceRepository.unregister(toDelete.map(_.id).flatten.map(ChildPresence(id, _)))

              Redirect(routes.Children.details(id)).flashing("success" -> "Aanwezigheden upgedated")
            }

            case _ => BadRequest("Ongeldig kind")
          }

        }
      }

    )
  }

  def register: Action[AnyContent] = DBAction { implicit req =>
    val allShifts = ShiftRepository.findAllWithTypeToday(LocalDate.now).toList
    val filledForm = registerForm.fill(PresencesPost(None, Nil, allShifts.map(_._2)))
    Ok(presences.register.render(filledForm, ChildRepository.findAll, LocalDate.now, allShifts, req.flash))
  }

  def registerWithId(childId: Long): Action[AnyContent] = DBAction { implicit req =>
    val child = ChildRepository.findById(childId)
    val allPresences = (for {
      c <- child
      id <- c.id
    } yield ChildPresenceRepository.findAllForChild(id).map(_._1).toList).getOrElse(Nil)


    val allShifts = ShiftRepository.findAllWithType.toList
    val filledForm = registerForm.fill(PresencesPost(child, allPresences, allShifts.map(_._2)))

    Ok(
      presences.register.render(filledForm,
        ChildRepository.findAll, LocalDate.now, allShifts, req.flash)
    )
  }

  def savePresence: Action[AnyContent] = DBAction { implicit req =>
    registerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(presences.register.render(formWithErrors, ChildRepository.findAll, LocalDate.now,
          ShiftRepository.findAllWithType.toList, req.flash)),
      _ match {
        case PresencesPost(None, _, _) => BadRequest("Ongeldig kind");
        case PresencesPost(Some(child), selectedShifts, possibleShifts) =>
          child.id match {
            case Some(id) => req.dbSession.withTransaction {
              val alreadyPersisted = ChildPresenceRepository.findAllForChild(id).map(_._1).toList
              val toPersist = PresencesPost.presencesToInsert(selectedShifts, possibleShifts, alreadyPersisted)

              ChildPresenceRepository.register(toPersist.map(_.id).flatten.map(ChildPresence(id, _)))

              Redirect(routes.Children.details(id)).flashing("success" -> s"${toPersist.size} aanwezigheden toegevoegd")
            }

            case _ => BadRequest("Ongeldig kind")
          }
      }

    )
  }

  def presentToday: Action[AnyContent] = TODO
}