package com.personal.hourstracker.api.v1.registration

import java.time.LocalDate

import akka.actor.{Actor, ActorLogging}
import com.personal.hourstracker.config.component.{RegistrationComponent, RegistrationService}
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.{Registration, SearchParameters}
import com.personal.hourstracker.service.RegistrationSelector

final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[User])

object RegistrationActor extends RegistrationComponent with Configuration {

  lazy val importFrom: String = Application.importFrom

  final case object GetRegistrations

  final case class GetRegistrationsBy(searchParameters: SearchParameters)
}

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
      sender() ! registrationService.readRegistrationsFrom(importFrom)

    case req: GetRegistrationsBy =>
      sender() ! registrationService
        .readRegistrationsFrom(importFrom)
        .filter(determineSelectorFor(req.searchParameters))
  }
}
