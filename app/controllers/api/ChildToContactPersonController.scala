package controllers.api

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.{Silhouette, Environment}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import dao.NonExistantChildOrContactPersonOrDontBelongToTenant
import dao.tenant.ChildToContactPersonDao
import models.tenant.{Crew, ContactPerson}
import play.api.i18n.MessagesApi
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc._
import models.tenant.json.ChildJson.childWrites
import models.tenant.json.ContactPersonJson.contactPersonWrites
import models.tenant.ChildToContactPersonRelationship
import utils.JsonStatus

import scala.concurrent.ExecutionContext.Implicits.global

class ChildToContactPersonController @Inject()(
  val messagesApi: MessagesApi,
  val env: Environment[Crew, JWTAuthenticator],
  val childToContactPersonDao: ChildToContactPersonDao)
  extends Silhouette[Crew, JWTAuthenticator]
{

  case class ContactPersonIdBindModel(contactPersonId: UUID, relationship: String)
  val bindmodelReads: Reads[ContactPersonIdBindModel] = Json.reads[ContactPersonIdBindModel]

  implicit val jsonWrites: Writes[(String, ContactPerson)] = (
      (JsPath \ "relationship").write[String] and
      (JsPath \ "contactPerson").write[ContactPerson]
    )(unlift((tuple: (String, ContactPerson)) => Some(tuple)))

  def contactPeopleForChild(id: UUID): Action[AnyContent] = SecuredAction.async { req =>
    childToContactPersonDao.contactPeopleForChild(req.identity.tenantCanonicalName, id).map { contactPeople =>
      Ok(Json.toJson(contactPeople))
    }
  }
  def childrenForContactPerson(id: UUID): Action[AnyContent] = SecuredAction.async { req =>
    childToContactPersonDao.childrenForContactPerson(req.identity.tenantCanonicalName, id).map { children =>
      Ok(Json.toJson(children))
    }
  }

  def addPersonForChild(childId: UUID): Action[ContactPersonIdBindModel] = SecuredAction.async(parse.json(bindmodelReads)) { req =>
    childToContactPersonDao.insert(
      req.identity.tenantCanonicalName,
      ChildToContactPersonRelationship(childId, req.body.contactPersonId, req.body.relationship)
    ).map { numInserted =>
      Ok(JsonStatus.success("message" -> "Successfully registered"))
    } recover {
      case e: NonExistantChildOrContactPersonOrDontBelongToTenant => Conflict(JsonStatus.error("message" ->
        "Non-existant child or contact person, or child/contact person does not belong to the current tenant"))
    }
  }

  def deletePersonForChild(childId: UUID, contactPersonId: UUID): Action[AnyContent] = SecuredAction.async { req =>
    childToContactPersonDao.deleteRelationship(req.identity.tenantCanonicalName, childId, contactPersonId).
      map { numDeleted =>
      Ok(JsonStatus.success("message" -> s"Deleted $numDeleted rows"))
    }
  }
}
