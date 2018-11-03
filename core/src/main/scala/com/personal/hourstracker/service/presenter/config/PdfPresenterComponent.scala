package com.personal.hourstracker.service.presenter.config

import com.personal.hourstracker.service.presenter.{ ConsolidatedRegistrationsPdfPresenter, Presenter }

trait PdfPresenterComponent extends ConsolidatedRegistrationsPdfPresenter {
  this: HtmlPresenterComponent =>

}

trait PdfPresenter[T] {
  this: HtmlPresenterComponent =>

  val pdfPresenter: Presenter[T]
}

