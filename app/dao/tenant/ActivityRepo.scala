package dao.tenant

import java.sql.{Timestamp, Date}
import java.time.{Instant, LocalDate}
import java.util.UUID

import io.strongtyped.active.slick._
import models.helpers.{TenantEntityActions, BelongsToTenantTable}
import slick.ast.BaseTypedType

import io.strongtyped.active.slick.Lens._
import slick.lifted.ProvenShape
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import models.tenant.Activity

class ActivityRepo extends TenantEntityActions[Activity, UUID] {

  import jdbcProfile.api._

  val baseTypedType = implicitly[BaseTypedType[Id]]

  type EntityTable = ActivityTable

  val tableQuery = TableQuery[ActivityTable]

  def $id(table: ActivityTable): Rep[Id] = table.id

  val idLens = lens { activity: Activity => activity.id } { (activity, id) => activity.copy(id = id) }

  implicit val myDateColumnType = MappedColumnType.base[LocalDate, Date](
    ld => Date.valueOf(ld),
    d => d.toLocalDate
  )
  implicit val myInstantColumnType = MappedColumnType.base[Instant, Timestamp](
    instant => Timestamp.from(instant),
    timestamp => timestamp.toInstant
  )

  class ActivityTable(tag: Tag) extends Table[Activity](tag, "activity") with BelongsToTenantTable {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)
    def place = column[Option[String]]("place")
    def activityTypeId = column[UUID]("activity_type_id")
    def date = column[LocalDate]("date")
    def startTime = column[Option[Instant]]("start_time")
    def endTime = column[Option[Instant]]("end_time")
    def tenantCanonicalName = column[String]("tenant_canonical_name")

    def * : ProvenShape[Activity] = (id.?, place, activityTypeId, date, startTime, endTime, tenantCanonicalName) <> (Activity.tupled, Activity.unapply)

  }

}
