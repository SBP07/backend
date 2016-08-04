package be.thomastoye.speelsysteem.legacy.data.comparing

import com.typesafe.scalalogging.StrictLogging
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future

trait ComparingRepository extends StrictLogging {
  /**
    * Compare the result from two different databases and log if the results aren't equal
    *
    * @param methodName The method name that was called, used for logging (e.g. "ChildRepository.findAll")
    * @param slickFut The result from Slick/Postgres
    * @param couchFut The result from CouchDB
    * @tparam T The type, e.g. Seq[Child]
    * @return The result from Slick/Postgres
    */
  def doCompare[T](methodName: String, slickFut: Future[T], couchFut: Future[T], splitOutputOn: Option[String] = None): Future[T] = {
    for {
      slickRes <- slickFut
      couchRes <- couchFut
    } yield {
      if(slickRes == couchRes) {
        logger.debug(s"Method [$methodName]: result from Slick == result from CouchDB")
      } else {
        logger.warn(s"Method [$methodName]: result from Slick != result from CouchDB")
        val slickOut = splitOutputOn map { x => slickRes.toString.split(x).mkString("\n" + x) } getOrElse slickRes
        val couchOut = splitOutputOn map { x => couchRes.toString.split(x).mkString("\n" + x) } getOrElse couchRes
        logger.warn(s" --> result from Slick  : $slickOut")
        logger.warn(s" --> result from CouchDB: $couchOut")
      }
    }

    couchFut
  }

}
