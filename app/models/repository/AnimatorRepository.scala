package models.repository

import models.Animator
import play.api.db.slick.Config.driver.simple._

trait AnimatorRepository {
  def findById(id: Long)(implicit s: Session): Option[Animator]
  def findAll(implicit s: Session): List[Animator]
  def insert(animator: Animator)(implicit s: Session): Unit
  def count(implicit s: Session): Int
  def update(animator: Animator)(implicit s: Session): Unit
}
