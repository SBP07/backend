package controllers.authentication

import javax.inject.Inject

import _root_.services.auth.UserService
import com.mohiva.play.silhouette.api.Authenticator.Implicits._
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.repositories.AuthInfoRepository
import com.mohiva.play.silhouette.api.util.{Clock, Credentials}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers._
import models.bindmodels.SignInData
import models.tenant.AuthCrewUser
import net.ceedubs.ficus.Ficus._
import play.api.Configuration
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._
import play.api.mvc.Action

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps

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
class CookieCredentialsAuthController @Inject()(
                                                 val messagesApi: MessagesApi,
                                                 val env: Environment[AuthCrewUser, CookieAuthenticator],
                                                 userService: UserService,
                                                 authInfoRepository: AuthInfoRepository,
                                                 credentialsProvider: CredentialsProvider,
                                                 socialProviderRegistry: SocialProviderRegistry,
                                                 configuration: Configuration,
                                                 clock: Clock)
  extends Silhouette[AuthCrewUser, CookieAuthenticator] {

  def authenticate: Action[SignInData.Data] = Action.async(parse.json(SignInData.dataReads)) { implicit req =>
    val data = req.body
    val credentials = Credentials(data.email, data.password)
    credentialsProvider.authenticate(credentials).flatMap { loginInfo =>
      val result = Ok(Messages("welcome.signed.inte")) //Redirect(routes.ApplicationController.index())
      userService.retrieve(loginInfo).flatMap {
        case Some(user) =>
          val c = configuration.underlying
          env.authenticatorService.create(loginInfo).map {
            case authenticator if data.rememberMe =>
              authenticator.copy(
                expirationDateTime = clock.now + c.as[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorExpiry"),
                idleTimeout = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.authenticatorIdleTimeout"),
                cookieMaxAge = c.getAs[FiniteDuration]("silhouette.authenticator.rememberMe.cookieMaxAge")
              )
            case authenticator => authenticator
          }.flatMap { authenticator =>
            env.eventBus.publish(LoginEvent(user, req, request2Messages))
            env.authenticatorService.init(authenticator).flatMap { v =>
              env.authenticatorService.embed(v, result)
            }
          }
        case None => Future.failed(new IdentityNotFoundException("Couldn't find user"))
      }
    }.recover {
      case e: ProviderException =>
        //Redirect(routes.ApplicationController.signIn()).flashing("error" -> Messages("invalid.credentials"))
        Unauthorized(Messages("invalid.credentials"))
    }

  }

}
