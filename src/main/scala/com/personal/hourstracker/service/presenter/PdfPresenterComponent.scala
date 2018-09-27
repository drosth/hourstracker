package com.personal.hourstracker.service.presenter

import java.io.File

import com.personal.hourstracker.domain.ConsolidatedRegistration.ConsolidatedRegistrations
import io.github.cloudify.scala.spdf._


trait PdfPresenterComponent
    extends ConsolidatedRegistrationsPdfPresenter {
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
  this: HtmlPresenterComponent =>

  val pdfPresenter: PdfPresenter[ConsolidatedRegistrations] = new ConsolidatedRegistrationsPdfPresenter()


  class ConsolidatedRegistrationsPdfPresenter extends PdfPresenter[ConsolidatedRegistrations] {
    lazy val pdf = Pdf(new PdfConfig {
      orientation := Portrait
      pageSize := "A4"
      marginTop := "0.5cm"
      marginBottom := "0.5cm"
      marginLeft := "0.5cm"
      marginRight := "0.5cm"
    })

    override def renderRegistrationsTo(registrations: ConsolidatedRegistrations, fileName: String): Unit = {

      val page = htmlPresenter.renderRegistrations(registrations)

      pdf.run(page, new File(fileName))
    }
  }

}
