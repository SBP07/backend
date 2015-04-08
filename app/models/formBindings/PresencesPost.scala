package models.formBindings

import models.{ChildPresence, Shift, Child}

case class PresencesPost(child: Option[Child], selectedShifts: List[Shift], possibleShifts: List[Shift])

object PresencesPost {
  /** Given a list of selected activities, a list of possible activities (from which the selected were picked) and
    * a list of activities already in the database, this method dictates which activities should be deleted.
    * When an activity was presented but not selected, it should be deleted
    * Effectively, (possible) difference (selected) intersect (already persisted) */
  def presencesToDelete(selected: List[Shift], possible: List[Shift],
                        alreadyPersisted: List[Shift]): List[Shift] = {
    (possible diff selected) intersect alreadyPersisted
  }

  /** Given a list of selected activities, a list of possible activities (from which the selected were picked) and
    * a list of activities already in the database, this method dictates which activities should be persisted.
    * Effectively, (selected) intersect (possible) difference (already persisted) */
  def presencesToInsert(selected: List[Shift], possible: List[Shift],
                        alreadyPersisted: List[Shift]): List[Shift] = {
    (selected intersect  possible) diff alreadyPersisted
  }
}
