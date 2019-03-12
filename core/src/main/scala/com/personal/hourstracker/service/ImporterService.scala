package com.personal.hourstracker.service

import com.personal.hourstracker.domain.Registration.Registrations

import scala.concurrent.Future

trait ImporterService {
  def importRegistrationsFrom(fileName: String): Future[Either[String, Registrations]]
}
