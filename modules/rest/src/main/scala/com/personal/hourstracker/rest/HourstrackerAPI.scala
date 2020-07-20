package com.personal.hourstracker.rest

import scala.util.{Failure, Success}

object HourstrackerAPI extends App with ApiV1 with WebModule {

  private val corsSettings = CorsSettings.defaultSettings
    .withAllowedOrigins(HttpOriginMatcher.*)
    .withAllowedMethods(List(HttpMethods.GET, HttpMethods.PUT, HttpMethods.DELETE, HttpMethods.POST, HttpMethods.HEAD, HttpMethods.OPTIONS))

  private val apiRoutes: Route = cors(corsSettings) {
    pathPrefix(Server.Api.basePath) {
      apiV1
    }
  }

  Http()
    .bindAndHandle(apiRoutes, Server.host, Server.port)
    .map(_.localAddress)
    .onComplete {
      case Success(address) =>
        system.log.info("REST Server online at http://{}:{}/{}/v{}", address.getHostString, address.getPort, Server.Api.basePath, version)
      case Failure(ex) =>
        system.log.error("Failed to bind HTTP endpoint, terminating system", ex)
        system.terminate()
    }
}
