package controllers

import javax.inject._

import dal._
import models.Person
import play.api.i18n._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class PersonController @Inject()(repo: PersonRepository,
                                  cc: ControllerComponents
                                )(implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {

  /**
   * The person action.
   */
  def person = Action { implicit request =>
    val auth = request.session.get("authorization").getOrElse("false").toBoolean
    if (auth) {
      Ok(views.html.person("person"))
    } else{
      Ok(views.html.login())
    }
  }

  /**
   * A REST endpoint that gets all the people as JSON.
   */
  def getPersons = Action.async { implicit request =>
    repo.list().map { people =>
      Ok(Json.toJson(people))
    }
  }


  def deletePerson(id: Long) = Action.async { implicit request =>
    repo.delete(id).map{
      person =>
        Ok(Json.toJson(person))
    }
  }

  def fetchPerson(id: Long) = Action.async { implicit request =>
      val person = repo.findById(id).headOption
      Future{Ok(Json.toJson(person))}
  }


  def createPerson() = Action.async { implicit request =>
    implicit val personReader = Json.reads[Person]
    val jsonBody: JsValue = request.body.asJson.get
    val name = (jsonBody \ "name").as[String]
    val lastname = (jsonBody \ "lastname").as[String]
    val age = (jsonBody \ "age").as[String]
    val email = (jsonBody \ "email").as[String]
    val passport = (jsonBody \ "passport").as[String]
    repo.create(name, lastname, age.toInt, email, passport, false) map {personObj =>
      Future {
        Ok(Json.toJson(personObj))
      }
    }
    Future {
      Ok(views.html.person("person"))
    }
  }
}
