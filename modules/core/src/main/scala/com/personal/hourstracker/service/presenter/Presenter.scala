package com.personal.hourstracker.service.presenter

import java.io.{File, PrintWriter, Writer}

trait Presenter {
  this: Configuration =>

  def renderRegistrationsPerSingleJob(job: String, registrations: ConsolidatedRegistrations): File

  def renderConsolidatedRegistrations: ConsolidatedRegistrations => String

  def renderRegistrationsPerJobs: ConsolidatedRegistrationsPerJob => Seq[File] = consolidatedRegistrationsPerJob => {
    consolidatedRegistrationsPerJob.map {
      case (job, consolidatedRegistrations) => renderRegistrationsPerSingleJob(job, consolidatedRegistrations)
    }.toSeq
  }

  def fileName(job: String, consolidatedRegistrations: ConsolidatedRegistrations, extension: String): String =
    s"${Application.exportTo}/[Timesheet] - $job - ${dateRangeAsStringOf(consolidatedRegistrations)}$extension"

  protected[presenter] def withWriterTo(fileName: String)(fn: Writer => Unit): Unit = {
    val writer: Writer = new PrintWriter(new File(fileName))
    try {
      fn(writer)
    } catch {
      case e: Exception =>
    } finally {
      writer.close()
    }
  }
}
