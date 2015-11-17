package dao

import io.strongtyped.active.slick._

trait RepoFor[M, I] extends EntityActions with PostgresProfileProvider {
  type Entity = M
  type Id = I
}
