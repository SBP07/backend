package controllers.api

import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import controllers.generic.GenericSecureApiController
import dao.tenant.ContactPersonRepo
import models.helpers.GenericApiRequiredRoles
import models.Role
import models.tenant.persistable.PersistableContactPerson
import models.tenant.{ContactPerson, Crew}
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.json._

class ContactPersonController @Inject()(dbConfigProvider: DatabaseConfigProvider,
  messagesApi: MessagesApi,
  env: Environment[Crew, JWTAuthenticator],
  socialProviderRegistry: SocialProviderRegistry,
  override val repo: ContactPersonRepo)
  extends GenericSecureApiController(dbConfigProvider, messagesApi, env, socialProviderRegistry)
{
  override type Id = UUID
  override type Model = ContactPerson
  override type PersistedModel = PersistableContactPerson

  override val requiredRoles: GenericApiRequiredRoles = GenericApiRequiredRoles(Role.NormalUser, Role.NormalUser,
    Role.NormalUser, Role.TenantAdmin)
  override protected def convertToDisplayable: PersistedModel => Model = _.convert
  override protected def convertToPersistable: Model => PersistedModel = PersistableContactPerson.build

  override implicit val reads: Reads[Model] = models.tenant.json.ContactPersonJson.contactPersonReads
  override implicit val writes: Writes[Model] = models.tenant.json.ContactPersonJson.contactPersonWrites

}
