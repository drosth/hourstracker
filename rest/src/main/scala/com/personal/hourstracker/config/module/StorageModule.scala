package com.personal.hourstracker.config.module

import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.storage.config.StorageConfiguration
import com.personal.hourstracker.storage.config.component.SquerylRegistrationRepositoryComponent
import org.slf4j.{ Logger, LoggerFactory }

trait StorageModule extends SquerylRegistrationRepositoryComponent with StorageConfiguration with LoggingComponent {
  override implicit lazy val logger: Logger = LoggerFactory.getLogger("StorageModule")
}
