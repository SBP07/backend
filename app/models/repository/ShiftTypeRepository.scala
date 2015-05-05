package models.repository

import models._
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.ProvenShape


private[models] class ShiftTypeRepository(tag: Tag) extends Table[ShiftType](tag, "shift_type") {
  def * : ProvenShape[ShiftType] = (id.?, mnemonic, description) <>
    (ShiftType.tupled, ShiftType.unapply)

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  private[models] def mnemonic = column[String]("mnemonic", O.NotNull)

  private[models] def description = column[String]("description", O.NotNull)
}

object ShiftTypeRepository {
  val types = TableQuery[ShiftTypeRepository]

  def findByMnemonic(mnemonic: String)(implicit s: Session): Option[ShiftType] = {
    types.filter(_.mnemonic === mnemonic).firstOption
  }

  def insert(shiftType: ShiftType)(implicit s: Session): Unit = types.insert(shiftType)

  def count(implicit s: Session): Int = types.length.run
}
