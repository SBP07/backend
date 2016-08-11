package be.thomastoye.speelsysteem.data.couchdb

import javax.inject.Inject

import be.thomastoye.speelsysteem.data.{PlayJsonReaderUpickleCompat, PlayJsonWriterUpickleCompat}
import com.typesafe.scalalogging.StrictLogging
import upickle.default.{Reader, Writer}
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
  val dayKind = "type/day/v1"

  implicit val dayReader: Reader[Day] = new PlayJsonReaderUpickleCompat[Day]
  implicit val dayWriter: Writer[Day] = new PlayJsonWriterUpickleCompat[Day]
}

class CouchDayService @Inject() (couchDatabase: CouchDatabase) extends StrictLogging {
  import CouchDayService._

  val db = couchDatabase.db


  def findAll: Future[Seq[(Id, Day)]] = {
   db.docs.getMany
     .byTypeUsingTemporaryView(MappedDocType(dayKind))
     .includeDocs[Day].build.query.toFuture
     .map(res => res.getDocs.map(doc => (doc._id, doc.doc)))
     .map(_.sortBy(x => x._2.date))
  }

  def findAttendancesForChild(id: Child.Id): Future[Seq[Day]] = {
    db.docs.get[Child](id)(CouchChildRepository.childReader).toFuture.map(_.doc).flatMap(findAttendancesForChild)
  }

  private def findAttendancesForChild(child: Child): Future[Seq[Day]] = {
    val daysChildAttended = child.attendances.map(_.day)
    db.docs.getMany[Day](daysChildAttended).toFuture.map(_.getDocs).map { allDays =>
      allDays.map { day =>
        val shiftsAttendedWithDetails = if(daysChildAttended.contains(day._id)) {
          val shiftsOnDay = allDays.filter(_._id == day._id).head.doc.shifts
          val shiftsChildAttended = child.attendances.filter(_.day == day._id).head.shifts
          Some(shiftsOnDay.filter(shiftsChildAttended.contains))
        } else None

        Day(day.doc.date, shiftsAttendedWithDetails.toSeq.flatten)
      }
    }
  }
}
