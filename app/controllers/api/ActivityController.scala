package controllers.api

import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import dao.tenant.ActivityRepo
import models.Role
import models.helpers.GenericApiRequiredRoles
import models.tenant.{Activity, Crew}
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.json.{Writes, Reads}

class ActivityController @Inject()(
  dbConfigProvider: DatabaseConfigProvider,
  messagesApi: MessagesApi,
  env: Environment[Crew, JWTAuthenticator],
  socialProviderRegistry: SocialProviderRegistry,
  override val repo: ActivityRepo)
  extends GenericSecureApiController(dbConfigProvider, messagesApi, env, socialProviderRegistry)
{
  override type Id = UUID
  override type Model = Activity
  override type PersistedModel = Activity

  override val requiredRoles: GenericApiRequiredRoles = GenericApiRequiredRoles(Role.TenantAdmin, Role.TenantAdmin,
    Role.TenantAdmin, Role.TenantAdmin)

  override def convertToDisplayable: Activity => Activity = identity
  override def convertToPersistable: Activity => Activity = identity

  override implicit val reads: Reads[Model] = models.tenant.json.ActivityJson.activityReads
  override implicit val writes: Writes[Model] = models.tenant.json.ActivityJson.activityWrites

}
