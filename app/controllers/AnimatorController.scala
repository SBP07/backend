package controllers

import java.util.UUID
import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.data.AnimatorRepository
import be.thomastoye.speelsysteem.legacy.data.comparing.ComparingAnimatorRepository
import be.thomastoye.speelsysteem.legacy.data.couchdb.{CouchAnimatorRepository, CouchDatabase}
import be.thomastoye.speelsysteem.legacy.models.{Animator, AnimatorConstants}
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.libs.concurrent.Execution.Implicits._
import views._

import scala.concurrent.Future

class AnimatorController @Inject() (animatorRepository: ComparingAnimatorRepository) extends Controller {

  val animatorForm = Form(
    mapping(
      "id" -> optional(of[String]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "mobilePhone" -> optional(text),
      "landline" -> optional(text),
      "email" -> optional(text),

      "street" -> optional(text),
      "streetNumber" -> optional(text),
      "zipCode" -> optional(number),
      "city" -> optional(text),
      "bankAccount" -> optional(text),
      "yearStartedVolunteering" -> optional(
        number(AnimatorConstants.minimumYearStartedVolunteering, AnimatorConstants.maximumYearStartedVolunteering)
      ),
      "isPartOfCore" -> boolean,

      "birthDate" -> optional(jodaLocalDate("dd-MM-yyyy"))
    )(Animator.apply)(Animator.unapply)
  )

  def list: Action[AnyContent] = Action.async { implicit req =>
    animatorRepository.findAll.map { all =>
      Ok(html.animator.list.render(all.toList, req.flash))
    }
  }

  def details(id: String): Action[AnyContent] = Action.async { implicit req =>
    animatorRepository.findById(id) map {
      case Some(animator) => Ok(html.animator.details(animator))
      case None => BadRequest("Geen animator met die ID")
    }
  }

  def newAnimator: Action[AnyContent] = Action { implicit req => Ok(html.animator.form.render(animatorForm, req.flash))}

  def saveAnimator: Action[AnyContent] = Action.async { implicit req =>
    animatorForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.animator.form.render(formWithErrors, req.flash))),
      animator => {
        animator.id match {
          case Some(id) =>
            animatorRepository
              .update(animator)
              .map(_ => Redirect(routes.AnimatorController.details(id)).flashing("success" -> "Animator upgedated"))
          case _ =>
            animatorRepository
              .insert(animator.copy(id = Some(UUID.randomUUID().toString)))
              .map(_ => Redirect(routes.AnimatorController.list()).flashing("success" -> "Animator toegevoegd"))
        }
      }
    )
  }

  def editAnimator(id: String): Action[AnyContent] = Action.async { implicit req =>
    animatorRepository.findById(id) map {
      case Some(ch) => Ok(html.animator.form.render(animatorForm.fill(ch), req.flash))
      case _ => BadRequest("Geen geldige id")
    }
  }
}
