package com.personal.hourstracker.config.module

import scala.concurrent.ExecutionContext

trait ApplicationModule extends Configuration with RegistrationModule with ImporterModule with SystemComponent with LoggingComponent {

  override implicit lazy val logger: Logger = LoggerFactory.getLogger("ApplicationModule")

  override implicit val system: ActorSystem = ActorSystem("registration-core")
  override implicit val executionContext: ExecutionContext = system.dispatcher
}

