package com.personal.hourstracker

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.scaladsl.CorsDirectives._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import com.personal.hourstracker.api.{Api => ApiV1}
import com.personal.hourstracker.config.WebModule
object WebServer extends App with ApiV1 with WebModule {

  val registrationActor: ActorRef =
    system.actorOf(Props(new RegistrationActor(registrationService)), "registrationActor")
  private val corsSettings = CorsSettings.defaultSettings.withAllowedMethods(
    List(HttpMethods.GET, HttpMethods.PUT, HttpMethods.DELETE, HttpMethods.POST, HttpMethods.HEAD, HttpMethods.OPTIONS))
  private val apiRoutes = cors(corsSettings) {
    pathPrefix(Api.basePath) {
      apiV1
    }
  }

  Http().bindAndHandle(apiRoutes, Server.host, Server.port).map { binding =>
    println(s"Server online at http://${Server.host}:${Api.port}/")
  }

  Await.result(system.whenTerminated, Duration.Inf)
}
