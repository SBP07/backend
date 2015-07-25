package helpers.reports

import scala.xml.Elem
import java.time.LocalDate

object ReportHelper {
  def generatedTimestamp: Elem = <em style="display: block; margin-top: 30px">Generated on {LocalDate.now.format(helpers.DateTime.fmt)}</em>
}
