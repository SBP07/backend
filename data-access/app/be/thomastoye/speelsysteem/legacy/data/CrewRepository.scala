package be.thomastoye.speelsysteem.legacy.data
import be.thomastoye.speelsysteem.models.Crew

import scala.concurrent.Future

trait CrewRepository {

  def findById(id: Crew.Id): Future[Option[(Crew.Id, Crew)]]

  def findAll: Future[Seq[(Crew.Id, Crew)]]

  def insert(crewMember: Crew): Future[Unit]

  def count: Future[Int]

  def update(id: Crew.Id, crewMember: Crew): Future[Unit]
}
