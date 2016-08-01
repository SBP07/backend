package controllers

import play.api.mvc._

class ApplicationController extends Controller {
  def home: Action[AnyContent] = Action { implicit req =>
    Ok(views.html.application.home.render(req.flash))
  }

  def heartbeat: Action[AnyContent] = Action { Ok("online") }
}
