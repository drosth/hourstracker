package com.personal.hourstracker.service.presenter

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import com.personal.hourstracker.domain.ConsolidatedRegistration
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations

case class Model(
  consultantName: String,
  registrations: ConsolidatedRegistrations) {

  private val monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM", new Locale("nl", "NL"))

  val fullMonthNameOf: LocalDate => String = date => date.format(monthFormatter)

  private def month(date: LocalDate): LocalDate = date.withDayOfMonth(1)

  def groupedRegistrations: Map[LocalDate, List[ConsolidatedRegistration]] = registrations.groupBy(registration => month(registration.date))
}
