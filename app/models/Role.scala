package models

import com.mohiva.play.silhouette.api.Authorization
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import models.tenant.Crew
import play.api.i18n._
import play.api.mvc.Request

import scala.concurrent.Future

/**
 * Check for authorization
 */
case class WithRole(role: Role) extends Authorization[Crew, JWTAuthenticator]
{
  def isAuthorized[B](user: Crew, authenticator: JWTAuthenticator)(implicit request: Request[B], messages: Messages): Future[Boolean] = user.roles match {
    case list: Set[Role] => Future.successful(list.contains(role))
    case _               => Future.successful(false)
  }
}

/**
  * Trait for all roles
  */
sealed trait Role {
  def name: String
}

/**
  * Companion object providing apply and unapply
  */
object Role {
  def apply(role: String): Role = role match {
    case GlobalAdmin.name => GlobalAdmin
    case TenantAdmin.name => TenantAdmin
    case NormalUser.name => NormalUser
    case _ => Unknown
  }

  def unapply(role: Role): Option[String] = Some(role.name)

  /**
    * Global administrator, can access and edit all tenants
    */
  object GlobalAdmin extends Role {
    val name = "globaladmin"
  }

  /**
    * Administrator of one tenant
    */
  object TenantAdmin extends Role {
    val name = "tenantadmin"
  }

  /**
    * Normal user belonging to a tenant
    */
  object NormalUser extends Role {
    val name = "normaluser"
  }

  /**
    * Generic unknown user role
    */
  object Unknown extends Role{
    val name = "-"
  }
}
