package com.personal.hourstracker.config.component

import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.domain.Registration.Registrations
import com.personal.hourstracker.service.DefaultFacturationService


trait FacturationComponent
    extends DefaultFacturationService


trait FacturationService {
  def facturationService: FacturationService


  trait FacturationService {
    val splitForFacturation: Registration => Registrations

    def splitForFacturation(registrations: Registrations): Registrations
  }


}
