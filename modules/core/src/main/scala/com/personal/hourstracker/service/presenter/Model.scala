package com.personal.hourstracker.service.presenter

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import com.personal.common.CommonJsonSupport
import com.personal.hourstracker.domain.ConsolidatedRegistration
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations
import com.personal.hourstracker.service.presenter.Model.{ month, monthFormatter }
import spray.json.RootJsonFormat

object Model {
  lazy val monthFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM", new Locale("nl", "NL"))

  private def month(date: LocalDate): LocalDate = date.withDayOfMonth(1)

  trait Protocol extends CommonJsonSupport with ConsolidatedRegistration.Protocol {
    implicit lazy val registrationFormat: RootJsonFormat[Model] = jsonFormat2(Model.apply)
  }

}

case class Model(consultantName: String, registrations: ConsolidatedRegistrations) {

  def groupedRegistrations: Map[LocalDate, List[ConsolidatedRegistration]] = registrations.groupBy(registration => month(registration.date))

  def fullMonthNameOf: LocalDate => String = date => date.format(monthFormatter)
}
