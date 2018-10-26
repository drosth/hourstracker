package com.personal.hourstracker.config.component

import com.personal.hourstracker.service.{ ConsolidatedRegistrationService, DefaultConsolidatedRegistrationService }

trait ConsolidatedRegistrationComponent {
  this: LoggingComponent with SystemComponent =>

  def consolidatedRegistrationService: ConsolidatedRegistrationService = new DefaultConsolidatedRegistrationService()
}
