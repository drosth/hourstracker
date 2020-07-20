package com.personal.hourstracker.config.component

trait FacturationComponent {
  this: LoggingComponent with SystemComponent =>

  def facturationService: FacturationService = new DefaultFacturationService()
}

