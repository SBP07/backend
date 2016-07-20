import helpers.CsvImporters
import org.joda.time.LocalDate
import play.api._

import models._
import models.repositories.slick._

import play.api.db.slick.Config.driver.simple.Session


object Global extends GlobalSettings {
  // scalastyle:off
  override def onStart(app: Application) {
    import play.api.Play.current
    play.api.db.slick.DB.withSession { implicit session =>
      insertChildren
      insertShiftTypes
      insertShifts
      insertAnimators
      insertChildPresences
    }

  }

  private def insertChildPresences(implicit s: Session) {
    if (ChildPresenceRepository.count == 0) {
      CsvImporters.childPresences("conf/initial_data/child_presences.csv").foreach(ChildPresenceRepository.register)
    }
  }

  private def insertAnimators(implicit s: Session) {
    if (AnimatorRepository.count == 0) {
      CsvImporters
        .animators("conf/initial_data/animators.csv")
        .foreach(AnimatorRepository.insert)
    }
  }

  def insertShifts(implicit s: Session) {
    if (ShiftRepository.count == 0) {
      CsvImporters.shifts("conf/initial_data/shifts.csv").foreach(ShiftRepository.insert)
    }
  }

  private def insertShiftTypes(implicit s: Session) {
    if (ShiftTypeRepository.count == 0) {
      ShiftTypeRepository.insert(ShiftType(None, "VM", "Voormiddag"))
      ShiftTypeRepository.insert(ShiftType(None, "NM", "Namiddag"))
      ShiftTypeRepository.insert(ShiftType(None, "MID", "Middag"))
      ShiftTypeRepository.insert(ShiftType(None, "EXT", "Externe activitieit (niet op het speelplein)"))
    }
  }

  private def insertChildren(implicit s: Session) {
    if (ChildRepository.count == 0) {
      CsvImporters.children("conf/initial_data/children.csv").foreach(ChildRepository.insert)
    }
  }
  // scalastyle:on
}
