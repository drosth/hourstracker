package com.personal.hourstracker.config.component

import java.util.Locale

import com.personal.hourstracker.service.FacturationService
import com.personal.hourstracker.service.impl.DefaultFacturationService

trait FacturationComponent {
  this: LoggingComponent with SystemComponent =>

  def facturationService: FacturationService = new DefaultFacturationService()
}
