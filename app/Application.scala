import com.softwaremill.macwire._
import controllers.{Main, ApiShifts, ApiAnimators, ApiChildren}
import models.repository.{ShiftRepository, SlickAnimatorRepository, ChildRepository}

object Application {

  lazy val childRepo = wire[ChildRepository]
  lazy val animatorRepo = wire[SlickAnimatorRepository]
  lazy val shiftRepo = wire[ShiftRepository]

  lazy val childrenController = wire[ApiChildren]
  lazy val animatorController = wire[ApiAnimators]
  lazy val shiftsController = wire[ApiShifts]
  lazy val mainController = wire[Main]
}
