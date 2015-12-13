package dao.tenant

import java.util.UUID
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.language.postfixOps
import models.tenant.{ChildToContactPersonRelationship, ContactPerson, Child}
import dao.NonExistantChildOrContactPersonOrDontBelongToTenant

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChildToContactPersonDao @Inject()(
  val dbConfigProvider: DatabaseConfigProvider,
  val contactPersonRepo: ContactPersonRepo,
  val childRepo: ChildRepo
) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.driver.api._

  val db = dbConfig.db

  // TableQueries
  val childToContactPersonTable = TableQuery[ChildToContactPersonTable]
  val contactPeople = contactPersonRepo.tableQuery
  val children = childRepo.tableQuery

  def contactPeopleForChild(tenantCanonicalName: String, childId: UUID): Future[Seq[(String, ContactPerson)]] = {
    val query = for {
      childToContact <- childToContactPersonTable if childToContact.childId === childId
      contactPerson <- contactPeople if contactPerson.id === childToContact.contactPersonId &&
      contactPerson.tenantCanonicalName === tenantCanonicalName // Just to be sure
    } yield { (childToContact.relationship, contactPerson) }

    db.run(query.result).map(_.map(tuple => (tuple._1, tuple._2.convert)))
  }

  def childrenForContactPerson(tenantCanonicalName: String, contactPersonId: UUID): Future[Seq[Child]] = {
    val query = for {
      childToContact <- childToContactPersonTable if childToContact.contactPersonId === contactPersonId
      child <- children if child.id === childToContact.childId &&
      child.tenantCanonicalName === tenantCanonicalName // Just to be sure
    } yield {
        child
    }

    db.run(query.result)
  }

  def insert(tenantCanonicalName: String, relationship: ChildToContactPersonRelationship): Future[Int] = {
    db.run {
      (for {
        child <- children if child.id === relationship.childId && child.tenantCanonicalName === tenantCanonicalName
        contactPerson <- contactPeople if contactPerson.id === relationship.contactPersonId &&
        contactPerson.tenantCanonicalName === tenantCanonicalName
      } yield (child, contactPerson)).length.result
    }.flatMap { numRows =>
      if(numRows == 0) {
        db.run(childToContactPersonTable += relationship)
      } else {
        Future.failed(new NonExistantChildOrContactPersonOrDontBelongToTenant(tenantCanonicalName))
      }
    }
  }

  def deleteRelationship(tenantCanonicalName: String, childId: UUID, contactPersionId: UUID): Future[Int] = {
    db.run {
      (for {
        childToContact <- childToContactPersonTable if childToContact.childId === childId && childToContact.contactPersonId === contactPersionId
        child <- childToContact.childFk if child.tenantCanonicalName === tenantCanonicalName
        contactPerson <- childToContact.contactPersonFk if contactPerson.tenantCanonicalName === tenantCanonicalName
      } yield {
        childToContact
      }).result
    }.flatMap { numFound =>
      if (numFound == 0)
        Future.failed(new NonExistantChildOrContactPersonOrDontBelongToTenant(tenantCanonicalName))
        else {
          db.run {
            childToContactPersonTable
              .filter(_.childId === childId)
              .filter(_.contactPersonId === contactPersionId)
              .delete
          }
        }
   }
  }

  // does not extend BelongsToTenant because there is no need
  // Children belong to just one tenant, so do contact people
  class ChildToContactPersonTable(tag: Tag) extends Table[ChildToContactPersonRelationship](tag, "child_to_contact_person") {
    def childId: Rep[UUID] = column[UUID]("child_id")

    def contactPersonId: Rep[UUID] = column[UUID]("contact_person_id")

    def relationship: Rep[String] = column[String]("relationship")

    def `*`: ProvenShape[ChildToContactPersonRelationship] = (childId, contactPersonId, relationship) <>
      (ChildToContactPersonRelationship.tupled, ChildToContactPersonRelationship.unapply)

    def childFk = foreignKey("child_id", childId, children)(_.id)
    def contactPersonFk = foreignKey("contact_person_id", contactPersonId, contactPeople)(_.id)
  }

}
