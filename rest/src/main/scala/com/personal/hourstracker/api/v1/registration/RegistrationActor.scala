package com.personal.hourstracker.api.v1.registration

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import com.personal.hourstracker.config.component.{ SystemComponent, _ }
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.{ Registration, SearchParameters }
import com.personal.hourstracker.service.RegistrationSelector

final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[User])

object RegistrationActor {

  final case object GetRegistrations

  final case class GetRegistrationsBy(searchParameters: SearchParameters)
}

trait RegistrationActor {
  this: RegistrationServiceContract with Configuration with SystemComponent =>

  def registrationActor: ActorRef = system.actorOf(Props(new RegistrationActor(registrationService)), "registrationActor")

  class RegistrationActor(registrationService: RegistrationService) extends Actor with ActorLogging {

    import RegistrationActor._

    def determineSelectorFor(searchParameters: SearchParameters): Registration => Boolean = searchParameters match {
      case SearchParameters(Some(startAt), None) => RegistrationSelector.registrationsStartingFrom(startAt)
      case SearchParameters(Some(startAt), Some(endAt)) => RegistrationSelector.registrationsBetween(startAt, endAt)
      case _ =>
        registration =>
          true
    }

    def receive: Receive = {
      case GetRegistrations =>
        sender() ! registrationService.importRegistrationsFrom(Application.importFrom)

      case request: GetRegistrationsBy =>
        sender() ! registrationService
          .importRegistrationsFrom(Application.importFrom)
          .map(_.filter(determineSelectorFor(request.searchParameters)))
    }
  }
}
