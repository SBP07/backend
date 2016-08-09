package be.thomastoye.speelsysteem.legacy.data
import be.thomastoye.speelsysteem.legacy.models.{LegacyShift, ShiftType}
import org.joda.time.LocalDate

import scala.concurrent.Future

trait ShiftRepository {

  def findAll: Future[Seq[LegacyShift]]

  def findById(id: Long): Future[Option[LegacyShift]]

  def insert(shift: LegacyShift): Future[Unit]

  def count: Future[Int]

  def findByIdWithTypeAndNumberOfPresences(id: Long): Future[Option[(LegacyShift, ShiftType, Int)]]

  def findByDate(date: LocalDate): Future[Seq[LegacyShift]]

  def findAllWithType: Future[Seq[(ShiftType, LegacyShift)]]

  def findAllWithTypeToday(today: LocalDate): Future[Seq[(ShiftType, LegacyShift)]]

  def findAllWithTypeAndNumberOfPresences: Future[Seq[(ShiftType, LegacyShift, Int)]]

  def findByIds(ids: Seq[Long]): Future[Seq[LegacyShift]]

  def findByDateAndType(date: LocalDate, shiftType: ShiftType): Future[Option[LegacyShift]]

  def delete(shift: LegacyShift): Future[Int]
}
