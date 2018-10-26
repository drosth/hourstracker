package com.personal.hourstracker.config.component

import com.personal.hourstracker.repository.{ DefaultRegistrationRepository, RegistrationRepository }
import com.personal.hourstracker.service.{ DefaultRegistrationService, RegistrationService }

trait RegistrationComponent {
  this: LoggingComponent with SystemComponent =>

  val registrationRepository: RegistrationRepository = DefaultRegistrationRepository()
  def registrationService: RegistrationService = new DefaultRegistrationService(registrationRepository)
}
