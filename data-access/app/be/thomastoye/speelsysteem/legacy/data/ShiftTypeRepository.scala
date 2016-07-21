package be.thomastoye.speelsysteem.legacy.data
import be.thomastoye.speelsysteem.legacy.models.ShiftType

import scala.concurrent.Future

trait ShiftTypeRepository {

  def findAll: Future[Seq[ShiftType]]

  def findById(id: Long): Future[Option[ShiftType]]

  def findByMnemonic(mnemonic: String): Future[Option[ShiftType]]

  def insert(shiftType: ShiftType): Future[Unit]

  def count: Future[Int]
}
