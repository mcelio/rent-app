package dal

import javax.inject.{Inject, Singleton}

import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import models.Person

import scala.concurrent.{Await, ExecutionContext, Future}

/**
 * A repository for people.
 *
 * @param dbConfigProvider The Play db config provider. Play will inject this for you.
 */
@Singleton
class PersonRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
   * Here we define the table. It will have a name of people
   */
  private class PeopleTable(tag: Tag) extends Table[Person](tag, "people") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The name column */
    def name = column[String]("name")

    /** The lastname column */
    def lastname = column[String]("lastname")

    /** The age column */
    def age = column[Int]("age")

    /** The email column */
    def email = column[String]("email")

    /** The passwport column */
    def passport = column[String]("passport")

    /** The removed column */
    def removed = column[Boolean]("removed", O.Default(false))

    /**
     * This is the tables default "projection".
     *
     * It defines how the columns are converted to and from the Person object.
     *
     * In this case, we are simply passing the id, name and page parameters to the Person case classes
     * apply and unapply methods.
     */
    def * = (id, name, lastname, age, email, passport, removed) <> ((Person.apply _).tupled, Person.unapply)
  }

  /**
   * The starting point for all queries on the people table.
   */
  private val people = TableQuery[PeopleTable]

  /**
   * Create a person with the given name and age.
   *
   * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
   * id for that person.
   */
  def create(name: String, lastname: String, age: Int, email: String, passport: String, removed: Boolean): Future[Person] = db.run {
    // We create a projection of just the name and age columns, since we're not inserting a value for the id column
    (people.map(p => (p.name, p.lastname, p.age, p.email, p.passport, p.removed))
      // Now define it to return the id, because we want to know what id was generated for the person
      returning people.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((personData, id) => Person(id, personData._1, personData._2, personData._3, personData._4, personData._5, personData._6))
    // And finally, insert the person into the database
    ) += (name, lastname, age, email, passport, removed)
  }


   def findById2(id: Long): Future[Seq[Person]] = {
     val filter: Query[PeopleTable, Person, Seq] = people.filter(_.id === id)
     db.run(filter.result)
   }

  def sfindById(id: Long): Future[Option[Person]] = db.run(
    people.filter(_.id === id).result.map(_.headOption)
  )

  def findById(id: Long): Seq[Person] = {
    import scala.concurrent.duration.Duration
    val filter: Query[PeopleTable, Person, Seq] = people.filter(_.id === id)
    Await.result(db.run(filter.result), Duration.Inf)
  }


  /**
    * Update person
    * @param id
    * @return
    */
  def delete(id: Long): Future[Int] = {
    val q = for {p <- people if p.id === id} yield p.removed
    db.run(q.update(true))

//    people.filter(_.id === id).map { p =>
//      p.removed
//    }.update(true)
  }

  /**
   * List all the people in the database.
   */
  def list(): Future[Seq[Person]] = db.run {
    people.filter(!_.removed).result
    //people.result
  }


}
