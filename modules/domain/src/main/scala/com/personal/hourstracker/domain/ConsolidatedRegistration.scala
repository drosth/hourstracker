package com.personal.hourstracker.domain

import java.time.LocalDate

import com.personal.common.CommonJsonSupport
import spray.json.JsonFormat

final case class ConsolidatedRegistration(date: LocalDate, job: String, duration: Option[Double], comment: Option[String])

object ConsolidatedRegistration {
  type ConsolidatedRegistrations = List[ConsolidatedRegistration]
  type ConsolidatedRegistrationsPerJob = Map[String, ConsolidatedRegistrations]

  trait Protocol extends CommonJsonSupport {
    implicit lazy val consolidatedRegistrationFormat: JsonFormat[ConsolidatedRegistration] = jsonFormat4(ConsolidatedRegistration.apply)
  }

  object DateTimeOrdering extends Ordering[ConsolidatedRegistration] {

    def compare(a: ConsolidatedRegistration, b: ConsolidatedRegistration): Int =
      a.date.compareTo(b.date) match {
        case 0 => a.job.compareTo(b.job)
        case x => x
      }
  }
}

