package com.personal.hourstracker.service

import com.personal.hourstracker.domain.Registration.Registrations

import scala.concurrent.Future

trait RegistrationService {
  def importRegistrationsFrom(fileName: String): Future[Either[String, Registrations]]
  def storeRegistrations(registrations: Registrations): Future[Either[String, Seq[Long]]]
}
