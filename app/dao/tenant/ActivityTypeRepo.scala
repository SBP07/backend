package dao.tenant

import java.sql.Date
import java.time.LocalDate
import java.util.UUID

import io.strongtyped.active.slick._
import models.helpers.{TenantEntityActions, BelongsToTenantTable}
import slick.ast.BaseTypedType

import io.strongtyped.active.slick.Lens._
import slick.lifted.ProvenShape
import scala.language.postfixOps
import scala.concurrent.ExecutionContext.Implicits.global
import models.tenant.ActivityType

class ActivityTypeRepo extends TenantEntityActions[ActivityType, UUID] {
  import jdbcProfile.api._

  val baseTypedType = implicitly[BaseTypedType[Id]]

  type EntityTable = ActivityTypeTable

  val tableQuery = TableQuery[ActivityTypeTable]

  def $id(table: ActivityTypeTable): Rep[Id] = table.id

  val idLens = lens { activityType: ActivityType => activityType.id } { (activityType, id) => activityType.copy(id = id) }

  implicit val myDateColumnType = MappedColumnType.base[LocalDate, Date](
    ld => Date.valueOf(ld),
    d => d.toLocalDate
  )

  class ActivityTypeTable(tag: Tag) extends Table[ActivityType](tag, "activity_type") with BelongsToTenantTable {
    def id = column[Id]("id", O.PrimaryKey, O.AutoInc)

    def mnemonic = column[String]("mnemonic")
    def description = column[String]("description")

    def tenantCanonicalName = column[String]("tenant_canonical_name")

    def * : ProvenShape[ActivityType] = (id.?, mnemonic, description, tenantCanonicalName) <> (ActivityType.tupled, ActivityType.unapply _)

  }

}
