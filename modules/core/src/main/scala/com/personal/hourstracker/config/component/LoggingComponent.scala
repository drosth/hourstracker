package com.personal.hourstracker.config.component

trait LoggingComponent {
  implicit def logger: Logger
}
