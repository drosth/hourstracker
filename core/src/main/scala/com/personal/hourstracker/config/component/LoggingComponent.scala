package com.personal.hourstracker.config.component

import org.slf4j.Logger

trait LoggingComponent {
  implicit def logger: Logger
}
