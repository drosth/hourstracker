package com.personal.hourstracker.api.v1.consolidatedregistration

import java.time.LocalDate

import akka.actor.{Actor, ActorLogging}
import com.personal.hourstracker.config.component.{ConsolidatedRegistrationComponent, RegistrationComponent, RegistrationService}
import com.personal.hourstracker.config.Configuration

final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[User])

object ConsolidatedRegistrationActor extends RegistrationComponent with ConsolidatedRegistrationComponent with Configuration {

  final case class GetConsolidatedRegistrations(start: LocalDate, end: LocalDate)
}

class ConsolidatedRegistrationActor(registrationService: RegistrationService) extends Actor with ActorLogging {

  import ConsolidatedRegistrationActor._

  def receive: Receive = {
    case GetConsolidatedRegistrations =>
      sender() ! registrationService.readRegistrationsFrom("")
  }
}
