package helpers

import java.time.format.DateTimeFormatter

object DateTime {
  val fmt: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val exportFormat: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
}
