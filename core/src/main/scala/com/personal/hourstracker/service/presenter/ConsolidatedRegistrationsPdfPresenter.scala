package com.personal.hourstracker.service.presenter

import java.io.File

import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.domain.ConsolidatedRegistration.{ ConsolidatedRegistrations, ConsolidatedRegistrationsPerJob }
import com.personal.hourstracker.service.presenter.config.component.{ HtmlPresenterComponent, PdfPresenter }
import io.github.cloudify.scala.spdf.{ Pdf, PdfConfig, Portrait }

trait ConsolidatedRegistrationsPdfPresenter extends PdfPresenter[ConsolidatedRegistrationsPerJob] {
  this: HtmlPresenterComponent with LoggingComponent =>

  val pdfPresenter: Presenter[ConsolidatedRegistrationsPerJob] = new ConsolidatedRegistrationsPdfPresenter()

  class ConsolidatedRegistrationsPdfPresenter extends Presenter[ConsolidatedRegistrationsPerJob] with Configuration {

    val executablePath = "/usr/local/bin/wkhtmltopdf"

    lazy val pdf = Pdf(executablePath, new PdfConfig {
      orientation := Portrait
      pageSize := "A4"
      marginTop := "0.5cm"
      marginBottom := "0.5cm"
      marginLeft := "0.5cm"
      marginRight := "0.5cm"
    })

    override val renderRegistrationsPerJobs: ConsolidatedRegistrationsPerJob => Seq[File] = registrationsPerJob => {
      logger.info(s"Rendering to HTML")
      htmlPresenter.renderRegistrationsPerJobs(registrationsPerJob)
      logger.info(s"Done rendering to HTML")

      registrationsPerJob.map {
        case (job, registrations) => renderRegistrationsPerSingleJob(job, registrations)
      }.toSeq
    }

    override def renderRegistrationsPerSingleJob(job: String, registrations: ConsolidatedRegistrations): File = {
      logger.info(s"Rendering #${registrations.size} consolidated registrations to PDF")
      logger.debug(registrations.toString())

      val outputFile = new File(fileName(job, registrations, ".pdf"))

      val exists = outputFile.exists()
      logger.debug(s"'${outputFile.getAbsolutePath}' exists? (${if (exists) "yes" else "NO"})")

      val makeWritable = outputFile.setWritable(true)
      logger.debug(s"'${outputFile.getAbsolutePath}' could make writable? (${if (makeWritable) "yes" else "NO"})")

      val canWrite = outputFile.canWrite()
      logger.debug(s"'${outputFile.getAbsolutePath}' can write to file? (${if (canWrite) "yes" else "NO"})")

      val rendered: String = htmlPresenter.renderConsolidatedRegistrations(registrations)

      logger.debug(s"writing to .PDF")
      pdf.run(rendered, outputFile)
      outputFile
    }
  }
}
