package com.personal.hourstracker.storage.config.module

trait StorageModule extends SelectRegistrationRepositoryComponent with StorageConfiguration with LoggingComponent {
  override implicit lazy val logger: Logger = LoggerFactory.getLogger("StorageModule")

  logger.info(s"Using url   : ${Storage.Registrations.url}")
  logger.info(s"Using driver: ${Storage.Registrations.driver}")
}
