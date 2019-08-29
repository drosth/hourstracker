package com.personal.hourstracker.config.module

import java.util.Locale

import com.personal.hourstracker.config.component._
import org.slf4j.{ Logger, LoggerFactory }

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
