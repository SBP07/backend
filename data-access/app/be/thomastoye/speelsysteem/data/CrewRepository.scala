package be.thomastoye.speelsysteem.data

import be.thomastoye.speelsysteem.models.Crew

import scala.concurrent.Future

trait CrewRepository {

  def findById(id: Crew.Id): Future[Option[(Crew.Id, Crew)]]

  def findAll: Future[Seq[(Crew.Id, Crew)]]

  def insert(crewMember: Crew): Future[Unit] // TODO return Future[Crew.Id]

  def count: Future[Int]

  def update(id: Crew.Id, crewMember: Crew): Future[Unit]
}
