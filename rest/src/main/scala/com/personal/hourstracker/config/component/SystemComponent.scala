package com.personal.hourstracker.config.component
import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout
import scala.concurrent.duration._

trait SystemComponent {
  implicit def system: ActorSystem
  implicit def materializer: ActorMaterializer
  implicit def executionContext: ExecutionContext

  implicit lazy val timeout = Timeout(5 seconds)
}
