package controllers

import javax.inject.Inject

import be.thomastoye.speelsysteem.data.ChildRepository
import be.thomastoye.speelsysteem.legacy.data.ChildPresenceRepository
import be.thomastoye.speelsysteem.legacy.models.Child
import models._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formats._
import play.api.mvc._
import views._

import scala.concurrent.Future

class ChildController @Inject() (childRepository: ChildRepository, childPresenceRepository: ChildPresenceRepository) extends Controller {

  val childForm = Form(
    mapping(
      "id" -> optional(of[Long]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "mobilePhone" -> optional(text),
      "landline" -> optional(text),

      "street" -> optional(text),
      "streetNumber" -> optional(text),
      "zipCode" -> optional(number),
      "city" -> optional(text),

      "birthDate" -> optional(jodaLocalDate("dd-MM-yyyy"))
    )((id: Option[Long], firstName: String, lastName: String, mobilePhone: Option[String], landline: Option[String],
       street: Option[String], streetNumber: Option[String], zipCode: Option[Int], city: Option[String], birthDate) =>
      Child.apply(id, firstName, lastName, mobilePhone, landline, street, streetNumber, zipCode,
      city, birthDate)
    )(unapply = {
      case Child(id, firstName, lastName, mobilePhone, landline, street, streetNumber, zipCode, city, birthDate) =>
        Some((id, firstName, lastName, mobilePhone, landline, street, streetNumber, zipCode, city, birthDate))
      case _ => None
    })
  )

  def showList: Action[AnyContent] = Action.async { implicit req =>
    childRepository.findAll.map(all => Ok(html.child.list.render(all.toList, req.flash)))
  }

  def newChild: Action[AnyContent] = Action { implicit req => Ok(html.child.form.render(childForm, req.flash))}

  def saveChild: Action[AnyContent] = Action.async { implicit req =>
    childForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.child.form.render(formWithErrors, req.flash))),
      child => {
        child.id match {
          case Some(id) =>
            childRepository.update(child).map(_ => Redirect(routes.ChildController.details(id)).flashing("success" -> "Kind upgedated"))
          case _ =>
            childRepository.insert(child).map(_ => Redirect(routes.ChildController.showList()).flashing("success" -> "Kind toegevoegd"))
        }
      }
    )
  }

  def editChild(id: Long): Action[AnyContent] = Action.async { implicit req =>
    childRepository.findById(id) map {
      case Some(ch) => Ok(html.child.form.render(childForm.fill(ch), req.flash))
      case _ => BadRequest("Geen geldige id")
    }
  }

  def details(id: Long): Action[AnyContent] = Action.async { implicit req =>
    childRepository.findById(id) flatMap  {
      case Some(x) => childPresenceRepository.findAllForChild(id).map(all => Ok(html.child.details(x, all.toList)))
      case None => Future.successful(BadRequest("Geen kind met die ID"))
    }
  }
}
