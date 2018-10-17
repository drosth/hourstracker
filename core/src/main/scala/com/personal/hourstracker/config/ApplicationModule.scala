package com.personal.hourstracker.config

import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem
import com.personal.hourstracker.config.component.{ ConsolidatedRegistrationComponent, FacturationComponent, RegistrationComponent, SystemComponent }
import com.personal.hourstracker.service.presenter.{ HtmlPresenterComponent, JsonPresenterComponent, PdfPresenterComponent }

trait ApplicationModule
  extends Configuration
  with RegistrationComponent
  with ConsolidatedRegistrationComponent
  with FacturationComponent
  with HtmlPresenterComponent
  with PdfPresenterComponent
  with JsonPresenterComponent
  with SystemComponent {

  override implicit val system: ActorSystem = ActorSystem("event-replay")
  //  override implicit val materializer: ActorMaterializer = ActorMaterializer()
  override implicit val executor: ExecutionContext = system.dispatcher
}
