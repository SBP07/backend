package helpers

import java.sql.{Date => Sqldate, Timestamp}
import java.time.LocalDate
import java.util.Date

import slick.driver.H2Driver.api._

object Db {
  implicit val dateToTimestampMapper = MappedColumnType.base[Date, Timestamp](
    d => new Timestamp(d.getTime),
    d => new Date(d.getTime)
  )

  implicit val localdateToSqldateMapper = MappedColumnType.base[LocalDate, Sqldate](
    d => Sqldate.valueOf(d),
    d => d.toLocalDate
  )

}
