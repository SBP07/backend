package controllers

import models.formBindings.PresencesPost
import org.joda.time.{DateTimeZone, LocalDate}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.db.slick._
import models.{Children => ChildrenModel, Activities => ActivitiesModel, _}
import play.api.Play.current
import views.html.presences

object Presences extends Controller {
  val registerForm = Form(
    mapping(
      "childId" -> of[Long],
      "selectedActivityIds" -> list(of[Long]),
      "possibleActivityIds" -> list(of[Long])
    ) {
      (childId, selectedActivityIds, possibleActivityIds) => {
        DB.withSession(s => {
          val child = ChildrenModel.findById(childId)(s)
          val selectedActivities = ActivitiesModel.findByIds(selectedActivityIds)(s).toList
          val possibleActivities = ActivitiesModel.findByIds(possibleActivityIds)(s).toList
          PresencesPost(child, selectedActivities, possibleActivities)
        })
      }
    } {
      p: PresencesPost => {
        val id = p.child.flatMap(_.id).getOrElse(-1L)
        val activityIds = p.selectedActivities.map(_.id).flatten
        val possibleActivityIds = p.possibleActivities.map(_.id).flatten
        Some((id, activityIds, possibleActivityIds))
      }
    }
  )

  def updateWithId(childId: Long): Action[AnyContent] = DBAction { implicit s =>
    val child = ChildrenModel.findById(childId)

    val allPresences = (for {
      child <- ChildrenModel.findById(childId)
      id <- child.id
    } yield ChildPresences.findAllForChild(id).map(_._1).toList).getOrElse(Nil)

    child match {
      case Some(c) =>
        val allActivities = ActivitiesModel.findAllWithType.toList
        val filledForm = registerForm.fill(PresencesPost(child, allPresences, allActivities.map(_._2)))
        Ok(presences.updatePresencesForm.render(filledForm, c, LocalDate.now, allActivities, s.flash))
      case _ => BadRequest("Kind niet gevonden")
    }
  }

  def saveUpdate: Action[AnyContent] = DBAction { implicit rs =>
    registerForm.bindFromRequest.fold(
      formWithErrors => {
        formWithErrors.value.flatMap(_.child).flatMap(_.id.flatMap(ChildrenModel.findById)) match {
          case Some(child) =>
            val allActivities = ActivitiesModel.findAllWithType.toList
            BadRequest(
              presences.updatePresencesForm.render(formWithErrors, child, LocalDate.now, allActivities, rs.flash)
            )
          case _ => BadRequest("Kind niet gevonden")
        }
      },
      _ match {
        case PresencesPost(None, _, _) => BadRequest("Ongeldig kind");
        case PresencesPost(Some(child), selectedActivities, possibleActivities) => {
          child.id match {
            case Some(id) => rs.dbSession.withTransaction {
              val alreadyPersisted = ChildPresences.findAllForChild(id).map(_._1).toList

              val toPersist = PresencesPost.presencesToInsert(selectedActivities, possibleActivities, alreadyPersisted)
              val toDelete = PresencesPost.presencesToDelete(selectedActivities, possibleActivities, alreadyPersisted)

              ChildPresences.register(toPersist.map(_.id).flatten.map(ChildPresence(id, _)))
              ChildPresences.unregister(toDelete.map(_.id).flatten.map(ChildPresence(id, _)))

              Redirect(routes.Children.details(id)).flashing("success" -> "Aanwezigheden upgedated")
            }

            case _ => BadRequest("Ongeldig kind")
          }

        }
      }

    )
  }

  def register: Action[AnyContent] = DBAction { implicit s =>
    val allActivities = ActivitiesModel.findAllWithType.toList
    val filledForm = registerForm.fill(PresencesPost(None, Nil, allActivities.map(_._2)))
    Ok(presences.register.render(filledForm, ChildrenModel.findAll, LocalDate.now, allActivities, s.flash))
  }

  def registerWithId(childId: Long): Action[AnyContent] = DBAction { implicit s =>
    val child = ChildrenModel.findById(childId)
    val allPresences = (for {
      c <- child
      id <- c.id
    } yield ChildPresences.findAllForChild(id).map(_._1).toList).getOrElse(Nil)


    val allActivities = ActivitiesModel.findAllWithType.toList
    val filledForm = registerForm.fill(PresencesPost(child, allPresences, allActivities.map(_._2)))

    Ok(
      presences.register.render(filledForm,
        ChildrenModel.findAll, LocalDate.now, allActivities, s.flash)
    )
  }

  def savePresence: Action[AnyContent] = DBAction { implicit rs =>
    registerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(presences.register.render(formWithErrors, ChildrenModel.findAll, LocalDate.now,
          ActivitiesModel.findAllWithType.toList, rs.flash)),
      _ match {
        case PresencesPost(None, _, _) => BadRequest("Ongeldig kind");
        case PresencesPost(Some(child), selectedActivities, possibleActivities) =>
          child.id match {
            case Some(id) => rs.dbSession.withTransaction {
              val alreadyPersisted = ChildPresences.findAllForChild(id).map(_._1).toList
              val toPersist = PresencesPost.presencesToInsert(selectedActivities, possibleActivities, alreadyPersisted)

              ChildPresences.register(toPersist.map(_.id).flatten.map(ChildPresence(id, _)))

              Redirect(routes.Children.details(id)).flashing("success" -> s"${toPersist.size} aanwezigheden toegevoegd")
            }

            case _ => BadRequest("Ongeldig kind")
          }
      }

    )
  }

  def presentToday: Action[AnyContent] = TODO
}
