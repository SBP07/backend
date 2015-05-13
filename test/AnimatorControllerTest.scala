import java.time.LocalDate

import controllers.ApiAnimators
import models.Animator
import models.repository.AnimatorRepository
import org.junit.runner._
import org.mockito.Matchers
import org.specs2.mock._
import org.specs2.runner._
import play.api.libs.json.JsString
import play.api.test._

@RunWith(classOf[JUnitRunner])
class AnimatorControllerTest extends PlaySpecification with Mockito {
  val exampleAnimator = Animator(
    Some(5), "firstname", "lastname", Some("0477 77 77 77"), None, None, Some("Straatlaan 55"), Some("Boeregat"),
    Some("BE55 5555 5555"), Some(2014), isPartOfCore = false, Some(LocalDate.of(1995, 5, 28))
  )

  "Requesting all animators" should {
    "Return a correct response" in new WithApplication {
      val mockedRepo = mock[AnimatorRepository]
      mockedRepo.findAll(Matchers.any()) returns List(exampleAnimator)

      val animatorController = new ApiAnimators(mockedRepo)

      val result = animatorController.allAnimators.apply(FakeRequest())

      status(result) must be equalTo OK
      contentType(result).map { res => res must be equalTo "application/json" }

      import models.json.AnimatorJson.animatorReads

      val validated = contentAsJson(result).validate[Seq[Animator]]
      validated.isSuccess must beTrue
      validated.get must be equalTo Seq(exampleAnimator)
    }
  }

  "Requesting an existing animator by id" should {
    "Return a correct JSON response" in new WithApplication() {
      val mockedRepo = mock[AnimatorRepository]
      mockedRepo.findById(Matchers.eq(5L))(Matchers.any()) returns Some(exampleAnimator)

      val animatorController = new ApiAnimators(mockedRepo)

      val result = animatorController.animatorById(5).apply(FakeRequest())

      status(result) must be equalTo OK
      contentType(result).map { res => res must be equalTo "application/json" }

      import models.json.AnimatorJson.animatorReads

      val validated = contentAsJson(result).validate[Animator]
      validated.isSuccess must beTrue
      validated.get must be equalTo exampleAnimator
    }
  }

  "Requesting a non-existing animator by id" should {
    "Return a HTTP 404 with JSON response" in new WithApplication() {
      val mockedRepo = mock[AnimatorRepository]
      mockedRepo.findById(Matchers.eq(5L))(Matchers.any()) returns None

      val animatorController = new ApiAnimators(mockedRepo)

      val result = animatorController.animatorById(5).apply(FakeRequest())

      status(result) must be equalTo NOT_FOUND
      contentType(result).map { res => res must be equalTo "application/json" }

      import helpers.JsonHelpers.JsonStatus

      val validated = contentAsJson(result).validate[JsonStatus]
      validated.isSuccess must beTrue
      validated.get must be equalTo JsonStatus("Not Found", JsString("No item found with id '5'."))
    }
  }
}
