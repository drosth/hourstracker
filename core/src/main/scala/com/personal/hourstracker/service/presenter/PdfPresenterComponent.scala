package com.personal.hourstracker.service.presenter

import java.io.File

import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations
import io.github.cloudify.scala.spdf._

trait PdfPresenterComponent extends ConsolidatedRegistrationsPdfPresenter {
  this: HtmlPresenterComponent =>

}

trait PdfPresenter[T] {
  this: HtmlPresenterComponent =>

  val pdfPresenter: PdfPresenter[T]

  trait PdfPresenter[T] {
    def renderRegistrationsTo(registrations: T, fileName: String)
  }

}

trait ConsolidatedRegistrationsPdfPresenter extends PdfPresenter[ConsolidatedRegistrations] {
  this: HtmlPresenterComponent with LoggingComponent =>

  val pdfPresenter: PdfPresenter[ConsolidatedRegistrations] =
    new ConsolidatedRegistrationsPdfPresenter()

  class ConsolidatedRegistrationsPdfPresenter extends PdfPresenter[ConsolidatedRegistrations] {

    val executablePath = "/usr/local/bin/wkhtmltopdf"

    lazy val pdf = Pdf(executablePath, new PdfConfig {
      orientation := Portrait
      pageSize := "A4"
      marginTop := "0.5cm"
      marginBottom := "0.5cm"
      marginLeft := "0.5cm"
      marginRight := "0.5cm"
    })

    override def renderRegistrationsTo(registrations: ConsolidatedRegistrations, fileName: String): Unit = {
      logger.info(s"Rendering #${registrations.size} consolidated registrations to PDF: '$fileName'")

      pdf.run(htmlPresenter.renderRegistrations(registrations), new File(fileName))
    }
  }

}
