package dao.auth

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.tenant.AuthCrewUser
import models.Role
import play.api.db.slick.DatabaseConfigProvider

import scalaz.Scalaz._

import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

/**
  * Give access to the user object using Slick
  */
class UserDAOImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider, roleDAO: RoleDAO) extends UserDAO with DAOSlick {

  import driver.api._

  /**
    * Finds a user by its login info.
    *
    * @param loginInfo The login info of the user to find.
    * @return The found user or None if no user for the given login info could be found.
    */
  def find(loginInfo: LoginInfo) = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- slickUsers.filter(_.id === dbUserLoginInfo.userID)
    } yield dbUser
    db.run(userQuery.result.headOption).map { dbUserOption =>
      dbUserOption.map { user =>
        AuthCrewUser(
          UUID.fromString(user.userID),
          loginInfo,
          user.firstName,
          user.lastName,
          user.fullName,
          user.email,
          user.avatarURL,
          Set.empty[Role] // TODO
        )
      }
    }
  }

  def find(userID: UUID): Future[Option[AuthCrewUser]] = {
    val query = for {
      dbUser <- slickUsers.filter(_.id === userID.toString)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
      dbLoginInfo <- slickLoginInfos.filter(_.id === dbUserLoginInfo.loginInfoId)
    } yield (dbUser, dbLoginInfo)


    val res = db.run(query.result.headOption).map { resultOption =>
      resultOption.map { result =>
        roleDAO.getRoles(resultOption.get._1.userID).map { roles =>
          val user = result._1
          val loginInfo = result._2
          AuthCrewUser(
            UUID.fromString(user.userID),
            LoginInfo(loginInfo.providerID, loginInfo.providerKey),
            user.firstName,
            user.lastName,
            user.fullName,
            user.email,
            user.avatarURL,
            roles
          )
        }
      }
    }

    res.map(_.sequence).join // scalaz magic: Future[Option[Future[X]]] => Future[Option[X]]
  }


  /**
    * Saves a user.
    *
    * @param user The user to save.
    * @return The saved user.
    */
  def save(user: AuthCrewUser): Future[AuthCrewUser] = {
    val dbUser = DBUser(user.userID.toString, user.firstName, user.lastName, user.fullName, user.email, user.avatarURL)
    val dbUserRoles: Set[DBUserRole] = user.roles.map(role => DBUserRole(user.userID.toString, role.name))
    val dbLoginInfo = DBLoginInfo(None, user.loginInfo.providerID, user.loginInfo.providerKey)
    // We don't have the LoginInfo id so we try to get it first.
    // If there is no LoginInfo yet for this user we retrieve the id on insertion.    
    val loginInfoAction = {
      val retrieveLoginInfo = slickLoginInfos.filter(
        info => info.providerID === user.loginInfo.providerID &&
          info.providerKey === user.loginInfo.providerKey).result.headOption
      val insertLoginInfo = slickLoginInfos.returning(slickLoginInfos.map(_.id)).
        into((info, id) => info.copy(id = Some(id))) += dbLoginInfo
      for {
        loginInfoOption <- retrieveLoginInfo
        loginInfo <- loginInfoOption.map(DBIO.successful).getOrElse(insertLoginInfo)
      } yield loginInfo
    }
    // combine database actions to be run sequentially
    val actions = (for {
      _ <- slickUsers.insertOrUpdate(dbUser)
      loginInfo <- loginInfoAction
      _ <- slickUserLoginInfos += DBUserLoginInfo(dbUser.userID, loginInfo.id.get)
      _ <- slickUserRoles ++= dbUserRoles
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions).map(_ => user)
  }
}
