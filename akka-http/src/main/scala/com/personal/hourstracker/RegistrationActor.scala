package com.personal.hourstracker

import akka.actor.{ Actor, ActorLogging }
import com.personal.hourstracker.config.component.{ RegistrationComponent, RegistrationService }

final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[User])

object RegistrationActor extends RegistrationComponent {

  final case class ActionPerformed(description: String)

  final case object GetRegistrations
}

class RegistrationActor(registrationService: RegistrationService) extends Actor with ActorLogging {

  import RegistrationActor._

  val fileName =
    "/Users/hansd/data/Projects/hourstracker/core/src/main/resources/CSVExport.csv"

  def receive: Receive = {
    case GetRegistrations =>
      sender() ! registrationService.readRegistrationsFrom(fileName)
  }
}
