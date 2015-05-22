import controllers._
import org.junit.runner._
import org.specs2.mock._
import org.specs2.runner._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class MainControllerTest extends PlaySpecification with Mockito {
  "Requesting the heartbeat" should {
    "Reply 'online'" in new WithApplication {
      val mainController = new Main

      val result = mainController.heartbeat(FakeRequest())

      status(result) must be equalTo OK
      contentType(result).map { res => res must be equalTo "text/plain" }

      contentAsString(result) must be equalTo "online"
    }
  }

  "Requesting the main page" should {
    "Return an HTML page" in new WithApplication {
      val mainController = new Main

      val result = mainController.home(FakeRequest())

      status(result) must be equalTo OK
      contentType(result).map { res => res must be equalTo "text/html" }
    }

    "Use angular" in new WithApplication {
      val mainController = new Main

      val result = mainController.home.apply(FakeRequest())

      status(result) must be equalTo OK
      contentAsString(result) must contain("angular")
    }
  }
}