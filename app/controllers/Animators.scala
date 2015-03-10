package controllers

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.db.slick._

import views._
import models.{Animators => AnimatorsModel, AnimatorVals, Animator}

object Animators extends Controller {

  val animatorForm = Form(
    mapping(
      "id" -> optional(of[Long]),
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "mobilePhone" -> optional(text),
      "landline" -> optional(text),
      "email" -> optional(text),

      "street" -> optional(text),
      "city" -> optional(text),
      "bankAccount" -> optional(text),
      "yearStartedVolunteering" -> optional(
        number(AnimatorVals.minimumYearStartedVolunteering, AnimatorVals.maximumYearStartedVolunteering)
      ),
      "isPartOfCore" -> boolean,

      "birthDate" -> optional(jodaLocalDate("dd-MM-yyyy"))
    )(Animator.apply)(Animator.unapply)
  )

  def list: Action[AnyContent] = DBAction { implicit rs => Ok(html.animator.list.render(AnimatorsModel.findAll, rs.flash))}

  def details(id: Long): Action[AnyContent] = DBAction { implicit rs =>
    val animator = AnimatorsModel.findById(id)
    animator match {
      case Some(x) => Ok(html.animator.details(x))
      case None => BadRequest("Geen animator met die ID")
    }
  }

  def newAnimator: Action[AnyContent] = Action { implicit rs => Ok(html.animator.form.render(animatorForm, rs.flash))}

  def saveAnimator: Action[AnyContent] = DBAction { implicit rs =>
    animatorForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.animator.form.render(formWithErrors, rs.flash)),
      animator => {
        animator.id match {
          case Some(id) => {
            AnimatorsModel.update(animator)
            Redirect(routes.Animators.details(id)).flashing("success" -> "Animator upgedated")
          }
          case _ => {
            AnimatorsModel.insert(animator)
            Redirect(routes.Animators.list).flashing("success" -> "Animator toegevoegd")
          }
        }
      }
    )
  }

  def editAnimator(id: Long): Action[AnyContent] = DBAction { implicit rs =>
    val animator = AnimatorsModel.findById(id)
    animator match {
      case Some(ch) => Ok(html.animator.form.render(animatorForm.fill(ch), rs.flash))
      case _ => BadRequest("Geen geldige id")
    }
  }
}
