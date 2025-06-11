package com.personal.hourstracker.config.module

import akka.actor.ActorSystem
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.{LoggingComponent, SystemComponent}
import com.personal.hourstracker.service.presenter.config.module.PresenterModule
import com.personal.hourstracker.storage.config.module.StorageModule
import org.slf4j.{Logger, LoggerFactory}

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

  override implicit val system: ActorSystem                = ActorSystem("hourstracker-rest")
  override implicit val executionContext: ExecutionContext = system.dispatcher

  runMigrations()
}
