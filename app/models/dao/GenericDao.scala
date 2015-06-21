package models.dao

import scala.concurrent.Future

trait GenericDao[T] {
  def findById(id: Long): Future[Option[T]]
  def findAll: Future[Seq[T]]
  def insert(toInsert: T): Future[Int]
  def count: Future[Int]
  def update(toUpdate: T): Future[Int] // return the number of updated rows
}
