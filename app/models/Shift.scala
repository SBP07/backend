package models

import models.repository.ChildRepository
import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ForeignKeyQuery, ProvenShape}

case class Shift(id: Option[Long] = None, date: LocalDate, place: String, shiftId: Long)
case class ShiftType(id: Option[Long], mnemonic: String, description: String)
