package controllers.api

import com.mohiva.play.silhouette.api.{Silhouette, Environment}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import models.WithRole
import models.helpers.GenericApiRequiredRoles
import models.tenant.Crew
import org.postgresql.util.PSQLException
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.json.Json
import play.api.mvc.{AnyContent, Action}
import utils.JsonStatus

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

abstract class GenericSecureApiController(dbConfigProvider: DatabaseConfigProvider,
                                          messagesApi: MessagesApi,
                                          env: Environment[Crew, JWTAuthenticator],
                                          socialProviderRegistry: SocialProviderRegistry)
  extends GenericApiController(dbConfigProvider, messagesApi, env, socialProviderRegistry)
  with Silhouette[Crew, JWTAuthenticator]
{
  import dbConfig.driver.api._

  /** Roles required for the various actions */
  val requiredRoles: GenericApiRequiredRoles

  override def update: Action[Model] = SecuredAction(WithRole(requiredRoles.requiredToUpdate)).async(parse.json(reads)) {
    entity =>
      db.run(convertToPersistable(entity.body).update).map(updated => Ok(Json.toJson(convertToDisplayable(updated))))
  }

  override def delete(id: Id): Action[AnyContent] = SecuredAction(WithRole(requiredRoles.requiredToDelete)).async {
    db.run(repo.filterById(id).delete).map {
      i =>
        if (i == 0) NotFound else Ok
    }
  }

  override def getById(id: Id): Action[AnyContent] = SecuredAction(WithRole(requiredRoles.requiredToGet)).async {
    db.run(repo.findById(id)).map(entity => Ok(Json.toJson(convertToDisplayable(entity))))
  }

  override def getAll: Action[AnyContent] = SecuredAction(WithRole(requiredRoles.requiredToGet)).async {
    val all = db.run(repo.tableQuery.result)

    all.map(all => Ok(Json.toJson(all.map(convertToDisplayable))))
  }

  override def create: Action[Model] = SecuredAction(WithRole(requiredRoles.requiredToCreate)).async(parse.json(reads)) {
    parsed =>
      db.run(convertToPersistable(parsed.body).save.asTry).map {
        case Success(created) => Created(Json.toJson(convertToDisplayable(created)))
        case Failure(e: PSQLException) if e.getSQLState == "23505" => Conflict(
          JsonStatus.error("message" -> "Unique key violation: unique key already exists in the database.")
        )
        case Failure(t: PSQLException) => InternalServerError(
          JsonStatus.error("message" -> "PSQL error", "info" -> t.getServerErrorMessage.toString)
        )
      }

  }
}
