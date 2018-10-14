package com.personal.hourstracker

import java.time.LocalDate

import akka.actor.{Actor, ActorLogging}
import com.personal.hourstracker.config.component.{RegistrationComponent, RegistrationService}
import com.personal.hourstracker.config.Configuration

final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[User])

object RegistrationActor extends RegistrationComponent with Configuration {

  lazy val importFrom: String = Application.importFrom

  final case class ActionPerformed(description: String)

  final case object GetRegistrations

  final case class GetConsolidatedRegistrationsAsPdf(start: LocalDate, end: LocalDate)
}

class RegistrationActor(registrationService: RegistrationService) extends Actor with ActorLogging {

  import RegistrationActor._

  def receive: Receive = {
    case GetRegistrations =>
      sender() ! registrationService.readRegistrationsFrom(importFrom)

    case GetConsolidatedRegistrationsAsPdf =>
      sender() ! registrationService.readRegistrationsFrom(importFrom)
  }
}
