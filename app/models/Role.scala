package models

import com.mohiva.play.silhouette.api.Authorization
import models.tenant.AuthCrewUser
import play.api.i18n._
import play.api.mvc.RequestHeader

/**
 * Check for authorization
 */
case class WithRole(role: Role) //extends Authorization[AuthCrewUser]
{
  def isAuthorized(user: AuthCrewUser)(implicit request: RequestHeader, lang: Lang) = user.roles match {
    case list: Set[Role] => list.contains(role)
    case _               => false
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
