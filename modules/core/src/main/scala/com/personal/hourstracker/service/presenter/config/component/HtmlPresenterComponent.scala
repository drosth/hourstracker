package com.personal.hourstracker.service.presenter.config.component

trait HtmlPresenterComponent {
  this: Configuration with LoggingComponent =>

  def htmlPresenter: HtmlPresenter
}

trait HtmlPresenter extends Presenter {
  this: Configuration =>
}
