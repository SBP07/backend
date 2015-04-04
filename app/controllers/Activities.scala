package controllers

import models.{Activities => ActivitiesModel, Activity, ActivityTypes}
import org.joda.time.LocalDate
import play.api.mvc._
import play.api.db.slick._
import play.api.data.Forms._
import play.api.data._

object Activities extends Controller {
  case class ActivitiesPost(date: LocalDate, activityTypes: List[Long], externalLocation: String)

  val activitiesPost = Form(
    mapping(
      "date" -> jodaLocalDate("dd-MM-yyyy"),
      "selectedActivityTypes" -> Forms.list(longNumber),
      "externalLocation" -> text
    )(ActivitiesPost.apply)(ActivitiesPost.unapply)
  )

  def list: Action[AnyContent] = DBAction { implicit s =>
    Ok(views.html.activities.list.render(ActivitiesModel.findAllWithType, s.flash))
  }

  def newActivity: Action[AnyContent] = DBAction { implicit s =>
    val types = ActivityTypes.findAll
    Ok(views.html.activities.form.render(activitiesPost, types, s.flash))
  }
  def saveActivity: Action[AnyContent] = DBAction { implicit s =>
    activitiesPost.bindFromRequest.fold(
      formWithErrors => {
        val types = ActivityTypes.findAll
        BadRequest(views.html.activities.form.render(formWithErrors, types, s.flash))
      },
      post => {
        val activityTypes = post.activityTypes.map(ActivityTypes.findById).flatten.toSet
        val alreadyPersisted: Set[Long] = activityTypes.map { t =>
          models.Activities.findByDateAndType(post.date, t)
        }.flatten.map(_.actNum)

        val notPersistedYet = activityTypes.filterNot(_.id.map(a =>
          alreadyPersisted.contains(a)
        ).get)

        val externalActivityId: Long = ActivityTypes.findByMnemonic("EXT").flatMap(_.id).getOrElse(-1)

        notPersistedYet.map(_.id).flatten foreach { id =>
          val place = if(id == externalActivityId) post.externalLocation else "Speelplein"
          models.Activities insert Activity(None, post.date, place, id)
        }

        Redirect(routes.Activities.list).flashing("success" -> s"${notPersistedYet.size} activiteiten toegevoegd")
      }
    )
  }
}
