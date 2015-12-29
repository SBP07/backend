package dao.tenant

import java.sql.Timestamp
import java.time.Instant
import java.util.UUID
import javax.inject.Inject

import play.api.data.validation.ValidationError
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads._


import dao.RowAlreadyExistsException
import dao.auth.DBTableDefinitions
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.language.postfixOps
import models.tenant._
import dao.{NonExistantCrewOrActivityOrDontBelongToTenantException, RowAlreadyExistsException}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CrewToActivityDao @Inject()(
  val dbConfigProvider: DatabaseConfigProvider,
  val activityRepo: ActivityRepo
) extends DBTableDefinitions
{
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  val driver = dbConfig.driver

  import dbConfig.driver.api._

  val db = dbConfig.db

  // TableQueries
  val crewToActivityTq = TableQuery[CrewToActivityTable]
  val activitiesTq = activityRepo.tableQuery
  val crewTq = TableQuery[Users]

  implicit val myInstantColumnType = MappedColumnType.base[Instant, Timestamp](
    instant => Timestamp.from(instant),
    timestamp => timestamp.toInstant
  )

  def activitiesForCrew(tenantCanonicalName: String, crewId: UUID): Future[Seq[(Activity, CrewToActivityRelationship)]] = {
    val query = for {
      crewToActivity <- crewToActivityTq if crewToActivity.crewId === crewId
      activity <- activitiesTq if activity.id === crewToActivity.activityId &&
      activity.tenantCanonicalName === tenantCanonicalName // Just to be sure
    } yield (activity, crewToActivity)

    db.run(query.result)
  }

  def crewForActivity(tenantCanonicalName: String, activityId: UUID): Future[Seq[DBUser]] = {
    val query = for {
      crewToActivity <- crewToActivityTq if crewToActivity.activityId === activityId
      crew <- crewTq if crew.id === crewToActivity.crewId &&
      crew.tenantCanonicalName === tenantCanonicalName // Just to be sure
    } yield crew

    db.run(query.result)
  }

  def insert(tenantCanonicalName: String, relationship: CrewToActivityRelationship): Future[Int] = {
    db.run {
      (for {
        crew <- crewTq if crew.id === relationship.crewId && crew.tenantCanonicalName === tenantCanonicalName
        activity <- activitiesTq if activity.id === relationship.activityId &&
        activity.tenantCanonicalName === tenantCanonicalName
      } yield (crew, activity)).length.result
    }.flatMap { numRows =>
      if(numRows == 1) {
        db.run {
          (for {
            crewToActivity <- crewToActivityTq if crewToActivity.crewId === relationship.crewId &&
            crewToActivity.activityId === relationship.activityId
          } yield crewToActivity).length.result
        }.flatMap { numRows =>
          if(numRows == 0){
            db.run(crewToActivityTq += relationship)
          } else {
            Future.failed(new RowAlreadyExistsException(tenantCanonicalName))
          }
        }
      } else {
        Future.failed(new NonExistantCrewOrActivityOrDontBelongToTenantException(tenantCanonicalName))
      }
    }
  }

  def deleteRelationship(tenantCanonicalName: String, crewId: UUID, activityId: UUID): Future[Int] = {
    db.run {
      (for {
        crewToActivity <- crewToActivityTq if crewToActivity.crewId === crewId && crewToActivity.activityId === activityId
        crew <- crewToActivity.crewFk if crew.tenantCanonicalName === tenantCanonicalName
        activity <- crewToActivity.activityFk if activity.tenantCanonicalName === tenantCanonicalName
      } yield {
        crewToActivity
      }).length.result
    }.flatMap { numFound =>
      if (numFound == 0) {
        Future.failed(new NonExistantCrewOrActivityOrDontBelongToTenantException(tenantCanonicalName))
      } else {
        db.run {
          crewToActivityTq
            .filter(_.crewId === crewId)
            .filter(_.activityId === activityId)
            .delete
        }
      }
    }
  }

  // does not extend BelongsToTenant because there is no need
  // Crew members belong to just one tenant, so do activities
  class CrewToActivityTable(tag: Tag) extends Table[CrewToActivityRelationship](tag, "crew_to_activity") {
    def crewId: Rep[UUID] = column[UUID]("crew_id")
    def activityId: Rep[UUID] = column[UUID]("activity_id")

    def checkInTime: Rep[Option[Instant]] = column[Option[Instant]]("check_in_time")
    def checkOutTime: Rep[Option[Instant]] = column[Option[Instant]]("check_out_time")

    def `*`: ProvenShape[CrewToActivityRelationship] = (crewId, activityId, checkInTime, checkOutTime) <>
      (CrewToActivityRelationship.tupled, CrewToActivityRelationship.unapply)

    def crewFk = foreignKey("crew_id", crewId, crewTq)(_.id)
    def activityFk = foreignKey("activity_id", activityId, activitiesTq)(_.id)
  }

}
