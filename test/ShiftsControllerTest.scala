import java.time.LocalDate

import controllers.ApiShifts
import models.dao.ShiftRepository
import models.{Child, Shift, ShiftType}
import models.json.ShiftJson._
import org.junit.runner._
import org.mockito.Matchers
import org.specs2.mock._
import org.specs2.runner._
import play.api.test._


@RunWith(classOf[JUnitRunner])
class ShiftsControllerTest extends PlaySpecification with Mockito {
  val exampleChild = Child(
    Some(5), "firstname", "lastname", Some("0477 77 77 77"), None, Some("Straatlaan 55"), Some("Boeregat"),
    Some(LocalDate.of(1995, 5, 28)), None
  )

  val exampleShiftType = ShiftType(Some(22), "VM", "Voormiddag")
  val exampleShift = Shift(Some(11), LocalDate.of(2015, 11, 22), "Speelplein", 22)

  "Requesting all shifts" should {
    "Return a correct JSON response" in new WithApplication {
      val mockedShiftRepo = mock[ShiftRepository]
      mockedShiftRepo.findAllWithTypeAndNumberOfPresences(Matchers.any()) returns
        Seq((exampleShiftType, exampleShift, 22))

      val shiftController = new ApiShifts(mockedShiftRepo)

      val result = shiftController.allShifts.apply(FakeRequest())

      status(result) must be equalTo OK
      contentType(result).map { res => res must be equalTo "application/json" }

      there was one(mockedShiftRepo).findAllWithTypeAndNumberOfPresences(Matchers.any())

      val validated = contentAsJson(result).validate[Seq[(ShiftType, Shift, Int)]]
      validated.isSuccess must beTrue
      validated.get must be equalTo Seq((exampleShiftType, exampleShift, 22))
    }
  }

  "Deleting a shift by id" should {
    "Return a 404 on non-existing id" in new WithApplication() {
      val mockedShiftRepo = mock[ShiftRepository]
      mockedShiftRepo.delete(Matchers.eq(55L))(Matchers.any()) returns 1

      val shiftController = new ApiShifts(mockedShiftRepo)

      val result = shiftController.delete(55L).apply(FakeRequest())

      status(result) must be equalTo OK
      contentType(result).map { res => res must be equalTo "application/json" }

      there was one(mockedShiftRepo).delete(Matchers.eq(55L))(Matchers.any())
    }

    "Return ok on existing id" in new WithApplication() {
      val mockedShiftRepo = mock[ShiftRepository]
      mockedShiftRepo.delete(Matchers.eq(55L))(Matchers.any()) returns 0

      val shiftController = new ApiShifts(mockedShiftRepo)

      val result = shiftController.delete(55L).apply(FakeRequest())

      status(result) must be equalTo NOT_FOUND
      contentType(result).map { res => res must be equalTo "application/json" }

      there was one(mockedShiftRepo).delete(Matchers.eq(55L))(Matchers.any())
    }
  }
}
