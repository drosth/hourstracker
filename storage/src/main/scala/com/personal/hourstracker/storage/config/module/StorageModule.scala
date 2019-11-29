package com.personal.hourstracker.storage.config.module

import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.storage.config.StorageConfiguration
import com.personal.hourstracker.storage.config.component.DefaultRegistrationRepositoryComponent
import org.slf4j.{ Logger, LoggerFactory }

trait StorageModule extends DefaultRegistrationRepositoryComponent with StorageConfiguration with LoggingComponent {
  override implicit lazy val logger: Logger = LoggerFactory.getLogger("StorageModule")

  logger.info(s"Using url   : ${Storage.Registrations.url}")
  logger.info(s"Using driver: ${Storage.Registrations.driver}")
}
