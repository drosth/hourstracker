package com.personal.hourstracker.config.component

import scala.concurrent.ExecutionContext

import akka.actor.ActorSystem

trait SystemComponent {
  implicit def system: ActorSystem
  //  implicit def materializer: ActorMaterializer
  implicit def executionContext: ExecutionContext
}
