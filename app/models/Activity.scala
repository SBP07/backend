package models

import java.util.Date
import java.sql.{Date => SqlDate}
import play.api.db.slick.Config.driver.simple._

case class Activity(id: Option[Long] = None, date: Date, place: String, actNum: Long)
case class ActivityType(id: Option[Long], mnemonic: String, description: String)


class Activities(tag: Tag) extends Table[Activity](tag, "ACTIVITY") {
  implicit val dateColumnType = MappedColumnType.base[Date, Long](d => d.getTime, d => new Date(d))

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def date = column[Date]("DATE", O.Nullable)
  def place = column[String]("PLACE", O.Nullable)
  def actNum = column[Long]("ACT_TYPE_NUM", O.NotNull)

  def * = (id.?, date, place, actNum) <> (Activity.tupled, Activity.unapply)

  def activityType = foreignKey("FK_ACT_TYPE", actNum, TableQuery[ActivityTypes])(_.id)

  def testJoin = TableQuery[ActivityTypes].filter(_.id === actNum)
}

class ActivityTypes(tag: Tag) extends Table[ActivityType](tag, "ACTIVITY_TYPE") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def mnemonic = column[String]("MNEMONIC", O.NotNull)
  def description = column[String]("DESCRIPTION", O.NotNull)

  def * = (id.?, mnemonic, description) <> (ActivityType.tupled, ActivityType.unapply)
}

object Activities {
  val activities = TableQuery[Activities]

  def findAll(implicit s: Session) = activities.list
  def findById(id: Long)(implicit s: Session) = activities.filter(_.id === id)
  def insert(activity: Activity)(implicit s: Session) = activities.insert(activity)
  def count(implicit s: Session) = activities.length.run
}

object ActivityTypes {
  val types = TableQuery[ActivityTypes]

  def findAll(implicit s: Session) = types.list
  def findById(id: Long)(implicit s: Session) = types.filter(_.id === id)
  def insert(actType: ActivityType)(implicit s: Session) = types.insert(actType)
  def count(implicit s: Session) = types.length.run
}