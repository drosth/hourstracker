package com.personal.hourstracker

import java.io.File
import java.util.Locale

import scala.concurrent.Await
import scala.concurrent.duration._

import com.personal.hourstracker.config.ApplicationModule
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations
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

  def fileName(job: String, registrations: ConsolidatedRegistrations) =
    s"target/[Timesheet] - $job - ${dateRangeAsStringOf(registrations)}.pdf"

  def processConsolidatedRegistrationsPerJob(consolidatedRegistrationsPerJob: (String, ConsolidatedRegistrations)): File = {
    pdfPresenter.renderRegistrationsTo(
      consolidatedRegistrationsPerJob._2,
      fileName(consolidatedRegistrationsPerJob._1, consolidatedRegistrationsPerJob._2))
  }

  val futureRendering = registrationService
    .importRegistrationsFrom(Application.importFrom)
    .map(facturationService.splitAllRegistrationsForFacturation)
    .map(consolidatedRegistrationService.consolidateRegistrations())
    .map(consolidatedRegistrationService.consolidateRegistrationsPerJob())
    .map(consolidatedRegistrationService.addUnregisteredRegistrationsPerJob())
    .map { item =>
      item.foreach(processConsolidatedRegistrationsPerJob)
    }

  Await.result(futureRendering, Duration.Inf)

  terminate
}
