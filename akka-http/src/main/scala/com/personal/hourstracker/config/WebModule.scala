package com.personal.hourstracker.config
import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait WebModule extends ApplicationModule {

  implicit val system: ActorSystem                = ActorSystem("WebServer")
  implicit val materializer: ActorMaterializer    = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

}
