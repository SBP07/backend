package controllers

import models.formBindings.PresencesPost
import org.joda.time.{DateTimeZone, LocalDate}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.db.slick._
import models.{Children => ChildrenModel, Activities => ActivitiesModel, ChildToActivity, ChildPresences, Activity, Child}
import play.api.Play.current

object Presences extends Controller {
  val registerForm = Form(
    mapping(
      "childId" -> of[Long],
      "activityIds" -> list(of[Long])
    ) {
      (childId, activityIds) => {
        DB.withSession(s => {
          val child = (ChildrenModel.findById(childId)(s))
          val activities = ActivitiesModel.findByIds(activityIds)(s).toList
          PresencesPost(child, activities)
        })
      }
    } {
      p: PresencesPost => Some((p.child.flatMap(_.id).getOrElse(-1L), p.activities.map(_.id).flatten))
    }
  )

  def register = DBAction { implicit s =>
    Ok(views.html.presences.register.render(registerForm, ChildrenModel.findAll, LocalDate.now, ActivitiesModel.findAllWithType.toList, s.flash))
  }

  def savePresence = DBAction { implicit rs =>
    registerForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.presences.register.render(formWithErrors, ChildrenModel.findAll, LocalDate.now, ActivitiesModel.findAllWithType.toList, rs.flash)),
      presences => {
        presences match {
          case PresencesPost(None, _) => BadRequest("Ongeldig kind");
          case PresencesPost(Some(child), activities) => {
            rs.dbSession.withTransaction {
              val childToActivities = activities.map(_.id).flatten.map(actId => ChildToActivity(child.id.get, actId))
              ChildPresences.register(childToActivities)
              Redirect(routes.Children.details(child.id.get)).flashing("success" -> "Aanwezigheden toegevoegd")
            }
          }
        }
      }
    )
  }

  def presentToday = TODO
}