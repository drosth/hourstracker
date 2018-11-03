package com.personal.hourstracker.service.presenter

import java.io.File

import com.personal.hourstracker._
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations

trait Presenter[T] {
  def renderRegistrations: T => Seq[File]

  def fileName(job: String, consolidatedRegistrations: ConsolidatedRegistrations, extension: String): String =
    s"target/[Timesheet] - $job - ${dateRangeAsStringOf(consolidatedRegistrations)}.$extension"
}
