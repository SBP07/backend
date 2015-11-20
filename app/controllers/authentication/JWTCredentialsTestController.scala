package controllers.authentication

import javax.inject.Inject

import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.JWTAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import models.tenant.AuthCrewUser
import play.api.i18n.{Messages, MessagesApi}
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent}

import scala.concurrent.Future

class JWTCredentialsTestController @Inject()(
                                              val messagesApi: MessagesApi,
                                              val env: Environment[AuthCrewUser, JWTAuthenticator],
                                              socialProviderRegistry: SocialProviderRegistry
                                               )
  extends Silhouette[AuthCrewUser, JWTAuthenticator]
{
  def index: Action[AnyContent] = SecuredAction.async { implicit request =>
    Future.successful(Ok(Json.obj("message" -> Messages("authentication.successful"))))
  }
}
