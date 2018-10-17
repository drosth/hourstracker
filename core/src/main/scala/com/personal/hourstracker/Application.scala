package com.personal.hourstracker

import java.time.{ LocalDate, Month }

import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

import com.personal.hourstracker.config.ApplicationModule
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.RegistrationSelector

object Application extends App with ApplicationModule {

  sys.addShutdownHook(terminate)

  private lazy val terminate = {
    logger.info("Shutting down APPLICATION")
    system.terminate()
    Await.result(system.whenTerminated, 30 seconds)
  }

  val imported: Future[Registrations] = registrationService
    .importRegistrationsFrom(Application.importFrom)

  /*
  println(
    jsonPresenter.renderRegistrationsTo(consolidatedRegistrations)
      .prettyPrint)
   */

  val f: Future[Registrations] = imported
    .map(
      registrations =>
        registrations
          .filter(
            RegistrationSelector.registrationsBetween(LocalDate.of(2018, Month.SEPTEMBER, 1), LocalDate.of(2018, Month.SEPTEMBER, 30)))
          .flatMap(facturationService.splitForFacturation))

  val registrations: Registrations = Await.result(f, 10 seconds)

  consolidatedRegistrationService
    .addUnregisteredEntriesTo(
      consolidatedRegistrationService
        .consolidateRegistrations(registrations)
        .groupBy(_.job))
    .foreach {
      case (job, consolidatedRegistrationsPerJob) => {

        val fileName =
          s"[Timesheet] - $job - ${dateRangeAsStringOf(consolidatedRegistrationsPerJob)}"

        htmlPresenter.renderRegistrationsTo(consolidatedRegistrationsPerJob, s"target/$fileName.html")

        pdfPresenter.renderRegistrationsTo(consolidatedRegistrationsPerJob, s"target/$fileName.pdf")
      }
    }

  terminate
}
