import org.joda.time.LocalDate
import play.api._

import models._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    import play.api.Play.current
    //Logger.debug("Application has started")
    play.api.db.slick.DB.withSession{ implicit session =>
      if(Children.count == 0) {
        Children.insert(Child(None, "Thomas", "Toye", Option("0455 55 55 55"), Option("056/55 55 55"),
          Option("Tomberg 21A"), Option("Beveren-Leie"), Option(new LocalDate(1995, 1,1)),
          Option(new LocalDate(2014,1,1))))
        Children.insert(Child(None, "Jan", "Doe", Option("+324 55 88 99 22"), Option("554 2245 58"),
          Option("Straatlaan 55"), Option("Boeregat"), Option(new LocalDate(1997,1,1)), None))
        Children.insert(Child(None, "Mies", "Doe", Option("+324 55 88 99 22"), Option("554 2245 58"),
          Option("Straatlaan 55"), Option("Boeregat"), Option(new LocalDate(1997,1,1)), None))
      }

      if(ActivityTypes.count == 0) {
        ActivityTypes.insert(ActivityType(None, "VM", "Voormiddag"))
        ActivityTypes.insert(ActivityType(None, "NM", "Namiddag"))
        ActivityTypes.insert(ActivityType(None, "MID", "Middag"))
        ActivityTypes.insert(ActivityType(None, "EXT", "Externe activitieit (niet op het speelplein)"))
      }

      if(Activities.count == 0) {
        val vm = ActivityTypes.findByMnemonic("VM")
        vm.map(
          _.id.map(id => {
            Activities.insert(Activity(None, new LocalDate(2014, 8, 15), "Speelplein", id))
            Activities.insert(Activity(None, new LocalDate(2014, 8, 16), "Speelplein", id))
            Activities.insert(Activity(None, new LocalDate(2014, 8, 17), "Speelplein", id))
            Activities.insert(Activity(None, new LocalDate(2014, 8, 18), "Speelplein", id))
            Activities.insert(Activity(None, new LocalDate(2014, 8, 19), "Speelplein", id))
          })
        )

        val nm = ActivityTypes.findByMnemonic("NM")
        nm.map(
          _.id.map(id => {
            Activities.insert(Activity(None, new LocalDate(2014, 8, 15), "Speelplein", id))
            Activities.insert(Activity(None, new LocalDate(2014, 8, 16), "Speelplein", id))
            Activities.insert(Activity(None, new LocalDate(2014, 8, 17), "Speelplein", id))
            Activities.insert(Activity(None, new LocalDate(2014, 8, 18), "Speelplein", id))
            Activities.insert(Activity(None, new LocalDate(2014, 8, 19), "Speelplein", id))
          })
        )
      }

      if(Animators.count == 0) {
        Animators.insert(Animator(None, "Marva", "De Kip", Option("0477 77 88 99"), Option("055 66 44 11"),
          Option("marva@dekip.be"), Option("Straatlaan 66"), Option("Meise"), Option("BE66 5555 4444 3333"),
          Option(2012), true, Option(new LocalDate(1994, 4, 1))))
      }

      if(ChildPresences.count == 0) {
        val someAct = Activities.findAll.apply(4)
        val someOtherAct = Activities.findAll.last
        val someChild = Children.findAll.head

        ChildPresences.register(ChildToActivity(someChild.id.get, someAct.id.get))
        ChildPresences.register(ChildToActivity(someChild.id.get, someOtherAct.id.get))
      }
    }

  }  
  
  override def onStop(app: Application) {
    //Logger.debug("Application shutdown...")
  }  
    
}