package be.thomastoye.speelsysteem.data.couchdb

import javax.inject.Inject

import be.thomastoye.speelsysteem.exceptions.ConfigurationMissingFieldException
import be.thomastoye.speelsysteem.models.{Child, Crew}
import com.ibm.couchdb._
import play.Logger
import play.api.Configuration

import scalaz.{-\/, \/-}

object CouchConfiguration {
  def fromConfig(config: Configuration): CouchConfiguration = {
    CouchConfiguration(
      config.getString("couchdb.server.host").getOrElse(throw ConfigurationMissingFieldException("couchdb.db.host")),
      config.getInt("couchdb.server.port").getOrElse(throw ConfigurationMissingFieldException("couchdb.db.port")),
      config.getBoolean("couchdb.server.https").getOrElse(throw ConfigurationMissingFieldException("couchdb.db.https")),
      config.getString("couchdb.server.user"),
      config.getString("couchdb.server.pass"),
      config.getString("couchdb.server.db").getOrElse(throw ConfigurationMissingFieldException("couchdb.server.db"))
    )
  }
}

case class CouchConfiguration(host: String, port: Int, https: Boolean, user: Option[String], pass: Option[String], db: String)

object CouchDatabase {
  case class CouchPersistenceException(msg: String) extends Exception(msg) // TODO is this used?
}

class CouchDatabase @Inject()(config: Configuration) {
  private val couchConfig = CouchConfiguration.fromConfig(config)
  val couchdb = (for (user <- couchConfig.user; pass <- couchConfig.pass)
    yield CouchDb(couchConfig.host, couchConfig.port, couchConfig.https, user, pass)
  ) getOrElse CouchDb(couchConfig.host, couchConfig.port, couchConfig.https)

  couchdb.server.info.unsafePerformAsync {
      case -\/(e) =>   Logger.warn("Could not connect to CouchDB", e)
      case \/-(res) => Logger.info(s"Successfully connected to CouchDB ${res.version} (vendor: ${res.vendor.name}): ${res.couchdb}")
  }

  val db = couchdb.db(couchConfig.db, TypeMapping(
    classOf[Crew] -> CouchCrewRepository.crewKind,
    classOf[Child] -> CouchChildRepository.childKind
  ))
}
