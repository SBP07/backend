package controllers.api

import com.mohiva.play.silhouette.api.{Silhouette, Environment}
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, CookieAuthenticator}
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import dao.RepoFor
import io.strongtyped.active.slick.ActiveRecord
import models.tenant.AuthCrewUser
import org.postgresql.util.PSQLException
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.mvc._
import slick.driver.JdbcProfile
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

abstract class GenericApiController(val dbConfigProvider: DatabaseConfigProvider,
                                    val messagesApi: MessagesApi,
                                    val env: Environment[AuthCrewUser, JWTAuthenticator],
                                    val socialProviderRegistry: SocialProviderRegistry)
  extends Silhouette[AuthCrewUser, JWTAuthenticator]
{


  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.driver.api._

  val db = dbConfig.db

  type Id
  type Model
  type PersistedModel

  def convertToPersistable: Model => PersistedModel

  def convertToDisplayable: PersistedModel => Model

  val repo: RepoFor[PersistedModel, Id]

  implicit class EntryExtensions(val model: PersistedModel) extends ActiveRecord(repo)


  implicit val reads: Reads[Model]
  implicit val writes: Writes[Model]

  def update: Action[Model] = Action.async(parse.json(reads)) {
    entity =>
      db.run(convertToPersistable(entity.body).update).map(updated => Ok(Json.toJson(convertToDisplayable(updated))))
  }

  def delete(id: Id): Action[AnyContent] = Action.async {
    db.run(repo.filterById(id).delete).map {
      i =>
        if (i == 0) NotFound else Ok
    }
  }

  def getById(id: Id): Action[AnyContent] = Action.async {
    db.run(repo.findById(id)).map(entity => Ok(Json.toJson(convertToDisplayable(entity))))
  }

  def getAll: Action[AnyContent] = Action.async {
    val all = db.run(repo.tableQuery.result)

    all.map(all => Ok(Json.toJson(all.map(convertToDisplayable))))
  }

  def create: Action[Model] = Action.async(parse.json(reads)) {
    parsed =>
      db.run(convertToPersistable(parsed.body).save.asTry).map {
        case Success(created) => Ok(Json.toJson(convertToDisplayable(created)))
        case Failure(e: PSQLException) if e.getSQLState == "23505" => InternalServerError("Unique key violation: unique key already exists in the database.")
        case Failure(t: PSQLException) => InternalServerError("PSQL error.")
      }

  }
}
