package models

import java.time.LocalDate

case class Shift(id: Option[Long] = None, date: LocalDate, place: String, shiftTypeId: Long)

case class ShiftType(id: Option[Long], mnemonic: String, description: String)

object ShiftTypeConstants {
  val vm = 1L
  val mid = 2L
  val nm = 3L
  val ext = 4L
}
