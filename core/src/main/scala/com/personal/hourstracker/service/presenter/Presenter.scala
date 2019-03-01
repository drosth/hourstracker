package com.personal.hourstracker.service.presenter

import java.io.File

import com.personal.hourstracker._
import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations

trait Presenter[T] {
  this: Configuration =>

  val renderRegistrationsPerJob: T => Seq[File]

  def fileName(job: String, consolidatedRegistrations: ConsolidatedRegistrations, extension: String): String =
    s"${Application.exportTo}/[Timesheet] - $job - ${dateRangeAsStringOf(consolidatedRegistrations)}$extension"
}
