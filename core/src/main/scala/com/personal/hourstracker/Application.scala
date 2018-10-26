package com.personal.hourstracker

import java.util.Locale

import scala.collection.immutable
import scala.concurrent.{ Await, Future }
import scala.concurrent.duration._

import com.personal.hourstracker.config.ApplicationModule
import com.personal.hourstracker.domain.SearchParameters

object Application extends App with ApplicationModule {

  sys.addShutdownHook(terminate)

  implicit def locale: Locale = new Locale("nl", "NL")

  private lazy val terminate = {
    logger.info("Shutting down APPLICATION")
    system.terminate()
    Await.result(system.whenTerminated, 30 seconds)
  }

  implicit val searchParameters: SearchParameters = SearchParameters(Some("september"))

  val futureRendering: Future[immutable.Iterable[Unit]] = registrationService
    .importRegistrationsFrom(Application.importFrom)
    .map(facturationService.splitAllRegistrationsForFacturation)
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

          pdfPresenter.renderRegistrationsTo(consolidatedRegistrationsPerJob, s"target/$fileName.pdf")
      }
    }

  Await.result(futureRendering, Duration.Inf)

  terminate
}
