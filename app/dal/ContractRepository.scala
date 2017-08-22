package dal

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import models.Contract

import scala.concurrent.{ Future, ExecutionContext }

/**
  * A repository for Contract.
  *
  * @param dbConfigProvider The Play db config provider. Play will inject this for you.
  */
@Singleton
class ContractRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  // We want the JdbcProfile for this provider
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  // These imports are important, the first one brings db into scope, which will let you do the actual db operations.
  // The second one brings the Slick DSL into scope, which lets you define the table and other queries.
  import dbConfig._
  import profile.api._

  /**
    * Here we define the table. It will have a name of people
    */
  private class ContractTable(tag: Tag) extends Table[Contract](tag, "contracts") {

    /** The ID column, which is the primary key, and auto incremented */
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    /** The person column */
    def personId = column[Long]("personId")

    /** The beginDate column */
    def beginDate = column[String]("beginDate")

    /** The beginDate column */
    def endDate = column[String]("endDate")

    /**
      * This is the tables default "projection".
      *
      * It defines how the columns are converted to and from the Person object.
      *
      * In this case, we are simply passing the id, name and page parameters to the Person case classes
      * apply and unapply methods.
      */
    def * = (id, personId, beginDate, endDate) <> ((Contract.apply _).tupled, Contract.unapply)
  }

  /**
    * The starting point for all queries on the people table.
    */
  private val contracts = TableQuery[ContractTable]

  /**
    * Create a person with the given name and age.
    *
    * This is an asynchronous operation, it will return a future of the created person, which can be used to obtain the
    * id for that person.
    */
  def create(personId: Long, beginDate: String, endDate: String): Future[Contract] = db.run {
    // We create a projection of just the name and age columns, since we're not inserting a value for the id column
    (contracts.map(c => (c.personId, c.beginDate, c.endDate))
      // Now define it to return the id, because we want to know what id was generated for the person
      returning contracts.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into ((contractData, id) => Contract(id, contractData._1, contractData._2, contractData._3))
      // And finally, insert the person into the database
      ) += (personId, beginDate, endDate)
  }

  /**
    * List all the people in the database.
    */
  def list(): Future[Seq[Contract]] = db.run {
    contracts.result
  }
}
