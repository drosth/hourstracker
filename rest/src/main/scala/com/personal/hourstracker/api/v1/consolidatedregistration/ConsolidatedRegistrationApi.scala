package com.personal.hourstracker.api.v1.consolidatedregistration

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.server.{MediaTypeNegotiator, Route}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.unmarshalling.Unmarshaller
import com.personal.hourstracker.config.component.{FacturationComponent, RegistrationComponent, SystemComponent}
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.{ConsolidatedRegistration, SearchParameters}
import com.personal.hourstracker.Application.consolidatedRegistrationService
import com.personal.hourstracker.dateRangeAsStringOf
import com.personal.hourstracker.domain.ConsolidatedRegistration.{ConsolidatedRegistrations, ConsolidatedRegistrationsPerJob}
import com.personal.hourstracker.service.presenter.config.PresenterComponents
import io.swagger.v3.oas.annotations.media.Schema

object ConsolidatedRegistrationApi {
  type ConsolidatedRegistrationModels = Seq[ConsolidatedRegistrationModel]

  implicit val stringToLocalDateUnmarshaller: Unmarshaller[String, LocalDate] =
    Unmarshaller.strict[String, LocalDate] {
      LocalDate.parse(_, localDateFormatter)
    }

  private val localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

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
    parameters("startAt".as[String].?, "endAt".as[String].?) { (startAt, endAt) =>
      implicit lazy val locale: Locale = new Locale("nl", "NL")
      implicit val searchParameters: SearchParameters = SearchParameters(startAt, endAt)

      onSuccess(
        registrationService
          .importRegistrationsFrom(Application.importFrom)
          .map(facturationService.splitAllRegistrationsForFacturation)
          .map(consolidatedRegistrationService.consolidateAndProcessRegistrations(_) { consolidatedRegistrationsPerJob =>
            println(s"Processing #${consolidatedRegistrationsPerJob.size} items:")
            processConsolidatedRegistrationsPerJob(consolidatedRegistrationsPerJob)
          })) { files =>
          {
            files.size match {
              case 1 => getFromFile(files.head)

              case numberOfFiles if numberOfFiles > 1 =>
                getFromBrowseableDirectories("target")

              case _ => complete(StatusCodes.NotFound)
            }
          }
        }
    }
  }

  def processConsolidatedRegistrationsPerJob(consolidatedRegistrationsPerJob: ConsolidatedRegistrationsPerJob): Seq[File] = {
    pdfPresenter.renderRegistrationsPerJob(consolidatedRegistrationsPerJob)
  }

  def fileName(job: String, registrations: ConsolidatedRegistrations) =
    s"target/[Timesheet] - $job - ${dateRangeAsStringOf(registrations)}.pdf"
}
