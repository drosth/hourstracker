package com.personal.hourstracker.repository

import java.io.Reader

import scala.concurrent.{ ExecutionContext, Future }

import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.domain.SearchParameters

trait RegistrationRepository {
  def readRegistrationsFrom(reader: Reader)(implicit searchParameters: SearchParameters, executionContext: ExecutionContext): Future[Registrations]
}
