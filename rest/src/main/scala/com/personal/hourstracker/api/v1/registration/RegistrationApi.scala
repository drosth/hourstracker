package com.personal.hourstracker.api.v1.registration

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import akka.NotUsed
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.Source
import com.personal.hourstracker.api.v1.domain.RegistrationModel
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.{FacturationComponent, LoggingComponent, RegistrationComponent, SystemComponent}
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.service.RegistrationService.{SelectByYear, SelectByYearAndMonth}
import com.personal.hourstracker.service.presenter.ConsolidatedRegistrationsPdfPresenter

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

  lazy val registrationRoutes: Route = getConsolidatedRegistrations ~ importRegistrationsFromSource ~ getRegistrations

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

  override def getConsolidatedRegistrations: Route = {
    get {
      pathPrefix("registrations") {
        path(Segment / "consolidated") { year =>
          {
            val source = registrationService.fetchRegistrations(request = SelectByYear(year.toInt))
            complete(consolidateRegistrationsFor(source))
          }
        } ~
          path(Segment / Segment / "consolidated") { (year, month) =>
            {
              val source: Source[Registration, NotUsed] =
                registrationService.fetchRegistrations(request = SelectByYearAndMonth(year.toInt, month.toInt))
              complete(consolidateRegistrationsFor(source))
            }
          }
      }
    }
  }

  private def consolidateRegistrationsFor(source: Source[Registration, NotUsed]): Source[List[String], NotUsed] =
    registrationService.consolidateRegistrations(source) {
      _.map {
        case (job, consolidatedRegistrations) =>
          pdfPresenter.renderRegistrationsPerSingleJob(job, consolidatedRegistrations)
      }.map(_.getAbsolutePath).toList
    }

  override def importRegistrationsFromSource: Route = path("registrations" / "import") {
    get {
      val source: Source[Either[String, Registration], NotUsed] = registrationService.importRegistrationsFromSource(Application.importFrom)

      complete(
        source
          .map(_.toOption)
          .filter(_.isDefined)
          .map(_.get.convert()))
    }
  }
}
