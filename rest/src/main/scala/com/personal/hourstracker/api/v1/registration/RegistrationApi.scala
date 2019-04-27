package com.personal.hourstracker.api.v1.registration

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import akka.NotUsed
import akka.http.scaladsl.common.{ EntityStreamingSupport, JsonEntityStreamingSupport }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.{ Source, _ }
import com.personal.hourstracker.Application.consolidatedRegistrationService
import com.personal.hourstracker.api.v1.domain.RegistrationModel
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.{ FacturationComponent, LoggingComponent, RegistrationComponent, SystemComponent }
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations
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

  lazy val registrationRoutes: Route = getConsolidatedRegistrationsWithSource ~ getRegistrations ~ importRegistrations

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

  private val splitRegistrationByTags: Flow[Registration, List[Registration], NotUsed] = Flow[Registration]
    .map(facturationService.splitOnTags)
    .alsoTo(Sink.foreach(i => logger.info(s"Number of Registrations after splitting #${i.head.id}: ${i.size}")))

  private val consolidateAndProcessRegistrations: Flow[Registrations, Seq[String], NotUsed] = {
    def renderPdfForConsolidatedRegistrations(registrations: Registrations): List[File] = {
      consolidatedRegistrationService.consolidateAndProcessRegistrations(registrations) {
        _.map {
          case (job, consolidatedRegistrations) =>
            pdfPresenter.renderRegistrationsPerSingleJob(job, consolidatedRegistrations)
        }.toList
      }
    }

    Flow[Registrations].map(registrations => {
      logger.info(s"Number of Registrations to render PDF for: ${registrations.size}")

      val y: Seq[File] = renderPdfForConsolidatedRegistrations(registrations)
      logger.info(s"Rendered #${y.size} files for #${registrations.size} Registrations for id's: ${registrations.map(_.id).toSet}")
      y.map(_.getAbsolutePath)
    })
  }

  def getConsolidatedRegistrationsWithSource: Route = {
    get {
      pathPrefix("registrations") {
        path(Segment / "consolidated") { year =>
          {
            complete(
              registrationService.fetchRegistrations(request = SelectByYear(year.toInt))
                .via(splitRegistrationByTags)
                .fold[List[Registration]](List[Registration]())((aggr, registrations) => aggr ::: registrations)
                .via(consolidateAndProcessRegistrations))
          }
        } ~
          path(Segment / Segment / "consolidated") { (year, month) =>
            {
              complete(
                registrationService.fetchRegistrations(request = SelectByYearAndMonth(year.toInt, month.toInt))
                  .via(splitRegistrationByTags)
                  .fold[List[Registration]](List[Registration]())((aggr, registrations) => aggr ::: registrations)
                  .via(consolidateAndProcessRegistrations))
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
