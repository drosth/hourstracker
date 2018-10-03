package com.personal.hourstracker.config

import com.personal.hourstracker.config.component.{
  ConsolidatedRegistrationComponent,
  FacturationComponent,
  RegistrationComponent
}
import com.personal.hourstracker.service.presenter.{
  HtmlPresenterComponent,
  JsonPresenterComponent,
  PdfPresenterComponent
}

trait ApplicationModule
    extends RegistrationComponent
    with ConsolidatedRegistrationComponent
    with FacturationComponent
    with HtmlPresenterComponent
    with PdfPresenterComponent
    with JsonPresenterComponent
