package com.personal.hourstracker.service

import scala.concurrent.Future

trait ImporterService {
  def importRegistrationsFrom(fileName: String): Future[Either[String, Registrations]]
}
