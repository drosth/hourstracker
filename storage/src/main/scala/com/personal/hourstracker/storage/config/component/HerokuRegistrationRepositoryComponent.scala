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

  private val constructUrlFromUri: URI => String = uri => s"jdbc:postgresql://${uri.getHost}:${uri.getPort}${uri.getPath}"

  private val datasource: BasicDataSource = ConnectionFactory.createDataSource(Storage.Registrations.url)(constructUrlFromUri)

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