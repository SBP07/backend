package controllers

import javax.inject.Inject

import be.thomastoye.speelsysteem.data.CrewRepository
import be.thomastoye.speelsysteem.models.Crew.Id
import play.api.mvc._
import play.api.libs.concurrent.Execution.Implicits._
import views._


class CrewController @Inject() (crewRepository: CrewRepository) extends Controller {
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
}
