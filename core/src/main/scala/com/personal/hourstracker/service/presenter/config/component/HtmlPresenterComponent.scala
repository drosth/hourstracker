package com.personal.hourstracker.service.presenter.config.component

import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.service.presenter.Presenter

trait HtmlPresenterComponent {
  this: Configuration with LoggingComponent =>

  def htmlPresenter: HtmlPresenter
}

trait HtmlPresenter extends Presenter {
  this: Configuration =>
}
