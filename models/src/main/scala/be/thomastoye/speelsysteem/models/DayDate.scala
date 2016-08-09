package be.thomastoye.speelsysteem.models

import java.time.LocalDate
import java.time.format.DateTimeFormatter

case class DayDate(day: Int, month: Int, year: Int) extends Ordered[DayDate] {
  def toLocalDate: LocalDate = LocalDate.of(year, month, day)

  override def toString = toLocalDate.format(DateTimeFormatter.ISO_LOCAL_DATE)

  override def compare(that: DayDate): Int = this.toLocalDate.compareTo(that.toLocalDate)
}
