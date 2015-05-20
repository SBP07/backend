package models

import java.time.LocalDate

case class Shift(id: Option[Long] = None, date: LocalDate, place: String, shiftTypeId: Long)

case class ShiftType(id: Option[Long], mnemonic: String, description: String)
