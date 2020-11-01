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

trait HerokuRegistrationRepositoryComponent extends RegistrationRepositoryComponent {
  this: StorageConfiguration =>

  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[HerokuRegistrationRepositoryComponent])

  private val constructUrlFromUri: URI => String = { uri =>
    logger.info("-" * 120)
    logger.info(s"jdbc:postgresql://${uri.getHost}:${uri.getPort}${uri.getPath}")
    logger.info("-" * 120)
    s"jdbc:postgresql://${uri.getHost}:${uri.getPort}${uri.getPath}"
  }

  private def createDataSource(url: String)(constructUrlFromUri: URI => String): Either[String, BasicDataSource] = {
    createDataSource(new URI(url))(constructUrlFromUri)
  }

  private def createDataSource(uri: URI)(constructUrlFromUri: URI => String): Either[String, BasicDataSource] = {
    for {
      userInfo <- Option(uri.getUserInfo)
        .map(_.split(":"))
        .map(Right.apply)
        .getOrElse(Left(s"No UserInfo defined in: ${uri.toString}"))
    } yield ConnectionFactory.createDataSource(constructUrlFromUri(uri), userInfo(0), userInfo(1))
  }

  private val datasource: BasicDataSource = createDataSource(Storage.Registrations.url)(constructUrlFromUri) match {
    case Left(message) =>
      logger.error(s"Could not create DataSource: $message")
      throw new IllegalArgumentException(s"Properties not configured correctly: $message")
    case Right(dataSource) => dataSource
  }

  override lazy val registrationRepository: RegistrationRepository = new HerokuRegistrationRepository(datasource).registrationRepository

  class HerokuRegistrationRepository(dataSource: DataSource) {
    private val driver = Storage.Registrations.driver

    private def startDatabaseSession(): Unit = {
      logger.debug(
        s"Starting Database session for driver '$driver', connected to '${Storage.Registrations.url}' ('${Storage.Registrations.user}')")
      Class.forName(driver)

      SessionFactory.concreteFactory = DatabaseAdapterFactory
        .retrieveDatabaseAdapterFor(driver)
        .map(databaseAdapter => () => Session.create(dataSource.getConnection(), databaseAdapter))
    }

    val registrationRepository: RegistrationRepository = {
      new SquerylRegistrationRepository() {
        startDatabaseSession()
        initialize()
      }
    }
  }

}
