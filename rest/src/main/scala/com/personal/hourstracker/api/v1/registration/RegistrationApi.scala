package com.personal.hourstracker.api.v1.registration

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import akka.http.scaladsl.common.{ EntityStreamingSupport, JsonEntityStreamingSupport }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.unmarshalling.Unmarshaller
import com.personal.hourstracker.api.v1.domain.RegistrationModel
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.{ FacturationComponent, LoggingComponent, RegistrationComponent, SystemComponent }
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.service.RegistrationService.{ SelectByYear, SelectByYearAndMonth }
import com.personal.hourstracker.service.presenter.ConsolidatedRegistrationsPdfPresenter

import scala.util.{ Failure, Success }

object RegistrationApi {
  implicit lazy val locale: Locale = new Locale("nl", "NL")

  implicit val stringToLocalDateUnmarshaller: Unmarshaller[String, LocalDate] =
    Unmarshaller.strict[String, LocalDate] {
      LocalDate.parse(_, DateTimeFormatter.ISO_LOCAL_DATE)
    }

  import ToModelAdapter._

  implicit class RegistrationOps(registration: Registration) {
    def convert(): RegistrationModel = toModel(registration)
  }

  case class Range(startAt: Option[String], endAt: Option[String])

  object ToModelAdapter {

    def toModel(registration: Registration): RegistrationModel =
      RegistrationModel(
        registration.job,
        registration.clockedIn,
        registration.clockedOut,
        registration.duration,
        registration.hourlyRate,
        registration.earnings,
        registration.comment,
        registration.tags,
        registration.totalTimeAdjustment,
        registration.totalEarningsAdjustment)
  }
}

trait RegistrationApi extends RegistrationApiProtocol with RegistrationApiDoc with SystemComponent {
  this: RegistrationComponent with FacturationComponent with ConsolidatedRegistrationsPdfPresenter with LoggingComponent with Configuration =>

  import RegistrationApi._

  lazy val registrationRoutes: Route = getConsolidatedRegistrations ~ getRegistrations ~ importRegistrations

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport
      .json()
      .withParallelMarshalling(parallelism = 8, unordered = false)

  override def getRegistrations: Route = {
    get {
      pathPrefix("registrations") {
        pathEnd {
          complete(registrationService.fetchRegistrations().map(_.convert()))
        } ~
          path(Segment) { year =>
            complete(
              registrationService
                .fetchRegistrations(request = SelectByYear(year.toInt))
                .map(_.convert()))
          } ~
          path(Segment / Segment) { (year, month) =>
            complete(
              registrationService
                .fetchRegistrations(request = SelectByYearAndMonth(year.toInt, month.toInt))
                .map(_.convert()))
          }
      }
    }
  }

  def getConsolidatedRegistrations: Route = {
    get {
      pathPrefix("registrations") {
        path(Segment / "consolidated") { year =>
          {
            val source = registrationService.fetchRegistrations(request = SelectByYear(year.toInt))
            complete(registrationService.consolidateRegistrations(source) {
              _.map {
                case (job, consolidatedRegistrations) =>
                  pdfPresenter.renderRegistrationsPerSingleJob(job, consolidatedRegistrations)
              }.map(_.getAbsolutePath).toList
            })
          }
        } ~
          path(Segment / Segment / "consolidated") { (year, month) =>
            {
              val source = registrationService.fetchRegistrations(request = SelectByYearAndMonth(year.toInt, month.toInt))
              complete(registrationService.consolidateRegistrations(source) {
                _.map {
                  case (job, consolidatedRegistrations) =>
                    pdfPresenter.renderRegistrationsPerSingleJob(job, consolidatedRegistrations)
                }.map(_.getAbsolutePath).toList
              })
            }
          }
      }
    }
  }

  override def importRegistrations: Route = path("registrations" / "import") {
    post {
      onComplete(registrationService.importRegistrationsFrom(Application.importFrom)) {
        case Success(importedRegistrations) =>
          importedRegistrations match {
            case Right(_) => complete(StatusCodes.Accepted)

            case Left(message) =>
              logger.error(message)
              complete(StatusCodes.NotFound, message)
          }

        case Failure(e) =>
          logger.error(e.getMessage)
          complete(StatusCodes.NotFound, e.getMessage)
      }
    }
  }
}
