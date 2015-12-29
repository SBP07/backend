package controllers.api

import java.time.Instant
import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Silhouette, Environment}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import dao.{RowAlreadyExistsException, NonExistantCrewOrActivityOrDontBelongToTenantException}
import dao.tenant.CrewToActivityDao
import models.tenant.{Activity, Crew, CrewToActivityRelationship}
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._
import models.tenant.json.CrewJson.crewWrites
import models.tenant.json.ActivityJson.activityWrites
import models.tenant.CrewToActivityRelationship.{jsonWrites => crewToActivityRelationshipWrites}
import utils.JsonStatus

import scala.concurrent.ExecutionContext.Implicits.global

class CrewToActivityController @Inject()(
  val messagesApi: MessagesApi,
  val env: Environment[Crew, JWTAuthenticator],
  val crewToActivityDao: CrewToActivityDao)
  extends Silhouette[Crew, JWTAuthenticator] {

  case class ActivityIdBindModel(activityId: UUID, checkInTime: Option[Instant], checkOutTime: Option[Instant])

  val bindmodelReads: Reads[ActivityIdBindModel] = Json.reads[ActivityIdBindModel]

  def activitiesForCrew(id: UUID): Action[AnyContent] = SecuredAction.async { req =>
    crewToActivityDao.activitiesForCrew(req.identity.tenantCanonicalName, id).map { activities =>
      implicit val relationshipWrites = crewToActivityRelationshipWrites
      implicit val jsonWrites: Writes[(Activity, CrewToActivityRelationship)] = (
        (JsPath \ "activity").write[Activity] and
          (JsPath \ "relationship").write[CrewToActivityRelationship]
        ) (unlift((tuple: (Activity, CrewToActivityRelationship)) => Some(tuple)))
      Ok(Json.toJson(activities))
    }
  }

  def crewForActivity(id: UUID): Action[AnyContent] = SecuredAction.async { req =>
    crewToActivityDao.crewForActivity(req.identity.tenantCanonicalName, id).map { crewMembers =>
      implicit val writes = crewToActivityDao.dbUserJsonWrites
      Ok(Json.toJson(crewMembers))
    }
  }

  def registerActivityForCrew(crewId: UUID): Action[ActivityIdBindModel] = SecuredAction.async(parse.json(bindmodelReads)) { req =>
    crewToActivityDao.insert(
      req.identity.tenantCanonicalName,
      CrewToActivityRelationship(crewId, req.body.activityId, req.body.checkInTime, req.body.checkOutTime)
    ).map { numInserted =>
      Ok(JsonStatus.success("message" -> "Successfully registered"))
    } recover {
      case e: NonExistantCrewOrActivityOrDontBelongToTenantException => BadRequest(JsonStatus.error("message" -> e.getMessage))
      case e: RowAlreadyExistsException => Conflict(JsonStatus.error("message" ->
        "This activity is already registered for this crew member."))
    }
  }

  def unregisterActivityForCrew(crewId: UUID, activityId: UUID): Action[AnyContent] = SecuredAction.async { req =>
    crewToActivityDao.deleteRelationship(req.identity.tenantCanonicalName, crewId, activityId).
      map { numDeleted =>
        Ok(JsonStatus.success("message" -> s"Deleted $numDeleted rows"))
      }.recover {
      case e: NonExistantCrewOrActivityOrDontBelongToTenantException => BadRequest(JsonStatus.error("message" -> e.getMessage))
    }
  }
}
