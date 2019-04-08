package com.personal.hourstracker.service

import com.personal.hourstracker.domain.Registration.Registrations

import scala.concurrent.Future

trait RegistrationService {
  def importRegistrationsFrom(fileName: String): Future[Either[String, Int]]
  def fetchRegistrations(): Future[Registrations]
  def storeRegistrations(registrations: Registrations): Future[Unit]
}
