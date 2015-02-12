import models.{ActivityTypes, Activities => ActivitiesModel}
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.db.slick.DB

import play.api.test._
import play.api.test.Helpers._

@RunWith(classOf[JUnitRunner])
class Activities extends Specification{
  "Activitytypes table" should {
    "Not be empty" in new WithApplication {
      DB.withSession { implicit session =>
        ActivityTypes.findAll must not have size(0)
      }
    }

    "Activities table" should {
      "Not be empty" in new WithApplication {
        DB.withSession { implicit session =>
          ActivitiesModel.findAll must not have size(0)
        }
      }

      "Be able to join activity types table" in new WithApplication {
        DB.withSession { implicit session =>
          ActivitiesModel.findAllWithType must not have size(0)

          ActivitiesModel.findAllWithType must have size(2)
        }
      }
    }
  }
}