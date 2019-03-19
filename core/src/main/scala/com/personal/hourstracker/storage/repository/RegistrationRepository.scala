package com.personal.hourstracker.storage.repository

import scala.concurrent.Future
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.domain.{ Registration, SearchParameters }

trait RegistrationRepository {
  @Deprecated
  def load()(implicit searchParameters: SearchParameters): Future[Registrations]

  def findById(id: Registration.RegistrationID): Future[Option[Registration]]

  def findAll(): Future[Either[String, Registrations]]

  def save(registration: Registration): Future[Either[String, Registration.RegistrationID]]
}

trait RegistrationRepositoryComponent {
  def registrationRepository: RegistrationRepository
}
