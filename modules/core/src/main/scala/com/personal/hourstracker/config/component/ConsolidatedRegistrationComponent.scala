package com.personal.hourstracker.config.component

trait ConsolidatedRegistrationComponent {
  this: LoggingComponent with SystemComponent =>

  def consolidatedRegistrationService: ConsolidatedRegistrationService = new DefaultConsolidatedRegistrationService()
}
