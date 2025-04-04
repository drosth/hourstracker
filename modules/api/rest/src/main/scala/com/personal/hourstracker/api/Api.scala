package com.personal.hourstracker.api

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.personal.hourstracker.api.v1.Swagger
import com.personal.hourstracker.api.v1.registration.RegistrationApi
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.{ FacturationComponent, LoggingComponent, RegistrationComponent }
import com.personal.hourstracker.service.presenter.config.module.PresenterModule

trait Api extends RegistrationApi {
  this: RegistrationComponent with FacturationComponent with PresenterModule with LoggingComponent with Configuration =>

  implicit def system: ActorSystem

  lazy val apiV1: Route = pathPrefix(s"v${version.value}") {
    registrationRoutes ~
      swagger.routes
  }

  private lazy val swagger = new Swagger(s"${Api.host}:${Api.port}", s"${Api.basePath}/v${version.value}", version)

  private val version = Version(1)
}
