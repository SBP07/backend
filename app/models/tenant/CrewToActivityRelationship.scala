package models.tenant

import java.time.Instant
import java.util.UUID

import play.api.libs.json.{Writes, Reads, Json}

/**
  * Case class that describes the relation between a crew member and an activity
  * An existing relationship means that the crew member was present on this activity
  * @param crewId UUID identifying the crew member
  * @param activityId UUID identifying the activity
  * @param checkInTime When the crew member checked in at the playground
  * @param checkOutTime When the crew member checked out at the playground
  */
case class CrewToActivityRelationship(
  crewId: UUID,
  activityId: UUID,
  checkInTime: Option[Instant],
  checkOutTime: Option[Instant]
)

object CrewToActivityRelationship extends ((UUID, UUID, Option[Instant], Option[Instant]) => CrewToActivityRelationship) {
  val jsonReads: Reads[CrewToActivityRelationship] = Json.reads[CrewToActivityRelationship]
  val jsonWrites: Writes[CrewToActivityRelationship] = Json.writes[CrewToActivityRelationship]
}
