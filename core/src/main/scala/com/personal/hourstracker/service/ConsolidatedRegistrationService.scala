package com.personal.hourstracker.service

import com.personal.hourstracker.domain.ConsolidatedRegistration.{ ConsolidatedRegistrations, ConsolidatedRegistrationsPerJob }
import com.personal.hourstracker.domain.Registration.Registrations

trait ConsolidatedRegistrationService {
  def consolidateRegistrations(): Registrations => ConsolidatedRegistrations

  def addUnregisteredRegistrationsPerJob(): ConsolidatedRegistrationsPerJob => ConsolidatedRegistrationsPerJob

  def consolidateRegistrationsPerJob(): ConsolidatedRegistrations => ConsolidatedRegistrationsPerJob
}
