package com.personal.hourstracker.service

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations

import scala.concurrent.Future

trait ImporterService {
  def importRegistrationsFrom(fileName: String): Future[Either[String, Registrations]]
  def importRegistrationsFromSource(fileName: String): Either[String, Source[Registration, NotUsed]]
}
