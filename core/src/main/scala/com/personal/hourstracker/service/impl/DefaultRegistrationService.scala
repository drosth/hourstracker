package com.personal.hourstracker.service.impl

import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.domain.{ Registration, SearchParameters }
import com.personal.hourstracker.service.{ ImporterService, RegistrationSelector, RegistrationService }
import org.slf4j.Logger

import scala.concurrent.{ ExecutionContext, Future }

object DefaultRegistrationService {

  def determineSelectorFor(searchParameters: SearchParameters): Registration => Boolean = searchParameters match {
    case SearchParameters(Some(startAt), None) => RegistrationSelector.registrationsStartingFrom(startAt)
    case SearchParameters(Some(startAt), Some(endAt)) => RegistrationSelector.registrationsBetween(startAt, endAt)
    case _ =>
      registration =>
        true
  }
}

class DefaultRegistrationService(importService: ImporterService)(
  implicit
  logger: Logger,
  executionContext: ExecutionContext) extends RegistrationService {

  override def importRegistrationsFrom(fileName: String): Future[Either[String, Registrations]] = {
    logger.info(s"Importing registrations from: '$fileName'")

    importService
      .importRegistrationsFrom(fileName)
      .recover {
        case e =>
          Left(s"Could not import from '$fileName': ${e.getMessage}")
      }
  }

  //  def storeRegistrations(registrations: Registrations): Future[Unit] = {
  //    val registrationRepository = RegistrationRepositoryFactory.registrationRepository
  //    registrationRepository.store(registrations)
  //  }

  override def loadRegistrations()(implicit searchParameters: SearchParameters): Future[Registrations] = ???
}
