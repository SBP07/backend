package helpers

import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object DateTime {
  val fmt: DateTimeFormatter = DateTimeFormat.forPattern("dd-MM-yyyy");
}
