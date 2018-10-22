package com.personal.hourstracker

import java.time.{ LocalDate, Month }

import scala.collection.immutable
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

import com.personal.hourstracker.config.ApplicationModule
import com.personal.hourstracker.service.RegistrationSelector

object Application extends App with ApplicationModule {

  sys.addShutdownHook(terminate)

  private lazy val terminate = {
    logger.info("Shutting down APPLICATION")
    system.terminate()
    Await.result(system.whenTerminated, 30 seconds)
  }

  val futureRendering: Future[immutable.Iterable[Unit]] = registrationService
    .importRegistrationsFrom(Application.importFrom)
    .map(
      _.filter(RegistrationSelector.registrationsBetween(LocalDate.of(2018, Month.SEPTEMBER, 1), LocalDate.of(2018, Month.SEPTEMBER, 30)))
        .flatMap(facturationService.splitForFacturation))
    .map(consolidatedRegistrationService.consolidateRegistrations())
    .map(consolidatedRegistrationService.consolidateRegistrationsPerJob())
    .map(consolidatedRegistrationService.addUnregisteredRegistrationsPerJob())
    .map {
      _.map {
        case (job, consolidatedRegistrationsPerJob) =>
          val fileName =
            s"[Timesheet] - $job - ${dateRangeAsStringOf(consolidatedRegistrationsPerJob)}"

          //            logger.info("Rendering to HTML")
          //            htmlPresenter.renderRegistrationsTo(consolidatedRegistrationsPerJob, s"target/$fileName.html")

          logger.info("Rendering to PDF")
          pdfPresenter.renderRegistrationsTo(consolidatedRegistrationsPerJob, s"target/$fileName.pdf")
      }
    }

  Await.result(futureRendering, Duration.Inf)

  terminate
}
