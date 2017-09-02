package models

import play.api.libs.json._

case class Property(id: Long, code: String, line1: String, line2: String, line3: String, zipcode: String, rooms: Int, removed: Boolean)

object Property {

  implicit val propertyFormat = Json.format[Property]
}
