import org.junit.runner.RunWith
import models.{Shifts => ActivitiesModel, ChildPresences, ShiftTypes}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.db.slick.DB

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class Presences extends Specification{
  "Getting all presences" should {
    "have correct size" in new WithApplication {
      DB.withSession { implicit session =>
        ChildPresences.all must have size(3)
      }
    }
  }
}
