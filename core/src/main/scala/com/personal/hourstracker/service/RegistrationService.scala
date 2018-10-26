package com.personal.hourstracker.service

import scala.concurrent.Future

import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.domain.SearchParameters

trait RegistrationService {
  def importRegistrationsFrom(fileName: String)(implicit searchParameters: SearchParameters): Future[Registrations]
}
