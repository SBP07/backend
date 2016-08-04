package be.thomastoye.speelsysteem.legacy.data.couchdb

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.data.couchdb.CouchDatabase.CouchPersistenceException
import be.thomastoye.speelsysteem.legacy.data.util.ScalazExtensions.PimpedScalazTask
import upickle.default.{Reader, Writer}
import be.thomastoye.speelsysteem.legacy.data.{AnimatorRepository, PlayJsonReaderUpickleCompat, PlayJsonWriterUpickleCompat}
import be.thomastoye.speelsysteem.legacy.models.Animator
import be.thomastoye.speelsysteem.models._
import be.thomastoye.speelsysteem.models.JsonFormats._
import com.ibm.couchdb.{CouchDoc, CouchException, CouchView, MappedDocType}
import com.typesafe.scalalogging.StrictLogging
import org.joda.time.LocalDate
import play.api.libs.concurrent.Execution.Implicits._

import scala.util.Try
import scala.concurrent.{Future, Promise}
import scalaz.{-\/, \/-}

object CouchAnimatorRepository {
  val crewKind = "type/crew/v1"
}

class CouchAnimatorRepository @Inject() (couchDatabase: CouchDatabase) extends AnimatorRepository with StrictLogging {
  import CouchAnimatorRepository.crewKind

  val db = couchDatabase.db

  implicit val crewReader: Reader[Crew] = new PlayJsonReaderUpickleCompat[Crew]
  implicit val crewWriter: Writer[Crew] = new PlayJsonWriterUpickleCompat[Crew]

  override def findById(id: String): Future[Option[Animator]] = findAll.map(_.find(_.id.contains(id)))

  override def findAll: Future[Seq[Animator]] = {
    val p: Promise[Seq[Animator]] = Promise()

    db.docs.getMany.byTypeUsingTemporaryView(MappedDocType(crewKind)).includeDocs[Crew].build.query.unsafePerformAsync {
      case \/-(res) =>
        p.success(res.getDocs.map(doc => crew2legacyModel(doc.doc, Some(doc._id))))
        p.success(Seq.empty)
      case -\/(e)   => p.failure(e)
    }

    p.future.map(_.sortBy(x => (x.lastName, x.firstName)))
  }


  override def insert(animator: Animator): Future[Unit] = {
    val (crew, maybeId) = legacyModel2crewAndId(animator)
    (maybeId map { id => db.docs.create(crew, id) } getOrElse db.docs.create(crew)).toFuture.map(_ =>())
  }

  override def count: Future[Int] = findAll.map(_.length)

  override def update(animator: Animator): Future[Unit] = {
    val (crew, maybeId) = legacyModel2crewAndId(animator)
    maybeId map { id =>
      for {
        currentRev <- db.docs.get[Crew](id).toFuture.map(_._rev)
        res        <- db.docs.update[Crew](CouchDoc(crew, crewKind, _id = id, _rev = currentRev)).toFuture
      } yield ()
    } getOrElse(throw CouchPersistenceException("Crew did not have an id"))
  }

  private def crew2legacyModel(crew: Crew, id: Option[String]): Animator = {
    Animator(
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

  private def legacyModel2crewAndId(animator: Animator): (Crew, Option[String]) = {
    val address = Address(
      animator.street,
      animator.streetNumber,
      animator.zipCode,
      animator.city
    )
    val contact = CrewContact(
      animator.mobilePhone.map(PhoneContact(Some("mobile"), None, _)).toSeq ++ animator.landline.map(PhoneContact(Some("landline"), None, _)).toSeq,
      animator.email.toSeq
    )
    val birthDate = animator.birthDate.map(day => Day(day.getDayOfMonth, day.getMonthOfYear, day.getYear))

    val crew = Crew(
      animator.firstName,
      animator.lastName,
      address,
      animator.bankAccount,
      contact,
      animator.yearStartedVolunteering,
      birthDate
    )

    (crew, animator.id)
  }
}
