package controllers.api

import com.google.inject.Inject
import dao.RepoFor
import models.organisation.Address
import play.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._

class AddressController @Inject()(dbConfigProvider: DatabaseConfigProvider) extends GenericApiController(dbConfigProvider) {
  override type Id = Long
  override type Model = Address

  override val repo: RepoFor[Address, Id] = dao.organisation.AddressRepo

  override implicit val reads: Reads[Model] = models.organisation.json.AddressJson.addressReads
  override implicit val writes: Writes[Model] = models.organisation.json.AddressJson.addressWrites
}
