package com.personal.hourstracker.service.presenter.config.component

import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.service.presenter.Presenter

trait PdfPresenterComponent {
  this: HtmlPresenterComponent =>

  def pdfPresenter: PdfPresenter
}

trait PdfPresenter extends Presenter {
  this: Configuration =>
}
