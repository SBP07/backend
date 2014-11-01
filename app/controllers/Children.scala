package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.slick._
import scala.slick.driver.H2Driver.simple._

import views._
import models._

object Children extends Controller {

  val childForm = Form(
    mapping(
      "id" -> ignored(None:Option[Long]), // difference between this and optional(number)?
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "mobilePhone" -> optional(text),
      "landline" -> optional(text),
      
      "street" -> optional(text),
      "city" -> optional(text),
      
      "birthdate" -> optional(date("dd-MM-yyyy")),
      "medicalRecordGood" -> boolean,
      "medicalRecordChecked" -> optional(date("dd-MM-yyyy"))
    )(Child.apply)(Child.unapply)
  )
  
  
  def showList = DBAction { implicit rs => Ok(html.child_list.render(models.ChildrenSlick.findAll)) }
  
  def newChild = Action { Ok(html.child_form.render(childForm)) }
  
  def saveChild = DBAction { implicit request =>
    childForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.child_form.render(formWithErrors)),
      child => {
        import models.ChildrenSlick
        child.id match{
          case Some(x) => ChildrenSlick.update(child)
          case _ => ChildrenSlick.insert(child)
        }
        Ok("Succes")
      }
    )
    
  }
  
  def editChild(id: Long) = DBAction { implicit rs =>
    val child = ChildrenSlick.findById(id)
    child match{
      case Some(ch) => Ok(html.child_form.render(childForm.fill(ch)));
      case _ => BadRequest("Geen geldige id")
    }
  }
  
  def testFlash = Action {implicit request => Ok( html.showflash.render(request.flash)) }
  
  def details(id: Long) = TODO

}