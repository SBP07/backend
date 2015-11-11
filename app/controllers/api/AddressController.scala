package controllers.api

import java.util.UUID

import com.google.inject.Inject
import dao.RepoFor
import models.tenant.Address
import play.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._

class AddressController @Inject()(dbConfigProvider: DatabaseConfigProvider) extends GenericApiController(dbConfigProvider) {
  override type Id = UUID
  override type Model = Address

  override val repo: RepoFor[Address, Id] = dao.tenant.AddressRepo

  override implicit val reads: Reads[Model] = models.tenant.json.AddressJson.addressReads
  override implicit val writes: Writes[Model] = models.tenant.json.AddressJson.addressWrites
}
