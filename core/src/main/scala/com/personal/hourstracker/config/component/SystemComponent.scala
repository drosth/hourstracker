package com.personal.hourstracker.config.component

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout

trait SystemComponent {
  implicit def executionContext: ExecutionContext
  implicit def materializer: ActorMaterializer
  implicit def system: ActorSystem
  implicit lazy val timeout = Timeout(5 seconds)
}
