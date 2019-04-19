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
import com.personal.hourstracker.config.component.{ LoggingComponent, RegistrationComponent, SystemComponent }
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.RegistrationSelector.{ RegistrationRangeSelector, _ }
import com.personal.hourstracker.service.RegistrationService.{ SelectByYear, SelectByYearAndMonth }
import com.personal.hourstracker.service.Selector

import scala.concurrent.Future
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
  this: RegistrationComponent with LoggingComponent with Configuration =>

  import RegistrationApi._

  lazy val registrationRoutes: Route = getRegistrations ~ importRegistrations

  private def whenCompleteProcessRegistrations(
    withFutureExecution: Future[Registrations])(process: Registrations => Registrations = collection => collection): Route = {
    onComplete(withFutureExecution) {
      case Success(registrations) =>
        complete(process(registrations).map(_.convert()))

      case Failure(e) =>
        logger.error(e.getMessage)
        complete(StatusCodes.NotFound, e.getMessage)
    }
  }

  override def getRegistrations: Route = {
    implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
      EntityStreamingSupport
        .json()
        .withParallelMarshalling(parallelism = 8, unordered = false)

    get {
      pathPrefix("registration") {
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

  private def withSelectorFor(startAt: Option[String], endAt: Option[String]): Option[Selector] = (startAt, endAt) match {
    case (None, None) => None
    case _ => Some(RegistrationRangeSelector(startAt, endAt))
  }

  private def filterRegistrations(year: Int): Registration => Boolean =
    registration =>
      registration.clockedIn match {
        case None => false
        case Some(clocked) => clocked.getYear == year
      }

  private def filterRegistrations(year: Int, month: Int): Registration => Boolean =
    registration =>
      registration.clockedIn match {
        case None => false
        case Some(clocked) => clocked.getYear == year && clocked.getMonthValue == month
      }

  override def importRegistrations: Route = path("registration" / "import") {
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
