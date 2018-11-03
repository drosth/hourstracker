package com.personal.hourstracker.service.impl

import scala.concurrent.{ ExecutionContext, Future }

import com.personal.hourstracker.domain.{ Registration, SearchParameters }
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.service.{ RegistrationSelector, RegistrationService }
import org.slf4j.Logger

class DefaultRegistrationService(registrationRepository: RegistrationRepository)(
  implicit
  logger: Logger,
  executionContext: ExecutionContext) extends RegistrationService {

  import com.personal.hourstracker.repository._

  override def importRegistrationsFrom(fileName: String)(implicit searchParameters: SearchParameters): Future[Registrations] = {
    logger.info(s"Importing registrations from: '$fileName'")

    registrationRepository
      .readRegistrationsFrom(fileName)
      .map {
        _.filter(determineSelectorFor(searchParameters))
      }
      .recover {
        case e =>
          logger.error(s"Could not import from '$fileName': ${e.getMessage}", e)
          Seq()
      }
  }

  def determineSelectorFor(searchParameters: SearchParameters): Registration => Boolean = searchParameters match {
    case SearchParameters(Some(startAt), None) => RegistrationSelector.registrationsStartingFrom(startAt)
    case SearchParameters(Some(startAt), Some(endAt)) => RegistrationSelector.registrationsBetween(startAt, endAt)
    case _ =>
      registration =>
        true
  }
}
