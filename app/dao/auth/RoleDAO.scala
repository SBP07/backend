package dao.auth

import models.Role
import models.tenant.AuthCrewUser


import scala.concurrent.Future

trait RoleDAO {
  def getRoles(userId: String): Future[Set[Role]]
}
