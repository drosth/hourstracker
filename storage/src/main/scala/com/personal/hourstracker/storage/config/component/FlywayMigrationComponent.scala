package com.personal.hourstracker.storage.config.component

import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.storage.config.StorageConfiguration
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateResult

trait FlywayMigrationComponent extends StorageConfiguration with LoggingComponent {

  def runMigrations(): MigrateResult = {
    logger.info("=" * 120)
    logger.info(s" Using Datasource '${Storage.Registrations.user} @ ${Storage.Registrations.url}' to run migrations ")

    val flyway = Flyway.configure.dataSource(Storage.Registrations.url, Storage.Registrations.user, Storage.Registrations.password).baselineOnMigrate(true).load
    flyway.migrate
  }
}
