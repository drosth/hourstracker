package com.personal.hourstracker.registration.service

import com.personal.hourstracker.domain.Registration
import com.personal.hourstracker.registration.repository.RegistrationRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import scala.collection.JavaConverters._
import com.personal.hourstracker.registration.repository._

@Service
class RegistrationService(@Autowired private val repository: RegistrationRepository) {

  def listRegistrations(): Iterable[Registration] = {
    repository.findAll.asScala
  }
}
