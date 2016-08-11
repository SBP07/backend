import be.thomastoye.speelsysteem.data.couchdb.{CouchChildRepository, CouchCrewRepository}
import be.thomastoye.speelsysteem.data.{ChildRepository, CrewRepository}
import com.google.inject.AbstractModule

class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[ChildRepository]).to(classOf[CouchChildRepository])
    bind(classOf[CrewRepository]).to(classOf[CouchCrewRepository])
  }
}
