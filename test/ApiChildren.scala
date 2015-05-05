import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class ApiChildren extends Specification {

  "respond with children in JSON format" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT)) {

    import browser._

    goTo("/api/child/all")

    pageSource must contain("example")
  }
}
