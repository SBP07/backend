package dao.auth

import java.util.UUID
import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import models.tenant.{Address, Crew}
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
  def find(loginInfo: LoginInfo): Future[Option[Crew]] = {
    val userQuery = for {
      dbLoginInfo <- loginInfoQuery(loginInfo)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.loginInfoId === dbLoginInfo.id)
      dbUser <- slickUsers.filter(_.id === dbUserLoginInfo.userID)
      dbRoles <- slickUserRoles.filter(_.userId === dbUserLoginInfo.userID)
    } yield (dbUser, dbRoles)
    db.run(userQuery.result).map { dbOption =>
      dbOption
        .groupBy(_._1)
        .map { case (k, v) => (k, v.map(_._2)) }
        .headOption
        .map { userWithRoles =>
          val user = userWithRoles._1
          val roles = userWithRoles._2.map(dbRole => Role(dbRole.roleId)).toSet
          val address = for {
            street <- user.street; city <- user.city; zipCode <- user.zipCode; country <- user.country
          } yield {
            Address(street, zipCode, city, country)
          }
          Crew(
            user.userID,
            loginInfo,
            user.firstName,
            user.lastName,
            user.fullName,
            user.email,
            user.avatarURL,
            user.birthDate,
            address,
            roles,
            user.tenantCanonicalName
          )
        }
    }
  }

  def find(userID: UUID): Future[Option[Crew]] = {
    val query = for {
      dbUser <- slickUsers.filter(_.id === userID)
      dbUserLoginInfo <- slickUserLoginInfos.filter(_.userID === dbUser.id)
      dbLoginInfo <- slickLoginInfos.filter(_.id === dbUserLoginInfo.loginInfoId)
    } yield (dbUser, dbLoginInfo)

    val res = db.run(query.result.headOption).map { resultOption =>
      resultOption.flatMap { result =>
        resultOption.get._1.userID.map { userId =>
          roleDAO.getRoles(userId).map { roles =>
            val user = result._1
            val loginInfo = result._2
            val address = for {
              street <- user.street; city <- user.city; zipCode <- user.zipCode; country <- user.country
            } yield {
              Address(street, zipCode, city, country)
            }
            Crew(
              user.userID,
              LoginInfo(loginInfo.providerID, loginInfo.providerKey),
              user.firstName,
              user.lastName,
              user.fullName,
              user.email,
              user.avatarURL,
              user.birthDate,
              address,
              roles,
              user.tenantCanonicalName
            )
          }
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
  def save(user: Crew): Future[Crew] = {
    val dbUser = DBUser(user.userID, user.firstName, user.lastName, user.fullName, user.email, user.avatarURL,
      user.address.map(_.street), user.address.map(_.zipCode), user.address.map(_.city), user.address.map(_.country),
      user.birthDate, user.tenantCanonicalName)
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
      maybeUserId <- (slickUsers returning slickUsers.map(_.id)).insertOrUpdate(dbUser)
      loginInfo <- loginInfoAction
      _ <- slickUserLoginInfos += DBUserLoginInfo(maybeUserId.get, loginInfo.id.get)
      _ <- slickUserRoles ++= user.roles.map(role => DBUserRole(maybeUserId.get, role.name))
    } yield ()).transactionally
    // run actions and return user afterwards
    db.run(actions).map(_ => user)
  }
}
