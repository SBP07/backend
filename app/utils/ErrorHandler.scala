package utils

import javax.inject.Inject

import com.mohiva.play.silhouette.api.SecuredErrorHandler
import play.api.http.DefaultHttpErrorHandler
import play.api.i18n.Messages
import play.api.mvc.Results._
import play.api.mvc.{Result, RequestHeader}
import play.api.routing.Router
import play.api.{OptionalSourceMapper, Configuration}
import play.api.libs.json.Json

import scala.concurrent.Future

/**
  * A secured error handler.
  */
class ErrorHandler @Inject()(
  env: play.api.Environment,
  config: Configuration,
  sourceMapper: OptionalSourceMapper,
  router: javax.inject.Provider[Router])
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router)
  with SecuredErrorHandler
{

  /**
    * Called when a user is not authenticated.
    *
    * As defined by RFC 2616, the status code of the response should be 401 Unauthorized.
    *
    * @param request The request header.
    * @param messages The messages for the current language.
    * @return The result to send to the client.
    */
  override def onNotAuthenticated(request: RequestHeader, messages: Messages): Option[Future[Result]] = {
    Some(Future.successful(Unauthorized(JsonStatus.error("message" -> messages("not.authenticated")))))
  }

  /**
    * Called when a user is authenticated but not authorized.
    *
    * As defined by RFC 2616, the status code of the response should be 403 Forbidden.
    *
    * @param request The request header.
    * @param messages The messages for the current language.
    * @return The result to send to the client.
    */
  override def onNotAuthorized(request: RequestHeader, messages: Messages): Option[Future[Result]] = {
    Some(Future.successful(Forbidden(JsonStatus.error("message" -> messages("not.authorized")))))
  }

  override def onBadRequest(request: RequestHeader, message: String): Future[Result] =
    Future.successful(
      BadRequest(
        JsonStatus.error("message" -> "Bad request",
          "details" -> Json.obj(
            "requestMethod" -> request.method,
            "requestUri" -> request.uri,
            "message" -> message
          )
        )
      )
    )
}
