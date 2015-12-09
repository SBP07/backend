package controllers.api

import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import dao.RepoFor
import models.Role
import models.admin.Tenant
import models.helpers.GenericApiRequiredRoles
import models.tenant.Crew
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.json._

class TenantController @Inject()(dbConfigProvider: DatabaseConfigProvider,
  messagesApi: MessagesApi,
  env: Environment[Crew, JWTAuthenticator],
  socialProviderRegistry: SocialProviderRegistry)
  extends GenericSecureApiController(dbConfigProvider, messagesApi, env, socialProviderRegistry)
{
  override type Id = UUID
  override type Model = Tenant
  override type PersistedModel = Tenant

  override val requiredRoles = GenericApiRequiredRoles(Role.NormalUser, Role.GlobalAdmin, Role.GlobalAdmin, Role.GlobalAdmin)

  override protected def convertToPersistable = identity
  override protected def convertToDisplayable = identity

  override val repo: RepoFor[Tenant, Id] = dao.admin.TenantRepo

  override implicit val reads: Reads[Model] = models.admin.json.TenantJson.tenantReads
  override implicit val writes: Writes[Model] = models.admin.json.TenantJson.tenantWrites
}
