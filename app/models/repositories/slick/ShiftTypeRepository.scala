package models.repositories.slick

import models.ShiftType
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ForeignKeyQuery, ProvenShape}

object ShiftTypeRepository {
  val types = TableQuery[ShiftTypeRepository]

  def findAll(implicit s: Session): List[ShiftType] = types.list
  def findById(id: Long)(implicit s: Session): Option[ShiftType] = types.filter(_.id === id).firstOption
  def findByMnemonic(mnemonic: String)(implicit s: Session): Option[ShiftType] = {
    types.filter(_.mnemonic === mnemonic).firstOption
  }
  def insert(shiftType: ShiftType)(implicit s: Session): Unit = types.insert(shiftType)
  def count(implicit s: Session): Int = types.length.run
}

private[models] class ShiftTypeRepository(tag: Tag) extends Table[ShiftType](tag, "shift_type") {
  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private[models] def mnemonic = column[String]("mnemonic", O.NotNull)
  private[models] def description = column[String]("description", O.NotNull)

  def * : ProvenShape[ShiftType] = (id.?, mnemonic, description) <>
    (ShiftType.tupled, ShiftType.unapply)
}