package be.thomastoye.speelsysteem.legacy.data
import be.thomastoye.speelsysteem.legacy.models.Animator

import scala.concurrent.Future

trait AnimatorRepository {

  def findById(id: Long): Future[Option[Animator]]

  def findAll: Future[Seq[Animator]]

  def insert(animator: Animator): Future[Unit]

  def count: Future[Int]

  def update(animator: Animator): Future[Unit]
}
