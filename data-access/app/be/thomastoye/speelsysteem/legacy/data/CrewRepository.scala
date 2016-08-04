package be.thomastoye.speelsysteem.legacy.data
import be.thomastoye.speelsysteem.legacy.models.LegacyCrew

import scala.concurrent.Future

trait CrewRepository {

  def findById(id: String): Future[Option[LegacyCrew]]

  def findAll: Future[Seq[LegacyCrew]]

  def insert(animator: LegacyCrew): Future[Unit]

  def count: Future[Int]

  def update(animator: LegacyCrew): Future[Unit]
}
