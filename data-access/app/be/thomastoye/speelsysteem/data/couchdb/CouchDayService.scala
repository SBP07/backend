package be.thomastoye.speelsysteem.data.couchdb

import javax.inject.Inject

import be.thomastoye.speelsysteem.data.{PlayJsonReaderUpickleCompat, PlayJsonWriterUpickleCompat}
import com.typesafe.scalalogging.StrictLogging
import be.thomastoye.speelsysteem.models._
import be.thomastoye.speelsysteem.models.JsonFormats._
import be.thomastoye.speelsysteem.data.util.ScalazExtensions.PimpedScalazTask
import be.thomastoye.speelsysteem.models.Day.Id
import com.ibm.couchdb.{CouchDoc, MappedDocType}
import com.typesafe.scalalogging.StrictLogging
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.{Future, Promise}
import scalaz.{-\/, \/-}


object CouchDayService extends StrictLogging {
  val shiftKind = "type/shift/v1"
  val dayKind = "type/day/v1"
}

class CouchDayService @Inject() (couchDatabase: CouchDatabase) extends StrictLogging {
  import CouchDayService.shiftKind

  val db = couchDatabase.db

  implicit val shiftReader = new PlayJsonReaderUpickleCompat[Day]
  implicit val shiftWriter = new PlayJsonWriterUpickleCompat[Day]

  def findAll: Future[Seq[(Id, Day)]] = {
   db.docs.getMany
     .byTypeUsingTemporaryView(MappedDocType(shiftKind))
     .includeDocs[Day].build.query.toFuture
     .map(res => res.getDocs.map(doc => (doc._id, doc.doc)))
     .map(_.sortBy(x => x._2.date))
  }
}
