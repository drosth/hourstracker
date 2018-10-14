package com.personal.hourstracker.api.v1.consolidatedregistration

import java.time.LocalDate
import java.time.format.DateTimeFormatter

import scala.concurrent.duration._

import akka.actor.ActorRef
import akka.http.scaladsl.model.{MediaRange, StatusCodes}
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.util.Timeout
import com.personal.hourstracker.config.component.SystemComponent
import com.personal.hourstracker.domain.ConsolidatedRegistration
import io.swagger.v3.oas.annotations.media.Schema

object ConsolidatedRegistrationApi {

  type ConsolidatedRegistrationModels = Seq[ConsolidatedRegistrationModel]

  @Schema
  final case class ConsolidatedRegistrationModel(date: LocalDate, job: String, duration: Option[Double], comment: Option[String])

  private val localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  implicit val stringToLocalDateUnmarshaller: Unmarshaller[String, LocalDate] =
    Unmarshaller.strict[String, LocalDate] { LocalDate.parse(_, localDateFormatter) }

  object ModelAdapter {

    implicit def toModel(consolidatedRegistration: ConsolidatedRegistration): ConsolidatedRegistrationModel =
      ConsolidatedRegistrationModel(
        consolidatedRegistration.date,
        consolidatedRegistration.job,
        consolidatedRegistration.duration,
        consolidatedRegistration.comment)
  }
}

trait ConsolidatedRegistrationApi extends ConsolidatedRegistrationApiDoc with ConsolidatedRegistrationApiProtocol with SystemComponent {

  lazy val consolidatedRegistrationRoutes: Route = getConsolidatedRegistrations

  // Required by the `ask` (?) method below
  private implicit lazy val timeout = Timeout(5.seconds) // usually we'd obtain the timeout from the system's configuration

  def consolidatedRegistrationActor: ActorRef

  //  val AcceptJson = Accept(MediaRange(MediaTypes.`application/json`))

  val myEncodings = Seq(MediaRange(`application/pdf`), MediaRange(`application/json`))

  override def getConsolidatedRegistrations: Route =
    path("consolidated") {
      (get & extract(_.request.headers)) { requestHeaders =>
        println(s"requestHeaders: $requestHeaders")

        parameters("startAt".as[LocalDate].?, "endAt".as[LocalDate].?) {
          case (Some(startAt), endAt) =>
            println(s"(startAt, endAt) => ($startAt, $endAt)")
            complete(StatusCodes.OK)

          case _ =>
            complete(StatusCodes.OK)
        }
      }
      //          println(s"requestHeaders: $requestHeaders")
      //          println(s"query: $query")
      //
      //          val mediaTypeNegotiator = new MediaTypeNegotiator(requestHeaders)
      //
      //          val encoding: MediaRange = mediaTypeNegotiator.acceptedMediaRanges
      //            .intersect(myEncodings)
      //            .headOption
      //            .getOrElse(MediaRange(`application/json`))
      //
      //          println(s"encoding: $encoding")
      //
      //          complete(StatusCodes.OK)
    }
}
