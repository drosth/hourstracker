package com.personal.hourstracker.service

import com.personal.hourstracker.config.component.{RegistrationRepository, RegistrationService}
import com.personal.hourstracker.domain.Registration.Registrations


trait DefaultRegistrationService extends RegistrationService {
  this: RegistrationRepository =>

  def registrationService: DefaultRegistrationService = new DefaultRegistrationService()


  class DefaultRegistrationService extends RegistrationService {

    import com.personal.hourstracker.repository._


    override def readRegistrationsFrom(fileName: String): Registrations = registrationRepository.readRegistrationsFrom(fileName)
  }


}
