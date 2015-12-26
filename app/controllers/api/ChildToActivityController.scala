package controllers.api

import java.time.Instant
import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Silhouette, Environment}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import dao.{RowAlreadyExistsException, NonExistantChildOrActivityOrDontBelongToTenantException}
import dao.tenant.ChildToActivityDao
import models.tenant.{Activity, Crew, ChildToActivityRelationship}
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._
import models.tenant.json.ChildJson.childWrites
import models.tenant.json.ActivityJson.activityWrites
import models.tenant.ChildToActivityRelationship.{jsonWrites => childToActivityRelationshipWrites}
import utils.JsonStatus

import scala.concurrent.ExecutionContext.Implicits.global

class ChildToActivityController @Inject()(
  val messagesApi: MessagesApi,
  val env: Environment[Crew, JWTAuthenticator],
  val childToActivityDao: ChildToActivityDao)
  extends Silhouette[Crew, JWTAuthenticator]
{

  case class ActivityIdBindModel(activityId: UUID, checkInTime: Option[Instant], checkOutTime: Option[Instant])
  val bindmodelReads: Reads[ActivityIdBindModel] = Json.reads[ActivityIdBindModel]

  def activitiesForChild(id: UUID): Action[AnyContent] = SecuredAction.async { req =>
    childToActivityDao.activitiesForChild(req.identity.tenantCanonicalName, id).map { activities =>
      implicit val relationshipWrites = childToActivityRelationshipWrites
      implicit val jsonWrites: Writes[(Activity, ChildToActivityRelationship)] = (
        (JsPath \ "activity").write[Activity] and
        (JsPath \ "relationship").write[ChildToActivityRelationship]
        )(unlift((tuple: (Activity, ChildToActivityRelationship)) => Some(tuple)))
      Ok(Json.toJson(activities))
    }
  }
  def childrenForActivity(id: UUID): Action[AnyContent] = SecuredAction.async { req =>
    childToActivityDao.childrenForActivity(req.identity.tenantCanonicalName, id).map { children =>
      Ok(Json.toJson(children))
    }
  }

  def registerActivityForChild(childId: UUID): Action[ActivityIdBindModel] = SecuredAction.async(parse.json(bindmodelReads)) { req =>
    childToActivityDao.insert(
      req.identity.tenantCanonicalName,
      ChildToActivityRelationship(childId, req.body.activityId, req.body.checkInTime, req.body.checkOutTime)
    ).map { numInserted =>
      Ok(JsonStatus.success("message" -> "Successfully registered"))
    } recover {
      case e: NonExistantChildOrActivityOrDontBelongToTenantException => BadRequest(JsonStatus.error("message" ->
        "Non-existant child or activity, or child/activity does not belong to the current tenant"))
      case e: RowAlreadyExistsException => Conflict(JsonStatus.error("message" ->
        "This activity is already registered for this child."))
    }
  }

  def unregisterActivityForChild(childId: UUID, activityId: UUID): Action[AnyContent] = SecuredAction.async { req =>
    childToActivityDao.deleteRelationship(req.identity.tenantCanonicalName, childId, activityId).
      map { numDeleted =>
        Ok(JsonStatus.success("message" -> s"Deleted $numDeleted rows"))
      }
  }
}
