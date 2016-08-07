package be.thomastoye.speelsysteem.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class Day(day: Int, month: Int, year: Int) {
  def toLocalDate: LocalDate = LocalDate.of(year, month, day)
  override def toString = toLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
}
