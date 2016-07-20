package models

import org.joda.time.LocalDate

case class Shift(id: Option[Long] = None, date: LocalDate, place: String, shiftId: Long)
case class ShiftType(id: Option[Long], mnemonic: String, description: String)
