package com.personal.hourstracker.service.presenter.config.module

trait PresenterModule
  extends DefaultJsonPresenterComponent
    with ConsolidatedRegistrationsHtmlPresenterComponent
    with ConsolidatedRegistrationsPdfPresenterComponent {
  this: LoggingComponent with Configuration =>
}
