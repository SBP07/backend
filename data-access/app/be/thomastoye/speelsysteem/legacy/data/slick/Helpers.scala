package be.thomastoye.speelsysteem.legacy.data.slick

import java.sql.{Date => Sqldate}
import org.joda.time.LocalDate
import java.time.{LocalDate => JavaLocalDate}
import slick.driver.PostgresDriver.api._

object Helpers {
  implicit val jodaDatetimeToSqldateMapper = MappedColumnType.base[LocalDate, Sqldate](
    d => new Sqldate(d.toDate.getTime),
    d => new LocalDate(d.getTime)
  )

  implicit val javaLocalDateToSqldateMapper = MappedColumnType.base[JavaLocalDate, Sqldate](
    d => Sqldate.valueOf(d),
    d => d.toLocalDate
  )

}
