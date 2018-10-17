package com.personal.hourstracker.config.component

import java.io.Reader

import scala.concurrent.Future

import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.domain.SearchParameters
import com.personal.hourstracker.repository.DefaultRegistrationRepository
import com.personal.hourstracker.service.DefaultRegistrationService

trait RegistrationComponent extends DefaultRegistrationService
  with DefaultRegistrationRepository
  with LoggingComponent
  with SystemComponent

trait RegistrationServiceContract {
  this: RegistrationRepository =>

  def registrationService: RegistrationService
}

trait RegistrationService {
  def importRegistrationsFrom(fileName: String): Future[Registrations]
}

trait RegistrationRepository {
  def registrationRepository: RegistrationRepository

  trait RegistrationRepository {
    def readRegistrationsFrom(reader: Reader): Future[Registrations]
  }
}
