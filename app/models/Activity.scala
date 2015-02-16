package models

import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

case class Activity(id: Option[Long] = None, date: LocalDate, place: String, actNum: Long)
case class ActivityType(id: Option[Long], mnemonic: String, description: String)


class Activities(tag: Tag) extends Table[Activity](tag, "ACTIVITY") {
  import helpers.Db.jodaDatetimeToSqldateMapper

  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def date = column[LocalDate]("DATE", O.Nullable)
  def place = column[String]("PLACE", O.Nullable)
  def actNum = column[Long]("ACT_TYPE_NUM", O.NotNull)

  def * = (id.?, date, place, actNum) <> (Activity.tupled, Activity.unapply)

  def activityType = foreignKey("FK_ACT_TYPE", actNum, TableQuery[ActivityTypes])(_.id)
  def activityTypeJoin = TableQuery[ActivityTypes].filter(_.id === actNum)

  def children = TableQuery[ChildrenToActivities].filter(_.childId === id).flatMap(_.childFK)
}

class ActivityTypes(tag: Tag) extends Table[ActivityType](tag, "ACTIVITY_TYPE") {
  def id = column[Long]("ID", O.PrimaryKey, O.AutoInc)
  def mnemonic = column[String]("MNEMONIC", O.NotNull)
  def description = column[String]("DESCRIPTION", O.NotNull)

  def * = (id.?, mnemonic, description) <> (ActivityType.tupled, ActivityType.unapply)
}

object Activities {
  import helpers.Db.jodaDatetimeToSqldateMapper

  val activities = TableQuery[Activities]

  def findAll(implicit s: Session): List[Activity] = activities.list
  def findById(id: Long)(implicit s: Session) = activities.filter(_.id === id).run
  def insert(activity: Activity)(implicit s: Session) = activities.insert(activity)
  def count(implicit s: Session) = activities.length.run

  def findByDate(date: LocalDate)(implicit s: Session) = activities.filter(_.date === date).run

  def findAllWithType(implicit s: Session): Seq[(ActivityType, Activity)] = (for {
    act <- activities
    t <- act.activityTypeJoin} yield {
    (t, act)
  }).run

  def findByIds(ids: List[Long])(implicit s: Session): Seq[Activity] = activities.filter(_.id inSet ids).run
}

object ActivityTypes {
  val types = TableQuery[ActivityTypes]

  def findAll(implicit s: Session): List[ActivityType] = types.list
  def findById(id: Long)(implicit s: Session): Option[ActivityType] = types.filter(_.id === id).firstOption
  def findByMnemonic(mnemonic: String)(implicit s: Session): Option[ActivityType] = types.filter(_.mnemonic === mnemonic).firstOption
  def insert(actType: ActivityType)(implicit s: Session) = types.insert(actType)
  def count(implicit s: Session): Int = types.length.run
}
