package com.personal.hourstracker

import java.util.Locale

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._
import scala.util.{ Failure, Success }
import com.personal.hourstracker.config.ApplicationModule
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrationsPerJob
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.domain.SearchParameters
import com.personal.hourstracker.service.presenter.Presenter

object Application extends App with ApplicationModule {

  sys.addShutdownHook(terminate)

  lazy val presenter: Presenter[ConsolidatedRegistrationsPerJob] = htmlPresenter

  implicit def locale: Locale = new Locale("nl", "NL")

  implicit val searchParameters: SearchParameters = SearchParameters(Some("sep"))

  val importedRegistrations: Future[Either[String, Registrations]] = registrationService.importRegistrationsFrom(Application.importFrom)
  importedRegistrations
    .map {
      case Right(registrations: Registrations) =>
        consolidatedRegistrationService.consolidateAndProcessRegistrations(
          facturationService.splitAllRegistrationsForFacturation(registrations)) { registrations =>
            logger.info(s"Processing #${registrations.size} items:")
            presenter.renderRegistrationsPerJob(registrations)
          }

      case Left(message) =>
        logger.info(s"Could not import registrations: '$message'")
    }
    .onComplete {
      case Failure(e) =>
        logger.info(s"Some error occurred: ${e.getMessage}")
        terminate

      case Success(s) =>
        logger.info("Done")
        terminate
    }

  private lazy val terminate = {
    logger.info("Shutting down APPLICATION")
    system.terminate()
    Await.result(system.whenTerminated, 30 seconds)
  }
}
