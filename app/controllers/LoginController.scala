package controllers

import javax.inject.Inject

import play.api.i18n.I18nSupport
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AbstractController, ControllerComponents}
import utils.AuthorizationUtils

import scala.concurrent.{ExecutionContext, Future}

class LoginController @Inject()(authorizationUtils: AuthorizationUtils,
                                cc: ControllerComponents
                               )(implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {

  /**
    * The login action.
    */
  def login() = Action { implicit request =>
    Ok(views.html.login()).withNewSession
  }

  def auth() = Action.async { implicit request =>
    val jsonBody: JsValue = request.body.asJson.get
    val username = (jsonBody \ "username").as[String]
    val password = (jsonBody \ "password").as[String]
    val auth = authorizationUtils.authorize(username, password)
    Future {
      Ok(Json.toJson(auth)).withSession("authorization" -> auth.toString)
    }
  }
}
