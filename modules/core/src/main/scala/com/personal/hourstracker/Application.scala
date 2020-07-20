package com.personal.hourstracker

object Application
  extends App
  with ApplicationModule
  with ImporterModule
  with NoopRegistrationRepositoryComponent
