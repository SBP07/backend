package helpers

import java.sql.{Date => Sqldate, Timestamp}
import java.util.Date
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import play.api.db.slick.Config.driver.simple._

object Db {
  implicit val dateToTimestampMapper = MappedColumnType.base[Date, Timestamp](
    d => new Timestamp(d.getTime),
    d => new Date(d.getTime)
  )

  val jodaTzUTC: DateTimeZone = DateTimeZone.forID("UTC");

  implicit val jodaDatetimeToSqldateMapper = MappedColumnType.base[LocalDate, Sqldate](
    d => new Sqldate(d.toDateTimeAtStartOfDay(jodaTzUTC).getMillis()),
    d => new LocalDate(d.getTime(), jodaTzUTC)
  )

}
