import com.softwaremill.macwire._
import controllers.{ApiAnimators, ApiChildren}
import models.repository.{SlickAnimatorRepository, ChildRepository}

object Application {

  lazy val childRepo = wire[ChildRepository]
  lazy val animatorRepo = wire[SlickAnimatorRepository]

  lazy val childrenController = wire[ApiChildren]
  lazy val animatorController = wire[ApiAnimators]
}
