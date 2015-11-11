package dao

import io.strongtyped.active.slick._

trait RepoFor[M, I] extends EntityActions with PostgresProfileProvider { // TODO break this out in seperate file
  type Entity = M
  type Id = I // TODO Make this UUID
}
