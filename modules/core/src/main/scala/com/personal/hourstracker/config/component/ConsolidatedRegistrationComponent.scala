package com.personal.hourstracker.config.component

import com.personal.hourstracker.service.ConsolidatedRegistrationService
import com.personal.hourstracker.service.impl.DefaultConsolidatedRegistrationService

trait ConsolidatedRegistrationComponent {
  this: LoggingComponent with SystemComponent =>

  def consolidatedRegistrationService: ConsolidatedRegistrationService = new DefaultConsolidatedRegistrationService()
}
