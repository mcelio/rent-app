package controllers

import javax.inject._

import dal._
import models.Property
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class PropertyController @Inject()(repo: PropertyRepository,
                                 cc: ControllerComponents
                                )(implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {

  /**
    * The person action.
    */
  def property = Action { implicit request =>
    Ok(views.html.property("property"))
  }

  /**
    * A REST endpoint that gets all the people as JSON.
    */
  def getProperties = Action.async { implicit request =>
    repo.list().map { properties =>
      Ok(Json.toJson(properties))
    }
  }


  def deleteProperty(id: Long) = Action.async { implicit request =>
    repo.delete(id).map{
      property =>
        Ok(Json.toJson(property))
    }
  }


  def createProperty() = Action.async { implicit request =>
    implicit val propertyReader = Json.reads[Property]
    val jsonBody: JsValue = request.body.asJson.get
    val code = (jsonBody \ "code").as[String]
    val line1 = (jsonBody \ "line1").as[String]
    val line2 = (jsonBody \ "line2").as[String]
    val line3 = (jsonBody \ "line3").as[String]
    val zipcode = (jsonBody \ "zipcode").as[String]
    val rooms = (jsonBody \ "rooms").as[String]
    repo.create(code, line1, line2, line3, zipcode, rooms.toInt, false) map {propertyObj =>
      Future {
        Ok(Json.toJson(propertyObj))
      }
    }
    Future {
      Ok(views.html.property("property"))
    }
  }
}