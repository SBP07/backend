package controllers.generic

import java.util.NoSuchElementException

import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import io.strongtyped.active.slick.exceptions.{ActiveSlickException, RowNotFoundException}
import models.WithRole
import models.helpers.{BelongsToTenant, GenericApiRequiredRoles, TenantEntityActions}
import models.tenant.Crew
import org.postgresql.util.PSQLException
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc.{Action, AnyContent}
import slick.driver.JdbcProfile
import utils.JsonStatus

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

abstract class GenericSecureApiController(val dbConfigProvider: DatabaseConfigProvider,
                                          val messagesApi: MessagesApi,
                                          val env: Environment[Crew, JWTAuthenticator],
                                          val socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[Crew, JWTAuthenticator]
{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db

  def getTenantCanonicalNameFromModelRequest(implicit securedRequest: SecuredRequest[Model]): String = securedRequest.identity.tenantCanonicalName
  def getTenantCanonicalNameFromAnyContentRequest(implicit securedRequest: SecuredRequest[AnyContent]): String = securedRequest.identity.tenantCanonicalName

  implicit val reads: Reads[Model]
  implicit val writes: Writes[Model]

  type Id
  type Model
  type PersistedModel <: BelongsToTenant[PersistedModel]

  protected def convertToPersistable: Model => PersistedModel
  protected def convertToDisplayable: PersistedModel => Model

  val repo: TenantEntityActions[PersistedModel, Id]

  /** Roles required for the various actions */
  val requiredRoles: GenericApiRequiredRoles

  def update: Action[Model] = SecuredAction(WithRole(requiredRoles.requiredToUpdate)).async(parse.json(reads)) { implicit req =>
      db.run(repo.update(getTenantCanonicalNameFromModelRequest, convertToPersistable(req.body)))
      .map(updated => Ok(Json.toJson(convertToDisplayable(updated))))
        .recover(_ match {case e: RowNotFoundException[_] => NotFound(JsonStatus.error("message" -> "Not found")) })
  }

  def delete(id: Id): Action[AnyContent] = SecuredAction(WithRole(requiredRoles.requiredToDelete)).async { implicit req =>
    db.run(repo.deleteById(getTenantCanonicalNameFromAnyContentRequest, id)).map { i =>
      Ok(JsonStatus.success())
    }.recover(_ match {
      case e: PSQLException => BadRequest(JsonStatus.error("message" -> "Database exception", "details" -> e.getServerErrorMessage.toString))
      case e: ActiveSlickException => NotFound(
        JsonStatus.error("message" -> s"No child found with id $id, or child does not belong to the current tenant")
      )
    })

  }

  def getById(id: Id): Action[AnyContent] = SecuredAction(WithRole(requiredRoles.requiredToGet)).async { implicit req =>
    db.run(repo.findById(getTenantCanonicalNameFromAnyContentRequest, id))
      .map(entity => Ok(Json.toJson(convertToDisplayable(entity))))
      .recover(_ match {case e: NoSuchElementException => NotFound(JsonStatus.error("message" -> "Not found")) })
  }

  def getAll: Action[AnyContent] = SecuredAction(WithRole(requiredRoles.requiredToGet)).async { implicit req =>
    val all: Future[Seq[PersistedModel]] = db.run(repo.fetchAll(getTenantCanonicalNameFromAnyContentRequest, 100))

    all.map(all => Ok(Json.toJson(all.map(convertToDisplayable))))
  }

  def create: Action[Model] = SecuredAction(WithRole(requiredRoles.requiredToCreate)).async(parse.json(reads)) {
    implicit parsed =>
      db.run(repo.save(getTenantCanonicalNameFromModelRequest, convertToPersistable(parsed.body)).asTry).map {
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
