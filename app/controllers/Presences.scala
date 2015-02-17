package controllers

import models.formBindings.PresencesPost
import org.joda.time.{DateTimeZone, LocalDate}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.db.slick._
import models.{Children => ChildrenModel, Activities => ActivitiesModel, ChildPresences, Activity, Child, ChildPresence}
import play.api.Play.current

object Presences extends Controller {
  val registerForm = Form(
    mapping(
      "childId" -> of[Long],
      "selectedActivityIds" -> list(of[Long]),
      "possibleActivityIds" -> list(of[Long])
    ) {
      (childId, selectedActivityIds, possibleActivityIds) => {
        DB.withSession(s => {
          val child = (ChildrenModel.findById(childId)(s))
          val selectedActivities = ActivitiesModel.findByIds(selectedActivityIds)(s).toList
          val possibleActivities = ActivitiesModel.findByIds(possibleActivityIds)(s).toList
          PresencesPost(child, selectedActivities, possibleActivities)
        })
      }
    } {
      p: PresencesPost => Some((p.child.flatMap(_.id).getOrElse(-1L), p.selectedActivities.map(_.id).flatten, p.possibleActivities.map(_.id).flatten))
    }
  )

  def register = DBAction { implicit s =>
    Ok(views.html.presences.register.render(registerForm, ChildrenModel.findAll, LocalDate.now, ActivitiesModel.findAllWithType.toList, s.flash))
  }

  def registerWithId(childId: Long) = DBAction { implicit s =>
    val child = ChildrenModel.findById(childId)
    val presences = child.flatMap(_.id).map(ChildPresences.findAllForChild(_)).map(_.map(_._1)).map(_.toList).getOrElse(Nil)
    Ok(
      views.html.presences.register.render(registerForm.fill(PresencesPost(child, presences, ActivitiesModel.findAllWithType.toList.map(_._2))),
        ChildrenModel.findAll, LocalDate.now, ActivitiesModel.findAllWithType.toList, s.flash)
    )
  }

  def savePresence = DBAction { implicit rs =>
    registerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.presences.register.render(formWithErrors, ChildrenModel.findAll, LocalDate.now, ActivitiesModel.findAllWithType.toList, rs.flash)),
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

              Redirect(routes.Children.details(id)).flashing("success" -> "Aanwezigheden toegevoegd")
            }

            case _ => BadRequest("Ongeldig kind")
          }

        }
      }

    )
  }

  def presentToday = TODO
}