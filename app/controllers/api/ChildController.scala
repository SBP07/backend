package controllers.api

import java.util.UUID

import com.google.inject.Inject
import dao.RepoFor
import models.tenant.Child
import play.api._
import play.api.db.slick.DatabaseConfigProvider
import play.api.libs.json._

class ChildController @Inject()(dbConfigProvider: DatabaseConfigProvider) extends GenericApiController(dbConfigProvider) {
  override type Id = UUID
  override type Model = Child

  override val repo: RepoFor[Child, Id] = dao.tenant.ChildRepo

  override implicit val reads: Reads[Model] = models.tenant.json.ChildJson.childReads
  override implicit val writes: Writes[Model] = models.tenant.json.ChildJson.childWrites
}
