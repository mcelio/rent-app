package models

import java.util.Date

import play.api.libs.json._

case class Contract(id: Long, personId: Long, beginDate: String, endDate: String, numberAdvances: Int,
                    numberDeposits: Int, rentAmount: Double, depositAmount: Double, notes: String, removed: Boolean)

object Contract {

  implicit val contractFormat = Json.format[Contract]
}
