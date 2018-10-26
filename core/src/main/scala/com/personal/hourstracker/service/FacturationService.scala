package com.personal.hourstracker.service

import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations

trait FacturationService {
  def splitAllRegistrationsForFacturation: Registrations => Registrations = registrations => registrations.flatMap(splitRegistrationForFacturation(_))

  val splitRegistrationForFacturation: Registration => Registrations
}
