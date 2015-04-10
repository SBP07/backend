package controllers

import models.{ShiftRepository, Shift, ShiftTypeRepository}
import org.joda.time.LocalDate
import play.api.mvc._
import play.api.db.slick._
import play.api.data.Forms._
import play.api.data._
import helpers.DateTime._

object Shifts extends Controller {
  case class ShiftsPost(date: LocalDate, shiftTypes: List[Long], externalLocation: String)
  case class ShiftDelete(id: Long)

  val deleteForm = Form(
      mapping(
      "id" -> longNumber
    )(ShiftDelete.apply)(ShiftDelete.unapply)
  )

  val shiftsForm = Form(
    mapping(
      "date" -> jodaLocalDate("dd-MM-yyyy"),
      "selectedShiftTypes" -> Forms.list(longNumber),
      "externalLocation" -> text
    )(ShiftsPost.apply)(ShiftsPost.unapply)
  )

  def list: Action[AnyContent] = DBAction { implicit req =>
    Ok(views.html.shifts.list.render(ShiftRepository.findAllWithTypeAndNumberOfPresences, req.flash))
  }

  def newShift: Action[AnyContent] = DBAction { implicit req =>
    val types = ShiftTypeRepository.findAll
    Ok(views.html.shifts.form.render(shiftsForm, types, req.flash))
  }
  def saveShift: Action[AnyContent] = DBAction { implicit req =>
    shiftsForm.bindFromRequest.fold(
      formWithErrors => {
        val types = ShiftTypeRepository.findAll
        BadRequest(views.html.shifts.form.render(formWithErrors, types, req.flash))
      },
      post => {
        val shiftTypes = post.shiftTypes.map(ShiftTypeRepository.findById).flatten.toSet
        val alreadyPersisted: Set[Long] = shiftTypes.map { t =>
          models.ShiftRepository.findByDateAndType(post.date, t)
        }.flatten.map(_.shiftId)

        val notPersistedYet = shiftTypes.filterNot(_.id.map(a =>
          alreadyPersisted.contains(a)
        ).get)

        val externalActivityId: Long = ShiftTypeRepository.findByMnemonic("EXT").flatMap(_.id).getOrElse(-1)

        notPersistedYet.map(_.id).flatten foreach { id =>
          val place = if(id == externalActivityId) post.externalLocation else "Speelplein"
          models.ShiftRepository insert Shift(None, post.date, place, id)
        }

        Redirect(routes.Shifts.list).flashing("success" -> s"${notPersistedYet.size} dagdelen toegevoegd")
      }
    )
  }

  def updateShift(dateString: String): Action[AnyContent] = DBAction { implicit req =>
    try{
      val date: LocalDate = LocalDate.parse(dateString, fmt)
      val shift = models.ShiftRepository.findByDate(date)
      val extPlace = "Test"
      val fill = ShiftsPost(date, shift.map(_.shiftId).toList, extPlace)
      val types = ShiftTypeRepository.findAll
      Ok(views.html.shifts.form.render(shiftsForm.fill(fill), types, req.flash))
    } catch {
      case e: IllegalArgumentException => BadRequest("Could not parse date")
    }
  }

  def deleteShift(id: Long): Action[AnyContent] = DBAction { implicit req =>
    val found = models.ShiftRepository.findByIdWithTypeAndNumberOfPresences(id)
    val numChildren = 4

    found.map { found =>
      Ok(views.html.shifts.confirm_delete(found._3, found._1, found._2))
    }.getOrElse(BadRequest("Dagdeel niet gevonden"))

  }

  def reallyDeleteShift(): Action[AnyContent] = DBAction { implicit req =>
    deleteForm.bindFromRequest.fold(
      errorForm => BadRequest("Bad id"),
      deleteShift => {
        val shift = models.ShiftRepository.findById(deleteShift.id)

        shift.map { act =>
          models.ShiftRepository.delete(act)
          Redirect(routes.Shifts.list()).flashing("success" -> "Dagdeel verwijderd")
        }.getOrElse(BadRequest("Dagdeel niet gevonden"))
      }
    )
  }

}
