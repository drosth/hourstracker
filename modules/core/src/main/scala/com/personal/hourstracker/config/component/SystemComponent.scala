package com.personal.hourstracker.config.component

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait SystemComponent {
  implicit def executionContext: ExecutionContext

  implicit def system: ActorSystem

  implicit lazy val timeout = Timeout(5 seconds)
}
