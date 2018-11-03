package com.personal.hourstracker.service.presenter

import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations

case class Model(
  consultantName: String,
  registrations: ConsolidatedRegistrations,
  totalHours: Double,
  monthName: String)
