package com.personal.hourstracker.service.presenter

import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations

case class BackOffice(emailAddress: String)

case class Model(
  consultantName: String,
  registrations: ConsolidatedRegistrations,
  totalHours: Double,
  monthName: String,
  backoffice: BackOffice)
