package com.personal.hourstracker.config.component

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

import akka.actor.ActorSystem
import akka.util.Timeout

trait SystemComponent {
  implicit def system: ActorSystem
  implicit def executionContext: ExecutionContext
  implicit lazy val timeout = Timeout(5 seconds)
}
