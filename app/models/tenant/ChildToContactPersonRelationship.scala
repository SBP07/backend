package models.tenant

import java.util.UUID

import play.api.libs.json.{Reads, Json}

/**
  * Case class that describes the relation between a child and a contact person
  * @param childId UUID identifying the child
  * @param contactPersonId UUID identifying the contact person
  * @param relationship Type of the relationship between the two, e.g. parent, grandparent, legal guardian...
  */
case class ChildToContactPersonRelationship(childId: UUID, contactPersonId: UUID, relationship: String)

object ChildToContactPersonRelationship extends ((UUID, UUID, String) => ChildToContactPersonRelationship) {
  val jsonReads: Reads[ChildToContactPersonRelationship] = Json.reads[ChildToContactPersonRelationship]
}
