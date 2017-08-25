package controllers

import javax.inject._

import dal._
import models.Person
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success

class PersonController @Inject()(repo: PersonRepository,
                                  cc: ControllerComponents
                                )(implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {

  /**
   * The mapping for the person form.
   */
  val personForm: Form[CreatePersonForm] = Form {
    mapping(
      "name" -> nonEmptyText,
      "lastname" -> nonEmptyText,
      "age" -> number.verifying(min(0), max(140)),
      "email" -> email,
      "passport" -> nonEmptyText
    )(CreatePersonForm.apply)(CreatePersonForm.unapply)
  }

  /**
   * The person action.
   */
  def person = Action { implicit request =>
    Ok(views.html.person(personForm, "person"))
  }

  /**
   * A REST endpoint that gets all the people as JSON.
   */
  def getPersons = Action.async { implicit request =>
    repo.list().map { people =>
      Ok(Json.toJson(people))
    }
  }




  def createPerson() = Action.async { implicit request =>
    implicit val personReader = Json.reads[Person]
    val jsonBody: JsValue = request.body.asJson.get
    val name = (jsonBody \ "name").as[String]
    val lastname = (jsonBody \ "lastname").as[String]
    val age = (jsonBody \ "age").as[String]
    val email = (jsonBody \ "email").as[String]
    val passport = (jsonBody \ "passport").as[String]
    repo.create(name, lastname, age.toInt, email, passport) map {personObj =>
      Future {
        Ok(Json.toJson(personObj))
      }
    }
    Future {
      Ok(views.html.person(personForm, "person"))
    }
  }
}

/**
 * The create person form.
 *
 * Generally for forms, you should define separate objects to your models, since forms very often need to present data
 * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
 * that is generated once it's created.
 */
case class CreatePersonForm(name: String, lastname: String, age: Int, email: String, passport: String)
