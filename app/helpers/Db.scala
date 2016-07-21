package helpers

import java.sql.{Date => Sqldate, Timestamp}
import java.util.Date
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import slick.driver.PostgresDriver.api._

object Db {
  implicit val dateToTimestampMapper = MappedColumnType.base[Date, Timestamp](
    d => new Timestamp(d.getTime),
    d => new Date(d.getTime)
  )

  implicit val jodaDatetimeToSqldateMapper = MappedColumnType.base[LocalDate, Sqldate](
    d => new Sqldate(d.toDate().getTime()),
    d => new LocalDate(d.getTime())
  )

}
