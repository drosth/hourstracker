package com.personal.hourstracker

import java.util.Locale

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

import com.personal.hourstracker.config.ApplicationModule
import com.personal.hourstracker.domain.ConsolidatedRegistration.{ ConsolidatedRegistrations, ConsolidatedRegistrationsPerJob }
import com.personal.hourstracker.domain.SearchParameters
import com.personal.hourstracker.service.presenter.Presenter

object Application extends App with ApplicationModule {

  sys.addShutdownHook(terminate)

  lazy val presenter: Presenter[ConsolidatedRegistrationsPerJob] = pdfPresenter

  implicit def locale: Locale = new Locale("nl", "NL")

  private lazy val terminate = {
    logger.info("Shutting down APPLICATION")
    system.terminate()
    Await.result(system.whenTerminated, 30 seconds)
  }

  implicit val searchParameters: SearchParameters = SearchParameters(Some("okt"))

  def fileName(job: String, registrations: ConsolidatedRegistrations) =
    s"target/[Timesheet] - $job - ${dateRangeAsStringOf(registrations)}"

  /*
  def processConsolidatedRegistrationsPerJob(consolidatedRegistrationsPerJob: (String, ConsolidatedRegistrations)): File = {
    htmlPresenter.renderRegistrationsTo(
      consolidatedRegistrationsPerJob._2,
      s"${fileName(consolidatedRegistrationsPerJob._1, consolidatedRegistrationsPerJob._2)}.html")
  }
  */

  registrationService
    .importRegistrationsFrom(Application.importFrom)
    .map(facturationService.splitAllRegistrationsForFacturation)
    .map(consolidatedRegistrationService.consolidateAndProcessRegistrations(_) { registrations =>
      println(s"Processing #${registrations.size} items:")
      presenter.renderRegistrations(registrations)
    })
    .onComplete {
      case Failure(e) =>
        println(s"Some error occurred: ${e.getMessage}")
        terminate

      case Success(s) =>
        println("Done")
        terminate
    }
}
