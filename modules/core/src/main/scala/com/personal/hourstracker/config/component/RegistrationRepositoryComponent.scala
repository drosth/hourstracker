package com.personal.hourstracker.config.component
import java.time.LocalDateTime

import akka.NotUsed
import akka.stream.scaladsl.Source
import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.service.RegistrationService.RegistrationRequest

trait RegistrationRepositoryComponent {
  def registrationRepository: RegistrationRepository
}

trait NoopRegistrationRepositoryComponent extends RegistrationRepositoryComponent {
  lazy val registrationRepository: RegistrationRepository = new NoopRegistrationRepository()

  class NoopRegistrationRepository extends RegistrationRepository {
    override def findAll(): Source[Registration, NotUsed]                                                                    = Source.empty
    override def findByRequest(request: RegistrationRequest): Source[Registration, NotUsed]                                  = Source.empty
    override def save(entity: Registration): Either[String, Registration]                                                    = Left("Not implemented")
    override def findById(id: Long): Option[Registration]                                                                    = None
    override def findBy(job: String, clockedIn: Option[LocalDateTime], clockedOut: Option[LocalDateTime]): Seq[Registration] = Seq.empty
  }
}
