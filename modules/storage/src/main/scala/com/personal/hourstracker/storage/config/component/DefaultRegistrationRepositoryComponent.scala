package com.personal.hourstracker.storage.config.component

import java.net.URI

import com.personal.hourstracker.config.component.RegistrationRepositoryComponent
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.storage.config.StorageConfiguration
import com.personal.hourstracker.storage.repository.squeryl.SquerylRegistrationRepository
import javax.sql.DataSource
import org.apache.commons.dbcp2.BasicDataSource
import org.slf4j.{ Logger, LoggerFactory }
import org.squeryl.{ Session, SessionFactory }

trait DefaultRegistrationRepositoryComponent extends RegistrationRepositoryComponent {
  this: StorageConfiguration =>

  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[DefaultRegistrationRepositoryComponent])

  private def createDataSource(username: String, password: String, dataSourceUri: URI): Either[String, BasicDataSource] = {
    logger.info("-" * 120)
    logger.info(s"creating DataSource:")
    logger.info(s"\turi = ${dataSourceUri.toString}")
    logger.info(s"\tusername = $username")
    logger.info(s"\tpassword = $password")
    logger.info("-" * 120)

    Right(ConnectionFactory.createDataSource(url = dataSourceUri.toString, username = username, password = password))
  }

  override lazy val registrationRepository: RegistrationRepository = {
    logger.info(
      s"Starting Database session for driver '$driver', connected to '${Storage.Registrations.url}' ('${Storage.Registrations.user}')")
    Class.forName(driver)

    SessionFactory.concreteFactory = DatabaseAdapterFactory
      .retrieveDatabaseAdapterFor(driver)
      .map { databaseAdapter => () =>
        Session.create(dataSource.getConnection(), databaseAdapter)
      }

    new SquerylRegistrationRepository() {
      //      initialize()
    }
  }

  private lazy val driver: String = Storage.Registrations.driver

  private lazy val dataSource: BasicDataSource = createDataSource(
    username = Storage.Registrations.user,
    password = Storage.Registrations.password,
    dataSourceUri = new URI(Storage.Registrations.url)) match {
    case Left(message) =>
      logger.error(s"Could not create DataSource: $message")
      throw new IllegalArgumentException(s"Properties not configured correctly: $message")
    case Right(dataSource) =>
      dataSource
  }

}
