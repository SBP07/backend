import java.time.LocalDate

import controllers.ApiChildren
import helpers.JsonHelpers.JsonStatus
import models.Child
import models.dao.ChildDao
import models.json.ChildJson._
import org.junit.runner._
import org.mockito.Matchers
import org.mockito.Mockito.doNothing
import org.specs2.mock._
import org.specs2.runner._
import play.api.http.HeaderNames
import play.api.libs.json._
import play.api.test._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@RunWith(classOf[JUnitRunner])
class ChildrenControllerTest extends PlaySpecification with Mockito {
  val exampleChild = Child(
    Some(5), "firstname", "lastname", Some("0477 77 77 77"), None, Some("Straatlaan 55"), Some("Boeregat"),
    Some(LocalDate.of(1995, 5, 28)), None
  )

  "Requesting all children" should {
    "Return a correct response" in new WithApplication {
      val mockedRepo = mock[ChildDao]
      mockedRepo.findAll returns Future(List(exampleChild))

      val childrenController = new ApiChildren(mockedRepo)

      val result = childrenController.all.apply(FakeRequest())

      status(result) must be equalTo OK
      contentType(result).map { res => res must be equalTo "application/json" }

      val validated = contentAsJson(result).validate[Seq[Child]]
      validated.isSuccess must beTrue
      validated.get must be equalTo Seq(exampleChild)
    }
  }

  "Requesting an existing child by id" should {
    "Return a correct JSON response" in new WithApplication() {
      val mockedRepo = mock[ChildDao]
      mockedRepo.findById(Matchers.eq(5L)) returns Future(Some(exampleChild))

      val childController = new ApiChildren(mockedRepo)

      val result = childController.byId(5).apply(FakeRequest())

      status(result) must be equalTo OK
      contentType(result).map { res => res must be equalTo "application/json" }

      val validated = contentAsJson(result).validate[Child]
      validated.isSuccess must beTrue
      validated.get must be equalTo exampleChild
    }
  }

  "Requesting a non-existing child by id" should {
    "Return a HTTP 404 with JSON response" in new WithApplication() {
      val mockedRepo = mock[ChildDao]
      mockedRepo.findById(Matchers.eq(5L)) returns Future(None)

      val childController = new ApiChildren(mockedRepo)

      val result = childController.byId(5).apply(FakeRequest())

      status(result) must be equalTo NOT_FOUND
      contentType(result).map { res => res must be equalTo "application/json" }

      val validated = contentAsJson(result).validate[JsonStatus]
      validated.isSuccess must beTrue
      validated.get must be equalTo JsonStatus("Not Found", JsString("No child found with id '5'."))
    }
  }

  "Updating a child" should {
    "Return a bad request response on badly formatted input" in new WithApplication() {
      val mockedRepo = mock[ChildDao]
      doNothing.when(mockedRepo).update(Matchers.any())

      val childController = new ApiChildren(mockedRepo)

      val json = Json.parse( """{"firstName": true}""")
      val result = childController.update.apply(FakeRequest().withBody(json)).run

      status(result) must be equalTo BAD_REQUEST
      contentType(result).map { res => res must be equalTo "application/json" }

      there was no(mockedRepo).update(Matchers.any())
    }

    "Update the child on a correctly formatted request" in new WithApplication() {
      val mockedRepo = mock[ChildDao]
      doNothing.when(mockedRepo).update(Matchers.any())

      val childController = new ApiChildren(mockedRepo)

      val json = Json.toJson(exampleChild)
      val result = childController.update.apply(FakeRequest().withJsonBody(json).withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")).run

      play.Logger.debug(json.toString)
      play.Logger.debug(contentAsString(result))

      status(result) must be equalTo OK
      contentType(result).map { res => res must be equalTo "application/json" }

      val validated = contentAsJson(result).validate[JsonStatus]
      validated.isSuccess must beTrue

      validated.get must like { case JsonStatus("Success", _) => ok }

      there was one(mockedRepo).update(Matchers.any())
    }
  }

  "Inserting a child" should {
    "Return a bad request response on badly formatted input" in new WithApplication {
      val mockedRepo = mock[ChildDao]
      mockedRepo.insert(Matchers.any()) returns Future(0)

      val childController = new ApiChildren(mockedRepo)

      val json = Json.parse( """{"firstName": true}""")
      val result = childController.create.apply(FakeRequest().withJsonBody(json)).run

      status(result) must be equalTo BAD_REQUEST
      contentType(result).map { res => res must be equalTo "application/json" }

      there was no(mockedRepo).insert(Matchers.any())
    }

    "Work for correctly formatted inputs" in new WithApplication {
      val mockedRepo = mock[ChildDao]
      mockedRepo.insert(Matchers.any()) returns Future(1)

      val childController = new ApiChildren(mockedRepo)

      val json = Json.toJson(exampleChild)
      val result = childController.create.apply(FakeRequest().withJsonBody(json).withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")).run

      status(result) must be equalTo CREATED

      there was one(mockedRepo).insert(Matchers.any())
    }

  }

}
