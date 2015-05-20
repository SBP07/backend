package controllers

import play.api.mvc._

class Main extends Controller {
  def home: Action[AnyContent] = Action { implicit req =>
    Ok(views.html.main.render)
  }

  def heartbeat: Action[AnyContent] = Action {
    Ok("online")
  }
}
