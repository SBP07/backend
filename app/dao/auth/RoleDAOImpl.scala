package dao.auth

import javax.inject.Inject

import models.Role
import models.tenant.AuthCrewUser
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RoleDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends RoleDAO with DAOSlick {
  import driver.api._

  val rolesTable = TableQuery[Roles]
  val usersToRoles = TableQuery[UsersToRoles]

  override def getRoles(userId: String): Future[Set[Role]] = {
    val query = for {
      userToRole <- usersToRoles if userToRole.userId === userId
      role <- rolesTable if role.id === userToRole.roleId
    } yield {
      role
    }

    db.run(query.result).map { _.map(dbRole => Role.apply(dbRole.id)).toSet }
  }
}
