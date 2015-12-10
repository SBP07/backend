package dao

import io.strongtyped.active.slick._
import models.helpers.{BelongsToTenant, TenantEntityActions}
import slick.dbio._

import scala.concurrent.ExecutionContext

trait RepoFor[M <: BelongsToTenant[M], I] extends TenantEntityActions with PostgresProfileProvider {
  type Entity = M
  type Id = I
}
