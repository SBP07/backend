package be.thomastoye.speelsysteem.data

import be.thomastoye.speelsysteem.models.Child
import be.thomastoye.speelsysteem.models.Child.Id

import scala.concurrent.Future

trait ChildRepository {

  def findById(id: Id): Future[Option[(Id, Child)]]

  def findAll: Future[Seq[(Id, Child)]]

  def insert(child: Child): Future[Id]

  def count: Future[Int]

  def update(id: Id, child: Child): Future[Unit]

  def findByFirstAndLastname(firstName: String, lastName: String): Future[Option[(Id, Child)]]
}
