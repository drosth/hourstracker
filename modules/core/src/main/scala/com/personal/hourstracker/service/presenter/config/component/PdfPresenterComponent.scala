package com.personal.hourstracker.service.presenter.config.component

trait PdfPresenterComponent {
  this: HtmlPresenterComponent =>

  def pdfPresenter: PdfPresenter
}

trait PdfPresenter extends Presenter {
  this: Configuration =>
}

