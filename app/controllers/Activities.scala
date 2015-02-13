package controllers

import models.{Activities => ActivitiesModel}
import play.api.mvc._
import play.api.db.slick._

object Activities extends Controller {
  def list = DBAction { implicit s =>
    Ok(views.html.activities.list.render(ActivitiesModel.findAllWithType, s.flash))
  }

  def newActivity = TODO
}
