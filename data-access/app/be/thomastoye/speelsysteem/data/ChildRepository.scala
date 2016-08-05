package be.thomastoye.speelsysteem.data

import be.thomastoye.speelsysteem.legacy.models.Child

import scala.concurrent.Future

trait ChildRepository {

  def findById(id: Long): Future[Option[Child]]

  def findAll: Future[Seq[Child]]

  def insert(child: Child): Future[Long]

  def count: Future[Int]

  def update(child: Child): Future[Unit]

  def findByFirstAndLastname(firstName: String, lastName: String): Future[Option[Child]]
}
