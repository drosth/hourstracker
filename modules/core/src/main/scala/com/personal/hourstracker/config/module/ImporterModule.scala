package com.personal.hourstracker.config.module

trait ImporterModule
  extends CSVImporterServiceComponent
  with LoggingComponent {
  this: SystemComponent =>

  override implicit lazy val logger: Logger = LoggerFactory.getLogger("ImporterModule")
}
