package com.personal.hourstracker.service.impl

import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.service.{ ImporterService, RegistrationService }
import org.slf4j.Logger

import scala.concurrent.{ ExecutionContext, Future }

class DefaultRegistrationService(registrationRepository: RegistrationRepository, importService: ImporterService)(
  implicit
  logger: Logger,
  executionContext: ExecutionContext) extends RegistrationService {

  override def importRegistrationsFrom(fileName: String): Future[Either[String, Registrations]] = {
    logger.info(s"Importing registrations from: '$fileName'")

    importService
      .importRegistrationsFrom(fileName)
      .map {
        case Left(message) =>
          logger.warn(s"Could not import from '$fileName': '$message'")
          Left("Could not import registrations")
        case Right(registrations) =>
          storeRegistrations(registrations)
          Right(registrations)
      }
  }

  override def storeRegistrations(registrations: Registrations): Future[Unit] = {
    logger.info(s"Storing #${registrations.size} registrations")

    Future({
      registrations
        .map(registration =>
          registrationRepository.save(registration) match {
            case Left(message) =>
              Left(s"Could not store registration: '$message'")
            case _ => Right(())
          })
        .partition(_.isLeft) match {
          case (messages, _) =>
            for (Left(message) <- messages) yield message
        }
      ()
    })
  }
}
