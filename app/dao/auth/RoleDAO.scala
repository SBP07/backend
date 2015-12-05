package dao.auth

import java.util.UUID

import models.Role
import models.tenant.AuthCrewUser


import scala.concurrent.Future

trait RoleDAO {
  def getRoles(userId: UUID): Future[Set[Role]]
}
