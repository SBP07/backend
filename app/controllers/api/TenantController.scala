package controllers.api

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Silhouette, Environment}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import models.{Role, WithRole}
import models.admin.Tenant
import models.helpers.GenericApiRequiredRoles
import models.tenant.Crew
import org.postgresql.util.PSQLException
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.json.{Writes, Reads, Json}
import play.api.mvc.{AnyContent, Action}
import slick.driver.JdbcProfile
import utils.JsonStatus

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class TenantController @Inject()(dbConfigProvider: DatabaseConfigProvider,
  val messagesApi: MessagesApi,
  val env: Environment[Crew, JWTAuthenticator],
  val socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[Crew, JWTAuthenticator]
{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val db = dbConfig.db
  import dbConfig.driver.api._

  type Id = UUID
  type Model = Tenant

  val requiredRoles = GenericApiRequiredRoles(Role.NormalUser, Role.GlobalAdmin, Role.GlobalAdmin, Role.GlobalAdmin)

  val repo = dao.admin.TenantRepo

  implicit val reads: Reads[Model] = models.admin.json.TenantJson.tenantReads
  implicit val writes: Writes[Model] = models.admin.json.TenantJson.tenantWrites

  def update: Action[Model] = SecuredAction(WithRole(requiredRoles.requiredToUpdate)).async(parse.json(reads)) { implicit req =>
    db.run(repo.update(req.body))
      .map(updated => Ok(Json.toJson(updated)))
  }

  def delete(id: Id): Action[AnyContent] = SecuredAction(WithRole(requiredRoles.requiredToDelete)).async { implicit req =>
    db.run(repo.deleteById(id)).map {
      i =>
        if (i == 0) NotFound else Ok
    }
  }

  def getById(id: Id): Action[AnyContent] = SecuredAction(WithRole(requiredRoles.requiredToGet)).async { implicit req =>
    db.run(repo.findById(id)).map(entity => Ok(Json.toJson(entity)))
  }

  def getAll: Action[AnyContent] = SecuredAction(WithRole(requiredRoles.requiredToGet)).async { implicit req =>
    val all: Future[Seq[Model]] = db.run(repo.fetchAll(100))

    all.map(all => Ok(Json.toJson(all)))
  }

  def create: Action[Model] = SecuredAction(WithRole(requiredRoles.requiredToCreate)).async(parse.json(reads)) {
    implicit parsed =>
      db.run(repo.save(parsed.body).asTry).map {
        case Success(created) => Created(Json.toJson(created))
        case Failure(e: PSQLException) if e.getSQLState == "23505" => Conflict(
          JsonStatus.error("message" -> "Unique key violation: unique key already exists in the database.")
        )
        case Failure(t: PSQLException) => InternalServerError(
          JsonStatus.error("message" -> "PSQL error", "info" -> t.getServerErrorMessage.toString)
        )
      }

  }
}
