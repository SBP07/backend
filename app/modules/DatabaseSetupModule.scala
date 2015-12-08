package modules

import com.google.inject.AbstractModule
import services.auth.{DatabaseSetupImpl, DatabaseSetup}
import net.codingwell.scalaguice.ScalaModule

class DatabaseSetupModule extends AbstractModule with ScalaModule {
  override def configure(): Unit = {
    bind[DatabaseSetup].to[DatabaseSetupImpl]
  }
}
