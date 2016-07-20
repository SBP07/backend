package models

import models.repositories.slick.{ChildRepository, ChildrenToShifts}
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ForeignKeyQuery, ProvenShape}

case class ChildPresence(childId: Long, shiftId: Long)

