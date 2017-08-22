package controllers

import javax.inject._

import dal._
import play.api.data.Form
import play.api.data.Forms.{mapping, longNumber, nonEmptyText}
import play.api.data.validation.Constraints._
import play.api.i18n._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

class ContractController @Inject()(repo: ContractRepository,
                                 cc: ControllerComponents
                                )(implicit ec: ExecutionContext)
  extends AbstractController(cc) with I18nSupport {

  /**
    * The mapping for the contract form.
    */
  val contractForm: Form[CreateContractForm] = Form {
    mapping(
      "personId" -> longNumber,
      "beginDate" -> nonEmptyText,
      "endDate" -> nonEmptyText
    )(CreateContractForm.apply)(CreateContractForm.unapply)
  }

  /**
    * The contract action.
    */
  def contract = Action { implicit request =>
    Ok(views.html.contract(contractForm, "contract") )
  }

  /**
    * The add contract action.
    *
    * This is asynchronous, since we're invoking the asynchronous methods on ContractRepository.
    */
  def addContract = Action.async { implicit request =>
    // Bind the form first, then fold the result, passing a function to handle errors, and a function to handle succes.
    contractForm.bindFromRequest.fold(
      // The error function. We return the index page with the error form, which will render the errors.
      // We also wrap the result in a successful future, since this action is synchronous, but we're required to return
      // a future because the contract creation function returns a future.
      errorForm => {
        Future.successful(Ok(views.html.contract(errorForm, "contract")))
      },
      // There were no errors in the from, so create the contract.
      contract => {
        repo.create(contract.personId, contract.beginDate, contract.endDate).map { _ =>
          // If successful, we simply redirect to the index page.
          Redirect(routes.ContractController.contract)
        }
      }
    )
  }

  /**
    * A REST endpoint that gets all the people as JSON.
    */
  def getContracts = Action.async { implicit request =>
    repo.list().map { contracts =>
      Ok(Json.toJson(contracts))
    }
  }
}

/**
  * The create contract form.
  *
  * Generally for forms, you should define separate objects to your models, since forms very often need to present data
  * in a different way to your models.  In this case, it doesn't make sense to have an id parameter in the form, since
  * that is generated once it's created.
  */
case class CreateContractForm(personId: Long, beginDate: String, endDate: String)
