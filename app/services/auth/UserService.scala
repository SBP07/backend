package services.auth

import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.tenant.Crew

import scala.concurrent.Future

/**
  * Handles actions to users.
  */
trait UserService extends IdentityService[Crew] {

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: Crew): Future[Crew]

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile, tenantCanonicalName: String): Future[Crew]
}
