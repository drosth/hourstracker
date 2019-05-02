package com.personal.hourstracker.service.impl

import java.io.File
import java.util.Locale

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Sink, Source }
import com.personal.hourstracker.Application.consolidatedRegistrationService
import com.personal.hourstracker.config.component.FacturationComponent
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrationsPerJob
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.service.RegistrationService.RegistrationRequest
import com.personal.hourstracker.service.{ FacturationService, ImporterService, RegistrationService }
import org.slf4j.Logger

import scala.concurrent.{ ExecutionContext, Future }

class DefaultRegistrationService(
  registrationRepository: RegistrationRepository,
  importService: ImporterService,
  facturationService: FacturationService)(
  implicit
  logger: Logger,
  locale: Locale,
  executionContext: ExecutionContext) extends RegistrationService {

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

  val storeRegistration: Flow[Registration, Either[String, Registration], NotUsed] = Flow[Registration].map(r =>
    registrationRepository.save(r) match {
      case Left(message) =>
        Left(s"Could not persist registration: $message")

      case Right(registration) => Right(registration)
    })

  override def importRegistrationsFromSource(fileName: String): Source[Either[String, Registration], NotUsed] = {
    logger.info(s"Importing registrations from: '$fileName' to Source")

    importService.importRegistrationsFromSource(fileName) match {
      case Left(message) =>
        Source.single(Left(s"Could not import '$fileName': $message"))

      case Right(source) =>
        source.via(storeRegistration)
    }
  }

  def storeRegistrations(registrations: Registrations): Future[Unit] = {
    logger.info(s"Storing #${registrations.size} registrations")

    Future({
      registrations.foreach(
        registration =>
          registrationRepository.save(registration) match {
            case Left(error) => logger.warn(s"Could not store registration: '$error'")
            case a => a
          })
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
    processConsolidatedRegistrations: ConsolidatedRegistrationsPerJob => T): Flow[Registrations, T, NotUsed] = {
    Flow[Registrations].map(registrations => {
      logger.info(s"Number of Registrations to render PDF for: ${registrations.size}")

      consolidatedRegistrationService.consolidateAndProcessRegistrations(registrations)(processConsolidatedRegistrations)
    })
  }

  override def consolidateRegistrations[T](
    registrations: Source[Registration, NotUsed])(processConsolidatedRegistrations: ConsolidatedRegistrationsPerJob => T): Source[T, NotUsed] = {

    registrations
      .via(splitRegistrationByTags)
      .fold[List[Registration]](List[Registration]())((aggr, registrations) => aggr ::: registrations)
      .via(consolidateAndProcessRegistrations(processConsolidatedRegistrations))
  }
}
