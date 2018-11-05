package com.personal.hourstracker

import java.util.Locale

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

import com.personal.hourstracker.config.ApplicationModule
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrationsPerJob
import com.personal.hourstracker.domain.SearchParameters
import com.personal.hourstracker.service.presenter.Presenter

object Application extends App with ApplicationModule {

  sys.addShutdownHook(terminate)

  lazy val presenter: Presenter[ConsolidatedRegistrationsPerJob] = htmlPresenter

  implicit def locale: Locale = new Locale("nl", "NL")

  implicit val searchParameters: SearchParameters = SearchParameters(Some("sep"))

  registrationService
    .importRegistrationsFrom(Application.importFrom)
    .map(facturationService.splitAllRegistrationsForFacturation)
    .map(consolidatedRegistrationService.consolidateAndProcessRegistrations(_) { registrations =>
      logger.info(s"Processing #${registrations.size} items:")
      presenter.renderRegistrationsPerJob(registrations)
    })
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
