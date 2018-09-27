package com.personal.hourstracker

import com.personal.hourstracker.config.ApplicationModule
import com.personal.hourstracker.domain.ConsolidatedRegistration
import com.personal.hourstracker.domain.ConsolidatedRegistration.DateTimeOrdering
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.RegistrationSelector


object Application extends App
    with ApplicationModule {

  val fileName = "src/main/resources/CSVExport.csv"

  println(fileName)

  val registrations: Registrations = registrationService.readRegistrationsFrom(fileName)
      .filter(RegistrationSelector.registrationsForCurrentMonth)
      .flatMap(facturationService.splitForFacturation)

  import com.personal.hourstracker.domain.ConsolidatedRegistration.JsonProtocol._
  import spray.json._


  private val consolidatedRegistrations: Map[String, Seq[ConsolidatedRegistration]] = consolidatedRegistrationService
      .consolidateRegistrations(registrations)
      .sorted(DateTimeOrdering)
      .groupBy(_.job)

  println(consolidatedRegistrations.toJson.prettyPrint)

  consolidatedRegistrations.foreach(_ match {
    case (job, registrations) => {

      htmlPresenter.renderRegistrationsTo(registrations, s"CSVExport-$job.html")

      pdfPresenter.renderRegistrationsTo(registrations, s"CSVExport-$job.pdf")
    }
  })
}
