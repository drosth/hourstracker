package com.personal.hourstracker.api.v1.registration

import java.time.LocalDate

import akka.actor.{Actor, ActorLogging}
import com.personal.hourstracker.config.component.{RegistrationComponent, RegistrationService}
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.service.RegistrationSelector

final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[User])

object RegistrationActor extends RegistrationComponent with Configuration {

  lazy val importFrom: String = Application.importFrom

  final case object GetRegistrations

  final case class GetRegistrationsBetween(start: LocalDate, end: Option[LocalDate])
}

class RegistrationActor(registrationService: RegistrationService) extends Actor with ActorLogging {

  import RegistrationActor._

  def receive: Receive = {
    case GetRegistrations =>
      sender() ! registrationService.readRegistrationsFrom(importFrom)

    case req: GetRegistrationsBetween =>
      sender() ! registrationService
        .readRegistrationsFrom(importFrom)
        .filter(RegistrationSelector.registrationsBetween(req.start, req.end.getOrElse(LocalDate.of(9999, 12, 31))))
  }
}
