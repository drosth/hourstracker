package com.personal.hourstracker.service

trait ConsolidatedRegistrationService {

  def consolidateAndProcessRegistrations[T](registrations: Registrations)(processConsolidatedRegistrations: ConsolidatedRegistrationsPerJob => T): T
}
