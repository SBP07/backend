package controllers

import play.api.mvc._

class Application extends Controller {

  def index: Action[AnyContent] = Action {
    Ok("Application started.")
  }

}
