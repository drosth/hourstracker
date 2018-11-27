package com.personal.hourstracker.config.component

import scala.concurrent.Future

import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.domain.SearchParameters
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.service.{ImportService, RegistrationService}
import com.personal.hourstracker.service.impl.{CSVImportService, DefaultRegistrationService}


trait RegistrationComponent {
  this: LoggingComponent with SystemComponent =>

  val importService: ImportService = new CSVImportService()

  def registrationService: RegistrationService = new DefaultRegistrationService(importService)
}

object RegistrationRepositoryFactory {
  var _registrationRepository: RegistrationRepository = new RegistrationRepository() {
    override def load()(
      implicit searchParameters: SearchParameters
    ): Future[Registrations] = ???

    override def store(registrations: Registrations): Future[Unit] = ???
  }

  // Getter
  def registrationRepository = _registrationRepository

  // Setter
  def registrationRepository_=(newRegistrationRepository: RegistrationRepository): Unit =
    _registrationRepository = newRegistrationRepository
}
