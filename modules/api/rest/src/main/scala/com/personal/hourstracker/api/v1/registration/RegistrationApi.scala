package com.personal.hourstracker.api.v1.registration

import akka.NotUsed
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.FileInfo
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.stream.scaladsl.{Sink, Source}
import com.personal.hourstracker.api.v1.domain.RegistrationModel
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.{FacturationComponent, LoggingComponent, RegistrationComponent, SystemComponent}
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.service.RegistrationService.{SelectByYear, SelectByYearAndMonth}
import com.personal.hourstracker.service.presenter.Presenter
import com.personal.hourstracker.service.presenter.config.module.PresenterModule

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import scala.util.{Failure, Success}

object RegistrationApi {
  implicit lazy val locale: Locale = Locale.of("nl", "NL")

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
        registration.totalEarningsAdjustment
      )
  }
}

trait RegistrationApi extends RegistrationApiProtocol with RegistrationApiDoc with SystemComponent {
  this: RegistrationComponent with FacturationComponent with PresenterModule with LoggingComponent with Configuration =>

  import RegistrationApi._

  lazy val registrationRoutes: Route = getConsolidatedRegistrations ~ importRegistrationsFromSource ~ getRegistrations ~ uploadRegistrations

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
              .map(_.convert())
          )
        } ~
        path(Segment / Segment) { (year, month) =>
          complete(
            registrationService
              .fetchRegistrations(request = SelectByYearAndMonth(year.toInt, month.toInt))
              .map(_.convert())
          )
        }
      }
    }
  }

  override def getConsolidatedRegistrations: Route = {
    get {
      pathPrefix("registrations") {
        path(Segment / "consolidated") { year =>
          val source = registrationService.fetchRegistrations(request = SelectByYear(year.toInt))

          parameters('type.?) {
            case Some("html") =>
              onComplete(consolidateRegistrationsFor(source)(htmlPresenter).runWith(Sink.last)) {
                case Success(consolidatedFiles) if consolidatedFiles.nonEmpty =>
                  getFromFile(consolidatedFiles.head)

                case Success(consolidatedFiles) if consolidatedFiles.isEmpty =>
                  complete("nothing here...")

                case Failure(ex) => complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
              }

            case Some("json") => complete(consolidateRegistrationsFor(source)(jsonPresenter))

            case _ => complete(consolidateRegistrationsFor(source)(pdfPresenter))
          }
        } ~
        path(Segment / Segment / "consolidated") { (year, month) =>
          val source = registrationService.fetchRegistrations(request = SelectByYearAndMonth(year.toInt, month.toInt))

          parameters('type.?) {
            case Some("html") =>
              onComplete(consolidateRegistrationsFor(source)(htmlPresenter).runWith(Sink.last)) {
                case Success(consolidatedFiles) if consolidatedFiles.nonEmpty =>
                  getFromFile(consolidatedFiles.head)

                case Success(consolidatedFiles) if consolidatedFiles.isEmpty =>
                  complete("nothing here...")

                case Failure(ex) => complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
              }

            case Some("json") => complete(consolidateRegistrationsFor(source)(jsonPresenter))

            case _ => complete(consolidateRegistrationsFor(source)(pdfPresenter))
          }
        }
      }
    }
  }

  private def consolidateRegistrationsFor(source: Source[Registration, NotUsed])(presenter: Presenter): Source[List[String], NotUsed] =
    registrationService.consolidateRegistrations(source) {
      _.map {
        case (job, consolidatedRegistrations) =>
          logger.info(s"Rendering ${consolidatedRegistrations.size} registrations for job - '${job}'")
          presenter.renderRegistrationsPerSingleJob(job, consolidatedRegistrations)
      }.map(_.getAbsolutePath).toList
    }

  override def importRegistrationsFromSource: Route = path("registrations" / "import") {
    get {
      val source: Source[Either[String, Registration], NotUsed] =
        registrationService.importRegistrationsFromSource(s"${Application.importFrom}/CSVExport.csv")

      complete(
        source
          .map(_.toOption)
          .filter(_.isDefined)
          .map(_.get.convert())
      )
    }
  }

  private def createTempFile(fileInfo: FileInfo): File = File.createTempFile(fileInfo.fileName, ".tmp")

  override def uploadRegistrations: Route = path("registrations" / "upload") {
    storeUploadedFile("csv", createTempFile) {
      case (_, file: File) =>
        logger.debug(s"Reading registrations from: '${file.getAbsolutePath}'")
        val source: Source[Either[String, Registration], NotUsed] = registrationService.importRegistrationsFromSource(file.getAbsolutePath)

        complete(
          source
            .map(_.toOption)
            .filter(_.isDefined)
            .map(_.get.convert())
        )
    }
  }
}
