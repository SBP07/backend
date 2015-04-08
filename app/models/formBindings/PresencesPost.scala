package models.formBindings

import models.{ChildPresence, Shift, Child}

case class PresencesPost(child: Option[Child], selectedActivities: List[Shift], possibleActivities: List[Shift])

object PresencesPost {
  /** Given a list of selected activities, a list of possible activities (from which the selected were picked) and
    * a list of activities already in the database, this method dictates which activities should be deleted.
    * When an activity was presented but not selected, it should be deleted
    * Effectively, (possible) difference (selected) intersect (already persisted) */
  def presencesToDelete(selectedActivities: List[Shift], possibleActivities: List[Shift],
                        alreadyPersisted: List[Shift]): List[Shift] = {
    (possibleActivities diff selectedActivities) intersect alreadyPersisted
  }

  /** Given a list of selected activities, a list of possible activities (from which the selected were picked) and
    * a list of activities already in the database, this method dictates which activities should be persisted.
    * Effectively, (selected) intersect (possible) difference (already persisted) */
  def presencesToInsert(selectedActivities: List[Shift], possibleActivities: List[Shift],
                        alreadyPersisted: List[Shift]): List[Shift] = {
    (selectedActivities intersect  possibleActivities) diff alreadyPersisted
  }
}
