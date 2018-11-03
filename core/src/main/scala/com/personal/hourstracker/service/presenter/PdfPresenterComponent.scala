package com.personal.hourstracker.service.presenter

import com.personal.hourstracker.service.presenter.impl.ConsolidatedRegistrationsPdfPresenter

trait PdfPresenterComponent extends ConsolidatedRegistrationsPdfPresenter {
  this: HtmlPresenterComponent =>

}

trait PdfPresenter[T] {
  this: HtmlPresenterComponent =>

  val pdfPresenter: Presenter[T]
}

