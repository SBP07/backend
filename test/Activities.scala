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
          val all = ActivitiesModel.findAllWithType

          all must not have size(0)

          all must have size(10)

          all(0)._1.mnemonic must be("VM")
          all(0)._2.place must be("Speelplein")
        }
      }
    }
  }
}