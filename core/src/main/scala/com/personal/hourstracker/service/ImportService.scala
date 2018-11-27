package com.personal.hourstracker.service

import java.io.Reader

import scala.concurrent.{ExecutionContext, Future}

import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.domain.SearchParameters


trait ImportService {
  def importRegistrationsFrom(reader: Reader)(implicit searchParameters: SearchParameters, executionContext: ExecutionContext): Future[Registrations]
}
