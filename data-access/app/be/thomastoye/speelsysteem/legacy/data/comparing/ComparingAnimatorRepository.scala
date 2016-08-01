package be.thomastoye.speelsysteem.legacy.data.comparing

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.data.AnimatorRepository
import be.thomastoye.speelsysteem.legacy.data.couchdb.CouchAnimatorRepository
import be.thomastoye.speelsysteem.legacy.data.slick.SlickAnimatorRepository
import be.thomastoye.speelsysteem.legacy.models.Animator

import scala.concurrent.Future

class ComparingAnimatorRepository @Inject() (couchAnimatorRepository: CouchAnimatorRepository,
  slickAnimatorRepository: SlickAnimatorRepository)
extends AnimatorRepository with ComparingRepository
{
  override def findById(id: Long): Future[Option[Animator]] = doCompare("AnimatorRepository#findById", slickAnimatorRepository.findById(id), couchAnimatorRepository.findById(id))

  override def findAll: Future[Seq[Animator]] = doCompare("AnimatorRepository#findAll", slickAnimatorRepository.findAll, couchAnimatorRepository.findAll)

  override def insert(animator: Animator): Future[Unit] = doCompare("AnimatorRepository#insert", slickAnimatorRepository.insert(animator), couchAnimatorRepository.insert(animator))

  override def count: Future[Int] = doCompare("AnimatorRepository#count", slickAnimatorRepository.count, couchAnimatorRepository.count)

  override def update(animator: Animator): Future[Unit] = ???
}
