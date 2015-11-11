package controllers.api

import com.google.inject.Inject
import dao.RepoFor
import models.admin.Tenant
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._

class TenantController @Inject()(dbConfigProvider: DatabaseConfigProvider) extends GenericApiController(dbConfigProvider) {
  override type Id = Long
  override type Model = Tenant

  override val repo: RepoFor[Tenant, Id] = dao.admin.TenantRepo

  override implicit val reads: Reads[Model] = models.admin.json.TenantJson.tenantReads
  override implicit val writes: Writes[Model] = models.admin.json.TenantJson.tenantWrites
}
