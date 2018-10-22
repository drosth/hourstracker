package com.personal.hourstracker.api.v1.registration

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import akka.actor.ActorRef
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.unmarshalling.Unmarshaller
import akka.pattern.ask
import akka.util.Timeout
import com.personal.hourstracker.api.v1.domain.RegistrationModel
import com.personal.hourstracker.api.v1.registration.RegistrationActor.GetRegistrationsBy
import com.personal.hourstracker.config.component.SystemComponent
import com.personal.hourstracker.domain.{ Registration, SearchParameters }
import scala.concurrent.duration._

object RegistrationApi {
  implicit lazy val locale: Locale = new Locale("nl", "NL")

  implicit val stringToLocalDateUnmarshaller: Unmarshaller[String, LocalDate] =
    Unmarshaller.strict[String, LocalDate] { LocalDate.parse(_, DateTimeFormatter.ISO_LOCAL_DATE) }

  object ModelAdapter {

    implicit def toModel(registration: Registration): RegistrationModel =
      RegistrationModel(
        registration.job,
        registration.clockedIn,
        registration.clockedOut,
        registration.duration,
        registration.hourlyRate,
        registration.earnings,
        registration.comment,
        registration.tags,
        registration.breaks,
        registration.adjustments,
        registration.totalTimeAdjustment,
        registration.totalEarningsAdjustment)
  }
}

trait RegistrationApi extends RegistrationApiProtocol with RegistrationApiDoc with SystemComponent {
  import RegistrationApi._

  lazy val registrationRoutes: Route = getRegistrations

  def registrationActor: ActorRef

  override def getRegistrations: Route =
    pathEndOrSingleSlash {
      get {
        parameters("startAt".as[String].?, "endAt".as[String].?) { (startAt, endAt) =>
          {
            val searchParameters = SearchParameters(startAt, endAt)
            onSuccess((registrationActor ? GetRegistrationsBy(searchParameters)).mapTo[List[Registration]]) { registrations =>
              val models: List[RegistrationModel] = registrations.map(ModelAdapter.toModel)
              complete(models)
            }
          }
        }
      }
    }
}
