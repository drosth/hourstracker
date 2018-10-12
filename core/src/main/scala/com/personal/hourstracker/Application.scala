package com.personal.hourstracker

import java.time.{LocalDate, Month}

import com.personal.hourstracker.config.ApplicationModule
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.RegistrationSelector

object Application extends App with ApplicationModule {

  val registrations: Registrations = registrationService
    .readRegistrationsFrom(Application.importFrom)
    .filter(
      RegistrationSelector.registrationsBetween(
        LocalDate.of(2018, Month.SEPTEMBER, 1),
        LocalDate.of(2018, Month.SEPTEMBER, 30)))
    .flatMap(facturationService.splitForFacturation)

  import com.personal.hourstracker.domain.ConsolidatedRegistration.JsonProtocol._

  private val consolidatedRegistrations: Map[String, ConsolidatedRegistrations] =
    consolidatedRegistrationService
      .consolidateRegistrations(registrations)
      .groupBy(_.job)

  private val consolidatedRegistrationsWithMissingEntries =
    consolidatedRegistrationService
      .addUnregisteredEntriesTo(consolidatedRegistrations)

  println(
    jsonPresenter.renderRegistrationsTo(consolidatedRegistrations)
      .prettyPrint)

  consolidatedRegistrationsWithMissingEntries.foreach {
    case (job, registrations) => {

      val fileName =
        s"[Timesheet] - $job - ${dateRangeAsStringOf(registrations)}"

      htmlPresenter.renderRegistrationsTo(registrations, s"target/$fileName.html")

      pdfPresenter.renderRegistrationsTo(registrations, s"target/$fileName.pdf")
    }
  }
}
