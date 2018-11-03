package com.personal.hourstracker.service.presenter.impl

import java.io.File

import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrationsPerJob
import com.personal.hourstracker.service.presenter.{ HtmlPresenterComponent, PdfPresenter, Presenter }
import io.github.cloudify.scala.spdf.{ Pdf, PdfConfig, Portrait }

trait ConsolidatedRegistrationsPdfPresenter extends PdfPresenter[ConsolidatedRegistrationsPerJob] {
  this: HtmlPresenterComponent with LoggingComponent =>

  val pdfPresenter: Presenter[ConsolidatedRegistrationsPerJob] = new ConsolidatedRegistrationsPdfPresenter()

  class ConsolidatedRegistrationsPdfPresenter extends Presenter[ConsolidatedRegistrationsPerJob] {

    val executablePath = "/usr/local/bin/wkhtmltopdf"

    lazy val pdf = Pdf(executablePath, new PdfConfig {
      orientation := Portrait
      pageSize := "A4"
      marginTop := "0.5cm"
      marginBottom := "0.5cm"
      marginLeft := "0.5cm"
      marginRight := "0.5cm"
    })

    override def renderRegistrations: ConsolidatedRegistrationsPerJob => Seq[File] = registrationsPerJob => {
      registrationsPerJob.map {
        case (job, registrations) =>
          logger.info(s"Rendering #${registrations.size} consolidated registrations to PDF")

          val outputFile = new File(fileName(job, registrations, ".pdf"))
          pdf.run(htmlPresenter.renderConsolidatedRegistrations(registrations), outputFile)
          outputFile
      }.toSeq
    }
  }
}
