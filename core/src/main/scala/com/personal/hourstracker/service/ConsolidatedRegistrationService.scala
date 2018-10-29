package com.personal.hourstracker.service

import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrationsPerJob
import com.personal.hourstracker.domain.Registration.Registrations

trait ConsolidatedRegistrationService {

  def consolidateAndProcessRegistrations(
    futureRegistrations: Registrations)(processConsolidatedRegistrations: ConsolidatedRegistrationsPerJob => Unit): ConsolidatedRegistrationsPerJob
}
