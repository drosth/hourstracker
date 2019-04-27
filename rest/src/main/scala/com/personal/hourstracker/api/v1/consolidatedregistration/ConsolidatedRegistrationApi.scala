package com.personal.hourstracker.api.v1.consolidatedregistration

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.{ Locale, UUID }

import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{ ContentDispositionTypes, _ }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.server.{ MediaTypeNegotiator, Route }
import akka.http.scaladsl.unmarshalling.Unmarshaller
import com.personal.hourstracker.Application.consolidatedRegistrationService
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.{ FacturationComponent, RegistrationComponent, SystemComponent }
import com.personal.hourstracker.dateRangeAsStringOf
import com.personal.hourstracker.domain.ConsolidatedRegistration
import com.personal.hourstracker.domain.ConsolidatedRegistration.{ ConsolidatedRegistrations, ConsolidatedRegistrationsPerJob }
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.CompressorService
import com.personal.hourstracker.service.RegistrationSelector.{ RegistrationRangeSelector, _ }
import com.personal.hourstracker.service.presenter.config.PresenterComponents
import io.swagger.v3.oas.annotations.media.Schema

import scala.concurrent.Future
import scala.util.{ Failure, Success }

object ConsolidatedRegistrationApi {
  type ConsolidatedRegistrationModels = Seq[ConsolidatedRegistrationModel]

  implicit lazy val locale: Locale = new Locale("nl", "NL")

  implicit val stringToLocalDateUnmarshaller: Unmarshaller[String, LocalDate] =
    Unmarshaller.strict[String, LocalDate] {
      LocalDate.parse(_, localDateFormatter)
    }

  private val localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", locale)

  @Schema
  final case class ConsolidatedRegistrationModel(date: LocalDate, job: String, duration: Option[Double], comment: Option[String])

  object ModelAdapter {

    implicit def toModel(consolidatedRegistration: ConsolidatedRegistration): ConsolidatedRegistrationModel =
      ConsolidatedRegistrationModel(
        consolidatedRegistration.date,
        consolidatedRegistration.job,
        consolidatedRegistration.duration,
        consolidatedRegistration.comment)
  }

}

trait ConsolidatedRegistrationApi extends ConsolidatedRegistrationApiDoc with ConsolidatedRegistrationApiProtocol {
  this: RegistrationComponent with FacturationComponent with PresenterComponents with SystemComponent with Configuration =>

  lazy val consolidatedRegistrationRoutes: Route = getConsolidatedRegistrations

  lazy val compressorService: CompressorService = new CompressorService()

  val myEncodings = Seq(MediaRange(`application/pdf`), MediaRange(`application/json`))

  val acceptedEncodings = Seq(MediaRange(`application/json`))

  override def getConsolidatedRegistrations: Route =
    path("consolidated") {
      (get & extract(_.request.headers)) { requestHeaders =>
        val mediaTypeNegotiator = new MediaTypeNegotiator(requestHeaders)

        def encoding: Option[MediaRange] =
          mediaTypeNegotiator.acceptedMediaRanges
            .intersect(acceptedEncodings)
            .headOption

        processConsolidatedRegistrations()
      }
    }

  def processConsolidatedRegistrations(): Route = {
    import ConsolidatedRegistrationApi._

    parameters("startAt".as[String].?, "endAt".as[String].?) { (startAt: Option[String], endAt: Option[String]) =>
      onComplete(registrationService.importRegistrationsFrom(Application.importFrom)) {
        case Success(importedRegistrations: Either[String, Registrations]) =>
          importedRegistrations match {
            case Left(message) =>
              logger.warn(s"Did not receive registrations: $message")
              complete(StatusCodes.NotFound)

            case Right(registrations: Registrations) =>
              logger.debug(s"of #${registrations.size} registrations")
              val filtered: Registrations = registrations.filter(new RegistrationRangeSelector(startAt, endAt).filter)
              logger.debug(s"going to process #${filtered.size} filtered registrations")

              val splitted: Registrations = facturationService.splitAllRegistrationsForFacturation(filtered)
              logger.debug(s"after splitting, #${splitted.size} remains")

              val consolidatedFiles: Seq[File] = consolidatedRegistrationService.consolidateAndProcessRegistrations(splitted) {
                consolidatedRegistrationsPerJob =>
                  logger.debug(s"Processing #${consolidatedRegistrationsPerJob.size} items:")
                  processConsolidatedRegistrationsPerJob(consolidatedRegistrationsPerJob)
              }
              logger.debug(s"after consolidating, #${consolidatedFiles.size} file(s) have been created")

              consolidatedFiles.size match {
                case 1 =>
                  val file = consolidatedFiles.head
                  logger.info(s"Returning consolidated registrations file: '${file.getName}'")
                  getFromFile(file, ContentType(MediaTypes.`application/pdf`))

                case numberOfFiles if numberOfFiles > 1 =>
                  val zippedFiles = new File(s"${Application.exportTo}/consolidated-${UUID.randomUUID()}.zip")

                  logger.info(s"Returning #$numberOfFiles consolidated registrations files in .zip file: '${zippedFiles.getName}'")

                  val zipRoute: Future[Route] = compressorService.zip(consolidatedFiles, zippedFiles).map {
                    case Success(s) => getFromFile(zippedFiles)
                    case Failure(e) => complete(StatusCodes.NotFound)
                  }

                  onComplete(zipRoute) {
                    case Success(route) =>
                      respondWithHeader(
                        `Content-Disposition`(
                          ContentDispositionTypes.attachment,
                          Map("filename" -> constructZipFileNameWith(startAt, endAt)))) { route }

                    case Failure(e) =>
                      logger.error(e.getMessage)
                      complete(StatusCodes.NotFound, e.getMessage)
                  }

                case _ => complete(StatusCodes.NotFound, "No consolidated registration files created!")
              }
              complete(StatusCodes.OK)
          }

        case Failure(e) =>
          logger.error(e.getMessage)
          complete(StatusCodes.NotFound, e.getMessage)
      }
    }
  }

  private def constructZipFileNameWith(startAt: Option[String], endAt: Option[String]) = {
    val range = Seq(startAt.getOrElse(""), endAt.getOrElse(""))
    s"timesheets-${range.mkString("-")}.zip"
  }

  private def processConsolidatedRegistrationsPerJob(consolidatedRegistrationsPerJob: ConsolidatedRegistrationsPerJob): Seq[File] = {
    pdfPresenter.renderRegistrationsPerJobs(consolidatedRegistrationsPerJob)
  }

  private def fileName(job: String, registrations: ConsolidatedRegistrations) =
    s"target/[Timesheet] - $job - ${dateRangeAsStringOf(registrations)}.pdf"
}
