package com.personal.hourstracker.service

import scala.concurrent.Future

object RegistrationService {

  sealed trait RegistrationRequest

  case class SelectByYear(year: Int) extends RegistrationRequest

  case class SelectByYearAndMonth(year: Int, month: Int) extends RegistrationRequest

}

trait RegistrationService {

  import RegistrationService._

  def importRegistrationsFrom(fileName: String): Future[Either[String, Int]]

  def importRegistrationsFromSource(fileName: String): Source[Either[String, Registrations], NotUsed]

  def fetchRegistrations(): Source[Registration, NotUsed]

  def fetchRegistrations(request: RegistrationRequest): Source[Registration, NotUsed]

  def consolidateRegistrations[T](registrations: Source[Registration, NotUsed])(
    processConsolidatedRegistrations: ConsolidatedRegistrationsPerJob => T): Source[T, NotUsed]
}
