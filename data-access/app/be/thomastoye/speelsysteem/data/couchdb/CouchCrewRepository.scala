package be.thomastoye.speelsysteem.data.couchdb

import javax.inject.Inject

import be.thomastoye.speelsysteem.data.{CrewRepository, PlayJsonReaderUpickleCompat, PlayJsonWriterUpickleCompat}
import be.thomastoye.speelsysteem.data.util.ScalazExtensions.PimpedScalazTask
import upickle.default.{Reader, Writer}
import be.thomastoye.speelsysteem.models._
import be.thomastoye.speelsysteem.models.Crew.Id
import be.thomastoye.speelsysteem.models.JsonFormats._
import com.ibm.couchdb.{CouchDoc, MappedDocType}
import com.typesafe.scalalogging.StrictLogging
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.{Future, Promise}
import scalaz.{-\/, \/-}

object CouchCrewRepository {
  val crewKind = "type/crew/v1"

  implicit val crewReader: Reader[Crew] = new PlayJsonReaderUpickleCompat[Crew]
  implicit val crewWriter: Writer[Crew] = new PlayJsonWriterUpickleCompat[Crew]
}

class CouchCrewRepository @Inject() (couchDatabase: CouchDatabase) extends CrewRepository with StrictLogging {
  import CouchCrewRepository._

  val db = couchDatabase.db

  override def findById(id: Id): Future[Option[(Id, Crew)]] = findAll.map(_.find(_._1 == id))

  override def findAll: Future[Seq[(Id, Crew)]] = {
    val p: Promise[Seq[(Id, Crew)]] = Promise()

    db.docs.getMany.byTypeUsingTemporaryView(MappedDocType(crewKind)).includeDocs[Crew].build.query.unsafePerformAsync {
      case \/-(res) => p.success(res.getDocs.map(doc => (doc._id, doc.doc)))
      case -\/(e)   => p.failure(e)
    }

    p.future.map(_.sortBy(x => (x._2.lastName, x._2.firstName)))
  }

  override def insert(crewMember: Crew): Future[Unit] = db.docs.create(crewMember).toFuture.map(_ => ())

  override def count: Future[Int] = findAll.map(_.length)

  override def update(id: Id, crewMember: Crew): Future[Unit] = {
    for{
      currentRev <- db.docs.get[Crew](id).toFuture.map(_._rev)
      res        <- db.docs.update[Crew](CouchDoc(crewMember, crewKind, _id = id, _rev = currentRev)).toFuture
    } yield { () }
  }
}
