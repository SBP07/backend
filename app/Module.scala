import be.thomastoye.speelsysteem.data.{ChildRepository, CrewRepository}
import be.thomastoye.speelsysteem.legacy.data._
import be.thomastoye.speelsysteem.legacy.data.comparing.ComparingCrewRepository
import be.thomastoye.speelsysteem.legacy.data.slick._
import com.google.inject.AbstractModule

class Module extends AbstractModule {
  override def configure() = {
    bind(classOf[ChildRepository]).to(classOf[SlickChildRepository])
    bind(classOf[CrewRepository]).to(classOf[ComparingCrewRepository])
    bind(classOf[ChildPresenceRepository]).to(classOf[SlickChildPresenceRepository])
    bind(classOf[ShiftRepository]).to(classOf[SlickShiftRepository])
    bind(classOf[ShiftTypeRepository]).to(classOf[SlickShiftTypeRepository])
  }
}
