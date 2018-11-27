package com.personal.hourstracker.repository

import scala.concurrent.Future

import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.domain.SearchParameters

trait RegistrationRepository {
  def load()(implicit searchParameters: SearchParameters): Future[Registrations]

  def store(registrations: Registrations): Future[Unit]
}
