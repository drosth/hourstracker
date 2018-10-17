package com.personal.hourstracker.service

import scala.concurrent.Future

import com.personal.hourstracker.config.component._
import com.personal.hourstracker.domain.Registration.Registrations

trait DefaultRegistrationService extends RegistrationServiceContract {
  this: RegistrationRepository with LoggingComponent with SystemComponent =>

  def registrationService: DefaultRegistrationService =
    new DefaultRegistrationService()

  class DefaultRegistrationService extends RegistrationService {

    import com.personal.hourstracker.repository._

    override def importRegistrationsFrom(fileName: String): Future[Registrations] = {
      registrationRepository.readRegistrationsFrom(fileName).recover {
        case e =>
          logger.error(s"Could not import from '$fileName': ${e.getMessage}", e)
          Seq()
      }
    }
  }
}
