package com.personal.hourstracker.service

import scala.concurrent.Future

import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.domain.SearchParameters

trait RegistrationService {
  def importRegistrationsFrom(fileName: String): Future[Either[String, Registrations]]
  def loadRegistrations()(implicit searchParameters: SearchParameters): Future[Registrations]
}
