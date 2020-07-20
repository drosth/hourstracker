package com.personal.hourstracker.rest.config.module

import scala.concurrent.ExecutionContext

trait WebModule
  extends Configuration
    // modules
    with RegistrationModule
    with ImporterModule
    with StorageModule
    with PresenterModule
    // components
    with SystemComponent
    with LoggingComponent {

  override implicit lazy val logger: Logger = LoggerFactory.getLogger("WebModule")

  override implicit val system: ActorSystem = ActorSystem("hourstracker-rest")
  override implicit val executionContext: ExecutionContext = system.dispatcher
}
