package models.tenant

import java.time.LocalDate
import java.util.UUID

import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import models.Role
import models.helpers.BelongsToTenant

/**
  * The crew (user) object.
  *
  * @param userID The unique ID of the user.
  * @param loginInfo The linked login info.
  * @param firstName Maybe the first name of the authenticated user.
  * @param lastName Maybe the last name of the authenticated user.
  * @param fullName Maybe the full name of the authenticated user.
  * @param email Maybe the email of the authenticated provider.
  * @param avatarURL Maybe the avatar URL of the authenticated provider.
  */
case class AuthCrewUser(
                         userID: UUID,
                         loginInfo: LoginInfo,
                         firstName: Option[String],
                         lastName: Option[String],
                         fullName: Option[String],
                         email: Option[String],
                         avatarURL: Option[String],
                         roles: Set[Role]
                       )
  extends Identity


case class Crew(
                 id: Option[UUID],
                 loginInfo: LoginInfo,
                 firstName: Option[String],
                 lastName: Option[String],
                 fullName: Option[String],
                 email: Option[String],
                 avatarURL: Option[String],
                 birthDate: Option[LocalDate],
                 address: Option[Address],
                 tenantId: UUID
               )
  extends Identity with BelongsToTenant
