package dao.tenant

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.language.postfixOps
import models.tenant._
import dao.{NonExistantChildOrActivityOrDontBelongToTenantException, RowAlreadyExistsException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChildToActivityDao @Inject()(
  val dbConfigProvider: DatabaseConfigProvider,
  val activityRepo: ActivityRepo,
  val childRepo: ChildRepo
)
{
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig.driver.api._

  val db = dbConfig.db

  // TableQueries
  val childToActivityTq = TableQuery[ChildToActivityTable]
  val activitiesTq = activityRepo.tableQuery
  val childrenTq = childRepo.tableQuery

  implicit val myInstantColumnType = MappedColumnType.base[Instant, Timestamp](
    instant => Timestamp.from(instant),
    timestamp => timestamp.toInstant
  )

  def activitiesForChild(tenantCanonicalName: String, childId: UUID): Future[Seq[(Activity, ChildToActivityRelationship)]] = {
    val query = for {
      childToActivity <- childToActivityTq if childToActivity.childId === childId
      activity <- activitiesTq if activity.id === childToActivity.activityId &&
      activity.tenantCanonicalName === tenantCanonicalName // Just to be sure
    } yield (activity, childToActivity)

    db.run(query.result)
  }

  def childrenForActivity(tenantCanonicalName: String, activityId: UUID): Future[Seq[Child]] = {
    val query = for {
      childToActivity <- childToActivityTq if childToActivity.activityId === activityId
      child <- childrenTq if child.id === childToActivity.childId &&
      child.tenantCanonicalName === tenantCanonicalName // Just to be sure
    } yield child

    db.run(query.result)
  }

  def insert(tenantCanonicalName: String, relationship: ChildToActivityRelationship): Future[Int] = {
    db.run {
      (for {
        child <- childrenTq if child.id === relationship.childId && child.tenantCanonicalName === tenantCanonicalName
        activity <- activitiesTq if activity.id === relationship.activityId &&
        activity.tenantCanonicalName === tenantCanonicalName
      } yield (child, activity)).length.result
    }.flatMap { numRows =>
      if(numRows == 1) {
        db.run {
          (for {
            childToActivity <- childToActivityTq if childToActivity.childId === relationship.childId &&
            childToActivity.activityId === relationship.activityId
          } yield childToActivity).length.result
        }.flatMap { numRows =>
          if(numRows == 0){
            db.run(childToActivityTq += relationship)
          } else {
            Future.failed(new RowAlreadyExistsException(tenantCanonicalName))
          }
        }
      } else {
        Future.failed(new NonExistantChildOrActivityOrDontBelongToTenantException(tenantCanonicalName))
      }
    }
  }

  def deleteRelationship(tenantCanonicalName: String, childId: UUID, activityId: UUID): Future[Int] = {
    db.run {
      (for {
        childToActivity <- childToActivityTq if childToActivity.childId === childId && childToActivity.activityId === activityId
        child <- childToActivity.childFk if child.tenantCanonicalName === tenantCanonicalName
        activity <- childToActivity.activityFk if activity.tenantCanonicalName === tenantCanonicalName
      } yield {
        childToActivity
      }).length.result
    }.flatMap { numFound =>
      if (numFound == 0)
        Future.failed(new NonExistantChildOrActivityOrDontBelongToTenantException(tenantCanonicalName))
      else {
        db.run {
          childToActivityTq
            .filter(_.childId === childId)
            .filter(_.activityId === activityId)
            .delete
        }
      }
    }
  }

  // does not extend BelongsToTenant because there is no need
  // Children belong to just one tenant, so do activities
  class ChildToActivityTable(tag: Tag) extends Table[ChildToActivityRelationship](tag, "child_to_activity") {
    def childId: Rep[UUID] = column[UUID]("child_id")
    def activityId: Rep[UUID] = column[UUID]("activity_id")

    def checkInTime: Rep[Option[Instant]] = column[Option[Instant]]("check_in_time")
    def checkOutTime: Rep[Option[Instant]] = column[Option[Instant]]("check_out_time")

    def `*`: ProvenShape[ChildToActivityRelationship] = (childId, activityId, checkInTime, checkOutTime) <> (ChildToActivityRelationship.tupled, ChildToActivityRelationship.unapply)

    def childFk = foreignKey("child_id", childId, childrenTq)(_.id)
    def activityFk = foreignKey("activity_id", activityId, activitiesTq)(_.id)
  }

}
