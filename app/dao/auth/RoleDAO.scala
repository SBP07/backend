package dao.auth

import java.util.UUID

import models.Role
import models.tenant.Crew


import scala.concurrent.Future

trait RoleDAO {
  def getRoles(userId: UUID): Future[Set[Role]]
}
