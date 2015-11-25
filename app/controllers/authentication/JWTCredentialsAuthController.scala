package controllers.authentication

import javax.inject.Inject

import _root_.services.auth.UserService
import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import models.bindmodels.SignInData
import models.tenant.AuthCrewUser
import net.ceedubs.ficus.Ficus._
import play.api.Configuration
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.Action

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * The credentials auth controller.
  *
  * @param messagesApi The Play messages API.
  * @param env The Silhouette environment.
  * @param userService The user service implementation.
  * @param authInfoRepository The auth info repository implementation.
  * @param credentialsProvider The credentials provider.
  * @param socialProviderRegistry The social provider registry.
  * @param configuration The Play configuration.
  * @param clock The clock instance.
  */
class JWTCredentialsAuthController @Inject()(
                                              val messagesApi: MessagesApi,
                                              val env: Environment[AuthCrewUser, JWTAuthenticator],
                                              userService: UserService,
                                              authInfoRepository: AuthInfoRepository,
                                              credentialsProvider: CredentialsProvider,
                                              socialProviderRegistry: SocialProviderRegistry,
                                              configuration: Configuration,
                                              clock: Clock)
  extends Silhouette[AuthCrewUser, JWTAuthenticator] {


  /**
    * Authenticates a user against the credentials provider.
    *
    * @return The result to display.
    */
  def authenticate: Action[SignInData.Data] = Action.async(parse.json(SignInData.dataReads)) { implicit request =>
    val data = request.body
    credentialsProvider.authenticate(Credentials(data.email, data.password)).flatMap { loginInfo =>
      userService.retrieve(loginInfo).flatMap {
        case Some(user) => env.authenticatorService.create(loginInfo).map {
          case authenticator if data.rememberMe =>
            val c = configuration.underlying
            authenticator.copy(
              expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
              idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout")
            )
          case authenticator => authenticator
        }.flatMap { authenticator =>
          env.eventBus.publish(LoginEvent(user, request, request2Messages))
          env.authenticatorService.init(authenticator).map { token =>
            Ok(Json.obj("token" -> token))
          }
        }
        case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
      }
    }.recover {
      case e: ProviderException =>
        Unauthorized(Json.obj("message" -> Messages("invalid.credentials"), "status" -> "error"))
    }
  }
}
