package be.thomastoye.speelsysteem.legacy.data.slick

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.data.ShiftTypeRepository
import be.thomastoye.speelsysteem.legacy.models.ShiftType
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.concurrent.Execution.Implicits._
import slick.driver.JdbcProfile
import slick.driver.PostgresDriver.api._
import slick.lifted.ProvenShape

import scala.concurrent.Future

class SlickShiftTypeRepository @Inject()(dbConfigProvider: DatabaseConfigProvider) extends ShiftTypeRepository {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  val types = TableQuery[ShiftTypeTable]

  override def findAll: Future[Seq[ShiftType]] = db.run(types.result)
  override def findById(id: Long): Future[Option[ShiftType]] = db.run(types.filter(_.id === id).result.headOption)
  override def findByMnemonic(mnemonic: String): Future[Option[ShiftType]] = {
    db.run(types.filter(_.mnemonic === mnemonic).result.headOption)
  }
  override def insert(shiftType: ShiftType): Future[Unit] = db.run(types += shiftType).map(_ => ())
  override def count: Future[Int] = db.run(types.length.result)
}

class ShiftTypeTable(tag: Tag) extends Table[ShiftType](tag, "shift_type") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def mnemonic = column[String]("mnemonic")
  def description = column[String]("description")

  def * : ProvenShape[ShiftType] = (id.?, mnemonic, description) <>
    (ShiftType.tupled, ShiftType.unapply)
}
