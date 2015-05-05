package controllers

import play.api.mvc._
import play.api.db.slick.DBAction

object Angular extends Controller {
  def index = Action { implicit s =>
    Ok(views.html.angular.main.render)
  }
}
