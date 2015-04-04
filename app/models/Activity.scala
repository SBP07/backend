package models

import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ForeignKeyQuery, ProvenShape}

case class Activity(id: Option[Long] = None, date: LocalDate, place: String, actNum: Long)
case class ActivityType(id: Option[Long], mnemonic: String, description: String)


private[models] class Activities(tag: Tag) extends Table[Activity](tag, "activity") {
  import helpers.Db.jodaDatetimeToSqldateMapper

  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private[models] def date = column[LocalDate]("date", O.Nullable)
  private[models] def place = column[String]("place", O.Nullable)
  private[models] def actNum = column[Long]("activity_type", O.NotNull)

  def * : ProvenShape[Activity] = (id.?, date, place, actNum) <> (Activity.tupled, Activity.unapply)

  def activityType: ForeignKeyQuery[ActivityTypes, ActivityType] = {
    foreignKey("fk_act_type", actNum, TableQuery[ActivityTypes])(_.id)
  }
  def activityTypeJoin: Query[ActivityTypes, ActivityTypes#TableElementType, Seq] = {
    TableQuery[ActivityTypes].filter(_.id === actNum)
  }

  def children: Query[Children, Child, Seq] = {
    TableQuery[ChildrenToActivities].filter(_.activityId === id).flatMap(_.childFK)
  }
}

private[models] class ActivityTypes(tag: Tag) extends Table[ActivityType](tag, "activity_type") {
  private[models] def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  private[models] def mnemonic = column[String]("mnemonic", O.NotNull)
  private[models] def description = column[String]("description", O.NotNull)

  def * : ProvenShape[ActivityType] = (id.?, mnemonic, description) <>
    (ActivityType.tupled, ActivityType.unapply)
}

object Activities {
  import helpers.Db.jodaDatetimeToSqldateMapper

  val activities = TableQuery[Activities]

  def findAll(implicit s: Session): List[Activity] = activities.sortBy(_.date).list
  def findById(id: Long)(implicit s: Session): Seq[Activity] = activities.filter(_.id === id).run
  def insert(activity: Activity)(implicit s: Session): Unit = activities.insert(activity)
  def count(implicit s: Session): Int = activities.length.run

  def findByDate(date: LocalDate)(implicit s: Session): Seq[Activity] = activities.filter(_.date === date).run

  def findAllWithType(implicit s: Session): Seq[(ActivityType, Activity)] = (for {
    act <- activities.sortBy(_.date)
    t <- act.activityTypeJoin.sortBy(_.id)
  } yield {
    (t, act)
  }).run

  def findByIds(ids: List[Long])(implicit s: Session): Seq[Activity] = activities.filter(_.id inSet ids).run
  def findByDateAndType(date: LocalDate, actType: ActivityType)(implicit s: Session): Option[Activity] = {
    actType.id.flatMap { actType =>
      activities.filter(_.actNum === actType).filter(_.date === date).firstOption
    }
  }
}

object ActivityTypes {
  val types = TableQuery[ActivityTypes]

  def findAll(implicit s: Session): List[ActivityType] = types.list
  def findById(id: Long)(implicit s: Session): Option[ActivityType] = types.filter(_.id === id).firstOption
  def findByMnemonic(mnemonic: String)(implicit s: Session): Option[ActivityType] = {
    types.filter(_.mnemonic === mnemonic).firstOption
  }
  def insert(actType: ActivityType)(implicit s: Session): Unit = types.insert(actType)
  def count(implicit s: Session): Int = types.length.run
}
