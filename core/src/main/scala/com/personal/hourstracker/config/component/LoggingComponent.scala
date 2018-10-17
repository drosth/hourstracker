package com.personal.hourstracker.config.component

import org.slf4j.{ Logger, LoggerFactory }

trait LoggingComponent {
  implicit val logger: Logger = LoggerFactory.getLogger("com.personal.hourstracker")
}
