package dao.auth

import java.sql.Date
import java.time.LocalDate
import java.util.UUID

import play.api.libs.json._
import com.mohiva.play.silhouette.api.LoginInfo
import dao.admin.TenantRepo
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape.proveShapeOf

trait DBTableDefinitions {

  protected val driver: JdbcProfile
  import driver.api._

  val users = TableQuery[Users]
  val tenants = TableQuery[TenantRepo.TenantTable]

  case class DBUser (
    userID: Option[UUID],
    firstName: Option[String],
    lastName: Option[String],
    fullName: Option[String],
    email: Option[String],
    avatarURL: Option[String],
    street: Option[String],
    zipCode: Option[Int],
    city: Option[String],
    country: Option[String],
    birthDate: Option[LocalDate],
    tenantCanonicalName: String
  )

  implicit val dbUserJsonWrites = Json.writes[DBUser]

  case class DBUserRole(userId: UUID, roleId: String)

  class UsersToRoles(tag: Tag) extends Table[DBUserRole](tag, "auth_user_to_roles") {
    def userId = column[UUID]("user_id")
    def roleId = column[String]("role_id")

    def * = (userId, roleId) <> (DBUserRole.tupled, DBUserRole.unapply)

    def userFk = foreignKey("user_fk", userId, users)(_.id)
  }

  class Users(tag: Tag) extends Table[DBUser](tag, "auth_user") {
    implicit val JavaLocalDateMapper = MappedColumnType.base[LocalDate, Date](
      d => java.sql.Date.valueOf(d),
      d => d.toLocalDate
    )

    def id = column[UUID]("userID", O.PrimaryKey, O.AutoInc)
    def firstName = column[Option[String]]("firstName")
    def lastName = column[Option[String]]("lastName")
    def fullName = column[Option[String]]("fullName")
    def email = column[Option[String]]("email")
    def avatarURL = column[Option[String]]("avatarURL")
    def street = column[Option[String]]("address_street")
    def zipCode = column[Option[Int]]("address_zipcode")
    def city = column[Option[String]]("address_city")
    def country = column[Option[String]]("address_country")
    def birthDate = column[Option[LocalDate]]("birth_date")
    def tenantCanonicalName = column[String]("tenant_cname")

    def * = (id.?, firstName, lastName, fullName, email, avatarURL, street, zipCode, city, country, birthDate,
      tenantCanonicalName) <> (DBUser.tupled, DBUser.unapply)

    def tenantFk = foreignKey("tenant_fk", tenantCanonicalName, tenants)(_.canonicalName)
  }

  case class DBLoginInfo (
    id: Option[Long],
    providerID: String,
    providerKey: String
  )

  class LoginInfos(tag: Tag) extends Table[DBLoginInfo](tag, "auth_logininfo") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def providerID = column[String]("providerID")
    def providerKey = column[String]("providerKey")
    def * = (id.?, providerID, providerKey) <> (DBLoginInfo.tupled, DBLoginInfo.unapply)
  }

  case class DBUserLoginInfo (
    userID: UUID,
    loginInfoId: Long
  )

  class UserLoginInfos(tag: Tag) extends Table[DBUserLoginInfo](tag, "auth_userlogininfo") {
    def userID = column[UUID]("userID")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (userID, loginInfoId) <> (DBUserLoginInfo.tupled, DBUserLoginInfo.unapply)
  }

  case class DBPasswordInfo (
    hasher: String,
    password: String,
    salt: Option[String],
    loginInfoId: Long
  )

  class PasswordInfos(tag: Tag) extends Table[DBPasswordInfo](tag, "auth_passwordinfo") {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (hasher, password, salt, loginInfoId) <> (DBPasswordInfo.tupled, DBPasswordInfo.unapply)
  }

  case class DBOAuth1Info (
    id: Option[Long],
    token: String,
    secret: String,
    loginInfoId: Long
  )

  class OAuth1Infos(tag: Tag) extends Table[DBOAuth1Info](tag, "auth_oauth1info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def token = column[String]("token")
    def secret = column[String]("secret")
    def loginInfoId = column[Long]("loginInfoId")
    def * = (id.?, token, secret, loginInfoId) <> (DBOAuth1Info.tupled, DBOAuth1Info.unapply)
  }

  case class DBOAuth2Info (
    id: Option[Long],
    accessToken: String,
    tokenType: Option[String],
    expiresIn: Option[Int],
    refreshToken: Option[String],
    loginInfoId: Long
  )

  class OAuth2Infos(tag: Tag) extends Table[DBOAuth2Info](tag, "auth_oauth2info") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def accessToken = column[String]("accesstoken")
    def tokenType = column[Option[String]]("tokentype")
    def expiresIn = column[Option[Int]]("expiresin")
    def refreshToken = column[Option[String]]("refreshtoken")
    def loginInfoId = column[Long]("logininfoid")
    def * = (id.?, accessToken, tokenType, expiresIn, refreshToken, loginInfoId) <> (DBOAuth2Info.tupled, DBOAuth2Info.unapply)
  }

  case class DBOpenIDInfo (
    id: String,
    loginInfoId: Long
  )

  class OpenIDInfos(tag: Tag) extends Table[DBOpenIDInfo](tag, "auth_openidinfo") {
    def id = column[String]("id", O.PrimaryKey)
    def loginInfoId = column[Long]("logininfoid")
    def * = (id, loginInfoId) <> (DBOpenIDInfo.tupled, DBOpenIDInfo.unapply)
  }

  case class DBOpenIDAttribute (
    id: String,
    key: String,
    value: String
  )

  class OpenIDAttributes(tag: Tag) extends Table[DBOpenIDAttribute](tag, "auth_openidattributes") {
    def id = column[String]("id")
    def key = column[String]("key")
    def value = column[String]("value")
    def * = (id, key, value) <> (DBOpenIDAttribute.tupled, DBOpenIDAttribute.unapply)
  }

  // table query definitions
  val slickUsers = TableQuery[Users]
  val slickLoginInfos = TableQuery[LoginInfos]
  val slickUserLoginInfos = TableQuery[UserLoginInfos]
  val slickPasswordInfos = TableQuery[PasswordInfos]
  val slickOAuth1Infos = TableQuery[OAuth1Infos]
  val slickOAuth2Infos = TableQuery[OAuth2Infos]
  val slickOpenIDInfos = TableQuery[OpenIDInfos]
  val slickOpenIDAttributes = TableQuery[OpenIDAttributes]

  val slickUserRoles = TableQuery[UsersToRoles]

  // queries used in multiple places
  def loginInfoQuery(loginInfo: LoginInfo) =
    slickLoginInfos.filter(dbLoginInfo => dbLoginInfo.providerID === loginInfo.providerID && dbLoginInfo.providerKey === loginInfo.providerKey)
}
