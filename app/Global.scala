import helpers.CsvImporters
import models._
import models.repository._
import play.api._
import play.api.db.slick.Config.driver.simple.Session
import com.softwaremill.macwire.{InstanceLookup, Macwire}


object Global extends GlobalSettings with Macwire {

  val wired = wiredInModule(Application)
  override def getControllerInstance[A](controllerClass: Class[A]) = wired.lookupSingleOrThrow(controllerClass)

  val childRepository = wire[ChildRepository]
  val animatorRepository = wire[AnimatorRepository]

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
      CsvImporters.childPresences("conf/initial_data/child_presences.csv", childRepository).foreach(ChildPresenceRepository.register)
    }
  }

  private def insertAnimators(implicit s: Session) {
    if (animatorRepository.count == 0) {
      helpers.CsvImporters
        .animators("conf/initial_data/animators.csv")
        .foreach(animatorRepository.insert)
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
    if (childRepository.count == 0) {
      CsvImporters.children("conf/initial_data/children.csv").foreach(childRepository.insert)
    }
  }

  // scalastyle:on
}
