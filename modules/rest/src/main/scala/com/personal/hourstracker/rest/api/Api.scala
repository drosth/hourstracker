package com.personal.hourstracker.rest.api

trait Api extends RegistrationApi {
  this: RegistrationComponent with FacturationComponent with PresenterModule with LoggingComponent with Configuration =>

  implicit def system: ActorSystem

  lazy val version = Version(1)

  lazy val apiV1: Route = pathPrefix(s"v$version") {
    registrationRoutes ~
      swagger.routes ~
      get {
        pathEnd {
          complete("hello from apiV1")
        }
      }
  }

  private lazy val swagger = new Swagger(s"${Server.host}:${Server.port}", s"${Server.Api.basePath}/v$version", version)
}
