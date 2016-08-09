package be.thomastoye.speelsysteem.legacy.data.comparing

import javax.inject.Inject

import be.thomastoye.speelsysteem.data.ChildRepository
import be.thomastoye.speelsysteem.data.couchdb.CouchChildRepository
import be.thomastoye.speelsysteem.legacy.data.slick.SlickChildRepository
import be.thomastoye.speelsysteem.models.Child
import be.thomastoye.speelsysteem.models.Child.Id

import scala.concurrent.Future

class ComparingChildRepository @Inject() (couchChildRepository: CouchChildRepository, slickChildRepository: SlickChildRepository)
  extends ChildRepository with ComparingRepository
{
  override def findById(id: Id): Future[Option[(Id, Child)]] = doCompare(slickChildRepository.findById(id), couchChildRepository.findById(id))

  override def findAll: Future[Seq[(Id, Child)]] = doCompare(slickChildRepository.findAll, couchChildRepository.findAll, Some("Child"))

  override def insert(id: Id, child: Child): Future[Id] = doCompare(slickChildRepository.insert(id, child), couchChildRepository.insert(id, child))

  override def count: Future[Int] = doCompare(slickChildRepository.count, couchChildRepository.count)

  override def update(id: Id, child: Child): Future[Unit] = doCompare(slickChildRepository.update(id, child), couchChildRepository.update(id, child))
}
