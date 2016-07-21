package be.thomastoye.speelsysteem.legacy.data

import be.thomastoye.speelsysteem.legacy.models.{Child, ChildPresence, Shift, ShiftType}

import scala.concurrent.Future

trait ChildPresenceRepository {
  def all: Future[Seq[(Child, Shift)]]
  def findAllForChild(id: Long): Future[Seq[(Shift, ShiftType)]]
  def findAllForShift(id: Long): Future[Seq[(Child, Shift)]]
  def register(presence: ChildPresence): Future[Unit]
  def register(presence: List[ChildPresence]): Future[Unit]
  def unregister(presence: ChildPresence): Future[Unit]
  def unregister(presence: List[ChildPresence]): Future[Unit]
  def count: Future[Int]
}
