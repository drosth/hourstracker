package com.personal.hourstracker.storage.config.component
import com.personal.hourstracker.config.component.RegistrationRepositoryComponent
import com.personal.hourstracker.storage.config.StorageConfiguration
import com.personal.hourstracker.storage.repository.RegistrationRepository
import com.personal.hourstracker.storage.repository.squeryl.SquerylRegistrationRepository
import org.slf4j.LoggerFactory
import org.squeryl.adapters.{ H2Adapter, MySQLAdapter }
import org.squeryl.internals.DatabaseAdapter
import org.squeryl.{ Session, SessionFactory }

trait SquerylRegistrationRepositoryComponent extends RegistrationRepositoryComponent {
  this: StorageConfiguration =>

  override lazy val registrationRepository: RegistrationRepository = new SquerylRegistrationRepository()

  startDatabaseSession()

  private def startDatabaseSession(): Unit = {
    Class.forName(Storage.Registrations.driver)
    SessionFactory.concreteFactory = DatabaseAdapterFactory
      .create(Storage.Registrations.driver)
      .map(
        databaseAdapter =>
          () =>
            Session.create(
              java.sql.DriverManager.getConnection(Storage.Registrations.url, Storage.Registrations.user, Storage.Registrations.password),
              databaseAdapter))
  }

  object DatabaseAdapterFactory {
    private val logger = LoggerFactory.getLogger(this.getClass)

    def create(driver: String): Option[DatabaseAdapter] = driver match {
      case "org.h2.Driver" => Some(new H2Adapter)
      case "com.mysql.jdbc.Driver" => Some(new MySQLAdapter)
      case _ =>
        logger.warn(s"==== Could not create DatabaseAdapter for driver: $driver")
        None
    }
  }
}
