package controllers.api

import java.util.UUID

import com.google.inject.Inject
import com.mohiva.play.silhouette.api.Environment
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import dao.RepoFor
import models.tenant.persistable.PersistableCrew
import models.tenant.{Crew, AuthCrewUser}
import play.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.json.{Writes, Reads}
import play.api.mvc._

class CrewController @Inject()(dbConfigProvider: DatabaseConfigProvider,
                               messagesApi: MessagesApi,
                               env: Environment[AuthCrewUser, JWTAuthenticator],
                               socialProviderRegistry: SocialProviderRegistry)
  extends GenericSecureApiController(dbConfigProvider, messagesApi, env, socialProviderRegistry)
{
  override type Id = UUID
  override type Model = Crew
  override type PersistedModel = PersistableCrew

  override def convertToPersistable = PersistableCrew.build
  override def convertToDisplayable = _.convert

  override val repo: RepoFor[PersistedModel, Id] = dao.tenant.CrewRepo

  override implicit val reads: Reads[Model] = models.tenant.json.CrewJson.crewReads
  override implicit val writes: Writes[Model] = models.tenant.json.CrewJson.crewWrites
}
