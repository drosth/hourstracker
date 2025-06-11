package com.personal.hourstracker.service.presenter.config.module

import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.config.component.LoggingComponent
import com.personal.hourstracker.service.presenter.config.component.{
  ConsolidatedRegistrationsHtmlPresenterComponent,
  ConsolidatedRegistrationsPdfPresenterComponent,
  DefaultJsonPresenterComponent
}

trait PresenterModule
  extends DefaultJsonPresenterComponent
    with ConsolidatedRegistrationsHtmlPresenterComponent
    with ConsolidatedRegistrationsPdfPresenterComponent {
  this: LoggingComponent with Configuration =>
}
