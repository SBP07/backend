package controllers.api

import com.google.inject.Inject
import dao.RepoFor
import models.admin.Organisation
import models.organisation.Address
import play.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._

class OrganisationController @Inject()(dbConfigProvider: DatabaseConfigProvider) extends GenericApiController(dbConfigProvider) {
  override type Id = Long
  override type Model = Organisation

  override val repo: RepoFor[Organisation, Id] = dao.admin.OrganisationRepo

  override implicit val reads: Reads[Model] = models.admin.json.OrganisationJson.addressReads
  override implicit val writes: Writes[Model] = models.admin.json.OrganisationJson.addressWrites
}
