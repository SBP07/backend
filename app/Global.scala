import org.joda.time.LocalDate
import play.api._

import models._

import play.api.db.slick.Config.driver.simple.Session


object Global extends GlobalSettings {
  // scalastyle:off
  override def onStart(app: Application) {
    import play.api.Play.current
    play.api.db.slick.DB.withSession { implicit session =>
      insertChildren
      insertActivityTypes
      insertActivities
      insertAnimators
      insertChildActivities
    }

  }

  private def insertChildActivities(implicit s: Session) {
    if (ChildPresences.count == 0) {
      val someAct = Shifts.findAll.apply(4)
      val someOtherAct = Shifts.findAll.last
      val someChild = Children.findAll.head
      val anotherChild = Children.findAll.last

      ChildPresences.register(ChildPresence(someChild.id.get, someAct.id.get))
      ChildPresences.register(ChildPresence(someChild.id.get, someOtherAct.id.get))
      ChildPresences.register(ChildPresence(anotherChild.id.get, someOtherAct.id.get))
    }
  }

  private def insertAnimators(implicit s: Session) {
    if (Animators.count == 0) {
      Animators.insert(Animator(None, "Marva", "De Kip", Option("0477 77 88 99"), Option("055 66 44 11"),
        Option("marva@dekip.be"), Option("Straatlaan 66"), Option("Meise"), Option("BE66 5555 4444 3333"),
        Option(2012), true, Option(new LocalDate(1994, 4, 1))))
    }
  }

  def insertActivities(implicit s: Session) {
    if (Shifts.count == 0) {
      val vm = ShiftTypes.findByMnemonic("VM")
      val firstDay = new LocalDate(2014, 8, 15)
      vm.map(
        _.id.map(id => {
          Shifts.insert(Shift(None, firstDay, "Speelplein", id))
          Shifts.insert(Shift(None, firstDay.plusDays(1), "Speelplein", id))
          Shifts.insert(Shift(None, firstDay.plusDays(2), "Speelplein", id))
          Shifts.insert(Shift(None, firstDay.plusDays(3), "Speelplein", id))
          Shifts.insert(Shift(None, firstDay.plusDays(4), "Speelplein", id))
        })
      )

      val nm = ShiftTypes.findByMnemonic("NM")
      nm.map(
        _.id.map(id => {
          Shifts.insert(Shift(None, firstDay, "Speelplein", id))
          Shifts.insert(Shift(None, firstDay.plusDays(1), "Speelplein", id))
          Shifts.insert(Shift(None, firstDay.plusDays(2), "Speelplein", id))
          Shifts.insert(Shift(None, firstDay.plusDays(3), "Speelplein", id))
          Shifts.insert(Shift(None, firstDay.plusDays(4), "Speelplein", id))
        })
      )
    }
  }

  private def insertActivityTypes(implicit s: Session) {
    if (ShiftTypes.count == 0) {
      ShiftTypes.insert(ShiftType(None, "VM", "Voormiddag"))
      ShiftTypes.insert(ShiftType(None, "NM", "Namiddag"))
      ShiftTypes.insert(ShiftType(None, "MID", "Middag"))
      ShiftTypes.insert(ShiftType(None, "EXT", "Externe activitieit (niet op het speelplein)"))
    }
  }

  private def insertChildren(implicit s: Session) {
    if (Children.count == 0) {
      val exampleBirthdate1 = new LocalDate(1995, 1, 1)
      val exampleBirthdate2 = new LocalDate(1997, 12, 31)
      val exampleBirthdate3 = new LocalDate(2003, 6, 26)

      Children.insert(Child(None, "Thomas", "Toye", Option("0455 55 55 55"), Option("056/55 55 55"),
        Option("Tomberg 21A"), Option("Beveren-Leie"), Some(exampleBirthdate1),
        Option(new LocalDate(2014, 1, 1))))
      Children.insert(Child(None, "Jan", "Doe", Option("+324 55 88 99 22"), Option("554 2245 58"),
        Option("Straatlaan 55"), Option("Boeregat"), Some(exampleBirthdate2), None))
      Children.insert(Child(None, "Mies", "Doe", Option("+324 55 88 99 22"), Option("554 2245 58"),
        Option("Straatlaan 55"), Option("Boeregat"), Some(exampleBirthdate3), None))
    }
  }
  // scalastyle:on
}
