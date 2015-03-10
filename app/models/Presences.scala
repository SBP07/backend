package models

import play.api.db.slick.Config.driver.simple._

import scala.slick.lifted.{ProvenShape, ForeignKeyQuery}

case class ChildPresence(childId: Long, activityId: Long)

private[models] class ChildrenToActivities(tag: Tag) extends Table[ChildPresence](tag, "child_to_activity") {
  private[models] val children = TableQuery[Children]
  private[models] val activities = TableQuery[Activities]

  private[models] def childId = column[Long]("child_id")
  private[models] def activityId = column[Long]("activity_id")

  def * : ProvenShape[ChildPresence] = (childId, activityId) <> (ChildPresence.tupled, ChildPresence.unapply _)

  def childFK: ForeignKeyQuery[Children, Child] = foreignKey("child_fk", childId, children)(child => child.id)
  def activityFK: ForeignKeyQuery[Activities, Activity] = foreignKey("activity_fk",
    activityId, activities)(act => act.id)

  private[models] def pk = primaryKey("child_to_activity_pk", (childId, activityId))
}

object ChildPresences {
  private val children = TableQuery[Children]
  private val activities = TableQuery[Activities]
  private val presences = TableQuery[ChildrenToActivities]

  def all(implicit s: Session): Seq[(Child, Activity)] = (for {
    child <- children
    act <- child.activities
  } yield (child, act)).run

  def findAllForChild(id: Long)(implicit s: Session): Seq[(Activity, ActivityType)] = (for {
    child <- children if child.id === id
    act <- child.activities
    actType <- act.activityType
  } yield (act, actType)).run

  def findAllForActivity(id: Long)(implicit s: Session): Seq[(Child, Activity)] = (for {
    child <- children
    act <- child.activities if act.id === id
  } yield (child, act)).run

  def register(childToActivity: ChildPresence)(implicit s: Session): Unit = presences += childToActivity
  def register(childToActivity: List[ChildPresence])(implicit s: Session): Unit = presences ++= childToActivity

  def unregister(childToActivity: ChildPresence)(implicit s: Session): Unit =
    presences.filter(_.activityId === childToActivity.activityId)
              .filter(_.childId === childToActivity.childId)
              .delete
              .run

  def unregister(childToActivity: List[ChildPresence])(implicit s: Session): Unit =
    presences.filter(_.activityId inSet childToActivity.map(_.activityId))
              .filter(_.childId inSet childToActivity.map(_.childId))
              .delete
              .run

  def count(implicit s: Session): Int = presences.length.run
}
