package controllers

import play.Logger
import play.api.mvc._

class Main extends Controller {
  def home: Action[AnyContent] = Action { implicit req =>
    Ok(views.html.main.render)
  }

  def any(path: String): Action[AnyContent] = Action { implicit req =>
    Logger.info(s"Requesting Angular HTML5 path /$path")
    Ok(views.html.main.render)
  }

  def heartbeat: Action[AnyContent] = Action {
    Ok("online")
  }
}
