package com.personal.hourstracker.config.component

import java.util.Locale

import com.personal.hourstracker.service.{ DefaultFacturationService, FacturationService }

trait FacturationComponent {
  this: LoggingComponent with SystemComponent =>

  def facturationService(implicit locale: Locale): FacturationService = new DefaultFacturationService()
}

