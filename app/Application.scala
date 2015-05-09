import com.softwaremill.macwire._
import controllers.{ApiAnimators, ApiChildren}
import models.repository.{AnimatorRepository, ChildRepository}

object Application {

  lazy val childRepo = wire[ChildRepository]
  lazy val animatorRepo = wire[AnimatorRepository]

  lazy val childrenController = wire[ApiChildren]
  lazy val animatorController = wire[ApiAnimators]
}
