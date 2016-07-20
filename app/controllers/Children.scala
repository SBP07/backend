package controllers

import models._
import models.repositories.slick.{ChildPresenceRepository, ChildRepository}
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formats._
import play.api.db.slick._
import play.api.mvc._
import views._

object Children extends Controller {

  val childForm = Form(
    mapping(
      "id" -> optional(of[Long]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "mobilePhone" -> optional(text),
      "landline" -> optional(text),

      "street" -> optional(text),
      "city" -> optional(text),

      "birthDate" -> optional(jodaLocalDate("dd-MM-yyyy")),
      "medicalRecordChecked" -> optional(jodaLocalDate("dd-MM-yyyy"))
    )((id: Option[Long], firstName: String, lastName: String, mobilePhone: Option[String], landline: Option[String],
       street: Option[String], city: Option[String], birthDate,
       medRecChecked) => Child.apply(id, firstName, lastName, mobilePhone, landline, street,
      city, birthDate, medRecChecked)
      )(_ match {
          case Child(id, firstName, lastName, mobilePhone, landline, street, city, birthDate, medRecChecked) =>
            Some((id, firstName, lastName, mobilePhone, landline, street, city, birthDate, medRecChecked))
          case _ => None
        }
      )
  )

  def showList: Action[AnyContent] = DBAction { implicit req => Ok(html.child.list.render(ChildRepository.findAll, req.flash))}

  def newChild: Action[AnyContent] = Action { implicit req => Ok(html.child.form.render(childForm, req.flash))}

  def saveChild: Action[AnyContent] = DBAction { implicit req =>
    childForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.child.form.render(formWithErrors, req.flash)),
      child => {
        child.id match {
          case Some(id) => {
            ChildRepository.update(child)
            Redirect(routes.Children.details(id)).flashing("success" -> "Kind upgedated")
          }
          case _ => {
            ChildRepository.insert(child)
            Redirect(routes.Children.showList).flashing("success" -> "Kind toegevoegd")
          }
        }
      }
    )

  }

  def editChild(id: Long): Action[AnyContent] = DBAction { implicit req =>
    val child = ChildRepository.findById(id)
    child match {
      case Some(ch) => Ok(html.child.form.render(childForm.fill(ch), req.flash))
      case _ => BadRequest("Geen geldige id")
    }
  }

  def details(id: Long): Action[AnyContent] = DBAction { implicit req =>
    val child = ChildRepository.findById(id)
    child match {
      case Some(x) => Ok(html.child.details(x, ChildPresenceRepository.findAllForChild(id).toList))
      case None => BadRequest("Geen kind met die ID")
    }
  }
}
