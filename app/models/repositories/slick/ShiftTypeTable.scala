package models.repositories.slick

import javax.inject.Inject

import models.ShiftType
import play.api.libs.concurrent.Execution.Implicits._
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.ProvenShape

import scala.concurrent.Future

class ShiftTypeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  val types = TableQuery[ShiftTypeTable]

  def findAll: Future[Seq[ShiftType]] = db.run(types.result)
  def findById(id: Long): Future[Option[ShiftType]] = db.run(types.filter(_.id === id).result.headOption)
  def findByMnemonic(mnemonic: String): Future[Option[ShiftType]] = {
    db.run(types.filter(_.mnemonic === mnemonic).result.headOption)
  }
  def insert(shiftType: ShiftType): Future[Unit] = db.run(types += shiftType).map(_ => ())
  def count: Future[Int] = db.run(types.length.result)
}

private[models] class ShiftTypeTable(tag: Tag) extends Table[ShiftType](tag, "shift_type") {
  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private[models] def mnemonic = column[String]("mnemonic")
  private[models] def description = column[String]("description")

  def * : ProvenShape[ShiftType] = (id.?, mnemonic, description) <>
    (ShiftType.tupled, ShiftType.unapply)
}