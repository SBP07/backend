package controllers

import models.{Shifts => ShiftsModel, Shift, ShiftTypes}
import org.joda.time.LocalDate
import play.api.mvc._
import play.api.db.slick._
import play.api.data.Forms._
import play.api.data._
import helpers.DateTime._

object Shifts extends Controller {
  case class ShiftsPost(date: LocalDate, shiftTypes: List[Long], externalLocation: String)

  val shiftsForm = Form(
    mapping(
      "date" -> jodaLocalDate("dd-MM-yyyy"),
      "selectedShiftTypes" -> Forms.list(longNumber),
      "externalLocation" -> text
    )(ShiftsPost.apply)(ShiftsPost.unapply)
  )

  def list: Action[AnyContent] = DBAction { implicit s =>
    Ok(views.html.shifts.list.render(ShiftsModel.findAllWithTypeAndNumberOfPresences, s.flash))
  }

  def newShift: Action[AnyContent] = DBAction { implicit s =>
    val types = ShiftTypes.findAll
    Ok(views.html.shifts.form.render(shiftsForm, types, s.flash))
  }
  def saveShift: Action[AnyContent] = DBAction { implicit s =>
    shiftsForm.bindFromRequest.fold(
      formWithErrors => {
        val types = ShiftTypes.findAll
        BadRequest(views.html.shifts.form.render(formWithErrors, types, s.flash))
      },
      post => {
        val shiftTypes = post.shiftTypes.map(ShiftTypes.findById).flatten.toSet
        val alreadyPersisted: Set[Long] = shiftTypes.map { t =>
          models.Shifts.findByDateAndType(post.date, t)
        }.flatten.map(_.shiftId)

        val notPersistedYet = shiftTypes.filterNot(_.id.map(a =>
          alreadyPersisted.contains(a)
        ).get)

        val externalActivityId: Long = ShiftTypes.findByMnemonic("EXT").flatMap(_.id).getOrElse(-1)

        notPersistedYet.map(_.id).flatten foreach { id =>
          val place = if(id == externalActivityId) post.externalLocation else "Speelplein"
          models.Shifts insert Shift(None, post.date, place, id)
        }

        Redirect(routes.Shifts.list).flashing("success" -> s"${notPersistedYet.size} dagdelen toegevoegd")
      }
    )
  }

  def updateShift(dateString: String) = DBAction { implicit s =>
    try{
      val date: LocalDate = LocalDate.parse(dateString, fmt)
      val shift = models.Shifts.findByDate(date)
      val extPlace = "Test"
      val fill = ShiftsPost(date, shift.map(_.shiftId).toList, extPlace)
      val types = ShiftTypes.findAll
      Ok(views.html.shifts.form.render(shiftsForm.fill(fill), types, s.flash))
    } catch {
      case e: IllegalArgumentException => BadRequest("Could not parse date")
    }
  }

  def deleteShift(id: Long) = DBAction { implicit s =>
    val shift = models.Shifts.findById(id)

    shift.map { act =>
      models.Shifts.delete(act)
      Redirect(routes.Shifts.list()).flashing("success" -> "Dagdeel verwijderd")
    }.getOrElse(BadRequest("Activiteit niet gevonden"))
  }

}
