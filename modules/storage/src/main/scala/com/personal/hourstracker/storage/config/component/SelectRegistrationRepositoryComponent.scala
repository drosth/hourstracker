package com.personal.hourstracker.storage.config.component

import java.net.URI

import javax.sql.DataSource

trait SelectRegistrationRepositoryComponent extends RegistrationRepositoryComponent {
  this: StorageConfiguration =>

  override lazy val registrationRepository: RegistrationRepository = Application.environment match {
    case Heroku => HerokuRegistrationRepository().registrationRepository
    case _ => DefaultRegistrationRepository().registrationRepository
  }

  object HerokuRegistrationRepository {
    private lazy val logger: Logger = LoggerFactory.getLogger(classOf[HerokuRegistrationRepository])

    logger.info(
      s"""
 ************************************************************************************************
 Using HEROKU
 - ${Storage.Registrations.url}
 - with ${Storage.Registrations.driver}
 ************************************************************************************************
        """)
    private val constructUrlFromUri: URI => String = uri => s"jdbc:postgresql://${uri.getHost}:${uri.getPort}${uri.getPath}"

    private val datasource: BasicDataSource = ConnectionFactory.createDataSource(Storage.Registrations.url)(constructUrlFromUri)

    def apply(): HerokuRegistrationRepository =
      new HerokuRegistrationRepository(datasource)
  }

  class HerokuRegistrationRepository(dataSource: DataSource) {

    import HerokuRegistrationRepository._

    private val driver = Storage.Registrations.driver

    private def startDatabaseSession(): Unit = {
      logger.info(
        s"Starting Database session for driver '$driver', connected to '${Storage.Registrations.url}' ('${Storage.Registrations.user}')"
      )
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

  object DefaultRegistrationRepository {
    private lazy val logger: Logger = LoggerFactory.getLogger(classOf[DefaultRegistrationRepository])

    logger.info(
      s"""
************************************************************************************************
Using DEFAULT
- ${Storage.Registrations.user} @ ${Storage.Registrations.url}
- with ${Storage.Registrations.driver}
************************************************************************************************
        """)

    private val datasource: BasicDataSource =
      ConnectionFactory.createDataSource(Storage.Registrations.url, Storage.Registrations.user, Storage.Registrations.password)

    def apply(): DefaultRegistrationRepository = new DefaultRegistrationRepository(datasource)
  }

  class DefaultRegistrationRepository(dataSource: DataSource) {

    import DefaultRegistrationRepository._

    private val driver = Storage.Registrations.driver

    private def startDatabaseSession(): Unit = {
      logger.debug(
        s"Starting Database session for driver '$driver', connected to '${Storage.Registrations.url}' ('${Storage.Registrations.user}')"
      )
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
