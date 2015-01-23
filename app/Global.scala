import play.api._
import play.api.db.slick.Config.driver.simple._

import java.util.Date

import models._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    import play.api.Play.current
    Logger.info("Application has started")
    play.api.db.slick.DB.withSession{ implicit session =>
      if(Children.count == 0) {
        Children.insert(Child(None, "Thomas", "Toye", Option("0455 55 55 55"), Option("056/55 55 55"), Option("Tomberg 21A"), Option("Beveren-Leie"), Option(new Date()), true, Option(new Date())))
        Children.insert(Child(None, "Jan", "Doe", Option("+324 55 88 99 22"), Option("554 2245 58"), Option("Straatlaan 55"), Option("Boeregat"), Option(new Date()), false, Option(new Date())))
      }

      if(ActivityTypes.count == 0) {
        ActivityTypes.insert(ActivityType(None, "VM", "Voormiddag"))
        ActivityTypes.insert(ActivityType(None, "NM", "Namiddag"))
        ActivityTypes.insert(ActivityType(None, "MID", "Middag"))
        ActivityTypes.insert(ActivityType(None, "EXT", "Externe activitieit (niet op het speelplein)"))
      }

      if(Activities.count == 0) {
        Activities.insert(Activity(None, new Date(), "Speelplein", ActivityTypes.findAll.head.id.get))
      }
    }

  }  
  
  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }  
    
}