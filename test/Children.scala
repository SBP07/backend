import org.junit.runner.RunWith
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._

@RunWith(classOf[JUnitRunner])
class Children extends Specification{
  "have children inserted by the Global class" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT)) {
    browser.goTo("/kind/lijst")

    // Check the page
    browser.$("title").getTexts().get(0) must equalTo("Kinderen")
    browser.$("tr").get(1).getText must contain("Thomas Toye") // one header row
  }

  "show the new child form" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT)) {
    import browser._
    goTo("/kind/lijst")
    click("button")

    browser.title must equalTo("Nieuw kind")
  }

  "show errors when the form isn't correct" in new WithBrowser(webDriver = WebDriverFactory(HTMLUNIT)) {
    import browser._
    goTo("/kind/lijst")
    click("button")

    browser.title must equalTo("Nieuw kind")
    $("button.btn").submit
    pageSource must contain("This field is required")
  }
}

