package com.personal.hourstracker.api.v1.consolidatedregistration

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import scala.concurrent.duration._

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.util.Timeout
import com.personal.hourstracker.config.component.{ FacturationComponent, RegistrationComponent, SystemComponent }
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.{ ConsolidatedRegistration, SearchParameters }
import com.personal.hourstracker.Application.consolidatedRegistrationService
import com.personal.hourstracker.dateRangeAsStringOf
import com.personal.hourstracker.service.presenter.PresenterComponents
import io.swagger.v3.oas.annotations.media.Schema

object ConsolidatedRegistrationApi {
  implicit lazy val locale: Locale = new Locale("nl", "NL")

  type ConsolidatedRegistrationModels = Seq[ConsolidatedRegistrationModel]
  private val localDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  implicit val askTimeout: Timeout = 500.millis

  @Schema
  final case class ConsolidatedRegistrationModel(date: LocalDate, job: String, duration: Option[Double], comment: Option[String])

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

trait ConsolidatedRegistrationApi
  extends ConsolidatedRegistrationApiDoc
  with ConsolidatedRegistrationApiProtocol {

  this: RegistrationComponent with FacturationComponent with PresenterComponents with SystemComponent with Configuration =>

  lazy val consolidatedRegistrationRoutes: Route = getConsolidatedRegistrations

  val myEncodings = Seq(MediaRange(`application/pdf`), MediaRange(`application/json`))

  //  val AcceptJson = Accept(MediaRange(MediaTypes.`application/json`))

  override def getConsolidatedRegistrations: Route =
    path("consolidated") {
      (get & extract(_.request.headers)) { requestHeaders =>
        println(s"requestHeaders: $requestHeaders")

        parameters("startAt".as[String].?, "endAt".as[String].?) { (startAt, endAt) =>

          implicit def locale: Locale = new Locale("nl", "NL")
          implicit val searchParameters: SearchParameters = SearchParameters(startAt, endAt)

          onSuccess(registrationService
            .importRegistrationsFrom(Application.importFrom)
            .map(facturationService.splitAllRegistrationsForFacturation)
            .map(consolidatedRegistrationService.consolidateRegistrations())
            .map(consolidatedRegistrationService.consolidateRegistrationsPerJob())
            .map(consolidatedRegistrationService.addUnregisteredRegistrationsPerJob())) { consolidatedRegistrationsPerJob =>
            {
              consolidatedRegistrationsPerJob.foreach {
                case (job, registrations) =>
                  val fileName = s"target/[Timesheet] - $job - ${dateRangeAsStringOf(registrations)}.pdf"
                  pdfPresenter.renderRegistrationsTo(registrations, fileName)
              }
              complete(StatusCodes.OK)
            }
          }
        }
      }
    }
}
