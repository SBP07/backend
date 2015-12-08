package services.auth

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import dao.auth.UserDAO
import models.Role

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import models.tenant.Crew

/**
  * Handles actions to users.
  *
  * @param userDAO The user DAO implementation.
  */
class UserServiceImpl @Inject()(userDAO: UserDAO) extends UserService {

  /**
    * Retrieves a user that matches the specified login info.
    *
    * @param loginInfo The login info to retrieve a user.
    * @return The retrieved user or None if no user could be retrieved for the given login info.
    */
  def retrieve(loginInfo: LoginInfo): Future[Option[Crew]] = userDAO.find(loginInfo)

  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: Crew): Future[Crew] = userDAO.save(user)

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile, tenantCanonicalName: String): Future[Crew] = {
    userDAO.find(profile.loginInfo).flatMap {
      case Some(user) => // Update user with profile
        userDAO.save(user.copy(
          firstName = profile.firstName,
          lastName = profile.lastName,
          fullName = profile.fullName,
          email = profile.email,
          birthDate = None,
          avatarURL = profile.avatarURL
        ))
      case None => // Insert a new user
        userDAO.save(Crew(
          userID = None,
          loginInfo = profile.loginInfo,
          firstName = profile.firstName,
          lastName = profile.lastName,
          fullName = profile.fullName,
          email = profile.email,
          avatarURL = profile.avatarURL,
          birthDate = None,
          address = None,
          roles = Set(Role.NormalUser),
          tenantCanonicalName = tenantCanonicalName
        ))
    }
  }
}
