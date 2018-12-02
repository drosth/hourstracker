package com.personal.hourstracker.config.component

import com.personal.hourstracker.config.Configuration
import com.personal.hourstracker.repository.impl.DBRegistrationRepository

trait DBRepositoryComponent extends SquerylComponentForMySQL {
  this: Configuration =>

  RegistrationRepositoryFactory.registrationRepository = new DBRegistrationRepository(databaseSession)
}
