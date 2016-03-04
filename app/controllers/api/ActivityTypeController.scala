package controllers.api

import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import controllers.generic.GenericSecureApiController
import dao.tenant.ActivityTypeRepo
import models.Role
import models.helpers.GenericApiRequiredRoles
import models.tenant.{ActivityType, Crew}
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.json.{Writes, Reads}

class ActivityTypeController  @Inject()(dbConfigProvider: DatabaseConfigProvider,
  messagesApi: MessagesApi,
  env: Environment[Crew, JWTAuthenticator],
  socialProviderRegistry: SocialProviderRegistry,
  override val repo: ActivityTypeRepo)
  extends GenericSecureApiController(dbConfigProvider, messagesApi, env, socialProviderRegistry)
{
  override type Id = UUID
  override type Model = ActivityType
  override type PersistedModel = ActivityType

  override val requiredRoles: GenericApiRequiredRoles = GenericApiRequiredRoles(Role.TenantAdmin, Role.TenantAdmin,
    Role.TenantAdmin, Role.TenantAdmin)

  override def convertToDisplayable: ActivityType => ActivityType = identity
  override def convertToPersistable: ActivityType => ActivityType = identity

  override implicit val reads: Reads[Model] = models.tenant.json.ActivityTypeJson.activityTypeReads
  override implicit val writes: Writes[Model] = models.tenant.json.ActivityTypeJson.activityTypeWrites

}
