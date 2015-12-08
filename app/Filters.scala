import javax.inject.Inject

import play.api.http.HttpFilters
import play.filters.cors.CORSFilter
import play.api.mvc.Filter

class Filters @Inject() (corsFilter: CORSFilter) extends HttpFilters {
  def filters: Seq[Filter] = Seq(corsFilter)
}
