package com.personal.hourstracker.service.impl

import java.io.{ File, FileInputStream, InputStreamReader, Reader }

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.Success

import com.personal.hourstracker.config.component.RegistrationRepositoryFactory
import com.personal.hourstracker.domain.{ Registration, SearchParameters }
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.{ DEFAULT_ENCODING, ImportService, RegistrationSelector, RegistrationService }
import org.slf4j.Logger

object DefaultRegistrationService {
  implicit def toReader(fileName: String): Reader = {
    new File(fileName)
  }

  implicit def toReader(file: File): Reader = {
    new InputStreamReader(new FileInputStream(file), DEFAULT_ENCODING)
  }

  def determineSelectorFor(searchParameters: SearchParameters): Registration => Boolean = searchParameters match {
    case SearchParameters(Some(startAt), None) => RegistrationSelector.registrationsStartingFrom(startAt)
    case SearchParameters(Some(startAt), Some(endAt)) => RegistrationSelector.registrationsBetween(startAt, endAt)
    case _ =>
      registration =>
        true
  }
}

class DefaultRegistrationService(
  importService: ImportService)(
  implicit
  logger: Logger,
  executionContext: ExecutionContext) extends RegistrationService {

  import DefaultRegistrationService._

  override def importRegistrationsFrom(fileName: String)(implicit searchParameters: SearchParameters): Future[Registrations] = {
    logger.info(s"Importing registrations from: '$fileName'")

    val registrations: Future[Registrations] = importService
      .importRegistrationsFrom(fileName)
      .map {
        _.filter(determineSelectorFor(searchParameters))
      }
      .recover {
        case e =>
          logger.error(s"Could not import from '$fileName': ${e.getMessage}", e)
          List()
      }

    registrations.onComplete {
      case Success(registrationsToStore) => storeRegistrations(registrationsToStore)
    }

    registrations
  }

  def storeRegistrations(registrations: Registrations): Future[Unit] = {
    val registrationRepository = RegistrationRepositoryFactory.registrationRepository
    registrationRepository.store(registrations)
  }

  override def loadRegistrations()(implicit searchParameters: SearchParameters): Future[Registrations] = {
    val registrationRepository = RegistrationRepositoryFactory.registrationRepository
    registrationRepository.load()(searchParameters)
  }
}
