package helpers

import java.sql.Timestamp
import java.util.Date
import play.api.db.slick.Config.driver.simple._

object Db {
  implicit val dateToTimestampMapper = MappedColumnType.base[Date, Timestamp] (
    d => new Timestamp(d.getTime),
    d => new Date(d.getTime)
  )
}
