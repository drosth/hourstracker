package com.personal.hourstracker.service.presenter.config.module

import com.personal.hourstracker.service.presenter.config.component.{HtmlPresenterComponent, JsonPresenterComponent, PdfPresenterComponent}

trait PresenterModule extends JsonPresenterComponent with HtmlPresenterComponent with PdfPresenterComponent
