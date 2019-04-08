package com.personal.hourstracker.config.component

import com.personal.hourstracker.service.RegistrationService
import com.personal.hourstracker.service.impl.DefaultRegistrationService

trait RegistrationComponent {
  this: RegistrationRepositoryComponent with ImporterServiceComponent with LoggingComponent with SystemComponent =>

  def registrationService: RegistrationService = new DefaultRegistrationService(registrationRepository, importerService)
}
