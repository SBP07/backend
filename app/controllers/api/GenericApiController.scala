package controllers.api

import com.google.inject.Inject
import dao.RepoFor
import io.strongtyped.active.slick.JdbcProfileProvider.PostgresProfileProvider
import io.strongtyped.active.slick.{EntityActions, ActiveRecord}
import models.tenant.Address
import play.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.mvc._
import slick.driver.JdbcProfile
import play.api.libs.json._
import scala.concurrent.ExecutionContext.Implicits.global

abstract class GenericApiController(dbConfigProvider: DatabaseConfigProvider) extends Controller {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig.driver.api._
  val db = dbConfig.db

  type Id
  type Model

  val repo: RepoFor[Model, Id]
  implicit class EntryExtensions(val model: Model) extends ActiveRecord(repo)


  implicit val reads: Reads[Model]
  implicit val writes: Writes[Model]

  def update: Action[Model] = Action.async(parse.json(reads)) { entity =>
    db.run(entity.body.update).map(updated => Ok(Json.toJson(updated)))
  }

  def delete(id: Id): Action[AnyContent] = TODO

  def getById(id: Id): Action[AnyContent] = Action.async {
    db.run(repo.findById(id)).map(entity => Ok(Json.toJson(entity)))
  }

  def getAll: Action[AnyContent] = Action.async {
    val all = db.run(repo.tableQuery.result)

    all.map(all => Ok(Json.toJson(all)))
  }

  def create: Action[Model] = Action.async(parse.json(reads)) { parsed =>
    db.run(parsed.body.save).map(created => Ok(Json.toJson(created)))
  }
}
