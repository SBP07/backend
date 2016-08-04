package be.thomastoye.speelsysteem.legacy.data.couchdb

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.data.couchdb.CouchDatabase.CouchPersistenceException
import be.thomastoye.speelsysteem.legacy.data.util.ScalazExtensions.PimpedScalazTask
import upickle.default.{Reader, Writer}
import be.thomastoye.speelsysteem.legacy.data.{CrewRepository, PlayJsonReaderUpickleCompat, PlayJsonWriterUpickleCompat}
import be.thomastoye.speelsysteem.legacy.models.LegacyCrew
import be.thomastoye.speelsysteem.models._
import be.thomastoye.speelsysteem.models.JsonFormats._
import com.ibm.couchdb.{CouchDoc, CouchException, CouchView, MappedDocType}
import com.typesafe.scalalogging.StrictLogging
import org.joda.time.LocalDate
import play.api.libs.concurrent.Execution.Implicits._

import scala.util.Try
import scala.concurrent.{Future, Promise}
import scalaz.{-\/, \/-}

object CouchCrewRepository {
  val crewKind = "type/crew/v1"
}

class CouchCrewRepository @Inject() (couchDatabase: CouchDatabase) extends CrewRepository with StrictLogging {
  import CouchCrewRepository.crewKind

  val db = couchDatabase.db

  implicit val crewReader: Reader[Crew] = new PlayJsonReaderUpickleCompat[Crew]
  implicit val crewWriter: Writer[Crew] = new PlayJsonWriterUpickleCompat[Crew]

  override def findById(id: String): Future[Option[LegacyCrew]] = findAll.map(_.find(_.id.contains(id)))

  override def findAll: Future[Seq[LegacyCrew]] = {
    val p: Promise[Seq[LegacyCrew]] = Promise()

    db.docs.getMany.byTypeUsingTemporaryView(MappedDocType(crewKind)).includeDocs[Crew].build.query.unsafePerformAsync {
      case \/-(res) =>
        p.success(res.getDocs.map(doc => crew2legacyModel(doc.doc, Some(doc._id))))
        p.success(Seq.empty)
      case -\/(e)   => p.failure(e)
    }

    p.future.map(_.sortBy(x => (x.lastName, x.firstName)))
  }

//  TODO work in the database and legacy model, add/alter columns until legacy model == new model

  override def insert(crewMember: LegacyCrew): Future[Unit] = {
    val (crew, maybeId) = legacyModel2crewAndId(crewMember)
    (maybeId map { id => db.docs.create(crew, id) } getOrElse db.docs.create(crew)).toFuture.map(_ =>())
  }

  override def count: Future[Int] = findAll.map(_.length)

  override def update(crewMember: LegacyCrew): Future[Unit] = {
    val (crew, maybeId) = legacyModel2crewAndId(crewMember)
    maybeId map { id =>
      for {
        currentRev <- db.docs.get[Crew](id).toFuture.map(_._rev)
        res        <- db.docs.update[Crew](CouchDoc(crew, crewKind, _id = id, _rev = currentRev)).toFuture
      } yield ()
    } getOrElse(throw CouchPersistenceException("Crew did not have an id"))
  }

  private def crew2legacyModel(crew: Crew, id: Option[String]): LegacyCrew = {
    LegacyCrew(
      id,
      crew.firstName,
      crew.lastName,
      crew.contact.phone.find(_.kind.contains("mobile")).map(_.phoneNumber),
      crew.contact.phone.find(_.kind.contains("landline")).map(_.phoneNumber),
      crew.contact.email.headOption,
      crew.address.street,
      crew.address.number,
      crew.address.zipCode,
      crew.address.city,
      crew.bankAccount,
      crew.yearStarted,
      isPartOfCore = false,
      crew.birthDate.map(day => new LocalDate(day.year, day.month, day.day))
    )
  }

  private def legacyModel2crewAndId(legacyModel: LegacyCrew): (Crew, Option[String]) = {
    val address = Address(legacyModel.street, legacyModel.streetNumber, legacyModel.zipCode, legacyModel.city)

    val contact = CrewContact(
      legacyModel.mobilePhone.map(PhoneContact(Some("mobile"), None, _)).toSeq ++ legacyModel.landline.map(PhoneContact(Some("landline"), None, _)).toSeq,
      legacyModel.email.toSeq
    )
    val birthDate = legacyModel.birthDate.map(day => Day(day.getDayOfMonth, day.getMonthOfYear, day.getYear))

    val crew = Crew(
      legacyModel.firstName,
      legacyModel.lastName,
      address,
      legacyModel.bankAccount,
      contact,
      legacyModel.yearStartedVolunteering,
      birthDate
    )

    (crew, legacyModel.id)
  }
}
