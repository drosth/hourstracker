package com.personal.hourstracker.storage.config.component
import com.personal.hourstracker.config.component.RegistrationRepositoryComponent
import com.personal.hourstracker.repository.RegistrationRepository
import com.personal.hourstracker.storage.config.StorageConfiguration
import com.personal.hourstracker.storage.repository.squeryl.SquerylRegistrationRepository
import org.slf4j.{ Logger, LoggerFactory }
import org.squeryl.adapters.{ H2Adapter, MySQLAdapter }
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{ Session, SessionFactory }

trait SquerylRegistrationRepositoryComponent extends RegistrationRepositoryComponent {
  this: StorageConfiguration =>

  private lazy val logger: Logger = LoggerFactory.getLogger(classOf[SquerylRegistrationRepositoryComponent])

  override lazy val registrationRepository: RegistrationRepository = new SquerylRegistrationRepository() {
    startDatabaseSession()
    initialize()
  }

  def startDatabaseSession(): Unit = {
    val driver = Storage.Registrations.driver

    logger.debug(s"Creating DatabaseAdapter session for driver '$driver', connected to '${Storage.Registrations.url}' ('${Storage.Registrations.user}')")
    Class.forName(driver)

    SessionFactory.concreteFactory = DatabaseAdapterFactory
      .create(driver)
      .map(
        databaseAdapter =>
          () => {
            Session.create(
              java.sql.DriverManager.getConnection(Storage.Registrations.url, Storage.Registrations.user, Storage.Registrations.password),
              databaseAdapter)
          })
  }

  object DatabaseAdapterFactory {

    def create(driver: String): Option[DatabaseAdapter] = driver match {
      case "org.h2.Driver" =>
        Some(new H2Adapter)
      case "com.mysql.cj.jdbc.Driver" =>
        Some(new MySQLAdapter)
      case _ =>
        logger.error(s"==== Could not create DatabaseAdapter for driver: $driver")
        None
    }
  }
}
