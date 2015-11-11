package models.admin.json

import models.admin.Organisation
import play.api.libs.json._
import models.organisation._

object OrganisationJson {
  implicit val addressReads: Reads[Organisation] = Json.reads[Organisation]
  implicit val addressWrites: Writes[Organisation] = Json.writes[Organisation]
}
