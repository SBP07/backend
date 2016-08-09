package models.formBindings

import be.thomastoye.speelsysteem.legacy.models.LegacyShift
import be.thomastoye.speelsysteem.models.Child

case class PresencesPost(child: Option[(Child.Id, Child)], selectedShifts: Seq[LegacyShift], possibleShifts: Seq[LegacyShift])

object PresencesPost {
  /** Given a list of selected activities, a list of possible activities (from which the selected were picked) and
    * a list of activities already in the database, this method dictates which activities should be deleted.
    * When an activity was presented but not selected, it should be deleted
    * Effectively, (possible) difference (selected) intersect (already persisted) */
  def presencesToDelete(selected: Seq[LegacyShift], possible: Seq[LegacyShift],
                        alreadyPersisted: Seq[LegacyShift]): Seq[LegacyShift] = {
    (possible diff selected) intersect alreadyPersisted
  }

  /** Given a list of selected activities, a list of possible activities (from which the selected were picked) and
    * a list of activities already in the database, this method dictates which activities should be persisted.
    * Effectively, (selected) intersect (possible) difference (already persisted) */
  def presencesToInsert(selected: Seq[LegacyShift], possible: Seq[LegacyShift],
                        alreadyPersisted: Seq[LegacyShift]): Seq[LegacyShift] = {
    (selected intersect  possible) diff alreadyPersisted
  }
}
