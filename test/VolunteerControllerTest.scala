import java.time.LocalDate

import controllers.ApiVolunteers
import models.Volunteer
import models.dao.VolunteerDao
import org.junit.runner._
import org.mockito.Matchers
import org.specs2.mock._
import org.specs2.runner._
import play.api.libs.json.JsString
import play.api.test._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class VolunteerControllerTest extends PlaySpecification with Mockito {
  val exampleVolunteer = Volunteer(
    Some(5), "firstname", "lastname", Some("0477 77 77 77"), None, None, Some("Straatlaan 55"), Some("Boeregat"),
    Some("BE55 5555 5555"), Some(2014), isPartOfCore = false, Some(LocalDate.of(1995, 5, 28))
  )

  "Requesting all volunteers" should {
    "Return a correct response" in new WithApplication {
      val mockedRepo = mock[VolunteerDao]
      mockedRepo.findAll returns Future(List(exampleVolunteer))

      val volunteerController = new ApiVolunteers(mockedRepo)

      val result = volunteerController.all.apply(FakeRequest())

      status(result) must be equalTo OK
      contentType(result).map { res => res must be equalTo "application/json" }

      import models.json.VolunteerJson.volunteerReads

      val validated = contentAsJson(result).validate[Seq[Volunteer]]
      validated.isSuccess must beTrue
      validated.get must be equalTo Seq(exampleVolunteer)
    }
  }

  "Requesting an existing volunteer by id" should {
    "Return a correct JSON response" in new WithApplication() {
      val mockedRepo = mock[VolunteerDao]
      mockedRepo.findById(Matchers.eq(5L)) returns Future(Some(exampleVolunteer))

      val volunteerController = new ApiVolunteers(mockedRepo)

      val result = volunteerController.byId(5L).apply(FakeRequest())

      status(result) must be equalTo OK
      contentType(result).map { res => res must be equalTo "application/json" }

      import models.json.VolunteerJson.volunteerReads

      val validated = contentAsJson(result).validate[Volunteer]
      validated.isSuccess must beTrue
      validated.get must be equalTo exampleVolunteer
    }
  }

  "Requesting a non-existing animator by id" should {
    "Return a HTTP 404 with JSON response" in new WithApplication() {
      val mockedDao = mock[VolunteerDao]
      mockedDao.findById(Matchers.eq(5L)) returns Future(None)

      val animatorController = new ApiVolunteers(mockedDao)

      val result = animatorController.byId(5).apply(FakeRequest())

      status(result) must be equalTo NOT_FOUND
      contentType(result).map { res => res must be equalTo "application/json" }

      import helpers.JsonHelpers.JsonStatus

      val validated = contentAsJson(result).validate[JsonStatus]
      validated.isSuccess must beTrue
      validated.get must be equalTo JsonStatus("Not Found", JsString("No item found with id '5'."))
    }
  }
}
