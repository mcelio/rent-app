package models

import java.util.Date

import play.api.libs.json._

case class Contract(id: Long, personId: Long, beginDate: String, endDate: String)

object Contract {

  implicit val contractFormat = Json.format[Contract]
}
