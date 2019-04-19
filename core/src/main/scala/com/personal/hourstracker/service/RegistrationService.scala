package com.personal.hourstracker.service

import java.time.LocalDate

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations

import scala.concurrent.Future

object RegistrationService {
  sealed trait RegistrationRequest

  case class SelectByYear(year: Int) extends RegistrationRequest
  case class SelectByYearAndMonth(year: Int, month: Int) extends RegistrationRequest
}

trait RegistrationService {
  import RegistrationService._

  def importRegistrationsFrom(fileName: String): Future[Either[String, Int]]

  def fetchRegistrations(): Source[Registration, NotUsed]

  def fetchRegistrations(request: RegistrationRequest): Source[Registration, NotUsed]

  def storeRegistrations(registrations: Registrations): Future[Unit]
}
