package controllers.api

import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import dao.RepoFor
import models.tenant.{Crew, Child}
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.json._

class ChildController @Inject()(dbConfigProvider: DatabaseConfigProvider,
  messagesApi: MessagesApi,
  env: Environment[Crew, JWTAuthenticator],
  socialProviderRegistry: SocialProviderRegistry)
  extends GenericSecureApiController(dbConfigProvider, messagesApi, env, socialProviderRegistry)
{
  override type Id = UUID
  override type Model = Child
  override type PersistedModel = Child

  override val repo: RepoFor[Child, Id] = dao.tenant.ChildRepo

  override def convertToDisplayable: Child => Child = identity

  override def convertToPersistable: Child => Child = identity

  override implicit val reads: Reads[Model] = models.tenant.json.ChildJson.childReads
  override implicit val writes: Writes[Model] = models.tenant.json.ChildJson.childWrites

}
