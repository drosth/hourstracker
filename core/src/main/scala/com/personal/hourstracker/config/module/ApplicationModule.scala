package com.personal.hourstracker.config.module

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component._
import org.slf4j.{ Logger, LoggerFactory }

import scala.concurrent.ExecutionContext

trait ApplicationModule extends Configuration with RegistrationModule with ImporterModule with SystemComponent with LoggingComponent {

  override implicit lazy val logger: Logger = LoggerFactory.getLogger("ApplicationModule")

  override implicit val system: ActorSystem = ActorSystem("hourstracker-core")
  override implicit val executionContext: ExecutionContext = system.dispatcher
  override implicit val materializer: ActorMaterializer = ActorMaterializer()
}

