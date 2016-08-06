package models.formBindings

import be.thomastoye.speelsysteem.legacy.models.Shift
import be.thomastoye.speelsysteem.models.Child

case class PresencesPost(child: Option[(Child.Id, Child)], selectedShifts: Seq[Shift], possibleShifts: Seq[Shift])

object PresencesPost {
  /** Given a list of selected activities, a list of possible activities (from which the selected were picked) and
    * a list of activities already in the database, this method dictates which activities should be deleted.
    * When an activity was presented but not selected, it should be deleted
    * Effectively, (possible) difference (selected) intersect (already persisted) */
  def presencesToDelete(selected: Seq[Shift], possible: Seq[Shift],
                        alreadyPersisted: Seq[Shift]): Seq[Shift] = {
    (possible diff selected) intersect alreadyPersisted
  }

  /** Given a list of selected activities, a list of possible activities (from which the selected were picked) and
    * a list of activities already in the database, this method dictates which activities should be persisted.
    * Effectively, (selected) intersect (possible) difference (already persisted) */
  def presencesToInsert(selected: Seq[Shift], possible: Seq[Shift],
                        alreadyPersisted: Seq[Shift]): Seq[Shift] = {
    (selected intersect  possible) diff alreadyPersisted
  }
}
