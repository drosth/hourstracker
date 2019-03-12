package com.personal.hourstracker.service

import com.personal.hourstracker.domain.Registration.Registrations

import scala.concurrent.Future

trait ImportService {
  def importRegistrationsFrom(fileName: String): Future[Registrations]
}
