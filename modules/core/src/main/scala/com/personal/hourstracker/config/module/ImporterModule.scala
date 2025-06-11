package com.personal.hourstracker.config.module

import com.personal.hourstracker.config.component._
import org.slf4j.{Logger, LoggerFactory}

trait ImporterModule
  extends CSVImporterServiceComponent
    with LoggingComponent {
  this: SystemComponent =>

  override implicit lazy val logger: Logger = LoggerFactory.getLogger("ImporterModule")
}
