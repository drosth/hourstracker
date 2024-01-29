package com.personal.hourstracker

import akka.http.javadsl.model.headers.HttpOrigin
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, StatusCodes}
import akka.http.javadsl.model.headers.Host
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import ch.megard.akka.http.cors.javadsl
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.personal.hourstracker.api.{Api => ApiV1}
import com.personal.hourstracker.config.module.WebModule

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Failure

object HourstrackerAPI extends App with ApiV1 with WebModule {
  import ch.megard.akka.http.cors.scaladsl.CorsDirectives._

  // Your rejection handler
  val rejectionHandler = corsRejectionHandler.withFallback(RejectionHandler.default)

  // Your exception handler
  val exceptionHandler = ExceptionHandler {
    case e: NoSuchElementException =>
      complete(StatusCodes.NotFound -> e.getMessage)
  }

  // Combining the two handlers only for convenience
  val handleErrors = handleRejections(rejectionHandler) & handleExceptions(exceptionHandler)

  def corsAllowedOrigins: javadsl.model.HttpOriginMatcher =
    javadsl.model.HttpOriginMatcher.create(
      HttpOrigin.create("http", Host.create(Server.host, Server.port))
    )

  val corsSettings = CorsSettings.defaultSettings
    .withAllowGenericHttpRequests(true)
    .withAllowedOrigins(corsAllowedOrigins)
    .withAllowedMethods(List(HttpMethods.GET, HttpMethods.PUT, HttpMethods.DELETE, HttpMethods.POST, HttpMethods.HEAD, HttpMethods.OPTIONS))

//  private val corsSettings = CorsSettings.defaultSettings
//    .withAllowedOrigins(HttpOriginRange.create(AccessControlAllowOrigin))
//    .withAllowedMethods(List(HttpMethods.GET, HttpMethods.PUT, HttpMethods.DELETE, HttpMethods.POST, HttpMethods.HEAD, HttpMethods.OPTIONS))

  private val apiRoutes =
    handleErrors {
      cors(corsSettings) {
        pathPrefix(Api.basePath) {
          apiV1
        }
      }
    }

  Http()
    .newServerAt(Server.host, Server.port)
    .bind(apiRoutes)
    .onComplete {
      case Failure(e) =>
        logger.error(s"Could not start WebServer: ${e.getMessage}", e)
        sys.exit(1)
      case _ =>
        logger.info(s"Server online at http://${Server.host}:${Server.port}/")
        ()
    }

  //  Http().bindAndHandle(apiRoutes, Server.host, Server.port).map { _ =>
//    println(s"Server online at http://${Server.host}:${Server.port}/")
//  }
//
  Await.result(system.whenTerminated, Duration.Inf)
}
