package com.personal.hourstracker

import com.personal.hourstracker.config.component.NoopRegistrationRepositoryComponent
import com.personal.hourstracker.config.module.{ApplicationModule, ImporterModule}

object Application
  extends App
    with ApplicationModule
    with ImporterModule
    with NoopRegistrationRepositoryComponent
