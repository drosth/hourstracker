package com.personal.hourstracker.api.v1.registration

import java.time.LocalDateTime

import scala.concurrent.duration._

import akka.actor.ActorRef
import akka.event.Logging
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.pattern.ask
import akka.util.Timeout
import com.personal.hourstracker.RegistrationActor.GetRegistrations
import com.personal.hourstracker.api.v1.registration.RegistrationApi.{ModelAdapter, RegistrationModel}
import com.personal.hourstracker.config.component.SystemComponent
import com.personal.hourstracker.domain.Registration
import io.swagger.v3.oas.annotations.media.Schema

object RegistrationApi {

  type RegistrationModels = Seq[RegistrationModel]

  @Schema(name = "Registration")
  final case class RegistrationModel(
    job: String,
    clockedIn: Option[LocalDateTime],
    clockedOut: Option[LocalDateTime],
    duration: Option[Double],
    hourlyRate: Option[Double],
    earnings: Option[Double],
    comment: Option[String],
    tags: Option[Set[String]],
    breaks: Option[String],
    adjustments: Option[String],
    totalTimeAdjustment: Option[Double],
    totalEarningsAdjustment: Option[String])

  object ModelAdapter {

    implicit def toModel(registration: Registration): RegistrationModel =
      RegistrationModel(
        registration.job,
        registration.clockedIn,
        registration.clockedOut,
        registration.duration,
        registration.hourlyRate,
        registration.earnings,
        registration.comment,
        registration.tags,
        registration.breaks,
        registration.adjustments,
        registration.totalTimeAdjustment,
        registration.totalEarningsAdjustment)
  }
}

trait RegistrationApi extends RegistrationApiProtocol with RegistrationApiDoc with SystemComponent {

  lazy val log = Logging(system, classOf[RegistrationApi])
  lazy val registrationRoutes: Route = getRegistrations

  // Required by the `ask` (?) method below
  implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  def registrationActor: ActorRef

  override def getRegistrations: Route =
    path("registrations") {
      get {
        onSuccess((registrationActor ? GetRegistrations).mapTo[List[Registration]]) { registrations =>
          val models: List[RegistrationModel] =
            registrations.map(ModelAdapter.toModel)
          complete(models)
        }
      }
    }
}
