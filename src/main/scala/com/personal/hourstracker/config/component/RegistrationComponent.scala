package com.personal.hourstracker.config.component

import java.io.Reader

import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.repository.DefaultRegistrationRepository
import com.personal.hourstracker.service.DefaultRegistrationService


trait RegistrationComponent
    extends DefaultRegistrationService
        with DefaultRegistrationRepository


trait RegistrationService {
  def registrationService: RegistrationService


  trait RegistrationService {
    def readRegistrationsFrom(fileName: String): Registrations
  }


}


trait RegistrationRepository {
  def registrationRepository: RegistrationRepository


  trait RegistrationRepository {
    def readRegistrationsFrom(reader: Reader): Registrations
  }


}
