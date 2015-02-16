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

  def showList = DBAction { implicit rs => Ok(html.child.list.render(ChildrenModel.findAll, rs.flash))}

  def newChild = Action { implicit rs => Ok(html.child.form.render(childForm, rs.flash))}

  def saveChild = DBAction { implicit rs =>
    childForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.child.form.render(formWithErrors, rs.flash)),
      child => {
        child.id match {
          case Some(id) => {
            ChildrenModel.update(child)
            Redirect(routes.Children.details(id)).flashing("success" -> "Kind upgedated")
          }
          case _ => {
            ChildrenModel.insert(child)
            Redirect(routes.Children.showList).flashing("success" -> "Kind toegevoegd")
          }
        }
      }
    )

  }

  def editChild(id: Long) = DBAction { implicit rs =>
    val child = ChildrenModel.findById(id)
    child match {
      case Some(ch) => Ok(html.child.form.render(childForm.fill(ch), rs.flash))
      case _ => BadRequest("Geen geldige id")
    }
  }

  def details(id: Long) = DBAction { implicit rs =>
    val child = ChildrenModel.findById(id)
    child match {
      case Some(x) => Ok(html.child.details(x, ChildPresences.findAllForChild(id).toList))
      case None => BadRequest("Geen kind met die ID")
    }
  }
}