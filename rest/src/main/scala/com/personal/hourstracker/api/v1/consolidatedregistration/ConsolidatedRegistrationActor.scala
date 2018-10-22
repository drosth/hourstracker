package com.personal.hourstracker.api.v1.consolidatedregistration

import scala.concurrent.Future

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }
import com.personal.hourstracker.config.component._
import com.personal.hourstracker.domain.Registration.Registrations

final case class User(name: String, age: Int, countryOfResidence: String)

final case class Users(users: Seq[User])

object ConsolidatedRegistrationActor {

  final case class GetConsolidatedRegistrations(registrations: Future[Registrations])
}

trait ConsolidatedRegistrationActor {
  this: ConsolidatedRegistrationServiceContract with SystemComponent =>

  def consolidatedRegistrationActor: ActorRef =
    system.actorOf(Props(new ConsolidatedRegistrationActor(consolidatedRegistrationService)), "consolidatedRegistrationActor")

  class ConsolidatedRegistrationActor(consolidatedRegistrationService: ConsolidatedRegistrationService) extends Actor with ActorLogging {
    import ConsolidatedRegistrationActor._

    def receive: Receive = {
      case request: GetConsolidatedRegistrations =>
        sender() ! request.registrations
          .map(consolidatedRegistrationService.consolidateRegistrations())
    }
  }
}
