package com.personal.hourstracker.service

trait FacturationService {
  def splitAllRegistrationsForFacturation: Registrations => Registrations = registrations =>
    registrations.flatMap(splitRegistrationForFacturation(_))

  val splitRegistrationForFacturation: Registration => Registrations

  def splitOnTags(registration: Registration): Registrations
}
