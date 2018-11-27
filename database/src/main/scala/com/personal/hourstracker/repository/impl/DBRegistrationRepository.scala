package com.personal.hourstracker.repository.impl

import scala.concurrent.Future

import com.personal.hourstracker.config.component.DatabaseSession
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.domain.SearchParameters
import com.personal.hourstracker.repository.RegistrationRepository
import org.slf4j.LoggerFactory

class DBRegistrationRepository(databaseSession: DatabaseSession) extends RegistrationRepository {
  private lazy val logger = LoggerFactory.getLogger(classOf[DBRegistrationRepository])

  override def load()(implicit searchParameters: SearchParameters): Future[Registrations] = {
    logger.info(s"load :: ")
    Future.successful(List())
  }

  override def store(registrations: Registrations): Future[Unit] = {
    logger.info(s"store :: ${registrations.size}")
    Future.successful(())
  }
}
