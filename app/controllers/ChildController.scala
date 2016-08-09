package controllers

import javax.inject.Inject
import java.time.{LocalDate => JavaLocalDate}
import java.util.UUID

import be.thomastoye.speelsysteem.data.ChildRepository
import be.thomastoye.speelsysteem.legacy.models.LegacyChild
import be.thomastoye.speelsysteem.models.Child
import models._
import play.api.libs.concurrent.Execution.Implicits._
import play.api.data.Forms._
import play.api.data._
import play.api.data.format.Formats._
import play.api.mvc._
import views._
import org.joda.time.{LocalDate => JodaLocalDate}

import scala.concurrent.Future

class ChildController @Inject() (childRepository: ChildRepository) extends Controller {

  val childForm = Form(
    mapping(
      "id" -> optional(of[String]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "mobilePhone" -> optional(text),
      "landline" -> optional(text),

      "street" -> optional(text),
      "streetNumber" -> optional(text),
      "zipCode" -> optional(number),
      "city" -> optional(text),

      "birthDate" -> optional(jodaLocalDate("dd-MM-yyyy"))
    )((id: Option[String], firstName: String, lastName: String, mobilePhone: Option[String], landline: Option[String],
       street: Option[String], streetNumber: Option[String], zipCode: Option[Int], city: Option[String], birthDate) =>
      LegacyChild.apply(id, firstName, lastName, mobilePhone, landline, street, streetNumber, zipCode,
      city, birthDate.map(x => JavaLocalDate.of(x.getYear, x.getMonthOfYear, x.getDayOfMonth)))
    )(unapply = {
      case LegacyChild(id, firstName, lastName, mobilePhone, landline, street, streetNumber, zipCode, city, birthDate) =>
        Some((id, firstName, lastName, mobilePhone, landline, street, streetNumber, zipCode, city, birthDate.map(x => new JodaLocalDate(x.getYear, x.getMonthValue, x.getDayOfMonth))))
      case _ => None
    })
  )

  def showList: Action[AnyContent] = Action.async { implicit req =>
    childRepository.findAll.map(all => Ok(html.child.list.render(all, req.flash)))
  }

  def newChild: Action[AnyContent] = Action { implicit req => Ok(html.child.form.render(childForm, req.flash))}

  def saveChild: Action[AnyContent] = Action.async { implicit req =>
    childForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.child.form.render(formWithErrors, req.flash))),
      legacyChild => {
        val (_, child) = LegacyChild.legacyModel2childAndId(legacyChild)

        legacyChild.id match {
          case Some(id) =>
            childRepository
              .update(id, child)
              .map(_ => Redirect(routes.ChildController.details(id)).flashing("success" -> "Kind upgedated"))
          case _ =>
            childRepository.insert(UUID.randomUUID.toString, child).map(_ => Redirect(routes.ChildController.showList()).flashing("success" -> "Kind toegevoegd"))
        }
      }
    )
  }

  def editChild(id: Child.Id): Action[AnyContent] = Action.async { implicit req =>
    childRepository.findById(id) map {
      case Some(ch) => Ok(html.child.form.render(childForm.fill(LegacyChild.child2legacyModel(Some(ch._1), ch._2)), req.flash))
      case _ => BadRequest("Geen geldige id")
    }
  }

  def details(id: Child.Id): Action[AnyContent] = Action.async { implicit req =>
    childRepository.findById(id) map  {
      case Some(x) => Ok(html.child.details(x._2, x._1))
      case None => BadRequest("Geen kind met die ID")
    }
  }
}
