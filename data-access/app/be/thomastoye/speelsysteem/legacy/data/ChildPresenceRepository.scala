package be.thomastoye.speelsysteem.legacy.data

import be.thomastoye.speelsysteem.legacy.models.{ChildPresence, LegacyChild, LegacyShift, ShiftType}
import be.thomastoye.speelsysteem.models.Child

import scala.concurrent.Future

trait ChildPresenceRepository {
  def all: Future[Seq[(LegacyChild, LegacyShift)]]
  def findAllForChild(id: Child.Id): Future[Seq[(LegacyShift, ShiftType)]]
  def findAllForShift(id: Long): Future[Seq[(LegacyChild, LegacyShift)]]
  def register(presence: ChildPresence): Future[Unit]
  def register(presence: Seq[ChildPresence]): Future[Unit]
  def unregister(presence: ChildPresence): Future[Unit]
  def unregister(presence: Seq[ChildPresence]): Future[Unit]
  def count: Future[Int]
}
