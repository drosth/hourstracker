package com.personal.hourstracker.config.component

import com.personal.hourstracker.importer.service.CSVImportService
import com.personal.hourstracker.service.impl.DefaultRegistrationService
import com.personal.hourstracker.service.{ ImporterService, RegistrationService }

trait RegistrationComponent {
  this: RegistrationRepositoryComponent with ImporterServiceComponent with LoggingComponent with SystemComponent =>

  def registrationService: RegistrationService = new DefaultRegistrationService(registrationRepository, importerService)
}

//object RegistrationRepositoryFactory {
//  var _registrationRepository: RegistrationRepository = new RegistrationRepository() {
//    override def load()(
//      implicit
//      searchParameters: SearchParameters): Future[Registrations] = ???
//
//    override def store(registrations: Registrations): Future[Unit] = ???
//  }
//
//  // Getter
//  def registrationRepository = _registrationRepository
//
//  // Setter
//  def registrationRepository_=(newRegistrationRepository: RegistrationRepository): Unit =
//    _registrationRepository = newRegistrationRepository
//}
