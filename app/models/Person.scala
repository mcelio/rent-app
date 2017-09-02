package models

import play.api.libs.json._

case class Person(id: Long, name: String, lastname: String, age: Int, email: String, passport: String, removed: Boolean)

object Person {
  
  implicit val personFormat = Json.format[Person]
}
