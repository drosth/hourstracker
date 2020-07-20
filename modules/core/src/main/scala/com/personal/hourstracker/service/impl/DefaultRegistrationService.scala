package com.personal.hourstracker.service.impl

import java.util.Locale

import scala.concurrent.{ExecutionContext, Future}

class DefaultRegistrationService(
                                  registrationRepository: RegistrationRepository,
                                  importService: ImporterService,
                                  facturationService: FacturationService,
                                  consolidatedRegistrationService: ConsolidatedRegistrationService
                                )(
                                  implicit
                                  logger: Logger,
                                  locale: Locale,
                                  executionContext: ExecutionContext
                                ) extends RegistrationService {

  override def importRegistrationsFrom(fileName: String): Future[Either[String, Int]] = {
    logger.info(s"Importing registrations from: '$fileName'")

    importService
      .importRegistrationsFrom(fileName)
      .map {
        case Left(message) =>
          logger.warn(s"Could not import from '$fileName': '$message'")
          Left("Could not import registrations")
        case Right(registrations) =>
          storeRegistrations(registrations)
          Right(registrations.size)
      }
  }

  val storeRegistration: Flow[Registration, Either[String, Registration], NotUsed] = Flow[Registration].map(
    r =>
      registrationRepository.save(r) match {
        case Left(message) =>
          Left(s"Could not persist registration: $message")

        case Right(registration) => Right(registration)
      }
  )

  val storeRegistrations: Flow[Either[String, Registrations], Either[String, Registrations], NotUsed] =
    Flow[Either[String, Registrations]].map {
      case l@Left(message) =>
        logger.info(s"Skipping storage of registrations: $message")
        l

      case Right(registrations) =>
        logger.info(s"Storing #${registrations.size} registrations")

        val storedRegistrations = registrations.foldLeft[Registrations](List.empty) { (storedRegistrations, registration) =>
          registrationRepository.save(registration) match {
            case Left(error) =>
              logger.warn(s"Could not store registration: '$error'")
              storedRegistrations

            case Right(reg) => storedRegistrations :+ reg
          }
        }
        Right(storedRegistrations)
    }

  override def importRegistrationsFromSource(fileName: String): Source[Either[String, Registrations], NotUsed] = {
    logger.info(s"Importing registrations from: '$fileName' to Source")

    Source
      .future(importService.importRegistrationsFrom(fileName))
      .via(storeRegistrations)
  }

  def storeRegistrations(registrations: Registrations): Future[Unit] = {
    logger.info(s"Storing #${registrations.size} registrations")

    Future({
      registrations.foreach(
        registration =>
          registrationRepository.save(registration) match {
            case Left(error) => logger.warn(s"Could not store registration: '$error'")
            case a => a
          }
      )
    })
  }

  override def fetchRegistrations(): Source[Registration, NotUsed] = {
    logger.info(s"Fetching all registrations")
    registrationRepository.findAll()
  }

  override def fetchRegistrations(request: RegistrationRequest): Source[Registration, NotUsed] = {
    logger.info(s"Fetching registrations by request: '${request.getClass}': $request")
    registrationRepository.findByRequest(request)
  }

  private val splitRegistrationByTags: Flow[Registration, List[Registration], NotUsed] = Flow[Registration]
    .map(facturationService.splitOnTags)
    .alsoTo(Sink.foreach(i => logger.info(s"Number of Registrations after splitting #${i.head.id.getOrElse("")}: ${i.size}")))

  private def consolidateAndProcessRegistrations[T](
                                                     processConsolidatedRegistrations: ConsolidatedRegistrationsPerJob => T
                                                   ): Flow[Registrations, T, NotUsed] = {
    Flow[Registrations].map(registrations => {
      logger.info(s"Number of Registrations to render PDF for: ${registrations.size}")

      consolidatedRegistrationService.consolidateAndProcessRegistrations(registrations)(processConsolidatedRegistrations)
    })
  }

  override def consolidateRegistrations[T](
                                            registrations: Source[Registration, NotUsed]
                                          )(processConsolidatedRegistrations: ConsolidatedRegistrationsPerJob => T): Source[T, NotUsed] = {

    registrations
      .via(splitRegistrationByTags)
      .fold[List[Registration]](List[Registration]())((aggr, registrations) => aggr ::: registrations)
      .via(consolidateAndProcessRegistrations(processConsolidatedRegistrations))
  }
}
