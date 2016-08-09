import be.thomastoye.speelsysteem.data.{ChildRepository, CrewRepository}
import be.thomastoye.speelsysteem.legacy.data._
import be.thomastoye.speelsysteem.legacy.data.comparing.{ComparingChildRepository, ComparingCrewRepository}
import be.thomastoye.speelsysteem.legacy.data.slick._
import com.google.inject.AbstractModule

class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[ChildRepository]).to(classOf[ComparingChildRepository])
    bind(classOf[CrewRepository]).to(classOf[ComparingCrewRepository])
  }
}
