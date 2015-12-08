package services.auth

import javax.inject.{Singleton, Inject}

import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import com.mohiva.play.silhouette.api._
import models.bindmodels.SignUpData
import play.api.libs.concurrent.Execution.Implicits._
import models.Role.NormalUser
import models.tenant.Crew
import play.api.{Configuration, Logger}

import scala.concurrent.Future

/**
  * Injectable to set up the database
  */
trait DatabaseSetup {
}

@Singleton
class DatabaseSetupImpl @Inject()(
  userService: UserService,
  authInfoRepository: AuthInfoRepository,
  avatarService: AvatarService,
  passwordHasher: PasswordHasher,
  configuration: Configuration)
  extends DatabaseSetup {
  val email = configuration.getString("authBootstrapper.email").get
  val password = configuration.getString("authBootstrapper.password").get
  val firstName = configuration.getString("authBootstrapper.firstName").getOrElse("Global")
  val lastName = configuration.getString("authBootstrapper.lastName").getOrElse("Admin")
  val tenantCanonicalName = configuration.getString("authBootstrapper.tenantCanonicalName").getOrElse("platformm")
  val data = SignUpData(firstName, lastName, email, password)
  val loginInfo = LoginInfo(CredentialsProvider.ID, email)
  userService.retrieve(loginInfo).flatMap {
    case Some(user) =>
      // User exists, log
      Logger.info(s"Global admin user $email exists, not creating")
      Future.successful(Unit)
    case None =>
      val authInfo = passwordHasher.hash(data.password)
      val user = Crew(
        userID = None,
        loginInfo = loginInfo,
        firstName = Some(data.firstName),
        lastName = Some(data.lastName),
        fullName = Some(data.firstName + " " + data.lastName),
        email = Some(data.email),
        avatarURL = None,
        birthDate = None,
        address = None,
        roles = Set(NormalUser),
        tenantCanonicalName = tenantCanonicalName
      )
      for {
        avatar <- avatarService.retrieveURL(data.email)
        user <- userService.save(user.copy(avatarURL = avatar))
        authInfo <- authInfoRepository.add(loginInfo, authInfo)
      } yield {
        Logger.info(s"Global admin user $email did not exist, created")
      }
  }
}
