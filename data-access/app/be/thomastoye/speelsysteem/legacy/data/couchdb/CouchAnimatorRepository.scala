package be.thomastoye.speelsysteem.legacy.data.couchdb

import javax.inject.Inject

import be.thomastoye.speelsysteem.legacy.data.util.ScalazExtensions.PimpedScalazTask
import upickle.default.{Reader, Writer}
import be.thomastoye.speelsysteem.legacy.data.{AnimatorRepository, PlayJsonReaderUpickleCompat, PlayJsonWriterUpickleCompat}
import be.thomastoye.speelsysteem.legacy.models.Animator
import be.thomastoye.speelsysteem.models._
import be.thomastoye.speelsysteem.models.JsonFormats._
import com.ibm.couchdb.{CouchException, CouchView, MappedDocType}
import com.typesafe.scalalogging.StrictLogging
import org.joda.time.LocalDate
import play.api.libs.concurrent.Execution.Implicits._

import scala.util.Try
import scala.concurrent.{Future, Promise}
import scalaz.concurrent.Task
import scalaz.{-\/, \/-}

class CouchAnimatorRepository @Inject() (couchDatabase: CouchDatabase) extends AnimatorRepository with StrictLogging {
  val db = couchDatabase.db

  implicit val crewReader: Reader[Crew] = new PlayJsonReaderUpickleCompat[Crew](crewFormat)
  implicit val crewWriter: Writer[Crew] = new PlayJsonWriterUpickleCompat[Crew](crewFormat)

  override def findById(id: Long): Future[Option[Animator]] = findAll.map(_.find(_.id.contains(id)))

  override def findAll: Future[Seq[Animator]] = {
    val p: Promise[Seq[Animator]] = Promise()

    db.docs.getMany.byTypeUsingTemporaryView(MappedDocType("type/crew/v1")).includeDocs[Crew].build.query.unsafePerformAsync {
      case \/-(res) =>
        p.success(res.getDocs.map(_.doc).map(crew2legacyModel))
        p.success(Seq.empty)
      case -\/(e)   => p.failure(e)
    }

    p.future.map(_.sortBy(x => (x.lastName, x.firstName)))
  }

  override def insert(animator: Animator): Future[Unit] = db.docs.create(legacyModel2crew(animator)).toFuture.map(_ =>())

  override def count: Future[Int] = findAll.map(_.length)

  override def update(animator: Animator): Future[Unit] = ???

  private def crew2legacyModel(crew: Crew): Animator = {
    Animator(
      crew.legacyId,
      crew.firstName,
      crew.lastName,
      crew.contact.phone.find(_.kind.contains("mobile")).map(_.phoneNumber),
      crew.contact.phone.find(_.kind.contains("landline")).map(_.phoneNumber),
      crew.contact.email.headOption,
      for { street <- crew.address.street; nr <- crew.address.number } yield { s"$street $nr" },
      crew.address.city.map(x => crew.address.zipCode.map(_ + " ").getOrElse("") + x),
      crew.bankAccount,
      crew.yearStarted,
      isPartOfCore = false,
      crew.birthDate.map(day => new LocalDate(day.year, day.month, day.day))
    )
  }

  private def legacyModel2crew(animator: Animator): Crew = {
    val address = Address(
      animator.street.map(x => x.split(" ").drop(1).mkString(" ")),
      animator.street.map(x => x.split(" ").last),
      animator.city.flatMap(x => Try(x.split(" ").head.toInt).toOption),
      animator.city.map(_.split(" ").tail.mkString(" "))
    )
    val contact = CrewContact(
      animator.mobilePhone.map(PhoneContact(Some("mobile"), None, _)).toSeq ++ animator.landline.map(PhoneContact(Some("landline"), None, _)).toSeq,
      animator.email.toSeq
    )
    val birthDate = animator.birthDate.map(day => Day(day.getDayOfMonth, day.getMonthOfYear, day.getYear))

    Crew(
      animator.firstName,
      animator.lastName,
      address,
      animator.bankAccount,
      contact,
      animator.yearStartedVolunteering,
      birthDate,
      None
    )
  }
}
