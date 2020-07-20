package com.personal.hourstracker.config.component

import java.util.Locale

trait RegistrationComponent {
  this: RegistrationRepositoryComponent with ImporterServiceComponent with FacturationComponent with ConsolidatedRegistrationComponent with LoggingComponent with SystemComponent =>

  implicit lazy val locale: Locale = new Locale("nl", "NL")

  def registrationService: RegistrationService =
    new DefaultRegistrationService(registrationRepository, importerService, facturationService, consolidatedRegistrationService)
}
