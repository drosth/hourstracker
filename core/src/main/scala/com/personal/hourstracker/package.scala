package com.personal

import java.time.format.DateTimeFormatter
import java.time.LocalDate
import java.util.Locale

import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations

package object hourstracker {

  lazy val locale: Locale = new Locale("nl", "NL")

  def dateRangeAsStringOf(registrations: ConsolidatedRegistrations): String = {

    val firstDate = registrations.head.date
    val lastDate = registrations.last.date

    firstDate match {
      case e: LocalDate if e.getMonthValue < lastDate.getMonthValue =>
        s"${firstDate.format(DateTimeFormatter.ofPattern("MMM", locale))}-${
          lastDate
            .format(DateTimeFormatter.ofPattern("MMM-yyyy", locale))
        }"

      case e: LocalDate =>
        firstDate.format(DateTimeFormatter.ofPattern("MMM-yyyy", locale))
    }
  }

}
