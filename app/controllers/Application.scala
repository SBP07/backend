package controllers

import java.util.Date

import org.joda.time.{DateTimeZone, LocalDate}
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.db.slick._

import views._
import models._
import models.{Children => ChildrenModel}

object Application extends Controller {
  def home: Action[AnyContent] = Action { implicit req =>
    Ok(views.html.application.home.render(req.flash))
  }

  def heartbeat: Action[AnyContent] = Action { Ok("online") }
}
