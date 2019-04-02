package com.personal.hourstracker.config.component
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.repository.RegistrationRepository

trait RegistrationRepositoryComponent {
  def registrationRepository: RegistrationRepository
}

trait NoopRegistrationRepositoryComponent extends RegistrationRepositoryComponent {
  lazy val registrationRepository: RegistrationRepository = new NoopRegistrationRepository()

  class NoopRegistrationRepository extends RegistrationRepository {
    override def findAll(): List[Registration] = List.empty
    override def save(entity: Registration): Either[String, Long] = Right(0L)
    override def findById(id: Long): Option[Registration] = None
  }
}
