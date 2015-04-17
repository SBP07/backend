package controllers

import play.api.data.Form
import play.api.db.slick.DBAction
import play.api.mvc._
import models.{MedicalFile, MedicalFileRepository}

import play.api.data.Forms._
import play.api.data.format.Formats._


object MedicalFiles extends Controller {

  val fileFormPart = Form(
    mapping(
      "id" -> optional(of[Long]),

      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,

      "street" -> nonEmptyText,
      "city" -> nonEmptyText,

      "bloodType" -> optional(text),
      "isMale" -> boolean,

      "allergicToDust" -> boolean,
      "allergicToFacePaint" -> boolean,
      "allergicToBees" -> boolean,
      "otherAllergies" -> optional(text),

      "hasAsthma" -> boolean,
      "hasHayFever" -> boolean,
      "hasEpilepsy" -> boolean,
      "hasDiabetes" -> boolean,
      "otherConditions" -> optional(text),

      "extraInformation" -> optional(text),
      "tetanusShot" -> optional(jodaLocalDate("dd-MM-yyyy"))
    )(MedicalFile.apply)(MedicalFile.unapply)
  )

  def form: Action[AnyContent] = DBAction { implicit req =>
    Ok(views.html.medicalFile.form.render(fileFormPart))
  }

  def postForm: Action[AnyContent] = DBAction { implicit  req =>
    fileFormPart.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.medicalFile.form.render(formWithErrors)),
      medicalFile => {
        medicalFile.id match {
          case Some(id) => {
            //MedicalFiles.update(medicalFile)
            //Redirect(routes..details(id)).flashing("success" -> "Animator upgedated")
            Ok("Gelukt update")
          }
          case _ => {
            MedicalFileRepository.insert(medicalFile)(req.dbSession)
            Ok(views.html.medicalFile.successfullyCreated())
          }
        }
      }
    )
  }
}
