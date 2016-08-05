package controllers

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.data.CrewRepository
import be.thomastoye.speelsysteem.legacy.models.{LegacyCrew, LegacyCrewConstants}
import be.thomastoye.speelsysteem.legacy.data.slick.SlickCrewRepository.{crew2legacyModel, legacyModel2crewAndId}
import be.thomastoye.speelsysteem.models.Crew
import be.thomastoye.speelsysteem.models.Crew.Id
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.libs.concurrent.Execution.Implicits._
import views._

import scala.concurrent.Future

class CrewController @Inject() (crewRepository: CrewRepository) extends Controller {

  val crewMemberForm = Form(
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
        number(LegacyCrewConstants.minimumYearStartedVolunteering, LegacyCrewConstants.maximumYearStartedVolunteering)
      ),

      "birthDate" -> optional(jodaLocalDate("dd-MM-yyyy"))
    )(LegacyCrew.apply)(LegacyCrew.unapply)
  )

  def list: Action[AnyContent] = Action.async { implicit req =>
    crewRepository.findAll.map { all =>
      Ok(html.crew.list.render(all.toList, req.flash))
    }
  }

  def details(id: Id): Action[AnyContent] = Action.async { implicit req =>
    crewRepository.findById(id) map {
      case Some( (crewId, crewMember) ) => Ok(html.crew.details(crewId, crewMember))
      case None => BadRequest("Geen animator met die ID")
    }
  }

  def newCrewMember: Action[AnyContent] = Action { implicit req => Ok(html.crew.form.render(crewMemberForm, req.flash))}

  def saveCrewMember: Action[AnyContent] = Action.async { implicit req =>
    crewMemberForm.bindFromRequest.fold(
      formWithErrors => Future.successful(BadRequest(html.crew.form.render(formWithErrors, req.flash))),
      legacyCrew => {
        val (_, crew) = legacyModel2crewAndId(legacyCrew)
        val maybeId = legacyCrew.id

        maybeId match {
          case Some(id) =>
            crewRepository
              .update(id, crew)
              .map(_ => Redirect(routes.CrewController.details(id)).flashing("success" -> "Animator upgedated"))
          case _ =>
            crewRepository
              .insert(crew)
              .map(_ => Redirect(routes.CrewController.list()).flashing("success" -> "Animator toegevoegd"))
        }
      }
    )
  }

  def editCrewMember(id: Crew.Id): Action[AnyContent] = Action.async { implicit req =>
    crewRepository.findById(id) map {
      case Some(ch) => Ok(html.crew.form.render(crewMemberForm.fill(crew2legacyModel(ch._2, Some(ch._1))), req.flash))
      case _ => BadRequest("Geen geldige id")
    }
  }
}
