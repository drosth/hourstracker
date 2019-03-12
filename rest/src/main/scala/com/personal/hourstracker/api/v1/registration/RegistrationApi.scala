package com.personal.hourstracker.api.v1.registration

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.unmarshalling.Unmarshaller
import com.personal.hourstracker.api.v1.domain.RegistrationModel
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.{LoggingComponent, RegistrationComponent, SystemComponent}
import com.personal.hourstracker.domain.{Registration, SearchParameters}
import com.personal.hourstracker.service.RegistrationSelector

import scala.util.{Failure, Success}

object RegistrationApi {
  implicit lazy val locale: Locale = new Locale("nl", "NL")

  implicit val stringToLocalDateUnmarshaller: Unmarshaller[String, LocalDate] =
    Unmarshaller.strict[String, LocalDate] {
      LocalDate.parse(_, DateTimeFormatter.ISO_LOCAL_DATE)
    }

  def determineSelectorFor(searchParameters: SearchParameters): Registration => Boolean = searchParameters match {
    case SearchParameters(Some(startAt), None)        => RegistrationSelector.registrationsStartingFrom(startAt)
    case SearchParameters(Some(startAt), Some(endAt)) => RegistrationSelector.registrationsBetween(startAt, endAt)
    case _ =>
      registration =>
        true
  }

  object ModelAdapter {

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
  this: RegistrationComponent with LoggingComponent with Configuration =>

  import RegistrationApi._

  lazy val registrationRoutes: Route = getRegistrations

  override def getRegistrations: Route =
    pathEndOrSingleSlash {
      get {
        parameters("startAt".as[String].?, "endAt".as[String].?) { (startAt, endAt) =>
          {
            implicit val searchParameters: SearchParameters = SearchParameters(startAt, endAt)

            onComplete(registrationService.importRegistrationsFrom(Application.importFrom)) {
              case Success(importedRegistrations) =>
                importedRegistrations match {
                  case Right(registrations) =>
                    val models = registrations
                      .filter(determineSelectorFor(searchParameters))
                      .map(ModelAdapter.toModel)
                    complete(models)

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
    }
}
