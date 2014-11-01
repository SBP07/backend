import play.api._
import play.api.db.slick.Config.driver.simple._

import java.util.Date

import models._

object Global extends GlobalSettings {

  override def onStart(app: Application) {
    import play.api.Play.current
    Logger.info("Application has started")
    play.api.db.slick.DB.withSession{ implicit session =>
      ChildrenSlick.insert(Child(Some(4), "Thomas", "Toye", Option("0455 55 55 55"), Option("056/55 55 55"), Option("Tomberg 21A"), Option("Beveren-Leie"), Option(new Date()), true, Option(new Date())))
    }
    //Child(Some(4), "Thomas", "Toye", Option("0455 55 55 55"), Option("056/55 55 55"), Option("Tomberg 21A"), Option("Beveren-Leie"), Option(new Date()), true, Option(new Date())),
    //Child(None, "Jan", "Doe", Option("+324 55 88 99 22"), Option("554 2245 58"), Option("Straatlaan 55"), Option("Boeregat"), Option(new Date()), false, Option(new Date())

  }  
  
  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
  }  
    
}