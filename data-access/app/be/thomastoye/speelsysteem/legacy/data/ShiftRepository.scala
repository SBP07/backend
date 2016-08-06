package be.thomastoye.speelsysteem.legacy.data
import be.thomastoye.speelsysteem.legacy.models.{Shift, ShiftType}
import org.joda.time.LocalDate

import scala.concurrent.Future

trait ShiftRepository {

  def findAll: Future[Seq[Shift]]

  def findById(id: Long): Future[Option[Shift]]

  def insert(shift: Shift): Future[Unit]

  def count: Future[Int]

  def findByIdWithTypeAndNumberOfPresences(id: Long): Future[Option[(Shift, ShiftType, Int)]]

  def findByDate(date: LocalDate): Future[Seq[Shift]]

  def findAllWithType: Future[Seq[(ShiftType, Shift)]]

  def findAllWithTypeToday(today: LocalDate): Future[Seq[(ShiftType, Shift)]]

  def findAllWithTypeAndNumberOfPresences: Future[Seq[(ShiftType, Shift, Int)]]

  def findByIds(ids: Seq[Long]): Future[Seq[Shift]]

  def findByDateAndType(date: LocalDate, shiftType: ShiftType): Future[Option[Shift]]

  def delete(shift: Shift): Future[Int]
}
