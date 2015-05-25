import com.softwaremill.macwire.Macwire
import models.repository._
import play.api._


object Global extends GlobalSettings with Macwire {

  val wired = wiredInModule(Application)
  override def getControllerInstance[A](controllerClass: Class[A]): A = wired.lookupSingleOrThrow(controllerClass)

  val childRepository = wire[ChildRepository]
  val animatorRepository = wire[SlickAnimatorRepository]
  val shiftRepository = wire[ShiftRepository]

}
