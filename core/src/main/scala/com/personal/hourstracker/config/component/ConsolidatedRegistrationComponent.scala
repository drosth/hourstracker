package com.personal.hourstracker.config.component

import com.personal.hourstracker.domain.ConsolidatedRegistration.{ ConsolidatedRegistrations, ConsolidatedRegistrationsPerJob }
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.DefaultConsolidatedRegistrationService

trait ConsolidatedRegistrationComponent extends DefaultConsolidatedRegistrationService with LoggingComponent

trait ConsolidatedRegistrationServiceContract {
  def consolidatedRegistrationService: ConsolidatedRegistrationService
}

trait ConsolidatedRegistrationService {
  def consolidateRegistrations(): Registrations => ConsolidatedRegistrations

  def addUnregisteredRegistrationsPerJob(): ConsolidatedRegistrationsPerJob => ConsolidatedRegistrationsPerJob

  def consolidateRegistrationsPerJob(): ConsolidatedRegistrations => ConsolidatedRegistrationsPerJob
}
