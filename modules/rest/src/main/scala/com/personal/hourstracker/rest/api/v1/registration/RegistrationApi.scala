package com.personal.hourstracker.rest.api.v1.registration

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import scala.util.{Failure, Success}

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
        registration.id,
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

  lazy val registrationRoutes: Route = getConsolidatedRegistrations ~ importRegistrationsFromSource ~ getRegistrations ~ uploadRegistrations

  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport
      .json()
      .withParallelMarshalling(parallelism = 8, unordered = false)

  override def getRegistrations: Route = {
    logRequestResult("email-service") {
      get {
        pathPrefix("registrations") {
          pathEnd {
            println("1")
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
  }

  override def getConsolidatedRegistrations: Route = {
    get {
      pathPrefix("registrations") {
        path(Segment / "consolidated") { year => {
          val source = registrationService.fetchRegistrations(request = SelectByYear(year.toInt))
          complete(consolidateRegistrationsFor(source)(jsonPresenter))
        }
        } ~
          path(Segment / Segment / "consolidated") { (year, month) =>
            val source = registrationService.fetchRegistrations(request = SelectByYearAndMonth(year.toInt, month.toInt))

            parameters(Symbol("type").?) {
              case Some("pdf") =>
                complete(consolidateRegistrationsFor(source)(pdfPresenter))

              case Some("html") =>
                onComplete(consolidateRegistrationsFor(source)(htmlPresenter).runWith(Sink.last)) {
                  case Success(consolidatedFiles) if consolidatedFiles.nonEmpty =>
                    getFromFile(consolidatedFiles.head)

                  case Success(consolidatedFiles) if consolidatedFiles.isEmpty =>
                    complete("nothing here...")

                  case Failure(ex) => complete((StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}"))
                }

              case Some("json") =>
                complete(consolidateRegistrationsFor(source)(jsonPresenter))

              case _ =>
                complete(consolidateRegistrationsFor(source)(jsonPresenter))
            }
          }
      }
    }
  }

  private def consolidateRegistrationsFor(source: Source[Registration, NotUsed])(presenter: Presenter): Source[List[String], NotUsed] =
    registrationService.consolidateRegistrations(source) {
      _.map {
        case (job, consolidatedRegistrations) =>
          logger.info(s"Rendering ${consolidatedRegistrations.size} registrations for job - '$job'")
          presenter.renderRegistrationsPerSingleJob(job, consolidatedRegistrations)
      }.map(_.getAbsolutePath).toList
    }

  override def importRegistrationsFromSource: Route = path("registrations" / "import") {
    get {
      complete(
        registrationService
          .importRegistrationsFromSource(s"${Application.importFrom}")
          .map(_.toOption)
          .filter(_.isDefined)
          .map(_.get.map(_.convert()))
      )
    }
  }

  private def createTempFile(fileInfo: FileInfo): File = File.createTempFile(fileInfo.fileName, ".tmp")

  override def uploadRegistrations: Route = path("registrations" / "upload") {
    storeUploadedFile("csv", createTempFile) {
      case (_, file: File) =>
        logger.debug(s"Reading registrations from: '${file.getAbsolutePath}'")
        complete(
          registrationService
            .importRegistrationsFromSource(file.getAbsolutePath)
            .map(_.toOption)
            .filter(_.isDefined)
            .map(_.get.map(_.convert()))
        )
    }
  }
}
