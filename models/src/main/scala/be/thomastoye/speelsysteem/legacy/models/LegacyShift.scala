package be.thomastoye.speelsysteem.legacy.models

import org.joda.time.LocalDate

case class LegacyShift(id: Option[Long] = None, date: LocalDate, place: String, shiftId: Long)
case class ShiftType(id: Option[Long], mnemonic: String, description: String)
