package models

import play.api.db.slick.Config.driver.simple._

case class ChildPresence(childId: Long, activityId: Long)

class ChildrenToActivities(tag: Tag) extends Table[ChildPresence](tag, "child_to_activity") {
  val children = TableQuery[Children]
  val activities = TableQuery[Activities]

  def childId = column[Long]("child_id")
  def activityId = column[Long]("activity_id")

  def * = (childId, activityId) <> (ChildPresence.tupled, ChildPresence.unapply _)

  def childFK = foreignKey("child_fk", childId, children)(child => child.id)
  def activityFK = foreignKey("activity_fk", activityId, activities)(act => act.id)

  def pk = primaryKey("child_to_activity_pk", (childId, activityId))
}

object ChildPresences {
  val children = TableQuery[Children]
  val activities = TableQuery[Activities]
  val presences = TableQuery[ChildrenToActivities]

  def all(implicit s: Session): Seq[(Child, Activity)] = (for {
    child <- children
    act <- child.activities
  } yield (child, act)).run

  def findAllForChild(id: Long)(implicit s: Session) = (for {
    child <- children if child.id === id
    act <- child.activities
    actType <- act.activityType
  } yield (act, actType)).run

  def findAllForActivity(id: Long)(implicit s: Session) = (for {
    child <- children
    act <- child.activities if act.id === id
  } yield (child, act)).run

  def register(childToActivity: ChildPresence)(implicit s: Session) = presences += childToActivity
  def register(childToActivity: List[ChildPresence])(implicit s: Session) = presences ++= childToActivity

  def unregister(childToActivity: ChildPresence)(implicit s: Session) = presences
                                                                            .filter(_.activityId === childToActivity.activityId)
                                                                            .filter(_.childId === childToActivity.childId)
                                                                            .delete
                                                                            .run

  def unregister(childToActivity: List[ChildPresence])(implicit s: Session) = presences
                                                                                .filter(_.activityId inSet childToActivity.map(_.activityId))
                                                                                .filter(_.childId inSet childToActivity.map(_.childId))
                                                                                .delete
                                                                                .run

  def count(implicit s: Session) = presences.length.run
}
