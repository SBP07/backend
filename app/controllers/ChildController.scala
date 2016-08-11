package controllers

import javax.inject.Inject

import be.thomastoye.speelsysteem.data.ChildRepository
import be.thomastoye.speelsysteem.models.Child
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc._
import views._

class ChildController @Inject() (childRepository: ChildRepository) extends Controller {
  def showList: Action[AnyContent] = Action.async { implicit req =>
    childRepository.findAll.map(all => Ok(html.child.list.render(all, req.flash)))
  }

  def details(id: Child.Id): Action[AnyContent] = Action.async { implicit req =>
    childRepository.findById(id) map  {
      case Some(x) => Ok(html.child.details(x._2, x._1))
      case None => BadRequest("Geen kind met die ID")
    }
  }
}
