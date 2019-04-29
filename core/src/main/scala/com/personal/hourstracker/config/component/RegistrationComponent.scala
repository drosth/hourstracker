package com.personal.hourstracker.config.component

import java.util.Locale

import com.personal.hourstracker.service.RegistrationService
import com.personal.hourstracker.service.impl.DefaultRegistrationService

trait RegistrationComponent {
  this: RegistrationRepositoryComponent with ImporterServiceComponent with FacturationComponent with LoggingComponent with SystemComponent =>

  implicit lazy val locale: Locale = new Locale("nl", "NL")

  def registrationService: RegistrationService = new DefaultRegistrationService(registrationRepository, importerService, facturationService)
}
