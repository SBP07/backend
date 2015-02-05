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
import models.{Animators => AnimatorsModel}

object Animators extends Controller {
  def list = DBAction { implicit rs => Ok(html.animator_list.render(AnimatorsModel.findAll, rs.flash))}

  def details(id: Long) = DBAction { implicit rs =>
    val child = AnimatorsModel.findById(id)
    child match {
      case Some(x) => Ok(html.animator_details(x))
      case None => BadRequest("Geen animator met die ID")
    }
  }
}
