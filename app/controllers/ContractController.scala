package controllers

import javax.inject._

import dal._
import models.{Contract, Person, Property}
import play.api.i18n._
import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class ContractController @Inject()(repo: ContractRepository,
                                   personRepo: PersonRepository,
                                   propertyRepo: PropertyRepository,
                                   cc: ControllerComponents
                                )(implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {


  implicit val writer = new Writes[(Contract, Seq[Person], Seq[Property])] {
    def writes(t: (Contract, Seq[Person], Seq[Property])): JsValue = {
      val personName = t._2(0).name
      val passport = t._2(0).passport
      val propertyCode = t._3(0).code

      Json.obj("id: " -> t._1.id, "beginDate" -> t._1.beginDate, "endDate" -> t._1.endDate, "notes" -> t._1.notes,
        "rentAmount" -> t._1.rentAmount, "depositAmout" -> t._1.depositAmount,
        "numberAdvances" -> t._1.numberAdvances, "numberDeposits" -> t._1.numberDeposits, "personName" -> personName,
        "passport" -> passport, "propertyCode" -> propertyCode)
    }
  }

  /**
    * The contract action.
    */
  def contract = Action { implicit request =>
    val auth = request.session.get("authorization").getOrElse("false").toBoolean
    if (auth) {
      Ok(views.html.contract("contract"))
    } else{
      Ok(views.html.login())
    }
  }

  def createContract() = Action.async { implicit request =>
    implicit val contractReader = Json.reads[Contract]
    val jsonBody: JsValue = request.body.asJson.get
    val propertyId = (jsonBody \ "propertyId").as[Long]
    val personId = (jsonBody \ "personId").as[Long]
    val beginDate = (jsonBody \ "beginDate").as[String]
    val endDate = (jsonBody \ "endDate").as[String]
    val numberAdvances = (jsonBody \ "numberAdvances").as[String]
    val rentAmount = (jsonBody \ "rentAmount").as[String]
    val numberDeposits = (jsonBody \ "numberDeposits").as[String]
    val depositAmount = (jsonBody \ "depositAmount").as[String]
    val notes = (jsonBody \ "notes").as[String]
    repo.create(propertyId, personId, beginDate, endDate, numberAdvances.toInt, numberDeposits.toInt, rentAmount.toDouble,
      depositAmount.toDouble, notes, false) map {personObj =>
      Future {
        Ok(Json.toJson(personObj))
      }
    }
    Future {
      Ok(views.html.contract("contract"))
    }
  }


  def deleteContract(id: Long) = Action.async { implicit request =>
    repo.delete(id).map{
      contract =>
        Ok(Json.toJson(contract))
    }
  }

  /**
    * A REST endpoint that gets all the people as JSON.
    */
  def getContracts = Action.async { implicit request =>
    repo.list().map(contracts =>
      contracts.map(contract => {
        (contract, personRepo.findById(contract.personId), propertyRepo.findById(contract.propertyId))
      })
    ).map { results =>
      Ok(Json.toJson(results))
    }
  }
}
