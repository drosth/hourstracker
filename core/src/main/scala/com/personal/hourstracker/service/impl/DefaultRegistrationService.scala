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
      .recover {
        case e =>
          Left(s"Could not import from '$fileName': ${e.getMessage}")
      }
  }

  override def storeRegistrations(registrations: Registrations): Future[Either[String, Seq[Long]]] = {
    val result: Either[String, List[Long]] = registrations
      .map(registrationRepository.save) // Seq[Either[String, Long]]
      .partition(_.isLeft) match { // (List[Either[String, Long]], List[Either[String, Long]])
        case (Nil, ids) =>
          Right(for (Right(id) <- ids) yield id) // Either[Nothing, List[Long]]
        case (messages, _) =>
          Left((for (Left(message) <- messages) yield message).head) // Either[String, Nothing]
      }
    Future.successful(result)
  }
}
