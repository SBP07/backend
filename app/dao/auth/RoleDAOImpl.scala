package dao.auth

import java.util.UUID
import javax.inject.Inject

import models.Role
import models.tenant.AuthCrewUser
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RoleDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends RoleDAO with DAOSlick {
  import driver.api._

  val usersToRoles = TableQuery[UsersToRoles]

  override def getRoles(userId: UUID): Future[Set[Role]] = {
    val query = for {
      userToRole <- usersToRoles if userToRole.userId === userId
    } yield {
      userToRole.roleId
    }

    db.run(query.result).map { _.map(roleId => Role.apply(roleId)).toSet }
  }
}
