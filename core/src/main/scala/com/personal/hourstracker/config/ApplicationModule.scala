package com.personal.hourstracker.config

import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem
import com.personal.hourstracker.config.component._
import com.personal.hourstracker.service.presenter.config.PresenterComponents

trait ApplicationModule
  extends Configuration
  with RegistrationComponent
  with NoopRegistrationRepositoryComponent
  with CSVImporterServiceComponent
  with ConsolidatedRegistrationComponent
  with FacturationComponent
  with PresenterComponents
  with SystemComponent
  with LoggingComponent {

  override implicit val system: ActorSystem = ActorSystem("event-replay")
  override implicit val executionContext: ExecutionContext = system.dispatcher
}
