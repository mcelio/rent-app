package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import models.Property

import scala.concurrent.{ Future, ExecutionContext }

/**
  * A repository for properties.
  *
  * @param dbConfigProvider The Play db config provider. Play will inject this for you.
  */
@Singleton
class PropertyRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
    * Here we define the table. It will have a name of people
    */
  private class PropertyTable(tag: Tag) extends Table[Property](tag, "properties") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The ID column, which is the primary key, and auto incremented */
    def code = column[String]("code")

    /** Line 1 column */
    def line1 = column[String]("line1")

    /** Line 2 column */
    def line2 = column[String]("line2")

    /** Line 3 column */
    def line3 = column[String]("line3")

    /** zipccde column */
    def zipcode = column[String]("zipcode")

    /** The Rooms available column */
    def rooms = column[Int]("rooms")

    /** The passwport column */
    def removed = column[Boolean]("removed", O.Default(false))

    /**
      * This is the tables default "projection".
      *
      * It defines how the columns are converted to and from the Person object.
      *
      * In this case, we are simply passing the id, name and page parameters to the Property case classes
      * apply and unapply methods.
      */
    def * = (id, code, line1, line2, line3, zipcode, rooms, removed) <> ((Property.apply _).tupled, Property.unapply)
  }

  /**
    * The starting point for all queries on the Property table.
    */
  private val properties = TableQuery[PropertyTable]

  /**
    * Create a person with the given name and age.
    *
    * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
    * id for that person.
    */
  def create(code: String, line1: String, line2: String, line3: String, zipcode: String, rooms: Int, removed: Boolean): Future[Property] = db.run {
    // We create a projection of just the name and age columns, since we're not inserting a value for the id column
    (properties.map(p => (p.code, p.line1, p.line2, p.line3, p.zipcode, p.rooms, p.removed))
      // Now define it to return the id, because we want to know what id was generated for the person
      returning properties.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((propertyData, id) => Property(id, propertyData._1, propertyData._2, propertyData._3, propertyData._4, propertyData._5, propertyData._6, propertyData._7))
      // And finally, insert the person into the database
      ) += (code, line1, line2, line3, zipcode, rooms, removed)
  }

  /**
    * Update person
    * @param id
    * @return
    */
  def delete(id: Long): Future[Int] = {
    val q = for {p <- properties if p.id === id} yield p.removed
    db.run(q.update(true))

    //    people.filter(_.id === id).map { p =>
    //      p.removed
    //    }.update(true)
  }

  /**
    * List all the people in the database.
    */
  def list(): Future[Seq[Property]] = db.run {
    properties.filter(!_.removed).result
    //people.result
  }
}
