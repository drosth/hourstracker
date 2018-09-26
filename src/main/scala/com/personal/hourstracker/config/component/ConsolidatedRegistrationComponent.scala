package com.personal.hourstracker.config.component

import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.DefaultConsolidatedRegistrationService


trait ConsolidatedRegistrationComponent
    extends DefaultConsolidatedRegistrationService


trait ConsolidatedRegistrationService {
  def consolidatedRegistrationService: ConsolidatedRegistrationService


  trait ConsolidatedRegistrationService {
    def consolidateRegistrations(registrations: Registrations): ConsolidatedRegistrations
  }


}
