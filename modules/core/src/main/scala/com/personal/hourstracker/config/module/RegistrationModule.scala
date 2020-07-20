package com.personal.hourstracker.config.module

import java.util.Locale

trait RegistrationModule
  extends RegistrationComponent
  with RegistrationRepositoryComponent
  with ConsolidatedRegistrationComponent
  with FacturationComponent
  with LoggingComponent {

  this: SystemComponent with ImporterServiceComponent =>

  override implicit lazy val logger: Logger = LoggerFactory.getLogger("RegistrationModule")

  override implicit lazy val locale: Locale = new Locale("nl", "NL")
}
