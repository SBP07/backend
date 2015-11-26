package controllers.authentication

import java.util.UUID
import javax.inject.Inject

import _root_.services.auth.UserService
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.services.AvatarService
import com.mohiva.play.silhouette.api.util.PasswordHasher
import com.mohiva.play.silhouette.impl.authenticators.{JWTAuthenticator, CookieAuthenticator}
import com.mohiva.play.silhouette.impl.providers._
import models.Role.NormalUser
import models.bindmodels.{SignUpDataJson, SignUpData}
import models.tenant.AuthCrewUser
import play.api.i18n.{MessagesApi, Messages}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.json.Json
import play.api.mvc.Action
import models.tenant.json.AuthCrewUserJson.crewWrites

import scala.concurrent.Future

/**
  * The sign up controller.
  *
  * @param messagesApi The Play messages API.
  * @param env The Silhouette environment.
  * @param userService The user service implementation.
  * @param authInfoRepository The auth info repository implementation.
  * @param avatarService The avatar service implementation.
  * @param passwordHasher The password hasher implementation.
  */
class SignUpController @Inject()(
                                  val messagesApi: MessagesApi,
                                  val env: Environment[AuthCrewUser, JWTAuthenticator],
                                  userService: UserService,
                                  authInfoRepository: AuthInfoRepository,
                                  avatarService: AvatarService,
                                  passwordHasher: PasswordHasher)
  extends Silhouette[AuthCrewUser, JWTAuthenticator] {

  def signUp: Action[SignUpData] = Action.async(parse.json(SignUpDataJson.dataReads)) { implicit req =>
    val data = req.body
    val loginInfo = LoginInfo(CredentialsProvider.ID, data.email)
    userService.retrieve(loginInfo).flatMap {
      case Some(user) =>
        Future.successful(BadRequest(Json.toJson(Json.obj("message" -> Messages("user.exists")))))
      case None =>
        val authInfo = passwordHasher.hash(data.password)
        val user = AuthCrewUser(
          userID = UUID.randomUUID(),
          loginInfo = loginInfo,
          firstName = Some(data.firstName),
          lastName = Some(data.lastName),
          fullName = Some(data.firstName + " " + data.lastName),
          email = Some(data.email),
          avatarURL = None,
          Set(NormalUser)
        )
        for {
          avatar <- avatarService.retrieveURL(data.email)
          user <- userService.save(user.copy(avatarURL = avatar))
          authInfo <- authInfoRepository.add(loginInfo, authInfo)
          authenticator <- env.authenticatorService.create(loginInfo)
          value <- env.authenticatorService.init(authenticator)
          result <- env.authenticatorService.embed(value, Created(Json.toJson(user)))
        } yield {
          env.eventBus.publish(SignUpEvent(user, req, request2Messages))
          env.eventBus.publish(LoginEvent(user, req, request2Messages))
          result
        }
    }
  }

}
