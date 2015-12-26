package models.tenant

import java.time.Instant
import java.util.UUID

import play.api.libs.json.{Writes, Reads, Json}

/**
  * Case class that describes the relation between a child and an activity
  * An existing relationship means that the child was present on this activity
  * @param childId UUID identifying the child
  * @param activityId UUID identifying the activity
  * @param checkInTime When the child checked in at the playground
  * @param checkOutTime When the child checked out at the playground
  */
case class ChildToActivityRelationship(
  childId: UUID,
  activityId: UUID,
  checkInTime: Option[Instant],
  checkOutTime: Option[Instant]
)

object ChildToActivityRelationship extends ((UUID, UUID, Option[Instant], Option[Instant]) => ChildToActivityRelationship) {
  val jsonReads: Reads[ChildToActivityRelationship] = Json.reads[ChildToActivityRelationship]
  val jsonWrites: Writes[ChildToActivityRelationship] = Json.writes[ChildToActivityRelationship]
}
